package edu.utexas.tacc.tapis.systems.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//import org.glassfish.jersey.server.ResourceConfig;

import edu.utexas.tacc.tapis.systems.api.resources.SystemResource;
import edu.utexas.tacc.tapis.systems.api.resources.SystemsResource;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import java.util.HashSet;
import java.util.Set;

// The path here is appended to the context root and
// is configured to work when invoked in a standalone 
// container (command line) and in an IDE (eclipse). 
@ApplicationPath("/systems")
public class SystemsApplication extends Application// extends ResourceConfig
{
  public SystemsApplication() {
    // Log our existence.
    System.out.println("**** Starting tapis-systems ****");

    // Register the swagger resources that allow the
    // documentation endpoints to be automatically generated.
//        register(OpenApiResource.class);
//        register(AcceptHeaderOpenApiResource.class);

    // We specify what packages JAX-RS should recursively scan
    // to find annotations.  By setting the value to the top-level
    // tapis directory in all projects, we can use JAX-RS annotations
    // in any tapis class.  In particular, the filter classes in
    // tapis-sharedapi will be discovered whenever that project is
    // included as a maven dependency.
//        packages("edu.utexas.tacc.tapis");
//        setApplicationName("systems");

  }

// RESTEasy
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> empty = new HashSet<>();
    return empty;
  }

  @Override
  public Set<Object> getSingletons()
  {
    Set<Object> singletons = new HashSet<>();
    singletons.add(new SystemsResource());
    singletons.add(new SystemResource());
    return singletons;
  }
// RESTEasy
}
