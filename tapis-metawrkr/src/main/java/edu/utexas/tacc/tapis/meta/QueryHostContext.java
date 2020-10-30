package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * This class is needed to manage the host context that
 * a Query is run against.
 * Pulls context info from RunTime
 * We already have mongodb uri for host connectivity but that is
 * for the java driver
 *
 * need to distinguish between hosts with auth and no auth.
 * TODO flesh out this class to work for both mongoexport and mongodbUri
 */
public class QueryHostContext {
  private final String host;            // required
  private final String port;            // optional
  private final String user;            // optional
  private final String password;        // optional
  private final String authDB;          // optional
  // String authMechanism;   // optional needed for mongodb uri

  
  
  public QueryHostContext(){
    // setup context by grabbing MongoClientUri
    RuntimeParameters p = RuntimeParameters.getInstance();
    host = p.getQueryHost();
    port = p.getQueryPort();
    user = p.getQueryUser();
    password =  p.getQueryPwd();
    authDB = p.getQueryAuthDB();
    // RuntimeParameters.getQueryAuthMechanism();   // needed for mongodb uri
  }
  
  public Map<String,String> getContext(){
    Map<String,String> context = new HashedMap();
    context.put("host",this.host);
    if(!StringUtils.isEmpty(port)) context.put("port",port);
    if(!StringUtils.isEmpty(user)) context.put("user",user);
    if(!StringUtils.isEmpty(password)) context.put("pwd", password);
    if(!StringUtils.isEmpty(authDB)) context.put("authDB",authDB);
    // if(!StringUtils.isEmpty(authMechanism)) context.put("authMechanism",authMechanism);  // needed for mongodb uri
    return context;
  }
  
}
