package edu.utexas.tacc.tapis.meta.permissions;

import java.util.ArrayList;

public class RolesToResourceMapRunner {
  public static void main(String[] args) {
    V2PermissionsRegistry rolesToResourceMap = V2PermissionsRegistry.getInstance();
    String permSpec = null;
    String user = null;
    ArrayList<String> rolesList = null;
    
   
    System.out.println();
    
    // RolesToResourceMap.isPermitted(permSpec,user,rolesList);
  }
}
