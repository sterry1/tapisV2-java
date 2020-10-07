package edu.utexas.tacc.tapis.meta.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.utexas.tacc.tapis.utils.ConversionUtils;


// TODO swap out for generated version
public class LRQSubmission implements Serializable {
  private static final long serialVersionUID = 2852014510831095945L;
  
  @SerializedName("_id")
  @Expose
  private String id;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("queryType")
  @Expose
  private String queryType;
  @SerializedName("query")
  @Expose
  private List<Object> query = null;
  @SerializedName("notification")
  @Expose
  private String notification;
  
  public enum qType { SIMPLE, AGGREGATION }
  
  public LRQSubmission(){ }
  
  /*------------------------------------------------------------------------
   *                              Constructors
   * -----------------------------------------------------------------------*/
  /**
   * Convenience method constructor for request body LRQSubmission
   * @param name
   * @param queryType
   * @param query
   * @param notification
   */
  public LRQSubmission( String name, String queryType, List<Object> query, String notification) {
    this.name = name;
    this.queryType = queryType;
    this.query = query;
    this.notification = notification;
  }
  
  /**
   * Convenience method constructor for persisted LRQSubmission
   * @param _id
   * @param name
   * @param queryType
   * @param query
   * @param notification
   */
  public LRQSubmission(String id, String name, String queryType, List<Object> query, String notification) {
    super();
    this.id = id;
    this.name = name;
    this.queryType = queryType;
    this.query = query;
    this.notification = notification;
  }
  
  public String toJson() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }
  
  public LRQSubmission fromByteArray(byte[] msg){
    String msgString = new String(msg);
    JsonObject jsonObject = ConversionUtils.stringToJsonObject(msgString);
    ConversionUtils.jsonObjectToLRQSubmission(jsonObject);
    return this;
  }
  
/*------------------------------------------------------------------------
 *                              Getters and Setters
 * -----------------------------------------------------------------------*/
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  

  public String getQueryType() {
    return queryType;
  }
  public void setQueryType(String queryType) {
    this.queryType = queryType;
  }
  
  public List<Object> getQuery() {
    return query;
  }
  public void setQuery(List<Object> query) {
    this.query = query;
  }
  
  public String getNotification() {
    return notification;
  }
  public void setNotification(String notification) {
    this.notification = notification;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id)
                                    .append("name", name)
                                    .append("queryType", queryType)
                                    .append("query", query)
                                    .append("notification", notification).toString();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name)
                                .append(notification)
                                .append(id)
                                .append(queryType)
                                .append(query).toHashCode();
  }
  
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof LRQSubmission) == false) {
      return false;
    }
    LRQSubmission rhs = ((LRQSubmission) other);
    return new EqualsBuilder().append(name, rhs.name)
                              .append(notification, rhs.notification)
                              .append(id, rhs.id).append(queryType, rhs.queryType)
                              .append(query, rhs.query).isEquals();
  }

}