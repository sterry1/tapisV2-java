package edu.utexas.tacc.tapis.meta.api.resources;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import edu.utexas.tacc.aloe.shared.threadlocal.AloeThreadContext;
import edu.utexas.tacc.aloe.shared.threadlocal.AloeThreadLocal;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;


@Path("/")
public class ResourceBucket extends AbstractResource {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(ResourceBucket.class);
  
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
  
  /*-------------------------------------------------------
   *   Root endpoints
   * ------------------------------------------------------*/
  //----------------  List DBs in server ----------------
  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response listDBNames() {
    
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listDBs", _request.getRequestURL());
      _log.trace(msg);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  
  /*-------------------------------------------------------
   *    Database (DB) endpoints
   * ------------------------------------------------------*/
  
  //----------------  List Collections in DB ----------------
  @GET
  @Path("/{db}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response listCollectionNames(@PathParam("db") String db) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listCollections", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("List collections in " + db);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  // ----------------  Get DB metadata ----------------
  @GET
  @Path("/{db}/_meta")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response getDBMetadata(@PathParam("db") String db) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "getDBMetadata", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Get the Metadata for " + db);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  // ----------------  Create DB ----------------
  @PUT
  @Path("/{db}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response createDB(@PathParam("db") String db) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "createDB", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("create database " + db);
    }
    
    // Proxy the PUT request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPutRequest("{}");
    
    //---------------------------- Response -------------------------------
    // return core server response
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  // ----------------  Delete DB ----------------
  @DELETE
  @Path("/{db}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response deleteDB() {
    return javax.ws.rs.core.Response.status(200).entity("{ TODO }").build();
  }
  
  /*------------------------------------------------------------------------
   *                              Collection endpoints
   * -----------------------------------------------------------------------*/
  
  //----------------  Create a Collection ----------------
  @PUT
  @Path("/{db}/{collection}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response createCollection(@PathParam("db") String db,
                                                    @PathParam("collection") String collection,
                                                    InputStream payload) {
    
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "createCollection", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("create collection " + collection + " in " + db);
    }
    
    // TODO
    // Get the json payload to proxy to back end;
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // if payload is null we could end up here
      _log.debug("Error Parsing: - ");
    }
    String jsonPayload = jsonPayloadToProxy.toString();
    _log.debug("Data Received: " + jsonPayload);
    
    if (StringUtils.isEmpty(jsonPayload)) {
      jsonPayload = "{}";
    }
    
    // Proxy the PUT request and handle any exceptions
    // this request assumes a new collection. running this request on an existing collection
    //  will remove any RH properties for the collection including any aggregations.
    // Essentially signaling you intend to start over.
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPutRequest(jsonPayload);
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  //----------------  List documents in Collection ----------------
  @GET
  @Path("/{db}/{collection}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response listDocuments(@PathParam("db") String db,
                                                 @PathParam("collection") String collection,
                                                 @QueryParam("filter") String filter) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listDocuments", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("List documents in " + db + "/" + collection);
    }
    
    StringBuilder pathUrl = new StringBuilder(_request.getRequestURI());
    
