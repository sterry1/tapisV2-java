package edu.utexas.tacc.tapis.meta;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MongoExportCommand {
  // there is a --sort flag as well
  // "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
  private final static String template = "mongoexport --quiet -h={{hostPort}} -u={{user}} -p={{password}} --authenticationDatabase=admin -d={{db}} -c={{collection}}  {{fields}} {{query}}  | gzip > {{fileOutput}}";
  private String hostPortFlag = "-h=";
  private String userFlag = "-u=";
  private String passwordFlag = "-p=";
  private String dbFlag = "-d=";
  private String collectionFlag = "-c=";
  private String fileOutput = "-o+=";
  private String fields = "";
  private String query = "";
  private Map<String,String> mustacheMap = new HashedMap();
  public boolean isReady = false;
  
  public MongoExportCommand(Map<String,String> cmdMap){
    StringBuilder sb = new StringBuilder();
    cmdMap.forEach((k, v) -> {
      switch (k) {
        case "hostPort" :
          mustacheMap.put("hostPort", v);
          break;
        case "user" :
          mustacheMap.put("user", v);
          break;
        case "password" :
          mustacheMap.put("password", v);
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
  
  public String getHostPortFlag() {
    return hostPortFlag;
  }
  
  public void setHostPortFlag(String _hostPort) {
    this.hostPortFlag = hostPortFlag.concat(hostPortFlag);
  }
  
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
  
  public static void main(String[] args) {
    // "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    Map<String,String> params = new HashMap<>();
    params.put("hostPort","aloe-dev08.tacc.utexas.edu:27019");
    params.put("user","tapisadmin");
    params.put("password","d3f@ult");
    params.put("db","v1airr");
    params.put("collection","rearrangement");
    params.put("fileOutput","onejson.json");
    params.put("fields","repertoire_id,locus");
    params.put("query","{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}");
  
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
    
    System.out.println(mec.exportCommandAsString());
  }
}