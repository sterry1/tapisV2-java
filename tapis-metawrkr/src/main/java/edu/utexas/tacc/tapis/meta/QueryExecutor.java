package edu.utexas.tacc.tapis.meta;

import com.google.gson.*;
import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission.qType;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** ------------------------------------------------------------------------
 * responsibilities of executor
 * In general, this class or Actor will orchestrate the execution of
 *   a simple or aggregation query against the supplied database and
 *   collection.
 *  * Requires data about the target database and collection
 *  * The actual query itself
 *  * The naming and storage of result file.
 *  * Updates to the lrq persisted entity on status
 *  *
 *
 * 1. Assume query task has been delegated to this executor
 *
 * 2. If a simple query then use the mongoexport tool to write results to
 *    storage location
 * 3. If an aggregation query then submit aggregation with additional
 *      command to write results to temporary collection.
 *    Then use mongoexport to write results to storage location.
 *    Delete the temporary collection.
 * 4. Notify LRQWrkr of completion.
 * -----------------------------------------------------------------------*/
public class QueryExecutor {
  
  private static final Logger _log = LoggerFactory.getLogger(QueryExecutor.class);
  private final String lrqTaskString;
  // private static final RuntimeParameters _instance = RuntimeParameters.getInstance();
  
  
  /*------------------------------------------------------------------------
   *                       Constructors
   * -----------------------------------------------------------------------*/
  
  public QueryExecutor(String _lrqTaskString){
    // TODO check to make sure this returns a !null
    lrqTaskString = _lrqTaskString;
  }
  /*
  *   1. Extract the query array from the Task
  *   2. Call MongoQuery need qtype, query JsonArray
  *   3. Based on mongoQuery choose path of query execution
  */
  public boolean startQueryExecution(){
    // we have a lrq task
    // TODO we could throw an exception here for multiple reasons
    // we are reasonably assured the task was delivered uncorrupted from the task queue
    // i guess it's safe to assume it is a valid lrq task and conforms to syntactically correct json
    LRQTask lrqTask = ConversionUtils.stringToLRQTask(this.lrqTaskString);
    MongoQuery mongoQuery = null;
    
    if(lrqTask.getQueryType().equals(qType.SIMPLE.toString())){
      mongoQuery = new MongoQuery(qType.SIMPLE.toString(),lrqTask.getJsonQueryArray());
      try {
        mongoQuery.unpackQuery();
      } catch (TapisException e) {
        // TODO logging and exception handling here.
        // in general should any part of this process fail, we should error out and fail the submission
        e.printStackTrace();
      }
      
      // It is a simple query so we can query and export at the same time.
      // we have all the info needed to create an export command
      // and call the query-export process.
      Map<String,String> cmdMap = this.createCommandMap();
      System.out.println();
      
      // MongoExportCommand mongoExportCommand = new MongoExportCommand(Map params);
      // but we need a Map of query commands
      // create a new QueryHostContext ie. the mongodb host context info host:port user password security info ect.
      // for production we only need host:port but no security info.
      
    }else{
      if(lrqTask.getQueryType().equals(qType.AGGREGATION.toString())) {
        mongoQuery = new MongoQuery(qType.AGGREGATION.toString(), lrqTask.getJsonQueryArray());
        // prep the aggregation
        
        
      }
    }
    // major ERROR condition
    if(mongoQuery != null){
      return false;
    }
  
    return false;
  }
  
  // create a cmd map to drive the export command.
  private void simpleQueryExecution(){
    // create a map to drive the ExportCommand
    Map<String,String> cmdMap = new HashMap<>();
    addQueryHostContext(cmdMap);
    
    
    // this will create the commandline call to the Executor
    // Simple queries are a one step export and write to zip file to the
    // designated storage location. These are usually the largest result sets
    // generated.
    
  }
  
  private void aggregationExecution(){
  // TODO this is the aggregation run
  }
  
  private void addQueryHostContext(Map<String,String>  cmdMap){
    QueryHostContext context = new QueryHostContext();
    // add the query host context, is some cases, like prod, the map is small
    // because there is no auth information needed.
    cmdMap.putAll(context.getContext());
  }
  
  private Map<String,String> createCommandMap(){
    
    Map<String,String> params = new HashMap<>();
    params.put("host","");
    params.put("port","");
    params.put("user","");
    params.put("password","");
    params.put("authDB","");
    params.put("db","");
    params.put("collection","");
    params.put("fileOutput","");
    params.put("fields","");
    params.put("query","");
    // TODO context should be ok from Runtime parameters we can always check
    this.addQueryHostContext(params);

    return params;
  }
  
  private void exportQueryResults(String collectionName, String query){
    /*------------------------------------------------------------------------
     * Instantiate a Process exec to start mongoexport process to monitor
     * -----------------------------------------------------------------------*/

  }
  
}
