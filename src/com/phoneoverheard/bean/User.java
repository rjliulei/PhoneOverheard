package com.phoneoverheard.bean;

import cn.bmob.v3.BmobObject;

/** 
 * 用户类
 * 设备号deviceInfo、手机型号mobileType、手机号码、手机卡ismi、管理员电话号码
 * @author liulei
 * @date 2015-9-25 下午1:58:12 
 * @version 1.0 
*/
public class User extends BmobObject{

	/** 
	 * 
	*/ 
	private static final long serialVersionUID = 1L;

	private String deviceInfo;
	private String phoneNum;
	private String ismi;
	private String adminPhoneNum;

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getIsmi() {
		return ismi;
	}

	public void setIsmi(String ismi) {
		this.ismi = ismi;
	}

	public String getAdminPhoneNum() {
		return adminPhoneNum;
	}

	public void setAdminPhoneNum(String adminPhoneNum) {
		this.adminPhoneNum = adminPhoneNum;
	}
}
