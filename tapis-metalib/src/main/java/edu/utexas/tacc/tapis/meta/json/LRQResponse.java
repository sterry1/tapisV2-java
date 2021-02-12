
package edu.utexas.tacc.tapis.meta.json;

import javax.annotation.Generated;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class LRQResponse {

    @SerializedName("message")
    private String mMessage;
    @SerializedName("result")
    private LrqResponseResult mLrqResponseResult;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("version")
    private String mVersion;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public LrqResponseResult getResult() {
        return mLrqResponseResult;
    }

    public void setResult(LrqResponseResult lrqResponseResult) {
        mLrqResponseResult = lrqResponseResult;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }
    
    public static void main(String[] args) {
        // {"result":
        // {
        //   "_id":"5db9f4e34149c40001f6ea00",
        //   "location":"https://vdj-agave-api.tacc.utexas.edu/files/v2/media/system/data.vdjserver.org//irplus/data/lrqdata//lrq-5db9f4e34149c40001f6ea00.gz"
        //  },
        //  "status":"FINISHED",
        //  "message":"LRQ results complete",
        //  "version":"0.0.4"
        //  }
        LRQResponse lrqResponseT = new LRQResponse();
        LrqResponseResult rslt = new LrqResponseResult();
        rslt.set_id("122222222");
        rslt.setLocation("https://vdj-agave-api.tacc.utexas.edu/files/v2/media/system/data.vdjserver.org//irplus/data/lrqdata//lrq-5db9f4e34149c40001f6ea00.gz");
        lrqResponseT.setResult(rslt);
        lrqResponseT.setVersion("0.0.6");
        lrqResponseT.setMessage("success");
        lrqResponseT.setStatus("200");
    
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
    
        Gson gson = builder.create();
        String json = gson.toJson(lrqResponseT, LRQResponse.class);
        System.out.println(json);
    

    
    }
}
