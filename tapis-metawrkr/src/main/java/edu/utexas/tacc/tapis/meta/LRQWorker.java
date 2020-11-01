package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.client.TaskQueueClient;
import edu.utexas.tacc.tapis.meta.config.BeanstalkConfig;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.meta.model.Status;
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
  private static String tenant = null;
  
  public LRQWorker(String _tenant){
    tenant = _tenant;
  }
  
  protected void updateTaskStatus(String _tenant, String _id, Status status) {
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
    // TODO pull the tenant from the environment
    LRQWorker lrqWorker = new LRQWorker("vdjserver.org");
    System.out.println("LRQWorker start :");
    System.out.println("LRQWorker  queue name : "+runtime.getTaskQueueName());
    String taskQName = runtime.getTaskQueueName();
    // List<String> names = new ArrayList<String>(Arrays.asList("worker1","worker2","worker3"));
    List<String> names = new ArrayList<String>(Arrays.asList("worker1", "worker2"));
    LRQTaskWorker worker = null;
    names.forEach(name -> {
      System.out.println(name.toLowerCase());
      lrqWorker.workerList.put(name,new LRQTaskWorker(taskQName, name));
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