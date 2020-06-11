package edu.utexas.tacc.tapis.meta.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

public class MyMongoConnector {
  public static void main(String[] args) {
    String mongoUri = System.getenv("MONGO_URI");
    MongoDBClientSingleton.init(new MongoClientURI(mongoUri));
    MongoClient mongoClient = MongoDBClientSingleton.getInstance().getClient();
    //MongoClient mongoClient = (MongoClient) MongoConnector.CONNECTION.getClient();
    MongoIterable<String> dbList = mongoClient.listDatabaseNames();
    
    MongoCursor<String> cursor = dbList.iterator();
    while(cursor.hasNext()){
      String db = cursor.next();
      System.out.println(db);
    }
  }
}
