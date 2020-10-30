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
  
    // 1. get a task from the queue
    //LRQTask task = lrqWorker.getTaskFromQueue();
    //assert task.get_id() != null : "Task is null";
  
  
  // 2. update the lrq submission document in the database
    //lrqWorker.updateTaskStatus(_tenant, task.get_id(), Status.STARTED);
  
    // 3. spawn a Query executor with the lrqtask.
    try {
      //lrqWorker.spawnQueryExecutor(task);
    } catch (Exception e) {
      e.printStackTrace();
    }
  
    // 4. monitor the executor for completion or failure.
    // this is where the long running things happen.
    // 4.1 we want to monitor errors and exceptions
    // 4.2 we may want to stop the executor ( some queries and result storage may take hours ).
    // 4.3 how do we cleanup in case of failure?
    lrqWorker.monitorTaskExecution();
  
  
    // 5. We are finished or failed
    //lrqWorker.updateTaskStatus(_tenant, task.get_id(), Status.FINISHED);
  
    // 6. Send out notification on status of lrqtask.
    //lrqWorker.sendNotification(_tenant,task);
  
  }
}

