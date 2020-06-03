package edu.utexas.tacc.tapis.meta.utils;

public class MetaSKPermissionsMapperRunner {
  
  public static void main(String[] args) {
    String uri1 = "/v3/meta/StreamsTACCDB/Proj1/5e29fa28a93eebf39fba927b";
    // String uri1 = "/v3/meta/";
    // String uri1 = "/v3/meta/StreamsTACCDB/Proj1";
    MetaSKPermissionsMapper mapper = new MetaSKPermissionsMapper(uri1,"dev");
    String permSpec = mapper.convert("GET");
    System.out.println(permSpec);
  }
  
}
