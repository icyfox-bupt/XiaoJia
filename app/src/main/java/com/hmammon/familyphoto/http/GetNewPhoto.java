package com.hmammon.familyphoto.http;

import android.util.Log;

import com.hmammon.familyphoto.utils.BaseApp;
import com.hmammon.familyphoto.MyFileHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by icyfox on 2014/11/30.
 */
public class GetNewPhoto {

    public void start() {

        RequestParams rp = new RequestParams();
        rp.add("deviceId", BaseApp.getDeviceId());

        HttpHelper.post(HttpHelper.GETPHOTO, rp, handler);
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.i("tag", response.toString());

            JSONArray datas = response.optJSONArray("data");
            if (datas.length() == 0) return;

            for (int i = 0; i < 1; i++) {
                JSONObject photoInfo = datas.optJSONObject(i);

                RequestParams param = new RequestParams();
                param.add("fileName", photoInfo.optString("photoName"));
                param.add("deviceId", BaseApp.getDeviceId());
                param.add("photoUrl", photoInfo.optString("photoUrl"));

                File file = new File(HttpHelper.SAVEPATH,
                        "pic" + System.currentTimeMillis() + ".zip");

                String filename = photoInfo.optString("photoName");
                MyFileHandler fileHandler = new MyFileHandler(file);
                fileHandler.setFileName(filename);

                HttpHelper.post(HttpHelper.GETZIP, param, fileHandler);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    };

}
