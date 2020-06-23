package edu.utexas.tacc.tapis.meta.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import edu.utexas.tacc.tapis.meta.permissions.V2PermissionsDefinition;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;

import java.io.*;
import java.util.Iterator;

public class PermissionDefinitionInit {
  
  /* ---------------------------------------------------------------------------- */
  /* readPermissionsFile :                                                        */
  /* ---------------------------------------------------------------------------- */
  private void readPermissionsFile() throws TapisException
  {
    String permissionsFile = System.getenv("permissions.file");
    
    File file = new File(permissionsFile);
    Gson gson = new Gson();
    V2PermissionsDefinition permDef = null;
    
      // Open, read and close the permissions file.
    JsonArray jsonArray;
    try {
      jsonArray = gson.fromJson(new FileReader(file), JsonArray.class);
      Iterator<JsonElement> it = jsonArray.iterator();
      while(it.hasNext()){
        JsonElement element = it.next();
        String result = gson.toJson(element);
        System.out.println(result);
        permDef = gson.fromJson(result,V2PermissionsDefinition.class);
        System.out.println();
        
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  
  }
  
  public static void main(String[] args) throws TapisException {
    PermissionDefinitionInit init = new PermissionDefinitionInit();
    init.readPermissionsFile();
  }
  
}
