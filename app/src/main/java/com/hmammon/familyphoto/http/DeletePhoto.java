package com.hmammon.familyphoto.http;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hmammon.familyphoto.FileService;
import com.hmammon.familyphoto.Photo;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.utils.BaseApp;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icyfox on 2014/12/30.
 */
public class DeletePhoto {

    private PhotoDbHelper dbhelper;
    private SQLiteDatabase db;
    private JSONArray deletes;

    public DeletePhoto(JSONArray deletes) {
        this.deletes = deletes;

        dbhelper = new PhotoDbHelper(BaseApp.getInstance());
        db = dbhelper.getWritableDatabase();
    }

    public void start() {
        if (deletes == null || deletes.length() == 0) return;

        for (int i = 0; i < deletes.length(); i++) {
            String delGuid = deletes.optJSONObject(i).optString("guid");
            JSONArray arr = deletes.optJSONObject(i).optJSONArray("url");


            for (int j = 0; j < arr.length(); j++){
                String url = arr.optString(j);
                ArrayList<Photo> photos = dbhelper.getPhotoByGuidUrl(delGuid, url);
                dbhelper.delInDb(delGuid, url);
                delPhotos(photos);
            }


            Log.i("del", arr.length() + "photos have been deleted " + delGuid);
            UpdatePhoto up = new UpdatePhoto(delGuid, UpdatePhoto.TYPE_DELETE);
            if (arr.length() > 0) up.start();
        }

        //发送更新数据请求
        Intent msg = new Intent();
        msg.setAction(FileService.REFRESH);
        BaseApp.getInstance().sendBroadcast(msg);
    }

    private void delPhotos(ArrayList<Photo> photos){
        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            File p = new File(photo.path);
            p.delete();
            File t = new File(photo.thumb);
            t.delete();
        }
    }

    private class Gpack{
        String guid = "";
        ArrayList<String> urls = new ArrayList<>();
    }

}
