package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.client.TaskQueueClient;
import edu.utexas.tacc.tapis.meta.config.BeanstalkConfig;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.meta.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LRQWorker {
  
  private static final Logger _log = LoggerFactory.getLogger(LRQWorker.class);
  private static String tenant = null;
  
  public LRQWorker(String _tenant){
    tenant = _tenant;
  }
  
/* This is not managed manually. It spawns Tasks from RabbitMQ.
  protected LRQTask getTaskFromQueue() {
    _log.trace("... get a task from the queue. ");
    LRQTask task;
    // TODO factory method here to get client for tenant the worker is assigned
    // BeanstalkConfig beanstalkConfig = new BeanstalkConfig();
    // TaskQueueClient taskQClient = new BeanstalkMetaClient(beanstalkConfig, tenant);
    
    // task = taskQClient.getTask();
    
    return task;
  }
*/
  
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
  
  }
}