package edu.utexas.tacc.tapis.meta.config;

import com.dinstone.beanstalkc.Configuration;

public class BeanstalkConfig {
  private Configuration config;
  
  public BeanstalkConfig(){
    // TODO get runtime parameters for values
    config = new Configuration();
    config.setServiceHost("aloe-dev04.tacc.utexas.edu");
    config.setServicePort(11300);
    config.setConnectTimeout(2000);
    config.setReadTimeout(3000);
  }
  
  public Configuration getConfig() { return config; }
  
}
