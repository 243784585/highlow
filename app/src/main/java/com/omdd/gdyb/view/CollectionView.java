package com.omdd.gdyb.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omdd.gdyb.R;


/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class CollectionView extends LinearLayout {

    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tv_desc;
    public EditText et_content;
    private ImageView iv_clear;

    private String desc;

    public CollectionView(Context context) {
        super(context);
        initView();
        initData(null);
        initListener();
    }

    public CollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData(attrs);
        initListener();

    }

    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData(attrs);
        initListener();
    }

    private String hint;

    private void initData(AttributeSet attrs) {
        if(attrs != null) {
            tv_desc.setText(attrs.getAttributeValue(NAMESPACE, "desc"));
            boolean password = attrs.getAttributeBooleanValue(NAMESPACE, "password",false);
            if(password){
                et_content.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
            hint = attrs.getAttributeValue(NAMESPACE,"Hint");
        }
    }

    private void initView(){
        View.inflate(getContext(), R.layout.view_data_collection,this);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
    }

    private void initListener() {
        et_content.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(et_content.getText().length() > 0){
                        iv_clear.setVisibility(VISIBLE);
                        return;
                    }
                    et_content.setHint(hint);
                }else{
                    iv_clear.setVisibility(INVISIBLE);
                    et_content.setHint("");
                }
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            boolean theFirst;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!theFirst){
                    theFirst = true;
                    return;
                }
                iv_clear.setVisibility(s.length() > 0 ? VISIBLE:INVISIBLE);
            }
        });

        iv_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_content.setText("");
            }
        });
    }

    /**
     * 获取输入内容
     * @return
     */
    public String getContent(){
        return et_content.getText().toString();
    }
}
