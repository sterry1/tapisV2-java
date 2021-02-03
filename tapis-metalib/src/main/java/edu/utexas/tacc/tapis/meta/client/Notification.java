package edu.utexas.tacc.tapis.meta.client;

import edu.utexas.tacc.tapis.meta.config.OkSingleton;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static edu.utexas.tacc.tapis.meta.config.OkSingleton.getInstance;

public class Notification {
  private static final Logger _log = LoggerFactory.getLogger(Notification.class);
  
  private final String notificationUrl;
  
  public Notification(String _notificationUrl) {
    notificationUrl = _notificationUrl;
  }
  
  public boolean isValidNotificationUrl(){
    if(StringUtils.isEmpty(notificationUrl)){
      _log.debug("This url is empty or null : "+notificationUrl);
      return false;
    }
    
    HttpUrl httpUrl = HttpUrl.parse(notificationUrl);
    if (httpUrl != null){
      return true;
    }else{
      _log.debug("This url is bad : "+notificationUrl);
      return false;
    }
  }
  
  public void sendNotification(String jsonResponse){
    OkHttpClient client = getInstance();
    client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
    
    RequestBody body = RequestBody.create(
        MediaType.parse("application/json"), jsonResponse);
  
    Request request = new Request.Builder()
        .url(notificationUrl)
        .post(body)
        .build();
    _log.debug("Sending FINISHED response to : "+notificationUrl);
    try(Response response = client.newCall(request).execute()){
      _log.debug("This Response 1 succeeded : "+ response);
      _log.debug(response.body().string());
    
    } catch (IOException e) {
      _log.debug("Response 1 failed: \n" + e);
    }
  
/*
    OkHttpClient client2 = getInstance();
    client2 = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build();
  
    try(Response response = client2.newCall(request).execute()){
      _log.debug("This Response 2 succeeded : "+ response);
      _log.debug(response.body().string());
    } catch (IOException e) {
      _log.debug("Response 2 failed: \n" + e);
    }
*/
  
  
  
  
  }
  
  
}
