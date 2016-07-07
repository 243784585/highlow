package com.omdd.gdyb.main;


import android.content.Intent;
import android.graphics.Paint;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.omdd.gdyb.R;
import com.omdd.gdyb.base.BaseActivity;
import com.omdd.gdyb.bean.ProjectInfo;
import com.omdd.gdyb.utils.DateUtil;
import com.omdd.gdyb.utils.FileDao;
import com.omdd.gdyb.utils.NetworkUtil;
import com.omdd.gdyb.utils.Session;
import com.omdd.gdyb.utils.SessionValidate;
import com.omdd.gdyb.utils.ToastUtils;
import com.omdd.gdyb.utils.WifiAdmin;
import com.omdd.pickerview.TimePickerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends BaseActivity {

    /** 扫描wifi开关 */
    private WifiAdmin mWifiAdmin;
    private TextView tv_date,tv_project;
    private SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    private TimePickerView pvDate;

    private Spinner sp_datatype,sp_testtype,sp_testmethod;
    private String[] dataType = new String[]{"数据文件","波形","图片"};
    private String[] testType = new String[]{"基桩完整性","混凝土强度","钢筋检测","结构尺寸"};
    private String[] testMethod = new String[]{"超声透射法","低应变法","回弹法","超声回弹综合法","电磁感应法","电磁法","反射波法"};

    final int SCAN = 0x001;
    final int ISCONNECT = 0x002;

    private ProjectInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!SessionValidate.isDataIntegrity()){
            //第一次进入应用
            startActivity(new Intent(this,SettingActivity.class).putExtra("theFirst",true));
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public int getHeaderType() {
        return 1;
    }

    @Override
    protected Object getAvtionTitle() {
        return "本次检测";
    }

    @Override
    public Object getHeaderRight() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.menu);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = 30;
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    protected void initView() {
        myHeader.headerLeft.setVisibility(View.INVISIBLE);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_project = (TextView) findViewById(R.id.tv_project);
        sp_datatype = (Spinner) findViewById(R.id.sp_datatype);
        sp_testmethod = (Spinner) findViewById(R.id.sp_testmethod);
        sp_testtype = (Spinner) findViewById(R.id.sp_testtype);
        pvDate = new TimePickerView(this,TimePickerView.Type.YEAR_MONTH_DAY);
        pvDate.setRange(1970, Calendar.getInstance().get(Calendar.YEAR));
        pvDate.setTime(new Date());
        pvDate.setCyclic(false);
        pvDate.setCancelable(true);

        mWifiAdmin = new WifiAdmin(this);
    }

    @Override
    protected void initData() {
        tv_date.setText(date.format(new Date()));
        tv_date.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        sp_datatype.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,dataType));
        sp_testmethod.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,testMethod));
        sp_testtype.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,testType));
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.ll_project).setOnClickListener(this);
        tv_date.setOnClickListener(this);
        pvDate.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                if(date.getTime() > System.currentTimeMillis()){
                    date = new Date();
                    ToastUtils.showTextToast("选择的日期不能大于当前时间...");
                }
                tv_date.setText(MainActivity.this.date.format(date));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header_right:
                //信息设置
                startActivity(new Intent(this,SettingActivity.class));
//                new FileDao(this,1).queryAll();
//                new FileDao(this,1).clearData();
//                ToastUtils.showTextToast(String.valueOf(NetworkUtil.isFlashAir(this)));
                break;
            case R.id.btn_start:
                //启动传输
                startWork();
                break;
            case R.id.ll_project:
                //选择工程
                if("".equals(Session.getString(Session.KEY_GUID))){
                    startActivity(new Intent(this,SettingActivity.class));
                    ToastUtils.showTextToast("请先完成信息设置!");
                    return;
                }
                if(NetworkUtil.isFlashAir(this)){
                    mWifiAdmin.removeNetworkLink(mWifiAdmin.getCurrentNetId());
                }
                startActivityForResult(new Intent(this,ProjectActivity.class),110);

                break;
            case R.id.tv_date:
                //日期选择
                popDate();
                break;
        }
    }


    private void popDate(){
        pvDate.show();
    }

    /** 点击开始传输时,执行 */
    private void startWork(){
        if(info == null){
            ToastUtils.showTextToast("请选择检测工程");
            return;
        }
        try {
            Session.setLong(Session.KEY_TIME,DateUtil.parseDate(tv_date.getText().toString().replace("-","")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Session.setString(Session.KEY_TESTMETHOD,sp_testmethod.getSelectedItem().toString());
        Session.setString(Session.KEY_TESTTYPE,sp_testtype.getSelectedItem().toString());
        Session.setString(Session.KEY_DATATYPE,sp_datatype.getSelectedItem().toString());
        Session.setString(Session.KEY_FILETYPE,info.testProject);
        TransferActivity.planNo = info.planNo;
        startActivity(new Intent(this,TransferActivity.class).putExtra("projectInfo",info));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 110 && resultCode == 120) {
            info = (ProjectInfo) data.getSerializableExtra("project");
            tv_project.setText(info.testProject);
        }
    }


}