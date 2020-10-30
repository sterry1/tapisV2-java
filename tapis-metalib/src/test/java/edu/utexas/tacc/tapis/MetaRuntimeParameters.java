package edu.utexas.tacc.tapis;

public enum MetaRuntimeParameters {
  
  INSTANCE("Initial class info");
  
  private String info;
  private  String queryHost;
  private  String queryPort;
  private  String queryUser;
  private  String queryPwd;
  private  String queryAuthDB;
  
  
  private MetaRuntimeParameters(String info) {
    this.info = info;
    
  }
  
  public MetaRuntimeParameters getInstance() {
    return INSTANCE;
  }
  
  public String getInfo() {
    return info;
  }
  
  public void setInfo(String new_enum_info) {
    this.info = new_enum_info;
  }
  
  // initialzation
}
