package cn.linving.girls;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.phoneoverheard.phonne.R;

public class MyApplication extends Application {
	public static String CACHE_DIR = "";
	public static Context AppContext;
	// 成功加载广告数
	private static List<Activity> activitys = new LinkedList<Activity>();

	public List<Activity> getActivitys() {
		return activitys;
	}

	public static void addActivity(Activity a) {
		activitys.add(a);
	}

	public static void exit() {
		for (Activity a : activitys) {
			a.finish();
		}
	}

	@Override
	public void onCreate() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.loading)
				.bitmapConfig(Bitmap.Config.ALPHA_8).considerExifParams(true)
				.showImageOnFail(R.drawable.loaded_fail).cacheInMemory(true)
				.cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(3000)// 缓存一百张图片
				.writeDebugLogs().build();
		CACHE_DIR = getApplicationContext().getFilesDir().getPath();
		AppContext = getApplicationContext();

		// ImageLoaderConfiguration config2 = new
		// ImageLoaderConfiguration.Builder(this)
		// .defaultDisplayImageOptions(defaultOptions)
		// .threadPoolSize(3) // default
		// .threadPriority(Thread.NORM_PRIORITY - 2) // default
		// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		// .denyCacheImageMultipleSizesInMemory()
		// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		// .memoryCacheSize(2 * 1024 * 1024)
		// .memoryCacheSizePercentage(13) // default
		// .diskCacheSize(50 * 1024 * 1024)
		// .diskCacheFileCount(100*100)
		// .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) //
		// default
		// .imageDownloader(new BaseImageDownloader(this)) // default
		// .imageDecoder(new BaseImageDecoder(true)) // default
		// .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) //
		// default
		// .writeDebugLogs()
		// .build();

		ImageLoader.getInstance().init(config);
		
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	/**{"device_id":"869451015663771","mac":"78:f5:fd:63:65:47"}*/
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
