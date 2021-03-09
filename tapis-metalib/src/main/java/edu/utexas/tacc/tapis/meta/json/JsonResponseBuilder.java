package edu.utexas.tacc.tapis.meta.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.utexas.tacc.tapis.shared.utils.TapisUtils;

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
    LRQResponse lrqResponse = new LRQResponse();
    
    lrqResponse.setStatus(status);
    lrqResponse.setMessage(coreMsg);
    // TODO fix maven generation of files resp.version = TapisUtils.getTapisVersion(); or pull from env
    lrqResponse.setVersion( "0.0.6");
    
    //  get the location of the resource
    LrqResponseResult result = new LrqResponseResult();
    result.set_id(getId());
    result.setLocation(getLocation());
    lrqResponse.setResult(result);
    
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
  
    Gson gson = builder.create();
    String json = gson.toJson(lrqResponse, LRQResponse.class);
    
    return json;
  }
  
  public String getLocation() { return location; }
  
  public String getStatus() { return status; }
  
  public String getCoreMsg() { return coreMsg; }
  
  public String getId() { return id; }
}
