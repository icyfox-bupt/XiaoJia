package com.hmammon.familyphoto.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hmammon.familyphoto.db.PhotoDbHelper;
import com.hmammon.familyphoto.utils.BaseActivity;
import com.hmammon.familyphoto.utils.HorizontalListView;
import com.hmammon.familyphoto.utils.ImageHelper;
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
    private int bigItem = 0;

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

        manager = new ImageManager(iv, this, photos);
        refreshDb();

        adapter = new PhotoAdapter(photos, this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(itListener);
        list.setOnItemLongClickListener(ilListener);

        iv.setOnClickListener(clickListener);
        btnWifi.setOnClickListener(clickListener);


        if (photos.size() > 0)
            loader.displayImage("file://" + photos.get(0).path, iv, ImageHelper.options);
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
            if (bigItem == position) return;
            manager.show(position);
            lightView(position);
        }
    };

    private AdapterView.OnItemLongClickListener ilListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (bigItem == i) return false;
            manager.show(i);
            lightView(i);
            return false;
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
        photos = mHelper.getPhotoInDb(photos);

        if (photos.size() == 0)
            fragMana.beginTransaction().add(R.id.container, fragNopic).commit();

        manager.setPhotos(photos);
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

    /**
     * 设置某个list item为高亮显示
     * @param position
     */
    public void lightView(int position){
        adapter.setChecked(position);
        adapter.notifyDataSetChanged();
        list.invalidate();

        /*
        int top = list.getFirstVisiblePosition();
        int bottom = list.getLastVisiblePosition();

        View child1, child2, card1, card2, tmp1, tmp2;

        //先设置已经变大的变小
        if (bigItem >= top && bigItem <= bottom) {
            child1 = list.getChildAt(bigItem - top);
            card1 = child1.findViewById(R.id.card);
            tmp1 = child1.findViewById(R.id.tmp);

            Log.w("size1", child1.getMeasuredWidth() + " - " + child1.getMeasuredHeight() + " - " + card1.getMeasuredWidth() + " - " + card1.getMeasuredHeight()
                    + " - " + tmp1.getMeasuredWidth() + " - " + tmp1.getMeasuredWidth());

            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) card1.getLayoutParams();
            int smallSize = Tools.dp2px(this, 50);
            lp1.width = smallSize;
            lp1.height = smallSize;
            card1.setLayoutParams(lp1);
            child1.measure(-2, -2);

            Log.w("size1", child1.getMeasuredWidth() + " - " + child1.getMeasuredHeight() + " - " + card1.getMeasuredWidth() + " - " + card1.getMeasuredHeight()
                    + " - " + tmp1.getMeasuredWidth() + " - " + tmp1.getMeasuredWidth());
        }

        //再设置小的变大
        child2 = list.getChildAt(position - top);
        card2 = child2.findViewById(R.id.card);
        tmp2 = child2.findViewById(R.id.tmp);

        Log.w("size2", child2.getMeasuredWidth() + " - " + child2.getMeasuredHeight() + " - " + card2.getMeasuredWidth() + " - " + card2.getMeasuredHeight()
                + " - " + tmp2.getMeasuredWidth() + " - " + tmp2.getMeasuredWidth());

        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) card2.getLayoutParams();
        int bigSize = Tools.dp2px(this, 120);
        lp2.width = bigSize;
        lp2.height = bigSize;
        card2.setLayoutParams(lp2);
        child2.measure(-2, -2);

        bigItem = position;

        Log.w("size2", child2.getMeasuredWidth() + " - " + child2.getMeasuredHeight() + " - " + card2.getMeasuredWidth() + " - " + card2.getMeasuredHeight()
                + " - " + tmp2.getMeasuredWidth() + " - " + tmp2.getMeasuredWidth());
*/
    }
}
