package com.omdd.gdyb.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omdd.gdyb.R;


/**
 * Created by hxhuang on 2016/1/4 0004.
 * 取消按钮与副标题默认不显示
 */
public abstract class CommonDialog {

    Context context;
    /** 确定按钮 **/
    private Button btnConfirm;
    /** 取消按钮 **/
    private Button btnCancle;
    private View vSplit;
    private TextView tvTitle;
    private Dialog dialog;

    public CommonDialog(Context context){
        this.context = context;
        onCreate();
    }

    /**
     * 构建对话框
     * @return
     */
    public void onCreate(){
        //获取Dialog布局
        View v = LayoutInflater.from(context).inflate(R.layout.common_dialog, null);

        btnConfirm = (Button) v.findViewById(R.id.btn_confirm);
        btnCancle = (Button) v.findViewById(R.id.btn_cancel);
        vSplit = v.findViewById(R.id.v_split);
        tvTitle = (TextView) v.findViewById(R.id.tv_title_confirm);

        //设置按钮监听
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterCancle();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterConfirm();
            }
        });

        dialog = new Dialog(context,R.style.my_confirm_style);
        dialog.setContentView(v);

    }

    /**
     * 设置按钮样式
     * @param color 字体颜色
     * @param textSize 字体大小
     * @param unit 字体单位
     * @return
     */
    public CommonDialog setButtonStyle(Integer color, Float textSize, Integer unit){
        btnConfirm.setTextColor(context.getResources().getColor(color));
        btnCancle.setTextColor(context.getResources().getColor(color));
        btnConfirm.setTextSize(unit == null ? null : unit, textSize);
        btnCancle.setTextSize(unit == null ? null : unit, textSize);
        return this;
    }

    /**
     * 点击确定按钮后触发
     */
    protected abstract void afterConfirm();

    /**
     * 点击取消按钮后触发
     */
    protected void afterCancle(){
        dismiss();
    }

    /**
     * 设置标题
     * @param title 标题内容
     * @param color 字体颜色(资源id)
     * @param unit 字体大小单位
     * @param textSize 字体大小
     * @return
     */
    public CommonDialog setTitle(String title, Integer color, Float textSize, Integer unit){
        tvTitle.setText(title);
        if(color != null) {
            tvTitle.setTextColor(context.getResources().getColor(color));
        }
        if(textSize != null) {
            tvTitle.setTextSize(unit == null?null:unit,textSize);
        }
        return this;
    }

    /**
     * 设置副标题
     * @param subTitle 副标题内容
     * @param color 字体颜色
     * @param unit 字体大小单位
     * @param textSize 字体大小
     * @return
     */
    public CommonDialog setSubTitle(String subTitle, Integer color, Float textSize, Integer unit){
        TextView tvSubTitle = (TextView) dialog.findViewById(R.id.tv_sub_title);
        tvSubTitle.setVisibility(View.VISIBLE);
        tvSubTitle.setText(subTitle);
        if(color != null) {
            tvSubTitle.setTextColor(context.getResources().getColor(color));
        }
        if(textSize != null) {
            tvSubTitle.setTextSize(unit == null ? null : unit, textSize);
        }
        return this;
    }

    /**
     * 设置确定按钮
     * @param content 文本内容
     * @return
     */
    public CommonDialog setConfirm(String content){
        btnConfirm.setText(content);
        return this;
    }

    /**
     * 设置取消按钮
     * @param content 文本内容
     * @return
     */
    public CommonDialog setCancle(String content){
        btnCancle.setVisibility(View.VISIBLE);
        vSplit.setVisibility(View.VISIBLE);
        btnCancle.setText(content);
        return this;
    }

    /**
     * 禁用对话框范围外的点击
     * @param cancel
     * @return
     */
    public CommonDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CommonDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**展示对话框*/
    public void show(){
        dialog.show();
    }

    /**隐藏对话框*/
    public void dismiss(){
        dialog.dismiss();
    }

    public Dialog getDialog(){
        return dialog;
    }
}
