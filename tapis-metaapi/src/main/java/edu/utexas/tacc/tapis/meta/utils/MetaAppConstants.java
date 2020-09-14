package edu.utexas.tacc.tapis.meta.utils;

import edu.utexas.tacc.tapis.shared.TapisConstants;

public class MetaAppConstants {
  
  public final static int META_FILTER_PRIORITY_PERMISSIONS =
      TapisConstants.JAXRS_FILTER_PRIORITY_AFTER_AUTHENTICATION_3+100;
  
  public final static String META_REQUEST_PREFIX = "/meta/v3";
  
  public final static String V2_JWT_AUTH_HEADER_PREFIX = "X-JWT-Assertion-";
  public final static String ETAG_NAME                 = "ETag";
  public final static String V2_URI_BASEPATH           = "/meta/v3";
  
  // Set Permissions checking
  public static boolean TAPIS_ENVONLY_META_PERMISSIONS_CHECK = true;
  
  public static void setTapisEnvonlyMetaPermissionsCheck(boolean tapisEnvonlyMetaPermissionsCheck) {
    TAPIS_ENVONLY_META_PERMISSIONS_CHECK = tapisEnvonlyMetaPermissionsCheck;
  }
  
  
  
}
