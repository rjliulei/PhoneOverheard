package com.phoneoverheard.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

/**
 * @author liulei
 * @date 2015-1-3
 */
public class StringUtils {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** 预约提醒时用到的模板 */
	public static final String ALARM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

	public static final String POINT_DATE_PATTERN = "yyyy.MM.dd";
	public static final String STROKE_DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_HOUR_MINUTE_PATTERN = "HH:mm";
	public final static String EMPTY = "";

	/************* 日期相关 ***************/

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @return 例如2011-3-24
	 */
	public static String formatDate(Date date) {
		return formatDate(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 格式化日期时间字符串
	 * 
	 * @param date
	 * @return 例如2011-11-30 16:06:54
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * 获取当前时间 格式为yyyy-MM-dd 例如2011-07-08
	 * 
	 * @return
	 */
	public static String getDate() {
		return formatDate(new Date(), DEFAULT_DATE_PATTERN);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDateTime() {
		return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateStr() {
		String dateStr = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateStr = dateFormat.format(new Date());

		return dateStr;
	}

	/**
	 * 将字符串转换为时间
	 * 
	 * @param strDate
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date strToDate(String strDate) {

		return StringToDate(strDate, DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * String to Date
	 * 
	 * @author liulei
	 */
	public static Date StringToDate(String data, String pattern) {

		if (null == data)
			return null;

		Date date = null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(data);
		} catch (ParseException e) {
			Util.printInfor("StringUtils", e.getMessage());
		}

		return date;
	}

	/***
	 * 将字符串转化为calender
	 * */
	public static Calendar string2Calendar(String time, String pattern) {

		if (null == time || null == pattern)
			return null;

		Date date = StringToDate(time, pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar;
	}

	public static String calendar2String(Calendar time, String pattern) {

		if (null == time || null == pattern)
			return null;
		Date date = time.getTime();
		return formatDate(date, pattern);
	}

	/**
	 * 获取时间差，返回值以ms为单位 one - other
	 * 
	 * @author liulei
	 * @date 2015-2-5
	 * @Title: getDifTime
	 * @param one
	 * @param other
	 * @return long
	 */
	public static long getDifTime(Date one, Date other) {

		return one.getTime() - other.getTime();
	}

	/**
	 * 减少时间
	 */
	public static Calendar reduceByMinute(Calendar time, int minute) {

		Calendar calendar = null;

		if (null == time)
			return calendar;

		calendar = time;
		calendar.add(Calendar.MINUTE, -minute);

		return calendar;
	}

	public static int nowIsDayOrNight() {

		return 0;
	}

	/**
	 * 减少时间
	 * 
	 * @author liulei
	 * @date 2015-4-4
	 * @param time
	 * @param day
	 * @return Calendar
	 */
	public static Calendar reduceByDay(Calendar time, int day) {

		Calendar calendar = null;

		if (null == time)
			return calendar;

		calendar = time;
		calendar.add(Calendar.DAY_OF_YEAR, -day);

		return calendar;
	}

	/**
	 * 从日期时间中取出日期XXXX-XX-XX XX:XX:XX -> XX-XX
	 * 
	 * @author liulei
	 */
	public static String splitDateToDay(String date) {

		if (null == date)
			return "";

		return date.substring(5, 10);
	}

	/**
	 * 从日期时间中取出日期和时间 使用格式化函数
	 * 
	 * @author liulei
	 */
	public static String[] splitDate2Part(String date, String pattern) {

		if (null == date || null == pattern || date.trim().equals("") || pattern.trim().equals(""))
			return null;

		String[] result = new String[2];

		Date dateTime = StringToDate(date, pattern);
		result[0] = formatDate(dateTime, STROKE_DATE_PATTERN);
		result[1] = formatDate(dateTime, TIME_HOUR_MINUTE_PATTERN);
		return result;
	}

	/**
	 * 获取时间差
	 * 
	 * @param sysTime
	 *            系统时间
	 * @param time
	 *            时间
	 * @return 时间差
	 */
	public static String getRetime(String sysTime, String time) {

		String retime = null;

		long ss = (StringUtils.strToDate(sysTime).getTime() - StringUtils.strToDate(time).getTime()) / 1000;
		if (ss < 0) {
			ss = -1 * ss;
		}
		int MM = (int) ss / 60; // 共计分钟数
		int hh = (int) ss / 3600; // 共计小时数
		int dd = (int) hh / 24; // 共计天数

		if (dd >= 1) {
			if (dd > 30) {
				retime = "一个月前";
			} else {
				retime = dd + "天前";
			}
		} else {
			if (hh >= 1) {
				retime = hh + "小时前";
			} else {
				retime = MM + "分钟前";
				if (MM == 0) {
					retime = "刚刚";
				}
			}
		}
		return retime;

	}

	/**
	 * 获取日期是今天明天后天 -2前天-1昨天0今天1明天2后天
	 * 
	 * @author liulei
	 */
	public static String getWhichDay(String dateNow, String dateTarget) {

		String result = "";

		if (null == dateNow || null == dateTarget || dateNow.trim().equals("") || dateTarget.trim().equals("")) {
			return result;
		}

		int nowDay = string2Calendar(dateNow, DEFAULT_DATE_PATTERN).get(Calendar.DAY_OF_YEAR);
		int targetDay = string2Calendar(dateTarget, DEFAULT_DATE_PATTERN).get(Calendar.DAY_OF_YEAR);

		switch (targetDay - nowDay) {
		case -2:
			result = "前天";
			break;
		case -1:
			result = "昨天";
			break;
		case 0:
			result = "今天";
			break;
		case 1:
			result = "明天";
			break;
		case 2:
			result = "后天";
			break;

		default:
			break;
		}

		return result;
	}

	/**
	 * 获取日期为星期几
	 * 
	 * @author liulei
	 */
	public static String getDayOfWeek(String date) {

		String result = "";

		if (null == date) {
			return result;
		}

		int day = string2Calendar(date, DEFAULT_DATE_PATTERN).get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.MONDAY:
			result = "星期一";
			break;
		case Calendar.TUESDAY:
			result = "星期二";
			break;
		case Calendar.WEDNESDAY:
			result = "星期三";
			break;
		case Calendar.THURSDAY:
			result = "星期四";
			break;
		case Calendar.FRIDAY:
			result = "星期五";
			break;
		case Calendar.SATURDAY:
			result = "星期六";
			break;
		case Calendar.SUNDAY:
			result = "星期日";
			break;

		default:
			break;
		}

		return result;
	}

	/**************** 字符串处理相关 ************************/

	/**
	 * 将列表用--连接成字符串
	 * 
	 * @author liulei
	 */
	public static String jointListWithStroke(ArrayList<String> list, String stroke) {
		String result = "";

		if (null == list || null == stroke || list.size() == 0) {
			return result;
		}

		int iLoop = 0;
		for (; iLoop < list.size() - 1; ++iLoop) {

			result += list.get(iLoop) + stroke;

		}

		result += list.get(iLoop);
		return result;
	}

	/**
	 * 将arraylist<String>转化成String
	 * 
	 * @param arrayList
	 * @return
	 */
	public static String arrayListToString(List<String> arrayList, String separator) {

		if (null == arrayList || null == separator) {

			return "";
		}

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < arrayList.size(); i++) {
			if (i == 0) {
				buffer.append(arrayList.get(i));
			} else {
				buffer.append(separator);
				buffer.append(arrayList.get(i));
			}
		}
		return buffer.toString();
	}

	public static String join(final ArrayList<String> array, String separator) {
		StringBuffer result = new StringBuffer();
		if (array != null && array.size() > 0) {
			for (String str : array) {
				result.append(str);
				result.append(separator);
			}
			result.delete(result.length() - 1, result.length());
		}
		return result.toString();
	}

	public static boolean isEmpty(String str) {

		return str == null || str.length() == 0;
	}

	/**
	 * 格式化字符串，XXXX XXXX XXXX……
	 * 
	 * @author liulei
	 */
	public static String formatAppointCode(String code) {

		String result = null;

		if (null == code)
			return null;

		int length = code.length();
		int increase = 4;

		int iCount = length / increase;
		result = "";

		for (int iLoop = 0; iLoop < iCount; ++iLoop) {

			result += (code.substring(increase * iLoop, increase * (iLoop + 1)) + " ");
		}

		if (increase * iCount < length) {
			result += code.substring(increase * iCount);
		}

		return result;
	}

	/**
	 * 获取百分比字符串
	 * 
	 * @author liulei
	 * @date 2015-2-8
	 * @Title: getPercent
	 * @param progress
	 * @param max
	 * @return String
	 */
	public static String getPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress > max) {
			rate = 100;
		} else {
			rate = (int) ((double) progress / max * 100);
		}
		return new StringBuilder(16).append(rate).append("%").toString();
	}

