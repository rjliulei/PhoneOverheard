package com.phoneoverheard.phonne;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class WriteLog {	
	/**
	 * 写日志文件
	 * @param Info 日志信息
	 * @param filedir 日志文件保存目录
	 * @param TAG 日志来源（标志）
	 * @param infoType 日志类型
	 */
	@SuppressLint("SimpleDateFormat")
	public void WrLog(String infoType,String TAG,String Info){ 
		String Msg = "";
		String filename = "";	
		String filedir = "/phonne/log/";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat Timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMdd");
		final String receiveDate = Dateformat.format(date);
		final String receiveTime = Timeformat.format(date);
		//将数据写入SD卡文件中
	    FileService FileService = new FileService();
	    FileService.getSDPath(filedir);
	    //filedir = Environment.getExternalStorageDirectory()+filedir;//获取SDCard目录
	    Log.i(TAG, "日志文件保存目录:"+filedir);
	    try {
		    boolean sdCardExist = Environment.getExternalStorageState()
					.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在  	
    		if (sdCardExist){ 
    			new FileService().getSDPath(filedir);
    		}
    		if(infoType.equals("i")){
	    		filename = "info"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.i(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);
    		}else if(infoType.equals("w")){
	    		filename = "warn"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.w(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);	    			
    		}else{
	    		filename = "error"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.e(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);	    			
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 	
   

   
}
