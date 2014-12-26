package com.hmammon.familyphoto.http;

import com.hmammon.familyphoto.utils.BaseApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by icyfox on 2014/12/8.
 */
public class UpdatePhoto {

    private String guid;

    public UpdatePhoto(String guid) {
        this.guid = guid;
    }

    public void start() {
        RequestParams rp = new RequestParams();
        rp.add("deviceId", BaseApp.getDeviceId());

        JSONObject obj = new JSONObject();
        try {
            obj.put(guid, 2);
        } catch (JSONException e) {
        }
        String update = obj.toString();
        rp.add("update", update);

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
