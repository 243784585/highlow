package com.omdd.gdyb.utils.soap;

/**
 * Created by Administrator on 2016/5/29.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.omdd.gdyb.application.BaseApplication;
import com.omdd.gdyb.bean.NameValuePair;
import com.omdd.gdyb.bean.ProjectInfo;
import com.omdd.gdyb.utils.Constant;
import com.omdd.gdyb.utils.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Handler {
    private Handler mainHandler=null;
    String methodName=null;
    List<NameValuePair> params=null;
    private ProgressDialog progressDialog=null;

    public RequestHandler(Handler handler)
    {
        super(BaseApplication.getInstance().getWorkLooper());
        this.mainHandler=handler;
    }

    public void setProgressDialog(ProgressDialog pd){
        progressDialog = pd;
    }

    public void setParams(String methodName,List<NameValuePair> params)
    {
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public void handleMessage(Message msg) {

        Message message=mainHandler.obtainMessage();
        try
        {
            Log.i("testWebservice","start httpThread .....;\n  methodName is:" + methodName);

            SoapObject result=CallWebService();

            String respRes= result.getProperty("String").toString();

            if(respRes!=null && !respRes.isEmpty())
            {

                switch (result.getName()){
                    case Constant.RESPONSE_NAME_LOGIN:
                        //登录接口
                        login(message,respRes);
                        break;
                    case Constant.RESPONSE_NAME_PLANLIST:
                        //计划列表
                        parsePlans(message,respRes);
                        break;
                    /*case Constant.RESPONSE_NAME_PLAN:
                        //计划详情
                        parsePlan(message,respRes);
                        break;
                    case Constant.RESPONSE_NAME_UPLOAD:
                        //上传
                        upload(message,respRes);
                        break;*/
                    case Constant.RESPONSE_NAME_LOGOUT:
                        //退出
                        logout(message,respRes);
                        break;
                }

                //ResponseType.valueOf(respName).ordinal();

                mainHandler.sendMessage(message);
                if(progressDialog != null){
                    progressDialog.dismiss();
                }
            }else{

                Bundle b=new Bundle();
                b.putBoolean("data", false);

                message.what=3;
                message.setData(b);
                mainHandler.sendMessage(message);
            }

        }catch(Exception ex){
            Bundle b=new Bundle();
            message.what=3;
            b.putString("error", ex.getMessage());

            message.setData(b);
            mainHandler.sendMessage(message);

        }
    }

    /**
     * 登录
     * @param msg
     * @param resp
     */
    private void login(Message msg, String resp) {
        if(!"anyType{}".equals(resp)) {
            Session.setString(Session.KEY_GUID,resp);
            msg.what = 1;
        }else{
            msg.what = 2;
        }
    }

    /**
     * 退出
     * @param msg
     * @param resp
     */
    private void logout(Message msg, String resp) {
        if(!"anyType{}".equals(resp)) {
            msg.what = 1;
        }else{
            msg.what = 2;
        }
    }

    /**
     * 上传
     * @param msg
     * @param resp

    private void upload(Message msg, String resp) {
        if(!"anyType{}".equals(resp)) {
            msg.what = 1;
        }else{
            msg.what = 2;
        }
    }*/

    /**
     * 计划详情
     * @param msg
     * @param resp

    private void parsePlan(Message msg, String resp) {
        if(!"anyType{}".equals(resp)) {
            try {
                JSONObject entity = new JSONObject(resp).optJSONObject("Entitys");
                JSONObject record = entity.optJSONObject("Records");

                msg.obj = new PlanDetail(
                        record.optString("planNo"),
                        record.optString("schemeNo"),
                        record.optString("projectName"),
                        record.optString("planStartTime"),
                        record.optString("planEndTime"),
                        record.optString("devName"),
                        record.optString("model"),
                        record.optString("testProject"),
                        record.optString("majorMan"),
                        record.optString("majorPhone"),
                        record.optInt("pileNum")
                );

            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.what = 1;
        }else{
            msg.what = 2;
        }
    }*/

    /**
     * 计划列表
     * @param msg
     * @param resp
     */
    private void parsePlans(Message msg, String resp) {
        if(!"anyType{}".equals(resp)) {
            List<ProjectInfo> infos = new ArrayList<>();
            try {
                JSONObject entity = new JSONObject(resp).optJSONObject("Entitys");
                JSONArray records = entity.optJSONArray("Records");
                for(int i=0,len=records.length();i<len;i++){
                    entity = records.optJSONObject(i);
                    infos.add(
                            new ProjectInfo(
                                    entity.optString("planNo"),
                                    entity.optString("projectName"),
                                    entity.optString("schemeNo"),
                                    entity.optString("testProject")
                            )
                    );
                }
                /*HashMap<String, List<ProjectInfo>> map = new HashMap<>();
                ProjectInfo info;
                for(int i=0,len=records.length();i<len;i++){
                    entity = records.optJSONObject(i);
                    info = new ProjectInfo(
                            entity.optInt("planNo"),
                            entity.optString("projectName"),
                            entity.optString("schemeNo"),
                            entity.optString("testProject")
                    );
                    if(map.containsKey(info.schemeNo)){
                        map.get(info.schemeNo).add(info);
                    }else{
                        infos = new ArrayList<>();
                        infos.add(info);
                        map.put(info.schemeNo,infos);
                    }
                }
                infos = new ArrayList<>();
                for(Map.Entry<String,List<ProjectInfo>> entry : map.entrySet()){
                    if(entry.getValue().size() < 2){
                        infos.add(entry.getValue().get(0));
                    }else {
                        infos.add(Collections.max(entry.getValue(), new Comparator<ProjectInfo>() {
                            @Override
                            public int compare(ProjectInfo lhs, ProjectInfo rhs) {
                                return lhs.planNo - rhs.planNo;
                            }
                        }));
                    }
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.obj = infos;
            msg.what = 1;
        }else{
            msg.what = 2;
        }
    }

    private SoapObject CallWebService() throws Exception{
        SoapObject request=new SoapObject(Constant.NAMESPACE,methodName);
        if(params!=null&&!params.isEmpty())
        {
            for(NameValuePair temp : params){
                request.addProperty(temp.name,temp.value);
            }
        }

        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.dotNet=true;
        envelope.setOutputSoapObject(request);
        envelope.encodingStyle="UTF-8";
        envelope.bodyOut=request;

        HttpTransportSE hts=new HttpTransportSE(Constant.URL_MAIN);
        String SOAP_action=Constant.NAMESPACE+methodName;
        hts.call( SOAP_action, envelope);

        return (SoapObject) envelope.bodyIn;
    }


}
