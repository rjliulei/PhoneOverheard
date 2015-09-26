package com.phoneoverheard.phone.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.linving.girls.activity.BaseActivity;

import com.phoneoverheard.interfaces.Constant;
import com.phoneoverheard.phone.R;

/**
 * 管理员管理界面
 * 
 * @author liulei
 * @date 2015-9-17 下午12:25:19
 * @version 1.0
 */
public class AdminManageActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_manage);

		isOnlyActivity = true;

		initView();
	}

	private void initView() {

		((TextView) findViewById(R.id.tv_check_submit)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_db_test)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_sms_control)).setOnClickListener(this);		
	}

	/** 
	 * 打开权限管理
	 * @author liulei	
	 * @date 2015-9-24 void   
	*/
	private void openAppOps() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings");
		intent.setComponent(cn);
		intent.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_check_submit:
			sendBroadcast(new Intent(Constant.INTENT_ACTION_TIMER_CHECK_TO_SUBMIT));
			break;
		case R.id.tv_db_test:
			startActivity(new Intent(context, AdminDbTestActivity.class));
			break;
			
		case R.id.tv_sms_control:
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
				openAppOps();
			}
			break;

		default:
			break;
		}
	}

}
