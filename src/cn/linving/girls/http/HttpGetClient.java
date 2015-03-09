package cn.linving.girls.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.linving.girls.Config;
import cn.linving.girls.MyApplication;
import cn.linving.girls.tools.MySharePreference;
import cn.linving.girls.tools.MyLog;
import cn.linving.girls.tools.NetWorkUtils;
import cn.linving.girls.tools.NetWorkUtils.NetWorkState;

/**
 * EHTTP GET
 * 
 * @author ving
 *
 */
public class HttpGetClient {
	//
	public static final int NULL_INPUTSTREAM = -1;
	public static final int REQUEST_FAIL = -2;
	public static final int URL_Exception = -3;
	public static final int IO_Exception = -4;
	// 无网络访问权限
	public static final int Con_Permission = -5;

	//
	public static final String TAG = "HttpGetClient";
	private static HttpGetClient mHttpGetClient = null;
	private static ExecutorService threadPool = null;

	//

	MySharePreference appPreference = null;

	private HttpGetClient() {
		//
		int size = Runtime.getRuntime().availableProcessors();
		threadPool = Executors.newFixedThreadPool(size);
		if (null != MyApplication.AppContext)
			appPreference = new MySharePreference(MyApplication.AppContext);
	}

	//
	public static synchronized HttpGetClient getInstance() {
		if (null == mHttpGetClient) {
			mHttpGetClient = new HttpGetClient();
		}
		return mHttpGetClient;
	}

	public void asyHttpGetRequest(String url, HttpResponseCallBack httpCallBack) {
		if (null != appPreference) {
			int permission = appPreference.getInt(Config.TYPE_CONN, 0);
			NetWorkUtils netUtils = new NetWorkUtils(MyApplication.AppContext);
			NetWorkState state = netUtils.getConnectState();
			if (state.equals(NetWorkState.MOBILE)) {

				if (permission == Config.TYPE_ALL) {
					threadPool.execute(getGetHttpThread(url, httpCallBack));
				} else if (permission == Config.TYPE_WIFI) {
					httpCallBack.onFailure(0, Con_Permission,
							"请在设置中打开MOBILE连接 ");
					MyLog.i(TAG, "未发送请求，用户设置了网络限制");
				}
				// 未知网络
				else {
					threadPool.execute(getGetHttpThread(url, httpCallBack));
				}

			} else {
				threadPool.execute(getGetHttpThread(url, httpCallBack));
			}

		} else {
			threadPool.execute(getGetHttpThread(url, httpCallBack));
		}

	}

	private Runnable getGetHttpThread(final String urlStr,
			final HttpResponseCallBack httpCallBack) {

		return new Runnable() {
			int responseCode = -1;
			InputStream inputStream = null;

			BufferedReader reader = null;
			HttpURLConnection conn = null;
			URL url = null;

			@Override
			public void run() {
				try {
					url = new URL(urlStr);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					conn.setDoInput(true);
					conn.setDoOutput(false);
					conn.setUseCaches(true);
					conn.setRequestProperty("User-agent", "Mozilla/5.0");
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					// outputStream = conn.getOutputStream();
					// outputStream.write(params.toString().getBytes());
					responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						inputStream = conn.getInputStream();
						if (null != inputStream) {
							reader = new BufferedReader(new InputStreamReader(
									inputStream, "utf-8"));
							StringBuilder strBuilder = new StringBuilder();
							// int i = 0;
							// char[] buf = new char[1024];
							// while ((i = reader.read(buf)) != -1) {
							// strBuilder.append(buf, 0, i);
							// }

							String line = null;
							while (null != (line = reader.readLine()))
								strBuilder.append(line);

							httpCallBack.onSuccess(urlStr,
									strBuilder.toString());
						} else {
							httpCallBack.onFailure(responseCode,
									NULL_INPUTSTREAM, "读取数据失败！");
							MyLog.i(TAG, "读取数据失败！");
						}

					} else {
						httpCallBack.onFailure(responseCode, REQUEST_FAIL,
								"请求失败！");
					}

				} catch (MalformedURLException e) {
					httpCallBack.onFailure(responseCode, URL_Exception,
							e.getMessage());
					MyLog.i(TAG, e.toString());
				} catch (IOException e) {
					httpCallBack.onFailure(responseCode, IO_Exception,
							e.getMessage());
					MyLog.i(TAG, e.toString());
				} finally {
					try {
						if (null != reader)
							reader.close();
					} catch (IOException ex) {
						MyLog.i(TAG, ex.toString());
					}
					try {
						if (null != inputStream)
							inputStream.close();
					} catch (IOException ex) {
						MyLog.i(TAG, ex.toString());
					}
				}

				if (null != conn)
					conn.disconnect();
			}
		};

	}

}
