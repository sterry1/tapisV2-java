package edu.utexas.tacc.tapis.meta.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.utexas.tacc.tapis.meta.QueryExecutor;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LRQTaskWorker {
  private final String TASK_QUEUE_NAME;
  public final String workerName;
  
  public LRQTaskWorker(String _queueName, String _workerName){
    System.out.println("Task Queue Name constructor : "+_queueName);
    System.out.println("Task Worker Name constructor : "+_workerName);
    TASK_QUEUE_NAME = _queueName;
    this.workerName = _workerName;
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
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.basicQos(1);
  
    channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] "+this.workerName+" Waiting for messages. To exit press CTRL+C");
  
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
    
      System.out.println(" [x] "+this.workerName+" Received '" + message + "'");
      try {
        doWork(message, this.workerName);
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
  
  private static void doWork(String task, String workerName) throws InterruptedException {
    System.out.println(" got this task to do ");
    System.out.println("    but going to sleep instead.  yaaawwwnnn.");
    
    int interrupted = 0;
    QueryExecutor queryExecutor = new QueryExecutor(task);
    // queryExecutor.checkIntegrationWithQueue(workerName);
    queryExecutor.startQueryExecution();
    
/*
    for (char ch: task.toCharArray()) {
      if (ch == '.') {
        try {
          System.out.println(workerName+": I'm sleeping here ..");
          Thread.sleep(60000);
        } catch (InterruptedException e) {
          System.out.println("     catch thread interrupted count .. "+interrupted++);
          Thread.currentThread().interrupt();
        }
      }
    }
*/
  }
}
