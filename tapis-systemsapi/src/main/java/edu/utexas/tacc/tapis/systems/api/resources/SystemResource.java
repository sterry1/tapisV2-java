package edu.utexas.tacc.tapis.systems.api.resources;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import edu.utexas.tacc.tapis.sharedapi.security.AuthenticatedUser;
import org.glassfish.grizzly.http.server.Request;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import edu.utexas.tacc.tapis.shared.exceptions.TapisJSONException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.schema.JsonValidator;
import edu.utexas.tacc.tapis.shared.schema.JsonValidatorSpec;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadContext;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadLocal;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.sharedapi.responses.RespChangeCount;
import edu.utexas.tacc.tapis.sharedapi.responses.RespNameArray;
import edu.utexas.tacc.tapis.sharedapi.responses.RespResourceUrl;
import edu.utexas.tacc.tapis.sharedapi.responses.results.ResultChangeCount;
import edu.utexas.tacc.tapis.sharedapi.responses.results.ResultNameArray;
import edu.utexas.tacc.tapis.sharedapi.responses.results.ResultResourceUrl;
import edu.utexas.tacc.tapis.sharedapi.utils.RestUtils;
import edu.utexas.tacc.tapis.sharedapi.utils.TapisRestUtils;
import edu.utexas.tacc.tapis.systems.api.requests.ReqCreateSystem;
import edu.utexas.tacc.tapis.systems.api.responses.RespSystem;
import edu.utexas.tacc.tapis.systems.api.utils.ApiUtils;
import edu.utexas.tacc.tapis.systems.model.TSystem;
import edu.utexas.tacc.tapis.systems.model.TSystem.AccessMethod;
import edu.utexas.tacc.tapis.systems.model.TSystem.TransferMethod;
import edu.utexas.tacc.tapis.systems.service.SystemsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/*
 * JAX-RS REST resource for a Tapis System (edu.utexas.tacc.tapis.systems.model.TSystem)
 * Contains annotations which generate the OpenAPI specification documents.
 * Annotations map HTTP verb + endpoint to method invocation.
 *
 */
@Path("/")
public class SystemResource
{
  // ************************************************************************
  // *********************** Constants **************************************
  // ************************************************************************
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(SystemResource.class);

  // Json schema resource files.
  private static final String FILE_SYSTEM_CREATE_REQUEST = "/edu/utexas/tacc/tapis/systems/api/jsonschema/SystemCreateRequest.json";
  // String used to mask secrets in json
  private static final String SECRETS_MASK = "***";

  // Field names used in Json
  private static final String TSYSTEM_FIELD = "tSystem";
  private static final String NAME_FIELD = "name";
  private static final String SYSTEM_TYPE_FIELD = "systemType";
  private static final String HOST_FIELD = "host";
  private static final String DEFAULT_ACCESS_METHOD_FIELD = "defaultAccessMethod";
  private static final String ACCESS_CREDENTIAL_FIELD = "accessCredential";

  // ************************************************************************
  // *********************** Fields *****************************************
  // ************************************************************************
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
//  @Context
//  private ServletContext _servletContext;
//  @Context
//  private HttpServletRequest _request;

  // **************** Inject Services using HK2 ****************
  @Inject
  private SystemsService systemsService;

  // ************************************************************************
  // *********************** Public Methods *********************************
  // ************************************************************************

