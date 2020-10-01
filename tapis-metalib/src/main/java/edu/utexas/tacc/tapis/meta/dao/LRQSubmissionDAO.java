package edu.utexas.tacc.tapis.meta.dao;

import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;

import java.util.Collection;

public interface LRQSubmissionDAO {
  
  public boolean createSubmission(LRQSubmission lrqSubmission);
  public boolean deleteSubmission(String id);
  public boolean updateSubmissionStatus(String id, String status);
  public String getSubmission(String id);
  public Collection<LRQSubmission> listSubmissions();
  
}
