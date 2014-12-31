package com.hmammon.familyphoto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import java.io.FileNotFoundException;
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
    private GetNewPhoto gnp;
    private String url;

    public MyFileHandler(File file, String url, GetNewPhoto gnp) {
        super(file);
        PhotoDbHelper dbhelper = new PhotoDbHelper(BaseApp.getInstance());
        db = dbhelper.getWritableDatabase();
        this.gnp = gnp;
        this.url = url;
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

        //压缩图片
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap newBitmap = zoomBitmap(bitmap);
        bitmap.recycle();
        String thumb = savePhotoToSDCard(HttpHelper.SAVEPATH, "thumb_" + file.getName(), newBitmap);

        saveInDb(file.getAbsolutePath(), thumb);
    }

    private void saveInDb(String path, String thumb){
        ContentValues cv = new ContentValues();
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_PATH, path);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_TIME, System.currentTimeMillis()+"");
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_GUID, gnp.guid);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_UID, gnp.uid);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_NAME, "");
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_DESCRIPTION, "");
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_THUMB, thumb);
        cv.put(PhotoContract.COLUMN_NAME_PHOTO_URL, url);
        db.insert(PhotoContract.TABLE_NAME,null, cv);
    }

    /** 缩放Bitmap图片 **/
    private Bitmap zoomBitmap(Bitmap bitmap) {
        final int width = 100;
        final int height = 100;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**Save image to the SD card**/
    private String savePhotoToSDCard(String path, String photoName, Bitmap photoBitmap) {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName); //在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 50,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                    return photoFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
