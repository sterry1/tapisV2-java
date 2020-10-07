package edu.utexas.tacc.tapis.meta.dao;

import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import org.bson.types.ObjectId;

import java.util.Collection;

public interface LRQSubmissionDAO {
  
  /**
   * Create a new submission document in the DB / Collection
   * @param dto
   * @return new ObjectId of the persisted document or null if creation unsuccessful
   */
  public ObjectId createSubmission(LRQSubmission dto);
  public boolean deleteSubmission(String id);
  public boolean updateSubmissionStatus(String id, String status);
  public String getSubmission(String id);
  public String getSubmissionStatus(String id);
  public Collection<LRQSubmission> listSubmissions();
  
}
