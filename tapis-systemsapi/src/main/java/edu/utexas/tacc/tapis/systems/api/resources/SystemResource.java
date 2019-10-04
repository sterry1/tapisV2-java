package edu.utexas.tacc.tapis.systems.api.resources;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import edu.utexas.tacc.tapis.systems.dao.SystemsDao;
import edu.utexas.tacc.tapis.systems.model.System;
import edu.utexas.tacc.tapis.shared.exceptions.TapisJSONException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.schema.JsonValidator;
import edu.utexas.tacc.tapis.shared.schema.JsonValidatorSpec;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.sharedapi.utils.RestUtils;

@Path("/system")
public class SystemResource
{
  /* **************************************************************************** */
  /*                                   Constants                                  */
  /* **************************************************************************** */
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(SystemResource.class);

  // Json schema resource files.
  private static final String FILE_SYSTEMS_CREATE_REQUEST = "/edu/utexas/tacc/tapis/systems/api/jsonschema/SystemCreateRequest.json";

  /* **************************************************************************** */
  /*                                    Fields                                    */
  /* **************************************************************************** */
  /* Jax-RS context dependency injection allows implementations of these abstract
   * types to be injected (ch 9, jax-rs 2.0):
   *
   *      javax.ws.rs.container.ResourceContext
   *      javax.ws.rs.core.Application
   *      javax.ws.rs.core.HttpHeaders
   *      javax.ws.rs.core.Request
   *      javax.ws.rs.core.SecurityContext
   *      javax.ws.rs.core.UriInfo
   *      javax.ws.rs.core.Configuration
   *      javax.ws.rs.ext.Providers
   *
   * In a servlet environment, Jersey context dependency injection can also
   * initialize these concrete types (ch 3.6, jersey spec):
   *
   *      javax.servlet.HttpServletRequest
   *      javax.servlet.HttpServletResponse
   *      javax.servlet.ServletConfig
   *      javax.servlet.ServletContext
   *
   * Inject takes place after constructor invocation, so fields initialized in this
   * way can not be accessed in constructors.
   */
  @Context
  private HttpHeaders _httpHeaders;
  @Context
  private Application _application;
  @Context
  private UriInfo _uriInfo;
  @Context
  private SecurityContext _securityContext;
  @Context
  private ServletContext _servletContext;
  @Context
  private HttpServletRequest _request;

  // TODO Remove hard coded values
  private static final String tenant = "tenant1";

  /* **************************************************************************** */
  /*                                Public Methods                                */
  /* **************************************************************************** */

