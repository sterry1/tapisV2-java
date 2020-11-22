package edu.utexas.tacc.tapis.meta.client;

public class NotificationT {
  public static void main(String[] args) {
    System.out.println("send notification");
    String url = "https://httpbin.org/delay/12";
    String json = "{" +
        "\"status\":\"FINISHED LRQ task\"," +
        "\"results\" : \"/irplus/data/lrqdata/lrq-5fb93b8ad3c35b0235aad825.gz\"" +
        "}";
  
    Notification notification = new Notification(url);
    if(notification.isValidNotificationUrl()){
      notification.sendNotification(json);
    }
    
  }
}
