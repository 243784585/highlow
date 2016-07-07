package com.omdd.gdyb.utils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.omdd.gdyb.R;


/**
 * Created by admin on 2016-02-17.
 * descript:搜索弹出框
 */
public class SearchPopWindow extends PopupWindow {

    private View contentView;
    private Activity act;
    /**搜索输入框*/
    private EditText et_search;
    /**取消按钮*/
    private TextView tv_cancle;
    /**遮罩区域*/
    private View view;


    public SearchPopWindow(Activity activity,OnSearchListener listener){
        act=activity;
        this.listener = listener;
        LayoutInflater inflater=LayoutInflater.from(act);
        contentView=inflater.inflate(R.layout.popwindow_search,null);
        initView();
        setListener();
        setContentView(contentView);
        setWidth(CommonUtil.getDeviceSreenWidth(act));
        setHeight(CommonUtil.getDeviceSreenHeight(act));
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    private void initView() {
        tv_cancle=(TextView)contentView.findViewById(R.id.tv_cancle);
        et_search=(EditText)contentView.findViewById(R.id.et_search);
        view = contentView.findViewById(R.id.space);

        /*Drawable drawableSearch = contentView.getResources().getDrawable(R.drawable.search);
        drawableSearch.setBounds(CommonUtil.unitSwitch2Dp(et_search, 5), 0, CommonUtil.unitSwitch2Dp(et_search, 20), CommonUtil.unitSwitch2Dp(et_search, 15));
        et_search.setCompoundDrawables(drawableSearch, null, null, null);
        CommonUtil.showSoftInput(et_search, 100);//延迟100毫秒弹出软键盘*/

        //给遮罩区域添加背景色渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(100);
        view.setAnimation(alphaAnimation);
//        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
//        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        act.getWindow().setAttributes(lp);
    }

    private OnSearchListener listener;

    private void setListener() {
        //监听取消按钮点击事件
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMiss();
            }
        });
        //监听遮罩区域点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMiss();
            }
        });
        //监听软键盘搜索按钮点击事件
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//修改回车键功能
                    dismiss();
                    if(listener != null)listener.onSearch(v);
                }
                return false;
            }
        });
    }

    private void disMiss(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(200);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(alphaAnimation);
    }

    public interface OnSearchListener{
        void onSearch(TextView textView);
    }
}


