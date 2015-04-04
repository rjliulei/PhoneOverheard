package cn.linving.girls.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.linving.girls.Config;
import cn.linving.girls.MyApplication;
import com.phoneoverheard.phonne.R;
import com.phoneoverheard.view.waterfalllistview.WaterFallListView;
import com.phoneoverheard.view.waterfalllistview.WaterFallListView.IXListViewListener;
import com.phoneoverheard.view.waterfalllistview.internal.PLA_AdapterView;
import com.phoneoverheard.view.waterfalllistview.internal.PLA_AdapterView.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;

import cn.linving.girls.activity.BigImageActivity;
import cn.linving.girls.activity.HomeActivity;
import cn.linving.girls.activity.SettingActivity;
import cn.linving.girls.adapter.ListViewItemAdapter;
import cn.linving.girls.bean.ColumnImages;
import cn.linving.girls.bean.Params;
import cn.linving.girls.bean.RowImage;
import cn.linving.girls.http.HttpGetClient;
import cn.linving.girls.http.HttpResponseCallBack;
import cn.linving.girls.tools.CacheTools;
import cn.linving.girls.tools.MyLog;

import com.alibaba.fastjson.JSON;

public class BaseFragment extends Fragment implements OnClickListener,
		IXListViewListener, OnItemClickListener {
	private String TAG = "全部";
	public static final String ROWIMAGE = "rowImage";
	private boolean CacheFlag = true;
	public static final int RequestSuccess = 100;
	public static final int RequestFail = -100;
	public static final int RefreshSuccess = 101;
	public static final int RefreshFail = -101;
	public static final int LoadmoreSuccess = 102;
	public static final int LoadmoreFail = -102;
	public static final int Fail_Permission = -103; // 网络权限
	public static final int CacheSuccess = 104;
	public static final int CacheFail = -104;
	public static final int REVIEW = 105;
	public int position = 0;
	protected boolean isCacheSuccess = false;

	// view
	private View currentView = null;
	private WaterFallListView myListview;
	private TextView top_bar_title;
	private ImageButton m_toggle, m_setting;
	// data
	private HttpGetClient mHttpClient;
	private Params mParams;
	private int pn = 0;
	private static final int rn = 15;
	private Context mContext;
	private ListViewItemAdapter mAdapter;
	private List<RowImage> rowImages = new ArrayList<RowImage>();
	//
	private LinearLayout loading_layout, loading_fail_layout;
	private Button loading_fail_bt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (currentView == null) {
			currentView = inflater.inflate(R.layout.fragment_base_layout,
					container, false);
		} else {
			ViewGroup parent = (ViewGroup) currentView.getParent();
			if (parent != null) {
				parent.removeView(currentView);
			}
		}
		if (CacheFlag)
			initDatas();

		return currentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		initViews();
		super.onViewCreated(view, savedInstanceState);
	}

	public BaseFragment(String tag) {
		this.TAG = tag;
	}

	public BaseFragment() {
		super();
	}

	/**
	 * 
	 * 设置tag值
	 * 
	 * @author liulei
	 * @date 2015-3-28
	 * @param tag
	 *            void
	 */
	protected void setTag(String tag) {

		if (!TAG.equals(tag)) {
			this.TAG = tag;
			CacheFlag = true;
		}
	}

	private void initViews() {
		myListview = (WaterFallListView) currentView
				.findViewById(R.id.myListview);
		myListview.setPullLoadEnable(true);
		myListview.setPullRefreshEnable(true);
		m_toggle = (ImageButton) currentView.findViewById(R.id.m_toggle);
		m_toggle.setOnClickListener(this);
		m_setting = (ImageButton) currentView.findViewById(R.id.m_setting);
		m_setting.setOnClickListener(this);
		top_bar_title = (TextView) currentView.findViewById(R.id.top_bar_title);
		top_bar_title.setText(TAG);
		mAdapter = new ListViewItemAdapter(mContext, rowImages);
		myListview.setAdapter(mAdapter);
		myListview.setXListViewListener(this);
		myListview.setOnItemClickListener(this);
		//
		loading_layout = (LinearLayout) currentView
				.findViewById(R.id.loading_layout);
		loading_fail_layout = (LinearLayout) currentView
				.findViewById(R.id.loading_fail_layout);
		loading_layout.setVisibility(View.VISIBLE);
		loading_fail_layout.setVisibility(View.GONE);
		myListview.setVisibility(View.GONE);
		loading_fail_bt = (Button) currentView
				.findViewById(R.id.loading_fail_bt);
		loading_fail_bt.setOnClickListener(this);
	}

	private void initDatas() {
		mHttpClient = HttpGetClient.getInstance();
		mContext = getActivity();
		mParams = new Params();
		mParams.setCol(Config.APP_COL);
		mParams.setTag(TAG);
		getData(pn + "", rn + "", httpCallBack);
	}

	/**
	 * 
	 * @param pn
	 *            起始位置
	 * @param rn
	 *            条数
	 */
	private void getData(String pn, String rn, HttpResponseCallBack httpCallBack) {
		mParams.setPn(pn);
		mParams.setRn(rn);
		mHttpClient.asyHttpGetRequest(mParams.toString(), httpCallBack);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.arg1) {
			case RequestSuccess:
				loading_layout.setVisibility(View.GONE);
				myListview.setVisibility(View.VISIBLE);
				ColumnImages reqColImages = (ColumnImages) msg.obj;
				rowImages = reqColImages.getImgs().subList(0,
						reqColImages.getImgs().size() - 1);
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				mAdapter.updateAdapter(rowImages);
				position = rowImages.size();
				break;

			case RequestFail:

				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				position = rowImages.size();

				Toast.makeText(mContext, "拉取数据失败！", Toast.LENGTH_SHORT).show();
				break;

			case RefreshSuccess:
				loading_layout.setVisibility(View.GONE);
				loading_fail_layout.setVisibility(View.GONE);
				myListview.setVisibility(View.VISIBLE);
				myListview.stopRefresh();
				ColumnImages refColImages = (ColumnImages) msg.obj;
				rowImages = refColImages.getImgs().subList(0,
						refColImages.getImgs().size() - 1);
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				mAdapter.updateAdapter(rowImages);
				position = rowImages.size();

				break;

			case RefreshFail:
				if (isCacheSuccess) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}

				Toast.makeText(mContext, "刷新数据失败！", Toast.LENGTH_SHORT).show();
				myListview.stopRefresh();
				position = rowImages.size();

				break;

			case LoadmoreSuccess:

				myListview.stopLoadMore();
				ColumnImages loaColImages = (ColumnImages) msg.obj;
				rowImages.addAll(loaColImages.getImgs().subList(0,
						loaColImages.getImgs().size() - 1));
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				mAdapter.updateAdapter(rowImages);
				position = rowImages.size();

				break;

			case LoadmoreFail:
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				myListview.stopLoadMore();
				Toast.makeText(mContext, "加载更多失败！", Toast.LENGTH_SHORT).show();
				position = rowImages.size();

				break;

			case Fail_Permission:
				showSetNetWorkNotifiy();
				myListview.stopRefresh();
				break;
			case CacheSuccess:
				ColumnImages reqColImages2 = (ColumnImages) msg.obj;
				rowImages = reqColImages2.getImgs().subList(0,
						reqColImages2.getImgs().size() - 1);
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				mAdapter.updateAdapter(rowImages);
				position = rowImages.size();

				break;
			case CacheFail:
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				position = rowImages.size();

				break;
			case REVIEW:
				if (rowImages.size() > 0) {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.GONE);
					myListview.setVisibility(View.VISIBLE);
				} else {
					loading_layout.setVisibility(View.GONE);
					loading_fail_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				}
				position = rowImages.size();

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	HttpResponseCallBack httpCallBack = new HttpResponseCallBack() {

		@Override
		public void onSuccess(String url, String result) {
			ColumnImages colImages = JSON.parseObject(result,
					ColumnImages.class);
			pn = colImages.getStartIndex();
			Message msg = new Message();
			msg.arg1 = RequestSuccess;
			msg.obj = colImages;
			mHandler.sendMessage(msg);
			CacheTools.saveHttpCache(MyApplication.CACHE_DIR, TAG, colImages);
		}

		@Override
		public void onFailure(int httpResponseCode, int errCode, String err) {

			MyLog.i(TAG, httpResponseCode + "");
			MyLog.i(TAG, err);
			Message msg = new Message();

			if (errCode == HttpGetClient.Con_Permission) {
				msg.arg1 = Fail_Permission;
			} else {
				msg.arg1 = RequestFail;

			}
			mHandler.sendMessage(msg);
		}
	};

	// 刷新 回调
	HttpResponseCallBack refreshCallback = new HttpResponseCallBack() {

		@Override
		public void onSuccess(String url, String result) {
			ColumnImages colImages = JSON.parseObject(result,
					ColumnImages.class);
			pn = colImages.getStartIndex();
			Message msg = new Message();
			msg.arg1 = RefreshSuccess;
			msg.obj = colImages;
			mHandler.sendMessage(msg);

		}

		@Override
		public void onFailure(int httpResponseCode, int errCode, String err) {
			MyLog.i(TAG, httpResponseCode + "");
			MyLog.i(TAG, err);
			Message msg = new Message();
			if (errCode == HttpGetClient.Con_Permission) {
				msg.arg1 = Fail_Permission;
			} else {
				msg.arg1 = RequestFail;
			}
			mHandler.sendMessage(msg);

		}
	};

	HttpResponseCallBack loadMoreCallback = new HttpResponseCallBack() {

		@Override
		public void onSuccess(String url, String result) {
			ColumnImages colImages = JSON.parseObject(result,
					ColumnImages.class);
			pn = colImages.getStartIndex();
			Message msg = new Message();
			msg.arg1 = LoadmoreSuccess;
			msg.obj = colImages;
			mHandler.sendMessage(msg);

		}

		@Override
		public void onFailure(int httpResponseCode, int errCode, String err) {
			MyLog.i(TAG, httpResponseCode + "");
			MyLog.i(TAG, err);
			Message msg = new Message();
			if (errCode == HttpGetClient.Con_Permission) {
				msg.arg1 = Fail_Permission;
			} else {
				msg.arg1 = RequestFail;
			}
			mHandler.sendMessage(msg);

		}
	};

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onResume() {
		if (CacheFlag) {
			ColumnImages colImages = (ColumnImages) CacheTools.readHttpCache(
					MyApplication.CACHE_DIR, TAG);
			if (null != colImages && colImages.getImgs().size() > 0) {
				isCacheSuccess = true;
				Message msg = new Message();
				msg.arg1 = CacheSuccess;
				msg.obj = colImages;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.arg1 = CacheFail;
				msg.obj = colImages;
				mHandler.sendMessage(msg);
				isCacheSuccess = false;
			}

			CacheFlag = false;
		} else {
			Message msg = new Message();
			msg.arg1 = REVIEW;
			mHandler.sendMessage(msg);
		}

		if (rowImages.size() > 30) {
			myListview.setSelection(position);
		}

		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.m_toggle:
			((HomeActivity) getActivity()).getSlidingPaneLayout().openPane();
			break;
		case R.id.loading_fail_bt:
			initDatas();
			break;
		case R.id.m_setting:
			Intent intent = new Intent();
			intent.setClass(getActivity(), SettingActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public void onRefresh() {
		getData(0 + "", rn + "", refreshCallback);

	}

	@Override
	public void onLoadMore() {
		pn = pn + rn;
		getData(pn + "", rn + "", loadMoreCallback);

	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position,
			long id) {
		RowImage rowImage = (RowImage) parent.getAdapter().getItem(position);
		Intent intent = new Intent();
		intent.setClass(mContext, BigImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ROWIMAGE, rowImage);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void showSetNetWorkNotifiy() {
		new AlertDialog.Builder(getActivity()).setTitle("设置网络连接")
				.setIcon(R.drawable.setting_btn_pressed)
				// 设置光标
				.setMessage("打开移动网络？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(mContext, SettingActivity.class);
						mContext.startActivity(intent);
					}
				}).setNegativeButton("取消", null).show();
	}
}
