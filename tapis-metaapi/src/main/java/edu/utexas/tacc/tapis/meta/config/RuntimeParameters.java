package edu.utexas.tacc.tapis.meta.config;

import edu.utexas.tacc.tapis.meta.permissions.V2PermissionsRegistry;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.exceptions.runtime.TapisRuntimeException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.parameters.TapisEnv;
import edu.utexas.tacc.tapis.shared.parameters.TapisInput;
import edu.utexas.tacc.tapis.shared.uuid.TapisUUID;
import edu.utexas.tacc.tapis.shared.uuid.UUIDType;
import edu.utexas.tacc.tapis.sharedapi.security.ServiceJWT;
import edu.utexas.tacc.tapis.sharedapi.security.ServiceJWTParms;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.Properties;

public class RuntimeParameters {
  
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(RuntimeParameters.class);
  
  // Distinguished user-chosen name of this runtime instance.
  private String  instanceName;
  
  // Globally unique id that identifies this JVM instance.
  private static final TapisUUID id = new TapisUUID(UUIDType.METADATA);
  
  // Singleton instance.
  private static RuntimeParameters _instance = initInstance();
  
  /* ---------------------------------------------------------------------- */
  /* getLogSecurityInfo:                                                    */
  /* ---------------------------------------------------------------------- */
  /** Go directly to the environment to get the latest security info logging
   * value.  This effectively disregards any setting the appears in a
   * properties file or on the JVM command line.
   *
   * @return the current environment variable setting
   */
  public static boolean getLogSecurityInfo()
  {
    // Always return the latest environment value.
    return TapisEnv.getLogSecurityInfo();
  }
  
  // service locations.
  private String tenantBaseUrl = "https://dev.develop.tapis.io/";
  private String skSvcURL      = "https://dev.develop.tapis.io/v3";
  private String tokenBaseUrl  =  "https://dev.develop.tapis.io/";
  private String metaToken;
  private ServiceJWT serviceJWT;
  
  // The slf4j/logback target directory and file.
  private String  logDirectory ="/tmp/meta";
  private String  logFile ="meta-service.log";
  // default core server setup
  private String  coreServer = "http://restheart:8080/";
  private String coreserver_connection_timeout="3";   // default 2 minutes
  
  // these need to move to shared library
  public static final String SERVICE_NAME_META  = "meta";
  public static final String SERVICE_USER_NAME  = "meta";
  public static final String SERVICE_TENANT_NAME = "master";
  
  
  private RuntimeParameters() throws TapisRuntimeException {
    TapisInput tapisInput = new TapisInput(RuntimeParameters.SERVICE_NAME_META);
    Properties inputProperties = null;
    try {inputProperties = tapisInput.getInputParameters();}
    catch (TapisException e) {
      // Very bad news.
      String msg = MsgUtils.getMsg("TAPIS_SERVICE_INITIALIZATION_FAILED",
          RuntimeParameters.SERVICE_NAME_META,
          e.getMessage());
      _log.error(msg, e);
      throw new TapisRuntimeException(msg, e);
    }
  
    //----------------------   Input parameters   ----------------------

    String parm = System.getenv("tapis.meta.core.server");
    if (!StringUtils.isBlank(parm)) setCoreServer(parm);
  
    // String parm = inputProperties.getProperty(TapisEnv.EnvVar.TAPIS_LOG_DIRECTORY.getEnvName());
    parm = inputProperties.getProperty("tapis.meta.log.directory");
    if (!StringUtils.isBlank(parm)) setLogDirectory(parm);
  
    parm = inputProperties.getProperty("tapis.meta.log.file");
    if (!StringUtils.isBlank(parm)) setLogFile(parm);
  
    parm = System.getenv("tapis.meta.coreserver.connection.timeout");
    if (!StringUtils.isBlank(parm)){
      setCoreserver_connection_timeout(parm);
    }else{
      parm = inputProperties.getProperty("tapis.meta.coreserver.connection.timeout");
      if (!StringUtils.isBlank(parm)) setCoreserver_connection_timeout(parm);
    }
    
  
    //----------------------   Initialize Permissions Registry   ----------------------
    // 1. initialize Registry
    V2PermissionsRegistry registry = null;
    try {
      registry = V2PermissionsRegistry.getInstance();
    }catch (Exception e){
      String msg = "METAV3 PERMISSIONS REGISTRY INITIALIZATION FAILED \n"
          + RuntimeParameters.SERVICE_NAME_META +"\n"
          + e.getMessage();
      _log.error(msg, e);
      throw new TapisRuntimeException(msg, e);
    }
    
/*
    parm = inputProperties.getProperty("tapis.meta.service.token");
    if (!StringUtils.isBlank(parm)) setMetaToken(parm);
*/
  
  
  
  }
  
  /** Initialize the singleton instance of this class.
   *
   * @return the non-null singleton instance of this class
   */
  private static synchronized RuntimeParameters initInstance()
  {
    if (_instance == null) _instance = new RuntimeParameters();
    return _instance;
  }
  
