package com.hmammon.familyphoto.utils;

import android.net.wifi.ScanResult;

import java.util.Comparator;

/**
 * Created by icyfox on 2014/12/24.
 * WiFi强度比较器
 */
public class WifiComparator implements Comparator<ScanResult> {

    @Override
    public int compare(ScanResult scanResult, ScanResult scanResult2) {
        return scanResult2.level - scanResult.level;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
