package com.phoneoverheard.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Util {

	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	/*************** 系统相关 ********/

	/**
	 * An {@code int} value that may be updated atomically.
	 */
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * 动态生成View ID API LEVEL 17 以上View.generateViewId()生成 API LEVEL 17 以下需要手动生成
	 */
	public static int generateViewId() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			for (;;) {
				final int result = sNextGeneratedId.get();
				// aapt-generated IDs have the high byte nonzero; clamp to the
				// range under that.
				int newValue = result + 1;
				if (newValue > 0x00FFFFFF)
					newValue = 1; // Roll over to 1, not 0.
				if (sNextGeneratedId.compareAndSet(result, newValue)) {
					return result;
				}
			}
		} else {
			return View.generateViewId();
		}
	}

	/**
	 * list to array
	 * 
	 * @author liulei
	 * @date 2015-6-29
	 * @param list
	 * @return String[][]
	 */
	public static String[][] getArray(ArrayList<ArrayList<String>> list) {

		String[][] rsl = null;

		if (null != list) {

			int size = list.size();
			rsl = new String[size][]; // 转换为二维数组

			for (int i = 0; i < size; i++) {

				String[] child = (String[]) list.get(i).toArray();// 获取对象成员保存至一维数组
				rsl[i] = child;
			}
		}
		return rsl;
	}

	/**
	 * log的打印信息
	 * 
	 * @param tag
	 * @param infor
	 */
	public static void printInfor(String tag, String infor) {

		if (infor == null) {
			Log.i(tag, "空指针");
			return;
		}
		if (infor.equals("")) {
			Log.i(tag, "空字符串");
			return;
		}
		Log.i(tag, infor);
	}

	/**
	 * 判断某个activity是否在当前活跃的task栈底
	 * 
	 * @author liulei
	 * @date 2015-5-30
	 * @param context
	 * @param act
	 *            要判断的activity对应的类名
	 * @return boolean
	 */
	public static boolean isActivityAtStackRoot(Context context, String act) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(3);
		if (tasks.get(0).baseActivity.getClassName().equals(act)) {
			return true;
		}

		return false;
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 *            上下文，用来获取连接服务
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context == null) {
			return false;
		}

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] info = cm.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					if (info[i].isAvailable()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 获取设备的ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceID(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String strDeviceID = tm.getDeviceId();
		return strDeviceID;
	}

	/** 可直接使用Build.MODEL */
	public static String getPhoneModel() {

		String phoneModel = Build.MODEL;

		return phoneModel;
	}

	/**
	 * 获取手机号码
	 * 
	 * 普通手机是无法获取到本机号码的， 这和电信运营商有关，我国是将本机号码保存在基站中的， 并未保存在卡中
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		String phoneNumber = null;

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		phoneNumber = tm.getLine1Number();
		if ((null != phoneNumber) && (phoneNumber.length() > 11)) {
			phoneNumber = phoneNumber.substring(3);
		}

		return phoneNumber;
	}

	/**
	 * 分享文本信息
	 * 
	 * @param context
	 *            上下文环境
	 * @param title
	 *            对话框标题
	 * @param content
	 *            分享内容，可添加我们的网址或者app下载地址
	 */
	public static void shareFriends(Context context, String title, String content) {

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, content);
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, title));
	}

	/**
	 * 显示Toast信息
	 * 
	 * @author liulei
	 */
	public static void showToast(Context context, String content) {

		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 注册网络状态改变的监听器
	 * 
	 * @author bl_sun
	 * @param context
	 *            上下文
	 * @param llWarning
	 *            用于显示的warning
	 */
	public static void registerNetChangeView(final Context context, LinearLayout llWarning) {
		llWarning.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
			}
		});
		if (!Util.isNetworkConnected(context)) {
			llWarning.setVisibility(View.VISIBLE);
		} else {
			llWarning.setVisibility(View.GONE);
		}
	}

	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 * 
	 * @param context
	 *            上下文
	 * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G}, *
	 *         {@link #NETWORKTYPE_INVALID}, {@link #NETWORKTYPE_WAP}*
	 *         <p>
	 *         {@link #NETWORKTYPE_WIFI}
	 */

	public static int getNetWorkType(Context context) {

		int mNetWorkType = 0;

		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();

				mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G) : NETWORKTYPE_WAP;
			}
		} else {
			mNetWorkType = NETWORKTYPE_INVALID;
		}

		return mNetWorkType;
	}

	/**
	 * 判断service 是否正在运行
	 * 
	 * @param serviceName
	 *            service包名
	 * 
	 * @return
	 */
	public static boolean serviceIsWorked(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> serviceInfos = (ArrayList<RunningServiceInfo>) am.getRunningServices(50);
		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().toString().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将dp转换成px
	 * 
	 * @param dp
	 *            dp的数值
	 * @param context
	 *            上下文环境
	 * @return 转换出的px值
	 */
	public static int dp2Px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
				.getDisplayMetrics());
	}

	/********* 界面相关 *********/
	/**
	 * 关闭Dialog
	 * 
	 * @param dialog
	 */
	public static void dialogDissmis(Dialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param context
	 */
	public static void hideSoftKeyBoard(Activity context) {
		if (context.getCurrentFocus() != null) {
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

}
