package cn.linving.girls.activity;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class BaseActivity extends FragmentActivity {

	private String TAG;
	protected Context context;

	/**
	 * umeng页面统计策略： Activity：统计时长和页面 FragmentActivity +
	 * Fragment：Activity中统计时长，Fragment中统计页面 是否只使用activity
	 */
	protected boolean isOnlyActivity = true;

	@Override
	protected void onCreate(Bundle arg0) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		TAG = this.getClass().getSimpleName();
		context = this;
		super.onCreate(arg0);
	}

	@Override
	protected void onPause() {

		if (isOnlyActivity) {
			MobclickAgent.onPageEnd(TAG);
		}
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		if (isOnlyActivity) {
			MobclickAgent.onPageStart(TAG);
		}
		MobclickAgent.onResume(this);
		super.onResume();
	}

}
