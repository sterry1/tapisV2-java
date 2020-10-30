package edu.utexas.tacc.tapis.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/*
 *
 */
public class WorkerJohn {
  
  // **** name for our queue ****
  private final static String TASK_QUEUE_NAME = "task_queue";
  
  /*
   * constructor
   */
  public WorkerJohn() {
  }
  
  /*
   *
   */
  public void processWork() throws IOException, TimeoutException {
    
    // **** connect to RabbitMQ ****
    ConnectionFactory factory = new ConnectionFactory();
    
    // **** default host to use for connections ****
    factory.setHost("localhost");
    
    // **** ****
    final Connection connection = factory.newConnection();
    
    // **** ****
    final Channel channel = connection.createChannel();
    
    // **** declare a durable queue ****
    boolean durable = true;
    channel.queueDeclare(TASK_QUEUE_NAME,        // name of queue
        durable,                // durable
        false,                  // exclusive
        false,                  // auto delete
        null);                  // construction arguments for the queue
    
    // **** ****
    System.out.println("processWork <<< [*] waiting for messages (to exit press CTRL+C)"); // **** do not to give more than one message to a worker at a time **** int prefetchCount = 1; channel.basicQos(prefetchCount); // **** **** DeliverCallback deliverCallback = (consumerTag, delivery) -> {
    
    // **** ****
    // String message = new String(delivery.getBody(), "UTF-8");
    
    // **** ****
    // System.out.println("processWork <<< [x] received ==>" + message + "<==");
    
    // **** ****
    try {
      // doWork(message);
      doWork("Hello ...");
    } finally {
      // **** ****
      System.out.println("processWork <<< [x] Done"); // **** **** channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false); } }; // **** **** boolean autoAck = false; channel.basicConsume( TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    }
    
  }
  
  /*
   * fake task to simulate execution time
   */
  private static void doWork (String task){
    
    // **** loop working ****
    int i = 0;
    for (char ch : task.toCharArray()) {
      if (ch == '.') {
        try {
          System.out.println("doWork <<< working " + (i++) + " ...");
          Thread.sleep(1000);
        } catch (InterruptedException _ignored) {
          Thread.currentThread().interrupt();
        }
      }
    }
    
    // **** we are done ****
    System.out.println("doWork <<< done !!!");
  }
}