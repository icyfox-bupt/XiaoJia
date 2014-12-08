package com.hmammon.familyphoto.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hmammon.familyphoto.FileService;

/**
 * Created by icyfox on 2014/12/8.
 * 开机接收开机消息，然后自启动Service
 */
public class BootCompReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("rec",intent.getAction());

        Intent it = new Intent(context, FileService.class);
        context.startService(it);

    }
}
