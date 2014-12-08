package com.hmammon.familyphoto.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hmammon.familyphoto.FileService;

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
        Log.i("rec", intent.getAction());
        service.startDownload();
    }
}
