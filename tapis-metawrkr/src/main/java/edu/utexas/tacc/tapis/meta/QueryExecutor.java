package edu.utexas.tacc.tapis.meta;

import com.google.gson.*;
import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission.qType;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
  private final LRQTask lrqTask;
  
  
  /*------------------------------------------------------------------------
   *                       Constructors
   * -----------------------------------------------------------------------*/
  public QueryExecutor(LRQTask _lrqTask){
    lrqTask = _lrqTask;
  }
  
  /*
  *   1. Extract the query array from the Task
  *   2. Call MongoQuery need qtype, query JsonArray
  *   3. Based on mongoQuery choose path of query execution
  */
  
  public boolean startQueryExecution(){
    // we have a lrq task
    MongoQuery mongoQuery = null;
    
/*
    switch(lrqTask.getQueryType()) {
      case qType.SIMPLE.type.equals("SIMPLE") :
        // Statements
        break; // optional
    
      case qType.SIMPLE.type.equals("AGGREGATION") :
        // Statements
        break; // optional
    
      // You can have any number of case statements.
      default : // Optional
        // Statements
    }
*/
    if(lrqTask.getQueryType().equals(qType.SIMPLE.toString())){
      mongoQuery = new MongoQuery(qType.SIMPLE,lrqTask.getJsonQueryArray());
      // It is a simple query so we can query and export at the same time.
      // MongoExportCommand mongoExportCommand = new MongoExportCommand(Map params);
      // but we need a Map of query commands
      // create a new QueryHostContext ie. the mongodb host context info host:port user password security info ect.
      // for production we only need host:port but no security info.
      
    }else{
      if(lrqTask.getQueryType().equals(qType.AGGREGATION.toString())) {
        mongoQuery = new MongoQuery(qType.AGGREGATION, lrqTask.getJsonQueryArray());
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
    // this will create the commandline call to the Executor
    // Simple queries are a one step export and write to zip file to the
    // designated storage location. These are usually the largest result sets
    // generated.
    
  }
  
  private void aggregationExecution(){
  
  }
  
  
  
  private void exportQueryResults(String collectionName, String query){
    /*------------------------------------------------------------------------
     * Instantiate a Process exec to start mongoexport process to monitor
     * -----------------------------------------------------------------------*/

  }
  
}
