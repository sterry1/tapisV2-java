package edu.utexas.tacc.tapis.meta.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;
import edu.utexas.tacc.tapis.utils.ConversionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

public class AggregationQueryDAO {
  private static final Logger _log = LoggerFactory.getLogger(AggregationQueryDAO.class);
  
  private MongoClient client = null;
  private boolean isClientReady = false;
  private final String db;
  private final String collection;
  
  // TODO separate Query host and LRQ book keeping host runtime is already partitioned for this.
  public AggregationQueryDAO (String _db, String _collection){
    db = _db;
    collection = _collection;
    if (MongoDBClientSingleton.isInitialized()) {  // initialization should have been done at runtime startup.
      this.client = MongoDBClientSingleton.getInstance().getClient();
      if (client != null) isClientReady = true;
    }
    _log.debug("Mongodb client setup for DB: " + db + ", collection: " + collection);
  }
  
  public void runAggregation(JsonArray pipeline){
    LocalTime begin = LocalTime.now();
    _log.debug("beginning time: "+ begin);
  
    // pipeline must contain the $out name for the collection.
    MongoCollection<Document> mongoCollection = client.getDatabase(db).getCollection(collection);
    ArrayList jstages = new ArrayList();
    // make sure pipeline is not null and not empty
    if(pipeline != null && pipeline.size() > 0){
      Iterator<JsonElement> iter = pipeline.iterator();
      _log.debug("Aggregation :\n");
      _log.debug(ConversionUtils.jsonArrayToString(pipeline) +"\n");
  
      while(iter.hasNext()){
        JsonObject obj = iter.next().getAsJsonObject();
        String s = obj.toString();
        System.out.println("add stage : "+s);
        jstages.add(Document.parse(s));
      }
      
      AggregateIterable<Document> output = mongoCollection.aggregate(jstages);
      // you have to iterate the collection before the $out collection is created fully. why??
      // using run command or shell in robo3t always takes about the same time to create the
      // temp collection and rename.
      // not sure if there is a paging penalty for aggregations just like there is for queries.
      int document_count = 0;
      // need this loop before the pipeline will generate the out file
      for (Document doc : output) { document_count++; }
  
      LocalTime end = LocalTime.now();
      _log.debug("end time: "+ end);
      _log.debug("total document count: "+document_count);
      Duration duration = Duration.between(begin, end);
      long diff = Math.abs(duration.toMillis());
      long secs = diff/1000;
      _log.debug("ms : "+diff+" secs : "+secs+"   mins : "+secs/60+" : "+secs%60+" secs");
    }
  }
  
  public void removeCollection(String collectionName){
    MongoDatabase mongoDb = client.getDatabase(db);
    MongoCollection<Document> collection = mongoDb.getCollection(collectionName);
    try {
      collection.drop();
    } catch (MongoCommandException e) {
      e.printStackTrace();
    }
    _log.debug("Collection : "+collectionName+" was successfully removed.");
  }
  
}
