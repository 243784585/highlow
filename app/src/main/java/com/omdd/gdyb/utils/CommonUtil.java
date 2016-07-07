package com.omdd.gdyb.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class CommonUtil {


    /**
     * 获取默认DisplayMetrics
     * @param context 上下文
     * @return DisplayMetrics 默认DM
     */
    public static DisplayMetrics getDefaultDisplayMetrics(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     *
     * 获取屏幕宽度
     * @param context 上下文
     * @return int 屏幕宽度
     */
    public static int getDeviceSreenWidth(Context context){
        return getDefaultDisplayMetrics(context).widthPixels;
    }

    /**
     *
     * 获取屏幕宽度
     * @param context 上下文
     * @return int 屏幕宽度
     */
    public static int getDeviceSreenHeight(Context context){
        return getDefaultDisplayMetrics(context).heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isEmpty(String target){
        return target == null || "".equals(target);
    }

    public static String intToUnit(int bit){
        String result = "";
        if(bit < 1024){
            result = bit+"B";
        }else if(bit < 1024 * 1024){
            result = bit/1024 + "KB";
        }else if(bit < 1024 * 1024 * 1024){
            result = bit / (1024*1024) + "M";
        }
        return result;
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 弹出进度对话框
     * @param dialog
     * @param content
     */
    public static void showWaitDialog(ProgressDialog dialog, String content){
        dialog.setMessage("正在获取数据,请稍后...");
        dialog.setCancelable(false);
        dialog.show();
    }
}