  /**
   * Create a system
   * @param prettyPrint - pretty print the output
   * @param payloadStream - request body
   * @return response containing reference to created object
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Create a system",
    description =
        "Create a system using a request body. " +
        "System name must be unique within a tenant and can be composed of alphanumeric characters " +
        "and the following special characters: [-._~]. Name must begin with an alphabetic character " +
        "and can be no more than 256 characters in length. " +
        "Description is optional with a maximum length of 2048 characters.",
    tags = "systems",
    requestBody =
      @RequestBody(
        description = "A JSON object specifying information for the system to be created.",
        required = true,
        content = @Content(schema = @Schema(implementation = ReqCreateSystem.class))
      ),
    responses = {
      @ApiResponse(responseCode = "201", description = "System created.",
                   content = @Content(schema = @Schema(implementation = RespResourceUrl.class))
      ),
      @ApiResponse(responseCode = "400", description = "Input error. Invalid JSON.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "401", description = "Not authorized.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "409", description = "System already exists.",
                   content = @Content(schema = @Schema(implementation = RespResourceUrl.class))),
      @ApiResponse(responseCode = "500", description = "Server error.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class)))
    }
  )
  public Response createSystem(@QueryParam("pretty") @DefaultValue("false") boolean prettyPrint, InputStream payloadStream,
                               @Context SecurityContext securityContext)
  {
    String opName = "createSystem";
    String msg;
    // Trace this request.
    if (_log.isTraceEnabled())
    {
//      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "createSystem",
//                                   "  " + _request.getRequestURL());
//      _log.trace(msg);
    }

    // ------------------------- Retrieve and validate thread context -------------------------
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context
    // Check that we have all we need from the context, the tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // ------------------------- Extract and validate payload -------------------------
    // Read the payload into a string.
    String rawJson;
    try { rawJson = IOUtils.toString(payloadStream, StandardCharsets.UTF_8); }
    catch (Exception e)
    {
      msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", opName , e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    // Create validator specification and validate the json against the schema
    JsonValidatorSpec spec = new JsonValidatorSpec(rawJson, FILE_SYSTEM_CREATE_REQUEST);
    try { JsonValidator.validate(spec); }
    catch (TapisJSONException e)
    {
      msg = MsgUtils.getMsg("TAPIS_JSON_VALIDATION_ERROR", e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ------------------------- Create a TSystem from the json and validate constraints -------------------------
    TSystem system;
    try {
      ReqCreateSystem req = TapisGsonUtils.getGson().fromJson(rawJson, ReqCreateSystem.class);
      system = req.tSystem;
    }
    catch (Exception e)
    {
      msg = MsgUtils.getMsg("NET_INVALID_JSON_INPUT", opName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    // Fill in defaults and check constraints on TSystem attributes
    resp = validateTSystem(system, authenticatedUser, prettyPrint);
    if (resp != null) return resp;

    // Mask any secret info that might be contained in rawJson
    String scrubbedJson = rawJson;
    if (system.getAccessCredential() != null) scrubbedJson = maskCredSecrets(rawJson);

    // ---------------------------- Make service call to create the system -------------------------------
    // Update tenant name and pull out system name for convenience
    system.setTenant(authenticatedUser.getTenantId());
    String systemName = system.getName();
    try
    {
      systemsService.createSystem(authenticatedUser, system, scrubbedJson);
    }
    catch (IllegalStateException e)
    {
      if (e.getMessage().contains("SYSLIB_SYS_EXISTS"))
      {
        // IllegalStateException with msg containing SYS_EXISTS indicates object exists - return 409 - Conflict
        msg = ApiUtils.getMsgAuth("SYSAPI_SYS_EXISTS", authenticatedUser, systemName);
        _log.warn(msg);
        return Response.status(Status.CONFLICT).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
      }
      else if (e.getMessage().contains("SYSLIB_UNAUTH"))
      {
        // IllegalStateException with msg containing SYS_UNAUTH indicates operation not authorized for apiUser - return 401
        msg = ApiUtils.getMsgAuth("SYSAPI_SYS_UNAUTH", authenticatedUser, systemName, opName);
        _log.warn(msg);
        return Response.status(Status.UNAUTHORIZED).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
      }
      else
      {
        // IllegalStateException indicates an Invalid TSystem was passed in
        msg = ApiUtils.getMsgAuth("SYSAPI_CREATE_ERROR", authenticatedUser, systemName, e.getMessage());
        _log.error(msg);
        return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
      }
    }
    catch (IllegalArgumentException e)
    {
      // IllegalArgumentException indicates somehow a bad argument made it this far
      msg = ApiUtils.getMsgAuth("SYSAPI_CREATE_ERROR", authenticatedUser, systemName, e.getMessage());
      _log.error(msg);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    catch (Exception e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CREATE_ERROR", authenticatedUser, systemName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success ------------------------------- 
    // Success means the object was created.
    ResultResourceUrl respUrl = new ResultResourceUrl();
    respUrl.url = _request.getRequestURL().toString() + "/" + systemName;
    RespResourceUrl resp1 = new RespResourceUrl(respUrl);
    return Response.status(Status.CREATED).entity(TapisRestUtils.createSuccessResponse(
      ApiUtils.getMsgAuth("SYSAPI_CREATED", authenticatedUser, systemName), prettyPrint, resp1)).build();
  }

  /**
   * getSystemByName
   * @param sysName - name of the system
   * @param getCreds - should credentials of effectiveUser be included
   * @param accessMethodStr - access method to use instead of default
   * @param prettyPrint - pretty print the output
   * @return Response with system object as the result
   */
  @GET
  @Path("{sysName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Retrieve information for a system",
      description =
          "Retrieve information for a system given the system name. " +
          "Use query parameter returnCredentials=true to have effectiveUserId access credentials " +
          "included in the response. " +
          "Use query parameter accessMethod=<method> to override default access method.",
      tags = "systems",
      responses = {
          @ApiResponse(responseCode = "200", description = "System found.",
            content = @Content(schema = @Schema(implementation = RespSystem.class))),
          @ApiResponse(responseCode = "400", description = "Input error.",
            content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
          @ApiResponse(responseCode = "404", description = "System not found.",
            content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
          @ApiResponse(responseCode = "401", description = "Not authorized.",
            content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
          @ApiResponse(responseCode = "500", description = "Server error.",
            content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class)))
      }
  )
  public Response getSystemByName(@PathParam("sysName") String sysName,
                                  @QueryParam("returnCredentials") @DefaultValue("false") boolean getCreds,
                                  @QueryParam("accessMethod") @DefaultValue("") String accessMethodStr,
                                  @QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                  @Context SecurityContext securityContext)
  {
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context

    // Trace this request.
    if (_log.isTraceEnabled())
    {
//      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "getSystemByName",
//                                   "  " + _request.getRequestURL());
//      _log.trace(msg);
    }

    // Check that we have all we need from the context, the tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // Check that accessMethodStr is valid if is passed in
    AccessMethod accessMethod = null;
    try { if (!StringUtils.isBlank(accessMethodStr)) accessMethod =  AccessMethod.valueOf(accessMethodStr); }
    catch (IllegalArgumentException e)
    {
      String msg = ApiUtils.getMsgAuth("SYSAPI_ACCMETHOD_ENUM_ERROR", authenticatedUser, sysName, accessMethodStr, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    TSystem system;
    try
    {
      system = systemsService.getSystemByName(authenticatedUser, sysName, getCreds, accessMethod);
    }
    catch (Exception e)
    {
      String msg = ApiUtils.getMsgAuth("SYSAPI_GET_NAME_ERROR", authenticatedUser, sysName, e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // Resource was not found.
    if (system == null)
    {
      String msg = ApiUtils.getMsgAuth("SYSAPI_NOT_FOUND", authenticatedUser, sysName);
      _log.warn(msg);
      return Response.status(Status.NOT_FOUND).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    // Success means we retrieved the system information.
    RespSystem resp1 = new RespSystem(system);
    return Response.status(Status.OK).entity(TapisRestUtils.createSuccessResponse(
        MsgUtils.getMsg("TAPIS_FOUND", "System", sysName), prettyPrint, resp1)).build();
  }

  /**
   * getSystemNames
   * @param prettyPrint - pretty print the output
   * @return - list of system names
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Retrieve list of system names",
    description = "Retrieve list of system names.",
    tags = "systems",
    responses = {
      @ApiResponse(responseCode = "200", description = "Success.",
                   content = @Content(schema = @Schema(implementation = RespNameArray.class))
      ),
      @ApiResponse(responseCode = "400", description = "Input error.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "401", description = "Not authorized.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "500", description = "Server error.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class)))
    }
  )
  public Response getSystemNames(@QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                 @Context SecurityContext securityContext)
  {
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context

    // Trace this request.
    if (_log.isTraceEnabled())
    {
//      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "getSystems",
//                                   "  " + _request.getRequestURL());
//      _log.trace(msg);
    }

    // Check that we have all we need from the context, the tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // ------------------------- Retrieve all records -----------------------------
    List<String> systemNames;
    try { systemNames = systemsService.getSystemNames(authenticatedUser); }
    catch (Exception e)
    {
      String msg = ApiUtils.getMsgAuth("SYSAPI_SELECT_ERROR", authenticatedUser, e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    if (systemNames == null) systemNames = Collections.emptyList();
    int cnt = systemNames.size();
    ResultNameArray names = new ResultNameArray();
    names.names = systemNames.toArray(new String[0]);
    RespNameArray resp1 = new RespNameArray(names);
    return Response.status(Status.OK).entity(TapisRestUtils.createSuccessResponse(
        MsgUtils.getMsg("TAPIS_FOUND", "Systems", cnt + " items"), prettyPrint, resp1)).build();
  }

  /**
   * deleteSystemByName
   * @param sysName - name of the system to delete
   * @param prettyPrint - pretty print the output
   * @return - response with change count as the result
   */
  @DELETE
  @Path("{sysName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Delete a system given the system name",
    description = "Delete a system given the system name. ",
    tags = "systems",
    responses = {
      @ApiResponse(responseCode = "200", description = "System deleted.",
        content = @Content(schema = @Schema(implementation = RespChangeCount.class))),
      @ApiResponse(responseCode = "400", description = "Input error.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "401", description = "Not authorized.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class))),
      @ApiResponse(responseCode = "500", description = "Server error.",
        content = @Content(schema = @Schema(implementation = edu.utexas.tacc.tapis.sharedapi.responses.RespBasic.class)))
    }
  )
  public Response deleteSystemByName(@PathParam("sysName") String sysName,
                                     @QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                     @Context SecurityContext securityContext)
  {
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context

    // Trace this request.
    if (_log.isTraceEnabled())
    {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "deleteSystemByName",
                                   "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // Check that we have all we need from the context, the tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    int changeCount;
    try
    {
      changeCount = systemsService.deleteSystemByName(authenticatedUser, sysName);
    }
    catch (Exception e)
    {
      String msg = ApiUtils.getMsgAuth("SYSAPI_DELETE_NAME_ERROR", authenticatedUser, sysName, e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    // Success means we deleted the system.
    // Return the number of objects impacted.
    ResultChangeCount count = new ResultChangeCount();
    count.changes = changeCount;
    RespChangeCount resp1 = new RespChangeCount(count);
    return Response.status(Status.OK).entity(TapisRestUtils.createSuccessResponse(
      MsgUtils.getMsg("TAPIS_DELETED", "System", sysName), prettyPrint, resp1)).build();
  }

  /* **************************************************************************** */
  /*                                Private Methods                               */
  /* **************************************************************************** */

  /**
   * Fill in defaults and check constraints on TSystem attributes
   * Check values. name, host, accessMetheod must be set. effectiveUserId is restricted.
   * If transfer mechanism S3 is supported then bucketName must be set.
   * Collect and report as many errors as possible so they can all be fixed before next attempt
   * NOTE: JsonSchema validation should handle some of these checks but we check here again just in case
   *
   * @return null if OK or error Response
   */
  private static Response validateTSystem(TSystem system, AuthenticatedUser authenticatedUser, boolean prettyPrint)
  {
    // Make sure owner, effectiveUserId, transferMethods, notes and tags are all set
    TSystem system1 = TSystem.checkAndSetDefaults(system);

    String effectiveUserId = system1.getEffectiveUserId();
    String owner  = system1.getOwner();
    String name = system1.getName();
    String msg;
    var errMessages = new ArrayList<String>();
    if (StringUtils.isBlank(system1.getName()))
    {
      msg = MsgUtils.getMsg("SYSAPI_CREATE_MISSING_ATTR", NAME_FIELD);
      errMessages.add(msg);
    }
    if (system1.getSystemType() == null)
    {
      msg = MsgUtils.getMsg("SYSAPI_CREATE_MISSING_ATTR", SYSTEM_TYPE_FIELD);
      errMessages.add(msg);
    }
    else if (StringUtils.isBlank(system1.getHost()))
    {
      msg = MsgUtils.getMsg("SYSAPI_CREATE_MISSING_ATTR", HOST_FIELD);
      errMessages.add(msg);
    }
    else if (system1.getDefaultAccessMethod() == null)
    {
      msg = MsgUtils.getMsg("SYSAPI_CREATE_MISSING_ATTR", DEFAULT_ACCESS_METHOD_FIELD);
      errMessages.add(msg);
    }
    else if (system1.getDefaultAccessMethod().equals(AccessMethod.CERT) &&
            !effectiveUserId.equals(TSystem.APIUSERID_VAR) &&
            !effectiveUserId.equals(TSystem.OWNER_VAR) &&
            !StringUtils.isBlank(owner) &&
            !effectiveUserId.equals(owner))
    {
      // For CERT access the effectiveUserId cannot be static string other than owner
      msg = ApiUtils.getMsg("SYSAPI_INVALID_EFFECTIVEUSERID_INPUT");
      errMessages.add(msg);
    }
    else if (system1.getTransferMethods().contains(TransferMethod.S3) && StringUtils.isBlank(system.getBucketName()))
    {
      // For S3 support bucketName must be set
      msg = ApiUtils.getMsg("SYSAPI_S3_NOBUCKET_INPUT");
      errMessages.add(msg);
    }
    else if (system1.getAccessCredential() != null && effectiveUserId.equals(TSystem.APIUSERID_VAR))
    {
      // If effectiveUserId is dynamic then providing credentials is disallowed
      msg = ApiUtils.getMsg("SYSAPI_CRED_DISALLOWED_INPUT");
      errMessages.add(msg);
    }

    // If validation failed log error message and return response
    if (!errMessages.isEmpty())
    {
      // Construct message reporting all errors
      String allErrors = getListOfErrors(errMessages, authenticatedUser, name);
      _log.error(allErrors);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(allErrors, prettyPrint)).build();
    }
    return null;
  }

  /**
   * AccessCredential details can contain secrets. Mask any secrets given
   * and return a string containing the final redacted Json.
   * @param rawJson Json from request
   * @return A string with any secrets masked out
   */
  private static String maskCredSecrets(String rawJson)
  {
    if (StringUtils.isBlank(rawJson)) return rawJson;
    // Get the Json object and prepare to extract info from it
    JsonObject topObj = TapisGsonUtils.getGson().fromJson(rawJson, JsonObject.class);
    if (topObj == null || !topObj.has(TSYSTEM_FIELD)) return rawJson;
    var sysObj = topObj.getAsJsonObject(TSYSTEM_FIELD);
    if (!sysObj.has(ACCESS_CREDENTIAL_FIELD)) return rawJson;
    var credObj = sysObj.getAsJsonObject(ACCESS_CREDENTIAL_FIELD);
    maskSecret(credObj, CredentialResource.PASSWORD_FIELD);
    maskSecret(credObj, CredentialResource.PRIVATE_KEY_FIELD);
    maskSecret(credObj, CredentialResource.PUBLIC_KEY_FIELD);
    maskSecret(credObj, CredentialResource.ACCESS_KEY_FIELD);
    maskSecret(credObj, CredentialResource.ACCESS_SECRET_FIELD);
    maskSecret(credObj, CredentialResource.CERTIFICATE_FIELD);
    sysObj.remove(ACCESS_CREDENTIAL_FIELD);
    sysObj.add(ACCESS_CREDENTIAL_FIELD, credObj);
    topObj.remove(TSYSTEM_FIELD);
    topObj.add(TSYSTEM_FIELD, sysObj);
    return topObj.toString();
  }

  /**
   * If the Json object contains a non-blank value for the field then replace the value with the mask value.
   */
  private static void maskSecret(JsonObject credObj, String field)
  {
    if (!StringUtils.isBlank(ApiUtils.getValS(credObj.get(field), "")))
    {
      credObj.remove(field);
      credObj.addProperty(field, SECRETS_MASK);
    }
  }

  /**
   * Construct message containing list of errors
   */
  private static String getListOfErrors(List<String> msgList, AuthenticatedUser authenticatedUser, Object... parms) {
    if (msgList == null || msgList.isEmpty()) return "";
    var sb = new StringBuilder(ApiUtils.getMsgAuth("SYSAPI_CREATE_INVALID_ERRORLIST", authenticatedUser, parms));
    sb.append(System.lineSeparator());
    for (String msg : msgList) { sb.append("  ").append(msg).append(System.lineSeparator()); }
    return sb.toString();
  }
}
