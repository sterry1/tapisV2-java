package edu.utexas.tacc.tapis.systems.service;

import com.google.inject.Singleton;
import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.security.client.gen.model.ResultNameArray;
import edu.utexas.tacc.tapis.security.client.gen.model.SkRole;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.systems.config.RuntimeParameters;
import edu.utexas.tacc.tapis.systems.dao.SystemsDao;
import edu.utexas.tacc.tapis.systems.dao.SystemsDaoImpl;
import edu.utexas.tacc.tapis.systems.model.TSystem;
import edu.utexas.tacc.tapis.tenants.client.gen.model.Tenant;
import edu.utexas.tacc.tapis.tokens.client.TokensClient;
import edu.utexas.tacc.tapis.tenants.client.TenantsClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static edu.utexas.tacc.tapis.shared.TapisConstants.SERVICE_NAME_SYSTEMS;
import static edu.utexas.tacc.tapis.systems.model.TSystem.APIUSERID_VAR;
import static edu.utexas.tacc.tapis.systems.model.TSystem.OWNER_VAR;
import static edu.utexas.tacc.tapis.systems.model.TSystem.TENANT_VAR;

/*
 * Service level methods for Systems.
 *   Uses Dao layer and other service library classes to perform all
 *   top level service operations.
 */
@Singleton
public class SystemsServiceImpl implements SystemsService
{
  /* ********************************************************************** */
  /*                               Constants                                */
  /* ********************************************************************** */
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(SystemsServiceImpl.class);

  private static final String SYSTEM_OWNER_ROLE = "SystemOwner";
  private static final String[] ALL_VARS = {APIUSERID_VAR, OWNER_VAR, TENANT_VAR};

  // **************** Inject Dao singletons ****************
  @com.google.inject.Inject
  private SystemsDao dao;

