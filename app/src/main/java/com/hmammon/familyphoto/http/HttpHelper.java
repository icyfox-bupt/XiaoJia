package com.hmammon.familyphoto.http;

import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by icyfox on 2014/11/30.
 */
public class HttpHelper {

    private static final String BASEURL = "http://211.103.218.91:442/";
    public static final String SYNC = BASEURL + "device/syncdevicecontent";
    public static final String INVITE = BASEURL + "device/invite";

    public static final String GETPHOTO = BASEURL + "familyphoto/photo/Photos_getPhotos";
    public static final String GETZIP = BASEURL + "familyphoto/download/Download_download";
    public static final String UPDATE = BASEURL + "familyphoto/photo/Photos_updatePhotos";

    public static String SAVEPATH = Environment.getExternalStorageDirectory() + "/xiaojia";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
        Log.i("http","---------------post---------------");
        Log.i("http",url + "\n" + params.toString());
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
        Log.i("http","---------------get---------------");
        Log.i("http",url);
    }
}
