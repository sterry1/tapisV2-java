package edu.utexas.tacc.tapis;

public class MetaRuntimeParametersT {
  public static void main(String[] args) {
    
    MetaRuntimeParameters enumSingleton1 = MetaRuntimeParameters.INSTANCE.getInstance();
  
    System.out.println(enumSingleton1.getInfo()); //Initial enum info
  
    MetaRuntimeParameters enumSingleton2 = MetaRuntimeParameters.INSTANCE.getInstance();
    enumSingleton2.setInfo("New enum info");
  
    System.out.println(enumSingleton1.getInfo()); // New enum info
    System.out.println(enumSingleton2.getInfo()); // New enum info
  }
}

