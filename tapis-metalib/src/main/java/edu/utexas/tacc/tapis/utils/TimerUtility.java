package edu.utexas.tacc.tapis.utils;

import java.time.Duration;
import java.time.LocalTime;

public class TimerUtility {
  
  private LocalTime begin;
  private LocalTime end;
  
  public void start() {
    begin = LocalTime.now();
    System.out.println("==================================================================");
    System.out.println("Time begins here " + begin);
    System.out.println("==================================================================");
  }
  
  public void end(){
    end = LocalTime.now();
    Duration duration = Duration.between(begin, end);
    long diff = Math.abs(duration.toMillis());
    System.out.println("==================================================================");
    System.out.println("Time ends here " + end);
    System.out.println("milliseconds : "+diff);
    System.out.println("==================================================================");

  }
}
