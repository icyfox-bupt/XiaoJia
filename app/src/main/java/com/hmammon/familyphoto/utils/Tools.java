package com.hmammon.familyphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * xcfh9630 is key
 * Created by icyfox on 2014/12/23.
 */
public class Tools {

    public static Point getScreenSize(Activity activity) {
        Point realSize = null;
        try {
            WindowManager w = activity.getWindowManager();
            Display d = w.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            d.getMetrics(metrics);
            realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
        } catch (Exception ignored) {
        }
        return realSize;
    }

    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }

        return false ;
    }

}
