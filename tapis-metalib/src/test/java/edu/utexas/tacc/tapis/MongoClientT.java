package edu.utexas.tacc.tapis;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import edu.utexas.tacc.tapis.meta.TestData;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;
import org.bson.Document;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

public class MongoClientT {
  
  private static void setup(String url){
    MongoClientURI uri = new MongoClientURI(url);
    MongoDBClientSingleton.init(uri);
  }
  
  private static MongoClient serverVersion(){
    MongoClient client = null;
    if(MongoDBClientSingleton.isInitialized()){
      client = MongoDBClientSingleton.getInstance().getClient();
    }
    System.out.println("Server version: "+MongoDBClientSingleton.getServerVersion());
    return client;
  }
  
  private static void  aggr(MongoClient mongoClient, String _collection, String aggr){
    
    MongoCollection<Document> collection = mongoClient.getDatabase("v1airr").getCollection(_collection);
    ArrayList jstages = new ArrayList();
    JsonArray list = (JsonArray) JsonParser.parseString(aggr);
    Iterator<JsonElement> iter = list.iterator();
  
    System.out.println("Aggregation :\n");
    System.out.println(aggr+"\n");
    LocalTime begin = LocalTime.now();
    System.out.println("beginning time: "+ begin);
    
    while(iter.hasNext()){
      JsonObject obj = iter.next().getAsJsonObject();
      String s = obj.toString();
      System.out.println("add stage : "+s);
      jstages.add(Document.parse(s));
    }
    
    int document_count = 0;
    AggregateIterable<Document> output = collection.aggregate(jstages);
    // you have to iterate the collection before the $out collection is created fully. why??
    // using run command or shell in robo3t always takes about the same time to create the
    // temp collection and rename.
    // not sure if there is a paging penalty for aggregations just like there is for queries.
    
    // need this loop before the pipeline will generate the out file
    for (Document doc : output) {
      document_count++;
      // System.out.println(document_count);
    }
    // 2020-06-12 16:23:35.268   final 2020-06-12 16:35:53.954 ~ 12 mins
    // 2020-11-09 total document count: 121 ms : 191049 secs : 191   mins : 3 : 11 secs
    LocalTime end = LocalTime.now();
    System.out.println("end time: "+ end);
    System.out.println("total document count: "+document_count);
    Duration duration = Duration.between(begin, end);
    long diff = Math.abs(duration.toMillis());
    long secs = diff/1000;
    long minutes;
    System.out.println("ms : "+diff+" secs : "+secs+"   mins : "+secs/60+" : "+secs%60+" secs");
  }
  
  
  public static void main(String[] args) {
    
    // mongo-uri = mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin
    // MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev04.tacc.utexas.edu:27019/?authSource=admin"); // dev
    // MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin"); //  staging
    // String url = "mongodb://localhost:27020/?authSource=admin";
    // String url = "mongodb://tapisadmin:d3f%40ult@aloe-dev04.tacc.utexas.edu:27019/?authSource=admin";
   
    String url = "mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin";
    System.out.println(url);
    setup(url);
    
    MongoClient client = serverVersion();
    
    aggr(client,"rearrangement", TestData.aggregationShortWithOut);
    
    
  }
}
