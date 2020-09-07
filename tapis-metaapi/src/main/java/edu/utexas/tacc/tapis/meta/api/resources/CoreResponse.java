package edu.utexas.tacc.tapis.meta.api.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.shared.utils.TapisUtils;
import edu.utexas.tacc.tapis.sharedapi.dto.ResponseWrapper;
import edu.utexas.tacc.tapis.sharedapi.responses.RespBasic;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.ws.rs.core.EntityTag;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CoreResponse {
  
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(CoreResponse.class);

  // private boolean isResponseValid;
  private Map<String, List<String>> headers;
  private String method;
  private String coreResponsebody;
  private String coreMsg;
  private int statusCode;
  private String etag;
  private String location;
  private boolean basicResponse;
  
  /**
   * map the response from the core server request to our jaxrs response framework.
   * @param coreResponse
   */
  public void mapResponse(okhttp3.Response coreResponse) {
    // http method
    captureResponseMethod(coreResponse);
    
    // collect all the response headers
    captureCoreResponseHeaders(coreResponse);
    
    // collect the message returned from core
    captureResponseMsg(coreResponse);
    
    // collect the response body for return back to requestor
    captureResponseBody(coreResponse);
    
    // what was our status code returned
    statusCode = coreResponse.code();
    
  }
  
  /*------------------------------------------------------------------------
   *                              Private Methods
   * -----------------------------------------------------------------------*/
  /*------------------------------------------------------------------------
   *                Capture methods for core server Response
   * -----------------------------------------------------------------------*/
  /*------------------------------------------------------------------------
   * captureCoreResponseHeaders
   * -----------------------------------------------------------------------*/
  private void captureCoreResponseHeaders(okhttp3.Response coreResponse) {
    _log.debug("Capture Headers from core response ...");
    headers = coreResponse.headers().toMultimap();
    this.etag = coreResponse.header("ETag");
    this.location = coreResponse.header("Location");
  
    if (_log.isDebugEnabled()) {
      _log.debug(logResponseHeaders());
    }
  }
  
  /*------------------------------------------------------------------------
   * captureResponseBody
   * -----------------------------------------------------------------------*/
  private void captureResponseBody(okhttp3.Response coreResponse) {
    _log.debug("response body output ");
    ResponseBody responseBody = coreResponse.body();
    try {
      coreResponsebody = responseBody.string();
    } catch (IOException e) {
      _log.warn("response body exception thrown");
      e.printStackTrace();
    }
  }
  
  private void captureResponseMethod(okhttp3.Response coreResponse) {
    method = coreResponse.request().method();
  }
  
  private void captureResponseMsg(okhttp3.Response coreResponse){
    coreMsg = coreResponse.message();
  }
  
  private void captureStatusCode(okhttp3.Response coreResponse){
    statusCode = coreResponse.code();
  }
  
  private void logResponseBody() {
    _log.debug("response body output ");
    _log.debug("size of response body : " + coreResponsebody);
    if (coreResponsebody.length() > 0) {
      if (_log.isDebugEnabled()) {
        _log.debug("response : \n" + coreResponsebody.toString());
      }
    }
  }
  
  /*------------------------------------------------------------------------
   * logResponseHeaders
   * -----------------------------------------------------------------------*/
  private String logResponseHeaders() {
    StringBuilder sb = new StringBuilder();
    sb.append("Headers from core ...");
    Iterator<Map.Entry<String, List<String>>> iterator = headers.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, List<String>> entry = iterator.next();
      _log.debug(entry.getKey() + ":" + entry.getValue());
    }
    return sb.toString();
  }
  
  /*------------------------------------------------------------------------
   * getBasicResponse
   * -----------------------------------------------------------------------*/
  // the results from this call become the response body CoreResponse
  // which in turn is sent back to user
  protected String getBasicResponse(){
    RespBasic resp = new RespBasic();
    resp.status = String.valueOf(this.getStatusCode());
    resp.message = this.coreMsg;
    resp.version = TapisUtils.getTapisVersion();
    resp.result = "";
    return TapisGsonUtils.getGson().toJson(resp);
  }
  
  /*------------------------------------------------------------------------
   * getBasicResponse(String location)
   * -----------------------------------------------------------------------*/
  protected String getBasicResponse(String location){
    // Create a basic response to fill in for core server empty response
    RespBasic resp = new RespBasic();
    resp.status = String.valueOf(this.getStatusCode());
    resp.message = this.coreMsg;
    resp.version = TapisUtils.getTapisVersion();
    
    //  get the location of the resource
    String oid = getOidFromLocation(location);
    StringBuilder sb = new StringBuilder();
    
    // append the _id of the newly created resource, namely document
    // to the response json
    sb.append("{\"_id\":").append(oid).append("}");
    JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
    resp.result = jsonObject;
    return TapisGsonUtils.getGson().toJson(resp);
  }
  
  /*------------------------------------------------------------------------
   * getOidFromLocation
   * -----------------------------------------------------------------------*/
  private String getOidFromLocation(String location){
    // need to parse location which looks something like this from the core server response.
    // http://c002.rodeo.tacc.utexas.edu:30401/StreamsTACCDB/sltCollectionTst/5ea5bf3ca93eebf39fcc563b
    StringTokenizer st = new StringTokenizer(location,"/");
    
    // make the assumption this a a URL to a resource location with the ending value
    // the oid of the created document
    String oid = "";
    while (st.hasMoreElements()) {
      oid = st.nextToken();
      // we just need the last token which should be the oid
    }
    return oid;
  }
  
  private void printMethod() {
    _log.debug("http method used : " + this.method);
  }
  
  private void printResponseMsg() {
    _log.debug("http msg returned : " + coreMsg);
  }
  
  /*------------------------------------------------------------------------
   *                              Public Methods
   * -----------------------------------------------------------------------*/
  public String getEtagValueFromHeaders(){
    String etagValue = null;
    if(headers.containsKey("ETag")){
      List<String> etagList = headers.get("ETag");
      etagValue = etagList.get(0);
    }
    return etagValue;
  }
  
  /*------------------------------------------------------------------------
   * getLocationFromHeaders
   * -----------------------------------------------------------------------*/
  public String getLocationFromHeaders(){
    String locationValue = null;
    if(headers.containsKey("Location")){
      List<String> locationList = headers.get("Location");
      locationValue = locationList.get(0);
    }
    return locationValue;
  }
  
  public Map<String, List<String>> getHeaders() { return headers; }
  
  public String getCoreResponsebody() {
    return coreResponsebody;
  }
  
  public void setCoreResponsebody(String coreResponsebody) {
    this.coreResponsebody = coreResponsebody;
  }
  
  public String getMethod() {
    return method;
  }
  
  public void setMethod(String method) {
    this.method = method;
  }
  
  public int getStatusCode() {
    return statusCode;
  }
  
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
  
  public String getEtag() { return etag; }
  
  public void setEtag(String etag) { this.etag = etag; }
  
  public boolean isBasicResponse() { return basicResponse; }
  
  public void setBasicResponse(boolean basicResponse) { this.basicResponse = basicResponse; }
  
  public String getLocation() { return location; }
  
  public void setLocation(String location) { this.location = location; }
  
  public String getCoreMsg() { return coreMsg; }
  
  public void setCoreMsg(String coreMsg) { this.coreMsg = coreMsg; }
}
