package com.phoneoverheard.phone.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.linving.girls.activity.BaseActivity;
import cn.linving.girls.db.SqliteOpenHelper;

import com.phoneoverheard.adapter.base.BaseAdapterHelper;
import com.phoneoverheard.adapter.base.QuickAdapter;
import com.phoneoverheard.bean.LocNormal;
import com.phoneoverheard.db.LocNormalUnit;
import com.phoneoverheard.phone.R;
import com.phoneoverheard.util.ToastUtil;
import com.phoneoverheard.util.Util;
import com.phoneoverheard.view.DownRefreshUpMoreListView;
import com.phoneoverheard.view.DownRefreshUpMoreListView.OnLoadMoreListener;
import com.phoneoverheard.view.DownRefreshUpMoreListView.OnRefreshListener;

/**
 * 数据库调试界面
 * 
 * @author liulei
 * @date 2015-9-17 下午12:25:19
 * @version 1.0
 */
public class AdminDbTestActivity extends BaseActivity implements OnItemClickListener, OnRefreshListener,
		OnLoadMoreListener {

	private DownRefreshUpMoreListView lv;
	// 表
	private QuickAdapter<Class<?>> adapterTables;
	// 表里的数据
	private QuickAdapter<Object> adapterContents;

	// 当前列表
	private int currentIndex;
	private Class currentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_db_test);

		isOnlyActivity = true;

		initView();
	}

	private void initView() {
		lv = (DownRefreshUpMoreListView) findViewById(R.id.lv);

		adapterTables = new QuickAdapter<Class<?>>(context, R.layout.item_list_db) {

			@Override
			protected void convert(BaseAdapterHelper helper, Class<?> item) {
				// TODO Auto-generated method stub

				String name = null;

				if (item == LocNormal.class) {
					name = "常态定位表";
				} else {
					name = item.getSimpleName();
				}
				((TextView) helper.getView(R.id.tv_content)).setText(name);
			}
		};
		lv.setAdapter(adapterTables);
		lv.setOnItemClickListener(this);
		lv.setonRefreshListener(this);
		lv.setOnLoadMoreListener(this);
		adapterTables.addAll(Arrays.asList(SqliteOpenHelper.classes));
		lv.onRefreshComplete();
		currentIndex = 0;

		adapterContents = new QuickAdapter<Object>(context, R.layout.item_list_db) {

			@Override
			protected void convert(BaseAdapterHelper helper, Object item) {
				// TODO Auto-generated method stub

				((TextView) helper.getView(R.id.tv_content)).setText(Util.printDb(item));
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		currentClass = adapterTables.getItem(position - 1);

		if (currentClass == LocNormal.class) {

			LocNormalUnit unit = new LocNormalUnit(context);

			List<LocNormal> list = unit.queryPaging(0, DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE);

			if (list != null) {
				lv.setAdapter(adapterContents);
				adapterContents.clear();
				adapterContents.addAll(new ArrayList<Object>(list));
				lv.setOnItemClickListener(null);

				if (list.size() < DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE) {
					lv.onLoadMoreComplete(true);
				}

				currentIndex = 1;
			} else {
				ToastUtil.showToastShort(context, "没有数据");
			}

			lv.onRefreshComplete();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (0 == currentIndex) {
			finish();
		} else {
			lv.setAdapter(adapterTables);
			lv.setOnItemClickListener(this);
			currentIndex = 0;
		}
	}

	@Override
	public void onLoadMore(int pageIndex) {
		// TODO Auto-generated method stub
		switch (currentIndex) {
		case 0:
			lv.onLoadMoreComplete(true);
			break;
		default:
			if (currentClass == LocNormal.class) {
				LocNormalUnit unit = new LocNormalUnit(context);

				List<LocNormal> list = unit.queryPaging(pageIndex - 1, DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE);

				if (list != null) {
					adapterContents.addAll(new ArrayList<Object>(list));
					if (list.size() < DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE) {
						lv.onLoadMoreComplete(true);
					} else {
						lv.onLoadMoreComplete(false);
					}

				} else {
					lv.onLoadMoreComplete(true);
				}

			}
			break;
		}

	}

	@Override
	public int getListSize() {
		// TODO Auto-generated method stub
		switch (currentIndex) {
		case 0:

			return adapterTables.getCount();

		default:
			return adapterContents.getCount();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		switch (currentIndex) {
		case 0:
			lv.onRefreshComplete();
			break;

		default:
			if (currentClass == LocNormal.class) {
				LocNormalUnit unit = new LocNormalUnit(context);

				List<LocNormal> list = unit.queryPaging(0, DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE);

				if (list != null) {
					adapterContents.clear();
					adapterContents.addAll(new ArrayList<Object>(list));
					if (list.size() < DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE) {
						lv.onLoadMoreComplete(true);
					}

				}

				lv.onRefreshComplete();
			}
			break;

		}

	}
}