	/**
	 * 
	 * @author liulei
	 * @date 2015-1-31
	 * @Title: truncatString
	 * @Description: TODO 截断字符串，以某个字符替换 用在银行卡号只显示后几位，隐藏其他位
	 * @param @param src源字符串
	 * @param @param precount前半段保留字符个数
	 * @param @param lastcount后半段保留字符个数
	 * @param @param replace替换字符
	 * @param replacecount
	 *            替换字符的个数
	 * @return String
	 */
	public static String truncatString(String src, int precount, int lastcount, String replace, int replacecount) {

		if (null == src || null == replace)
			return "";

		int length = src.length();
		if (precount < 0 || lastcount < 0 || precount >= length || lastcount >= length
				|| (precount + lastcount) >= length) {

			return src;
		}

		StringBuilder des = null;
		if (precount > 0) {
			des.append(src.substring(0, precount));
		}

		int realcount = replacecount > 0 ? replacecount : length - precount - lastcount;
		for (int i = 0; i < realcount; ++i) {
			des.append(replace);
		}

		des.append(src.substring(length - lastcount, length));
		return des.toString();
	}

	/**
	 * 拼接字符串，以"至"相连
	 * 
	 * @author liulei
	 */
	public static String JointWithStrike(String left, String right) {

		String result = null;

		if ((null == left) && (null == right))
			return null;

		result = left + "至" + right;

		return result;
	}

