package com.hmammon.familyphoto;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.http.GetNewPhoto;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.logging.Handler;


public class MainActivity extends Activity {

    private TextView tv;
    private SQLiteDatabase db;
    private HorizontalListView list;
    private PhotoAdapter adapter;
    private ImageView iv;
    private ArrayList<String> paths;
    private ImageLoader loader;
    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);
        PhotoDbHelper mHelper = new PhotoDbHelper(this);
        db = mHelper.getWritableDatabase();
        list = (HorizontalListView) findViewById(R.id.listView);
        iv = (ImageView)findViewById(R.id.imageView);
        loader = ImageLoader.getInstance();

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

        paths = new ArrayList<String>();

        while (c.moveToNext()){
            paths.add(c.getString(0));
        }

        for (int i = 0 ;i <paths.size();i++){
            Log.i("path",paths.get(i));
        }

        c.close();
        db.close();

        adapter = new PhotoAdapter(paths, this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(itListener);

        iv.setOnClickListener(clickListener);
        loader.displayImage("file://" + paths.get(0), iv);
    }

    private AdapterView.OnItemClickListener itListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loader.displayImage("file://" + paths.get(position) , iv);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpen) close();
                    else open();
        }
    };

    private void open(){
        float y = list.getY();
        int height = list.getMeasuredHeight();

        list.animate().y(y + height);
        isOpen = true;
    }

    private void close(){
        float y = list.getY();
        int height = list.getMeasuredHeight();

        list.animate().y(y - height);
        isOpen = false;
    }

}
