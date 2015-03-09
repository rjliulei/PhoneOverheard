package cn.linving.girls.tools;

import android.util.Log;
import cn.linving.girls.Config;

/**
 * 
 * @author ving
 *
 */
public class MyLog {

	private static final String TAG = "grils";
	
	public static void i(String tag, String msg) {
	
			Log.i(TAG, "[" + tag + "]" + msg);
	}


}
