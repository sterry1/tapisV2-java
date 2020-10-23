package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.meta.model.Status;

public class LRQWorkerT {
  public static void main(String[] args) {
    /*------------------------------------------------------------------------
     * 1. use a client to get the query task to execute.
     * 2. update the database to reflect those actions
     * 3. hand that task off to an executor
     * 4. monitor the executor for completion
     * 5. update the db when executor fails or finishes
     * 6. send the notification of completion or failure
     * -----------------------------------------------------------------------*/
    // test setup with _tenant = "vdjserver.org"
    String _tenant = "vdjserver.org";
    LRQWorker lrqWorker = new LRQWorker(_tenant);
    
    // get the task
    LRQTask task = lrqWorker.getTaskFromQueue();
    
    // update db status
    lrqWorker.updateTaskStatus(_tenant, task.get_id(), Status.STARTED);
  
    try {
      lrqWorker.spawnQueryExecutor(task);
    } catch (Exception e) {
      e.printStackTrace();
    }
  
    // update db status
    lrqWorker.updateTaskStatus(_tenant, task.get_id(), Status.FINISHED);
  
    lrqWorker.sendNotification(_tenant,task);
  
  }
}

