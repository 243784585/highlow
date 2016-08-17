package com.omdd.gdyb.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/6/11 0011.
 */
public class NetworkUtil {

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     * public boolean isNetworkConnected(Context context) {
    if (context != null) {
    ConnectivityManager mConnectivityManager = (ConnectivityManager) context
    .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    if (mNetworkInfo != null) {
    return mNetworkInfo.isAvailable();
    }
    }
    return false;
    }
     */


    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return
     * public boolean isWifiConnected(Context context) {
    if (context != null) {
    ConnectivityManager mConnectivityManager = (ConnectivityManager) context
    .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWiFiNetworkInfo = mConnectivityManager
    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (mWiFiNetworkInfo != null) {
    return mWiFiNetworkInfo.isAvailable();
    }
    }
    return false;
    }
     */


    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return
     * public boolean isMobileConnected(Context context) {
    if (context != null) {
    ConnectivityManager mConnectivityManager = (ConnectivityManager) context
    .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mMobileNetworkInfo = mConnectivityManager
    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if (mMobileNetworkInfo != null) {
    return mMobileNetworkInfo.isAvailable();
    }
    }
    return false;
    }
     */


    /**
     * 获取当前网络连接的类型信息
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 是否连接上FlashAir设备
     * @param context
     * @return
     */
    public static boolean isFlashAir(Context context,WifiAdmin admin){
        if(context == null)throw new IllegalArgumentException("context为空");

        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        return info == null ? false:info.getType()==1?Constant.WIFI_SSID.equals(admin.getSSID().replace("\"","")):false;
    }

}
