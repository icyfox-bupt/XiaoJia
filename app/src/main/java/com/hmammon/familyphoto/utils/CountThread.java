package com.hmammon.familyphoto.utils;

import android.os.Handler;
import android.util.Log;

/**
 * Created by icyfox on 2014/12/31.
 */
public class CountThread extends Thread {

    final int TOGGLETIME = 60;
    int count = TOGGLETIME;
    int msg = 0;
    private Handler handler;

    public CountThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        while (count >=0 ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count--;
            Log.i("th", "count " + count);
            if (msg != 0) break;
        }
            //do toggle
            Log.i("th", "toggle here!");
            handler.sendEmptyMessage(msg);
    }

    public void reset(){
        count = TOGGLETIME;
    }

    public void exit(){
        msg = -1;
    }

}