  /**
   * Create a system
   * @param prettyPrint
   * @param payloadStream
   * @return
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      description = "Create a system using a request body. " +
          "System name must be unique within a tenant and can be composed of alphanumeric characters " +
          "and the following special characters: [-._~]. Name must begin with an alphabetic character " +
          "and can be no more than 256 characters in length. " +
          "Description is optional with a maximum length of 2048 characters.",
      tags = "system",
      responses = {
          @ApiResponse(responseCode = "201", description = "System created."),
          @ApiResponse(responseCode = "400", description = "Input error."),
          @ApiResponse(responseCode = "401", description = "Not authorized."),
          @ApiResponse(responseCode = "500", description = "Server error.")
      }
  )
  public Response createSystem(@DefaultValue("false") @QueryParam("pretty") boolean prettyPrint,
                               InputStream payloadStream)
  {
    // Trace this request.
    if (_log.isTraceEnabled())
    {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "createSystem",
                                   "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // ------------------------- Validate Payload -------------------------
    // Read the payload into a string.
    String json = null;
    try { json = IOUtils.toString(payloadStream, Charset.forName("UTF-8")); }
    catch (Exception e)
    {
      String msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", "post system", e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // Create validator specification.
    JsonValidatorSpec spec = new JsonValidatorSpec(json, FILE_SYSTEMS_CREATE_REQUEST);

    // Make sure the json conforms to the expected schema.
    try { JsonValidator.validate(spec); }
    catch (TapisJSONException e)
    {
      String msg = MsgUtils.getMsg("TAPIS_JSON_VALIDATION_ERROR", e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // Extract name, description, owner and host
    String name = null;
    String description = null;
    String owner = null;
    String host = null;
    JsonObject obj = TapisGsonUtils.getGson().fromJson(json, JsonObject.class);
    name = obj.get("name").getAsString();
    description = obj.get("description").getAsString();
    owner = obj.get("owner").getAsString();
    host = obj.get("host").getAsString();

    // Check values.
    if (StringUtils.isBlank(name))
    {
      String msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", "createSystem", "Null or empty name.");
      _log.error(msg);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    if (StringUtils.isBlank(owner))
    {
      String msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", "createSystem", "Null or empty owner.");
      _log.error(msg);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    if (StringUtils.isBlank(host))
    {
      String msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", "createSystem", "Null or empty host.");
      _log.error(msg);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ------------------------- Create System Record ---------------------
    try
    {
      SystemsDao dao = new SystemsDao();
      // TODO remove hard coded values
      dao.createSystem(tenant, name, description, owner, host, true,
                       "bucket1", "/root1", "effUser1");
    }
    catch (Exception e)
    {
      String msg = MsgUtils.getMsg("SYSTEMS_CREATE_ERROR", name, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success ------------------------------- 
    // Success means we found the job.
    return Response.status(Status.OK).entity(RestUtils.createSuccessResponse(
        MsgUtils.getMsg("SYSTEMS_CREATED", name), prettyPrint, "Created system record")).build();
  }

  /* ---------------------------------------------------------------------------- */
  /* getSystemByName:                                                             */
  /* ---------------------------------------------------------------------------- */
  @GET
  @Path("/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSystemByName(@PathParam("name") String name,
                                  @DefaultValue("false") @QueryParam("pretty") boolean prettyPrint,
                                  @DefaultValue("false") @QueryParam("returnCredentials") boolean getCreds)
  {
    // Trace this request.
    if (_log.isTraceEnabled())
    {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "getSystemByName",
                                   "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    SystemsDao dao = new SystemsDao();
    System system = null;
    try
    {
      system = dao.getSystemByName(name);
    }
    catch (Exception e)
    {
      String msg = MsgUtils.getMsg("SYSTEMS_GET_NAME_ERROR", name, e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // The specified job was not found for the tenant.
    if (system == null)
    {
      String msg = MsgUtils.getMsg("SYSTEMS_NOT_FOUND", name);
      _log.warn(msg);
      return Response.status(Status.NOT_FOUND).entity(RestUtils.createErrorResponse(MsgUtils.getMsg("TAPIS_NOT_FOUND", "System", name),
                                               prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    // Success means we found the job.
    return Response.status(Status.OK).entity(RestUtils.createSuccessResponse(
        MsgUtils.getMsg("TAPIS_FOUND", "System", name), prettyPrint, system)).build();
  }

  /* ---------------------------------------------------------------------------- */
  /* getSystems:                                                                  */
  /* ---------------------------------------------------------------------------- */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSystems(@DefaultValue("false") @QueryParam("pretty") boolean prettyPrint)
  {
    // Trace this request.
    if (_log.isTraceEnabled())
    {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "getSystems",
                                   "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // ------------------------- Retrieve all records -----------------------------
    SystemsDao dao = new SystemsDao();
    List<System> systems = null;
    try { systems = dao.getSystems(); }
    catch (Exception e)
    {
      String msg = MsgUtils.getMsg("SYSTEMS_SELECT_ERROR", e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(RestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    int cnt = systems == null ? 0 : systems.size();
    return Response.status(Status.OK).entity(RestUtils.createSuccessResponse(
        MsgUtils.getMsg("TAPIS_FOUND", "Systems", cnt + " items"), prettyPrint, systems)).build();
  }
}