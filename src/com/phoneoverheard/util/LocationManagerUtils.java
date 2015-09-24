package com.phoneoverheard.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.phoneoverheard.bean.LocNormal;
import com.phoneoverheard.db.LocNormalUnit;

/**
 * 百度定位工具类
 * 
 * @author liulei
 * @date 2015-9-15 上午11:09:22
 * @version 1.0
 */
public class LocationManagerUtils {

	public static final int SCAN_SPAN_LOC_NORMAL = 15 * 60 * 1000;
	public static final int SCAN_SPAN_LOC_EXACT = 60 * 1000;
	public static final int SCAN_SPAN_LOC_IDLE = 60 * 60 * 1000;

	private LocationClient locationClient;
	private MyLocationListener locationListener;
	private Context context;
	private LocNormalUnit unit;
	// app内实现的定位回调
	public BDLocationListener outerLocListener;

	/** 因为百度定位sdk没有实现在锁屏情况下的定时定位，所以要自己实现一次 */
	private AlarmManager am;
	private TimerReceiver receiver = null;
	private PendingIntent pi = null;
	private PowerManager.WakeLock wl = null;
	private final String INTENT_ACTION_TIMER = "com.baidu.locSDK.timer";

	public LocationManagerUtils(Context context) {

		this.context = context;
		locationClient = new LocationClient(context);
		locationListener = new MyLocationListener();
		locationClient.registerLocationListener(locationListener);

	}

	/**
	 * 定位分两种，常态定位和精准定位
	 * 
	 * @author liulei
	 * @date 2015-9-15 void
	 */
	public void initLocOptionNormal() {
		unit = new LocNormalUnit(context);

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，

		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

		locationClient.setLocOption(option);
	}

	public void changeLocScanSpan(int interval) {
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, interval, pi);
	}

	public void start() {
		locationClient.start();
		locationClient.requestLocation();

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationWackLock");
		wl.setReferenceCounted(false);

		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		receiver = new TimerReceiver();
		context.registerReceiver(receiver, new IntentFilter(INTENT_ACTION_TIMER));
		pi = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_ACTION_TIMER), PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, SCAN_SPAN_LOC_NORMAL, pi);
	}

	public void stop() {
		locationClient.stop();

		context.unregisterReceiver(receiver);
		am.cancel(pi);
	}

	/**
	 * 实现实时位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			boolean isSuccess = false;

			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

				isSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getCity());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");

				isSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
				isSuccess = true;
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}

			if (null != outerLocListener) {
				outerLocListener.onReceiveLocation(location);
			}

			if (isSuccess) {
				LocNormal locNormal = new LocNormal();
				locNormal.setLat(location.getLatitude());
				locNormal.setLng(location.getLongitude());
				locNormal.setState(0);
				locNormal.setTime(StringUtils.getDateTime());
				unit.create(locNormal);

				releaseWackLock();
			}

			Log.i("BaiduLocationApiDem", sb.toString());
			// mLocationClient.setEnableGpsRealTimeTransfer(true);
		}

	}

	public void releaseWackLock() {
		if (wl != null && wl.isHeld())
			wl.release();
	}

	public class TimerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			if (locationClient != null && locationClient.isStarted()) {
				wl.acquire();
				locationClient.requestLocation();
			}
		}

	}
}
