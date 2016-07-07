package com.omdd.gdyb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omdd.gdyb.R;
import com.omdd.gdyb.bean.ProjectInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class ProjectAdapter extends BaseAdapter {

    private List<ProjectInfo> list;
    private Context context;

    public ProjectAdapter(Context context , List<ProjectInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder vh;
        if(convertView != null){
            vh = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context,R.layout.item_test_project,null);
            convertView.setTag(vh = new ViewHolder(convertView));
        }

        ProjectInfo info = list.get(position);
        vh.tv_name.setText(info.projectName);
        vh.tv_test_project.setText(info.testProject);
        vh.tv_scheme.setText("方案编号："+info.schemeNo);
        vh.tv_plan.setText("计划编号："+info.planNo);
        return convertView;
    }

    private class ViewHolder{
        private TextView tv_name,tv_test_project,tv_plan,tv_scheme;

        public ViewHolder(View v){
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_test_project = (TextView) v.findViewById(R.id.tv_test_project);
            tv_plan = (TextView) v.findViewById(R.id.tv_plan);
            tv_scheme = (TextView) v.findViewById(R.id.tv_scheme);
        }
    }
}
