package com.hmammon.familyphoto.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.hmammon.familyphoto.Photo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Random;

/**
 * Created by icyfox on 2014/12/23.
 */
public class ImageManager {

    Activity activity;
    ImageView imageView;
    ImageLoader loader;
    boolean isRun = false;
    private int duration = 10000;
    List<Photo> photos;
    private Timer timer;
    private int current = 0;

    public ImageManager(ImageView imageView, Activity activity, List<Photo> photos) {
        this.imageView = imageView;
        this.activity = activity;
        this.loader = ImageLoader.getInstance();
        this.photos = photos;
    }

    public void next(){
        current++;
        if (current > photos.size() - 1) current = 0;
        display(current);
    }

    public void previous(){

    }

    public void start(){
        if (isRun) return;
        isRun = true;
        timer = new Timer();
        timer.start();
    }

    public void pause(){
        isRun = false;
        timer = null;
    }

    public void show(int index){
        display(index);
    }

    /**
     * handler, timer, onStart, onStop负责切换图片定时的功能
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (photos.size() <= 1) return;

            if (msg.what == 1) {
                next();
            }
        }
    };

    class Timer extends Thread{

        @Override
        public void run() {
            super.run();
            while (isRun){
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                }
                if (isRun)
                    handler.sendEmptyMessage(1);
            }
        }
    };

    private void display(int index){
        loader.displayImage("file://" + photos.get(index).path, imageView);
    }
}
