package com.omdd.gdyb.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omdd.gdyb.R;
import com.omdd.gdyb.application.BaseApplication;
import com.omdd.gdyb.base.BaseActivity;
import com.omdd.gdyb.bean.FlashAirFile;
import com.omdd.gdyb.bean.NameValuePair;
import com.omdd.gdyb.bean.ProjectInfo;
import com.omdd.gdyb.utils.CommonDialog;
import com.omdd.gdyb.utils.CommonUtil;
import com.omdd.gdyb.utils.Constant;
import com.omdd.gdyb.utils.DateUtil;
import com.omdd.gdyb.utils.FileDao;
import com.omdd.gdyb.utils.FlashAirRequest;
import com.omdd.gdyb.utils.NetworkUtil;
import com.omdd.gdyb.utils.Session;
import com.omdd.gdyb.utils.ToastUtils;
import com.omdd.gdyb.utils.WifiAdmin;
import com.omdd.gdyb.utils.download.AsyncDown;
import com.omdd.gdyb.utils.soap.RequestHandler;
import com.omdd.gdyb.utils.upload.AsyncUpload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hxhuang on 2016/6/15 0015.
 */
public class TransferActivity extends BaseActivity {
    public static String planNo;//计划编号
    public static FileDao fileDao;
    final int SCAN = 0x001;
    final int DOWN = 0x002;
    /**
     * 当前处于的状态(流程控制,解决网络状态接受者被重复调用的问题)
     * 0:等待状态 ->1
     * 1:下载中 ->2
     * 2:下载完成 ->3
     * 3:上传中 ->0
     */
    private int state;
    /**
     * 是否处于处理失败模式
     */
    private boolean isFailed;


    private ListView lv_transfer;
    private TextView tv_upload, tv_download, tv_count, emptyView;
    private NetReceiver netReceiver;
    private WifiAdmin mWifiAdmin;


    /**
     * 所有要下载的文件集合
     */
    private List<FlashAirFile> allFiles = new ArrayList<>();
    private List<AsyncDown> downs = new ArrayList<AsyncDown>();
    private List<AsyncUpload> uploads = new ArrayList<>();
    private ArrayList<Integer> failed = new ArrayList<>();

