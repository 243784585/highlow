package com.omdd.gdyb.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.omdd.gdyb.R;
import com.omdd.gdyb.adapter.ProjectAdapter;
import com.omdd.gdyb.base.BaseActivity;
import com.omdd.gdyb.bean.NameValuePair;
import com.omdd.gdyb.bean.ProjectInfo;
import com.omdd.gdyb.utils.CommonUtil;
import com.omdd.gdyb.utils.Constant;
import com.omdd.gdyb.utils.soap.RequestHandler;
import com.omdd.gdyb.utils.Session;
import com.omdd.gdyb.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hxhuang on 2016/6/15 0015.
 */
public class ProjectActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2{

    private ArrayList<ProjectInfo> list = new ArrayList<ProjectInfo>();
    private PullToRefreshListView ptr_project;
    private ProgressDialog waitDialog;
    private ProjectAdapter adapter;
    private List<NameValuePair> params = new ArrayList<>();
    private TextView emptyView;


    private Handler mainHan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //
            switch (msg.what){
                case 1:
                    list.addAll((List<ProjectInfo>) msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    ToastUtils.showTextToast("没有更多数据了...");
                    break;
                case 3:
                    ToastUtils.showTextToast("网络不可用！请切换至可用网络再重新选择工程!");
                    finish();
                    break;
            }
            ptr_project.onRefreshComplete();
        }
    };
    private RequestHandler reqHan = new RequestHandler(mainHan);



    @Override
    protected int setLayout() {
        return R.layout.activity_project;
    }

    @Override
    public int getHeaderType() {
        return 1;
    }

    @Override
    protected Object getAvtionTitle() {
        return "工程选择";
    }

    @Override
    protected void initView() {
        waitDialog = new ProgressDialog(this);
        ptr_project = (PullToRefreshListView) findViewById(R.id.ptr_project);

        emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        emptyView.setText("暂无工程可供选择");

        ((ViewGroup)ptr_project.getParent()).addView(emptyView);
        ptr_project.setEmptyView(emptyView);
        ptr_project.setAdapter(adapter = new ProjectAdapter(this,list));
        ptr_project.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    protected void initData(Bundle outState) {
//        myHeader.headerLeft.setVisibility(View.INVISIBLE);
        reqHan.setParams(Constant.METHOD_GETPLANLIST,params);

        onPullDownToRefresh(null);
    }

    @Override
    protected void initListener() {
        ptr_project.setOnRefreshListener(this);
        ptr_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectInfo projectInfo = list.get((int) id);
                setResult(120, new Intent().putExtra("project",projectInfo));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        //下拉刷新
        list.clear();
        params.clear();
        params.add(new NameValuePair("guid", Session.getString(Session.KEY_GUID)));
        params.add(new NameValuePair("pageIndex",1));
        params.add(new NameValuePair("pageSize",20));
        reqHan.sendEmptyMessage(0);
        reqHan.setProgressDialog(waitDialog);
        CommonUtil.showWaitDialog(waitDialog,"正在加载数据,请稍后...");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        //加载更多
        params.clear();
        params.add(new NameValuePair("guid", Session.getString(Session.KEY_GUID)));
        params.add(new NameValuePair("pageIndex",list.size()%20 == 0 ?list.size() / 20 + 1:list.size() / 20 + 2));
        params.add(new NameValuePair("pageSize",20));
        reqHan.sendEmptyMessage(0);
    }


}
