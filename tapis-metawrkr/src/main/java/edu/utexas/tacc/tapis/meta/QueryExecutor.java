package edu.utexas.tacc.tapis.meta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import edu.utexas.tacc.tapis.meta.client.Notification;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.dao.AggregationQueryDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.json.JsonResponseBuilder;
import edu.utexas.tacc.tapis.meta.model.LRQStatus;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission.qType;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
  private final String tenant;
  
  // private static final RuntimeParameters _instance = RuntimeParameters.getInstance();
  
  
  /*------------------------------------------------------------------------
   *                       Constructors
   * -----------------------------------------------------------------------*/
  
  public QueryExecutor(String _lrqTaskString, String _tenant){
    // TODO check to make sure this returns a !null
    lrqTaskString = _lrqTaskString;
    this.tenant = _tenant;
  }
  
  private void updateStatus(LRQTask lrqTask, LRQStatus lrqStatus){
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    // TODO we need the tenant context here because we use it for queue identification
    // and we use it for DAO storage location; this is not sustainable
    LRQSubmissionDAO lrqSubDAO = new LRQSubmissionDAOImpl(tenant);
    lrqSubDAO.updateSubmissionStatus(lrqTask.get_id(), lrqStatus.status);
  }
  
  public void checkIntegrationWithQueue(String workerName){
    System.out.println(workerName+": I'm sleeping here ..");
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  
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
    _log.debug("LRQ Task converted to json "+lrqTask.toJson());
    MongoQuery mongoQuery = null;
    updateStatus(lrqTask, LRQStatus.RUNNING );
    String taskId = lrqTask.get_id();
    // It is a simple query so we can query and export at the same time.
    // we have all the info needed to create an export command
    // and call the query-export process.
    Map<String,String> cmdMap = this.createCommandMap();
    cmdMap.put("db",lrqTask.getQueryDb());
    // file output depends on whether we want compressed output or plain
    if(lrqTask.getCompressedOutput()){
      cmdMap.put("fileOutput"," | gzip > lrqdata/lrq-"+taskId+".gz");
    }else {  // our default is plain
      cmdMap.put("fileOutput","--out=lrqdata/lrq-"+taskId+".json");
    }
  
    // This is a simple query
    if(lrqTask.getQueryType().equals(qType.SIMPLE.toString())){
      _log.trace("This is a simple query");
      mongoQuery = new MongoQuery(qType.SIMPLE.toString(),lrqTask.getJsonQueryArray());
      cmdMap.put("collection",lrqTask.getQueryCollection());
      try {
        mongoQuery.upackSimple(cmdMap,lrqTask.getJsonQueryArray());
      } catch (Exception e) {
        // TODO logging and exception handling here.
        _log.debug("Process the SIMPLE query from the task. If this fails we can't go any further.");
        // in general should any part of this process fail, we should error out and fail the submission
        // we need to update the DAO to FAILED
        // pass the failure along
        e.printStackTrace();
      }
      // Our parameters for the command should be complete and we are able to derive the command used for export.
      MongoExportExecutor mongoExportExecutor = new MongoExportExecutor();
      // run the command
      MongoExportCommand mongoExportCommand = new MongoExportCommand(cmdMap);
      _log.debug("Export cmd : "+mongoExportCommand.exportCommandAsString());
      mongoExportExecutor.runExportCommand(new MongoExportCommand(cmdMap));
      
      // but we need a Map of query commands
      // create a new QueryHostContext ie. the mongodb host context info host:port user password security info ect.
      // for production we only need host:port but no security info.
    }else{
      if(lrqTask.getQueryType().equals(qType.AGGREGATION.toString())) {
        _log.debug("Process the AGGREGATION query from the task. If this fails we can't go any further.");
        JsonArray queryArray = lrqTask.getJsonQueryArray();
        mongoQuery = new MongoQuery(qType.AGGREGATION.toString(), queryArray );
        // the Aggregation takes the initial collection for the query but later we need to export the tmp collection
        cmdMap.put("collection",lrqTask.getQueryCollection());

        // TODO run an aggregation against the database with the $out document setting to create a tmp collection.
        // assume the aggregation pipeline array does not have an $out operator.
        // create and add our $out for the lrq collection name.
        
        mongoQuery.injectOutDefinition(lrqTask.get_id());
        // running the aggregation
        AggregationQueryDAO queryDAO = new AggregationQueryDAO(lrqTask.getQueryDb(),lrqTask.getQueryCollection());
        if(mongoQuery.getAggregationQueryArray() != null){
          queryDAO.runAggregation(mongoQuery.getAggregationQueryArray());
        }
  
        // Our parameters for the command should be complete and we are able to derive the command used for export.
        MongoExportExecutor mongoExportExecutor = new MongoExportExecutor();
        // For export we want to use the tmp collection named after the LRQ id.
        cmdMap.put("collection",lrqTask.get_id());
        // run the command
        MongoExportCommand mongoExportCommand = new MongoExportCommand(cmdMap);
        _log.debug("Export cmd : "+mongoExportCommand.exportCommandAsString());
        int exitCode = mongoExportExecutor.runExportCommand(new MongoExportCommand(cmdMap));
        _log.debug("Exit code from mongoexport : "+exitCode);
        queryDAO.removeCollection(lrqTask.get_id());

      }
    }
    
    // TODO where do we need to update the task to FAILED
    // Update the status of LRQ task.
    this.updateStatus(lrqTask,LRQStatus.FINISHED);
    
    // send notification
    Notification notification = new Notification(lrqTask.getNotification());
    if(!notification.isValidNotificationUrl()){
      _log.debug("Notification for LRQ task : "+lrqTask.get_id()+" could not be sent.");
      return false;
    }else{
      // Build response
      //TODO default storage location
      String defaultLocation = RuntimeParameters.getInstance().getTenantDefaultStorageLocation();
      String location;
      if(lrqTask.getCompressedOutput()){
        location = defaultLocation+"/lrq-"+lrqTask.get_id()+".gz";
      }else{
        location = defaultLocation+"/lrq-"+lrqTask.get_id()+".json";
      }
      
      JsonResponseBuilder builder = new JsonResponseBuilder(lrqTask.get_id(),location,LRQStatus.FINISHED.status,"LRQ results complete");
      notification.sendNotification(builder.getBasicResponse());
    }
    return false;
  }
  
/*
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
*/
  
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
    System.out.println();

    return params;
  }
  
  private void exportQueryResults(String collectionName, String query){
    /*------------------------------------------------------------------------
     * Instantiate a Process exec to start mongoexport process to monitor
     * -----------------------------------------------------------------------*/

  }
  
}
