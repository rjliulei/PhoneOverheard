package com.phoneoverheard.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

import com.j256.ormlite.field.DatabaseField;

/**
 * 上传文件表 上传文件名、上传时间、是否已上传、文件路径、文件创建时间、文件所属模块、手机型号、设备号、主叫号码、被叫号码
 * 
 * @author liulei
 * @date 2015-9-15 上午11:46:59
 * @version 1.0
 */
public class UploadFiles extends BmobObject {

	/** 
	 * 
	*/
	private static final long serialVersionUID = 1L;

	//文件上传路径
	private String fileUploadPath;

	@DatabaseField
	private String userId;
	@DatabaseField
	private String deviceInfo;
	@DatabaseField
	private String sendNum;
	@DatabaseField
	private String receiveNum;

	// 文件名
	@DatabaseField
	private String fileName;
	@DatabaseField
	private String fileFullName;
	// 文件创建时间
	@DatabaseField
	private String createTime;
	// 模块
	@DatabaseField
	private String module;
	@DatabaseField(generatedId = true)
	private int id;
	/**
	 * 上传状态，0未上传，1已上传，2上传中
	 */
	@DatabaseField
	private int state;

	public String getFileUploadPath() {
		return fileUploadPath;
	}

	public void setFileUploadPath(String fileUploadPath) {
		this.fileUploadPath = fileUploadPath;
	}

	public String getFileFullName() {
		return fileFullName;
	}

	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getSendNum() {
		return sendNum;
	}

	public void setSendNum(String sendNum) {
		this.sendNum = sendNum;
	}

	public String getReceiveNum() {
		return receiveNum;
	}

	public void setReceiveNum(String receiveNum) {
		this.receiveNum = receiveNum;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
