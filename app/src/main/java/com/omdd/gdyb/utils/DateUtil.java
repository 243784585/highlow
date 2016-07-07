package com.omdd.gdyb.utils;

import com.omdd.gdyb.application.BaseApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class DateUtil {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");
    private static StringBuilder sb = new StringBuilder();

    /**
     * FlashAir文件时间转换成毫秒值
     * @param date
     * @param time
     * @return
     */
    public static long BinaryToDate(int date,int time){
        sb.setLength(0);
        String binaryDate = Integer.toBinaryString(date);
        int year = -1 , month = -1 , day = -1 , hour = -1 , min = -1;
        if(binaryDate.length() < 16){
            //15位
            year = 1980 + Integer.parseInt(binaryDate.substring(0, 6),2);//年份
            month = Integer.parseInt(binaryDate.substring(6, 10),2);//月份
            day = Integer.parseInt(binaryDate.substring(10, 15),2);//日
        }else{
            //16位
            year = 1980 + Integer.parseInt(binaryDate.substring(0, 7),2);//年份
            month = Integer.parseInt(binaryDate.substring(7, 11),2);//月份
            day = Integer.parseInt(binaryDate.substring(11, 16),2);//日
        }

        binaryDate = Integer.toBinaryString(time);
        if(binaryDate.length() < 16){
            //15位
            hour = Integer.parseInt(binaryDate.substring(0, 4),2);
            min = Integer.parseInt(binaryDate.substring(4, 10),2);
        }else{
            //16位
            hour = Integer.parseInt(binaryDate.substring(0, 5),2);
            min = Integer.parseInt(binaryDate.substring(5, 11),2);
        }

        try {
            return dateFormat.parse(sb.append(year).append("-").append(month).append("-").append(day).append(" ").append(hour).append(":").append(min).toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long parse(String time) throws ParseException {
        return dateFormat.parse(time).getTime();
    }

    public static long parseDate(String time) throws ParseException {
        return fileFormat.parse(time).getTime();
    }

    public static String format(long time){
        return dateFormat.format(new Date(time));
    }

    public static String getFilePath(){
        return BaseApplication.getInstance().getExternalCacheDir().getAbsolutePath()+"/"+fileFormat.format(new Date())+"/" + (new GregorianCalendar().get(GregorianCalendar.AM_PM) == 0 ? "am" : "pm") ;
    }
}
