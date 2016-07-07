package com.omdd.gdyb.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.omdd.gdyb.application.BaseApplication;


/**
 * 将数据存到本地
 */
public class Session {

    /**  记录是否第一次进入应用  */
    public static final String KEY_FLAG_FIRST = "theFirst";
    /**  登录3和的账号  */
    public static final String KEY_LOGIN_USER = "user";
    /**  登录3和的密码  */
    public static final String KEY_LOGIN_PWD = "pwd";
    /**  Guid  */
    public static final String KEY_GUID = "guid";
    /**  指定时间  */
    public static final String KEY_TIME = "time";

    /**  dataType文件类型  */
    public static final String KEY_DATATYPE = "dataType";
    /**  testType检测类型  */
    public static final String KEY_TESTTYPE = "testType";
    /**  testMethod检测方法  */
    public static final String KEY_TESTMETHOD = "testMethod";
    /**  fileType检测项目  */
    public static final String KEY_FILETYPE = "fileType";



    private static final SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences("yym",
            Activity.MODE_PRIVATE);

    public static void setString(String key, Object value) {
        preferences.edit().putString(key, value.toString()).apply();
    }

    public static void setBoolean(String key, Boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public static void setInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public static void setLong(String key, long time){
        preferences.edit().putLong(key,time).apply();
    }

    public static Boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static Boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static String getString(String key) {
        return preferences.getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static long getLong(String key){
        return preferences.getLong(key,0);
    }

    public static long getLong(String key,long defValue){
        return preferences.getLong(key,defValue);
    }

    public static void clearSession() {
        preferences.edit().clear().apply();
    }

    public static void remove(String key){
        preferences.edit().remove(key);
    }
}