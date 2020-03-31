package edu.utexas.tacc.tapis.systems.api.resources;

import edu.utexas.tacc.tapis.shared.exceptions.TapisJSONException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.schema.JsonValidator;
import edu.utexas.tacc.tapis.shared.schema.JsonValidatorSpec;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadContext;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadLocal;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.sharedapi.responses.RespBasic;
import edu.utexas.tacc.tapis.sharedapi.security.AuthenticatedUser;
import edu.utexas.tacc.tapis.sharedapi.utils.RestUtils;
import edu.utexas.tacc.tapis.sharedapi.utils.TapisRestUtils;
import edu.utexas.tacc.tapis.systems.api.requests.ReqCreateCredential;
import edu.utexas.tacc.tapis.systems.api.responses.RespCredential;
import edu.utexas.tacc.tapis.systems.api.utils.ApiUtils;
import edu.utexas.tacc.tapis.systems.model.Credential;
import edu.utexas.tacc.tapis.systems.model.TSystem.AccessMethod;
import edu.utexas.tacc.tapis.systems.service.SystemsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/*
 * JAX-RS REST resource for Tapis System credentials
 * Contains annotations which generate the OpenAPI specification documents.
 * Annotations map HTTP verb + endpoint to method invocation.
 * Secrets are stored in the Security Kernel
 *
 */
@Path("/credential")
public class CredentialResource
{
  // ************************************************************************
  // *********************** Constants **************************************
  // ************************************************************************
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(CredentialResource.class);

  // Json schema resource files.
  private static final String FILE_CRED_REQUEST = "/edu/utexas/tacc/tapis/systems/api/jsonschema/CredentialCreateRequest.json";

  // Field names used in Json
  public static final String PASSWORD_FIELD = "password";
  public static final String PRIVATE_KEY_FIELD = "privateKey";
  public static final String PUBLIC_KEY_FIELD = "publicKey";
  public static final String ACCESS_KEY_FIELD = "accessKey";
  public static final String ACCESS_SECRET_FIELD = "accessSecret";
  public static final String CERTIFICATE_FIELD = "certificate";

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
  @Context
  private ServletContext _servletContext;
  @Context
  private Request _request;

  // **************** Inject Services using HK2 ****************
  @Inject
  private SystemsService systemsService;

  // ************************************************************************
  // *********************** Public Methods *********************************
  // ************************************************************************

