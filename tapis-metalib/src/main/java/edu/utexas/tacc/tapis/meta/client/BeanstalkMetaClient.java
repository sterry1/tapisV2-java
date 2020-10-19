package edu.utexas.tacc.tapis.meta.client;

import com.dinstone.beanstalkc.BeanstalkClientFactory;
import com.dinstone.beanstalkc.Job;
import com.dinstone.beanstalkc.JobConsumer;
import com.dinstone.beanstalkc.JobProducer;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.meta.config.BeanstalkConfig;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanstalkMetaClient implements TaskQueueClient {
  
  private static final Logger _log = LoggerFactory.getLogger(BeanstalkMetaClient.class);
  
  private BeanstalkConfig beanstalkConfig;
  private JobProducer producer;
  private JobConsumer consumer;
  private int priority = 1;
  private int delay = 0;
  private int ttr = 5000;
  
  /**
   * Constructor that sets up the Beanstalk client
   * @param _beanstalkConfig  Connection settings
   * @param _tenant   the tenant id names our task queue Tube
   */
  public BeanstalkMetaClient(BeanstalkConfig _beanstalkConfig, String _tenant){
    beanstalkConfig = _beanstalkConfig;
    
    BeanstalkClientFactory factory = new BeanstalkClientFactory(beanstalkConfig.getConfig());
    producer = factory.createJobProducer(_tenant);
    consumer = factory.createJobConsumer(_tenant);
  }
  
  @Override
  public void putTask(String taskJson) {
    // putJob <priority>, <delay>, <time to reserve>, <bytes> <data>  number of bytes and the byte[] for data.
    try {
      producer.putJob(priority,delay,ttr, taskJson.getBytes());
    } catch (Exception e) {
      // TODO there are a number of exceptions and errors that can occur here
      _log.error("We failed to put the task on the queue for some reason.");
      e.printStackTrace();
    }
  
  }
  
  @Override
  public LRQTask getTask() {
  // TODO when we pull this task off the queue to work it
    Job job;
    LRQTask task = null;
    try {
      job = consumer.reserveJob(1);
      task = jobToTask(job);
    } catch (Exception e) {
      // TODO there are a number of exceptions and errors that can occur here
      _log.error("We failed to get a task from the queue for some reason.");
      e.printStackTrace();
    }
    return task;
  }
  
  @Override
  public void close() {
    if(producer != null){
      producer.close();
    }
    if(consumer != null){
      consumer.close();
    }
  }
  
  public BeanstalkConfig getBeanstalkConfig() {
    return beanstalkConfig;
  }
  
  private LRQTask jobToTask(Job job){
    LRQTask task = null;
    ConversionUtils conversionUtils = new ConversionUtils();
    JsonObject jsonObject = conversionUtils.byteArrayToJsonObject(job.getData());
    LRQSubmission submission = conversionUtils.jsonObjectToLRQSubmission(jsonObject);
    return task;
  }
  
}
