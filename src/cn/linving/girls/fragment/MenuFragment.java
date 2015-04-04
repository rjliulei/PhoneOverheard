package cn.linving.girls.fragment;


import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.phoneoverheard.phonne.R;
import cn.linving.girls.activity.HomeActivity;
import cn.linving.girls.activity.SettingActivity;
import cn.linving.girls.tools.MyLog;

public class MenuFragment extends Fragment implements OnClickListener {
	private View currentView = null;
	private Button bt_xiaoqingxin, bt_collect, showRecommendGameWall,
			showRecommendAppWall, bt_shouye, bt_systemSetting;

	private Button bt_exit, bt_setting;

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		currentView = inflater.inflate(R.layout.fragment_menu_layout,
				container, false);
		context = getActivity();
		bt_xiaoqingxin = (Button) currentView.findViewById(R.id.bt_xiaoqingxin);
		bt_collect = (Button) currentView.findViewById(R.id.bt_collect);
		showRecommendGameWall = (Button) currentView
				.findViewById(R.id.showRecommendGameWall);
		showRecommendAppWall = (Button) currentView
				.findViewById(R.id.showRecommendAppWall);
		bt_shouye = (Button) currentView.findViewById(R.id.bt_shouye);
		bt_systemSetting = (Button) currentView
				.findViewById(R.id.bt_systemSetting);
		bt_systemSetting.setOnClickListener(this);
		bt_xiaoqingxin.setOnClickListener(this);
		bt_collect.setOnClickListener(this);
		showRecommendAppWall.setOnClickListener(this);
		bt_shouye.setOnClickListener(this);
		showRecommendGameWall.setOnClickListener(this);
		bt_exit = (Button) currentView.findViewById(R.id.bt_exit);
		bt_exit.setOnClickListener(this);

		bt_setting = (Button) currentView.findViewById(R.id.bt_setting);
		bt_setting.setOnClickListener(this);

		return currentView;
	}

	public View getCurrentView() {
		return currentView;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		switch (v.getId()) {
		case R.id.bt_shouye:
			((HomeActivity) getActivity()).getSlidingPaneLayout().closePane();
			transaction.replace(R.id.slidingpane_content,
					HomeActivity.fragmentMap.get(MainFragment.TAG));
			transaction.commit();
			break;
		case R.id.bt_xiaoqingxin:
			((HomeActivity) getActivity()).getSlidingPaneLayout().closePane();
			transaction.replace(R.id.slidingpane_content,
					HomeActivity.fragmentMap.get(MeiZiCommonFragment.TAG));
			transaction.commit();
			break;
		case R.id.bt_collect:
			((HomeActivity) getActivity()).getSlidingPaneLayout().closePane();
			transaction.replace(R.id.slidingpane_content,
					HomeActivity.fragmentMap.get(CollectFragment.TAG));
			transaction.commit();
			break;
		case R.id.showRecommendAppWall:
			//DiyManager.showRecommendAppWall(getActivity());
			break;
		case R.id.showRecommendGameWall:
			//DiyManager.showRecommendGameWall(getActivity());
			break;
		case R.id.bt_systemSetting:
			Intent inten = new Intent();
			inten.setClass(context, SettingActivity.class);
			startActivity(inten);
			break;

		case R.id.bt_exit:
			
			SpotManager.getInstance(context).showSpotAds(context);
			// exitiDalog();
			// 展示插播广告，可以不调用loadSpot独立使用
			SpotManager.getInstance(getActivity()).showSpotAds(getActivity(),
					new SpotDialogListener() {
						@Override
						public void onShowSuccess() {
							MyLog.i("YoumiAdDemo", "展示成功");
						}

						@Override
						public void onShowFailed() {
							MyLog.i("YoumiAdDemo", "展示失败");
							System.exit(0);
						}

						@Override
						public void onSpotClosed() {
							MyLog.i("YoumiAdDemo", "插屏关闭");
							System.exit(0);
						}

					}); // //

			break;
		case R.id.bt_setting:
			Intent intent = new Intent();
			intent.setClass(context, SettingActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	protected void exitiDalog() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认退出吗？");

		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						System.exit(0);
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}
}
