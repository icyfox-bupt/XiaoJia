package com.hmammon.familyphoto.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
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
            int widthPixels = realSize.x;
            int heightPixels = realSize.y;
            Log.i("size", widthPixels + " " + heightPixels);
        } catch (Exception ignored) {
        }
        return realSize;
    }

}
