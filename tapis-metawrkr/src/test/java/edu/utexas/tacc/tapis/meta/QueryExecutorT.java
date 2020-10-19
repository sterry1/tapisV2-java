package edu.utexas.tacc.tapis.meta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutorT {
  
  public static void main(String[] args) {
  
  
    String simpleQSubmission = "{\n" +
        "  \"_id\": \"3423849\",\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1}],\n" +
        "  \"notification\": \"\"\n" +
        "}";
  
    // we can reuse this in MongoQuery
    LRQTask _lrqTask = ConversionUtils.jsonObjectToLRQTask(ConversionUtils.stringToJsonObject(simpleQSubmission));
  
    QueryExecutor executor = new QueryExecutor(_lrqTask);
  
    List<Object> q = _lrqTask.getQuery();
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
  
    String tmp = null;
    System.out.println();
  
    // executor.exportQueryResults("mycollection", tmp );
  
    // example: "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
    Map<String,String> params = new HashMap<>();
    params.put("hostPort","aloe-dev08.tacc.utexas.edu:27019");
    params.put("user","tapisadmin");
    params.put("password","d3f@ult");
    params.put("db","v1airr");
    params.put("collection","rearrangement");
    params.put("fileOutput","onejson.json.gz");
    params.put("fields","repertoire_id,locus");
    // params.put("query","{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}");
    params.put("query","{\"repertoire_id\":\"1841923116114776551-242ac11c-0001-012\"}");
  
    // params.put("fields","");
    // params.put("query","");
  
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
  
    String mongoexportCmd = mec.exportCommandAsString();
  
    System.out.println("Export cmd line : \n"+ mec.exportCommandAsString());
    
    MongoExportExecutor mongoExportExecutor = new MongoExportExecutor(mec);
    mongoExportExecutor.runExportCommand();
    
    
    
  }
  
}
