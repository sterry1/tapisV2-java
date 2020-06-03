package edu.utexas.tacc.tapis;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;

public class MongoClientT {
  
  public static void main(String[] args) {
  // mongo-uri = mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin
    MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev04.tacc.utexas.edu:27019/?authSource=admin");
    MongoDBClientSingleton.init(uri);
    if(MongoDBClientSingleton.isInitialized()){
      MongoClient client = MongoDBClientSingleton.getInstance().getClient();
      System.out.println(MongoDBClientSingleton.getServerVersion());
    }
  
  }
}
