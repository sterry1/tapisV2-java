package edu.utexas.tacc.tapis.meta;

import com.mongodb.MongoClientURI;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;
import org.apache.commons.collections.map.HashedMap;

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
  String host;
  String port;
  String user;
  String password;
  String authDB;
  String authMechanism;
  String prodHostContext="";
  String devHostContext="";
  Map<String,String> context = new HashedMap();
  
  
  public QueryHostContext(){
    // setup context by grabbing MongoClientUri
    
  }
  
  public Map<String,String> getContext(){
    return context;
  }
  
}
