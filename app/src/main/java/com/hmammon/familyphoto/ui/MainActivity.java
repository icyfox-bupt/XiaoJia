package com.hmammon.familyphoto.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hmammon.familyphoto.FileService;
import com.hmammon.familyphoto.Photo;
import com.hmammon.familyphoto.PhotoAdapter;
import com.hmammon.familyphoto.R;
import com.hmammon.familyphoto.db.PhotoContract;
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.utils.BaseActivity;
import com.hmammon.familyphoto.utils.BaseApp;
import com.hmammon.familyphoto.utils.HorizontalListView;
import com.hmammon.familyphoto.utils.ImageManager;
import com.hmammon.familyphoto.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    private SQLiteDatabase db;
    private HorizontalListView list;
    private PhotoAdapter adapter;
    private ImageView iv;
    private ArrayList<Photo> photos;
    private ImageLoader loader;
    private boolean isOpen = true, isFirst;
    private Button btnWifi;
    private ImageManager manager;
    private FragmentManager fragMana;
    private FrameLayout topbar;
    public Fragment fragWifi, fragNopic, fragSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragMana = getFragmentManager();

        list = (HorizontalListView) findViewById(R.id.listView);
        iv = (ImageView)findViewById(R.id.imageView);
        loader = ImageLoader.getInstance();
        btnWifi = (Button)findViewById(R.id.btn_wifi);
        topbar = (FrameLayout) findViewById(R.id.topbar);

        fragNopic = new NopicFragment();
        fragSMS = new SMSFragment();
        fragWifi = new WifiFragment();

        refreshDb();

        adapter = new PhotoAdapter(photos, this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(itListener);

        iv.setOnClickListener(clickListener);
        btnWifi.setOnClickListener(clickListener);

        manager = new ImageManager(iv, this, photos);

        if (photos.size() > 0)
            loader.displayImage("file://" + photos.get(0).path, iv);
        else
            iv.setImageResource(R.drawable.bg_nopic);

        //友盟更新
        UmengUpdateAgent.setUpdateCheckConfig(false);
        UmengUpdateAgent.update(this);
        MobclickAgent.updateOnlineConfig(this);
    }

    private AdapterView.OnItemClickListener itListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            manager.show(position);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if (view.getId() == R.id.imageView){
               toggle();
           }
           else if (view.getId() == R.id.btn_wifi){
               FragmentTransaction ft = fragMana.beginTransaction();
               ft.add(R.id.container, fragWifi);
               ft.commit();
           }
        }
    };

    /**
     * 刷新数据库内容
     */
    private void refreshDb(){
        PhotoDbHelper mHelper = new PhotoDbHelper(this);
        db = mHelper.getWritableDatabase();

        String[] projection = {
                PhotoContract.COLUMN_NAME_PHOTO_PATH,
                PhotoContract.COLUMN_NAME_PHOTO_THUMB
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
        else{
            photos.clear();
        }

        while (c.moveToNext()){
            Photo p = new Photo();
            p.path = c.getString(0);
            p.thumb = c.getString(1);
            photos.add(p);
        }

        for (int i = 0 ;i <photos.size();i++){
            Log.i("path", photos.get(i).path);
        }

        if (photos.size() == 0)
            fragMana.beginTransaction().add(R.id.container, fragNopic).commit();

        c.close();
        db.close();
    }

    /**
     * 打开 or 关闭底部快速选择框
     */
    private void toggle(){
        Point size = Tools.getScreenSize(this);
        int wHeight = size.y;

        int lHeight= list.getMeasuredHeight();
        int barHeight = topbar.getMeasuredHeight();

        if (isOpen){
            float y = wHeight;
            list.animate().y(y);
            topbar.animate().y(0 - barHeight);
            isOpen = false;
            manager.start();
        }else{
            float y = wHeight - lHeight;
            list.animate().y(y);
            topbar.animate().y(0);
            isOpen = true;
            manager.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.start();

        IntentFilter ift = new IntentFilter();
        ift.addAction(FileService.REFRESH);
        registerReceiver(DbChange, ift);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(DbChange);
        manager.pause();
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

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    Log.i("key", keyCode + " " +event);
    switch (keyCode){
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_BACK: {
            return true;
        }
        default:
            return super.onKeyDown(keyCode, event);
    }
    }

    /**
     * 防止在OnCreate里写导致View得不到实际高度的情况
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isFirst) {
            toggle();
            isFirst = true;
        }
    }
}
