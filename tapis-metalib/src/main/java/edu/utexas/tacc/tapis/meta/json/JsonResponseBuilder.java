package edu.utexas.tacc.tapis.meta.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.shared.utils.TapisUtils;
import edu.utexas.tacc.tapis.sharedapi.responses.RespBasic;

public class JsonResponseBuilder {
  private final String location;
  private final String status;
  private final String coreMsg;
  private final String id;
  
  public JsonResponseBuilder(String _id, String _location, String _status, String _coreMsg ){
    id = _id;
    location = _location;
    status = _status;
    coreMsg = _coreMsg;
  }
  /*------------------------------------------------------------------------
   * getBasicResponse(String location)
   * -----------------------------------------------------------------------*/
  public String getBasicResponse(){
    // Create a basic response to fill in for core server empty response
    RespBasic resp = new RespBasic();
    resp.status = String.valueOf(status);
    resp.message = coreMsg;
    // TODO fix maven generation of files resp.version = TapisUtils.getTapisVersion();
    resp.version = "0.0.4";
    
    //  get the location of the resource
    String id = this.id;
    StringBuilder sb = new StringBuilder();
    
    // append the _id of the newly created resource, namely document
    // to the response json
    sb.append("{\"location\":\"").append(location).append("\"}");
    JsonObject jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
    resp.result = jsonObject;
    return TapisGsonUtils.getGson().toJson(resp);
  }
  
  public String getLocation() { return location; }
  
  public String getStatus() { return status; }
  
  public String getCoreMsg() { return coreMsg; }
  
  public String getId() { return id; }
}
