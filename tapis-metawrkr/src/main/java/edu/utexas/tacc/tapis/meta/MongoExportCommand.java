package edu.utexas.tacc.tapis.meta;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MongoExportCommand {
  // there is a --sort flag as well
  // dev  example : "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
  // prod example : "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -d=v1airr -c=rearrangement -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'" | gzip > {{fileOutput}}";
  private final static String template = "mongoexport --quiet -h={{host}}:{{port}} {{user}} {{password}} {{authDB}} -d={{db}} -c={{collection}}  {{fields}} {{query}} {{fileOutput}}";
  private String hostFlag = "-h=";        // required
  private String portFlag = ":";          // required
  private String userFlag = "-u=";        // optional if no auth
  private String passwordFlag = "-p=";    // optional if no auth
  private String authDbFlag = "";         // optional --authenticationDatabase=admin always the same if present
  private String dbFlag = "-d=";          // required
  private String collectionFlag = "-c=";  // required
  private String fileOutput = "";         // required
  private String fields = "";
  private String query = "";              // required
  private Map<String,String> mustacheMap = new HashedMap();
  public boolean isReady = false;
  
  public MongoExportCommand(Map<String,String> cmdMap){
    StringBuilder sb = new StringBuilder();
    cmdMap.forEach((k, v) -> {
      switch (k) {
        case "host" :
          mustacheMap.put("host", v);
          break;
        case "port" :
          mustacheMap.put("port", v);
          break;
        case "user" :
          if(StringUtils.isEmpty(v)){
            mustacheMap.put("user", new String(""));
          }else {
            mustacheMap.put("user", new String("-u="+v));
          }
          break;
        case "password" :
          if(StringUtils.isEmpty(v)){
            mustacheMap.put("password", new String(""));
          }else {
            mustacheMap.put("password", new String("-p="+v));
          }
          break;
        case "authDB" :   // if filled this is always admin
          if(StringUtils.isEmpty(v)){
            mustacheMap.put("authDB", new String(""));
          }else {
            mustacheMap.put("authDB", new String("--authenticationDatabase=admin"));
          }
          break;
        case "db" :
          mustacheMap.put("db", v);
          break;
        case "collection" :
          mustacheMap.put("collection", v);
          break;
        case "fileOutput" :
          mustacheMap.put("fileOutput", v);
          break;
        case "fields" :
          if(StringUtils.isEmpty(v)){
            mustacheMap.put("fields", new String(""));
          }else {
            mustacheMap.put("fields", new String("-f=\""+v+"\""));
          }
          break;
        case "query" :
          if(StringUtils.isEmpty(v)){
            mustacheMap.put("query", new String(""));
          }else {
            mustacheMap.put("query",new String("-q='"+v+"'"));
          }
          break;
      }
    });

    isReady = true;
  }
  
  public String exportCommandAsString() {
    String result="";
    if (isReady){
      String cmd = MongoExportCommand.getTemplate();
      Template tmpl = Mustache.compiler().escapeHTML(false).compile(cmd);
      result = tmpl.execute(mustacheMap);
    }
    return result;
  }
  
  
  
  
  /*------------------------------------------------------------------------
   *                              Getters and Setters
   * -----------------------------------------------------------------------*/
  
  public static String getTemplate() {
    return template;
  }
  
  public String getHostFlag() { return hostFlag; }
  
  public void setHostFlag(String _host) {
    this.hostFlag = hostFlag.concat(hostFlag);
  }
  
  public String getPortFlag() { return portFlag; }
  
  public void setPortFlag(String portFlag) { this.portFlag = portFlag; }
  
  public String getUserFlag() {
    return userFlag;
  }
  
  public void setUserFlag(String _user) {
    this.userFlag = this.userFlag.concat(userFlag);
  }
  
  public String getPasswordFlag() {
    return passwordFlag;
  }
  
  public void setPasswordFlag(String _password) {
    this.passwordFlag = this.passwordFlag.concat(_password);
  }
  
  public String getDbFlag() {
    return dbFlag;
  }
  
  public void setDbFlag(String _db) {
    this.dbFlag = dbFlag;
  }
  
  public String getCollectionFlag() {
    return collectionFlag;
  }
  
  public void setCollectionFlag(String _collection) {
    this.collectionFlag = collectionFlag;
  }
  
  public String getFileOutput() {
    return fileOutput;
  }
  
  public void setFileOutput(String _fileOutput) {
    this.fileOutput = "-o=".concat(_fileOutput);
  }
  
  public String getFields() {
    return fields;
  }
  
  public void setFields(String _fields) {
    this.fields = "-f=".concat(_fields);
  }
  
  public String getQuery() {
    return query;
  }
  
  public void setQuery(String _query) {
    this.query = "-q=".concat(_query);
  }
  
}