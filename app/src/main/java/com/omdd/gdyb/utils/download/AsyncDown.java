package com.omdd.gdyb.utils.download;

import android.os.AsyncTask;
import android.util.Log;

import com.omdd.gdyb.bean.FlashAirFile;
import com.omdd.gdyb.main.TransferActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class AsyncDown extends AsyncTask<FlashAirFile,Integer,Integer> {


    private TransferActivity ta;

    public AsyncDown(TransferActivity ta){
        this.ta = ta;
    }


    /**
     * 第0个是url,第1个是路径,第2个文件名,
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(FlashAirFile... params) {
        //已下载直接返回
        if(params[0].state == FlashAirFile.STATE_LOADED)return null;
        FileOutputStream fo = null;
        InputStream inputStream = null;
        File file = null;
        try{
            URL url = new URL("http://flashair"+params[0].flashAirPath +File.separator+ URLEncoder.encode(params[0].fileName));
            URLConnection urlCon = url.openConnection();
            urlCon.connect();
            inputStream = urlCon.getInputStream();
            byte[] byteChunk = new byte[1024];
            int bytesRead = 0;
            file = new File(params[0].localPath);
            if(!file.exists()){
                file.mkdirs();
            }
            file = new File(params[0].localPath,params[0].fileName.substring(0,params[0].fileName.indexOf("."))+".download");
            fo = new FileOutputStream(file);
            params[0].state = FlashAirFile.STATE_LOADING;//下载中
            ta.fileDao.updateFlashAirFile(params[0]);

            while( (bytesRead = inputStream.read(byteChunk)) != -1) {
                fo.write(byteChunk, 0, bytesRead);
            }

            params[0].state = FlashAirFile.STATE_LOADED;
            ta.fileDao.updateFlashAirFile(params[0]);

            if(file != null){
                file.renameTo(new File(params[0].localPath,params[0].fileName));
            }

        }catch(Exception e) {
            //下载失败
            Log.e("ERROR", "ERROR: " + e.toString());

            params[0].state = FlashAirFile.STATE_LOADFAILED;
            ta.fileDao.updateFlashAirFile(params[0]);

            e.printStackTrace();
        }finally {
            if(inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fo != null) try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        ta.onDownLoaded();
    }
}
