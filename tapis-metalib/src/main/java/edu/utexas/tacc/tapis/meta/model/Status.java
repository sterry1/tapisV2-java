package edu.utexas.tacc.tapis.meta.model;

public enum Status {
  SUBMITTED("SUBMITTED"),
  STARTED("STARTED"),
  FINISHED("FINISHED"),
  FAILED("FAILED");
  
  public final String status;
  
  private Status(String _status){ this.status=_status;}
  
}
