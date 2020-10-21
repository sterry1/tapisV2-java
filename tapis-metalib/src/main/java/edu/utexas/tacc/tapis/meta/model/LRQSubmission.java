package edu.utexas.tacc.tapis.meta.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.JsonArray;
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
  private String _id;
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
  
  public enum qType {
    SIMPLE("SIMPLE"),
    AGGREGATION("AGGREGATION");
    
    public final String type;
    
    private qType(String type) {
      this.type = type;
    }
  }
  
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
   * @param id
   * @param name
   * @param queryType
   * @param query
   * @param notification
   */
  public LRQSubmission(String id, String name, String queryType, List<Object> query, String notification) {
    super();
    this._id = id;
    this.name = name;
    this.queryType = queryType;
    this.query = query;
    this.notification = notification;
  }
  
/*------------------------------------------------------------------------
 *                              Getters and Setters
 * -----------------------------------------------------------------------*/
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }
  
  public String get_id() {
    return _id;
  }
  public void set_id(String _id) {
    this._id = _id;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getQueryType() { return queryType; }
  public void setQueryType(String _queryType) {
    this.queryType = _queryType;
  }
  
  // this is a list of JsonObjects [{},{},...]
  // For a SIMPLE query there may be one to two JsonObjects the
  // the first should be a matching query and the second should be a projection.
  // TODO? specify match and projection for simple query.
  public List<Object> getQuery() { return query; }
  public JsonArray getJsonQueryArray(){
    Gson gson = new Gson();
    
    return null;
  }
  public String getQueryAsString() {
    Gson gson = new Gson();
    return gson.toJson(this.getQuery());
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
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("_id", _id)
                                    .append("name", name)
                                    .append("queryType", queryType)
                                    .append("query", query)
                                    .append("notification", notification).toString();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name)
                                .append(notification)
                                .append(_id)
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
                              .append(_id, rhs._id).append(queryType, rhs.queryType)
                              .append(query, rhs.query).isEquals();
  }
  
}