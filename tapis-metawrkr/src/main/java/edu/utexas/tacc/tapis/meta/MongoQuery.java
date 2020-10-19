package edu.utexas.tacc.tapis.meta;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import scala.$eq$colon$eq;


public class MongoQuery {
  private LRQTask.qType qType;
  // if qType is an Aggregation then this variable represents an Aggregation query.
  private JsonArray jsonQueryArray;
  private JsonObject match;
  private JsonObject projection;
  private JsonArray aggregationPipeline;
  public boolean hasProjections = false;
  
  public boolean isInitialized = false;
  
  public MongoQuery(LRQTask.qType _qType, JsonArray jsonArray){
    // let's just check to see if LRQTask is valid to proceed.
    if(_qType != null && jsonArray != null){
      this.qType = _qType;
      jsonQueryArray = jsonArray;
      isInitialized = true;
    }else{
      isInitialized = false;
    }
  }
  
  private void unpackQuery() throws TapisException {
    // 1. get the query type
    if(isInitialized){
      // TODO are we guaranteed that jsonQueryArray is valid json?
      if(qType.equals(LRQTask.qType.SIMPLE)){
        upackSimple(jsonQueryArray);
      }
      if(qType.equals(LRQTask.qType.AGGREGATION)){
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
    return null;
  }
  
  /**
   *
   * @return a simple string of fields to include in results document
s   */
  public String getSimpleQueryProjetionAsFields(){
    return null;
  }
  
  
  
}
