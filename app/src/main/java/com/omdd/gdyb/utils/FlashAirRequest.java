package com.omdd.gdyb.utils;

import android.util.Log;

import com.omdd.gdyb.application.BaseApplication;
import com.omdd.gdyb.bean.FlashAirFile;
import com.omdd.gdyb.main.TransferActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/5/28 0028.
 */

public class FlashAirRequest
{
    /**
     * 往数据库里添加下载文件清单
     * @param cmd  http://flashair/DCIM
     * @return
     */
    public static void initDownloadList(String cmd){
        BufferedReader bufreader = null;
        try
        {
            Log.e("mURL", cmd);

            HttpURLConnection urlCon = (HttpURLConnection)new URL(cmd).openConnection();
            urlCon.setRequestMethod("GET");
            urlCon.connect();
            bufreader = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "UTF-8"));
            StringBuilder strbuf = new StringBuilder();
            String str;
            while ((str = bufreader.readLine()) != null)
            {
                strbuf.append(str).append("\n");
            }
            String[] files =  strbuf.toString().split("\\n");


            FlashAirFile faf = null;
            long targetTime = Session.getLong(Session.KEY_TIME);
            for(int i = 1 , len = files.length ; i<len ; i++){
                faf = new FlashAirFile(files[i]);
                if("syslog.txt".equals(faf.fileName)||"PData.DAT".equals(faf.fileName)||faf.fileName.startsWith("."))continue;
                if(faf.fileName.contains(".")){
                    //文件
                    if(faf.fileTime > targetTime) {
                        faf.planNo = TransferActivity.planNo;
                        if(TransferActivity.fileDao.queryFlashAirFileById(faf) == null){
                            //保存信息到数据库
                            faf.state = FlashAirFile.STATE_UNLOAD;
                            faf.localPath = BaseApplication.downLoadPath + faf.flashAirPath;
                            TransferActivity.fileDao.saveFlashAirFile(faf);
                        }else{
                            TransferActivity.fileDao.updateFlashAirFile(faf);
                        }
                    }
                }else{
                    //目录
                    initDownloadList(cmd + URLEncoder.encode(cmd.endsWith("/") ?faf.fileName:"/" + faf.fileName,"UTF-8"));
                }
            }

        }
        catch(MalformedURLException e)
        {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        catch(IOException e)
        {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        catch (Exception e){
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }finally {
            try {
                if(bufreader != null)
                    bufreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}