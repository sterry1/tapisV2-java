package edu.utexas.tacc.tapis.meta;

import java.io.IOException;

public class MongoExportExecutorT {
  public static void main(String[] args) {
    String mongoexportCmd = "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("bash", "-c", mongoexportCmd);
    
    processBuilder.inheritIO();
    Process process = null;
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    int exitCode = 0;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    if(exitCode == 0){
      System.out.println("Success exitCode : "+exitCode);
    }else{
      System.out.println("ERROR something went wrong exitCode : "+exitCode);
    }
    
  }
  
}
