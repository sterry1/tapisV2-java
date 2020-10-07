package edu.utexas.tacc.tapis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReduceDoubleQuotes {
  public static void main(String[] args) {
    String json = "{\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1}],\n" +
        "  \"notification\": \"mailto:sterry1@tacc.utexas.edu\"\n" +
        "}";
    JsonObject jsonObject;
    System.out.println("getAsString");
    jsonObject = JsonParser.parseString(json).getAsJsonObject();
    System.out.println(jsonObject.get("name").getAsString());
    System.out.println(jsonObject.get("queryType").getAsString());
    // System.out.println(jsonObject.get("query").getAsString());
    System.out.println(jsonObject.get("notification").getAsString());
  
    System.out.println("\ntoString");
    System.out.println(jsonObject.get("name").toString());
    System.out.println(jsonObject.get("queryType").toString());
    System.out.println(jsonObject.get("query").toString());
    System.out.println(jsonObject.get("notification").toString());

  }
}
