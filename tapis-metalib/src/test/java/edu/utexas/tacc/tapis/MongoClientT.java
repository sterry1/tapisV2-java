package edu.utexas.tacc.tapis;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;

public class MongoClientT {
  
  public static void main(String[] args) {
    // mongo-uri = mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin
    // MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev04.tacc.utexas.edu:27019/?authSource=admin"); // dev
    // MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin"); //  staging
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27020/?authSource=admin");
    MongoDBClientSingleton.init(uri);
    MongoClient client = null;
    if(MongoDBClientSingleton.isInitialized()){
      client = MongoDBClientSingleton.getInstance().getClient();
      System.out.println(MongoDBClientSingleton.getServerVersion());
    }
    if(client != null){
      client.getDatabase("LRQ");
      
    }
    
  
  }
}
