package edu.utexas.tacc.tapis.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingMeT {
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(LoggingMeT.class);
  public static void main(String[] args) {
    _log.debug("what up");
  }
}