  public static RuntimeParameters getInstance() {
    return _instance;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getRuntimeInfo:                                                        */
  /* ---------------------------------------------------------------------- */
  /** Augment the buffer with printable text based mostly on the parameters
   * managed by this class but also OS and JVM information.  The intent is
   * that the various job programs and utilities that rely on this class can
   * print their configuration parameters, including those from this class,
   * when they start up.
   *
   * @param buf
   */
  public void getRuntimeInfo(StringBuilder buf)
  {
    buf.append("\n------- Logging -----------------------------------");
    buf.append("\ntapis.meta.log.directory: ");
    buf.append(this.getLogDirectory());
    buf.append("\ntapis.meta.log.file: ");
    buf.append(this.getLogFile());
    
    buf.append("\n\n------- Network -----------------------------------");
    buf.append("\nHost Addresses: ");
    // buf.append(getNetworkAddresses());
    
    buf.append("\n\n------- Tenants -----------------------------------");
    buf.append("\ntapis.tenant.svc.baseurl: ");
    buf.append(this.getTenantBaseUrl());
    
    buf.append("\n\n------- Core Server Configuration --------------------------");
    buf.append("\ntapis.meta.core.server: ");
    buf.append(this.getCoreServer());
    buf.append("\ntapis.meta.coreserver.connection.timeout: ");
    buf.append(this.getCoreserver_connection_timeout());
  
    
    buf.append("\n\n------- EnvOnly Configuration ---------------------");
    buf.append("\ntapis.envonly.log.security.info: ");
    buf.append(RuntimeParameters.getLogSecurityInfo());
    buf.append("\ntapis.envonly.allow.test.header.parms: ");
    // buf.append(this.isAllowTestHeaderParms());
    buf.append("\ntapis.envonly.jwt.optional: ");
    buf.append(TapisEnv.getBoolean(TapisEnv.EnvVar.TAPIS_ENVONLY_JWT_OPTIONAL));
    buf.append("\ntapis.envonly.skip.jwt.verify: ");
    buf.append(TapisEnv.getBoolean(TapisEnv.EnvVar.TAPIS_ENVONLY_SKIP_JWT_VERIFY));
    
    buf.append("\n\n------- Java Configuration ------------------------");
    buf.append("\njava.version: ");
    buf.append(System.getProperty("java.version"));
    buf.append("\njava.vendor: ");
    buf.append(System.getProperty("java.vendor"));
    buf.append("\njava.vm.version: ");
    buf.append(System.getProperty("java.vm.version"));
    buf.append("\njava.vm.vendor: ");
    buf.append(System.getProperty("java.vm.vendor"));
    buf.append("\njava.vm.name: ");
    buf.append(System.getProperty("java.vm.name"));
    buf.append("\nos.name: ");
    buf.append(System.getProperty("os.name"));
    buf.append("\nos.arch: ");
    buf.append(System.getProperty("os.arch"));
    buf.append("\nos.version: ");
    buf.append(System.getProperty("os.version"));
    buf.append("\nuser.name: ");
    buf.append(System.getProperty("user.name"));
    buf.append("\nuser.home: ");
    buf.append(System.getProperty("user.home"));
    buf.append("\nuser.dir: ");
    buf.append(System.getProperty("user.dir"));
    
    buf.append("\n\n------- JVM Runtime Values ------------------------");
    NumberFormat formatter = NumberFormat.getIntegerInstance();
    buf.append("\navailableProcessors: ");
    buf.append(formatter.format(Runtime.getRuntime().availableProcessors()));
    buf.append("\nmaxMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().maxMemory()));
    buf.append("\ntotalMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().totalMemory()));
    buf.append("\nfreeMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().freeMemory()));
  }
  
  public String getTenantBaseUrl() {
    return this.tenantBaseUrl;
  }
  
  public void setTenantBaseUrl(String tenantBaseUrl) {
    this.tenantBaseUrl = tenantBaseUrl;
  }
  
  public String getSkSvcURL() {
    return skSvcURL;
  }
  
  public void setSkSvcURL(String skSvcURL) {
    this.skSvcURL = skSvcURL;
  }
  
  public String getMetaToken() {
    return metaToken;
  }
  
  public void setMetaToken(String metaToken) {
    this.metaToken = metaToken;
  }
  
  public String getLogDirectory() {
    return logDirectory;
  }
  
  public void setLogDirectory(String logDirectory) {
    this.logDirectory = logDirectory;
  }
  
  public String getLogFile() {
    return logFile;
  }
  
  public void setLogFile(String logFile) {
    this.logFile = logFile;
  }
  
  public String getCoreServer() {
    return coreServer;
  }
  
  public void setCoreServer(String coreServer) {
    this.coreServer = coreServer;
  }
  
  public String getCoreserver_connection_timeout() { return coreserver_connection_timeout; }
  
  public void setCoreserver_connection_timeout(String coreserver_connection_timeout) { this.coreserver_connection_timeout = coreserver_connection_timeout; }
  
  public void setServiceJWT(){
    _log.debug("calling setServiceJWT ...");
    ServiceJWTParms serviceJWTParms = new ServiceJWTParms();
    serviceJWTParms.setAccessTTL(43200); // 12 hrs
    serviceJWTParms.setRefreshTTL(43200);
    serviceJWTParms.setServiceName(RuntimeParameters.SERVICE_USER_NAME);
    serviceJWTParms.setTenant(RuntimeParameters.SERVICE_TENANT_NAME);
    serviceJWTParms.setTokensBaseUrl(this.getTenantBaseUrl());
    serviceJWT = null;
    try {
      serviceJWT = new ServiceJWT(serviceJWTParms, TapisEnv.get(TapisEnv.EnvVar.TAPIS_SERVICE_PASSWORD));
    } catch (TapisException e) {
      e.printStackTrace();
    }
  }

  public String getSeviceToken(){
    if(serviceJWT == null || serviceJWT.hasExpiredAccessJWT()){
      setServiceJWT();
    }
    return serviceJWT.getAccessJWT();
  }
}
