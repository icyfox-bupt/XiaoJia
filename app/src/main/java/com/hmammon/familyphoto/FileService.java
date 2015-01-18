package com.hmammon.familyphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.hmammon.familyphoto.http.GetNewPhoto;
import com.hmammon.familyphoto.receivers.TimeTickReceiver;
import com.hmammon.familyphoto.utils.BaseApp;

public class FileService extends Service {

    private TimeTickReceiver receiver;
    private SharedPreferences sp;
    private final String LASTTIME = "lasttime";
    public static final String REFRESH = "action_refresh";
    public static final String START_DOWN = "start_down";
    public static final String STOP_DOWN = "stop_down";
    public boolean isDownloading = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("ser", "被启动了!");

        BaseApp.getInstance().service = this;

        receiver = new TimeTickReceiver(this);
        IntentFilter ift = new IntentFilter();
        ift.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, ift);

        sp = getSharedPreferences("xiaojia", Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i("ser", "被结束了!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 开始下载进程
     */
    public void startDownload() {
        long now = System.currentTimeMillis();
        long last = sp.getLong(LASTTIME, 0L);
        if (now - last < 7200 * 1000L) return;
        else {
            isDownloading = true;
            BaseApp.getInstance().activity.setDownloading(true, 0);
            BaseApp.getInstance().activity.manual = false;
            Log.i("down", "开始下载啦！" + now + " " + last);
            sp.edit().putLong(LASTTIME, now).commit();
            new GetNewPhoto().start();
        }
    }

    /**
     * 手动开始下载
     */
    public void startManual() {
        if (isDownloading) return;
        long now = System.currentTimeMillis();

        isDownloading = true;
        BaseApp.getInstance().activity.setDownloading(true, 0);
        BaseApp.getInstance().activity.manual = true;
        Log.i("down", "开始下载啦！" + now);
        sp.edit().putLong(LASTTIME, now).apply();
        new GetNewPhoto().start();
    }

}
