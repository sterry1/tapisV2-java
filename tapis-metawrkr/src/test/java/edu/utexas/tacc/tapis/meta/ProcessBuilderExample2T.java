package edu.utexas.tacc.tapis.meta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ProcessBuilderExample2T {
  
  public static void main(String[] args) {
    
    ExecutorService pool = Executors.newSingleThreadExecutor();
    
    ProcessBuilder processBuilder = new ProcessBuilder();
    
    // Run this on Windows, cmd, /c = terminate after this run
    processBuilder.command("bash", "-c", "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'");
    // processBuilder.command("bash", "-c", "ping -c 3 google.com");
    
    try {
      
      Process process = processBuilder.start();
      
      System.out.println("process ping...");
      ProcessReadTask task = new ProcessReadTask(process.getInputStream());
      Future<List<String>> future = pool.submit(task);
      
      // no block, can do other tasks here
      System.out.println("process task1...");
      System.out.println("process task2...");
      
      List<String> result = future.get(5, TimeUnit.SECONDS);
      for (String s : result) {
        System.out.println(s);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.shutdown();
    }
  }
  
  private static class ProcessReadTask implements Callable<List<String>> {
    
    private InputStream inputStream;
    
    public ProcessReadTask(InputStream inputStream) {
      this.inputStream = inputStream;
    }
    
    @Override
    public List<String> call() {
      return new BufferedReader(new InputStreamReader(inputStream))
          .lines()
          .collect(Collectors.toList());
    }
  }
  
}