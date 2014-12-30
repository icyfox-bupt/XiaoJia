package com.hmammon.familyphoto.http;

import com.hmammon.familyphoto.utils.BaseApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by icyfox on 2014/12/8.
 */
public class UpdatePhoto {

    private String guid;
    private int type;
    public static final int TYPE_GET = 0, TYPE_DELETE = 1;

    public UpdatePhoto(String guid, int type) {
        this.guid = guid;
        this.type = type;
    }

    public void start() {
        RequestParams rp = new RequestParams();
        rp.add("deviceId", BaseApp.getDeviceId());
        String update = "";

        if (type == TYPE_GET) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(guid, 2);
            } catch (JSONException e) {
            }

            update = obj.toString();
            rp.add("update", update);
        }
        else{
            JSONArray arr = new JSONArray();
            arr.put("guid");
            update = arr.toString();
            rp.add("deleted", update);
        }

        HttpHelper.post(HttpHelper.UPDATE, rp, handler);
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler(){

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    };
}