	/**
	 * 拆分字符串，以中划线相连
	 * 
	 * @author liulei
	 */
	public static String[] SplitWithStrike(String data) {

		if (null == data)
			return null;

		String[] result = data.split("至");

		return result;
	}

	/**
	 * 生成“xx点xx分”
	 * 
	 * @param hour
	 *            小时
	 * @param strHour
	 *            点
	 * @param minute
	 *            分钟
	 * @param strMinute
	 *            分
	 * @author liulei
	 */
	public static String createTime(int hour, String strHour, int minute, String strMinute) {

		if (0 == minute && !strMinute.trim().equals("")) {
			return String.format("%02d", hour) + strHour;
		} else {
			return String.format("%02d", hour) + strHour + String.format("%02d", minute) + strMinute;
		}
	}

	/**
	 * 拆分字符串，以中划线相连
	 * 
	 * @author liulei
	 */
	public static String[] splitWithStrike(String data) {

		if (null == data)
			return null;

		String[] result = data.split("-");

		return result;
	}

	/** 传入时间段获取开始时间 */
	public static String[] splitToStartTime(String timeBucket, String hour, String minute) {

		String startTime[] = new String[2];

		String time[] = splitWithStrike(timeBucket);

		if (time.length < 2)
			return startTime;

		startTime[0] = timeClientModeToServerMode(time[0], hour, minute);
		startTime[1] = timeClientModeToServerMode(time[1], hour, minute);

		return startTime;
	}

	/**
	 * 拆分出小时和分钟,传入单位（点、分）
	 * 
	 * @param time
	 *            时间
	 * @param strHour
	 *            点
	 * @param strMinute
	 *            分
	 * @author liulei
	 */
	public static int[] splitHourMinute(String time, String strHour, String strMinute) {

		int[] iResult = new int[2];
		String[] temp = null;

		if ((null != time) && (time.contains(strHour))) {
			temp = time.split(strHour);
			iResult[0] = Integer.valueOf(temp[0]);

			if (1 < temp.length && strMinute.trim().equals("")) {
				iResult[1] = Integer.valueOf(temp[1]);
			} else if ((1 < temp.length) && (temp[1].contains(strMinute))) {
				temp = temp[1].split(strMinute);
				iResult[1] = Integer.valueOf(temp[0]);
			}
		}

		return iResult;
	}

	/**
	 * 计算出一天的工作时间（以分钟为单位）
	 * 
	 * @author liulei
	 */
	public static int calculateWorkTime(String startTime, String endTime, String strHour, String strMinute) {

		int[] iStartTime = splitHourMinute(startTime, strHour, strMinute);
		int[] iEndTime = splitHourMinute(endTime, strHour, strMinute);

		return (iEndTime[0] * 60 + iEndTime[1]) - (iStartTime[0] * 60 + iStartTime[1]);
	}

	/**
	 * 拼接字符串，以"-"相连
	 * 
	 * @author liulei
	 */
	public static String jointWithStrike(String left, String right) {

		String result = null;

		if ((null == left) && (null == right))
			return null;

		result = left + "-" + right;

		return result;
	}