    if (!StringUtils.isEmpty(_request.getQueryString())) {
      pathUrl.append("?").append(_request.getQueryString());
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(pathUrl.toString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  //----------------  large query submission ----------------
  // this endpoint takes a valid mongodb query document and submits it to
  // the core server to bypass the max request header limit
  @POST
  @Path("/{db}/{collection}/_filter")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response submitLargeQuery(@PathParam("db") String db,
                                                    @PathParam("collection") String collection,
                                                    @QueryParam("page") String page,
                                                    @QueryParam("pagesize") String pagesize,
                                                    InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "submitLargeQuery", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Submit a query larger than the url query param limitation for HTTP to " + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // TODO finish out
      _log.error("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // we change here from a POST request to a GET request.
    // RH core will except URL GET filter request without the URL limitation.
    // This keeps behaviour consistent with GET requests to core server.
    String inComingRequest = _request.getRequestURI();
    inComingRequest = inComingRequest.replace("_filter", "?filter=");
    
    // add query parameters that were a part of the request
    StringBuilder newUriPath = new StringBuilder();
    newUriPath.append(inComingRequest)
              .append(jsonPayloadToProxy.toString()).append("&" + _request.getQueryString());
    
    CoreRequest coreRequest = new CoreRequest(newUriPath.toString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    String result;
    result = coreResponse.getCoreResponsebody();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(
        result);
    
    return responseBuilder.build();
    
  }
  
  // ----------------  Get the number of documents in Collection ----------------
  @GET
  @Path("/{db}/{collection}/_size")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response getCollectionSize(@PathParam("db") String db,
                                                     @PathParam("collection") String collection,
                                                     @QueryParam("filter") String filter) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listDocuments", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("List documents in " + db + "/" + collection);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI() + "?" + _request.getQueryString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * getCollectionMetadata
   * -----------------------------------------------------------------------*/
  @GET
  @Path("/{db}/{collection}/_meta")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response getCollectionMetadata(@PathParam("db") String db,
                                                         @PathParam("collection") String collection) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listDocuments", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("List documents in " + db + "/" + collection);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI() + "?" + _request.getQueryString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * Create document
   * -----------------------------------------------------------------------*/
  // this endpoint also returns the oid of a newly created document as an ETag header
  @POST
  @Path("/{db}/{collection}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response createDocument(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  @QueryParam("basic") boolean basic,
                                                  InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "createDocument", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Create document  in " + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // TODO finish out
      _log.error("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // Proxy the POST request and handle any exceptions
    // we will always return a response for a request that means something
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPostRequest(jsonPayloadToProxy.toString());
    
    String result = null;
    // we grab the Etag out of the headers
    String etag = coreResponse.getEtag();
    // we snag the location of the document resource that was collected
    String location = coreResponse.getLocationFromHeaders();
    
    // if the basic flag is thrown let's get the location header result
    if (basic) {
      result = coreResponse.getBasicResponse(location);
    } else {
      result = coreResponse.getCoreResponsebody();
    }
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(
        result);
    // add the Etag to response headers
    if (!StringUtils.isBlank(etag)) {
      responseBuilder.tag(etag);
    }
    // add the new resource location to response headers
    if (!StringUtils.isBlank(location)) {
      responseBuilder.header("location", location);
    }
    
    Response response = responseBuilder.build();
    return response;
  }
  
  //----------------  Delete a collection  ----------------
  @DELETE
  @Path("/{db}/{collection}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response deleteCollection(@PathParam("db") String db,
                                                    @PathParam("collection") String collection) {
    
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "deleteCollection", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Delete collection " + collection + " in " + db);
    }
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyDeleteRequest(_httpHeaders);
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   *                              Index endpoints
   * -----------------------------------------------------------------------*/
  /*------------------------------------------------------------------------
   * List Indexes
   * -----------------------------------------------------------------------*/
  @GET
  @Path("/{db}/{collection}/_indexes")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response listIndexes(@PathParam("db") String db,
                                               @PathParam("collection") String collection) {
    
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "listIndexes", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("List indexes in " + db + "/" + collection);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI() + "?" + _request.getQueryString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  //----------------  Create an Index ----------------
  @PUT
  @Path("/{db}/{collection}/_indexes/{indexName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response createIndex(@PathParam("db") String db,
                                               @PathParam("collection") String collection,
                                               @PathParam("indexName") String indexName,
                                               InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "createIndex", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Create index " + indexName + " in " + db + "/" + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // TODO finish out
      _log.error("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPutRequest(jsonPayloadToProxy.toString());
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  // ----------------  Delete an Index ----------------
  @DELETE
  @Path("/{db}/{collection}/_indexes/{indexName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response deleteIndex(@PathParam("db") String db,
                                               @PathParam("collection") String collection,
                                               @PathParam("indexName") String indexName) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "deleteIndex", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Delete index " + indexName + " in " + db + "/" + collection);
    }
    
    // Proxy the DELETE request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyDeleteRequest(_httpHeaders);
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*************************************************
   *    Document endpoints
   *************************************************/
  
  //----------------  Get a specific Document ----------------
  @GET
  @Path("/{db}/{collection}/{documentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response getDocument(@PathParam("db") String db,
                                               @PathParam("collection") String collection,
                                               @PathParam("documentId") String documentId) {
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  // ----------------  Put a document ----------------
  @PUT
  @Path("/{db}/{collection}/{documentId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response replaceDocument(@PathParam("db") String db,
                                                   @PathParam("collection") String collection,
                                                   @PathParam("documentId") String documentId,
                                                   InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "putDocument", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Put a document in " + db + "/" + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // TODO finish out
      _log.error("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPutRequest(jsonPayloadToProxy.toString());
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * Modify a document
   * -----------------------------------------------------------------------*/
  
  /**
   * Takes a partial document and modifies the fields of the target document
   * with new values
   *
   * @param db
   * @param collection
   * @param documentId
   * @param payload
   * @return
   */
  @PATCH
  @Path("/{db}/{collection}/{documentId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response modifyDocument(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  @PathParam("documentId") String documentId,
                                                  InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "patchDocument", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Patch document in " + db + "/" + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      // TODO finish out
      _log.error("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyPatchRequest(jsonPayloadToProxy.toString());
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * Delete a specific Document
   * -----------------------------------------------------------------------*/
  
  /**
   * Deletes a specified document from the collection
   *
   * @param db
   * @param collection
   * @param documentId
   * @return
   */
  @DELETE
  @Path("/{db}/{collection}/{documentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response deleteDocument(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  @PathParam("documentId") String documentId) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "deleteDocument", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Delete document " + documentId + " in " + db + "/" + collection);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyDeleteRequest(_httpHeaders);
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(
        coreResponse.getCoreResponsebody());
    Response response = responseBuilder.build();
    return response;
  }
  
  /*------------------------------------------------------------------------
   *                              Aggregation endpoints
   * -----------------------------------------------------------------------*/
  /*------------------------------------------------------------------------
   * addAggregation
   * -----------------------------------------------------------------------*/
  @PUT
  @Path("/{db}/{collection}/_aggrs")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response addAggregation(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "addAggregation", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Add an aggregation in " + db + "/" + collection);
    }
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line = null;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      _log.debug("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    // uri /meta/v3/v1airr/rearrangement/_aggrs
    // this endpoint takes the _aggrs designation. we do this because we don't
    //  want to overload the PUT /db/collection endpoint between properties added to metadata in a collection
    //  and adding an aggregation.
    // this is cleaner but requires us to strip away the _aggrs designation in order to forward the request
    // to the core server.
    
    String _pathUri = _request.getRequestURI();
    String pathUri = _pathUri.replace("/_aggrs", "");
    
    // Proxy the PUT request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(pathUri);
    CoreResponse coreResponse = coreRequest.proxyPutRequest(jsonPayloadToProxy.toString());
    
    //---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * useAggregation
   * -----------------------------------------------------------------------*/
  @GET
  @Path("/{db}/{collection}/_aggrs/{aggregation}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response useAggregation(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  @PathParam("aggregation") String aggregation,
                                                  @QueryParam("page") String page,
                                                  @QueryParam("pagesize") String pagesize,
                                                  @QueryParam("avars") String avars) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "getAggregation", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Get aggregation " + aggregation + " in " + db + "/" + collection);
      _log.trace("avars: " + avars + "page: " + page + "pagesize: " + pagesize);
    }
    
    // Proxy the GET request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI() + "?" + _request.getQueryString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*------------------------------------------------------------------------
   * submitLargeAggregation
   * -----------------------------------------------------------------------*/
  @POST
  @Path("/{db}/{collection}/_aggrs/{aggregation}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response submitLargeAggregation(@PathParam("db") String db,
                                                          @PathParam("collection") String collection,
                                                          @PathParam("aggregation") String aggregation,
                                                          @QueryParam("page") String page,
                                                          @QueryParam("pagesize") String pagesize,
                                                          InputStream payload) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "addAggregation", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Add an aggregation in " + db + "/" + collection);
    }
    // we want to capture the oversize avars parameter in a request body
    // to use as a submission to RH core server in a GET.
    // This does not submit a new aggregation pipeline only the avars parameters
    // for an existing aggregation
    
    // Get the json payload to proxy to back end
    StringBuilder jsonPayloadToProxy = new StringBuilder();
    // we will assign the payload to an avars Query parameter to RH core server.
    jsonPayloadToProxy.append("?avars=");
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(payload));
      String line;
      while ((line = in.readLine()) != null) {
        jsonPayloadToProxy.append(line);
      }
    } catch (Exception e) {
      _log.debug("Error Parsing: - ");
    }
    
    _log.debug("Data Received: " + jsonPayloadToProxy.toString());
    
    String inComingRequest = _request.getRequestURI();
    StringBuilder newUriPath = new StringBuilder(); //  /meta/v3/v1airr/rearrangement/_aggrs/facets
    newUriPath.append(inComingRequest)
              .append(jsonPayloadToProxy.toString()).append("&" + _request.getQueryString());
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(newUriPath.toString());
    CoreResponse coreResponse = coreRequest.proxyGetRequest();
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  //----------------  delete an aggregation ----------------
  @DELETE
  @Path("/{db}/{collection}/_aggr/{aggregation}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response deleteAggregation(@PathParam("db") String db,
                                                     @PathParam("collection") String collection,
                                                     @PathParam("aggregation") String aggregation,
                                                     @QueryParam("avars") String agvars) {
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "deleteAggregation", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Delete aggregation in " + db + "/" + collection);
    }
    
    // Proxy the POST request and handle any exceptions
    CoreRequest coreRequest = new CoreRequest(_request.getRequestURI());
    CoreResponse coreResponse = coreRequest.proxyDeleteRequest(_httpHeaders);
    
    // ---------------------------- Response -------------------------------
    // just return whatever core server sends to us
    return javax.ws.rs.core.Response.status(coreResponse.getStatusCode()).entity(coreResponse.getCoreResponsebody()).build();
  }
  
  /*-------------------------------------------------------
   * Long Running Queries
   * ------------------------------------------------------*/
  
  /*-------------------------------------------------------
   * Long Running Query status check
   * ------------------------------------------------------*/
  
  
  //  TODO ----------------   Get an lrq submission status ----------------
  @GET
  @Path("/{db}/{collection}/_lrq/{lrqId}")
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response checkLRQstatus(@PathParam("db") String db,
                                                  @PathParam("collection") String collection,
                                                  @PathParam("lrqId") String lrqId) {
  
    LRQSubmissionDAO dao = new LRQSubmissionDAOImpl(collection);
    String status = dao.getSubmissionStatus(lrqId);
    
    return javax.ws.rs.core.Response.status(200).entity(status).build();
  }
  
  //  TODO ----------------   Post long running queries submission ----------------
  @POST
  @Path("/{db}/{collection}/_lrq")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public javax.ws.rs.core.Response submitLongRunningQuery(@PathParam("db") String db,
                                                          @PathParam("collection") String collection,
                                                          InputStream payload) {
    
    AloeThreadContext threadContext = AloeThreadLocal.aloeThreadContext.get();
    
    // Trace this request.
    if (_log.isTraceEnabled()) {
      String msg = MsgUtils.getMsg("TAPIS_TRACE_REQUEST", getClass().getSimpleName(),
          "submitLongRunningQuery", _request.getRequestURL());
      _log.trace(msg);
      _log.trace("Submit a long running query to the queue " + db + "/" + collection);
    }
    
    //  TODO ----------------   process the json payload ----------------
    // *****   validSubmission validation    *****
    // check the payload for empty

    JsonObject jsonObject = getValidJson(payload);
  
//    _log.debug("checking the validSubmission payload ...");
//    if (!checkPayload(payload)) {
//      return javax.ws.rs.core.Response.status(500).entity("{ 'msg' : 'ERROR empty request payload' }").build();
//    }
    
    if (jsonObject == null) {
      return javax.ws.rs.core.Response.status(500).entity("{ 'msg' : 'ERROR payload is not syntactic JSON' }").build();
    }
    
    // payload not empty let's assume schema adherence.
    ValidateSubmissionJson validSubmission = new ValidateSubmissionJson(jsonObject);
    // method to check validity of validSubmission for db persistenance
    submissionLRQ(validSubmission);
    
    // do we have a vaild validSubmission json
    if (!validSubmission.isValid()) {
      // Return an Error Response
      return javax.ws.rs.core.Response.status(500).entity("{  'msg' : 'ERROR validSubmission not valid' }").build();
    }
    
    //  TODO ----------------   submit to book keeping ----------------
    // Use the DTO from validSubmission validation to
    // create and populate the DAO to create a persistent record of validSubmission
    // give it a unique id
    // we take a dto and create a dao for storage in DB
    _log.debug("create a dao for storage ... ");
    _log.debug("write the validSubmission to the database ...");
    // need the collection that will store the validSubmission
    // pull the tenant id from context that will map to the collection to use.
    
    LRQSubmission lrqSubmission = validSubmission.getLRQSubmission();
    LRQSubmissionDAO lrqDao = new LRQSubmissionDAOImpl(threadContext.getTenantId());
    // the request context carries the names of the target db and collection the query executes against.
    ObjectId objectId = lrqDao.createSubmission(lrqSubmission,db ,collection );
    
    // result will be null if creation of document in the collection was unsuccessful
    if (objectId == null) {
      // Return an Error Response
      return javax.ws.rs.core.Response.status(500).entity("{ 'msg': 'ERROR unable to persist validSubmission to data store' }").build();
    }
    //  TODO ----------------   package for message queue validSubmission ----------------
    // create a message and submit to msg client
    // using rabbitMQ for task queue
    // TODO add db and collection context to submission.
    LRQSubmission validSubmissionLRQSubmission = validSubmission.getLRQSubmission();
    validSubmissionLRQSubmission.set_id(objectId.toString());
    validSubmissionLRQSubmission.setQueryDb(db);
    validSubmissionLRQSubmission.setQueryCollection(collection);
    
    boolean result = sendSubmissionToQueue(validSubmissionLRQSubmission);
    
    if (!result) {
      // Return an Error Response
      return javax.ws.rs.core.Response.status(500).entity("{ 'msg': 'ERROR sending the validSubmission to task queue'  }").build();
    }
    
    //  TODO ----------------   respond to user ----------------
    // an LRQ was submitted
    // if success
    //   response will include link to status record
    // else
    //   response will indicate the error that occurred.
    
    return javax.ws.rs.core.Response.status(201).entity("{ \"msg\": \"SUCCESS created document\",  \"_id\": \""+objectId.toString()+"\" }").build();
  }
  
  private void submissionLRQ(ValidateSubmissionJson submission) {
    // validate the submission by schema
    _log.trace("validating the submission json by schema");
  }
  
  private boolean sendSubmissionToQueue(LRQSubmission dto) {
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    _log.debug("Queue Host : "+runtime.getTaskQueueHost());
    _log.debug("TaskQueue name : "+runtime.getTaskQueueName());
    // TODO check what kind of exceptions can happen here
    String message = dto.toJson();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(runtime.getTaskQueueHost());
    factory.setPort(Integer.parseInt(runtime.getTaskQueuePort()));
    factory.setUsername(runtime.getTaskQueueUser());
    factory.setPassword(runtime.getTaskQueuePassword());
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      // set durable to true may have to remove queue
      channel.queueDeclare(runtime.getTaskQueueName(), true, false, false, null);
      channel.basicPublish("", runtime.getTaskQueueName(),
          MessageProperties.PERSISTENT_TEXT_PLAIN,
          message.getBytes("UTF-8"));
      _log.debug(" [x] Sent '" + message + "'");
    } catch (TimeoutException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
}

