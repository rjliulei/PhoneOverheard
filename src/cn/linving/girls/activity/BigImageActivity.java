package cn.linving.girls.activity;

import java.io.File;
import java.io.IOException;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.linving.girls.Config;
import cn.linving.girls.MyApplication;
import cn.linving.girls.bean.RowImage;
import cn.linving.girls.db.DaoHelper;
import cn.linving.girls.fragment.XiaoQingXinFragment;
import cn.linving.girls.tools.MySharePreference;
import cn.linving.girls.tools.BitmapTools;
import cn.linving.girls.tools.MyLog;
import cn.linving.girls.view.AttacherImageView;
import cn.linving.girls.view.ProgressWheel;
import cn.linving.girls.volley.BitmapCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.phoneoverheard.phonne.R;
import com.umeng.analytics.MobclickAgent;

public class BigImageActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, OnLongClickListener {
	public final String TAG = "BigImageActivity";
	private AttacherImageView mBigImage;
	private ProgressWheel mProgressBar;
	// private PhotoViewAttacher mViewAttacher = null;
	private RowImage mRowImage = null;
	private Button bt_downLoad, bt_share, bt_setwallpaper;
	private String sharePic = "temp_photo";
	private CheckBox rb_collect;
	private DaoHelper<RowImage> rowImageDao;
	private boolean isCollect = false;
	private RelativeLayout parent_layout;

