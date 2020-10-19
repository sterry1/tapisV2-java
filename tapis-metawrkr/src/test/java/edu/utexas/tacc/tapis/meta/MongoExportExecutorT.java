package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.utils.TimerUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MongoExportExecutorT {
  public static void main(String[] args) {
  
    // example: "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
    Map<String,String> params = new HashMap<>();
    params.put("hostPort","aloe-dev08.tacc.utexas.edu:27019");
    params.put("user","tapisadmin");
    params.put("password","d3f@ult");
    params.put("db","v1airr");
    params.put("collection","rearrangement");
    params.put("fileOutput","onejson.json.gz");
    // params.put("fields","repertoire_id,locus");
    // params.put("query","{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}");
    params.put("query","{\"repertoire_id\":\"1841923116114776551-242ac11c-0001-012\"}");
  
    params.put("fields","");
    // params.put("query","");
  
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
  
    String mongoexportCmd = mec.exportCommandAsString();
  
    System.out.println("Export cmd line : \n"+ mec.exportCommandAsString());
    
    MongoExportExecutor mongoExportExecutor = new MongoExportExecutor(mec);
    
    TimerUtility timer = new TimerUtility();
    timer.start();
    mongoExportExecutor.runExportCommand();
    timer.end();
  }
  
}
