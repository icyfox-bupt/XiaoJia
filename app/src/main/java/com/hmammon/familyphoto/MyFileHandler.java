package com.hmammon.familyphoto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.http.HttpHelper;
import com.hmammon.familyphoto.http.UpdatePhoto;
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

    public MyFileHandler(File file) {
        super(file);
        PhotoDbHelper dbhelper = new PhotoDbHelper(BaseApp.getInstance());
        db = dbhelper.getWritableDatabase();
    }

    public void setFileName(String name){
        fileName = name;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration enums = zipFile.entries();
            while (enums.hasMoreElements()){
                ZipEntry zipEntry = (ZipEntry) enums.nextElement();
                Log.i("tag", "解压中..." + zipEntry.getName());

                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));

                int size;
                byte[] buffer = new byte[2048];

                FileOutputStream fos = new FileOutputStream(
                        HttpHelper.SAVEPATH + "/" + zipEntry.getName());
                BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

                while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, size);
                }

                bos.flush();
                bos.close();
                fos.close();

                bis.close();

                saveInDb(HttpHelper.SAVEPATH + "/" + zipEntry.getName());
            }

            //删除临时的zip文件
            file.delete();

            //向服务器发送删除请求
            UpdatePhoto up = new UpdatePhoto(fileName);
            up.start();

            //发送更新数据请求
            Intent msg = new Intent();
            msg.setAction(FileService.REFRESH);
            BaseApp.getInstance().sendBroadcast(msg);

        } catch (IOException e) {
            Log.e("http","解压失败 " + file.getAbsolutePath());
        }
        Log.i("tag", file.getAbsolutePath());
    }

    private void saveInDb(String path){
        ContentValues cv = new ContentValues();
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_PATH, path);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_TIME, System.currentTimeMillis()+"");
        db.insert(PhotoContract.TABLE_NAME,null, cv);
    }
}
