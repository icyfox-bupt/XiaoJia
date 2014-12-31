package com.hmammon.familyphoto.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hmammon.familyphoto.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icyfox on 2014/12/3.
 */
public class PhotoDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Photo.db";

        //SQL语句
        private static final String TEXT_TYPE = " TEXT";
        private static final String LONG_TYPE = " LONG";
        private static final String SHORT_TYPE = " SHORT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + PhotoContract.TABLE_NAME + " (" +
                        PhotoContract.COLUMN_NAME_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        PhotoContract.COLUMN_NAME_PHOTO_PATH + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_GUID + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_UID + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_THUMB + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_URL + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_NAME + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_LIKE + SHORT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_SEE + SHORT_TYPE + COMMA_SEP +
                        PhotoContract.COLUMN_NAME_PHOTO_TIME + LONG_TYPE +
                        ")";

        private static final String SQL_ADD_URL =
                "ALTER TABLE " + PhotoContract.TABLE_NAME + " ADD url TEXT DEFAULT ''";

        public PhotoDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    public void onCreate(SQLiteDatabase db) {
        Log.i("db","create");
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.i("db","update");
        db.execSQL(SQL_ADD_URL);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Photo> getPhotoInDb(ArrayList<Photo> photos){
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {
                PhotoContract.COLUMN_NAME_PHOTO_PATH,
                PhotoContract.COLUMN_NAME_PHOTO_THUMB,
                PhotoContract.COLUMN_NAME_PHOTO_TIME
        };

        String sortOrder = PhotoContract.COLUMN_NAME_PHOTO_TIME + " DESC";

        Cursor c = db.query(
                PhotoContract.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (photos == null)
            photos = new ArrayList<>();
        else photos.clear();

        while (c.moveToNext()){
            Photo p = new Photo();
            p.path = c.getString(0);
            p.thumb = c.getString(1);
            p.savetime = c.getLong(2);
            photos.add(p);
        }

        c.close();
        db.close();
        return photos;
    }

    /**
     * 根据guid删除数据库中照片
     * @param delGuid
     */
    public int delInDb(String delGuid) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {delGuid};
        int num =  db.delete(PhotoContract.TABLE_NAME, PhotoContract.COLUMN_NAME_PHOTO_GUID + "=?", args);
        db.close();
        return num;
    }

    /**
     * 通过GUID获得照片信息
     * @return
     */
    public ArrayList<Photo> getPhotoByGuid(String guid){
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {
                PhotoContract.COLUMN_NAME_PHOTO_PATH,
                PhotoContract.COLUMN_NAME_PHOTO_THUMB
        };

        String sortOrder = PhotoContract.COLUMN_NAME_PHOTO_TIME + " DESC";
        String whereClause = PhotoContract.COLUMN_NAME_PHOTO_GUID + "=?";
        String [] whereArgs = {guid};

        Cursor c = db.query(
                PhotoContract.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<Photo> photos = new ArrayList<>();

        while (c.moveToNext()){
            Photo p = new Photo();
            p.path = c.getString(0);
            p.thumb = c.getString(1);
            photos.add(p);
        }

        c.close();
        db.close();
        return photos;
    }

}