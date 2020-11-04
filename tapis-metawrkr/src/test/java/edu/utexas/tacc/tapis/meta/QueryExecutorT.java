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
    System.out.println("begin printSubmissionInfo");
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
    System.out.println("end printSubmissionInfo\n");
  }
  
  public void runTest(String simpleQTask){
    System.out.println("begin runTest");
    // we can reuse this in MongoQuery
    LRQTask _lrqTask = ConversionUtils.stringToLRQTask(simpleQTask);
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    // just some logging to see what the values are
    this.printSubmissionInfo(_lrqTask);
  
    // this setsup the executor with an immutable lrqtask value
    System.out.println("logging the submitted lrq : \n"+_lrqTask.toJson());
    QueryExecutor executor = new QueryExecutor(_lrqTask.toJson(), runtime.getTenantId());
  
    executor.startQueryExecution();
    System.out.println("end runTest\n");
  }
  
  public static void main(String[] args) {
    System.out.println("*********  Test harness begin QueryExecutorT");
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
    // String simpleQTask = TestData.taskJson;
  
    t.runTest(TestData.getTaskJsonSimpleFields("123456"));
    System.out.println("\n============================================================================\n");
    t.runTest(TestData.getTaskJsonSimpleNoFields("574893"));
    System.out.println("\n============================================================================\n");
    t.runTest(TestData.getTaskJsonSimpleFields("789345"));
    System.out.println("\n============================================================================\n");
    System.out.println("Done running tests.");
    System.out.println("*********  Test harness End QueryExecutorT");
  }
  
}
