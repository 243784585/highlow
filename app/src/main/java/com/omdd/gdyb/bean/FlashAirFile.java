package com.omdd.gdyb.bean;

import com.omdd.gdyb.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/14 0014.
 * desc:FlashAir设备文件(目录)
 */
public class FlashAirFile implements Serializable {

    public int _id;
    public String pileNo;
    public String planNo;
    public String localPath;
    public String flashAirPath;
    public String fileName;
    public int size;
    public long fileTime;//毫秒值
    public int state;

    /**未下载*/
    public static final int STATE_UNLOAD = 1;
    /**下载中*/
    public static final int STATE_LOADING = 2;
    /**下载失败*/
    public static final int STATE_LOADFAILED = 3;
    /**已下载*/
    public static final int STATE_LOADED = 4;
    /**上传中*/
    public static final int STATE_UPLOADING = 5;
    /**上传失败*/
    public static final int STATE_UPFAILED = 6;
    /**已上传*/
    public static final int STATE_UPLOADED = 7;
    /**文件已存在*/
    public static final int STATE_EXIST = 8;
    /**数据类型不正确*/
    public static final int STATE_DATETYPEERROR = 9;
    /**系统错误*/
    public static final int STATE_SYSTEMERROR = 10;
    /**重新登录*/
    public static final int STATE_RELOGIN = 11;

    public FlashAirFile(String file){
        String[] attr = file.split(",");
        this.flashAirPath = attr[0];
        this.fileName = attr[1];
        this.size = Integer.parseInt(attr[2]);
        this.fileTime = DateUtil.BinaryToDate(Integer.parseInt(attr[4]),Integer.parseInt(attr[5]));
    }

    public FlashAirFile(int _id, String planNo, String localPath, String flashAirPath, String fileName, int size, long fileTime, int state) {
        this._id = _id;
        this.planNo = planNo;
        this.localPath = localPath;
        this.flashAirPath = flashAirPath;
        this.fileName = fileName;
        this.size = size;
        this.fileTime = fileTime;
        this.pileNo = fileName.substring(0,fileName.indexOf("."));
        this.state = state;
    }

    /**
     * 获取文件后缀名
     * @return
     */
    public String getPostfix(){
        return fileName.substring(fileName.indexOf("."));
    }
}
