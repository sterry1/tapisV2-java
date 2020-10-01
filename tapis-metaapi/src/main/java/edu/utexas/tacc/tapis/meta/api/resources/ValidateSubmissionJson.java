package edu.utexas.tacc.tapis.meta.api.resources;

import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the request submission body. checks for valid json
 * creates a DTO
 */
public class ValidateSubmissionJson {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(ValidateSubmissionJson.class);
  
  private boolean hasError=false;
  private String errorMsg="";
  private String jsonSubmission;
  
  /**
   * We have checked for correct json so jsonObjectPayload should never be null
   * @param jsonObjectPayload
   */
  public ValidateSubmissionJson(JsonObject jsonObjectPayload) {
    // AbstractResource checks for empty payload and valid json
    // We need to either check schema or allow MongoDb to check schema from DAO
    
  }
  
  public boolean hasError() {
    return hasError;
  }
  
  public LRQSubmission getLRQ() { return null; }
  
}