  /**
   * Store or update credential for given system and user.
   * @param prettyPrint - pretty print the output
   * @param payloadStream - request body
   * @return basic response
   */
  @POST
  @Path("/{systemName}/user/{userName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Create or update access credential in the Security Kernel for given system and user",
    description =
        "Create or update access credential in the Security Kernel for given system and user using a request body. " +
        " Requester must be owner of the system.",
    tags = "credentials",
    requestBody =
      @RequestBody(
        description = "A JSON object specifying a credential.",
        required = true,
        content = @Content(schema = @Schema(implementation = ReqCreateCredential.class))
      ),
    responses = {
      @ApiResponse(responseCode = "200", description = "Credential updated.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "400", description = "Input error. Invalid JSON.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "401", description = "Not authorized.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "500", description = "Server error.",
        content = @Content(schema = @Schema(implementation = RespBasic.class)))
    }
  )
  public Response createUserCredential(@PathParam("systemName") String systemName,
                                       @PathParam("userName") String userName,
                                       @QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                       InputStream payloadStream,
                                       @Context SecurityContext securityContext)
  {
    String msg;
    // Trace this request.
    if (_log.isTraceEnabled())
    {
      msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "createUserCredential",
              "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // ------------------------- Retrieve and validate thread context -------------------------
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context
    // Check that we have all we need from the context, tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // ------------------------- Check prerequisites -------------------------
    // Check that the system exists
    resp = ApiUtils.checkSystemExists(systemsService, authenticatedUser, systemName, prettyPrint, "createUserCredential");
    if (resp != null) return resp;

    // ------------------------- Extract and validate payload -------------------------
    // Read the payload into a string.
    String json;
    try { json = IOUtils.toString(payloadStream, StandardCharsets.UTF_8); }
    catch (Exception e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_JSON_ERROR", authenticatedUser, systemName, userName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }
    // Create validator specification and validate the json against the schema
    JsonValidatorSpec spec = new JsonValidatorSpec(json, FILE_CRED_REQUEST);
    try { JsonValidator.validate(spec); }
    catch (TapisJSONException e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_JSON_INVALID",authenticatedUser, systemName, userName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // Populate credential from payload
    Credential credential;
    ReqCreateCredential req = TapisGsonUtils.getGson().fromJson(json, ReqCreateCredential.class);
    credential = req.credential;

    // If one of PKI keys is missing then reject
    resp = ApiUtils.checkSecrets(authenticatedUser, systemName, userName, prettyPrint, AccessMethod.PKI_KEYS.name(), PRIVATE_KEY_FIELD, PUBLIC_KEY_FIELD,
                                 credential.getPrivateKey(), credential.getPublicKey());
    if (resp != null) return resp;
    // If one of Access key or Access secret is missing then reject
    resp = ApiUtils.checkSecrets(authenticatedUser, systemName, userName, prettyPrint, AccessMethod.ACCESS_KEY.name(), ACCESS_KEY_FIELD, ACCESS_SECRET_FIELD,
                                 credential.getAccessKey(), credential.getAccessSecret());
    if (resp != null) return resp;

    // ------------------------- Perform the operation -------------------------
    // Make the service call to create or update the credential
    try
    {
      systemsService.createUserCredential(authenticatedUser, systemName, userName, credential);
    }
    catch (Exception e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_ERROR", authenticatedUser, systemName, userName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    RespBasic resp1 = new RespBasic();
    return Response.status(Status.CREATED)
      .entity(TapisRestUtils.createSuccessResponse(ApiUtils.getMsgAuth("SYSAPI_CRED_UPDATED", authenticatedUser, systemName, userName),
                                                   prettyPrint, resp1))
      .build();
  }

  /**
   * getUserCredential
   * @param accessMethodStr - access method to use instead of default
   * @param prettyPrint - pretty print the output
   * @return Response
   */
  @GET
  @Path("/{systemName}/user/{userName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Retrieve credential for given system and user",
      description = "Retrieve credential for given system and user. Requester must be owner of the system. " +
                    "Use query parameter accessMethod=<method> to override default access method.",
      tags = "credentials",
      responses = {
          @ApiResponse(responseCode = "200", description = "Success.",
            content = @Content(schema = @Schema(implementation = RespCredential.class))),
          @ApiResponse(responseCode = "400", description = "Input error.",
            content = @Content(schema = @Schema(implementation = RespBasic.class))),
          @ApiResponse(responseCode = "404", description = "System not found.",
            content = @Content(schema = @Schema(implementation = RespBasic.class))),
          @ApiResponse(responseCode = "401", description = "Not authorized.",
            content = @Content(schema = @Schema(implementation = RespBasic.class))),
          @ApiResponse(responseCode = "500", description = "Server error.",
            content = @Content(schema = @Schema(implementation = RespBasic.class)))
      }
  )
  public Response getUserCredential(@PathParam("systemName") String systemName,
                                    @PathParam("userName") String userName,
                                    @QueryParam("accessMethod") @DefaultValue("") String accessMethodStr,
                                    @QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                    @Context SecurityContext securityContext)
  {
    String msg;
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context

    // Trace this request.
    if (_log.isTraceEnabled())
    {
      msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "getUserCredential",
                                   "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // Check that we have all we need from the context, the tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // ------------------------- Check prerequisites -------------------------
    // Check that the system exists
    resp = ApiUtils.checkSystemExists(systemsService, authenticatedUser, systemName, prettyPrint, "getUserCredential");
    if (resp != null) return resp;

    // Check that accessMethodStr is valid if it is passed in
    AccessMethod accessMethod = null;
    try { if (!StringUtils.isBlank(accessMethodStr)) accessMethod =  AccessMethod.valueOf(accessMethodStr); }
    catch (IllegalArgumentException e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_ACCMETHOD_ENUM_ERROR", authenticatedUser, systemName, accessMethodStr, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.BAD_REQUEST).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ------------------------- Perform the operation -------------------------
    // Make the service call to get the credentials
    Credential credential;
    try { credential = systemsService.getUserCredential(authenticatedUser, systemName, userName, accessMethod); }
    catch (Exception e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_ERROR", authenticatedUser, systemName, userName, e.getMessage());
      _log.error(msg, e);
      return Response.status(RestUtils.getStatus(e)).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // Resource was not found.
    if (credential == null)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_NOT_FOUND", authenticatedUser, systemName, userName);
      _log.warn(msg);
      return Response.status(Status.NOT_FOUND).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    // Success means we retrieved the information.
    RespCredential resp1 = new RespCredential(credential);
    return Response.status(Status.OK).entity(TapisRestUtils.createSuccessResponse(
            ApiUtils.getMsgAuth("SYSAPI_CRED_FOUND", authenticatedUser, systemName, userName), prettyPrint, resp1)).build();
  }

  /**
   * Remove credential for given system and user.
   * @param prettyPrint - pretty print the output
   * @return basic response
   */
  @DELETE
  @Path("/{systemName}/user/{userName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Remove credential in the Security Kernel for given system and user",
    description =
      "Remove credential from the Security Kernel for given system and user. Requester must be owner of the system.",
    tags = "credentials",
    responses = {
      @ApiResponse(responseCode = "200", description = "Credential removed.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "400", description = "Input error. Invalid JSON.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "401", description = "Not authorized.",
        content = @Content(schema = @Schema(implementation = RespBasic.class))),
      @ApiResponse(responseCode = "500", description = "Server error.",
        content = @Content(schema = @Schema(implementation = RespBasic.class)))
    }
  )
  public Response removeUserCredential(@PathParam("systemName") String systemName,
                                       @PathParam("userName") String userName,
                                       @QueryParam("pretty") @DefaultValue("false") boolean prettyPrint,
                                       @Context SecurityContext securityContext)
  {
    String msg;
    TapisThreadContext threadContext = TapisThreadLocal.tapisThreadContext.get(); // Local thread context

    // Trace this request.
    if (_log.isTraceEnabled())
    {
      msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(), "removeUserCredential",
                            "  " + _request.getRequestURL());
      _log.trace(msg);
    }

    // Check that we have all we need from the context, tenant name and apiUserId
    // Utility method returns null if all OK and appropriate error response if there was a problem.
    Response resp = ApiUtils.checkContext(threadContext, prettyPrint);
    if (resp != null) return resp;

    // Get AuthenticatedUser which contains jwtTenant, jwtUser, oboTenant, oboUser, etc.
    AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

    // ------------------------- Check prerequisites -------------------------
    // Check that the system exists
    resp = ApiUtils.checkSystemExists(systemsService, authenticatedUser, systemName, prettyPrint, "removeUserCredential");
    if (resp != null) return resp;

    // ------------------------- Perform the operation -------------------------
    // Make the service call to remove the credential
    try
    {
      systemsService.deleteUserCredential(authenticatedUser, systemName, userName);
    }
    catch (Exception e)
    {
      msg = ApiUtils.getMsgAuth("SYSAPI_CRED_ERROR", authenticatedUser, systemName, userName, e.getMessage());
      _log.error(msg, e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(TapisRestUtils.createErrorResponse(msg, prettyPrint)).build();
    }

    // ---------------------------- Success -------------------------------
    RespBasic resp1 = new RespBasic();
    return Response.status(Status.CREATED)
      .entity(TapisRestUtils.createSuccessResponse(ApiUtils.getMsgAuth("SYSAPI_CRED_DELETED", authenticatedUser, systemName,
                                                                       userName), prettyPrint, resp1))
      .build();
  }

  // ************************************************************************
  // *********************** Private Methods ********************************
  // ************************************************************************

}
