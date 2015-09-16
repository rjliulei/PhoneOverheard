package com.phoneoverheard.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

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
	// app内实现的定位回调
	public BDLocationListener outerLocListener;

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
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，

		option.setScanSpan(SCAN_SPAN_LOC_NORMAL);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

		locationClient.setLocOption(option);
	}

	public void changeLocScanSpan(int interval) {

		LocationClientOption option = locationClient.getLocOption();
		option.setScanSpan(interval);
		locationClient.setLocOption(option);
	}

	public void start() {
		locationClient.start();
		locationClient.requestLocation();
	}

	public void stop() {
		locationClient.stop();
	}

	/**
	 * 实现实时位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
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

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getCity());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
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

			Log.i("BaiduLocationApiDem", sb.toString());
			// mLocationClient.setEnableGpsRealTimeTransfer(true);
		}

	}
}
