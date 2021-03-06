package com.hmammon.familyphoto.utils;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.hmammon.familyphoto.FileService;
import com.hmammon.familyphoto.http.HttpHelper;
import com.hmammon.familyphoto.ui.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by icyfox on 2014/11/30.
 */
public class BaseApp extends Application {

    private static BaseApp app = null;
    public FileService service;
    public MainActivity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        //确认临时保存文件夹存在
        File savePath = new File(HttpHelper.SAVEPATH);
        if (!savePath.exists())
            savePath.mkdirs();

        //初始化图片加载器
        ImageLoader.getInstance().init(ImageHelper.config);

        Intent it = new Intent(this, FileService.class);
        startService(it);

        setClock();
    }

    public static BaseApp getInstance(){
        return app;
    }

    public static String getDeviceId(){
        String id = getSerialNumber();
        Log.i("id", id);
        return id;
    }

    /** 
     * getSerialNumber 
     * @return result is same to getSerialNumber1() */
    public static String getSerialNumber(){
        String serial = null;
        try {
            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get",String.class);
            serial = (String)get.invoke(c,"ro.serialno");
        }catch(Exception e){
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 设置自动睡眠时间
     */
    public void setClock(){
        String lockTime = SPHelper.getOffTime();
        String startTime = SPHelper.getOnTime();

        if (TextUtils.isEmpty(lockTime) || TextUtils.isEmpty(startTime)) return;

        String[] locks = lockTime.split(":");
        String[] starts = startTime.split(":");

        int lockH = Integer.valueOf(locks[0], 10);
        int lockM = Integer.valueOf(locks[1], 10);
        int startH = Integer.valueOf(starts[0], 10);
        int startM = Integer.valueOf(starts[1], 10);

        Calendar now = Calendar.getInstance(Locale.CHINA);
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);

        int nYear = now.get(Calendar.YEAR);
        int nMonth = now.get(Calendar.MONTH) + 1;
        int nDate = now.get(Calendar.DATE);

        //无论如何都加一天，因为开机是第二天的事
        now.add(Calendar.DATE, 1);
        int pYear = now.get(Calendar.YEAR);
        int pMonth = now.get(Calendar.MONTH) + 1;
        int pDate = now.get(Calendar.DATE);


        Intent intent = new Intent("android.56iq.intent.action.setpoweronoff");
        //下次开机具体日期时间，即在2014/9/1 8:30会开机
        int[] timeonArray = {pYear, pMonth, pDate, startH, startM};

        //下次关机具体日期时间， 即在2014/10/1 8:30 会关机
        int[] timeoffArray = {nYear, nMonth, nDate, lockH, lockM};

        intent.putExtra("timeon",timeonArray);
        intent.putExtra("timeoff",timeoffArray);
        intent.putExtra("enable",true); //使能开关机功能，设为false,则为关闭
        sendBroadcast(intent);
    }

}
