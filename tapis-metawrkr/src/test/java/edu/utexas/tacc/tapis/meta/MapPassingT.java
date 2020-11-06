package edu.utexas.tacc.tapis.meta;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class MapPassingT {
  private void firsMethod(Map map) {
    map.put("field1", "first value");
    secondMethod(map);
  }
  
  private void secondMethod(Map map) {
    map.put("field2", "second value");
  }
  
  public static void main(String[] args) {
    MapPassingT mpt = new MapPassingT();
    Map<String, String> map = new HashedMap();
    map.put("field1", "");
    map.put("field2", "");
    
    mpt.firsMethod(map);
    
    map.forEach((k, v) -> System.out.println((k + ":" + v)));
  }
}

