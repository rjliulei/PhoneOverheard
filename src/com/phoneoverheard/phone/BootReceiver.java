package com.phoneoverheard.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * mqttService的开机启动
 * 
 * 这个广播接收者需要在配置文件中注册开机启动aciton:android.intent.action.BOOT_COMPLETED
 * 需要权限：<uses-permission
 * android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * 
 * @author bl_sun
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		context.startService(new Intent(context, BackService.class));
	}

}
