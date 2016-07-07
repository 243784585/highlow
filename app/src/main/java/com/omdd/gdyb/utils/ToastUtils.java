package com.omdd.gdyb.utils;

import android.content.Context;
import android.widget.Toast;


import com.omdd.gdyb.application.BaseApplication;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2016-02-24.
 */
public class ToastUtils {
    public static void alert(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    private static WeakReference<Toast> toast = null;

    //连续吐司
    public static void showTextToast(String msg) {
        if (toast == null || toast.get() == null) {
            toast = new WeakReference<Toast>(Toast.makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_SHORT));
        } else {
            toast.get().setText(msg);
        }
        toast.get().show();
    }

    public static void showTextToast(int msg) {
        if (toast == null || toast.get() == null) {
            toast = new WeakReference<Toast>(Toast.makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_SHORT));
        } else {
            toast.get().setText(msg);
        }
        toast.get().show();
    }

}
