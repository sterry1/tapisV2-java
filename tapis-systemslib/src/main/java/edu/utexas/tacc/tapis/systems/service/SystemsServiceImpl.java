package edu.utexas.tacc.tapis.systems.service;

import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecret;
import edu.utexas.tacc.tapis.security.client.model.KeyType;
import edu.utexas.tacc.tapis.security.client.model.SKSecretMetaParms;
import edu.utexas.tacc.tapis.security.client.model.SKSecretReadParms;
import edu.utexas.tacc.tapis.security.client.model.SKSecretWriteParms;
import edu.utexas.tacc.tapis.security.client.model.SecretType;
import edu.utexas.tacc.tapis.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.threadlocal.TapisThreadContext;
import edu.utexas.tacc.tapis.sharedapi.security.AuthenticatedUser;
import edu.utexas.tacc.tapis.sharedapi.security.ServiceJWT;
import edu.utexas.tacc.tapis.sharedapi.security.TenantManager;
import edu.utexas.tacc.tapis.systems.config.RuntimeParameters;
import edu.utexas.tacc.tapis.systems.dao.SystemsDao;
import edu.utexas.tacc.tapis.systems.model.Credential;
import edu.utexas.tacc.tapis.systems.model.TSystem;
import edu.utexas.tacc.tapis.systems.model.TSystem.AccessMethod;
import edu.utexas.tacc.tapis.systems.model.TSystem.TransferMethod;
import edu.utexas.tacc.tapis.systems.utils.LibUtils;
import edu.utexas.tacc.tapis.tenants.client.gen.model.Tenant;

import org.apache.commons.lang3.StringUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.utexas.tacc.tapis.systems.model.Credential.*;
import static edu.utexas.tacc.tapis.systems.model.TSystem.APIUSERID_VAR;
import static edu.utexas.tacc.tapis.systems.model.TSystem.OWNER_VAR;
import static edu.utexas.tacc.tapis.systems.model.TSystem.TENANT_VAR;

/*
 * Service level methods for Systems.
 *   Uses Dao layer and other service library classes to perform all top level service operations.
 * Annotate as an hk2 Service so that default scope for DI is singleton
 */
@Service
public class SystemsServiceImpl implements SystemsService
{
  // ************************************************************************
  // *********************** Constants **************************************
  // ************************************************************************
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(SystemsServiceImpl.class);

  private static final String[] ALL_VARS = {APIUSERID_VAR, OWNER_VAR, TENANT_VAR};
  private static final List<String> ALL_PERMS = new ArrayList<>(List.of("*"));
  private static final String PERM_SPEC_PREFIX = "system:";

  private static final String SYSTEMS_ADMIN_ROLE = "SystemsAdmin";
  private static final String HDR_TAPIS_TOKEN = "X-Tapis-Token";
  private static final String HDR_TAPIS_TENANT = "X-Tapis-Tenant";
  private static final String HDR_TAPIS_USER = "X-Tapis-User";

  // ************************************************************************
  // *********************** Fields *****************************************
  // ************************************************************************

  // Use HK2 to inject singletons
  @Inject
  private SystemsDao dao;

  @Inject
  private SKClient skClient;

  @Inject
  private ServiceJWT serviceJWT;

  // ************************************************************************
  // *********************** Public Methods *********************************
  // ************************************************************************

  // -----------------------------------------------------------------------
  // ------------------------- Systems -------------------------------------
  // -----------------------------------------------------------------------

