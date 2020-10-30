package edu.utexas.tacc.tapis.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import edu.utexas.tacc.tapis.TestData;

public class NewTask {

  private static final String TASK_QUEUE_NAME = "vdjserver.org";
  
  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
      
      for (int i = 0; i < 10; i++) {
  
        // String message = String.join(" ", ". . "+String.valueOf(i));
        String message = TestData.taskJson;
  
        channel.basicPublish("", TASK_QUEUE_NAME,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
        Thread.sleep(5000);
        
      }
    }
    
  }
}