	/**
	 * 根据增幅计算出相应时间
	 * 
	 * @return XX点XX分
	 * @author liulei
	 */
	public static String calculateNextTime(String time, int timeInterval, String strHour, String strMinute) {

		int iTime = calculateWorkTime("", time, strHour, strMinute);
		int iNextTime = iTime + timeInterval;

		int iHour = iNextTime / 60;
		int iMinute = iNextTime % 60;

		return createTime(iHour, strHour, iMinute, strMinute);
	}

	public static final String STRING_POINT_SPLIT = ":";

	/**
	 * 格式化时间字符串XX:XX
	 * 
	 * @author liulei
	 */
	public static String timeClientModeToServerMode(String time, String strHour, String strMinute) {

		int[] temp = splitHourMinute(time, strHour, strMinute);

		if (0 == temp[1]) {

			return String.format("%02d", temp[0]);
		} else {

			if (0 == temp[1]) {

				return String.format("%02d", temp[0]);
			}

			return String.format("%02d", temp[0]) + STRING_POINT_SPLIT + String.format("%02d", temp[1]);
		}
	}

	/**
	 * 格式化时间字符串XX点XX分
	 * 
	 * @author liulei
	 */
	public static String timeServerModeToClientMode(String time, String strHour, String strMinute) {

		String strRsl = "";

		if (null != time) {
			String[] temp = time.split(STRING_POINT_SPLIT);

			if (1 < temp.length) {
				strRsl = temp[0] + strHour + temp[1] + strMinute;
			} else {
				strRsl = temp[0] + strHour;
			}
		}

		return strRsl;
	}

	/**
	 * 获取时间间隔 以分钟为单位
	 * 
	 * @param startTime
	 *            xx:xx
	 * @author liulei
	 */
	public static int getTimeInterval(String startTime, String endTime) {

		if (null == startTime || null == endTime)
			return -1;

		int start = 0;
		int end = 0;
		String[] tempStart = startTime.split(STRING_POINT_SPLIT);
		String[] tempEnd = endTime.split(STRING_POINT_SPLIT);

		if (1 < tempStart.length) {
			start = Integer.valueOf(tempStart[0]) * 60 + Integer.valueOf(tempStart[1]);
		} else {

			start = Integer.valueOf(tempStart[0]) * 60;
		}

		if (1 < tempEnd.length) {
			end = Integer.valueOf(tempEnd[0]) * 60 + Integer.valueOf(tempEnd[1]);
		} else {

			end = Integer.valueOf(tempEnd[0]) * 60;
		}

		return end - start;
	}
	
	/**
	 * 根据当前时间获取定时器触发时间
	 * 
	 * @author liulei
	 * @date 2015-7-14
	 * @return Calendar
	 */
	public static Calendar getTargetTime(int interval) {

		Calendar time = Calendar.getInstance();
		int hour = time.get(Calendar.HOUR_OF_DAY);

		int hourTarget = hour + (interval - hour % interval);
		time.set(Calendar.HOUR_OF_DAY, hourTarget);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);

