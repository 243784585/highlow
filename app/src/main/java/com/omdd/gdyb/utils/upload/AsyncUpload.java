package com.omdd.gdyb.utils.upload;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.omdd.gdyb.bean.FlashAirFile;
import com.omdd.gdyb.main.TransferActivity;
import com.omdd.gdyb.utils.Constant;
import com.omdd.gdyb.utils.Session;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 2016/6/21 0021.
 * 0：成功；1：数据类型不正确； 2:文件已存在；3:用户信息错误，请重新登录 ；4:系统错误；
 */
public class AsyncUpload extends AsyncTask<FlashAirFile,Integer,Integer>{

    private TransferActivity ta;

    public AsyncUpload(TransferActivity ta){
        this.ta = ta;
    }


    @Override
    protected Integer doInBackground(FlashAirFile... params) {
        /*boolean flag = true;
        if (flag) {
            params[0].state = FlashAirFile.STATE_UPLOADED;
            TransferActivity.fileDao.updateFlashAirFile(params[0]);
            return null;
        }*/
        SoapObject result= null;
        try {
            result = CallWebService(params[0]);//请求
        } catch (Exception e) {
            Log.e("ERROR",e.getMessage());
            e.printStackTrace();
            params[0].state = FlashAirFile.STATE_UPFAILED;
        }
        String respRes = null;
        if(result != null) {
             respRes = result.getProperty("String").toString();
        }

        if(respRes!=null && !respRes.isEmpty()) {
            Log.e("upload",params[0].fileName + "\tstatusCode:" + respRes);
            new File(params[0].localPath, params[0].pileNo + ".upload").renameTo(new File(params[0].localPath, params[0].fileName));
            if("0".equals(respRes)){
                //0：成功；
                params[0].state = FlashAirFile.STATE_UPLOADED;
            }else if("1".equals(respRes)){
                //1：数据类型不正确；
                params[0].state = FlashAirFile.STATE_DATETYPEERROR;
            }else if("2".equals(respRes)){
                //2:文件已存在；
                params[0].state = FlashAirFile.STATE_EXIST;
            }else if("3".equals(respRes)){
                //3:用户信息错误，请重新登录 ；
                params[0].state = FlashAirFile.STATE_RELOGIN;
            }else if("4".equals(respRes)){
                //4:系统错误；
                params[0].state = FlashAirFile.STATE_SYSTEMERROR;
            }
        }
        TransferActivity.fileDao.updateFlashAirFile(params[0]);
        return null;
    }

    /**
     *
     * @param result 文件名
     */
    @Override
    protected void onPostExecute(Integer result) {
        ta.onUploaded();
    }

    private SoapObject CallWebService(FlashAirFile file) throws Exception{
        SoapObject request=new SoapObject(Constant.NAMESPACE,Constant.METHOD_UPLOAD);

        setParams(request,file);//设置参数
        request.addProperty("data", Base64Encode(file));

        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.dotNet=true;
        envelope.setOutputSoapObject(request);
        envelope.encodingStyle="UTF-8";
        envelope.bodyOut=request;

        HttpTransportSE hts=new HttpTransportSE(Constant.URL_MAIN);
        String SOAP_action=Constant.NAMESPACE+Constant.METHOD_UPLOAD;
        hts.call( SOAP_action, envelope);

        return (SoapObject) envelope.bodyIn;
    }

    private String Base64Encode(FlashAirFile file){
        String in64 = null;
        FileInputStream in = null;
        try {
            File upFile = new File(file.localPath, file.fileName);
            File upload = new File(file.localPath, file.pileNo + ".upload");
            upFile.renameTo(upload);
            file.state = FlashAirFile.STATE_UPLOADING;
            TransferActivity.fileDao.updateFlashAirFile(file);
            in = new FileInputStream(upload);
            byte[] buf = new byte[1024];
            byte[] fileByte = new byte[file.size = in.available()];
            int len;
            int percent = 0;
            while ((len = in.read(buf)) != -1){
                System.arraycopy(buf,0,fileByte,percent,len);
                percent += len;
            }
            in64 = Base64.encodeToString(fileByte,Base64.DEFAULT);
//            upload.renameTo(upFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return in64;
    }

    private void setParams(SoapObject request,FlashAirFile file){
        request.addProperty("guid", Session.getString(Session.KEY_GUID));//guid
        request.addProperty("planNo",file.planNo);//计划号
        request.addProperty("pileNo",file.pileNo);//桩号
        request.addProperty("fileName",file.fileName);//文件名
        request.addProperty("dataType",Session.getString(Session.KEY_DATATYPE));//dataType
        request.addProperty("testType",Session.getString(Session.KEY_TESTTYPE));//testType
        request.addProperty("testMethod",Session.getString(Session.KEY_TESTMETHOD));//testMethod
        request.addProperty("fileType",Session.getString(Session.KEY_FILETYPE));//fileType
    }
}
