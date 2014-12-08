package com.hmammon.familyphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hmammon.familyphoto.receivers.TimeTickReceiver;

public class FileService extends Service {

    private TimeTickReceiver receiver;
    private SharedPreferences sp;
    private final String LASTTIME = "lasttime";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("ser","被启动了!");

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 开始下载进程
     */
    public void startDownload(){
        long now = System.currentTimeMillis();
        long last = sp.getLong(LASTTIME ,0L);
        if (now - last < 60 * 1000L) return;
        else {
            Log.i("down","开始下载啦！" + now + " " + last);
            sp.edit().putLong(LASTTIME,now).commit();
        }
    }

}
