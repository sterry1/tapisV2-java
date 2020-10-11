package edu.utexas.tacc.tapis.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MongoExportExecutor {
  private static final Logger _log = LoggerFactory.getLogger(MongoExportExecutor.class);
  
  private StringBuilder stringBuilder;
  private MongoExportCommand command;
  
  // this guy takes a MongoQuery to execute an export on a collection
  //  the collection may be created from an aggregation or
  //  it may simply be a core collection of the database.
  
  // TODO need template for each type of export
  // 1. Aggregation result written to temp collection
  // 2. Simple find with query
  // 3. Simple find with query and projections.
  /**
   * There are 3 cases each slightly different for the command to export a MongoDB collection.
   * 1. Export of a collection without any query or projection as result of an Aggregation run. The
   *    resulting collection specified in the AGGREGATION will be exported and deleted.
   * 2. Export of a named collection with a SIMPLE query run to filter the results immediately written
   *    to file in the default storage location.
   * 3. Export of a named collection with a SIMPLE query and projection of fields to return immediately
   *    written to file in the default storage location.
   *
   * @param exportCommand
   */
  public MongoExportExecutor (MongoExportCommand exportCommand){
    command = exportCommand;
  }
  
  public void runExportCommand(String mongoexportCmd){
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("bash", "-c", mongoexportCmd);
  
    processBuilder.inheritIO();
    Process process = null;
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      _log.error("ERROR something went wrong on process startup "+e.getMessage());
      e.printStackTrace();
    }
  
    int exitCode = 0;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      _log.error("ERROR something went wrong exitCode : "+exitCode+" "+e.getMessage());
      e.printStackTrace();
    }
  
    if(exitCode == 0){
      _log.debug("Success exitCode : "+exitCode);
    }else{
      _log.error("ERROR something went wrong exitCode : "+exitCode);
    }
  }
  
}
