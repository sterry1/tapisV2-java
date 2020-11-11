package edu.utexas.tacc.tapis;

import com.google.gson.JsonArray;
import edu.utexas.tacc.tapis.meta.TestData;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.dao.AggregationQueryDAO;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

public class AggregationQueryDAOT {
  public static void main(String[] args) {
    System.out.println("*********  Test harness begin QueryExecutorT");
    RuntimeParameters parms = null;
    try {parms = RuntimeParameters.getInstance();}
    catch (Exception e) {
      // We don't depend on the logging subsystem.
      System.out.println("**** FAILURE TO INITIALIZE: tapis-jobsapi RuntimeParameters [ABORTING] ****");
      e.printStackTrace();
      throw e;
    }
    System.out.println("**** SUCCESS:  RuntimeParameters read ****");
  
  
    String aggr = TestData.aggregationShortWithOut;

    JsonArray jsonArray = ConversionUtils.stringToJsonArray(aggr);
    AggregationQueryDAO dao = new AggregationQueryDAO("v1airr","rearrangement");
    dao.runAggregation(jsonArray);
  }
}
