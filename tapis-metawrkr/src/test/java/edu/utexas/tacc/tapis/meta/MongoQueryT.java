package edu.utexas.tacc.tapis.meta;

import com.google.gson.JsonArray;
import edu.utexas.tacc.tapis.meta.model.LRQTask;
import edu.utexas.tacc.tapis.utils.ConversionUtils;

public class MongoQueryT {
  public static void main(String[] args) {
    String stringTask = TestData.taskJsonAggr;
    LRQTask task = ConversionUtils.stringToLRQTask(stringTask);
    JsonArray jsonArray = ConversionUtils.arrayListOfStringToJsonArray(task.getQuery());
    MongoQuery mongoQuery = new MongoQuery(task.getQueryType(),jsonArray);
    assert mongoQuery.isInitialized;
    assert mongoQuery.hasProjections;
    try {
      mongoQuery.injectOutDefinition("12322344");
    } catch (Exception e) {
      e.printStackTrace();
    }
    // System.out.println("$match : "+mongoQuery.);
    // System.out.println("$projection: "+mongoQuery.getSimpleQueryProjetionAsFields());
    System.out.println("test this");
  }
}