  /* ********************************************************************** */
  /*                             Public Methods                             */
  /* ********************************************************************** */
  /**
   * Create a new system object
   *
   * @return Sequence id of object created
   * @throws TapisException
   * @throws IllegalStateException - if system already exists
   */
  @Override
  public int createSystem(String tenantName, String apiUserId, String name, String description, String owner, String host,
                          boolean available, String bucketName, String rootDir, String jobInputDir,
                          String jobOutputDir, String workDir, String scratchDir, String effectiveUserId, String tags,
                          String notes, String accessCredential, String accessMechanism, String transferMechanisms,
                          int protocolPort, boolean protocolUseProxy, String protocolProxyHost, int protocolProxyPort,
                          String rawRequest)
          throws TapisException, IllegalStateException
  {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();

    // Resolve owner if necessary. If empty or "${apiUserId}" then fill in with apiUserId
    if (StringUtils.isBlank(owner) || owner.equalsIgnoreCase(APIUSERID_VAR)) owner = apiUserId;

    // TODO/TBD do this check here? it is already being done in systemsapi front-end. If we are going to support
    //   other front-ends over which we have less control then a lot more checking needs to be done here as well.
    // Check for valid effectiveUserId
    // For SSH_CERT access the effectiveUserId cannot be static string other than owner
//    if (accessMechanism != null && accessMechanism.equals(Protocol.AccessMechanism.SSH_CERT) &&
//        !effectiveUserId.equals(owner) &&
//        !effectiveUserId.equals(APIUSERID_VAR) &&
//        !effectiveUserId.equals(OWNER_VAR))
//    {
//
//    }

      // Perform variable substitutions that happen at create time: bucketName, rootDir, jobInputDir, jobOutputDir, workDir, scratchDir
    // NOTE: effectiveUserId is not processed. Var reference is retained and substitution done as needed when system is retrieved.
    //    ALL_VARS = {APIUSERID_VAR, OWNER_VAR, TENANT_VAR};
    String[] allVarSubstitutions = {apiUserId, owner, tenantName};
    bucketName = StringUtils.replaceEach(bucketName, ALL_VARS, allVarSubstitutions);
    rootDir = StringUtils.replaceEach(rootDir, ALL_VARS, allVarSubstitutions);
    jobInputDir = StringUtils.replaceEach(jobInputDir, ALL_VARS, allVarSubstitutions);
    jobOutputDir = StringUtils.replaceEach(jobOutputDir, ALL_VARS, allVarSubstitutions);
    workDir = StringUtils.replaceEach(workDir, ALL_VARS, allVarSubstitutions);
    scratchDir = StringUtils.replaceEach(scratchDir, ALL_VARS, allVarSubstitutions);

    int itemId = dao.createTSystem(tenantName, name, description, owner, host, available, bucketName, rootDir,
                                   jobInputDir, jobOutputDir, workDir, scratchDir, effectiveUserId, tags, notes,
                                   accessMechanism, transferMechanisms, protocolPort, protocolUseProxy,
                                   protocolProxyHost, protocolProxyPort, rawRequest);

    // TODO: Remove debug System.out statements

    // TODO/TBD: Creation of system and role/perms not in single transaction. Need to handle failure of role/perms operations

    // TODO Store credentials in Security Kernel

    // Use Tenants service to lookup information we need to:
    //  Access the tokens service associated with the tenant.
    //  Access the security kernel service associated with the tenant.
    // NOTE: The front-end is responsible for validating the JWT using the public key for the tenant.
    //       edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.JWTValidateRequestFilter

    // Tenants and tokens services base URLs from the environment have precedence.
    // NOTE: Tenants URL is a required parameter, so no need to check here
    RuntimeParameters parms = RuntimeParameters.getInstance();

//    String tenantsURL = "https://dev.develop.tapis.io";
    String tenantsURL = parms.getTenantsSvcURL();
    var tenantsClient = new TenantsClient(tenantsURL);
    Tenant tenant1 = null;
    // TODO proper exception handling
    try {tenant1 = tenantsClient.getTenant(tenantName);}
    catch (Exception e) {throw new TapisException("Exception from Tenants service", e);}
    if (tenant1 == null) throw new TapisException("ERROR: Unable to create client for Tenants service.");

    // Tokens service URL must come from env or the tenants service
//    String tokensURL = "https://dev.develop.tapis.io";
    String tokensURL = parms.getTokensSvcURL();
    if (StringUtils.isBlank(tokensURL)) tokensURL = tenant1.getTokenService();
    if (StringUtils.isBlank(tokensURL)) throw new TapisException("ERROR: Unable to determine Tokens service base URL");

    // Get short term service JWT from tokens service
    var tokClient = new TokensClient(tokensURL);
    String svcJWT = null;
    // TODO proper exception handling
    try {svcJWT = tokClient.getSvcToken(tenantName, SERVICE_NAME_SYSTEMS);}
    catch (Exception e) {throw new TapisException("Exception from Tokens service", e);}
    System.out.println("Got svcJWT: " + svcJWT);
    _log.error("Got svcJWT: " + svcJWT);
    // Basic check of JWT
    if (StringUtils.isBlank(svcJWT)) throw new TapisException("Token service returned invalid JWT");

    // TODO/TBD: tenants java client can return tenant object with more info, use it here?

    // Get Security Kernel URL from the env or the tenants service
    // Env value has precedence
//    String skURL = "https://dev.develop.tapis.io/v3";
    String skURL = parms.getSkSvcURL();
    if (StringUtils.isBlank(skURL)) skURL = tenant1.getSecurityKernel();
    if (StringUtils.isBlank(skURL)) throw new TapisException("ERROR: Unable to determine Security Kernel service URL");
    // TODO remove strip-off of everything after /v3 once tenant is updated or we do something different for base URL in auto-generated clients
    // Strip off everything after the /v3 so we have a valid SK base URL
    skURL = skURL.substring(0, skURL.indexOf("/v3") + 3);

    var skClient = new SKClient(skURL, svcJWT);
    // TODO/TBD: Build perm specs here? review details
    String sysPerm = "system:" + tenantName + ":*:" + name;
    String storePerm = "store:" + tenantName + ":*:" + name + ":*";

    // Create Role with perms and grant it to user
    // TODO/TBD: name of system owner role, one for each "tenant+system"?
    String roleName = SYSTEM_OWNER_ROLE + "_" + name;
    try
    {
      skClient.createRole(roleName, "System owner role");
      skClient.addRolePermission(roleName, sysPerm);
      skClient.addRolePermission(roleName, storePerm);
      skClient.grantUserRole(owner, roleName);
    }
    catch (Exception e) { _log.error(e.toString()); throw e;}

    // TODO remove tests
    // Test by retrieving role and permissions from SK
    SkRole skRole = null;
    try { skRole = skClient.getRoleByName(roleName); }
    catch (Exception e) { _log.error(e.toString()); throw e;}
    _log.error("Created and then found SKRole with name: " + skRole.getName() + " Id: " + skRole.getId());
    System.out.println("Created and then found SKRole with name: " + skRole.getName() + " Id: " + skRole.getId());
    // Test retrieving users with the role
    ResultNameArray nameArray = skClient.getUsersWithRole(roleName);
    List<String> names = nameArray.getNames();
    if (names != null && names.contains(owner))
    {
      _log.error("User " + owner + " does have role " + skRole.getName());
      System.out.println("User " + owner + " does have role " + skRole.getName());
    } else {
      _log.error("User " + owner + " does NOT have role " + skRole.getName());
      System.out.println("User " + owner + " does NOT have role " + skRole.getName());
    }
    // Test retrieving all perms for a user
    ResultNameArray permArray = skClient.getUserPerms(owner);
    List<String> perms = permArray.getNames();
    _log.error("User " + owner + " has the following permissions: ");
    System.out.println("User " + owner + " has the following permissions: ");
    for (String perm : perms) {
      _log.error("  perm: " + perm);
      System.out.println("  perm: " + perm);
    }
    return itemId;
  }

