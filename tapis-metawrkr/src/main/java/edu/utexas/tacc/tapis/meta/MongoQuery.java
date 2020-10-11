package edu.utexas.tacc.tapis.meta;

import com.google.gson.JsonElement;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;


public class MongoQuery {
  private LRQSubmission.qType qType;
  private JsonElement jsonQueryArray;
  private String queryString;
  private String match;
  private String projection;
  private LRQSubmission lrqSubmission;
  public boolean isInitialized = false;
  
  
  public MongoQuery(LRQSubmission _lrqSubmission){
    // let's just check to see if LRQSubmission is valid to proceed.
    if(_lrqSubmission != null){
      lrqSubmission = _lrqSubmission;
      isInitialized = true;
    }
  }
  
  private void unpackQuery() throws TapisException {
    // 1. get the query type
    if(isInitialized){
      if(lrqSubmission.getQueryType().equals(LRQSubmission.qType.SIMPLE)){
        // set to SIMPLE
        qType = LRQSubmission.qType.SIMPLE;
        // jsonQueryArray =
      }
      if(lrqSubmission.getQueryType().equals(LRQSubmission.qType.AGGREGATION)){
        // set to Aggregation
        qType = LRQSubmission.qType.AGGREGATION;
      }
      
    }
  }
  
  private void upackSimple(){
  
  }
  /*------------------------------------------------------------------------
   *                              Getters and Setters
   * -----------------------------------------------------------------------*/
  
  
  
}
