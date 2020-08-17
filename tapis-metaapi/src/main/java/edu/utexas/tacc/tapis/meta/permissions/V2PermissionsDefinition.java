package edu.utexas.tacc.tapis.meta.permissions;

import java.util.List;
import java.util.StringTokenizer;

public class V2PermissionsDefinition {
  private static final String service = "meta";
  private String tenant;
  private String role;
  private List<String> ops;
  private String db = "";
  private String collection = "";
  private String docAggrFilter = "";
  
  public V2PermissionsDefinition(){}
  
  /*-------------------------------------------------------
  *                    Public Methods
  * ------------------------------------------------------*/
  
  public boolean isPermitted(V2PermissionsRequest request){
    // we already know the meta and tenant match
    boolean foundRoleMatch = request.getRoleList().contains(role);
    boolean foundOpsMatch = ops.contains(request.getOp());

    int comparisonsToMake = this.getComparisons();
    switch(comparisonsToMake){
      case 0 : {  // no db value
        return foundRoleMatch && foundOpsMatch;
      }
      case 1 : {  // just the db value
        return foundRoleMatch && foundOpsMatch && db.compareTo(request.getDb()) == 0 ? true : false;
      }
      case 2 : {  // a collection value
        return foundRoleMatch && foundOpsMatch && db.compareTo(request.getDb()) == 0 ? true : false &&
            collection.compareTo(request.getCollection()) == 0 ? true : false;
      }
    }
    return false;
  }
  
  private int getComparisons() {
    int compares = 0;
    compares += isDbSet();
    compares += isCollectionSet();
    return compares;
  }
  
  
  public String getTenant() { return tenant; }
  
  public void setTenant(String tenant) { this.tenant = tenant; }
  
  public List<String> getOps() { return ops; }
  
  public void setOps(List<String> ops) { this.ops = ops; }
  
  public String getDb() { return db; }
  
  public void setDb(String db) { this.db = db; }
  
  public String getCollection() { return collection; }
  
  public void setCollection(String collection) { this.collection = collection; }
  
  public String getRole() { return role; }
  
  public void setRole(String role) { this.role = role; }
  
  public String getDocAggrFilter() { return docAggrFilter; }
  
  public void setDocAggrFilter(String docAggrFilter) { this.docAggrFilter = docAggrFilter; }
  
  public int isDbSet(){
    if(!db.isEmpty()){
      return 1;
    }
    return 0;
  }
  
  public int isCollectionSet(){
    if(!collection.isEmpty()){
      return 1;
    }
    return 0;
  }
  
}
