package com.phoneoverheard.bean;

import cn.bmob.v3.BmobObject;

import com.j256.ormlite.field.DatabaseField;

/**
 * 短信表
 * 
 * @author liulei
 * @date 2015-9-15 上午11:46:59
 * @version 1.0
 */
public class Sms extends BmobObject {

	/** 
	 * 
	*/
	private static final long serialVersionUID = 1L;

	private String userId;

	//发件人
	@DatabaseField
	private String sendPhoneNum;
	//收件人
	@DatabaseField
	private String receivePhoneNum;
	//短信内容
	@DatabaseField
	private String content;
	//短信时间
	@DatabaseField
	private String smsTime;
	@DatabaseField(generatedId = true)
	private int id;
	/**
	 * 上传状态，0未上传，1已上传，2上传中
	 */
	@DatabaseField
	private int state;
	//系统数据库中的id
	@DatabaseField
	private int idLocal;

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSendPhoneNum() {
		return sendPhoneNum;
	}

	public void setSendPhoneNum(String sendPhoneNum) {
		this.sendPhoneNum = sendPhoneNum;
	}

	public String getReceivePhoneNum() {
		return receivePhoneNum;
	}

	public void setReceivePhoneNum(String receivePhoneNum) {
		this.receivePhoneNum = receivePhoneNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSmsTime() {
		return smsTime;
	}

	public void setSmsTime(String smsTime) {
		this.smsTime = smsTime;
	}

}