    /**
     * 当前下载文件个数(位置)
     */
    private int curDown;
    /**
     * 当前上传文件个数(位置)
     */
    private int curUpload;
    /**
     * 工作次数(扫描设备,有文件可下载的次数)
     */
    private int workCount;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 2.  遍历下载清单
            List<FlashAirFile> flashAirFiles = fileDao.queryFlashAirFileByUnDownload(null, planNo);
            if ((flashAirFiles != null && flashAirFiles.size() > 1) || workFinish != null) {
                //有可下载文件
                workCount++;
                if (workFinish == null) {
                    flashAirFiles.remove(flashAirFiles.size() - 1);//移除最后一个,即最新的(有可能正在编辑的)
                }
                allFiles.addAll(flashAirFiles);
                tv_count.setVisibility(View.VISIBLE);
                tv_count.setText("总文件数：" + allFiles.size());
                tv_download.setVisibility(View.VISIBLE);
                tv_download.setText("已下载：" + curDown);
                tv_upload.setVisibility(View.VISIBLE);
                tv_upload.setText("已上传：" + curUpload);
                startDownLoad();
            } else {
                emptyView.setText("FlashAir暂时没有" + new SimpleDateFormat("yyyy-MM-dd").format(Session.getLong(Session.KEY_TIME)) + "之后的文件,请按返回键退出或继续等待");
                //所有文件传输工作完成
                state = 0;
                if (NetworkUtil.isFlashAir(TransferActivity.this, mWifiAdmin)) {
                    workHandler.sendEmptyMessageDelayed(DOWN, 1000);
                } else {
                    workHandler.sendEmptyMessage(SCAN);
                }
            }
        }
    };

    Handler workHandler = new Handler(BaseApplication.getInstance().getWorkLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN:
                    startScanFlashAir();
                    break;
                case DOWN:
                    //   1.初始化下载清单
                    initDownloadList();
                    break;
            }
        }
    };
    private boolean scan;

    /**
     * 开始扫描设备热点
     */
    private void startScanFlashAir() {
        if (scan) return;
        scan = true;
        mWifiAdmin.openWifi();
        while (scan) {
            mWifiAdmin.startScan();
            if (mWifiAdmin.getWifiList() == null || mWifiAdmin.getWifiList().size() == 0) continue;
            for (ScanResult scanRes : mWifiAdmin.getWifiList()) {
                if (scanRes.SSID.replace("\"", "").equals(Constant.WIFI_SSID)) {
                    for (WifiConfiguration item : mWifiAdmin.getWifiConfigList()) {
                        if (item.SSID.replace("\"", "").equals(Constant.WIFI_SSID)) {
                            mWifiAdmin.addNetWordLink(item);
                            scan = false;
                            return;
                        }
                    }
                    mWifiAdmin.addNetWordLink(mWifiAdmin.CreateWifiInfo(Constant.WIFI_SSID, Constant.WIFI_PWD, 3));
                    scan = false;
                    return;
                }
            }
        }
    }

    /**
     * 初始化下载清单
     */
    private void initDownloadList() {
        if (state == 0) {
            state++;
            FlashAirRequest.initDownloadList(Constant.FA_CMD_GETFILELIST + Constant.PATH);
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public int getHeaderType() {
        return 1;
    }

    @Override
    protected Object getAvtionTitle() {
        return "传输状态";
    }

    @Override
    public Object getHeaderRight() {
        return "完成检测";
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_transfer;
    }

    @Override
    protected void initView() {
        //监听网络状态的广播接收者
        registerReceiver(netReceiver = new NetReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        lv_transfer = (ListView) findViewById(R.id.lv_transfer);
        lv_transfer.setAdapter(new MyAdapter());
        myHeader.headerLeft.setVisibility(View.INVISIBLE);


        /** 空视图 */
        emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyView.setText("FlashAir设备暂无" + DateUtil.format(Session.getLong(Session.KEY_TIME)) + "的文件");
        emptyView.setGravity(Gravity.CENTER);
        ((ViewGroup) lv_transfer.getParent()).addView(emptyView);
        lv_transfer.setEmptyView(emptyView);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_download = (TextView) findViewById(R.id.tv_download);
    }

    @Override
    protected void initData() {
        mWifiAdmin = new WifiAdmin(this);
        fileDao = new FileDao(this, 1);
        if (NetworkUtil.isFlashAir(this, mWifiAdmin)) {
            //已连接上设备
            workHandler.sendEmptyMessage(DOWN);
        } else {
            //未连接上设备
            emptyView.setText("正在连接FlashAir设备,请稍后...");
            workHandler.sendEmptyMessage(SCAN);
        }
    }

    @Override
    protected void initListener() {
    }

    private String workFinish;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_right:
                if (workFinish != null) {
                    ToastUtils.showTextToast("请等待传输完成");
                    return;
                }
                new CommonDialog(this) {
                    @Override
                    protected void afterConfirm() {
                        List<FlashAirFile> flashAirFiles = fileDao.queryFlashAirFileByUnDownload(null, planNo);
                        if (flashAirFiles != null) {
                            //有可下载文件
                            workFinish = "";
                            ToastUtils.showTextToast("请等待传输完成");
                        } else {
                            finish();
                        }
                        dismiss();
                    }
                }.setTitle(fileDao.queryFlashAirFileByUnDownload(null, planNo) != null ? "确认上传最后一个检测文件?请确认该检测文件为最终版本" : "确定传输工作已完成?", R.color.black, 11f, TypedValue.COMPLEX_UNIT_SP)
                        .setButtonStyle(R.color.dialog_btn_color, 15f, TypedValue.COMPLEX_UNIT_SP)
                        .setCancle("继续工作")
                        .setConfirm("确认上传")
                        .show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netReceiver);
        fileDao.close();
        fileDao = null;
        scan = false;
    }

    /**
     * 开启下载
     */
    private void startDownLoad() {
        //3.下载
        for (int i = curDown, len = allFiles.size(); i < len; i++) {
            downs.add(new AsyncDown(this));
        }
        downs.get(curDown).execute(allFiles.get(curDown));
        ((MyAdapter) lv_transfer.getAdapter()).notifyDataSetChanged();
    }

    /**
     * 下载完成回调
     */
    public void onDownLoaded() {
        ((MyAdapter) lv_transfer.getAdapter()).notifyDataSetChanged();
        if (isFailed) {
            int position = failed.get(0);
            if (allFiles.get(position).state == FlashAirFile.STATE_LOADFAILED) {
                //失败之后,仍然失败
                AsyncDown temp = new AsyncDown(this);
                downs.set(position, temp);
                temp.execute(allFiles.get(position));
                return;
            } else if (allFiles.get(position).state == FlashAirFile.STATE_LOADED) {
                failed.remove(0);
                if (!failed.isEmpty()) {
                    tv_download.setText("已下载：" + (curDown + 1 - failed.size()));
                    downs.get(failed.get(0)).execute(allFiles.get(failed.get(0)));
                    return;
                }
                isFailed = false;
            }
        }
        if (allFiles.get(curDown).state == FlashAirFile.STATE_LOADFAILED && !failed.contains(curDown)) {
            //如果下载失败
            failed.add(curDown);
        }


        tv_download.setText("已下载：" + (curDown + 1 - failed.size()));
        if (++curDown == allFiles.size()) {

            if (!failed.isEmpty()) {
                for (Integer i : failed) {
                    downs.set(i, new AsyncDown(this));
                }
                curDown--;
                isFailed = true;
                downs.get(failed.get(0)).execute(allFiles.get(failed.get(0)));
                return;
            }


            //4.下载完成回调,若成功则断开设备,准备上传
            //下载完成
            state++;//2
//            mWifiAdmin.closeWifi();
            mWifiAdmin.closeWifi();//关闭FlashAir设备wifi
            return;
        }
        downs.get(curDown).execute(allFiles.get(curDown));//开启下一个下载任务
    }

    /**
     * 开启上传
     */
    private void startUpload() {
        if (state == 2) {
            //全部下载完成,上传开始
            state++;
            for (int i = curUpload, len = allFiles.size(); i < len; i++) {
                uploads.add(new AsyncUpload(this));
            }
            uploads.get(curUpload).execute(allFiles.get(curUpload));
            ((MyAdapter) lv_transfer.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * 上传完成回调
     */
    public void onUploaded() {
        //上传完成回调
        ((MyAdapter) lv_transfer.getAdapter()).notifyDataSetChanged();

        if (isFailed) {
            int position = failed.get(0);
            if (allFiles.get(position).state == FlashAirFile.STATE_UPFAILED) {
                //失败之后,仍然失败
                AsyncUpload temp = new AsyncUpload(this);
                uploads.set(position, temp);
                temp.execute(allFiles.get(position));
                return;
            }
            failed.remove(0);
            tv_download.setText("已下载：" + (curDown + 1 - failed.size()));
            if (!failed.isEmpty()) {
                uploads.get(failed.get(0)).execute(allFiles.get(failed.get(0)));
                return;
            }
            isFailed = false;
        }

        if (allFiles.get(curUpload).state == FlashAirFile.STATE_UPFAILED && !failed.contains(curUpload)) {
            //如果下载失败
            failed.add(curUpload);
        }


        tv_upload.setText("已上传：" + (curUpload + 1 - failed.size()));
        if (++curUpload == allFiles.size()) {

            if (!failed.isEmpty()) {
                for (Integer i : failed) {
                    uploads.set(i, new AsyncUpload(this));
                }
                curUpload--;
                isFailed = true;
                uploads.get(failed.get(0)).execute(allFiles.get(failed.get(0)));
                return;
            }

            if (workFinish != null) {
                finish();
                ToastUtils.showTextToast("传输工作完成");
                return;
            }
            //当次全部上传完成
            state = 0;
            tv_upload.setText("已上传：" + curUpload);
            workHandler.sendEmptyMessage(SCAN);//开启扫描,连接设备
            return;
        }
        tv_upload.setText("已上传：" + curUpload);
        uploads.get(curUpload).execute(allFiles.get(curUpload));
    }

    @Override
    public void onBackPressed() {
        if (workFinish != null) {
            ToastUtils.showTextToast("请等待传输完成");
            return;
        }
        final boolean hasFile = fileDao.queryFlashAirFileByUnDownload(null, planNo) != null;
        new CommonDialog(this) {
            @Override
            protected void afterConfirm() {
                if (hasFile) {
                    //有可下载文件
                    workFinish = "";
                    ToastUtils.showTextToast("请等待传输完成");
                } else {
                    finish();
                }
                dismiss();
            }
        }.setTitle(hasFile ? "确认上传最后一个检测文件?请确认该检测文件为最终版本" : "确定传输工作已完成?", R.color.black, 11f, TypedValue.COMPLEX_UNIT_SP)
                .setButtonStyle(R.color.dialog_btn_color, 15f, TypedValue.COMPLEX_UNIT_SP)
                .setCancle(hasFile?"继续工作":"取消")
                .setConfirm(hasFile?"确认上传":"确定")
                .show();
        /*new CommonDialog(this) {
            @Override
            protected void afterConfirm() {
                dismiss();
                finish();
            }
        }.setTitle("确定退出传输工作吗?", R.color.black, 11f, TypedValue.COMPLEX_UNIT_SP)
                .setButtonStyle(R.color.dialog_btn_color, 15f, TypedValue.COMPLEX_UNIT_SP)
                .setCancle("取消")
                .setConfirm("确定")
                .show();*/
    }

    //String guid, String planNo, String pileNo,String fileName, String dataType,
    // String testType,String testMethod,String fileType, String data

    /*************************
     * 适配器类
     ********************************/

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allFiles == null ? 0 : allFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView != null) {
                vh = (ViewHolder) convertView.getTag();
            } else {
                convertView = getLayoutInflater().inflate(R.layout.item_transfer, null);
                convertView.setTag(vh = new ViewHolder(convertView));
            }
            FlashAirFile airFile = allFiles.get(position);
            switch (airFile.state) {
                case FlashAirFile.STATE_UNLOAD:
                    //待下载
                    vh.tv_state.setText("待下载");
                    break;
                case FlashAirFile.STATE_LOADING:
                    //下载中
                    vh.tv_state.setText("下载中");
                    break;
                case FlashAirFile.STATE_LOADED:
                    //已下载
                    vh.tv_state.setText("已下载");
                    break;
                case FlashAirFile.STATE_UPLOADING:
                    //上传中
                    vh.tv_state.setText("上传中");
                    break;
                case FlashAirFile.STATE_UPLOADED:
                    //已上传
                    vh.tv_state.setText("已上传");
                    break;
                case FlashAirFile.STATE_LOADFAILED:
                    //下载失败
                    vh.tv_state.setText(Html.fromHtml("<font color='red'>下载失败</font>"));
                    break;
                case FlashAirFile.STATE_EXIST:
                    //文件已存在
                    vh.tv_state.setText("文件已存在");
                    break;
                case FlashAirFile.STATE_DATETYPEERROR:
                    //数据类型错误
                    vh.tv_state.setText(Html.fromHtml("<font color='red'>数据类型错误</font>"));
                    break;
                case FlashAirFile.STATE_RELOGIN:
                    //请重新登录
                    vh.tv_state.setText(Html.fromHtml("<font color='red'>请重新登录</font>"));
                    break;
                case FlashAirFile.STATE_SYSTEMERROR:
                    //系统错误
                    vh.tv_state.setText(Html.fromHtml("<font color='red'>系统错误</font>"));
                    break;
                case FlashAirFile.STATE_UPFAILED:
                    //上传失败
                    vh.tv_state.setText(Html.fromHtml("<font color='red'>上传失败</font>"));
            }
            vh.tv_project.setText(airFile.flashAirPath + File.separator + airFile.fileName);

            return convertView;
        }

        private class ViewHolder {
            private TextView tv_project, tv_state;

            public ViewHolder(View v) {
                tv_project = (TextView) v.findViewById(R.id.tv_project);
                tv_state = (TextView) v.findViewById(R.id.tv_state);
            }
        }

    }

    /*************************
     * 网络监听广播接受者
     ********************************/

    class NetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取手机的连接服务管理器，这里是连接管理器类
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                switch (info.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        if (Constant.WIFI_SSID.equals(mWifiAdmin.getSSID().replace("\"", ""))) {
                            //连接上Flashair设备
                            workHandler.sendEmptyMessage(DOWN);
                        }
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        //手机网络,上传文件到服务器
                        startUpload();//开始上传
                        break;
                }
                return;
            }
            //无网络

        }
    }
}
