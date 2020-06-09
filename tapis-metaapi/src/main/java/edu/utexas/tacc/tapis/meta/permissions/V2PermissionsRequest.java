package edu.utexas.tacc.tapis.meta.permissions;

import edu.utexas.tacc.tapis.meta.api.jaxrs.filters.MetaPermissionsRequestFilter;
import edu.utexas.tacc.tapis.meta.utils.MetaSKPermissionsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class V2PermissionsRequest {
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(V2PermissionsRequest.class);
  
  private static String service = "meta";
  private String tenant;
  private List<String> roleList;
  private String op;
  private String db;
  private String collection;
  private String permSpec;
  
  public V2PermissionsRequest(MetaSKPermissionsMapper mapper, List<String> _roleList) {
    this.roleList = _roleList;
    this.tenant = mapper.getTenant();
    this.op = mapper.getOp();
    this.db = mapper.getDb();
    this.collection = mapper.getCollection();
    this.permSpec = mapper.getPermSpec();
  }
  
/*
  public boolean isPermitted(String role, String permSpec){
    
    return false;
  }
  
  private void unPackPermSpec(String permSpec){
    StringTokenizer stringTokenizer = new StringTokenizer(permSpec, ":");
    // while()
  }
*/
  
  public String getTenant() { return tenant; }
  
  public void setTenant(String tenant) { this.tenant = tenant; }
  
  public String getOp() { return op; }
  
  public void setOp(String op) { this.op = op; }
  
  public String getDb() { return db; }
  
  public void setDb(String db) { this.db = db; }
  
  public String getCollection() { return collection; }
  
  public void setCollection(String collection) { this.collection = collection; }
  
  public List<String> getRoleList() { return roleList; }
  
  public void setRoleList(List<String>roleList) { this.roleList = roleList; }
  
  public String getPermSpec() { return permSpec; }
  
  public void setPermSpec(String permSpec) { this.permSpec = permSpec; }
}
