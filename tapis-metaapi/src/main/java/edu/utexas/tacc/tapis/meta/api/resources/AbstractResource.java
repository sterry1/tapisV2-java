package edu.utexas.tacc.tapis.meta.api.resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import edu.utexas.tacc.tapis.shared.exceptions.TapisImplException;
import edu.utexas.tacc.tapis.shared.exceptions.TapisNotFoundException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadContext;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadLocal;
import edu.utexas.tacc.tapis.sharedapi.responses.RespName;
import edu.utexas.tacc.tapis.sharedapi.responses.results.ResultName;
import edu.utexas.tacc.tapis.sharedapi.utils.TapisRestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AbstractResource {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(AbstractResource.class);
  
  protected boolean checkPayload(InputStream payload){
    try {
      int avail = payload.available();
      if(avail > 0){
        return true;
      }
    } catch (IOException e) {
      _log.error("Payload has a problem. It is probably closed");
    }
    return false;
  }
  
  protected JsonObject getValidJson(InputStream payload){
    StringBuilder stringPayload = new StringBuilder();

    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line;
      while ((line = in.readLine()) != null) {
        stringPayload.append(line);
      }
    }catch (IOException e) {
      String msg = "getValidJson Error Reading the payload from request";
      _log.debug(msg);
    }
    
    JsonObject jsonObject;
    String string = stringPayload.toString();
    if (!StringUtils.isEmpty(string)){
      try {
        jsonObject = new Gson().fromJson(string,JsonObject.class);
      } catch (JsonSyntaxException e ) {
        _log.error("Not valid Json, "+e.getMessage());
        return null;
      }
      // must be good json so send it back
      return jsonObject;
    }else {
      return null;
    }
  }
  
  protected Response getExceptionResponse(Exception e, String message,
                                          boolean prettyPrint, String... parms)
  {
    // Select and print a message and the caller's stack frame info.
    String msg = message == null ? e.getMessage() : message;
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    String caller = stack.length > 1 ? ("\n   " + stack[2]) : "";
    _log.error(msg + caller);
    
    // Send response based on the type of exception.
    if (e instanceof TapisNotFoundException) {
      
      // Create the response payload.
      TapisNotFoundException e2 = (TapisNotFoundException) e;
      ResultName missingName = new ResultName();
      missingName.name = e2.missingName;
      RespName r = new RespName(missingName);
      
      // Get the not found message parameters.
      String missingType  = parms.length > 0 ? parms[0] : "entity";
      String missingValue = parms.length > 1 ? parms[1] : missingName.name;
      
      return Response.status(Response.Status.NOT_FOUND).entity(TapisRestUtils.createSuccessResponse(
          MsgUtils.getMsg("TAPIS_NOT_FOUND", missingType, missingValue),
          prettyPrint, r)).build();
    }
    else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
          entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
  }
  
}
