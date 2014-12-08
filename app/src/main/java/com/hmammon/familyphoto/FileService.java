package com.hmammon.familyphoto;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class FileService extends Service {

    private TimeTickReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new TimeTickReceiver();
        IntentFilter ift = new IntentFilter();
        ift.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, ift);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public FileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
