package edu.utexas.tacc.tapis.meta;

import com.google.gson.*;
import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * We can have
 * 1. Simple queries with projections
 * 2. Aggregations
 *
 */
public class QueryExecutor {
  
  private static final Logger _log = LoggerFactory.getLogger(QueryExecutor.class);
  
  private void exportQueryResults(String collectionName, String query){
    /*------------------------------------------------------------------------
     * Instantiate a Process exec to start mongoexport process to monitor
     * -----------------------------------------------------------------------*/
    Process process = null;
    try {
      process = Runtime.getRuntime().exec(query);
    } catch (IOException e) {
      _log.error("{ 'mgs': 'ERROR failed to execute the process command' }");
      e.printStackTrace();
    }
    
    BufferedReader output = null;
    try {
      String strCurrentLine;
      output = new BufferedReader(new InputStreamReader(process.getInputStream()));
      while ((strCurrentLine = output.readLine()) != null) {
        System.out.println(strCurrentLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (output != null)
          output.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public static void main(String[] args) {
    /*------------------------------------------------------------------------
     * responsibilities of executor
     * 1. Assume query task has been delegated to this executor
     * 2. If a simple query then use the mongoexport tool to write results to
     *    storage location
     * 3. If an aggregation query then submit aggregation with additional
     *      command to write results to temporary collection.
     *    Then use mongoexport to write results to storage location.
     *    Delete the temporary collection.
     * 4. Notify Wrkr of completion.
     * -----------------------------------------------------------------------*/
    QueryExecutor executor = new QueryExecutor();

    String simpleQSubmission = "{\n" +
        "  \"_id\": \"3423849\",\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1}],\n" +
        "  \"notification\": \"\"\n" +
        "}";
    
    // we can reuse this in MongoQuery
    LRQSubmission lrqSubmission = ConversionUtils.jsonObjectToLRQSubmission(ConversionUtils.stringToJsonObject(simpleQSubmission));
    List<Object> q = lrqSubmission.getQuery();
    Gson gson = new Gson();
    String q1 = gson.toJson(q);
  
    for (int i = 0; i < q.size(); i++) {
      System.out.println(gson.toJson(q.get(i)));
    }
    
    JsonObject jsonObject = JsonParser.parseString(simpleQSubmission.toString()).getAsJsonObject();
    JsonArray jsonArray = jsonObject.get("query").getAsJsonArray();
    System.out.println("query type: "+jsonObject.get("queryType").getAsString());
    System.out.println("number of elements: "+jsonArray.size());
    System.out.println("query: "+gson.toJson(jsonArray));
    String match;
    String projection;
    int size = jsonArray.size();
  
    String template = "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json {{fields}} -q='{{query}}'";
    String tmp = null;
    switch (size){
      case 2 : {
        // do q and projection
        JsonObject jo = (JsonObject)jsonArray.get(0);
        match = jo.toString();
        jo = (JsonObject)jsonArray.get(1);
        projection = jo.toString();
        tmp = template.replace("{{query}}",match).replace("{{fields}}","-f=\"repertoire_id,locus\"");
        break;
      }
      case 1 : {
        // just do q
        JsonObject jo = (JsonObject)jsonArray.get(0);
        match = jo.getAsString();
        break;
      }
      default:  System.out.println("nothing here");
    }
    System.out.println();
    
    executor.exportQueryResults("mycollection", tmp );
  
  }
}
