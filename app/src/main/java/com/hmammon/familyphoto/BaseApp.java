package com.hmammon.familyphoto;

import android.app.Application;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.hmammon.familyphoto.http.HttpHelper;
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

        Toast.makeText(this,"设备号:" + getDeviceId(), Toast.LENGTH_LONG).show();
    }

    public static Application getInstance(){
        return app;
    }

    public static String getDeviceId(){
        TelephonyManager tm = (TelephonyManager) getInstance().
                getSystemService(TELEPHONY_SERVICE);
//        return "359209020434936";
        return tm.getDeviceId();
    }

}
