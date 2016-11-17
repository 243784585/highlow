package com.omdd.gdyb.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.omdd.gdyb.R;
import com.omdd.gdyb.view.MyHeader;

import java.util.HashMap;


/**
 * Activity基础类
 * 
 * @author hxhuang
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
	//自定义ActionBar，标题栏
	protected MyHeader myHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(setLayout());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setActionBar();
		initView();
		initData(savedInstanceState);
		initListener();
	}

	/**
	 * 设置标题栏
	 */
	protected void setActionBar() {

		if (getHeaderType() == 1) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("left", "left");
			hashMap.put("center", this.getAvtionTitle());
			myHeader = (MyHeader) findViewById(R.id.header);

			//设置ActionBar右侧事件
			if (getHeaderRight() != null) {
				hashMap.put("right", getHeaderRight());
				myHeader.headerRight.setOnClickListener(this);
			}
			myHeader.setHeaderView(this, hashMap);
		}
	}

	/**
	 * 设置自定义ActionBar的描述
	 *
	 * @return
	 */
	protected Object getAvtionTitle(){
		return null;
	}


	public Object getHeaderRight() {
		return null;
	}

	/**
	 * 重写并更改返回值为1,则设置标题栏
	 * Header类型
	 *
	 * @ 1:LydbHeader
	 * 0:自定义
	 */

	public int getHeaderType() {
		return 0;
	}

	/**
	 * 关联布局文件
	 */
	protected abstract int setLayout();

	/**
	 * 初始化界面
	 */
	protected abstract void initView();

	protected abstract void initData(Bundle outState);

	/**
	 * 各种事件
	 */
	protected abstract void initListener();

	/**
	 * 通过id获取view并设置点击事件监听
	 * @param id 资源id
	 * @param listener 监听器
	 * @return 资源id对应的view
	 */
	protected View getViewAndSetOnClick(int id,View.OnClickListener listener){
		View view = findViewById(id);
		view.setOnClickListener(listener);
		return view;
	}

//	@Override
//	public SwipeBackLayout getSwipeBackLayout() {
//		mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
//				me.imid.swipebacklayout.lib.R.layout.swipeback_layout2, null);
//		return mSwipeBackLayout;
//	}
}
