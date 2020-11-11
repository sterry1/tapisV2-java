package edu.utexas.tacc.tapis.meta.model;

public enum LRQStatus {
  SUBMITTED("SUBMITTED"),
  RUNNING("RUNNING"),
  FINISHED("FINISHED"),
  FAILED("FAILED");
  
  public final String status;
  
  private LRQStatus(String _status){ this.status=_status;}
  
}