  /**
   * Delete a system record given the system name.
   *
   */
  @Override
  public int deleteSystemByName(String tenant, String name) throws TapisException
  {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();
    return dao.deleteTSystem(tenant, name);
  }

  /**
   * getSystemByName
   * @param name
   * @return
   * @throws TapisException
   */
  @Override
  public boolean checkForSystemByName(String tenant, String name) throws TapisException {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();
    boolean result = dao.checkForTSystemByName(tenant, name);
    return result;
  }

  /**
   * getSystemByName
   * @param name
   * @return
   * @throws TapisException
   */
  @Override
  public TSystem getSystemByName(String tenant, String name, String apiUserId, boolean getCreds) throws TapisException {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();
    TSystem result = dao.getTSystemByName(tenant, name);
    if (result == null) return result;
    // Resolve effectiveUserId if necessary
    result.setEffectiveUserId(resolveEffectiveUserId(result.getEffectiveUserId(), result.getOwner(), apiUserId));
    // TODO If requested retrieve credentials from Security Kernel
    //result.setAccessCredential();
    return result;
  }

  /**
   * Get all systems
   * @param tenant
   * @return
   * @throws TapisException
   */
  @Override
  public List<TSystem> getSystems(String tenant, String apiUserId) throws TapisException
  {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();
    List<TSystem> result = dao.getTSystems(tenant);
    for (TSystem sys : result)
    {
      sys.setEffectiveUserId(resolveEffectiveUserId(sys.getEffectiveUserId(), sys.getOwner(), apiUserId));
      // TODO If requested retrieve credentials from Security Kernel
      //sys.setAccessCredential();
    }
    return result;
  }

  /**
   * Get list of system names
   * @param tenant
   * @return
   * @throws TapisException
   */
  @Override
  public List<String> getSystemNames(String tenant) throws TapisException
  {
    // TODO Use static factory methods for DAOs, or better yet use DI, maybe Guice
    var dao = new SystemsDaoImpl();
    return dao.getTSystemNames(tenant);
  }

  /* ********************************************************************** */
  /*                             Private Methods                            */
  /* ********************************************************************** */

  /**
   * If effectiveUserId is dynamic then resolve it
   * @param userId
   * @return
   */
  private String resolveEffectiveUserId(String userId, String owner, String apiUserId)
  {
    if (StringUtils.isBlank(userId)) return userId;
    else if (userId.equals(OWNER_VAR) && !StringUtils.isBlank(owner)) return owner;
    else if (userId.equals(APIUSERID_VAR) && !StringUtils.isBlank(apiUserId)) return apiUserId;
    else return userId;
  }
}
