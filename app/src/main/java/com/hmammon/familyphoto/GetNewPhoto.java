package com.hmammon.familyphoto;

import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
            for (int i = 0; i < datas.length(); i++) {
                JSONObject photoInfo = datas.optJSONObject(i);

                RequestParams param = new RequestParams();
                param.add("fileName", photoInfo.optString("photoName"));
                param.add("deviceId", BaseApp.getDeviceId());
                param.add("photoUrl", photoInfo.optString("photoUrl"));

                File file = new File(HttpHelper.SAVEPATH,
                        "pic" + System.currentTimeMillis() + ".zip");
                MyFileHandler fileHandler = new MyFileHandler(file);

                HttpHelper.post(HttpHelper.GETZIP, param, fileHandler);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    };

}
