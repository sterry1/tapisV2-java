package edu.utexas.tacc.tapis.meta.model;

import java.io.Serializable;

public class LRQSubmission implements Serializable {
  private static final long serialVersionUID = 2852014510831095945L;
  
  private String _id;
  private String name;
  private String queryType;
  private String query;
  private String notification;
  
  public LRQSubmission(){ }
  
  public LRQSubmission(String name, String queryType, String query, String notification) {
    this.name = name;
    this.queryType = queryType;
    this.query = query;
    this.notification = notification;
  }
  
  public LRQSubmission(String _id, String name, String queryType, String query, String notification) {
    this._id = _id;
    this.name = name;
    this.queryType = queryType;
    this.query = query;
    this.notification = notification;
  }
  
  @Override
  public String toString() {
    return "LRQSubmission{" +
        "_id='" + _id + '\'' +
        ", name='" + name + '\'' +
        ", queryType='" + queryType + '\'' +
        ", query='" + query + '\'' +
        ", notification='" + notification + '\'' +
        '}';
  }
  
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }
  
  public String get_id() {
    return _id;
  }
  
  public void set_id(String _id) {
    this._id = _id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getQueryType() {
    return queryType;
  }
  
  public void setQueryType(String queryType) {
    this.queryType = queryType;
  }
  
  public String getQuery() {
    return query;
  }
  
  public void setQuery(String query) {
    this.query = query;
  }
  
  public String getNotification() {
    return notification;
  }
  
  public void setNotification(String notification) {
    this.notification = notification;
  }
}