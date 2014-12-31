package com.hmammon.familyphoto.utils;

import android.net.NetworkInfo;

/**
 * Created by icyfox on 2014/12/25.
 */
public class WifiHelper {

    public static String getInfo (NetworkInfo.DetailedState state){
        switch (state){
            case AUTHENTICATING:
                return "Network link established, performing authentication.";
            case BLOCKED :
                return "Access to this network is blocked.";
            case CAPTIVE_PORTAL_CHECK :
                return "Checking if network is a captive portal.";
            case CONNECTED :
                return "已连接";
            case DISCONNECTED :
                return "请等待";
            case DISCONNECTING :
                return "连接断开中";
            case FAILED :
                return "连接失败";
            case IDLE :
                return "网络连接就绪";
            case OBTAINING_IPADDR  :
                return "获取IP地址中...";
            case SCANNING  :
                return "网络扫描中...";
            case SUSPENDED  :
                return "连接中断";
            case VERIFYING_POOR_LINK   :
                return "连接信号较差";
        }
        return "none";
    }
}
