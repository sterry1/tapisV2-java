package edu.utexas.tacc.tapis.meta.utils;

import java.util.StringTokenizer;

public class MetaSKPermissionsMapper {
  private String uriPath;
  private String meta = "meta";
  private String op = "GET";
  private String tenant;
  private String db="";
  private String collection="";
  private String document="";
  private String permSpec="";
  
  // public String
  
  /**
   * constructor for uripath mapping to sk permission spec
   * @param _uriPath   ie. /v3/meta/"db"/"collection"/"document"
   * @param _tenant    tenant id of the user for checking permissions
   */
  public MetaSKPermissionsMapper(String _uriPath, String _tenant){
    this.uriPath = _uriPath;
    this.tenant = _tenant;
  }
  
  /**
   * Converts the uri path into Shiro permissions format to use in an isPermitted
   * check against the SK.
   * @return  Shiro formatted permission string
   * @param op
   */
  public String convert(String op){
    String pems = "";
    if(notNull(uriPath )&& notEmpty(tenant)){
      String processed = uriPath.replace(MetaAppConstants.V2_URI_BASEPATH, "");
      
      StringTokenizer st = new StringTokenizer(processed,"/");
      
      int resources = st.countTokens();
      switch(resources){
        case 0 : {
          pems = meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
          this.permSpec = pems;
          return pems;
        }
        case 1 : {
          db = st.nextToken();
          pems = meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
          this.permSpec = pems;
          return pems;
        }
        case 2 : {
          db = st.nextToken();
          collection = st.nextToken();
          pems = meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
          this.permSpec = pems;
          return pems;
        }
        case 3 :
        case 4 : {
          db = st.nextToken();
          collection = st.nextToken();
          document = st.nextToken();
          pems = meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
          this.permSpec = pems;
          return pems;
        }
      }
    }
    return pems;
  }
  
  /**
   * Simple check to see if header values returned actually have something in them
   * @param str
   * @return true or false
   */
  private static boolean notEmpty(String str){
    if (str != null && !str.trim().isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
  
  private static boolean notNull(String str){
    if (str != null ) {
      return true;
    } else {
      return false;
    }
  }
  
  public String getMeta() { return meta; }
  
  public void setMeta(String meta) { this.meta = meta; }
  
  public String getOp() { return op; }
  
  public void setOp(String op) { this.op = op; }
  
  public String getTenant() { return tenant; }
  
  public void setTenant(String tenant) { this.tenant = tenant; }
  
  public String getDb() { return db; }
  
  public void setDb(String db) { this.db = db; }
  
  public String getCollection() { return collection; }
  
  public void setCollection(String collection) { this.collection = collection; }
  
  public String getDocument() { return document; }
  
  public void setDocument(String document) { this.document = document; }
  
  public String getPermSpec() { return permSpec; }
  
  public void setPermSpec(String permSpec) { this.permSpec = permSpec; }
}