  /**
   * Create a new system object
   *
   * @return Sequence id of object created
   * @throws TapisException - for Tapis related exceptions
   * @throws IllegalStateException - system exists OR TSystem in invalid state OR unauthorized
   * @throws IllegalArgumentException - invalid parameter passed in
   */
  @Override
  public int createSystem(AuthenticatedUser authenticatedUser, TSystem system, String scrubbedJson)
          throws TapisException, IllegalStateException, IllegalArgumentException
  {
    if (authenticatedUser == null || system == null) throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();
    String systemName = system.getName();
    String effectiveUserId = system.getEffectiveUserId();

    // ---------------------------- Check inputs ------------------------------------
    // Required system attributes: name, type, host, defaultAccessMethod
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(apiUserId) || StringUtils.isBlank(systemName) ||
        system.getSystemType() == null || StringUtils.isBlank(system.getHost()) ||
        system.getDefaultAccessMethod() == null || StringUtils.isBlank(apiUserId) || StringUtils.isBlank(scrubbedJson))
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_CREATE_ARG_ERROR", tenantName, apiUserId, systemName));
    }

    String opName = "createSystem";
    // ------------------------- Check service level authorization -------------------------
    checkAuth(authenticatedUser, opName, system.getName(), system);

    // ---------------- Fill in defaults and check constraints on TSystem attributes ------------------------
    system.setTenant(tenantName);
    validateTSystem(authenticatedUser, system);

    // ----------------- Resolve variables for any attributes that might contain them --------------------
    system = resolveVariables(system, apiUserId);

    // ----------------- Create all artifacts --------------------
    // Creation of system and role/perms/creds not in single DB transaction. Need to handle failure of role/perms/creds operations
    // Use try/catch/finally to rollback any writes in case of failure.
    int itemId = -1;
    // Get SK client now. If we cannot get this rollback not needed.
    var skClient = getSKClient(authenticatedUser);
    try {
      // ------------------- Make Dao call to persist the system -----------------------------------
       itemId = dao.createTSystem(authenticatedUser, system, scrubbedJson);

      // ------------------- Add permissions -----------------------------------
      // Give owner and possibly effectiveUser access to the system
      grantUserPermissions(authenticatedUser, systemName, system.getOwner(), ALL_PERMS);
      if (!effectiveUserId.equals(APIUSERID_VAR) && !effectiveUserId.equals(OWNER_VAR)) {
        grantUserPermissions(authenticatedUser, systemName, effectiveUserId, ALL_PERMS);
      }
      // TODO remove addition of files related permSpec
      // Give owner/effectiveUser files service related permission for root directory
      String filesPermSpec = "files:" + tenantName + ":*:" + systemName;
      skClient.grantUserPermission(tenantName, system.getOwner(), filesPermSpec);
      if (!effectiveUserId.equals(APIUSERID_VAR) && !effectiveUserId.equals(OWNER_VAR))
        skClient.grantUserPermission(tenantName, effectiveUserId, filesPermSpec);

      // ------------------- Store credentials -----------------------------------
      // Store credentials in Security Kernel if cred provided and effectiveUser is static
      if (system.getAccessCredential() != null && !effectiveUserId.equals(APIUSERID_VAR)) {
        String accessUser = effectiveUserId;
        // If effectiveUser is owner resolve to static string.
        if (effectiveUserId.equals(OWNER_VAR)) accessUser = system.getOwner();
        createUserCredential(authenticatedUser, systemName, accessUser, system.getAccessCredential());
      }
    }
    catch (Exception e) { throw e; }
    finally
    {
      // Remove system from DB
      if (itemId != -1) try {dao.deleteTSystem(tenantName, systemName); } catch (Exception e) {};
      // Remove perms
      String filesPermSpec = "files:" + tenantName + ":*:" + systemName;
      try { revokeUserPermissions(authenticatedUser, systemName, system.getOwner(), ALL_PERMS); } catch (Exception e) {};
      try { revokeUserPermissions(authenticatedUser, systemName, effectiveUserId, ALL_PERMS); } catch (Exception e) {};
      try { revokeUserPermissions(authenticatedUser, systemName, effectiveUserId, ALL_PERMS); } catch (Exception e) {};
      try { skClient.revokeUserPermission(tenantName, system.getOwner(), filesPermSpec);  } catch (Exception e) {};
      try { skClient.revokeUserPermission(tenantName, effectiveUserId, filesPermSpec);  } catch (Exception e) {};
      // Remove creds
      try
      {
        if (system.getAccessCredential() != null && !effectiveUserId.equals(APIUSERID_VAR)) {
          String accessUser = effectiveUserId;
          if (effectiveUserId.equals(OWNER_VAR)) accessUser = system.getOwner();
          deleteUserCredential(authenticatedUser, systemName, accessUser);
        }
      } catch (Exception e) {};
    }
    return itemId;
  }

  /**
   * Delete a system record given the system name.
   * Also remove permissions and credentials from the Security Kernel
   *
   * @param authenticatedUser - principal user containing tenant and user info
   * @param systemName - name of system
   * @return Number of items deleted
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public int deleteSystemByName(AuthenticatedUser authenticatedUser, String systemName) throws TapisException
  {
    String opName = "deleteSystem";
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // ------------------------- Check service level authorization -------------------------
    checkAuth(authenticatedUser, opName, systemName, null);

    var skClient = getSKClient(authenticatedUser);
    // TODO: Remove all credentials associated with the system.
    // TODO: Have SK do this in one operation?
//    deleteUserCredential(tenantName, systemName, );
    // Construct basic SK secret parameters
//    var sParms = new SKSecretMetaParms(SecretType.System).setSecretName(TOP_LEVEL_SECRET_NAME).setSysId(systemName).setSysOwner(accessUser);
//    skClient.destroySecretMeta(sParms);

    // TODO/TBD: How to make sure all perms for a system are removed?
    // TODO: See if it makes sense to have a SK method to do this in one operation
    // Use Security Kernel client to find all users with perms associated with the system.
    // Get the Security Kernel client
    String permSpec = PERM_SPEC_PREFIX + tenantName + ":%:" + systemName;
    var userNames = skClient.getUsersWithPermission(tenantName, permSpec);
    // Revoke all perms for all users
    for (String userName : userNames) {
      revokeUserPermissions(authenticatedUser, systemName, userName, ALL_PERMS);
    }

    // Delete the system
    return dao.deleteTSystem(tenantName, systemName);
  }

  /**
   * getSystemByName
   * @param authenticatedUser - principal user containing tenant and user info
   * @param systemName - Name of the system
   * @return true if system exists, false if system does not exist
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public boolean checkForSystemByName(AuthenticatedUser authenticatedUser, String systemName) throws TapisException {
    String opName = "readSystem";

    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // ------------------------- Check service level authorization -------------------------
    checkAuth(authenticatedUser, opName, systemName, null);
    boolean result = dao.checkForTSystemByName(tenantName, systemName);
    return result;
  }

  /**
   * getSystemByName
   * @param authenticatedUser - principal user containing tenant and user info
   * @param systemName - Name of the system
   * @return TSystem - populated instance or null if not found.
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public TSystem getSystemByName(AuthenticatedUser authenticatedUser, String systemName, boolean getCreds, AccessMethod accMethod1) throws TapisException {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    TSystem result = dao.getTSystemByName(tenantName, systemName);
    if (result == null) return null;
    // Resolve effectiveUserId if necessary
    String effectiveUserId = resolveEffectiveUserId(result.getEffectiveUserId(), result.getOwner(), apiUserId);
    // Update result with effectiveUserId
    result.setEffectiveUserId(effectiveUserId);
    // If requested and effectiveUserid is not ${apiUserId} (i.e. is static) then retrieve credentials from Security Kernel
    if (getCreds && !result.getEffectiveUserId().equals(TSystem.APIUSERID_VAR))
    {
      AccessMethod accMethod = result.getDefaultAccessMethod();
      // If accessMethod specified then use it instead of default access method defined for the system.
      if (accMethod1 != null) accMethod = accMethod1;
      Credential cred = getUserCredential(authenticatedUser, systemName, effectiveUserId, accMethod);
      result.setAccessCredential(cred);
    }
    return result;
  }

  /**
   * Get all systems
   * @param authenticatedUser - principal user containing tenant and user info
   * @return List of TSystem objects
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public List<TSystem> getSystems(AuthenticatedUser authenticatedUser) throws TapisException
  {
    List<TSystem> result = dao.getTSystems(authenticatedUser.getTenantId());
    for (TSystem sys : result)
    {
      sys.setEffectiveUserId(resolveEffectiveUserId(sys.getEffectiveUserId(), sys.getOwner(), authenticatedUser.getName()));
    }
    return result;
  }

  /**
   * Get list of system names
   * @param authenticatedUser - principal user containing tenant and user info
   * @return - list of systems
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public List<String> getSystemNames(AuthenticatedUser authenticatedUser) throws TapisException
  {
    return dao.getTSystemNames(authenticatedUser.getTenantId());
  }

  /**
   * Get system owner
   * @param authenticatedUser - principal user containing tenant and user info
   * @param systemName - Name of the system
   * @return - Owner or null if system not found
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public String getSystemOwner(AuthenticatedUser authenticatedUser, String systemName) throws TapisException
  {
    return dao.getTSystemOwner(authenticatedUser.getTenantId(), systemName);
  }

  // -----------------------------------------------------------------------
  // --------------------------- Permissions -------------------------------
  // -----------------------------------------------------------------------

  /**
   * Grant permissions to a user for a system
   * NOTE: This only impacts the default user role
   *
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public void grantUserPermissions(AuthenticatedUser authenticatedUser, String systemName, String userName, List<String> permissions)
    throws TapisException
  {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();

    // Check inputs. If anything null or empty throw an exception
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(systemName) || StringUtils.isBlank(userName) ||
        permissions == null || permissions.isEmpty())
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    }

    // Create a set of individual permSpec entries based on the list passed in
    Set<String> permSpecSet = getPermSpecSet(tenantName, systemName, permissions);

    // Get the Security Kernel client
    var skClient = getSKClient(authenticatedUser);

    // Assign perms to user. SK creates a default role for the user
    try
    {
      for (String permSpec : permSpecSet)
      {
        skClient.grantUserPermission(tenantName, userName, permSpec);
      }
    }
    // TODO exception handling
    catch (Exception e) { _log.error(e.toString()); throw e;}
  }

  /**
   * Revoke permissions from a user for a system
   * NOTE: This only impacts the default user role
   *
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public void revokeUserPermissions(AuthenticatedUser authenticatedUser, String systemName, String userName, List<String> permissions)
    throws TapisException
  {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();

    // Check inputs. If anything null or empty throw an exception
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(systemName) || StringUtils.isBlank(userName) ||
      permissions == null || permissions.isEmpty())
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    }

    // Create a set of individual permSpec entries based on the list passed in
    Set<String> permSpecSet = getPermSpecSet(tenantName, systemName, permissions);

    // Get the Security Kernel client
    var skClient = getSKClient(authenticatedUser);

    // Remove perms from default user role
    try
    {
      for (String permSpec : permSpecSet)
      {
        skClient.revokeUserPermission(tenantName, userName, permSpec);
      }
    }
    // TODO exception handling
    catch (Exception e) { _log.error(e.toString()); throw e;}
  }

  /**
   * Get list of system permissions for a user
   * NOTE: This retrieves permissions from all roles.
   *
   * @return List of permissions
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public List<String> getUserPermissions(AuthenticatedUser authenticatedUser, String systemName, String userName) throws TapisException
  {
    var userPerms = new ArrayList<String>();
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // Use Security Kernel client to check for each permission in the enum list
    var skClient = getSKClient(authenticatedUser);
    for (TSystem.Permission perm : TSystem.Permission.values())
    {
      String permSpec = PERM_SPEC_PREFIX + tenantName + ":" + perm.name() + ":" + systemName;
      try
      {
        Boolean isAuthorized = skClient.isPermitted(tenantName, userName, permSpec);
        if (Boolean.TRUE.equals(isAuthorized)) userPerms.add(perm.name());
      }
      // TODO exception handling
      catch (Exception e) { _log.error(e.toString()); throw e;}
    }
    return userPerms;
  }

  // -----------------------------------------------------------------------
  // ---------------------------- Credentials ------------------------------
  // -----------------------------------------------------------------------

  /**
   * Store or update credential for given system and user.
   *
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public void createUserCredential(AuthenticatedUser authenticatedUser, String systemName, String userName, Credential credential)
          throws TapisException
  {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // Check inputs. If anything null or empty throw an exception
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(systemName) || StringUtils.isBlank(userName) || credential == null)
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    }
    // Get the Security Kernel client
    var skClient = getSKClient(authenticatedUser);
    try {
      // Construct basic SK secret parameters
      var sParms = new SKSecretWriteParms(SecretType.System).setSecretName(TOP_LEVEL_SECRET_NAME).setSysId(systemName).setSysUser(userName);
      Map<String, String> dataMap;
      // Check for each secret type and write values if they are present
      // Not that multiple secrets may be present.
      // Store password if present
      if (!StringUtils.isBlank(credential.getPassword())) {
        dataMap = new HashMap<>();
        sParms.setKeyType(KeyType.password);
        dataMap.put(SK_KEY_PASSWORD, credential.getPassword());
        sParms.setData(dataMap);
        skClient.writeSecret(tenantName, userName, sParms);
      }
      // Store PKI keys if both present
      if (!StringUtils.isBlank(credential.getPublicKey()) && !StringUtils.isBlank(credential.getPublicKey())) {
        dataMap = new HashMap<>();
        sParms.setKeyType(KeyType.sshkey);
        dataMap.put(SK_KEY_PUBLIC_KEY, credential.getPublicKey());
        dataMap.put(SK_KEY_PRIVATE_KEY, credential.getPrivateKey());
        sParms.setData(dataMap);
        skClient.writeSecret(tenantName, userName, sParms);
      }
      // Store Access key and secret if both present
      if (!StringUtils.isBlank(credential.getAccessKey()) && !StringUtils.isBlank(credential.getAccessSecret())) {
        dataMap = new HashMap<>();
        sParms.setKeyType(KeyType.accesskey);
        dataMap.put(SK_KEY_ACCESS_KEY, credential.getAccessKey());
        dataMap.put(SK_KEY_ACCESS_SECRET, credential.getAccessSecret());
        sParms.setData(dataMap);
        skClient.writeSecret(tenantName, userName, sParms);
      }
      // TODO what about ssh certificate? Nothing to do here?
    }
    // TODO exception handling
    catch (Exception e) { _log.error(e.toString()); throw e;}
  }

  /**
   * Delete credential for given system and user
   *
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public void deleteUserCredential(AuthenticatedUser authenticatedUser, String systemName, String userName)
          throws TapisException
  {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // Check inputs. If anything null or empty throw an exception
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(systemName) || StringUtils.isBlank(userName))
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    }
    // Get the Security Kernel client
    var skClient = getSKClient(authenticatedUser);
    // Construct basic SK secret parameters
    var sParms = new SKSecretMetaParms(SecretType.System).setSecretName(TOP_LEVEL_SECRET_NAME).setSysId(systemName).setSysUser(userName);
    try
    {
      sParms.setTenant(tenantName);
      sParms.setUser(apiUserId);
      sParms.setKeyType(KeyType.password);
      skClient.destroySecretMeta(sParms);
      sParms.setKeyType(KeyType.sshkey);
      skClient.destroySecretMeta(sParms);
      sParms.setKeyType(KeyType.accesskey);
      skClient.destroySecretMeta(sParms);
      sParms.setKeyType(KeyType.cert);
      skClient.destroySecretMeta(sParms);
    }
    // TODO exception handling
    // TODO/TBD: If tapis client exception then log warning but continue
    // TODO/TBD: for other exectpions log error and re-throw the exception
    catch (TapisClientException tce) { _log.warn(tce.toString()); }
    catch (Exception e) { _log.error(e.toString()); throw e;}
  }

  /**
   * Get credential for given system, user and access method
   * @return Credential - populated instance or null if not found.
   * @throws TapisException - for Tapis related exceptions
   */
  @Override
  public Credential getUserCredential(AuthenticatedUser authenticatedUser, String systemName, String userName, AccessMethod accessMethod) throws TapisException
  {
    // Extract various names for convenience
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();

    // Check inputs. If anything null or empty throw an exception
    if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(systemName) || StringUtils.isBlank(userName))
    {
      throw new IllegalArgumentException(LibUtils.getMsg("SYSLIB_NULL_INPUT"));
    }
    // If system does not exist throw an exception
    if (!checkForSystemByName(authenticatedUser, systemName)) throw new TapisException(LibUtils.getMsg("SYSLIB_NOT_FOUND", authenticatedUser.getName(), systemName));

    // If accessMethod not passed in fill in with default from system
    if (accessMethod == null)
    {
      TSystem sys = dao.getTSystemByName(tenantName, systemName);
      if (sys == null)  throw new TapisException(LibUtils.getMsg("SYSLIB_NOT_FOUND", authenticatedUser.getName(), systemName));
      accessMethod = sys.getDefaultAccessMethod();
    }


    Credential credential = null;
    try
    {
      // Get the Security Kernel client
      var skClient = getSKClient(authenticatedUser);
      // Construct basic SK secret parameters
      var sParms = new SKSecretReadParms(SecretType.System).setSecretName(TOP_LEVEL_SECRET_NAME).setSysId(systemName).setSysUser(userName);
      // TODO/TBD Should these be OBO or svc values?
      sParms.setTenant(tenantName).setUser(apiUserId);
      // Set key type based on access method
      if (accessMethod.equals(AccessMethod.PASSWORD))sParms.setKeyType(KeyType.password);
      else if (accessMethod.equals(AccessMethod.PKI_KEYS))sParms.setKeyType(KeyType.sshkey);
      else if (accessMethod.equals(AccessMethod.ACCESS_KEY))sParms.setKeyType(KeyType.accesskey);
      else if (accessMethod.equals(AccessMethod.CERT))sParms.setKeyType(KeyType.cert);

      // Retrieve the secrets
      SkSecret skSecret = skClient.readSecret(sParms);
      if (skSecret == null) return null;
      var dataMap = skSecret.getSecretMap();
      if (dataMap == null) return null;

      // Create a credential
      credential = new Credential(dataMap.get(SK_KEY_PASSWORD),
              dataMap.get(SK_KEY_PRIVATE_KEY),
              dataMap.get(SK_KEY_PUBLIC_KEY),
              dataMap.get(SK_KEY_ACCESS_KEY),
              dataMap.get(SK_KEY_ACCESS_SECRET),
              null); //dataMap.get(CERT) TODO: how to get ssh certificate
    }
    // TODO exception handling
    // TODO/TBD: If tapis client exception then log error but continue so null is returned.
    // TODO/TBD: for other exectpions log error and re-throw the exception
    catch (TapisClientException tce) { _log.error(tce.toString()); }
    catch (Exception e) { _log.error(e.toString()); throw e;}

    return credential;
  }

  // ************************************************************************
  // **************************  Private Methods  ***************************
  // ************************************************************************

  /**
   * Get Security Kernel client associated with specified tenant
   * @param authenticatedUser - name of tenant
   * @return SK client
   * @throws TapisException - for Tapis related exceptions
   */
  private SKClient getSKClient(AuthenticatedUser authenticatedUser) throws TapisException
  {
    String tenantName = authenticatedUser.getTenantId();
    String apiUserId = authenticatedUser.getName();
    // Use TenantManager to get tenant info. Needed for tokens and SK base URLs.
    Tenant tenant = TenantManager.getInstance().getTenant(tenantName);

    // Update SKClient on the fly. If this becomes a bottleneck we can add a cache.
    // Get Security Kernel URL from the env or the tenants service. Env value has precedence.
    //    String skURL = "https://dev.develop.tapis.io/v3";
    String skURL = RuntimeParameters.getInstance().getSkSvcURL();
    if (StringUtils.isBlank(skURL)) skURL = tenant.getSecurityKernel();
    if (StringUtils.isBlank(skURL)) throw new TapisException(LibUtils.getMsg("SYSLIB_CREATE_SK_URL_ERROR", authenticatedUser, apiUserId));
    // TODO remove strip-off of everything after /v3 once tenant is updated or we do something different for base URL in auto-generated clients
    // Strip off everything after the /v3 so we have a valid SK base URL
    skURL = skURL.substring(0, skURL.indexOf("/v3") + 3);
    skClient.setBasePath(skURL);
    skClient.addDefaultHeader(HDR_TAPIS_TOKEN, serviceJWT.getAccessJWT());
    // For incoming service requests put oboTenant and oboUser in headers
    if (authenticatedUser.getAccountType().equals(TapisThreadContext.AccountType.service.name()))
    {
      skClient.addDefaultHeader(HDR_TAPIS_TENANT, authenticatedUser.getOboTenantId());
      skClient.addDefaultHeader(HDR_TAPIS_USER, authenticatedUser.getOboUser());
    }
    return skClient;
  }

  /**
   * Check constraints on TSystem attributes
   * @param system - the TSystem to process
   */
  private static TSystem resolveVariables(TSystem system, String apiUserId)
  {
    // Resolve owner if necessary. If empty or "${apiUserId}" then fill in with apiUserId
    String owner = system.getOwner();
    if (StringUtils.isBlank(owner) || owner.equalsIgnoreCase(APIUSERID_VAR)) owner = apiUserId;
    system.setOwner(owner);

    // Perform variable substitutions that happen at create time: bucketName, rootDir, jobLocalWorkingDir, jobLocalArchiveDir
    // NOTE: effectiveUserId is not processed. Var reference is retained and substitution done as needed when system is retrieved.
    //    ALL_VARS = {APIUSERID_VAR, OWNER_VAR, TENANT_VAR};
    String[] allVarSubstitutions = {apiUserId, owner, system.getTenant()};
    system.setBucketName(StringUtils.replaceEach(system.getBucketName(), ALL_VARS, allVarSubstitutions));
    system.setRootDir(StringUtils.replaceEach(system.getRootDir(), ALL_VARS, allVarSubstitutions));
    system.setJobLocalWorkingDir(StringUtils.replaceEach(system.getJobLocalWorkingDir(), ALL_VARS, allVarSubstitutions));
    system.setJobLocalArchiveDir(StringUtils.replaceEach(system.getJobLocalArchiveDir(), ALL_VARS, allVarSubstitutions));
    system.setJobRemoteArchiveDir(StringUtils.replaceEach(system.getJobRemoteArchiveDir(), ALL_VARS, allVarSubstitutions));
    return system;
  }

  /**
   * Fill in defaults and check constraints on TSystem attributes.
   * effectiveUserId is restricted.
   * If transfer mechanism S3 is supported then bucketName must be set.
   * @param system - the TSystem to check
   * @throws IllegalStateException - if any constraints are violated
   */
  private static void validateTSystem(AuthenticatedUser authenticatedUser, TSystem system) throws IllegalStateException
  {
    String msg;
    var errMessages = new ArrayList<String>();
    // Make sure owner, effectiveUserId, notes and tags are all set
    system = TSystem.checkAndSetDefaults(system);

    // Check for valid effectiveUserId
    // For CERT access the effectiveUserId cannot be static string other than owner
    String effectiveUserId = system.getEffectiveUserId();
    if (system.getDefaultAccessMethod().equals(AccessMethod.CERT) &&
        !effectiveUserId.equals(TSystem.APIUSERID_VAR) &&
        !effectiveUserId.equals(TSystem.OWNER_VAR) &&
        !StringUtils.isBlank(system.getOwner()) &&
        !effectiveUserId.equals(system.getOwner()))
    {
      // For CERT access the effectiveUserId cannot be static string other than owner
      msg = LibUtils.getMsg("SYSLIB_INVALID_EFFECTIVEUSERID_INPUT");
      errMessages.add(msg);
    }
    else if (system.getTransferMethods().contains(TransferMethod.S3) && StringUtils.isBlank(system.getBucketName()))
    {
      // For S3 support bucketName must be set
      msg = LibUtils.getMsg("SYSLIB_S3_NOBUCKET_INPUT");
      errMessages.add(msg);
    }
    else if (system.getAccessCredential() != null && effectiveUserId.equals(TSystem.APIUSERID_VAR))
    {
      // If effectiveUserId is dynamic then providing credentials is disallowed
      msg = LibUtils.getMsg("SYSLIB_CRED_DISALLOWED_INPUT");
      errMessages.add(msg);
    }
    // If validation failed throw an exception
    if (!errMessages.isEmpty())
    {
      // Construct message reporting all errors
      String allErrors = getListOfErrors(authenticatedUser, system.getName(), errMessages);
      _log.error(allErrors);
      throw new IllegalStateException(allErrors);
    }
  }

  /**
   * If effectiveUserId is dynamic then resolve it
   * @param userId - effectiveUserId string, static or dynamic
   * @return Resolved value for effective user.
   */
  private static String resolveEffectiveUserId(String userId, String owner, String apiUserId)
  {
    if (StringUtils.isBlank(userId)) return userId;
    else if (userId.equals(OWNER_VAR) && !StringUtils.isBlank(owner)) return owner;
    else if (userId.equals(APIUSERID_VAR) && !StringUtils.isBlank(apiUserId)) return apiUserId;
    else return userId;
  }

  /**
   * Create a set of individual permSpec entries based on the list passed in
   * @param permList - list of individual permissions
   * @return - Set of permSpec entries based on permissions
   */
  private static Set<String> getPermSpecSet(String tenantName, String systemName, List<String> permList)
  {
    var permSet = new HashSet<String>();
    for (String permStr : permList)
    {
      String permSpec = PERM_SPEC_PREFIX + tenantName + ":" + permStr.toUpperCase() + ":" + systemName;
      permSet.add(permSpec);
    }
    return permSet;
  }

  /**
   * Construct message containing list of errors
   */
  private static String getListOfErrors(AuthenticatedUser authenticatedUser, String systemName, List<String> msgList) {
    if (msgList == null || msgList.isEmpty()) return "";
    var sb = new StringBuilder(LibUtils.getMsg("SYSLIB_CREATE_INVALID_ERRORLIST", authenticatedUser.getName(), systemName));
    sb.append(System.lineSeparator());
    for (String msg : msgList) { sb.append("  ").append(msg).append(System.lineSeparator()); }
    return sb.toString();
  }

  /**
   * Check service level authorization
   * Create - must be owner or have admin role
   * Delete - must be owner or have admin role
   * TODO Modify - must be owner or have admin role
   * TODO Modify owner - must have admin role
   * @param authenticatedUser - principal user containing tenant and user info
   * @param opName - operation name, for constructing response msg
   * @param systemName - name of the system
   * @throws IllegalStateException - apiUserId not authorized to perform operation
   */
  private void checkAuth(AuthenticatedUser authenticatedUser, String opName, String systemName, TSystem system)
           throws IllegalStateException, TapisException
  {
    // TODO
    return;
//    String msg;
//    // Tenant Admin has full authorization
//    if (hasAdminRole(tenantName, apiUserId)) return;
//
//    // Retrieve owner if necessary, use empty string if system obj is null, as will be the case for
//    //   all operations except create.
//    String owner = (system == null ? "": system.getOwner());
//    if (StringUtils.isBlank(owner)) owner = getSystemOwner(tenantName, apiUserId, systemName);
//
//    // Requester (apiUserId) must be the owner
//    if (owner.equals(apiUserId)) return;
//
//    // Not authorized
//    msg = LibUtils.getMsg("SYSLIB_UNAUTH", apiUserId, opName, systemName);
//    _log.error(msg);
//    throw new IllegalStateException(msg);
  }

  /**
   * Check to see if apiUserId has the service admin role
   */
  private boolean hasAdminRole(AuthenticatedUser authenticatedUser) throws TapisException
  {
    var skClient = getSKClient(authenticatedUser);
    return skClient.hasRole(authenticatedUser.getTenantId(), authenticatedUser.getName(), SYSTEMS_ADMIN_ROLE);
  }

// TODO *************** remove debug output ********************
//  private static void printPermInfoForUser(SKClient skClient, String userName)
//  {
//    if (skClient == null || userName == null) return;
//    try {
//      // Test retrieving all roles for a user
//      List<String> roles = skClient.getUserRoles(userName);
//      _log.error("User " + userName + " has the following roles: ");
//      for (String role : roles) { _log.error("  role: " + role); }
//      // Test retrieving all perms for a user
//      List<String> perms = skClient.getUserPerms(userName, null, null);
//      _log.error("User " + userName + " has the following permissions: ");
//      for (String perm : perms) { _log.error("  perm: " + perm); }
//    } catch (Exception e) { _log.error(e.toString()); }
//  }
// TODO *************** remove tests ********************
}
