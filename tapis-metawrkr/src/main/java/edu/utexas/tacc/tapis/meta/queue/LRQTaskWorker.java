package edu.utexas.tacc.tapis.meta.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.utexas.tacc.tapis.meta.QueryExecutor;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LRQTaskWorker {
  private static final Logger _log = LoggerFactory.getLogger(LRQTaskWorker.class);
  
  private final String TASK_QUEUE_NAME;
  public final String workerName;
  public final String tenantId;
  
  public LRQTaskWorker(String _queueName, String _workerName, String tenantId){
    _log.debug("Tenant ID  constructor : "+tenantId);
    _log.debug("Task Queue Name constructor : "+_queueName);
    _log.debug("Task Worker Name constructor : "+_workerName);
    TASK_QUEUE_NAME = _queueName;
    this.workerName = _workerName;
    this.tenantId = tenantId;
  }
  
  public String getWorkerName(){
    return this.workerName;
  }
  
  public void processTask() throws IOException, TimeoutException {
    RuntimeParameters runtimeP = RuntimeParameters.getInstance();
    ConnectionFactory factory = new ConnectionFactory();
    System.out.println("Task Queue Host runtime : "+runtimeP.getTaskQueueHost());
    System.out.println("Task Queue Name runtime : "+runtimeP.getTaskQueueName());
    factory.setHost(runtimeP.getTaskQueueHost());
    factory.setPort(Integer.parseInt(runtimeP.getTaskQueuePort()));
    factory.setUsername(runtimeP.getTaskQueueUser());
    factory.setPassword(runtimeP.getTaskQueuePassword());
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.basicQos(1);
  
    channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
    System.out.println(" [*] "+this.workerName+" Waiting for messages. To exit press CTRL+C");
  
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
    
      System.out.println(" [x] "+this.workerName+" Received '" + message + "'");
      try {
        doWork(message, this.workerName, tenantId);
      } catch (InterruptedException e) {
        System.out.println(" [x] "+this.workerName+" Interrupted");
      } finally {
        System.out.println(" [x] "+this.workerName+" Done");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      }
    };
    boolean autoAck = false; // acknowledgment is covered below
    channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
  }
  
  private static void doWork(String task, String workerName, String tenantId) throws InterruptedException {
    _log.debug(" have this task :\n"+task+"\n   for worker named "+workerName+" with tenant id "+tenantId);

    
    int interrupted = 0;
    QueryExecutor queryExecutor = new QueryExecutor(task, tenantId);
    // queryExecutor.checkIntegrationWithQueue(workerName);
    queryExecutor.startQueryExecution();
    
  }
}
