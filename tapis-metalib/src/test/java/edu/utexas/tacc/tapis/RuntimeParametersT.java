package edu.utexas.tacc.tapis;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;

public class RuntimeParametersT {
  public static void main(String[] args) {
    RuntimeParameters rp = RuntimeParameters.getInstance();
    StringBuilder sb = new StringBuilder();
    rp.getRuntimeInfo(sb);
    System.out.println(sb);
  }
}
