package edu.utexas.tacc.tapis;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import edu.utexas.tacc.tapis.meta.mongo.MongoDBClientSingleton;
import edu.utexas.tacc.tapis.meta.mongo.ValidateAggrQuery;
import org.bson.Document;

public class ValidateAggrQueryT {
  public static void main(String[] args) {
    ValidateAggrQuery validateAggrQuery = new ValidateAggrQuery();
    String json = "{\"owner2\":\"sterry1\"}";
    Document document = Document.parse(json);
    
    MongoClient client = null;
    MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev04.tacc.utexas.edu:27019/?authSource=admin");
    MongoDBClientSingleton.init(uri);
    if(MongoDBClientSingleton.isInitialized()){
      client = MongoDBClientSingleton.getInstance().getClient();
      System.out.println("MongoDB server version : "+MongoDBClientSingleton.getServerVersion());
    }
  
    Block<Document> printBlock = new Block<Document>() {
      @Override
      public void apply(final Document document) {
        System.out.println(document.toJson());
      }
    };
    
    // 1. string is a valid json document
    // 2. string is a valid query
    if(client != null){
      FindIterable<Document> rslts = client.getDatabase("api").getCollection("metadata").find(document);
      rslts.forEach(printBlock);
      System.out.println(rslts.toString());
      
    }
  }
  
}
