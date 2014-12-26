package com.hmammon.familyphoto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.http.GetNewPhoto;
import com.hmammon.familyphoto.http.HttpHelper;
import com.hmammon.familyphoto.http.UpdatePhoto;
import com.hmammon.familyphoto.utils.BaseApp;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by icyfox on 2014/12/1.
 */
public class MyFileHandler extends FileAsyncHttpResponseHandler {

    private final SQLiteDatabase db;
    private String fileName;
    private int index;
    private GetNewPhoto gnp;

    public MyFileHandler(File file, int index, GetNewPhoto gnp) {
        super(file);
        PhotoDbHelper dbhelper = new PhotoDbHelper(BaseApp.getInstance());
        db = dbhelper.getWritableDatabase();
        this.index = index;
        this.gnp = gnp;
    }

    public void setFileName(String name){
        fileName = name;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        gnp.notifyDone();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {
        Log.i("file", "下载成功！" + file.getAbsolutePath());
        gnp.notifyDone();

        //发送更新数据请求
        Intent msg = new Intent();
        msg.setAction(FileService.REFRESH);
        BaseApp.getInstance().sendBroadcast(msg);
    }

    private void saveInDb(String path){
        ContentValues cv = new ContentValues();
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_PATH, path);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_TIME, System.currentTimeMillis()+"");
        db.insert(PhotoContract.TABLE_NAME,null, cv);
    }
}
