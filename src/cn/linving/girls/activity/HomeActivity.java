package cn.linving.girls.activity;

import java.util.HashMap;
import java.util.Map;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import cn.linving.girls.MyApplication;
import cn.linving.girls.fragment.CollectFragment;
import cn.linving.girls.fragment.MainFragment;
import cn.linving.girls.fragment.MeiZiCommonFragment;
import cn.linving.girls.fragment.MenuFragment;

import com.phoneoverheard.phone.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class HomeActivity extends BaseActivity {
	public final String TAG = "MainActivity";
	public MenuFragment menuFragment;

	private SlidingPaneLayout slidingPaneLayout;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private int maxMargin = 0;
	private FragmentTransaction transaction;
	private MainFragment mainFragment;
	private MeiZiCommonFragment meiZiCommonFragment;
	public CollectFragment collectFragment;
	// /
	public static Map<String, Fragment> fragmentMap = new HashMap<String, Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApplication.addActivity(this);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		setContentView(R.layout.activity_main);
		initData();
		initView();

		isOnlyActivity = false;
	}

	private void initView() {
		slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidingpanellayout);
		menuFragment = new MenuFragment();
		transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.slidingpane_menu, menuFragment);
		transaction.replace(R.id.slidingpane_content, mainFragment);
		transaction.commit();
		maxMargin = displayMetrics.heightPixels / 10;
		slidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				int contentMargin = (int) (slideOffset * maxMargin);

				FrameLayout.LayoutParams contentParams = mainFragment
						.getCurrentViewParams();
				contentParams.setMargins(0, contentMargin, 0, contentMargin);

				mainFragment.setCurrentViewPararms(contentParams);

				float scale = 1 - ((1 - slideOffset) * maxMargin * 3)
						/ (float) displayMetrics.heightPixels;
				menuFragment.getCurrentView().setScaleX(scale);// 设置缩放的基准点
				menuFragment.getCurrentView().setScaleY(scale);// 设置缩放的基准点
				menuFragment.getCurrentView().setPivotX(0);// 设置缩放和选择的点
				menuFragment.getCurrentView().setPivotY(
						displayMetrics.heightPixels / 2);
				menuFragment.getCurrentView().setAlpha(slideOffset);
			}

			@Override
			public void onPanelOpened(View arg0) {
			}

			@Override
			public void onPanelClosed(View arg0) {
			}
		});
	}

	private void initData() {
		mainFragment = new MainFragment();
		meiZiCommonFragment = new MeiZiCommonFragment();
		collectFragment = new CollectFragment();

		//
		fragmentMap.put(MainFragment.TAG, mainFragment);
		fragmentMap.put(MeiZiCommonFragment.TAG, meiZiCommonFragment);
		fragmentMap.put(CollectFragment.TAG, collectFragment);
		//

	}

	/**
	 * @return the slidingPaneLayout
	 */
	public SlidingPaneLayout getSlidingPaneLayout() {
		return slidingPaneLayout;
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

	// 返回键 监听
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// 如果有需要，可以点击后退关闭插播广告。
			if (!SpotManager.getInstance(this).disMiss()) {
				// 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
				return true;
			}

			if (slidingPaneLayout.isOpen()) {
				slidingPaneLayout.closePane();
			} else {
				// slidingPaneLayout.openPane();
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.push_right_in,
						R.anim.push_right_out);
				transaction.replace(R.id.slidingpane_content, mainFragment);
				transaction.commit();
			}
		}
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
		SpotManager.getInstance(this).onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		SpotManager.getInstance(this).onDestroy();
		super.onDestroy();
	}

}