		return time;
	}

	/**
	 * 根据当前时间获取倒计时时间
	 * 
	 * @author liulei
	 * @date 2015-7-14
	 * @param interval
	 *            几小时提醒一次
	 * @return int[]
	 */
	public static int[] getCountDown(int interval) {

		int[] rsl = new int[3];

		Calendar time = Calendar.getInstance();
		Calendar timeTarget = getTargetTime(interval);

		long left = (timeTarget.getTimeInMillis() - time.getTimeInMillis()) / 1000;
		// int day = (int) (left / (24 * 60 * 60));
		rsl[2] = (int) (left / (60 * 60));// 时
		rsl[1] = (int) (left % (60 * 60) / 60);// 分
		rsl[0] = (int) (left % 60);// 秒

		return rsl;
	}

	/************* 正则表达式相关 Regular Expression ********************************/

	/** 金额，小数点后两位，最大值9999.99 */
	public static final String REGULAR_EX_MONEY = "^([1-9]\\d{0,3}|0)(\\.\\d{1,2})?$";
	// 手机号和固话大于10小于13
	public static final String REGULAR_EX_MOBILE = "[0-1]\\d{9,12}";
	public static final String RE_FULL_CHINESE = "^[\u4e00-\u9fa5]*$";

	// 固定电话
	// public static final String REGULAR_EX_TELEPHONE =
	// "(\\d{4}-|\\d{3}-)?(\\d{8}|\\d{7})";

	/**
	 * 匹配正则表达式
	 * 
	 * @author liulei
	 * @date 2014-12-27
	 * @param data
	 * @param regularex
	 * @return
	 */
	public static boolean matchRegularExpression(String data, String regularex) {

		Pattern p = Pattern.compile(regularex);
		Matcher m = p.matcher(data);

		return m.matches();
	}

	/***
	 * 常用正则表达式
	 * 
	 * 用户名 /^[a-z0-9_-]{3,16}$/ 密码 /^[a-z0-9_-]{6,18}$/ 密码2
	 * (?=^.{8,}$)(?=.*\d)(?=.*\W+)(?=.*[A-Z])(?=.*[a-z])(?!.*\n).*$
	 * (由数字/大写字母/小写字母/标点符号组成，四种都必有，8位以上) 十六进制值 /^#?([a-f0-9]{6}|[a-f0-9]{3})$/
	 * 电子邮箱 /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/
	 * /^[a-z\d]+(\.[a-z\d
	 * ]+)*@([\da-z](-[\da-z])?)+(\.{1,2}[a-z]+)+$/或\w+([-+.]\
	 * w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)* URL
	 * /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/ 或
	 * [a-zA-z]+://[^\s]* IP 地址
	 * /((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)/
	 * /^(?:(
	 * ?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|
	 * [01]?[0-9][0-9]?)$/ 或
	 * ((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?) HTML 标签
	 * /^<([a-z]+)([^<]+)*(?:>(.*)<\/\1>|\s+\/>)$/或<(.*)(.*)>.*<\/\1>|<(.*) \/>
	 * 删除代码\\注释 (?<!http:|\S)//.*$ 匹配双字节字符(包括汉字在内) [^\x00-\xff] 汉字(字符)
	 * [\u4e00-\u9fa5] Unicode编码中的汉字范围 /^[\u2E80-\u9FFF]+$/ 中文及全角标点符号(字符)
	 * [\u3000-\u301e\ufe10-\ufe19\ufe30-\ufe44\ufe50-\ufe6b\uff01-\uffee]
	 * 日期(年-月-日)
	 * (\d{4}|\d{2})-((0?([1-9]))|(1[1|2]))-((0?[1-9])|([12]([1-9]))|(3[0|1]))
	 * 日期(月/日/年)
	 * ((0?[1-9]{1})|(1[1|2]))/(0?[1-9]|([12][1-9])|(3[0|1]))/(\d{4}|\d{2})
	 * 时间(小时:分钟, 24小时制) ((1|0?)[0-9]|2[0-3]):([0-5][0-9]) 中国大陆固定电话号码
	 * (\d{4}-|\d{3}-)?(\d{8}|\d{7}) 中国大陆手机号码 1\d{10} 中国大陆邮政编码 [1-9]\d{5}
	 * 中国大陆身份证号(15位或18位) \d{15}(\d\d[0-9xX])? 非负整数(正整数或零) \d+ 正整数
	 * [0-9]*[1-9][0-9]* 负整数 -[0-9]*[1-9][0-9]* 整数 -?\d+ 小数 (-?\d+)(\.\d+)? 空白行
	 * \n\s*\r 或者 \n\n(editplus) 或者 ^[\s\S ]*\n QQ号码 [1-9]\d{4,} 不包含abc的单词
	 * \b((?!abc)\w)+\b 匹配首尾空白字符 ^\s*|\s*$ 编辑常用 以下是针对特殊中文的一些替换(editplus)
	 * 
	 * ^[0-9].*\n
	 * 
	 * ^[^第].*\n
	 * 
	 * ^[习题].*\n
	 * 
	 * ^[\s\S ]*\n ^[0-9]*\. ^[\s\S ]*\n
	 * <p
	 * [^<>*]>
	 * href="javascript:if\(confirm\('(.*?)'\)\)window\.location='(.*?)'" <span
	 * style=".[^"]*rgb\(255,255,255\)">.[^<>]*</span>
	 * 
	 * <DIV class=xs0>[\s\S]*?</DIV>
	 */

	/**
	 * @author liulei
	 * @date 2015-1-3 小于1000m, m 大于1000m，km
	 * @param distance
	 * @return
	 */
	public static String convert2DistanceString(float distance) {

		String rsl = "";

		if (distance < 1000) {

			rsl = distance + "m";
		} else if (distance >= 1000) {

			DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
			rsl = decimalFormat.format(distance / 1000) + "km";
		}

		return rsl;
	}

	public static String formatDouble(double data) {

		return new DecimalFormat(".00").format(data);
	}

}
