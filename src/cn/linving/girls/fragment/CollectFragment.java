package cn.linving.girls.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.phoneoverheard.phonne.R;
import com.phoneoverheard.view.waterfalllistview.WaterFallListView;
import com.phoneoverheard.view.waterfalllistview.WaterFallListView.IXListViewListener;
import com.phoneoverheard.view.waterfalllistview.internal.PLA_AdapterView;
import com.phoneoverheard.view.waterfalllistview.internal.PLA_AdapterView.OnItemClickListener;
import com.phoneoverheard.view.waterfalllistview.internal.PLA_AdapterView.OnItemLongClickListener;

import cn.linving.girls.activity.BigImageActivity;
import cn.linving.girls.activity.HomeActivity;
import cn.linving.girls.adapter.ListViewItemAdapter;
import cn.linving.girls.bean.RowImage;
import cn.linving.girls.db.DaoHelper;


/**
 * 收藏 界面
 * 
 * @author ving
 *
 */
public class CollectFragment extends Fragment implements OnClickListener,
		IXListViewListener, OnItemClickListener, OnItemLongClickListener {
	public static String TAG = "CollectFragment";
	private int FLAG = 1;
	private int FAIL = -1;

	public static final String ROWIMAGE = "rowImage";
	// view
	private View currentView = null;
	private WaterFallListView myListview;
	private TextView top_bar_title;
	private ImageButton m_toggle;
	private ListViewItemAdapter mAdapter;
	private List<RowImage> rowImages = new ArrayList<RowImage>();
	//
	private DaoHelper<RowImage> rowImageDao;
	private Context mContext;
	private LinearLayout notifiy_layout;
	private Button notifiy_bt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (currentView == null) {
			currentView = inflater.inflate(R.layout.fragment_collect_layout,
					container, false);
		} else {
			ViewGroup parent = (ViewGroup) currentView.getParent();
			if (parent != null) {
				parent.removeView(currentView);
			}
		}
		initDatas();
		return currentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		initViews();
		super.onViewCreated(view, savedInstanceState);
	}

	private void initViews() {

		myListview = (WaterFallListView) currentView
				.findViewById(R.id.myListview);
		myListview.setPullLoadEnable(false);
		myListview.setPullRefreshEnable(true);
		m_toggle = (ImageButton) currentView.findViewById(R.id.m_toggle);
		m_toggle.setOnClickListener(this);
		top_bar_title = (TextView) currentView.findViewById(R.id.top_bar_title);
		top_bar_title.setText("我的收藏");
		mAdapter = new ListViewItemAdapter(mContext, rowImages);
		myListview.setAdapter(mAdapter);
		myListview.setXListViewListener(this);
		myListview.setOnItemClickListener(this);
		myListview.setOnItemLongClickListener(this);
		myListview.setVisibility(View.VISIBLE);
		//
		notifiy_layout = (LinearLayout) currentView
				.findViewById(R.id.notifiy_layout);
		notifiy_layout.setVisibility(View.GONE);
		notifiy_bt = (Button) currentView.findViewById(R.id.notifiy_bt);
		notifiy_bt.setOnClickListener(this);
	}

	private void initDatas() {
		mContext = getActivity();
		rowImageDao = new DaoHelper<RowImage>(mContext, RowImage.class);
		loadDatas();
	}

	private void loadDatas() {
		new Thread(runable).start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == FLAG) {
				myListview.stopRefresh();
				rowImages = (List<RowImage>) msg.obj;
				if (rowImages.size() == 0) {
					notifiy_layout.setVisibility(View.VISIBLE);
					myListview.setVisibility(View.GONE);
				} else {
					mAdapter.updateAdapter(rowImages);
				}
			} else if (msg.arg1 == FAIL) {
				notifiy_layout.setVisibility(View.VISIBLE);
				myListview.setVisibility(View.GONE);

			}
			super.handleMessage(msg);
		}

	};

	private Runnable runable = new Runnable() {

		@Override
		public void run() {
			List<RowImage> tmpList = rowImageDao.queryForAll();
			if (null != tmpList) {
				Message msg = new Message();
				msg.arg1 = FLAG;
				msg.obj = tmpList;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.arg1 = FAIL;
				mHandler.sendMessage(msg);
			}
		}
	};

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

	@Override
	public boolean onItemLongClick(PLA_AdapterView<?> parent, View view,
			int position, long id) {
		// TODO 取消收藏
		return false;
	}

	@Override
	public void onRefresh() {
		loadDatas();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.m_toggle:
			((HomeActivity) getActivity()).getSlidingPaneLayout().openPane();
			break;
		case R.id.notifiy_bt:
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out);
			((HomeActivity) getActivity()).getSlidingPaneLayout().closePane();
			transaction.replace(R.id.slidingpane_content,
					HomeActivity.fragmentMap.get(AllmeinviFragment.TAG));
			transaction.commit();
			break;
		default:
			break;
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		if (null != rowImageDao) {
			if (null != notifiy_layout && null != myListview) {
				notifiy_layout.setVisibility(View.GONE);
				myListview.setVisibility(View.VISIBLE);
			}

			loadDatas();
		}

		super.onResume();
	}

}
