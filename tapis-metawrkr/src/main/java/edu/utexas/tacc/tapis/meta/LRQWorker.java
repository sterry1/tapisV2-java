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
  
  protected LRQTask getTaskFromQueue() {
    _log.trace("... get a task from the queue. ");
    LRQTask task;
    // TODO factory method here to get client for tenant the worker is assigned
    BeanstalkConfig beanstalkConfig = new BeanstalkConfig();
    TaskQueueClient taskQClient = new BeanstalkMetaClient(beanstalkConfig, tenant);
    
    task = taskQClient.getTask();
    
    return task;
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
    String tenant = "vdjserver.org";
    LRQWorker lrqWorker = new LRQWorker(tenant);
    /*------------------------------------------------------------------------
     * The responsibilities of LRQWrkr
     *
     * 1. use a client to get the query task to execute.
     * 2. update the database to reflect those actions
     * 3. hand that task off to an executor
     * 4. monitor the executor for completion
     * 5. update the db when executor fails or finishes
     * 6. send the notification of completion or failure
     *
     * -----------------------------------------------------------------------*/
    
    // 1. get a task from the queue
    LRQTask lrqTask = lrqWorker.getTaskFromQueue();
    
    // 2. update the lrq submission document in the database
    lrqWorker.updateTaskStatus(tenant,lrqTask.get_id(), Status.STARTED);
    
    // 3. spawn a Query executor with the lrqtask.
    lrqWorker.spawnQueryExecutor(lrqTask);
    
    // 4. monitor the executor for completion or failure.
    // this is where the long running things happen.
    // 4.1 we want to monitor errors and exceptions
    // 4.2 we may want to stop the executor ( some queries and result storage may take hours ).
    // 4.3 how do we cleanup in case of failure?
    lrqWorker.monitorTaskExecution();
    
    // 5. We are finished or failed
    lrqWorker.updateTaskStatus(tenant,lrqTask.get_id(), Status.FINISHED);
    
    // 6. Send out notification on status of lrqtask.
    lrqWorker.sendNotification(tenant,lrqTask);
  }
  

}