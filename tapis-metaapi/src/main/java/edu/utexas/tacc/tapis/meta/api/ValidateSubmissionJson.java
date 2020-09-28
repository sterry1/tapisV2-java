package edu.utexas.tacc.tapis.meta.api;

import java.io.InputStream;

/**
 * Handles the request submission body. checks for valid json
 * creates a DTO
 */
public class ValidateSubmissionJson {
  
  public ValidateSubmissionJson(InputStream payload) {
    // check if payload is null or empty
    
    // check if payload is valid json
    
    // fill a DTO
    
    
  }
  
  public boolean hasError() {
    return false;
  }
  
  public SubmissionDTO getDTO() {
    return null;
  }
}
