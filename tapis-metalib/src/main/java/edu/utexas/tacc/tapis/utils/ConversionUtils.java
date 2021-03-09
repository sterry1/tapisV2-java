package edu.utexas.tacc.tapis.utils;

import com.google.gson.*;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class helps with the transformation of our LRQ model across different
 * layers within the application.
 * 1. We receive the json string in the initial request from an InputStream
 * 2. We need to turn it into json then into our LRQSubmission object.
 * 3. LRQSubmission becomes the linqua franca across the remaining two layers TaskQueue and Persistance.
 *
 * We use this class with static methods to help with the transition from lower level data structure to
 * LRQSubmission and back.
 */
public class ConversionUtils {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(ConversionUtils.class);
  private static final Gson gson = new Gson();
  /**
   * We need to convert InputStream to a String
   * @param payload
   * @return String or null if there is an error
   */
  public static String inputStreamToString(InputStream payload){
    StringBuilder stringPayload = new StringBuilder();
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line;
      while ((line = in.readLine()) != null) {
        stringPayload.append(line);
      }
      String string = stringPayload.toString();
      return string;
    }catch (IOException e) {
      String msg = "getValidJson Error Reading the payload from request";
      // TODO throw new Tapis Exception
      _log.error(msg);
    }
    return null;
  }
  
  /**
   *
   * @param payload
   * @return JsonObject or null if there is an error
   */
  public static JsonObject inputStreamToJsonObject(InputStream payload){
    
    JsonObject jsonObject;
    String string = ConversionUtils.inputStreamToString(payload);
  
    if (!StringUtils.isEmpty(string)){
      try {
        jsonObject = JsonParser.parseString(string).getAsJsonObject();
      } catch (JsonSyntaxException e ) {
        // TODO throw new Tapis Exception
        _log.error("Not valid Json, "+e.getMessage());
        return null;
      }
      // must be good json so send it back
      return jsonObject;
    }else {
      return null;
    }
  }
  
  public static LRQTask stringToLRQTask(String taskString){
    return jsonObjectToLRQTask(stringToJsonObject(taskString));
  }
  
  public static JsonObject stringToJsonObject(String string){
    JsonObject jsonObject;
    // checked for null and empty
    if (!StringUtils.isEmpty(string)){
      try {
        jsonObject = JsonParser.parseString(string).getAsJsonObject();
      } catch (JsonSyntaxException e ) {
        // TODO throw new Tapis Exception
        _log.error("Not valid Json, "+e.getMessage());
        return null;
      }
      // must be good json so send it back
      return jsonObject;
    }else {
      return null;
    }
  }
  
  /**
   * Conversion to JsonObject from a byte[], useful for exchanges between
   * TaskQueue and API or worker classes
   * @param data
   * @return a JsonObject unless there's an error then return null
   */
  public static JsonObject byteArrayToJsonObject(byte[] data){
    String string = new String(data);
    JsonObject jsonObject = null;
    if (!StringUtils.isEmpty(string)) {
      // this relies on previous conversion
      jsonObject = stringToJsonObject(string);
      return jsonObject;
    }
    return jsonObject;
  }
  
  public static String jsonArrayToString(JsonArray jsonArray){
    String queryArray = gson.toJson(jsonArray);
    return queryArray;
  }
  
  public static JsonArray stringToJsonArray(String _jsonArray){
    JsonArray jsonArray = gson.fromJson(_jsonArray,JsonArray.class);
    return jsonArray;
  }
  
  public static JsonArray arrayListOfStringToJsonArray(List<Object> strArray) {
    JsonArray jsonArray = gson.toJsonTree(strArray).getAsJsonArray();
    return jsonArray;
  }
  
  public static LRQSubmission jsonObjectToLRQSubmission(JsonObject jsonObject){
    // what if we get a null or empty object
    LRQSubmission lrqSubmission = null;
    if(jsonObject != null){
      try {
        lrqSubmission = gson.fromJson(jsonObject, LRQSubmission.class );
      } catch (JsonSyntaxException e ) {
        // TODO throw new Tapis Exception?
        _log.error("Not valid Json, "+e.getMessage());
      }
    }
    return  lrqSubmission;
  }
  
  public static LRQTask jsonObjectToLRQTask(JsonObject jsonObject){
    // what if we get a null or empty object
    LRQTask lrqTask = null;
    if(jsonObject != null){
      try {
        lrqTask = gson.fromJson(jsonObject, LRQTask.class );
      } catch (JsonSyntaxException e ) {
        // TODO throw new Tapis Exception?
        _log.error("Not valid Json, "+e.getMessage());
      }
    }
    return  lrqTask;
  }
  
}
