package edu.utexas.tacc.tapis.meta.json;

import edu.utexas.tacc.tapis.meta.TestData;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.model.LRQStatus;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

public class JsonResponseBuilderT {
  public static void main(String[] args) {
    LRQTask task = ConversionUtils.stringToLRQTask(TestData.simpleQTask);
    String defaultLocation = RuntimeParameters.getInstance().getTenantDefaultStorageLocation()+"/lrq-"+task.get_id()+".gz";
    
    String msg = "Your results are complete.";
    JsonResponseBuilder jsonResponseBuilder = new JsonResponseBuilder(task.get_id(),defaultLocation, LRQStatus.FINISHED.status, msg);
    String string = jsonResponseBuilder.getBasicResponse();
  }
}
