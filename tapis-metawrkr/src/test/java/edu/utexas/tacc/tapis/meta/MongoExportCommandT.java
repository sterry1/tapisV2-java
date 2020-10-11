package edu.utexas.tacc.tapis.meta;

import java.util.HashMap;
import java.util.Map;

public class MongoExportCommandT {
  public static void main(String[] args) {
    // "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
    Map<String,String> params = new HashMap<>();
    params.put("hostPort","aloe-dev08.tacc.utexas.edu:27019");
    params.put("user","tapisadmin");
    params.put("password","d3f@ult");
    params.put("db","v1airr");
    params.put("collection","rearrangement");
    params.put("fileOutput","onejson.json");
    // params.put("fields","repertoire_id,locus");
    params.put("query","{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}");
    params.put("fields","");
    // params.put("query","");
    
    MongoExportCommand mec = new MongoExportCommand(params);
    assert (mec.isReady);
    
    System.out.println(mec.exportCommand());
  }
  
}
