package edu.utexas.tacc.tapis.meta;

import edu.utexas.tacc.tapis.utils.TimerUtility;

import java.util.HashMap;
import java.util.Map;

public class MongoExportExecutorT {
  public static void main(String[] args) {
  
    // example: "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
    Map<String,String> params = new HashMap<>();
    params.put("host","aloe-dev04.tacc.utexas.edu");
    params.put("port","27019");
    params.put("user","tapisadmin");
    params.put("password","d3f@ult");
    params.put("authDB","--authenticationDatabase=admin");
    params.put("db","v1airr");
    params.put("collection","rearrangement");
    params.put("fileOutput","xyzabc123.gz");
    params.put("fields","");
    params.put("query","{\"repertoire_id\":\"1841923116114776551-242ac11c-0001-012\"}");
  
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
  
    String mongoexportCmd = mec.exportCommandAsString();
  
    System.out.println("Export cmd line : \n"+ mec.exportCommandAsString());
    
    MongoExportExecutor mongoExportExecutor = new MongoExportExecutor();
    
    TimerUtility timer = new TimerUtility();
    timer.start();
    mongoExportExecutor.runExportCommand(mec);
    timer.end();
  }
  
}
