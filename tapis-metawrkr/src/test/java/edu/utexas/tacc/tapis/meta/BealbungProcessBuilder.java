package edu.utexas.tacc.tapis.meta;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

public class BealbungProcessBuilder {
  
  public static void main(String[] args) {
    // ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "echo hello");
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("bash", "-c", "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'");

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
    
    
    assertEquals("No errors should be detected", 0, exitCode);
  }
}
