package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;

import java.util.Map;

public class QueryHostContextT {
  public static void main(String[] args) {
    RuntimeParameters runtimeParameters = RuntimeParameters.getInstance();
    QueryHostContext context = new QueryHostContext();
    
    Map<String,String> contextMap = context.getContext();
    System.out.println(contextMap.toString());
    
  }
}
