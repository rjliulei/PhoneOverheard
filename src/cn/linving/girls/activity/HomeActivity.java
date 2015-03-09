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
import cn.linving.girls.fragment.AllmeinviFragment;
import cn.linving.girls.fragment.BijiniFragment;
import cn.linving.girls.fragment.ChangTuiFragment;
import cn.linving.girls.fragment.ChangfaFragment;
import cn.linving.girls.fragment.ChemoFragment;
import cn.linving.girls.fragment.CollectFragment;
import cn.linving.girls.fragment.DuanfaFragment;
import cn.linving.girls.fragment.FeizhuliuFragment;
import cn.linving.girls.fragment.GaoyayoufanFragment;
import cn.linving.girls.fragment.GudianmeinvFragment;
import cn.linving.girls.fragment.KeaiFragment;
import cn.linving.girls.fragment.LuoliFragment;
import cn.linving.girls.fragment.MainFragment;
import cn.linving.girls.fragment.MenuFragment;
import cn.linving.girls.fragment.QingchunFragment;
import cn.linving.girls.fragment.QizhiFragment;
import cn.linving.girls.fragment.ShishangFragment;
import cn.linving.girls.fragment.SuyanFragment;
import cn.linving.girls.fragment.TiansuchunFragment;
import cn.linving.girls.fragment.WangluomeinvFragment;
import cn.linving.girls.fragment.WeimeiFragment;
import cn.linving.girls.fragment.XiaoHuaFragment;
import cn.linving.girls.fragment.XiaoQingXinFragment;
import cn.linving.girls.fragment.XiezhenFragment;
import cn.linving.girls.fragment.XingGanFragment;
import cn.linving.girls.fragment.YouhuoFragment;
import cn.linving.girls.fragment.ZuqiubaobeiFragment;

import com.phoneoverheard.phonne.R;
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
	public XiaoQingXinFragment xiaoQingXinFragment;
	public XingGanFragment xingGanFragment;
	public ChangTuiFragment changTuiFragment;
	public XiaoHuaFragment xiaoHuaFragment;
	public QingchunFragment qingchunFragment;
	public XiezhenFragment xiezhenFragment;
	public QizhiFragment qizhiFragment;
	public ShishangFragment shishangFragment;
	public ChangfaFragment changfaFragment;
	public DuanfaFragment duanfaFragment;
	public GaoyayoufanFragment gaoyayoufanFragment;
	public TiansuchunFragment tiansuchunFragment;
	public KeaiFragment keaiFragment;
	public LuoliFragment luoliFragment;
	public WeimeiFragment weimeiFragment;
	public SuyanFragment suyanFragment;
	public YouhuoFragment youhuoFragment;
	public BijiniFragment bijiniFragment;
	public ChemoFragment chemoFragment;
	public ZuqiubaobeiFragment zuqiubaobeiFragment;
	public GudianmeinvFragment gudianmeinvFragment;
	public WangluomeinvFragment wangluomeinvFragment;
	public FeizhuliuFragment feizhuliuFragment;
	public AllmeinviFragment allmeinvFragment;
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
				menuFragment.getCurrentView().setScaleX(scale);//设置缩放的基准点
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
		xiaoQingXinFragment = new XiaoQingXinFragment(XiaoQingXinFragment.TAG);
		xingGanFragment = new XingGanFragment(XingGanFragment.TAG);
		changTuiFragment = new ChangTuiFragment(ChangTuiFragment.TAG);
		xiaoHuaFragment = new XiaoHuaFragment(XiaoHuaFragment.TAG);
		qingchunFragment = new QingchunFragment(QingchunFragment.TAG);
		xiezhenFragment = new XiezhenFragment(XiezhenFragment.TAG);
		qizhiFragment = new QizhiFragment(QizhiFragment.TAG);
		shishangFragment = new ShishangFragment(ShishangFragment.TAG);
		changfaFragment = new ChangfaFragment(ChangfaFragment.TAG);
		duanfaFragment = new DuanfaFragment(DuanfaFragment.TAG);
		gaoyayoufanFragment = new GaoyayoufanFragment(GaoyayoufanFragment.TAG);
		tiansuchunFragment = new TiansuchunFragment(TiansuchunFragment.TAG);
		keaiFragment = new KeaiFragment(KeaiFragment.TAG);
		luoliFragment = new LuoliFragment(LuoliFragment.TAG);
		weimeiFragment = new WeimeiFragment(WeimeiFragment.TAG);
		suyanFragment = new SuyanFragment(SuyanFragment.TAG);
		youhuoFragment = new YouhuoFragment(YouhuoFragment.TAG);
		bijiniFragment = new BijiniFragment(BijiniFragment.TAG);
		chemoFragment = new ChemoFragment(ChemoFragment.TAG);
		zuqiubaobeiFragment = new ZuqiubaobeiFragment(ZuqiubaobeiFragment.TAG);
		gudianmeinvFragment = new GudianmeinvFragment(GudianmeinvFragment.TAG);
		wangluomeinvFragment = new WangluomeinvFragment(
				WangluomeinvFragment.TAG);
		feizhuliuFragment = new FeizhuliuFragment(FeizhuliuFragment.TAG);
		allmeinvFragment = new AllmeinviFragment(AllmeinviFragment.TAG);
		collectFragment = new CollectFragment();

		//
		fragmentMap.put(MainFragment.TAG, mainFragment);
		fragmentMap.put(XiaoQingXinFragment.TAG, xiaoQingXinFragment);
		fragmentMap.put(XingGanFragment.TAG, xingGanFragment);
		fragmentMap.put(ChangTuiFragment.TAG, changTuiFragment);
		fragmentMap.put(XiaoHuaFragment.TAG, xiaoHuaFragment);
		fragmentMap.put(QingchunFragment.TAG, qingchunFragment);
		fragmentMap.put(XiezhenFragment.TAG, xiezhenFragment);
		fragmentMap.put(QizhiFragment.TAG, qizhiFragment);
		fragmentMap.put(ShishangFragment.TAG, shishangFragment);
		fragmentMap.put(ChangfaFragment.TAG, changfaFragment);
		fragmentMap.put(DuanfaFragment.TAG, duanfaFragment);
		fragmentMap.put(GaoyayoufanFragment.TAG, gaoyayoufanFragment);
		fragmentMap.put(TiansuchunFragment.TAG, tiansuchunFragment);
		fragmentMap.put(KeaiFragment.TAG, keaiFragment);
		fragmentMap.put(LuoliFragment.TAG, luoliFragment);
		fragmentMap.put(WeimeiFragment.TAG, weimeiFragment);
		fragmentMap.put(SuyanFragment.TAG, suyanFragment);
		fragmentMap.put(YouhuoFragment.TAG, youhuoFragment);
		fragmentMap.put(BijiniFragment.TAG, bijiniFragment);
		fragmentMap.put(ChemoFragment.TAG, chemoFragment);
		fragmentMap.put(ZuqiubaobeiFragment.TAG, zuqiubaobeiFragment);
		fragmentMap.put(GudianmeinvFragment.TAG, gudianmeinvFragment);
		fragmentMap.put(WangluomeinvFragment.TAG, wangluomeinvFragment);
		fragmentMap.put(FeizhuliuFragment.TAG, feizhuliuFragment);
		fragmentMap.put(AllmeinviFragment.TAG, allmeinvFragment);
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