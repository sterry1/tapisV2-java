package edu.utexas.tacc.tapis.meta.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LRQTaskWorker {
  private final String TASK_QUEUE_NAME;
  public final String workerName;
  
  public LRQTaskWorker(String _queueName, String _workerName){
    TASK_QUEUE_NAME = _queueName;
    this.workerName = _workerName;
  }
  
  public String getWorkerName(){
    return this.workerName;
  }
  
  public void processTask() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
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
    for (char ch: task.toCharArray()) {
      if (ch == '.') {
        try {
          System.out.println(workerName+": I'm working here ..");
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
