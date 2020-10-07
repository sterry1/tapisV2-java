package edu.utexas.tacc.tapis.meta.client;

import edu.utexas.tacc.tapis.meta.model.LRQTask;

public interface TaskQueueClient {
  public void putTask(String taskJson);
  public LRQTask getTask();
  public void close();
}
