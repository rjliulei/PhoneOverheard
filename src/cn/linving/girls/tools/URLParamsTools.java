package cn.linving.girls.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.linving.girls.bean.Params;

/**
 * 
 * @author ving
 *
 */
public class URLParamsTools {
	public static final String BASE_URL = "http://image.baidu.com/data/imgs?";
	public static final String TAG = "URLParamsTools";

	/**
	 * ������·�� ת�� ��׼·��
	 * 
	 * From :
	 * 
	 * http://image.baidu.com/data/imgs?col=��Ů&tag=С����&sort=0&tag3=&pn=117&rn=60
	 * &p=channel&from=1
	 * 
	 * To :
	 * 
	 * http://image.baidu.com/data/imgs?col=%E7%BE%
	 * 8E%E5%A5%B3&tag=%E5%B0%8F%E6%B8%85%E6%96%B0&sort=0&tag3=&pn=117&rn=60&p=ch
	 * a n n e l & f r o m = 1
	 * 
	 * @return URLEncoder.encode("����","UTF-8")
	 */
	public static String getEncoderURL(Params params) {
		String url = "";
		if (null != params) {
			url = BASE_URL + "col=" + EncodeString(params.getCol()) + "&tag="
					+ EncodeString(params.getTag()) + "&sort=0&tag3=&pn="
					+ params.getPn() + "&rn=" + params.getRn()
					+ "&p=channel&from=1";
		} else {
			url = "http://image.baidu.com/data/imgs?col=%E6%90%9E%E7%AC%91&tag=%E5%85%A8%E9%83%A8&sort=0&tag3=a&pn=17&rn=1&p=channel&from=1";
		}

		return url;
	}

	public static String EncodeString(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			MyLog.i(TAG, e.toString());
			return "";
		}

	}
}
