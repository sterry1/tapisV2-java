package edu.utexas.tacc.tapis.meta.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import edu.utexas.tacc.tapis.meta.mongo.MongoDBClientSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDAO {
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(AbstractDAO.class);
  
  
/*
  MongoClientURI uri = new MongoClientURI("mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/?authSource=admin");
  MongoDBClientSingleton.init(uri);
  if(MongoDBClientSingleton.isInitialized()){ MongoClient client = MongoDBClientSingleton.getInstance().getClient();
    System.out.println(MongoDBClientSingleton.getServerVersion());
    }
*/

  
}
