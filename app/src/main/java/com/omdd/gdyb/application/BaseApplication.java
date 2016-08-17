package com.omdd.gdyb.application;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.omdd.gdyb.utils.DateUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/6/11 0011.
 */
public class BaseApplication extends Application {

    /**
     * 工作线程Handler
     */
    private Handler workHandler;
    private HandlerThread workThread;

    private static BaseApplication instance;
    public static String downLoadPath;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        workThread = new HandlerThread("workThread");
        workThread.start();

        workHandler = new Handler(workThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        File file = new File(downLoadPath = DateUtil.getFilePath());
        if(!file.exists()) file.mkdirs();
        CrashHandler.getInstance().init(this);
    }

    /** 获取工作线程轮询器 */
    public Looper getWorkLooper(){
        return workHandler.getLooper();
    }

    public Handler getWorkHandler(){
        return workHandler;
    }

    public static BaseApplication getInstance(){
        return instance;
    }

}
