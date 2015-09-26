package com.phoneoverheard.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phoneoverheard.phone.R;
import com.phoneoverheard.util.Util;

/**
 * 下拉刷新上拉加载更多
 * 
 * 上拉加载更多，在 OnLoadMoreListener接口中获取更多列表项， 获取成功后调用onLoadMoreComplete
 * 
 * 
 * 上拉加载更多： 滑动时判断是否到了列表的最后一行，如果是则判断是否网络上是否有数据，
 * 
 * @author liulei
 */
public class DownRefreshUpMoreListView extends ListView implements OnScrollListener {

	private final static int RELEASE_To_REFRESH = 0;// 下拉过程的状态值
	private final static int PULL_To_REFRESH = 1; // 从下拉返回到不刷新的状态值
	private final static int REFRESHING = 2;// 正在刷新的状态值
	private final static int DONE = 3;
	private final static int LOADING = 4;

	/** 刷新间隔时间 */
	private final int REFRESH_SPACE_TIME = 5000;
	private long lastRefreshTime;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private LinearLayout headerView;
	private TextView lvHeaderTipsTv;
	private TextView lvHeaderLastUpdatedTv;
	private ImageView lvHeaderArrowIv;
	private ProgressBar lvHeaderProgressBar;

	// 定义头部下拉刷新的布局的高度
	private int headerContentHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int state;
	private boolean isBack;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	// 上拉加载
	private LinearLayout mLayoutLoading = null;
	private int mLastVisiblePosition;
	private Boolean mIsEnd = false;
	public static int MAX_ITEMS_PER_PAGE = 10;
	private int mPageIndex = 2;
	private OnLoadMoreListener mLoadMoreListener;

	// 正在下载
	private boolean mIsLoadRunning = false;
	private boolean isRefreshing;

	public DownRefreshUpMoreListView(Context context) {
		super(context);
		if (isInEditMode()) {
			return;
		}
		init(context);
	}

