package edu.utexas.tacc.tapis.meta.dao;

import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;

import java.util.Collection;

public class LRQSubmissionDAOImpl implements LRQSubmissionDAO{
  // TODO log marker
  
  MongoDBClientSingleton client;
  
  public LRQSubmissionDAOImpl(){
    // check if client is inited
  }

  @Override
  public boolean createSubmission(LRQSubmission lrqSubmission) {
    return false;
  }
  
  @Override
  public boolean deleteSubmission(String id) {
    return false;
  }
  
  @Override
  public boolean updateSubmissionStatus(String id, String status) {
    return false;
  }
  
  @Override
  public String getSubmission(String id) {
    return null;
  }
  
  @Override
  public Collection<LRQSubmission> listSubmissions() {
    return null;
  }
}
