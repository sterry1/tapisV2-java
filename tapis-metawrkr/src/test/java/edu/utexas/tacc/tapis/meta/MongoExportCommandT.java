package edu.utexas.tacc.tapis.meta;

import java.util.HashMap;
import java.util.Map;

public class MongoExportCommandT {
  public static void main(String[] args) {
    // "mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'";
    // the Map must include the fields and query entries even if they are empty. In fact, they
    // must be empty if not used for a correct command to be returned.
  
    Map<String,String> params;
    MongoExportCommand mec;
  
/*
    params = TestData.getSimpleCmdWithAuthAndFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println("simple query with auth with fields");
    System.out.println(mec.exportCommandAsString());
*/
  
    params = TestData.getSimpleCmdWithAuthWOFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println("simple query with auth wo fields");
    System.out.println(mec.exportCommandAsString());
  
/*
    params = TestData.getSimpleCmdWOAuthWOFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println("simple query wo auth wo fields");
    System.out.println(mec.exportCommandAsString());
  
    params = TestData.getSimpleCmdWOAuthWithFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println("simple query wo auth with fields");
    System.out.println(mec.exportCommandAsString());
  
    params = TestData.getExportCmdWithAuthWOQueryOrFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println("export with auth wo query fields");
    System.out.println(mec.exportCommandAsString());
  
    params = TestData.getExportCmdWOAuthWOQueryOrFields();
    mec = new MongoExportCommand(params);
    assert (mec.isReady);
    System.out.println(mec.exportCommandAsString());
*/
  }
  
}

/*
simple query with auth with fields
mongoexport --quiet -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement  -f="repertoire_id,locus" -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}'  | gzip > onejson.json.gz

simple query with auth wo fields
mongoexport --quiet -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement   -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}'  | gzip > onejson.json.gz
mongoexport --quiet -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement   -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}' --out=onejson.json

simple query wo auth wo fields
mongoexport --quiet -h=aloe-dev08.tacc.utexas.edu:27019    -d=v1airr -c=rearrangement   -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}'  | gzip > onejson.json.gz

simple query wo auth with fields
mongoexport --quiet -h=aloe-dev08.tacc.utexas.edu:27019    -d=v1airr -c=rearrangement  -f="repertoire_id,locus" -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}'  | gzip > onejson.json.gz

export with auth wo query fields
mongoexport --quiet -h=aloe-dev04.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement     | gzip > onejson.json.gz

mongoexport --quiet -h=aloe-dev04.tacc.utexas.edu:27019    -d=v1airr -c=rearrangement     | gzip > onejson.json.gz


 mongoexport -h=aloe-dev04.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f%40ult --authenticationDatabase=admin -d=v1airr -c=rearrangement  -f="cdr1,cdr2" -q='{"repertoire_id":"1841923116114776551-242ac11c-0001-012"}'
*
mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f=\"repertoire_id,locus\" -q='{\"repertoire_id\":\"1993707260355416551-242ac11c-0001-012\"}'"
db.getCollection('metadata').find({"tenantId":"sgci"}).count()

mongoexport -h=mongo-prod-agave.tacc.utexas.edu:9000 -u=adminuser -p=4b0141548e3e9e5581772ff4 --authenticationDatabase=admin -d=api  -c=metadata --type=csv -o=scgi.csv -q='{"tenantId":"sgci"}'

* */