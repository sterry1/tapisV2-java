package edu.utexas.tacc.tapis.meta.permissions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.shared.exceptions.runtime.TapisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class V2PermissionsRegistry {
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(V2PermissionsRegistry.class);
  
  private static Map<String, ArrayList<V2PermissionsDefinition>> map = new HashMap<String, ArrayList<V2PermissionsDefinition>>();
  private static V2PermissionsRegistry INSTANCE;
  
  private V2PermissionsRegistry(){
    // initialize our registry of permissions by tenant
    initPermissions();
  }
  
  public static V2PermissionsRegistry getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new V2PermissionsRegistry();
    }
    
    return INSTANCE;
  }
  
  enum OP {
    GET, PUT, POST, PATCH, DELETE
  }

  private void fillMap() {
    // here comes file reading code with loop
    // ex. permissions spec meta:master:GET:StreamsTACCDB:wq_demo_project:
    // this is currently hard coded to test feasiblility
    // master tenant for key
    // the operation, one of GET,PUT,POST,PATCH and DELETE
    String devTenant = "dev";
    String vdjTenant = "vdjserver.org";
    String dsTenant = "designsafe";
    String vdjAdminRole = "Internal/vdjserver-org-services-admin";
    String vdjUser = "Internal/everyone";
    String  dsUser = "Internal/designsafe";
    String  dsAdmin= "Internal/designsafe-services-admin";

    
    ArrayList<V2PermissionsDefinition> permsList = new ArrayList<V2PermissionsDefinition>();
    V2PermissionsDefinition mpd = new V2PermissionsDefinition();
    // everyone role, for tenant dev and db v1airr all of these must be true along with a GET op
    // for this to work.
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString())));
    mpd.setRole(vdjUser); // everyone
    mpd.setTenant(vdjTenant);
    mpd.setDb("v1airr");
    permsList.add(mpd);
  
    // role of vdjAdmin, for tenant dev and db v1airr
    mpd = new V2PermissionsDefinition();
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString(),OP.PUT.toString(),OP.POST.toString(),OP.PATCH.toString(),OP.DELETE.toString())));
    mpd.setRole(vdjAdminRole);
    mpd.setTenant(vdjTenant); // dev
    mpd.setDb("v1airr");
    permsList.add(mpd);
  
    map.put(vdjTenant, permsList);
  
    permsList = new ArrayList<V2PermissionsDefinition>();
    mpd = new V2PermissionsDefinition();
    // everyone role, for tenant dev and db v1airr all of these must be true along with a GET op
    // for this to work.
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString())));
    mpd.setRole(vdjUser); // everyone
    mpd.setTenant(vdjTenant);
    mpd.setDb("v1airr");
    permsList.add(mpd);
  
    // role of vdjAdmin, for tenant dev and db v1airr
    mpd = new V2PermissionsDefinition();
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString(),OP.PUT.toString(),OP.POST.toString(),OP.PATCH.toString(),OP.DELETE.toString())));
    mpd.setRole(vdjAdminRole);
    mpd.setTenant(vdjTenant); //
    mpd.setDb("v1airr");
    permsList.add(mpd);
  
    map.put(devTenant, permsList);
  
    permsList = new ArrayList<V2PermissionsDefinition>();
    // role of dsUser , for tenant dev and db DSdbs
    mpd = new V2PermissionsDefinition();
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString())));
    mpd.setRole(dsUser);
    mpd.setTenant(dsTenant); // dev
    mpd.setDb("DSdbs");
    permsList.add(mpd);
  
    // role of dsAdmin , for tenant dev and db DSdbs
    mpd = new V2PermissionsDefinition();
    mpd.setOps(new ArrayList<String>(Arrays.asList(OP.GET.toString(),OP.PUT.toString(),OP.POST.toString(),OP.PATCH.toString(),OP.DELETE.toString())));
    mpd.setRole(dsAdmin);
    mpd.setTenant(dsTenant); // dev
    mpd.setDb("DSdbs");
    permsList.add(mpd);
    
    map.put(dsTenant, permsList);
    System.out.println();
  }
  
  /* ---------------------------------------------------------------------------- */
  /* initPermissions :                                                        */
  /* ---------------------------------------------------------------------------- */
  private void initPermissions() {
    _log.debug("Initializing permissions ...");
    String permissionsFile = System.getenv("tapis.meta.security.permissions.file");
    ArrayList<V2PermissionsDefinition> permsList = null;
    
    if(permissionsFile.isEmpty()){
      // of course it's empty it is the first time in
      // TODO log no permissions file
    }
    
    File file = new File(permissionsFile);
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      
    } catch (FileNotFoundException e) {
      // T
      String msg = "Permissions definition file not found "
          + RuntimeParameters.SERVICE_NAME_META +"\n"
          + e.getMessage();
      _log.error(msg);
      throw new TapisRuntimeException(msg, e);
    }
    
    Gson gson = new Gson();
    V2PermissionsDefinition permDef = null;
  
    // Open, read and close the permissions file.
    JsonArray jsonArray;
    jsonArray = gson.fromJson(reader, JsonArray.class);
    Iterator<JsonElement> it = jsonArray.iterator();
    while (it.hasNext()) {
      JsonElement element = it.next();
      String result = gson.toJson(element);
      permDef = gson.fromJson(result, V2PermissionsDefinition.class);
      // get the tenant from permDef and add to map of tenant permissions
      String tenant = permDef.getTenant();
      // if tenant key not in map already add the key and empty permissionsList to tenant permissions.
      if(map.containsKey(tenant)){
        // if tenant key already in map add the permDef
        map.get(tenant).add(permDef);
      }else{
        // if tenant key not in map already add the key and empty permissionsList to tenant permissions
        // and add definition to the tenant permsList
        map.put(tenant,new ArrayList<V2PermissionsDefinition>());
        map.get(tenant).add(permDef);
      }
    }
  }
  
  //
   public static boolean isPermitted(V2PermissionsRequest v2PermissionsRequest){
     // Our permission request has the request path and valid roles for the user
     //  we compare the permissions request object to our
     // list of permissions by tenant (ex. dev) and iterate over our permissions
     // by checking the role of each permission definition against the roles in the users rolelist
     // with role Internal/vdj until
     // we need to search the users role list for a valid role in this tenant.
     // the valid role will be found from the list of permission definitions for the tenant.
     
     ArrayList<V2PermissionsDefinition> permissionsList = map.get(v2PermissionsRequest.getTenant());
     
     // we should have a list of permissions for a tenant
     // check the role defined in the permission definition against our user roleList
  
     // An AND list of comparisons that result in a boolean for isPermitted.
     // 1. get a list of definitions that match the role (may need to iterate)
     // 2. does the request op match anything in the op list for the definition (only one match)
     // 3. do the db's match
     // ArrayList<V2PermissionsDefinition> definitions =  getDefinitionsByRole();
     // possible iteration over list
     //if(definitions != null || !definitions.isEmpty()){
     //  definitions.forEach(matchingDefinition -> {
     // there is at least one matching definition
     //  });
     //}
     boolean isPermitted = false;
     if(permissionsList == null || permissionsList.isEmpty()){
       _log.debug("No permissions found for "+v2PermissionsRequest.getTenant()+" tenant.");
       return isPermitted;
     }
     
     for (V2PermissionsDefinition permDefinition : permissionsList) {
       System.out.println(permDefinition.getRole());
       boolean foundPermitted = permDefinition.isPermitted(v2PermissionsRequest);
       // we always return with the first permitted action that matches our request.
       // there may be more but they will never be evaluated.
       if (foundPermitted) {
         return foundPermitted;
       }
     }
     return isPermitted;
   }
   
}
