package edu.utexas.tacc.tapis.meta.api.jaxrs.filters;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.utils.MetaAppConstants;
import edu.utexas.tacc.tapis.meta.utils.MetaSKPermissionsMapper;
import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadContext;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static edu.utexas.tacc.tapis.meta.config.RuntimeParameters.V2_JWT_AUTH_HEADER_PREFIX;

@Provider
@Priority(MetaAppConstants.META_FILTER_PRIORITY_PERMISSIONS)
public class MetaPermissionsRequestFilter implements ContainerRequestFilter {
  
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(MetaPermissionsRequestFilter.class);
  
  
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    // Tracing.
    if (_log.isTraceEnabled())
      _log.trace("Executing Permissions request filter: " + this.getClass().getSimpleName() + ".");
  
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get();
    boolean v2exists = false;
    
    //   get the path and jwt from runtime parameters
    RuntimeParameters runTime = RuntimeParameters.getInstance();
    // check to see if running in V2 compatability mode
    if(runTime.isV2CompatibilityMode()){
      _log.trace("WARNING V2 Compatibility mode is ON!!!     " + this.getClass().getSimpleName() + ".");
      v2exists = existsV2Jwt(requestContext);
      if(!v2exists){
        StringBuilder msg = new StringBuilder()
            .append("NO V2 JWT for validating while in V2 Compatible mode");
        _log.debug(msg.toString());
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(msg.toString()).build());
        return;
      }
    }
  
    // let's turn off permissions cd for testing without SK client calls
    if(!MetaAppConstants.TAPIS_ENVONLY_META_PERMISSIONS_CHECK){
      _log.trace("WARNING Permissions Check is turned OFF!!! for testing" + this.getClass().getSimpleName() + ".");
      return;
    }
  
    // is this request permitted
    boolean isPermitted = false;
    
    //   permissions spec that is derived from request
    String permissionsSpec = null;
  
    // uri info for request V2 & V3
    String uri = requestContext.getUriInfo().getPath();
    
    
    /**************************************************************************
     * This section of code bifrucates the pathways for V3 vs V2 security processing
     * If we are running V2 compatibility mode we look for and process an V2JWT
     * otherwise we are running V3 mode.
     **************************************************************************/
    
    // check to see if we are running in V2 compatibility mode.
    // we"ll need to look for a V2JWT and process the claims.
    
    if(runTime.isV2CompatibilityMode()){
      isPermitted = false;
      // check for V2 jwt
      if(v2exists){
        _log.debug("We have at a minimum a header value with v2 assertion label");
      }
      existsV2Jwt(requestContext);
      // Map<String,String> jwtMap = checkForV2Jwt(requestContext);
      // if(jwtMap != null){
      
      // }
  
      //   permissions spec that is derived from request
      // permissionsSpec = mapRequestToPermissions(requestContext);
  
      // go do a completely different pathway
      // isPermitted = isPermittedV2CompatibilityMode(requestContext);
      
    } else {  // else process as normal
      // done 1. turn request into a permission spec.
      // done 2. utilize the SK client sdk to ask isPermitted
      // done 3. decide yes or no based on response
      // done 4. add a permission switch for allowAll for testing
  
      //   the JWTValidateRequestFilter has already consumed and validated the V3 JWT at this point.
      //   setting up the ThreadContext information about an authenticated user.
      //   this is the permissions spec that is derived from request
      permissionsSpec = mapRequestToPermissions(threadContext,requestContext);
      
      //   Use Meta master token for call to SK
      SKClient skClient = new SKClient(runTime.getSkSvcURL(), runTime.getMetaToken());
  
      // Is this a request with a Service token?
      if(threadContext.getAccountType() == TapisThreadContext.AccountType.service){
        isPermitted = serviceJWT(threadContext, skClient, permissionsSpec);
      }
  
      // Is this a request with a User token?
      if(threadContext.getAccountType() == TapisThreadContext.AccountType.user){
        isPermitted = userJWT(threadContext, skClient, permissionsSpec);
      }
    }
    
    if(!isPermitted){
      uri = (uri.equals("")) ? "root" : uri;
      StringBuilder msg = new StringBuilder()
          .append("request for this uri path "+uri+" permissions spec ")
          .append(permissionsSpec)
          .append(" is NOT permitted.");
      
      _log.debug(msg.toString());
      requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(msg.toString()).build());
      return;
    }
    
    //------------------------------  permitted to continue  ------------------------------
    StringBuilder msg = new StringBuilder()
        .append("request for this uri permission spec ")
        .append(permissionsSpec)
        .append(" is permitted.");
    
    _log.debug(msg.toString());
  }
  
  /**
   *
   * @param threadContext
   * @return
   */
  private boolean isPermittedV2CompatibilityMode(ContainerRequestContext threadContext) {
    System.out.println(threadContext.getHeaderString(MetaAppConstants.TAPIS_TENANT_HEADER_NAME));
    return false;
  }
  
  /**
   * Check Permissions based on service JWT from request.
   * make an isPermitted request of the SK
   * @param threadContext
   * @param skClient
   * @param permissionsSpec
   */
  private boolean serviceJWT(TapisThreadContext threadContext, SKClient skClient, String permissionsSpec){
    // 1. If a service receives a request that contains a service JWT,
    //    the request is rejected if it does not have the X-Tapis-Tenant and X-Tapis-User headers set.
    // **** this check happens in the JWT filter
    
    // 2. If a service receives a request that has the X-Tapis-Tenant and X-Tapis-User headers set,
    //    the service should forward those header values on  to any service to service call it may make.
    // skClient was created with metav3 master service token.
    
    boolean isPermitted = false;
    
    skClient.addDefaultHeader(MetaAppConstants.TAPIS_USER_HEADER_NAME, threadContext.getOboUser());
    skClient.addDefaultHeader(MetaAppConstants.TAPIS_TENANT_HEADER_NAME, threadContext.getOboTenantId());
    
    // check skClient.isPermitted against the requested uri path
    try {
      // checking obo tenant and user
      StringBuilder msg = new StringBuilder();
      msg.append("Permissions check for Tenant ")
         .append(threadContext.getOboTenantId())
         .append(", User ")
         .append(threadContext.getOboUser())
         .append(" with permissions ")
         .append(permissionsSpec+".");
      
      _log.debug(msg.toString());
      isPermitted = skClient.isPermitted(threadContext.getOboTenantId(),threadContext.getOboUser(), permissionsSpec);
    } catch (TapisClientException e) {
      // todo add log msg for exception
      e.printStackTrace();
    }
    
    return isPermitted;
  }
  
  private boolean userJWT(TapisThreadContext threadContext, SKClient skClient, String permissionsSpec){
    // 3. Services should reject any request that contains a user JWT and has the X-Tapis-Tenant
    //    or X-Tapis-User headers set.
    // assume this is already done via the jwt filter
    
    // 4. If a service receives a request that contains a user JWT, the service should use the JWT's
    //    tenant and user values as the X-Tapis-Tenant and X-Tapis-User headers on any call it may make
    //    to another service to satisfy the request.
    
    boolean isPermitted = false;
    
    // set X-Tapis-Tenant and X-Tapis-User headers with jwtTenant & jwtUser
    // with meta user token
    skClient.addDefaultHeader(MetaAppConstants.TAPIS_USER_HEADER_NAME, threadContext.getJwtUser());
    skClient.addDefaultHeader(MetaAppConstants.TAPIS_TENANT_HEADER_NAME, threadContext.getJwtTenantId());
    
    // check skClient.isPermitted against the requested uri path
    try {
      // checking obo tenant and user
      isPermitted = skClient.isPermitted(threadContext.getOboTenantId(),threadContext.getOboUser(), permissionsSpec);
    } catch (TapisClientException e) {
      // todo add log msg for exception
      e.printStackTrace();
    }
    
    return isPermitted;
  }
  
  
  /**
   * Turn the request uri into a SK permissions spec to check authorization
   *  in the V3 Tapis environment
   *
   * @param threadContext
   * @param requestContext
   * @return  the String representing a permissions spec for comparison
   */
  private String mapRequestToPermissions(TapisThreadContext threadContext,
                                         ContainerRequestContext requestContext) {
    String requestMethod = requestContext.getMethod();
    String requestUri = requestContext.getUriInfo().getPath();
    String tenantId = threadContext.getOboTenantId();
    // getting the tenant info
    MetaSKPermissionsMapper mapper = new MetaSKPermissionsMapper(requestUri, tenantId);
    String permSpec = mapper.convert(requestMethod);
    
    return permSpec;
  }
  
  /**
   * Turn the request uri into a SK permissions spec to check authorization
   *  in the V3 Tapis environment
   *
   * @param requestContext
   * @return  the String rdepresenting a permissions spec for comparison
   */
  private String mapRequestToPermissions(ContainerRequestContext requestContext) {
    String requestMethod = requestContext.getMethod();
    String requestUri = requestContext.getUriInfo().getPath();
    var headerMap = requestContext.getHeaders();
    
    String tenantId = "dev";
    // getting the tenant info
    MetaSKPermissionsMapper mapper = new MetaSKPermissionsMapper(requestUri, tenantId);
    String permSpec = mapper.convert(requestMethod);
    
    return permSpec;
  }
  
  private boolean existsV2Jwt(ContainerRequestContext requestContext) {
    boolean exists = false;
    MultivaluedMap<String, String> headers = requestContext.getHeaders();
  
    for(String headerKey : headers.keySet()) {
      if(headerKey.startsWith(MetaAppConstants.V2_JWT_AUTH_HEADER_PREFIX.toLowerCase())){
        _log.debug("found v2 header name : "+headerKey);
        return true;
        // String tenant = key.replace()
        // return encodedJWT = headers.getFirst(key);
      }
    }
    return false;
  }
  
  
  /**
   * checks for the presents of a V2 jwt and its value
   * @param requestContext
   * @return encodedJWT if present else return null
   */
  private Map<String,String> checkForV2Jwt(ContainerRequestContext requestContext) {
    // Extract the jwt header from the set of headers.
    // We expect the key search to be case insensitive.
    String encodedJWT = null;
    Map<String,String> resultMap = null;
    MultivaluedMap<String, String> headers = requestContext.getHeaders();
    
    for(String key : headers.keySet()) {
      if(key.startsWith(MetaAppConstants.V2_JWT_AUTH_HEADER_PREFIX.toLowerCase())){
        _log.debug("found v2 header name : "+key.toLowerCase());
        
        // String tenant = key.replace()
        // return encodedJWT = headers.getFirst(key);
      }
    }
    return resultMap;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getTenantAndToken:                                                    */
  /* ---------------------------------------------------------------------- */
  /**
   * Gets the jwt header and value out of the request headers
   *   parses the header name for the tenant value and the jwt token string
   * @param requestContext
   * @return Map of two values "tenant" string name and "jwtToken" string
   *         for processing
   */
  private Map getTenantAndToken(ContainerRequestContext requestContext) {
    MultivaluedMap<String, String> headers = requestContext.getHeaders();
    Set<String> headerNames = headers.keySet();
    for (String name : headerNames){
      if(name.startsWith(V2_JWT_AUTH_HEADER_PREFIX)){
        Map<String,String>jwtMap = new HashMap<>();
        jwtMap.put("tenant",name.replace(V2_JWT_AUTH_HEADER_PREFIX,""));
        //HeaderValues values = headers.get(name);
        //jwtMap.put("jwtToken",values.getFirst());
        //LOGGER.debug("jwt : "+values.getFirst());
        return jwtMap;
      }
    }
    return null;
  }
  
  
}
