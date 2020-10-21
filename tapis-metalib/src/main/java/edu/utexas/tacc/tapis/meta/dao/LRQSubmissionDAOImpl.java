package edu.utexas.tacc.tapis.meta.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.model.LRQSubmission;
import edu.utexas.tacc.tapis.mongo.MongoDBClientSingleton;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class LRQSubmissionDAOImpl extends LRQAbstractDAO implements LRQSubmissionDAO {
  // TODO log marker
  private static final Logger _log = LoggerFactory.getLogger(LRQSubmissionDAOImpl.class);
  
  private MongoClient client;
  private boolean isClientReady = false;
  private String LRQdb;
  private String LRQcollection;
  
  public LRQSubmissionDAOImpl(String collection) {
    // these documents will go into the LRQ database under a tenant collection
    this.LRQdb = RuntimeParameters.getInstance().getLrqDB();
    this.LRQcollection = collection;

    if (MongoDBClientSingleton.isInitialized()) {
      client = MongoDBClientSingleton.getInstance().getClient();
      if (client != null) isClientReady = true;
    }
    _log.debug("Mongodb client setup for DB: " + LRQdb + ", collection: " + LRQcollection);
  }
  
  /**
   * Create a new submission document in the DB / Collection
   * @param dto
   * @return new ObjectId of the persisted document or null if creation unsuccessful
   */
  @Override
  public ObjectId createSubmission(LRQSubmission dto) {
    _log.trace("Create a valid submission for DB: " + LRQdb + ", collection: " + LRQcollection);
  
    try {
      // this check is meaningless because there is always a client created and ready even if it can't connect
      if (isClientReady) {
        MongoDatabase db = client.getDatabase(LRQdb);
        MongoCollection<Document> collection = db.getCollection(LRQcollection);

        // create the bson document for insertion into DB, we predefine the id for insertion
        // Document submissionDocument = Document.parse(dto.toJson());

        Document submissionDocument = new Document();//Document.parse(dto.toJson());
        submissionDocument.append("name",dto.getName());
        submissionDocument.append("queryType",dto.getQueryType());
        submissionDocument.append("query",dto.getQuery().toString());
        submissionDocument.append("notification",dto.getNotification());

        ObjectId newId = new ObjectId();
        submissionDocument.append("_id", newId );
        submissionDocument.append("status","SUBMITTED");
        submissionDocument.append("createdDate", new Date());

        // insert into db/collection
        try {
          collection.insertOne(submissionDocument);
        } catch (IllegalArgumentException e) {
          _log.error("Failed to insert document in DB: " + LRQdb + ", collection: " + LRQcollection + " " + e.getMessage());
          _log.error("FAILED: document \n"+dto.toJson());
          return null;
        }
  
        return newId;
      }
    } catch (MongoTimeoutException e) {
      _log.error("Submission failed for DB: " + LRQdb + ", collection: " + LRQcollection + ". Due to Connection timeout to DB host. ");
      _log.error(e.getMessage());
      return null;
    }
    return null;
  }
  
  @Override
  public boolean deleteSubmission(String id) {
    return false;
  }
  
  @Override
  public boolean updateSubmissionStatus(String id, String _status) {
    try {
      // TODO this check is meaningless because there is always a client created and ready even if it can't connect
      if (isClientReady) {
        MongoDatabase db = client.getDatabase(LRQdb);
        MongoCollection<Document> collection = db.getCollection(LRQcollection);

       UpdateResult result = collection.updateOne(eq("_id", new ObjectId(id)), Updates.set("status","RUNNING"));
       System.out.println();
      }
    } catch (Exception e) {
      
      _log.error("Update failed for DB: " + LRQdb + ", collection: " + LRQcollection + ", document: "+id);
      _log.error(e.getMessage());
      return false;
    }
    return false;
  }
  
  @Override
  public String getSubmission(String id) {
    
    return null;
  }
  
  @Override
  public String getSubmissionStatus(String id) {
    try {
      // TODO this check is meaningless because there is always a client created and ready even if it can't connect
      if (isClientReady) {
        MongoDatabase db = client.getDatabase(LRQdb);
        MongoCollection<Document> collection = db.getCollection(LRQcollection);
        
        FindIterable<Document> documents = collection.find(eq("_id", new ObjectId(id)))
            .projection(fields(include("_id", "status")));
        Document document = documents.first();
        String status = document.toJson();
        return status;
      }
    } catch (MongoTimeoutException e) {
      _log.error("Submission failed for DB: " + LRQdb + ", collection: " + LRQcollection + ". Due to Connection timeout to DB host. ");
      _log.error(e.getMessage());
      return null;
    }
    return null;
  }
  
  @Override
  public Collection<LRQSubmission> listSubmissions() {
    return null;
  }
}
