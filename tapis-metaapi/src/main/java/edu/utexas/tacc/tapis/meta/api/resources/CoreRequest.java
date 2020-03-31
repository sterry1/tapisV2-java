package edu.utexas.tacc.tapis.meta.api.resources;

import edu.utexas.tacc.tapis.meta.config.OkSingleton;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.utils.MetaAppConstants;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


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
    
    _log.debug("pathUri : "+_pathUri);
  
    pathUri = _pathUri.replace(MetaAppConstants.META_REQUEST_PREFIX,"");
    pathURL = RuntimeParameters.getInstance().getCoreServer()+pathUri;
  
    _log.debug("pathURL : "+pathURL);
  }
  
  // proxy GET request
  public CoreResponse proxyGetRequest(){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .build();
  
    Response response = null;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      StringBuilder sb = coreResponse.getCoreResponsebody();
      
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
    
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody().toString());
    
    return coreResponse;
  }
  
  // proxy PUT request
  public CoreResponse  proxyPutRequest(){
    return null;
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
      StringBuilder sb = coreResponse.getCoreResponsebody();
    
    } catch (IOException e) {
      // todo log message
      // todo throw a custom exception about request failure to core
      e.printStackTrace();
    }
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody().toString());
  
    return coreResponse;
  }
  
  // proxy DELETE request
  public CoreResponse  proxyDeleteRequest(){
    return null;
  }
  
  
}
