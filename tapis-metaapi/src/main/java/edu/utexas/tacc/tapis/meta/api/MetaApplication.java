package edu.utexas.tacc.tapis.meta.api;

import edu.utexas.tacc.aloe.sharedapi.jaxrs.filters.ClearThreadLocalRequestFilter;
import edu.utexas.tacc.aloe.sharedapi.jaxrs.filters.ClearThreadLocalResponseFilter;
import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.ApplicationInfoRequestFilter;
import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.JWTValidateRequestFilterV2;
import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.MetaPermissionsRequestFilter;
import edu.utexas.tacc.tapis.meta.api.resources.ResourceBucket;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.permissions.V2PermissionsRegistry;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.exceptions.runtime.TapisRuntimeException;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.TestParameterRequestFilter;
import edu.utexas.tacc.tapis.sharedapi.providers.TapisExceptionMapper;
import edu.utexas.tacc.tapis.sharedapi.providers.ValidationExceptionMapper;
import edu.utexas.tacc.tapis.sharedapi.security.TenantManager;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("/v3")
public class MetaApplication extends ResourceConfig {
  
  public MetaApplication() throws TapisException {
    // Log our existence.
    System.out.println("**** Starting tapisv2-metaapiv3 ****");
    
    // load our runtime parameters
    System.out.println("**** Loading Runtime parameters ...");
    RuntimeParameters runtimeParameters = RuntimeParameters.getInstance();
    StringBuilder buf = new StringBuilder(2500); // capacity to avoid resizing
    buf.append("\n------- Starting Meta Service ");
    buf.append(" -------");
  
  
    //----------------------   Initialize Permissions Registry   ----------------------
    // 1. initialize Registry
    V2PermissionsRegistry registry = null;
    try {
      registry = V2PermissionsRegistry.getInstance();
    }catch (Exception e){
      String msg = "METAV3 PERMISSIONS REGISTRY INITIALIZATION FAILED \n"
          + RuntimeParameters.SERVICE_NAME_META +"\n"
          + e.getMessage();
      System.out.println(msg);
      throw new TapisRuntimeException(msg, e);
    }
  
    // Dump the runtime configuration.
    runtimeParameters.getRuntimeInfo(buf);
    buf.append("\n---------------------------------------------------\n");
  
    // Write the output information.
    System.out.println(buf.toString());
  
  
    // Register the swagger resources that allow the
    // documentation endpoints to be automatically generated.
    // TODO expand to all endpoints for auto generation of openapi definition
    register(OpenApiResource.class);
    register(AcceptHeaderOpenApiResource.class);
    
    // register the providers, resources we need.
    register(ResourceBucket.class);
    register(TestParameterRequestFilter.class);
    register(ApplicationInfoRequestFilter.class);
    register(ValidationExceptionMapper.class);
    register(MetaPermissionsRequestFilter.class);
    register(TapisExceptionMapper.class);
    register(JWTValidateRequestFilterV2.class);
    register(ClearThreadLocalRequestFilter.class);
    register(ClearThreadLocalResponseFilter.class);
    
    
    // only register this class if we are in Tapis V3 environment
    // packages("edu.utexas.tacc.tapis");
    setApplicationName("meta");
  
  }

}
