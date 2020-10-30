package edu.utexas.tacc.tapis.rabbit;

import edu.utexas.tacc.tapis.meta.queue.LRQTaskWorker;
import org.apache.commons.collections.map.HashedMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class LRQTaskWorkerT {
  public Map<String, LRQTaskWorker> workerList = new HashedMap();
  
  public static void main(String[] args) {
    LRQTaskWorkerT t = new LRQTaskWorkerT();
    String taskQName = "vdjserver.org";
    List<String> names = new ArrayList<String>(Arrays.asList("worker1","worker2","worker3"));
    LRQTaskWorker worker = null;
    names.forEach(name -> {
      System.out.println(name.toLowerCase());
        t.workerList.put(name,new LRQTaskWorker(taskQName, name));
    });
  
    System.out.println("We have a list of workers to deal with.");
    t.workerList.forEach((name,lrqTaskWorker) ->{
      try {
        lrqTaskWorker.processTask();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    });
    
    
    
    System.out.println("\nAll done here.");
    
  }
}
