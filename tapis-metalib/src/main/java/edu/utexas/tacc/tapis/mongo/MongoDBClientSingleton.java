package edu.utexas.tacc.tapis.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBClientSingleton {

    private static boolean initialized = false;
    private static MongoClientURI mongoUri;
    private static String serverVersion = null;

    
    private static final Logger _log = LoggerFactory.getLogger(MongoDBClientSingleton.class);

    public static void init(MongoClientURI uri) {
        mongoUri = uri;
        initialized = true;
    }

    public static MongoDBClientSingleton getInstance() {
        return MongoDBClientSingletonHolder.INSTANCE;
    }

    public static boolean isInitialized() {
        return initialized;
    }
    
    public static String getServerVersion() {
        return serverVersion;
    }

    private MongoClient mongoClient;

    private MongoDBClientSingleton() {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        try {
            setup();
        } catch (UnknownHostException ex) {
            _log.error("error initializing mongodb client", ex);
        } catch (Throwable tr) {
            _log.error("error initializing mongodb client", tr);
        }
    }

    private void setup() throws UnknownHostException {
        if (isInitialized()) {
            mongoClient = new MongoClient(mongoUri);
        }

        // get the db version
        try {
            Document res = mongoClient.getDatabase("admin")
                    .runCommand(
                            new BsonDocument("buildInfo",
                                    new BsonInt32(1)));

            Object _version = res.get("version");

            if (_version != null && _version instanceof String) {
                serverVersion = (String) _version;
            } else {
                _log.warn("Cannot get the MongoDb version.");
                serverVersion = "3.x?";
            }
        } catch (Throwable t) {
            _log.warn("Cannot get the MongoDb version.");
            serverVersion = "?";
        }
    }

    /**
     *
     * @return
     */
    public MongoClient getClient() {
        if (this.mongoClient == null) {
            throw new IllegalStateException("mongo client not initialized");
        }

        return this.mongoClient;
    }
    
    public MongoClientURI getMongoUri(){
        if (MongoDBClientSingleton.mongoUri == null) {
            throw new IllegalStateException("mongo client uri not initialized");
        }
    
        return MongoDBClientSingleton.mongoUri;
    }

    private static class MongoDBClientSingletonHolder {

        private static final MongoDBClientSingleton INSTANCE = new MongoDBClientSingleton();

        private MongoDBClientSingletonHolder() {
        }
    }
}
