package edu.utexas.tacc.tapis.meta.api.resources;

import edu.utexas.tacc.tapis.meta.config.OkSingleton;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.utils.MetaAppConstants;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;


/**
 * this class is responsible for proxying the requests from the security container sidecar to the core container
 *
 */
public class CoreRequest {
  private static final long serialVersionUID = 2267124925495540982L;
  
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(CoreRequest.class);
  
  // fields
  private static String coreServiceUrl;  // look at a static init block
  private OkHttpClient okHttpClient = OkSingleton.getInstance();
  private String pathUri;
  private String pathURL;
  
  // constructor(s)
  public CoreRequest(String _pathUri){
    // this gives us the valid Core server path URI by stripping the service
    // prefix from the beginning of the path
    
    _log.debug("constructed with path Uri : "+_pathUri);
  
    pathUri = _pathUri.replace(MetaAppConstants.META_REQUEST_PREFIX,"");
    pathURL = RuntimeParameters.getInstance().getCoreServer()+pathUri;
  
    _log.debug("constructed with path URL : "+pathURL);
  }
  
  // proxy GET request
  public CoreResponse proxyGetRequest(){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .build();
  
    // set runtime parameter for connection tineout setting from
    long connection_timeout = Long.parseLong(RuntimeParameters.getInstance().getCoreserver_connection_timeout());
    okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(connection_timeout, TimeUnit.MINUTES)
        .readTimeout(connection_timeout, TimeUnit.MINUTES)
        .writeTimeout(connection_timeout, TimeUnit.MINUTES)
        .retryOnConnectionFailure(false)

        .build();
    
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
      
    } catch (SocketTimeoutException e){
      _log.debug("socket exception");
      e.printStackTrace();
    } catch (IOException e) {
      // TODO log message
      // TODO throw a custom exception about request failure to core
      _log.debug("io exception");
      e.printStackTrace();
    }
  
    _log.debug("call to host : GET "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
    
    return coreResponse;
  }
  
  // proxy PUT request
  public CoreResponse  proxyPutRequest(String json){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .put(body)
        .build();
  
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
    
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  // proxy POST request
  public CoreResponse proxyPostRequest(String json){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .post(body)
        .build();
  
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
    
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  // proxy DELETE request
  public CoreResponse  proxyDeleteRequest(HttpHeaders _httpHeaders){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
  
    String headerValue = null;
    okhttp3.Request coreRequest = null;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    if(_httpHeaders.getRequestHeaders().containsKey("If-Match")){
      headerValue = _httpHeaders.getHeaderString("If-Match");
      coreRequest = new Request.Builder()
          .url(pathURL)
          .addHeader("If-Match",headerValue)
          .delete()
          .build();
    }else {
      coreRequest = new Request.Builder()
          .url(pathURL)
          .delete()
          .build();
    }
    
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
    
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  // --------------------------------  proxy Patch request  --------------------------------
  public CoreResponse proxyPatchRequest(String json) {
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .patch(body)
        .build();
  
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
    
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  // TODO --------------------------------  proxy Generic request  --------------------------------
  public CoreResponse proxyRequest(okhttp3.Request coreRequest){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // RequestBody body = RequestBody.create(json, JSON);
    // okhttp3.Request coreRequest = new Request.Builder()
    //    .url(pathURL)
    //    .post(body)
    //    .build();
    
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      String sb = coreResponse.getCoreResponsebody();
      
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
    
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
    
    return coreResponse;
  }
  
}
