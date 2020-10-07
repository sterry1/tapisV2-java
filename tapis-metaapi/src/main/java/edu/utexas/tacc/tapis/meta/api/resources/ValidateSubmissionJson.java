package edu.utexas.tacc.tapis.meta.api.resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the request submission body.
 * checks for valid json
 * creates a DTO
 */
public class ValidateSubmissionJson {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(ValidateSubmissionJson.class);
  
  private boolean isValid = false;
  private String errorMsg="";
  private JsonObject jsonSubmission;
  private String stringSubmission;
  
  
  /**
   * We have checked for correct json so jsonObjectPayload should never be null
   * @param jsonObjectPayload
   */
  public ValidateSubmissionJson(JsonObject jsonObjectPayload) {
    // AbstractResource checks for empty payload and valid json
    // We need to either check schema or allow MongoDb to check schema from DAO
    jsonSubmission = jsonObjectPayload;
    schemaCheck();
  }
  
  public boolean isValid() {
    return isValid;
  }
  
  private void schemaCheck(){
    // TODO validity check against schema.
    // we hard code validitiy for now while we are testing
    this.isValid = true;
  }
  
  public LRQSubmission getLRQSubmission() {
    // TODO bullet proof this transform
    String name = jsonSubmission.get("name").getAsString();
    String queryType = jsonSubmission.get("queryType").getAsString();
    String query = jsonSubmission.get("query").toString(); 
    String notification = jsonSubmission.get("notification").getAsString();
    LRQSubmission lrqSubmission = new LRQSubmission(name, queryType, query, notification);
    return  lrqSubmission;
  }
  
  public String getLRQasString(){
    stringSubmission = jsonSubmission.getAsString();
    return stringSubmission;
  }
  
  public JsonObject getLRQasJsonObject(){
    return jsonSubmission;
  }
  
  
}