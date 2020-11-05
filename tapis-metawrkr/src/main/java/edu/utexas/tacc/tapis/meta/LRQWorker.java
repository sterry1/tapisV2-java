package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.meta.model.LRQStatus;
import edu.utexas.tacc.tapis.meta.queue.LRQTaskWorker;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class LRQWorker {
  
  private static final Logger _log = LoggerFactory.getLogger(LRQWorker.class);
  public Map<String, LRQTaskWorker> workerList = new HashedMap();
  public final String tenant;
  
  public LRQWorker(String _tenant){
    this.tenant = _tenant;
  }
  
  protected void updateTaskStatus(String _tenant, String _id, LRQStatus status) {
    _log.trace("... update the db with status info. ");
    LRQSubmissionDAO dao = new LRQSubmissionDAOImpl(_tenant);
  }
  
  protected void spawnQueryExecutor(LRQTask lrqTask) {
    _log.trace("TODO ... ");
    _log.trace("... spawn a query executor with the task. ");
  }
  
  protected void monitorTaskExecution() {
    _log.trace("... monitor task execution. ");
  }
  
  protected void sendNotification(String tenant, LRQTask task) {
    _log.trace("... send notification of done or failure. ");
  }
  
  
  
  public static void main(String[] args) {
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    
    StringBuilder buf = new StringBuilder(2500); // capacity to avoid resizing
    buf.append("\n------- Starting Meta Worker ");
    buf.append(" -------");
  
    runtime.getRuntimeInfo(buf);
    buf.append("\n---------------------------------------------------\n");
    // Write the output information.
    System.out.println(buf.toString());
    
    // TODO pull the tenant from the environment
    
    LRQWorker lrqWorker = new LRQWorker(runtime.getTenantId());
    System.out.println("LRQWorker start :");
    System.out.println("LRQWorker  tenant Id : "+runtime.getTenantId());
    System.out.println("LRQWorker  queue name : "+runtime.getTaskQueueName());
    
    // which queue do we want to use
    
    String taskQName = runtime.getTaskQueueName();
    LRQTaskWorker worker = null;
    
    // Startup an array of workers
    List<String> names = new ArrayList<String>(Arrays.asList("worker1"));
    
    // fill our worker list with new workers
    // TODO no management of list here, no checking on workers ect.
    names.forEach(name -> {
      System.out.println(name.toLowerCase());
      lrqWorker.workerList.put(name,new LRQTaskWorker(taskQName, name, lrqWorker.tenant ));
    });
  
    System.out.println("   We have a list of workers to deal with.");
    lrqWorker.workerList.forEach((name,lrqTaskWorker) ->{
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