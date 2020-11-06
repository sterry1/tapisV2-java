package edu.utexas.tacc.tapis.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class IterateOverElementT {
  public static void main(String[] args) {
    String json = TestData.taskJsonSimpleFields;
    JsonElement jsonElement = JsonParser.parseString(json);
    if(jsonElement.isJsonObject()){
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      System.out.println(jsonObject.size());
      for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue() );
      }
    }
/*
    final JsonObject jsonObject = GSON.toJsonTree(<Object>).getAsJsonObject();
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue());
    }
*/
  }
}
