package edu.utexas.tacc.tapis.meta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QueryExecutorT {
  public static void main(String[] args) {
    
    ProcessBuilder processBuilder = new ProcessBuilder();
    String mongoexportCmd = "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
  
    processBuilder.command("bash", "-c", mongoexportCmd);
    
    try {
      
      Process process = processBuilder.start();
      
      // blocked :(
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));
      
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      
      int exitCode = process.waitFor();
      System.out.println("\nExited with error code : " + exitCode);
      
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
  }
  
}
