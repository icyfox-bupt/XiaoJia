package com.hmammon.familyphoto.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hmammon.familyphoto.FileService;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created by icyfox on 2014/12/8.
 * 接收每分钟自动发送的消息，用来进行定时轮询
 */
public class TimeTickReceiver extends BroadcastReceiver {

    FileService service;

    public TimeTickReceiver(FileService service) {
        super();
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long time = System.currentTimeMillis();
        Log.i("rec", intent.getAction());
        service.startDownload();

        //一小时更新一次
        if (time / 1000 % 3600 == 0){
            UmengUpdateAgent.setUpdateCheckConfig(false);
            UmengUpdateAgent.update(context);
        }


    }
}
