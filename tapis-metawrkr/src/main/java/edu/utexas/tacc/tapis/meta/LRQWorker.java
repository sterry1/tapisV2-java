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
  
  protected void monitorTaskExecution() {
    _log.trace("... monitor task execution. ");
  }
  
  public static void main(String[] args) {
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    
    StringBuilder buf = new StringBuilder(2500); // capacity to avoid resizing
    buf.append("\n------- Starting Meta Worker ");
    buf.append(" -------");
  
    runtime.getRuntimeInfo(buf);
    buf.append("\n---------------------------------------------------\n");
    // Write the output information.
    _log.debug(buf.toString());
    
    
    // TODO pull the tenant from the environment
    
    LRQWorker lrqWorker = new LRQWorker(runtime.getTenantId());
    _log.debug("LRQWorker start :");
    _log.debug("LRQWorker  tenant Id : "+runtime.getTenantId());
    _log.debug("LRQWorker  queue name : "+runtime.getTaskQueueName());
    
    
    
    // which queue do we want to use
    String taskQName = runtime.getTaskQueueName();
    LRQTaskWorker worker = null;
    
    // Startup an array of workers
    List<String> names = new ArrayList<String>();
    for (int i = 0; i < runtime.getTaskQueueWorkers(); i++) {
      names.add("worker"+i);
    }
    
    // fill our worker list with new workers
    // TODO no management of list here, no checking on workers ect.
    names.forEach(name -> {
      _log.debug(name.toLowerCase());
      lrqWorker.workerList.put(name,new LRQTaskWorker(taskQName, name, lrqWorker.tenant ));
    });
  
    _log.debug("   We have a list of workers to deal with.");
    lrqWorker.workerList.forEach((name,lrqTaskWorker) ->{
      try {
        lrqTaskWorker.processTask();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    });
    _log.debug("\nAll done here.");
  
  
  }
}