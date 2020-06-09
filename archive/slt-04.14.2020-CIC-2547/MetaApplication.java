package edu.utexas.tacc.tapis.meta.api;

import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.ApplicationInfoRequestFilter;
import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.MetaPermissionsRequestFilter;
import edu.utexas.tacc.tapis.meta.api.resources.ResourceBucket;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.ClearThreadLocalRequestFilter;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.ClearThreadLocalResponseFilter;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.JWTValidateRequestFilter;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.TestParameterRequestFilter;
import edu.utexas.tacc.tapis.sharedapi.providers.TapisExceptionMapper;
import edu.utexas.tacc.tapis.sharedapi.providers.ValidationExceptionMapper;
import edu.utexas.tacc.tapis.sharedapi.security.TenantManager;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("/meta")
public class MetaApplication extends ResourceConfig {
  
  public MetaApplication()
  {
    // Log our existence.
    System.out.println("**** Starting tapis-metaapi ****");
  
    // load our runtime parameters
    RuntimeParameters runtimeParameters = RuntimeParameters.getInstance();
  
    // Register the swagger resources that allow the
    // documentation endpoints to be automatically generated.
    register(OpenApiResource.class);
    register(AcceptHeaderOpenApiResource.class);
    
    // register the providers, resources we need.
    register(ResourceBucket.class);
    register(TestParameterRequestFilter.class);
    register(ApplicationInfoRequestFilter.class);
    register(ValidationExceptionMapper.class);
    register(MetaPermissionsRequestFilter.class);
    register(TapisExceptionMapper.class);
    register(ClearThreadLocalRequestFilter.class);
    register(ClearThreadLocalResponseFilter.class);
    
    // only register this class if we are in Tapis V3 environment
    // TODO new wrinkle on jwt validation for V2
    if(!runtimeParameters.isV2CompatibilityMode()){
      register(JWTValidateRequestFilter.class);
    }
  
    // packages("edu.utexas.tacc.tapis");
    setApplicationName("meta");
  
    // Force runtime initialization of the tenant manager.  This creates the
    // singleton instance of the TenantManager that can then be accessed by
    // all subsequent application code--including filters--without reference
    // to the tenant service base url parameter.
    try {
      // The base url of the tenants service is a required input parameter.
      // We actually retrieve the tenant list from the tenant service now
      // to fail fast if we can't access the list.
      String url = runtimeParameters.getTenantBaseUrl();
      
      // if running in V2 compatability this evaluates to true
      if(runtimeParameters.isV2CompatibilityMode()){
        return;
      }
      TenantManager.getInstance(url).getTenants();
    } catch (Exception e) {
      // We don't depend on the logging subsystem.
      System.out.println("**** FAILURE TO INITIALIZE: tapis-metaapi ****");
      e.printStackTrace();
      throw e;
    }
  
  
  }

}
