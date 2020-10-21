package edu.utexas.tacc.tapis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

public class ConversionUtilsT {
  public static void main(String[] args) {
    
    String jobJsonSimpleWithId = "{\n" +
        "  \"_id\": \"57485869987\",\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1}],\n" +
        "  \"notification\": \"mailto:sterry1@tacc.utexas.edu\"\n" +
        "}";
    // put <priority>, <delay>, <time to reserve>, <bytes> <data>  number of bytes and the byte[] for data.
    // producer.putJob(1,0,5000, jobJson.getBytes());
  
    String jobJsonAggr = "{\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"AGGREGATION\",\n" +
        "  \"query\": [\n" +
        "    {\n" +
        "      \"$match\": {\n" +
        "        \"repertoire_id\": {\n" +
        "          \"$in\": [\n" +
        "            \"978739827430911510-242ac11a-0001-012\",\n" +
        "            \"967272264750591510-242ac11a-0001-012\"\n" +
        "          ]\n" +
        "        }\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"$group\": {\n" +
        "        \"_id\": \"$repertoire_id\",\n" +
        "        \"count\": {\n" +
        "          \"$sum\": \"1\"\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  ],\n" +
        "  \"notification\": \"\"\n" +
        "}\n";
  
    Gson gson = new Gson();
    // LRQTask gen = gson.fromJson(jobJsonSimpleWithId, LRQTask.class );
    JsonObject jsonObject = gson.fromJson(jobJsonSimpleWithId, JsonObject.class);
    LRQTask gen = ConversionUtils.jsonObjectToLRQTask(jsonObject);
    System.out.println(gen.toJson());
  }
}
