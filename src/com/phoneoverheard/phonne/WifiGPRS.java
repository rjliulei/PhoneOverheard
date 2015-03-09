package com.phoneoverheard.phonne;

import java.lang.reflect.Method;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiGPRS{
	private final static String TAG = "WifiGPRS";
	private ConnectivityManager mCM;	
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult = null;
	// 定义WifiManager对象
	private WifiManager mWifiManager = null;
	// 定义WifiInfo对象
	private  WifiInfo mWifiInfo = null;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	WriteLog mylog = new WriteLog();

	/**
	 * 构造方法
	 */
	public WifiGPRS(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		mCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * 打开Wifi网卡
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			Log.i(TAG, "开始打开Wifi网卡");
			mylog.WrLog("i",TAG,"开始打开Wifi网卡");
			long a = System.currentTimeMillis();
			try {
				int i = 0;
				mWifiManager.setWifiEnabled(true);
				while(checkNetCardState()!=3 && i < 60){					
		            Thread.currentThread();
					Thread.sleep(1000);
					i++;
				}
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			long b = System.currentTimeMillis();
			Log.i(TAG, "已经打开Wifi网卡："+" 用时："+(b-a)+"毫秒");
			mylog.WrLog("i",TAG,"已经打开Wifi网卡："+" 用时："+(b-a)+"毫秒");
		}
	}

	/**
	 * 关闭Wifi网卡
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 检查当前Wifi网卡状态
	 */
	public int checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "网卡正在关闭");
			mylog.WrLog("i",TAG,"网卡正在关闭");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "网卡已经关闭");
			mylog.WrLog("i",TAG,"网卡已经关闭");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "网卡正在打开");
			mylog.WrLog("i",TAG,"网卡正在打开");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "网卡已经打开");
			mylog.WrLog("i",TAG,"网卡已经打开");
		} else {
			Log.i(TAG, "没有获取到状态");
			mylog.WrLog("i",TAG,"没有获取到状态");
		}
		return mWifiManager.getWifiState();
	}

	/**
	 * 扫描周边网络
	 */
	public boolean scan() {
		Log.i(TAG, "开始扫描周边网络");	
		mylog.WrLog("i",TAG,"开始扫描周边网络");
		mWifiManager.startScan();
		long a = System.currentTimeMillis();
		try {
			int i = 0;
			while(i < 60 && mWifiManager.getScanResults() == null){	
				if(checkNetCardState() != 3){
					openNetCard();
				}
	            Thread.currentThread();
				Thread.sleep(1000);
				i++;
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		long b = System.currentTimeMillis();
		Log.i(TAG, "扫描周边网络完成："+" 用时："+(b-a)+"毫秒");	
		mylog.WrLog("i",TAG, "扫描周边网络完成："+" 用时："+(b-a)+"毫秒");
		
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {			
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
			mylog.WrLog("i",TAG, "当前区域存在无线网络，请查看扫描结果");
			return true;
		} else {
			Log.i(TAG, "当前区域没有无线网络");
			mylog.WrLog("i",TAG, "当前区域没有无线网络");
			return false;
		}
	}

	/**
	 * 得到扫描结果
	 */
	public String getScanResult() {
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// 开始扫描网络
		scan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
					.append(" :").append(mScanResult.SSID).append("->")
					.append(mScanResult.BSSID).append("->")
					.append(mScanResult.capabilities).append("->")
					.append(mScanResult.frequency).append("->")
					.append(mScanResult.level).append("->")
					.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	/**
	 * 连接指定网络
	 */
	public boolean connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		Log.i(TAG, "开始连接指定网络");
		mylog.WrLog("i",TAG, "开始连接指定网络");
		long a = System.currentTimeMillis();
		int i = 0;
		try {			
			while(i < 10 && mWifiInfo.getSupplicantState().toString() != "COMPLETED"){
	            Thread.currentThread();
				Thread.sleep(1000);
				i++;
				mWifiInfo = mWifiManager.getConnectionInfo();
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		long b = System.currentTimeMillis();
		Log.i(TAG, "连接指定网络完成："+" 用时："+(b-a)+"毫秒");	
		mylog.WrLog("i",TAG, "连接指定网络完成："+" 用时："+(b-a)+"毫秒");
		
		if(checkNetWorkState()){
			Log.i(TAG, "连接成功，Wifi网络正常工作");
			mylog.WrLog("i",TAG, "连接成功，Wifi网络正常工作");
			return true;
		}else if(i >= 10){
			Log.i(TAG, "连接Wifi网络超时");
			mylog.WrLog("i",TAG, "连接Wifi网络超时");
			return false;
		}else{
			Log.i(TAG, "连接Wifi网络失败");
			mylog.WrLog("i",TAG, "连接Wifi网络失败");
			return false;
		}
	}

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		Log.i(TAG, "断开Wifi当前连接的网络：NetworkId="+netId);
		mylog.WrLog("i",TAG, "断开Wifi当前连接的网络：NetworkId="+netId);
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * 检查当前Wifi网络状态
	 * 
	 * @return String
	 */
	public  boolean checkNetWorkState() {
		Log.i(TAG, "mWifiInfo=="+mWifiInfo);
		mylog.WrLog("i",TAG, "mWifiInfo=="+mWifiInfo);
		if (mWifiInfo != null) {
			if(mWifiInfo.getSupplicantState().toString() == "COMPLETED"){
				return true;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}
	
	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}
	
	
	/**
	 * 检查并设置GPRS网络连接
	 */
	public  boolean CheckAndConnectGPRS(){
		boolean ConnectState = false;
		String methodName = "setMobileDataEnabled";
		//关闭Wifi网络
		disconnectWifi();
		// 打开GPRS网络
		setGprsEnable(methodName, true);
		Log.i(TAG, "GPRS网络正常工作");	
		mylog.WrLog("i",TAG,"GPRS网络正常工作");
		ConnectState = true;
		return ConnectState;
	}
	
	
	/**
	 * 检查并设置Wifi网络连接
	 */
	public  boolean CheckAndConnectWifi(){
		boolean ConnectState = false;
		// 检查Wifi网络连接状态
		if(!checkNetWorkState()){
			Log.i(TAG, "Wifi网络未连接");
			mylog.WrLog("i",TAG,"Wifi网络未连接");
			// 检查WIFI网卡状态
			if(checkNetCardState()>1){
				Log.i(TAG, "网卡已经打开或正在打开");
				mylog.WrLog("i",TAG,"网卡已经打开或正在打开");
				// WIFI网卡是打开状态，开始扫描周边WIFI网络
				if(scan()){
					Log.i(TAG, "当前区域存在无线网络");
					mylog.WrLog("i",TAG,"当前区域存在无线网络");
					// 打开WIFI无线网络
					if(connect()){
						Log.i(TAG, "Wifi网络正常工作");	
						mylog.WrLog("i",TAG,"Wifi网络正常工作");
						ConnectState = true;
					}else{
						Log.i(TAG, "Wifi网络连接失败");
						mylog.WrLog("i",TAG,"Wifi网络连接失败");
						ConnectState = false;
					}
				}else{
					Log.i(TAG, "当前区域没有无线网络");
					mylog.WrLog("i",TAG,"当前区域没有无线网络");
					ConnectState = false;
				}
			}else{
				Log.i(TAG, "网卡已经关闭或者正在关闭");
				mylog.WrLog("i",TAG,"网卡已经关闭或者正在关闭");
				//WIFI网卡是关闭状态，打开WIFI网卡
				openNetCard();
				CheckAndConnectWifi();  //重新检查并设置网络连接
			}			
		}else{			
			Log.i(TAG, "Wifi网络正常工作");	
			mylog.WrLog("i",TAG,"Wifi网络正常工作");
			ConnectState = true;
		}
		return ConnectState;
	}
	
	
	//检测GPRS是否打开
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean gprsIsOpenMethod(String methodName){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= null;
		Object[] argObject 	= null;
		Boolean isOpen = false;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			isOpen = (Boolean) method.invoke(mCM, argObject);
		} catch (Exception e){
			e.printStackTrace();
		}
		return isOpen;
	}	
	
	/**
	 * 开启/关闭GPRS
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setGprsEnable(String methodName, boolean isEnable){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= new Class[1];
		argClasses[0] 		= boolean.class;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
