package com.hmammon.familyphoto.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by icyfox on 2014/12/26.
 */
public class SPHelper {

    private final static String NAME = "xiaojia";
    private static Context context = BaseApp.getInstance();
    private static SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor = sp.edit();

    public static void setOnOffTime(String locktime, String starttime){
        editor.putString("locktime", locktime);
        editor.putString("starttime", starttime);
        editor.commit();
    }

    public static String getOnTime(){
        return sp.getString("starttime", "");
    }

    public static String getOffTime(){
        return sp.getString("locktime", "");
    }

    public static void setSMSTime(){
        editor.putLong("smstime", System.currentTimeMillis());
        editor.commit();
    }

    public static long getSMSTime(){
        return sp.getLong("smstime", 0L);
    }

    public static void setFirst(){
        editor.putBoolean("first", false).commit();
    }

    public static boolean isFirst(){
        return sp.getBoolean("first", true);
    }

    public static void setSend(){
        editor.putBoolean("sended", true).commit();
    }

    public static boolean isSend(){
        return sp.getBoolean("sended", false);
    }
}
