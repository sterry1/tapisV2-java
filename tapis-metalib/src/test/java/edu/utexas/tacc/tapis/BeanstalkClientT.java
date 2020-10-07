package edu.utexas.tacc.tapis;


import com.dinstone.beanstalkc.Job;
import edu.utexas.tacc.tapis.meta.client.BeanstalkMetaClient;
import edu.utexas.tacc.tapis.meta.config.BeanstalkConfig;
import edu.utexas.tacc.tapis.meta.model.LRQTask;

public class BeanstalkClientT {
  public static void main(String[] args) {
    
    BeanstalkConfig beanstalkConfig = new BeanstalkConfig();
    BeanstalkMetaClient client = new BeanstalkMetaClient(beanstalkConfig,"vdjserver.org");
// do something
    String jobJson = "{\n" +
        "  \"name\": \"myQuery\",\n" +
        "  \"queryType\": \"SIMPLE\",\n" +
        "  \"query\": [{\"repertoire_id\": \"1841923116114776551-242ac11c-0001-012\"}, {\"cdr1\": 1}],\n" +
        "  \"notification\": \"mailto:sterry1@tacc.utexas.edu\"\n" +
        "}";
    // put <priority>, <delay>, <time to reserve>, <bytes> <data>  number of bytes and the byte[] for data.
    // producer.putJob(1,0,5000, jobJson.getBytes());
    client.putTask(jobJson);
    
    LRQTask task = client.getTask();
    // Job job = consumer.reserveJob(1);
    String result = new String();
    System.out.println(result);
// close client and release resources
    client.close();
    // producer.close();
    // consumer.close();
  }
}