	public DownRefreshUpMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		init(context);
	}

	public DownRefreshUpMoreListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) {
			return;
		}
		init(context);
	}

	private void init(Context context) {

		isRefreshing = false;

		setCacheColorHint(android.R.color.transparent);
		inflater = LayoutInflater.from(context);
		headerView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);
		lvHeaderTipsTv = (TextView) headerView.findViewById(R.id.lvHeaderTipsTv);
		lvHeaderLastUpdatedTv = (TextView) headerView.findViewById(R.id.lvHeaderLastUpdatedTv);

		lvHeaderArrowIv = (ImageView) headerView.findViewById(R.id.lvHeaderArrowIv);
		// 设置下拉刷新图标的最小高度和宽度
		lvHeaderArrowIv.setMinimumWidth(70);
		lvHeaderArrowIv.setMinimumHeight(50);

		lvHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.lvHeaderProgressBar);
		measureView(headerView);
		headerContentHeight = headerView.getMeasuredHeight();
		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
		// 重绘一下
		headerView.invalidate();
		// 将下拉刷新的布局加入ListView的顶部
		addHeaderView(headerView, null, false);
		// 设置滚动监听事件
		setOnScrollListener(this);

		// 设置旋转动画事件
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		state = DONE;
		// 是否正在刷新
		isRefreshable = false;
		mIsLoadRunning = false;

		// 上拉加载更多
		mLayoutLoading = (LinearLayout) inflater.inflate(R.layout.footview_pulltorefresh, null);
		addFooterView(mLayoutLoading);
		mLayoutLoading.setVisibility(View.GONE);
	}

	public void hideFooterView() {

		this.removeFooterView(mLayoutLoading);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:

			// if (mIsEnd) {
			// this.removeFooterView(mLayoutLoading);
			// }

			Util.printInfor("DownRefreshUpMoreListView", "mLastVisiblePosition " + mLastVisiblePosition
					+ "view.getCount() " + view.getCount() + "mIsEnd" + mIsEnd);

			// 总条数小于10条或者已经加载了所有条目
			if (MAX_ITEMS_PER_PAGE > (null == mLoadMoreListener ? 0 : mLoadMoreListener.getListSize())) {
				Util.printInfor("DownRefreshUpMoreListView", "总条数小于10条");
				mIsEnd = true;
				break;
			}

			// 已经到最底部了
			if ((mLastVisiblePosition == view.getCount() - 1) && (!mIsEnd)) {

				if (null != mLoadMoreListener) {

					if (!mIsLoadRunning) {
						mIsLoadRunning = true;
						mLoadMoreListener.onLoadMore(mPageIndex++);
					}
				}
			}

			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}

		if (totalItemCount <= 0) {
			return;
		}

		mLastVisiblePosition = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isRecored) {
					isRecored = true;
					startY = (int) ev.getY();// 手指按下时记录当前位置
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onLvRefresh();
					}
				}
				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored) {
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headerContentHeight)// 由松开刷新状态转变到下拉刷新状态
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {// 由松开刷新状态转变到done状态
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headerContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headerView.setPadding(0, -1 * headerContentHeight + (tempY - startY) / RATIO, 0, 0);

					}
					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO - headerContentHeight, 0, 0);
					}

				}
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderTipsTv.setVisibility(View.VISIBLE);
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

			lvHeaderArrowIv.clearAnimation();// 清除动画
			lvHeaderArrowIv.startAnimation(animation);// 开始动画效果

			lvHeaderTipsTv.setText("松开刷新");
			break;
		case PULL_To_REFRESH:
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderTipsTv.setVisibility(View.VISIBLE);
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				lvHeaderArrowIv.clearAnimation();
				lvHeaderArrowIv.startAnimation(reverseAnimation);

				lvHeaderTipsTv.setText("下拉刷新");
			} else {
				lvHeaderTipsTv.setText("下拉刷新");
			}
			break;

		case REFRESHING:

			headerView.setPadding(0, 0, 0, 0);

			lvHeaderProgressBar.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.GONE);
			lvHeaderTipsTv.setText("正在刷新...");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headerView.setPadding(0, -1 * headerContentHeight, 0, 0);

			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setImageResource(R.drawable.arrows_down);
			lvHeaderTipsTv.setText("下拉刷新");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	/** 下拉刷新监听 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/** 刷新完成的回调 */
	public void onRefreshComplete() {

		if (!isRefreshing) {
			return;
		}

		state = DONE;
		lvHeaderLastUpdatedTv.setText("最近更新:" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date()));
		changeHeaderViewByState();
	}

	private void onLvRefresh() {
		if (refreshListener != null) {

			isRefreshing = true;

			if (System.currentTimeMillis() - lastRefreshTime < REFRESH_SPACE_TIME) {
				// 刷新时间小于一定间隔不刷新
				onRefreshComplete();
			} else {

				lastRefreshTime = System.currentTimeMillis();
				refreshListener.onRefresh();
			}

		}

		mPageIndex = 2;
		mIsEnd = false;
	}

	public void setAdapter(ListAdapter adapter) {
		mPageIndex = 2;
		mIsEnd = false;
		lvHeaderLastUpdatedTv.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

	/*** 上拉加载更多 */
	public interface OnLoadMoreListener {
		/** 获取更多列表项 */
		public void onLoadMore(int pageIndex);

		/** 获取当前数据 */
		public int getListSize();
	}

	/**
	 * 加载更多完成时的回调
	 * 
	 * @param isEnd
	 *            是否已经加载了所有列表项
	 */
	public void onLoadMoreComplete(boolean isEnd) {

		mIsLoadRunning = false;
		mIsEnd = isEnd;

		if (isEnd) {
			mLayoutLoading.setVisibility(View.GONE);

			mPageIndex = 2;
		} else {
			mLayoutLoading.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置上啦加载更多的毁掉方法
	 * 
	 * @param listener
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener listener) {
		mLoadMoreListener = listener;
	}

	/**
	 * 正在执行的是刷新还是加载更多
	 * 
	 * @author liulei
	 * @date 2015-1-3
	 * @return true 加载更多 false 刷新
	 */
	public boolean isLoadingMore() {

		return mIsLoadRunning;
	}

}
