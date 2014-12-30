package com.hmammon.familyphoto.http;

import android.util.Log;

import com.hmammon.familyphoto.MyFileHandler;
import com.hmammon.familyphoto.utils.BaseApp;
import com.hmammon.familyphoto.utils.SPHelper;
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

    private int imageIndex = 0;
    private JSONArray downloads;
    private int msgLength = 0;
    private int downed = 0;
    public String guid, uid;

    public void start() {
        RequestParams rp = new RequestParams();
        rp.add("deviceId", BaseApp.getDeviceId());

        HttpHelper.post(HttpHelper.SYNC, rp, handler);
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
//            Log.i("tag", response.toString());

            JSONObject json = response.optJSONObject("data");
            if (json == null) {
                BaseApp.getInstance().activity.setDownloading(false);
                BaseApp.getInstance().service.isDownloading = false;
                return;
            }

            String locktime = json.optString("locktime");
            String starttime = json.optString("starttime");
            SPHelper.setOnOffTime(locktime, starttime);
            BaseApp.getInstance().setClock();

            //下载新图片
            downloads = json.optJSONArray("undownloads");
            downPack(downloads);

            //删除旧照片
            JSONArray deletes = json.optJSONArray("deleteds");
            DeletePhoto dp = new DeletePhoto(deletes);
            dp.start();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.e("http", statusCode + "");
        }
    };

    /**
     * 打包下载一组文件
     *
     */
    private void downPack(JSONArray downloads) {
        Log.i("down", downloads + " \n" +  imageIndex);

        if (downloads != null && imageIndex < downloads.length()) {
            JSONObject image = downloads.optJSONObject(imageIndex);

            guid = image.optString("guid");
            uid = image.optString("uid");
            JSONArray msgs = image.optJSONArray("msgs");

            if (msgs == null || msgs.length() == 0) return;

            for (int i = 0; i < msgs.length(); i++) {
                String url = msgs.optString(i);
                String filename = guid + "_" + i + ".jpg";

                File file = new File(HttpHelper.SAVEPATH, filename);
                MyFileHandler handler = new MyFileHandler(file, this);

                HttpHelper.get(url, handler);
            }
        }else{
            //没得下了
            BaseApp.getInstance().activity.setDownloading(false);
            BaseApp.getInstance().service.isDownloading = false;
        }
    }

    /**
     * 通知下载完成
     */
    public void notifyDone(){
        Log.i("file", downed + " length = " + msgLength);
        downed++;
        if (downed >= msgLength){
            //下载一个包完成了
            new UpdatePhoto(guid, UpdatePhoto.TYPE_GET).start();
            gohead();
        }
    }

    /**
     * 继续下载更多的东西
     */
    public void gohead(){
        imageIndex++;
        downPack(downloads);
    }

}
