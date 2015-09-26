package com.phoneoverheard.bean;

import cn.bmob.v3.BmobObject;

import com.j256.ormlite.field.DatabaseField;

/**
 * 短信号码排除表
 * 
 * @author liulei
 * @date 2015-9-15 上午11:46:59
 * @version 1.0
 */
public class SmsExclude extends BmobObject {

	/** 
	 * 
	*/
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private int id;
	//
	@DatabaseField
	private Boolean enable;
	//
	@DatabaseField
	private String phoneNum;
	/**
	 * 上传状态，0未上传，1已上传，2上传中
	 */
	@DatabaseField
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
