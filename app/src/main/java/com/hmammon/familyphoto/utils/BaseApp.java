package com.hmammon.familyphoto.utils;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.hmammon.familyphoto.FileService;
import com.hmammon.familyphoto.http.HttpHelper;
import com.hmammon.familyphoto.utils.ImageHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by icyfox on 2014/11/30.
 */
public class BaseApp extends Application {

    private static Application app = null;

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
    }

    public static Application getInstance(){
        return app;
    }

    public static String getDeviceId(){
        String id = Build.SERIAL;
//        return "359209020434936";
        return id;
    }

}
