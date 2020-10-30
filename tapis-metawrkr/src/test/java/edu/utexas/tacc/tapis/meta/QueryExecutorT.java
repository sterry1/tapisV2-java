package edu.utexas.tacc.tapis.meta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

import java.util.List;


public class QueryExecutorT {
  public void printSubmissionInfo(LRQTask lrqTask){
    List<Object> q = lrqTask.getQuery();
    Gson gson = new Gson();
    String q1 = gson.toJson(q);
    System.out.println(q1);
    
    for (int i = 0; i < q.size(); i++) {
      System.out.println(gson.toJson(q.get(i)));
    }
    
    String simpleQSubmission = lrqTask.toJson();
    
    JsonObject jsonObject = JsonParser.parseString(simpleQSubmission.toString()).getAsJsonObject();
    JsonArray jsonArray = jsonObject.get("query").getAsJsonArray();
    System.out.println("query type: "+jsonObject.get("queryType").getAsString());
    System.out.println("number of elements: "+jsonArray.size());
    System.out.println("query: "+gson.toJson(jsonArray));
    
    String tmp = null;
    System.out.println();
    
    // executor.exportQueryResults("mycollection", tmp );
    
  }
  public static void main(String[] args) {
    RuntimeParameters parms = null;
    try {parms = RuntimeParameters.getInstance();}
    catch (Exception e) {
      // We don't depend on the logging subsystem.
      System.out.println("**** FAILURE TO INITIALIZE: tapis-jobsapi RuntimeParameters [ABORTING] ****");
      e.printStackTrace();
      throw e;
    }
    System.out.println("**** SUCCESS:  RuntimeParameters read ****");
    
    // this is basically the doWork function of the LRQWorker Task
    QueryExecutorT t = new QueryExecutorT();
    
    // this comes in from the queue, appropiate transformation here.
    String simpleQSubmission = "{\n" +
        "  \"_id\": \"3423849\",\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1,\"cdr2\": 1}],\n" +
        "  \"notification\": \"\"\n" +
        "}";
  
    // we can reuse this in MongoQuery
    LRQTask _lrqTask = ConversionUtils.stringToLRQTask(simpleQSubmission);
    // MongoQuery mongoQuery = new MongoQuery(_lrqTask.getQueryType(),_lrqTask.getJsonQueryArray());
  
    // just some logging to see what the values are
    t.printSubmissionInfo(_lrqTask);
    
    // this setsup the executor with an immutable lrqtask value
    System.out.println(_lrqTask.toJson());
    QueryExecutor executor = new QueryExecutor(_lrqTask.toJson());
    
    executor.startQueryExecution();
    
    
    // Pull json string query out and print
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
    // example: "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
/*
    Map<String,String> params = TestData.getSimpleCmdWithAuthWOFields();
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
  
    String mongoexportCmd = mec.exportCommandAsString();
  
    System.out.println("Export cmd line : \n"+ mec.exportCommandAsString());
    
    MongoExportExecutor mongoExportExecutor = new MongoExportExecutor(mec);
    mongoExportExecutor.runExportCommand();
*/
  
  }
  
}
