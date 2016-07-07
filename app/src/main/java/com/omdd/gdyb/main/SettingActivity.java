package com.omdd.gdyb.main;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.omdd.gdyb.R;
import com.omdd.gdyb.base.BaseActivity;
import com.omdd.gdyb.bean.NameValuePair;
import com.omdd.gdyb.utils.Constant;
import com.omdd.gdyb.utils.soap.RequestHandler;
import com.omdd.gdyb.utils.Session;
import com.omdd.gdyb.utils.ToastUtils;
import com.omdd.gdyb.view.CollectionView;

import java.util.ArrayList;

/**
 * 设置全局保存数据页面
 */
public class SettingActivity extends BaseActivity {

    private CollectionView cv_user,cv_pwd;
    private Handler han = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //登录成功
                    Session.setString(Session.KEY_LOGIN_USER, cv_user.et_content.getText().toString().trim());
                    Session.setString(Session.KEY_LOGIN_PWD, cv_pwd.et_content.getText().toString().trim());
                    //数据校验成功
                    if(theFirst)startActivity(new Intent(SettingActivity.this,MainActivity.class));
                    finish();
                    break;
                case 2:
                    ToastUtils.showTextToast("三和账号或密码错误");
                    break;
            }
        }
    };
    private RequestHandler reqHan = new RequestHandler(han);

    private boolean theFirst;
    @Override
    public int getHeaderType() {
        return 1;
    }

    @Override
    protected Object getAvtionTitle() {
        return "信息设置";
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_setting;
    }

    protected void initView(){
        cv_user = (CollectionView) findViewById(R.id.cv_user);
        cv_pwd = (CollectionView) findViewById(R.id.cv_pwd);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_save).setOnClickListener(this);
    }

    protected void initData(){
        theFirst = getIntent().getBooleanExtra("theFirst",false);
        if(theFirst){
            myHeader.headerLeft.setVisibility(View.INVISIBLE);
        }
        cv_user.et_content.setText(Session.getString(Session.KEY_LOGIN_USER));
        cv_pwd.et_content.setText(Session.getString(Session.KEY_LOGIN_PWD));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                saveInfo();
                break;
        }
    }

    private void saveInfo() {
        String user = cv_user.et_content.getText().toString().trim();
        String pwd = cv_pwd.et_content.getText().toString().trim();
        if("".equals(user)){
            ToastUtils.showTextToast("请输入三和账号");
            return;
        }

        if("".equals(pwd)){
            ToastUtils.showTextToast("请输入密码");
            return;
        }

        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("userName",user));
        params.add(new NameValuePair("password",pwd));
        reqHan.setParams(Constant.METHOD_LOGIN,params);
        reqHan.sendEmptyMessage(0);

    }
}
