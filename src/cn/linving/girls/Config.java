package cn.linving.girls;

import android.os.Environment;

public class Config {
	/**
	 * 是否只在wifi 下使用 的key
	 * 
	 * values 1 表示 仅在wifi下使用 values 0 表示 都可以使用
	 */
	public static String TYPE_CONN = "TYPE_CONN";
	public static int TYPE_ALL = 0;
	public static int TYPE_WIFI = 1;
	//
	public static String COUNT = "count";

	// URLEncoder.encode("����","UTF-8")
	// http://image.baidu.com/data/imgs?col=��Ů&tag=С����&sort=0&tag3=&pn=0&rn=60&p=channel&from=1
	// http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=��Ů&tag2=ȫ��&ftags=У��&ie=utf8
	// http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=��Ů&tag2=ȫ��&ftags=С����&ie=utf8
	// http://image.baidu.com/data/imgs?col=��Ů&tag=С����&sort=0&tag3=&pn=0&rn=60&p=channel&from=1
	/**
	 * col = ��ǩ �� ��Ů ��Ц��
	 * 
	 * tag = ����ǩ �� ȫ�� ��Ů���ӣ� С���µ�
	 * 
	 * pn�Ǵ����￪ʼ
	 * 
	 * rn �Ƿ�������
	 * 
	 */
	// http://image.baidu.com/data/imgs?col=��Ů&tag=С����&sort=0&tag3=&pn=117&rn=60&p=channel&from=1
	public static final String BASE_URL = "http://image.baidu.com/data/imgs?";
	public static String DIR_PATH = Environment.getExternalStorageDirectory()
			.toString() + "/girls/images/";
	public static final String APP_COL = "美女";
//	public static final String[] TAGS = { "比基尼", "长发",  "短发", "非主流",
//			"高雅大气很有范", "古典美女", "可爱", "嫩萝莉", "清纯", "气质", "时尚", "素颜", "甜素纯",
//			"网络美女", "唯美", "校花"   };
	
	public static final String[] TAGS = { "比基尼", "长发", "长腿", "车模", "短发", "非主流",
		"高雅大气很有范", "古典美女", "可爱", "嫩萝莉", "清纯", "气质", "时尚", "素颜", "甜素纯",
		"网络美女", "唯美", "校花", "写真", "性感美女", "诱惑", "足球宝贝" };
	

}
