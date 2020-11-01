package edu.utexas.tacc.tapis;

import edu.utexas.tacc.tapis.meta.TestData;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAO;
import edu.utexas.tacc.tapis.meta.dao.LRQSubmissionDAOImpl;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.bson.types.ObjectId;

public class LRQSubmissionDAOImplT {
  public static void main(String[] args) {
    // use json submission string to test
    LRQSubmissionDAO dao = new LRQSubmissionDAOImpl("vdjserver.org");
    LRQSubmission lrqSubmission = ConversionUtils.jsonObjectToLRQSubmission(ConversionUtils.stringToJsonObject(TestData.submissionJsonSimple));
    
    ObjectId objectId = dao.createSubmission(lrqSubmission,"v1airr" ,"rearrangement" );
    
    dao.updateSubmissionStatus(objectId.toString(),"RUNNING");
    
  }
}
