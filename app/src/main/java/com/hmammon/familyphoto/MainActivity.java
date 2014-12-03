package com.hmammon.familyphoto;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;


public class MainActivity extends Activity {

    private TextView tv;
    private SQLiteDatabase db;
    private ListView list;
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);
        PhotoDbHelper mHelper = new PhotoDbHelper(this);
        db = mHelper.getWritableDatabase();
        list = (ListView)findViewById(R.id.listView);

        String[] projection = {
                PhotoContract.COLUMN_NAME_PHOTO_PATH
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

        c.moveToFirst();
//        String path = "";
//        while (c.moveToNext()){
//            tv.append("\n" + c.getString(0));
//            path = c.getString(0);
//        }

        db.close();

        adapter = new PhotoAdapter(c, this);
        list.setAdapter(adapter);

       // new GetNewPhoto().start();
    }

}