	//
	// 查看广告次数
	private int adcount = 0;
	private MySharePreference sp;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MyApplication.addActivity(this);
		setContentView(R.layout.activity_bigimage_layout);
		initData();
		initView();
		addADS();
		if (null != mRowImage)
			try {
				attachData();
			} catch (Exception e) {
				MyLog.i(TAG, e.toString());
			}

	}

	private void initView() {
		mBigImage = (AttacherImageView) findViewById(R.id.mBigImage);
		mProgressBar = (ProgressWheel) findViewById(R.id.mProgressBar);
		int[] pixels = new int[] { 0xFF2E9121, 0xFF2E9121, 0xFF2E9121,
				0xFF2E9121, 0xFF2E9121, 0xFF2E9121, 0xFFFFFFFF, 0xFFFFFFFF };
		Bitmap bm = Bitmap.createBitmap(pixels, 8, 1, Bitmap.Config.ARGB_8888);
		Shader shader = new BitmapShader(bm, Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		mProgressBar.setRimShader(shader);
		mProgressBar.setVisibility(View.GONE);
		// mViewAttacher = new PhotoViewAttacher(mBigImage);
		bt_downLoad = (Button) findViewById(R.id.bt_downLoad);
		bt_downLoad.setOnClickListener(this);
		// bt_downLoad.setClickable(false);
		bt_share = (Button) findViewById(R.id.bt_share);
		bt_share.setOnClickListener(this);
		// bt_share.setClickable(false);
		bt_setwallpaper = (Button) findViewById(R.id.bt_setwallpaper);
		bt_setwallpaper.setOnClickListener(this);
		// bt_setwallpaper.setClickable(false);
		rb_collect = (CheckBox) findViewById(R.id.rb_collect);
		rb_collect.setOnCheckedChangeListener(this);
		// rb_collect.setSelected(isCollect(mRowImage));
		rb_collect.setChecked(isCollect);
		parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);
		parent_layout.setOnLongClickListener(this);
		// parent_layout.setClickable(false);
	}

	private void initData() {
		mRowImage = (RowImage) getIntent().getExtras().get(
				XiaoQingXinFragment.ROWIMAGE);
		rowImageDao = new DaoHelper<RowImage>(this, RowImage.class);
		sharePic = "image_" + System.currentTimeMillis();
		isCollect = isCollect(mRowImage);
		sp = new MySharePreference(this);
	}

	private void addADS() {
		// 实例化LayoutParams(重要)
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT; // 这里示例为右下角
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		this.addContentView(adView, layoutParams);

		// 监听广告条接口
		adView.setAdListener(new AdViewListener() {

			@Override
			public void onSwitchedAd(AdView arg0) {
				MyLog.i("YoumiAdDemo", "广告条切换");
			}

			@Override
			public void onReceivedAd(AdView arg0) {
				MyLog.i("YoumiAdDemo", "请求广告成功");
				adcount = sp.getInt(Config.COUNT, 0);
				adcount++;
				MyLog.i("YoumiAdDemo", "请求广告成功" + adcount);
				sp.commitInt(Config.COUNT, adcount);
				if (adcount < 125)
					Toast.makeText(getApplicationContext(), "+1",
							Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailedToReceivedAd(AdView arg0) {
				MyLog.i("YoumiAdDemo", "请求广告失败");
			}
		});

		// 友盟
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);

	}

	private void attachData() {

		RequestQueue mQueue = Volley.newRequestQueue(this);
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(mBigImage,
				R.drawable.loading, R.drawable.loaded_fail);
		imageLoader.get(mRowImage.getImageUrl(), listener);
	}

	private boolean saveImage(String fileName) {
		boolean flag = false;
		// mViewAttacher.setScale(1.0f);
		mBigImage.setDrawingCacheEnabled(true);
		Bitmap bitmap = BitmapTools.drawableToBitmap2(mBigImage.getDrawable());
		if (null != bitmap)
			flag = BitmapTools.saveBitmapToSDCard(bitmap, Config.DIR_PATH,
					fileName);
		mBigImage.setDrawingCacheEnabled(false);

		return flag;
	}

	private boolean setWallpaper() {
		boolean flag = false;
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		mBigImage.setDrawingCacheEnabled(true);

		Drawable drawable = mBigImage.getDrawable();
		if (null != drawable) {
			Bitmap bitmap = BitmapTools.drawableToBitmap(drawable);
			try {
				if (null != bitmap)
					wallpaperManager.setBitmap(bitmap);
				flag = true;
			} catch (IOException e) {
				flag = false;
			}
		}
		mBigImage.setDrawingCacheEnabled(false);

		return flag;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_downLoad:
			boolean flag = saveImage(mRowImage.getTitle() + "_"
					+ System.currentTimeMillis());
			if (flag) {
				Toast.makeText(this, "保存图片成功 sdcard/grils/images",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.bt_share:
			saveImage(sharePic);
			String imgPath = Config.DIR_PATH + sharePic + ".jpg";
			shareMsg("集图分享", "集图分享", "集图分享内容", imgPath);
			break;
		case R.id.bt_setwallpaper:
			boolean a = setWallpaper();
			if (a) {
				Toast.makeText(this, "设置壁纸成功", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(this, "设置壁纸失败", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 分享功能
	 * 
	 * @param context
	 *            上下文
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public void shareMsg(String activityTitle, String msgTitle, String msgText,
			String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/jpg");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}

	int flag = 0;

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

		if (arg1) {
			rowImageDao.createOrUpdate(mRowImage);
			if (flag != 0)
				Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();

		} else {
			rowImageDao.delete(mRowImage);
			if (flag != 0) {
				Toast.makeText(this, "删除收藏！", Toast.LENGTH_SHORT).show();
			}
		}
		flag++;
	}

	private boolean isCollect(RowImage rowImage) {
		if ((rowImageDao.queryForEq("id", rowImage.getId()).size()) >= 1) {

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		String[] array1 = new String[] { "下载到手机", "分享给好友", "设置为壁纸", "加入收藏" };
		String[] array2 = new String[] { "下载到手机", "分享给好友", "设置为壁纸", "取消收藏" };
		if (!isCollect) {
			showDialog(array1);
		} else {
			showDialog(array2);
		}
		return true;
	}

	private void showDialog(String[] array) {
		new AlertDialog.Builder(this)
				.setTitle("选择你的操作")
				.setItems(array,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int n) {
								switch (n) {
								case 0:
									boolean flag = saveImage(mRowImage
											.getTitle()
											+ "_"
											+ System.currentTimeMillis());
									if (flag) {
										Toast.makeText(BigImageActivity.this,
												"保存图片成功 sdcard/grils/images",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(BigImageActivity.this,
												"保存图片失败", Toast.LENGTH_SHORT)
												.show();
									}
									break;
								case 1:
									saveImage(sharePic);
									String imgPath = Config.DIR_PATH + sharePic
											+ ".jpg";
									shareMsg("集图分享", "集图分享", "集图分享内容", imgPath);

									break;
								case 2:
									boolean a = setWallpaper();
									if (a) {
										Toast.makeText(BigImageActivity.this,
												"设置壁纸成功", Toast.LENGTH_SHORT)
												.show();

									} else {
										Toast.makeText(BigImageActivity.this,
												"设置壁纸失败", Toast.LENGTH_SHORT)
												.show();
									}
									break;
								case 3:
									if (!isCollect) {
										rowImageDao.createOrUpdate(mRowImage);
										Toast.makeText(BigImageActivity.this,
												"收藏成功！", Toast.LENGTH_SHORT)
												.show();
										isCollect = true;
									} else {
										rowImageDao.delete(mRowImage);
										Toast.makeText(BigImageActivity.this,
												"删除收藏！", Toast.LENGTH_SHORT)
												.show();
										isCollect = false;

									}
									rb_collect.setChecked(isCollect);

									break;

								default:
									break;
								}
							}
						}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			String[] array1 = new String[] { "下载到手机", "分享给好友", "设置为壁纸", "加入收藏" };
			String[] array2 = new String[] { "下载到手机", "分享给好友", "设置为壁纸", "取消收藏" };
			if (!isCollect) {
				showDialog(array1);
			} else {
				showDialog(array2);
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.gc();
			finish();
		}
		return true;
	}

	@Override
	protected void onPause() {

		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		super.onResume();
	}
}
