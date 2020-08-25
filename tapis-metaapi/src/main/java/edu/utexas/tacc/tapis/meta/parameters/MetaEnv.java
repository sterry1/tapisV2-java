package edu.utexas.tacc.tapis.meta.parameters;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.shared.parameters.TapisEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaEnv {
  
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(MetaEnv.class);
  
  private static String getEnvValue(MetaEnv.EnvVar envVar) {
    if (envVar == null) {
      return null;
    } else {
      String retVal = System.getenv(envVar.getEnvName());
      if (retVal == null) {
        retVal = System.getenv(envVar.name());
      }
      
      return retVal;
    }
  }
  
  public static enum EnvVar {
    META_CORE_SERVER("meta.core.server");
  
    private String _envName;
  
    private EnvVar(String envName) {
      this._envName = envName;
    }
  
    public String getEnvName() {
      return this._envName;
    }
  }
  
}
