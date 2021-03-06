package com.hmammon.familyphoto.utils;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.hmammon.familyphoto.Photo;
import com.hmammon.familyphoto.ui.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by icyfox on 2014/12/23.
 */
public class ImageManager {

    MainActivity activity;
    ImageView imageView;
    ImageLoader loader;
    boolean isRun = false;
    private final int duration = 10000;
    List<Photo> photos;
    private Timer timer;
    private int current = 0;

    public ImageManager(ImageView imageView, MainActivity activity, List<Photo> photos) {
        this.imageView = imageView;
        this.activity = activity;
        this.loader = ImageLoader.getInstance();
        this.photos = photos;
    }

    public void next(){
        current++;
        if (current > photos.size() - 1) current = 0;
        display(current);
        activity.lightView(current);
        activity.checkNet();
    }

    public void previous(){
        current--;
        if (current < 0) current = photos.size() - 1;
        display(current);
        activity.lightView(current);
        activity.checkNet();
    }

    public void start(){
        if (isRun) return;
        isRun = true;
        timer = new Timer();
        timer.start();
    }

    public void pause(){
        isRun = false;
        if (timer != null) {
            timer.exit();
            timer = null;
        }
    }

    public void show(int index){
        if (current == index) return;
        current = index;
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

        boolean exit = false;

        @Override
        public void run() {
            super.run();
            while (isRun){
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                }
                if (exit) break;

                if (isRun)
                    handler.sendEmptyMessage(1);

            }
        }

        public void exit(){
            exit = true;
        }
    };

    private void display(int index){
        loader.displayImage("file://" + photos.get(index).path, imageView);
        activity.setNew(photos.get(index));
        activity.list.setSelection(index);
        activity.list.scrollTo(index * 100 - 450);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
