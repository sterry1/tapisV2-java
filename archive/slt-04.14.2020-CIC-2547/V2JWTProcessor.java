package edu.utexas.tacc.tapis.meta.utils;


import edu.utexas.tacc.tapis.meta.api.exceptions.AloeSecurityException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Processes the JWT Header value and verifies it against out Aloe keys
 *
 * @author sterry1
 */public class V2JWTProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(V2JWTProcessor.class);
  /* ********************************************************************** */
  /*                                Fields                                  */
  /* ********************************************************************** */
  // The public key used to check the JWT signature.  This cached copy is
  // used by all instances of this class.
  private static PublicKey _jwtPublicKey;
  // The JWT key alias.  This is the key pair used to sign JWTs
  // and verify them.
  public static final String DEFAULT_KEY_ALIAS = "wso2";
  
  public boolean isVerified = false;
  public String userName = null;
  private String _roles = null;
  public Set<String> roles;
  public String jwtToken;
  
  
  
  /* ---------------------------------------------------------------------- */
  /* default contructor:                                                    */
  /* ---------------------------------------------------------------------- */
  /**
   * @return new V2JWTProcessor
   */
  public V2JWTProcessor(){ }
  
  public Jwt get_jwt() {
    return _jwt;
  }
  
  // the decoded and verified jwt that can produce a json version of claims body.
  private Jwt _jwt = null;
  
  
  /* ********************************************************************** */
  /*                            Private Methods                             */
  /* ********************************************************************** */
  /* ---------------------------------------------------------------------- */
  /* decodeJwt:                                                             */
  /* ---------------------------------------------------------------------- */
  /** Decode the jwt without verifying it signature.
   *
   * @param encodedJWT the JWT from the request header
   * @return the decoded but not verified jwt
   */
  Jwt decodeJwt(String encodedJWT)
      throws AloeSecurityException
  {
    // Some defensive programming.
    if (encodedJWT == null) return null;
    
    // Lop off the signature part of the encoding so that the
    // jjwt library can parse it without attempting validation.
    // We expect the jwt to contain exactly two periods in
    // the following encoded format: header.body.signature
    // We need to remove the signature but leave both periods.
    String remnant = encodedJWT;
    int lastDot = encodedJWT.lastIndexOf(".");
    if (lastDot + 1 < encodedJWT.length()) // should always be true
      remnant = encodedJWT.substring(0, lastDot + 1);
    
    // Parse the header and claims. If for some reason the remnant
    // isn't of the form header.body. then parsing will fail.
    Jwt jwt = null;
    try {jwt = Jwts.parser().parse(remnant);}
    catch (Exception e) {
      String msg = "ALOE_SECURITY_JWT_PARSE_ERROR";
      throw new AloeSecurityException(msg, e);
    }
    
    if (jwt !=null){
      this._jwt = jwt;
      this.isVerified = true;
      // we need to unpack the username and roles from claims
      // Retrieve the user name from the claims section.
      Claims claims = (Claims) jwt.getBody();
      if (claims != null) {
        userName = this.getUser(claims);
        _roles = this.getRoles(claims);
      }
    }
  
    roles = new LinkedHashSet<>();
    
    if (_roles != null && !_roles.isEmpty()) {
      try {
        String[] values = _roles.split(",");
        roles = new LinkedHashSet<String>(Arrays.asList(values));
      } catch (Exception ex ) {
        LOGGER.warn("Jwt cannot get roles from claim {}, "
                + "extepected an array of strings: {}",
            _roles.toString());
      }
    }
  
    return jwt;
  }
  
  /* ---------------------------------------------------------------------- */
  /* decodeAndVerifyJwt:                                                    */
  /* ---------------------------------------------------------------------- */
  /** Decode the jwt and use the JWT signature to validate that the header
   * and payload have not changed.
   *
   * @param encodedJWT the JWT from the request header
   * @return the decoded and verified jwt
   */
  public Jwt decodeAndVerifyJwt(String encodedJWT) throws AloeSecurityException {
    // Some defensive programming.
    if (encodedJWT == null) return null;
    
    // Get the public part of the signing key.
    PublicKey publicKey = getJwtPublicKey();
    
    // Verify and import the jwt data.
    Jwt jwt = null;
    try { jwt = Jwts.parser().setSigningKey(publicKey).parse(encodedJWT);}
    catch (Exception e) {
      String msg = "ALOE_SECURITY_JWT_PARSE_ERROR";
      LOGGER.error(msg, e);
      throw new AloeSecurityException(msg, e);
    }
    
    if (jwt !=null){
      this._jwt = jwt;
      this.isVerified = true;
      // we need to unpack the username and roles from claims
      // Retrieve the user name from the claims section.
      Claims claims = (Claims) jwt.getBody();
      if (claims != null) {
        userName = this.getUser(claims);
        _roles = this.getRoles(claims);
      }
  
    }
    
    // We have a validated jwt.
    return jwt;
  }
  /* ---------------------------------------------------------------------- */
  /* getJwtPublicKey:                                                       */
  /* ---------------------------------------------------------------------- */
  /** Return the cached public key if it exists.  If it doesn't exist, load it
   * from the keystore, cache it, and then return it.
   *
   * @return jwt Public Key
   */
  private PublicKey getJwtPublicKey() throws AloeSecurityException {
    // Use the cached copy if it has already been loaded.
    if (_jwtPublicKey != null) return _jwtPublicKey;
    
    // Serialize access to this code.
    synchronized (V2JWTProcessor.class)
    {
      // Maybe another thread loaded the key in the intervening time.
      if (_jwtPublicKey != null) return _jwtPublicKey;
      
      // We need to load the key from the keystore.
      // Get our own instance of the key manager
      // to avoid possible multithreading issues.
      V2KeyManager km = null;
      try {km = new V2KeyManager();}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_NO_KEYSTORE";
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Get the keystore's password.
      String password = System.getenv("KEYSTORE_PASS");  // AloeEnv.get(EnvVar.ALOE_ENVONLY_KEYSTORE_PASSWORD);
      if (StringUtils.isBlank(password)) {
        String msg = "ALOE_SECURITY_NO_KEYSTORE_PASSWORD";
        LOGGER.error(msg);
        throw new AloeSecurityException(msg);
      }
      
      // Load the complete store.
      try {km.load(password);}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_KEYSTORE_LOAD_ERROR";
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Get the certificate containing the public key.
      Certificate cert = null;
      try {cert = km.getCertificate(DEFAULT_KEY_ALIAS);}
      catch (KeyStoreException e) {
        String msg = "ALOE_SECURITY_GET_CERTIFICATE "+ DEFAULT_KEY_ALIAS+" "+e.getMessage();
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Make sure we got a certificate.
      if (cert == null) {
        String msg = "ALOE_SECURITY_CERTIFICATE_NOT_FOUND "+DEFAULT_KEY_ALIAS+" "+km.getStorePath();
        LOGGER.error(msg);
        throw new AloeSecurityException(msg);
      }
      
      // Get the public key from the certificate and verify the certificate.
      PublicKey publicKey = cert.getPublicKey();
      try {cert.verify(publicKey);}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_CERTIFICATE_VERIFY "+DEFAULT_KEY_ALIAS+" "+ e.getMessage();
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Success!
      _jwtPublicKey = publicKey;
    }
    
    return _jwtPublicKey;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getUser:                                                               */
  /* ---------------------------------------------------------------------- */
  /** Get the user name or return null.
   *
   * @param claims the JWT claims object
   * @return the simple user name or null
   */
  public String getUser(Claims claims)
  {
    // The enduser name may have extraneous information around it.
    String s = (String)claims.get("http://wso2.org/claims/enduser");
    if (StringUtils.isBlank(s)) return null;
    else if (s.contains("@")) return StringUtils.substringBefore(s, "@");
    else if (s.contains("/")) return StringUtils.substringAfter(s, "/");
    else return s;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getRoles:                                                              */
  /* ---------------------------------------------------------------------- */
  /** Get the set of roles or return null.
   *
   * @param claims the JWT claims object
   * @return the user's roles or null
   */
  public String getRoles(Claims claims)
  {
    String s =  (String)claims.get("http://wso2.org/claims/role");
    if (StringUtils.isBlank(s)) return null;
    else return s;
  }
  
}
