package edu.utexas.tacc.tapis.meta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;

import java.util.Map;


public class MongoQuery {
  private final String qType;
  // if qType is an Aggregation then this variable represents an Aggregation query.
  private final JsonArray jsonQueryArray;
  private JsonObject match;
  private JsonObject projection;
  private JsonArray aggregationPipeline;
  
  public boolean hasProjections = false;
  public boolean isInitialized = false;
  
  public MongoQuery(String queryType, JsonArray jsonArray){
    // let's just check to see if LRQTask is valid to proceed.
    this.qType = queryType;
    this.jsonQueryArray = jsonArray;
    if(queryType != null && jsonArray != null){
      isInitialized = true;
    }else{
      isInitialized = false;
    }
  }
  
  public void unpackQuery() throws TapisException {
    // 1. get the query type
    if(isInitialized){
      // TODO are we guaranteed that jsonQueryArray is valid json?
      if(qType.equals(LRQTask.qType.SIMPLE.toString())){
        upackSimple(jsonQueryArray);
      }
      if(qType.equals(LRQTask.qType.AGGREGATION.toString())){
        unpackAggregation(jsonQueryArray);
      }
      
    }
  }
  
  private void upackSimple(JsonArray jsonQueryArray){
    // unpack
    int size = jsonQueryArray.size();
    switch (size){
      case 2 : {
        // do query and projection
        match = (JsonObject)jsonQueryArray.get(0);      // this should be a valid mongodb query document
        projection = (JsonObject)jsonQueryArray.get(1); // this should be a valid mongodb projection document
        hasProjections = true;
        break;
      }
      case 1 : {
        // just do query
        JsonObject jo = (JsonObject)jsonQueryArray.get(0);
        match = (JsonObject)jsonQueryArray.get(0);
        break;
      }
      default:  System.out.println("nothing here");
    }
  
  }
  
  private void unpackAggregation(JsonArray jsonQueryArray){
    // unpack
    aggregationPipeline = jsonQueryArray;
  
  }
  
  
  /*------------------------------------------------------------------------
   *                              Getters and Setters
   * -----------------------------------------------------------------------*/
  
  /**
   *
   * @return a json string of an aggregation array.
   */
  public String getAggregationAsJsonString(){
    
    return null;
  }
  
  /**
   *
   * @return a json string of an simple matching query document
   */
  public String getSimpleQueryMatchAsJsonString(){
    return new Gson().toJson(match);
  }
  
  /**
   *
   * @return a simple string of fields to include in results document
s   */
  public String getSimpleQueryProjetionAsFields(){
    StringBuilder sb = new StringBuilder();
    for(Map.Entry<String, JsonElement> entry : projection.entrySet()) {
      sb.append(entry.getKey()+",");
      System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue() );
    }
    
    String fields = sb.toString().replaceAll(",$","");
    return fields;
  }
  
}
