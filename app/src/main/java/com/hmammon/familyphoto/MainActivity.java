package com.hmammon.familyphoto;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity {

    private TextView tv;
    private SQLiteDatabase db;
    private HorizontalListView list;
    private PhotoAdapter adapter;
    private ImageView iv;
    private ArrayList<String> paths;
    private ImageLoader loader;
    private boolean isOpen;
    private boolean isRun;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);
        list = (HorizontalListView) findViewById(R.id.listView);
        iv = (ImageView)findViewById(R.id.imageView);
        loader = ImageLoader.getInstance();

        refreshDb();

        adapter = new PhotoAdapter(paths, this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(itListener);

        iv.setOnClickListener(clickListener);
        toggle();

        if (paths.size() > 0)
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
           toggle();
        }
    };

    /**
     * 刷新数据库内容
     */
    private void refreshDb(){
        PhotoDbHelper mHelper = new PhotoDbHelper(this);
        db = mHelper.getWritableDatabase();

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

        if (paths == null)
            paths = new ArrayList<String>();
        else{
            paths.clear();
        }

        while (c.moveToNext()){
            paths.add(c.getString(0));
        }

        for (int i = 0 ;i <paths.size();i++){
            Log.i("path",paths.get(i));
        }

        c.close();
        db.close();
    }

    /**
     * 打开 or 关闭底部快速选择框
     */
    private void toggle(){
        DisplayMetrics out = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(out);
        int wHeight = out.heightPixels;
        int lHeight= list.getMeasuredHeight();

        if (isOpen){
            float y = wHeight;
            list.animate().y(y);
            isOpen = false;
        }else{
            float y = wHeight - lHeight;
            list.animate().y(y);
            isOpen = true;
        }
    }

    /**
     * handler, timer, onStart, onStop负责切换图片定时的功能
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (paths.size() <= 1) return;

            if (msg.what == 1) {
                int index = new Random().nextInt(paths.size());
                loader.displayImage("file://" + paths.get(index), iv);
            }
        }
    };

    class Timer extends Thread{

        @Override
        public void run() {
            super.run();
            while (isRun){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        isRun = true;
        timer.start();

        IntentFilter ift = new IntentFilter();
        ift.addAction(FileService.REFRESH);
        registerReceiver(DbChange, ift);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
        unregisterReceiver(DbChange);
        timer = null;
    }

    /**
     * 通知Activity图片数据库有变化
     */
    private BroadcastReceiver DbChange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == FileService.REFRESH){
                refreshDb();
                adapter.notifyDataSetChanged();
            }
        }

    };
}
