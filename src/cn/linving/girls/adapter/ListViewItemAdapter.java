package cn.linving.girls.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.phoneoverheard.phonne.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.linving.girls.bean.RowImage;

public class ListViewItemAdapter extends BaseAdapter {
	private Context mContext;
	private List<RowImage> rowImages;

	public ListViewItemAdapter(Context mContext, List<RowImage> rowImages) {
		super();
		this.mContext = mContext;
		this.rowImages = rowImages;
	}

	public void updateAdapter(List<RowImage> rowImages) {
		this.rowImages = rowImages;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return rowImages.size();
	}

	@Override
	public Object getItem(int position) {
		return rowImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		RowImage rowImage = rowImages.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_listview_layout, null);
			viewHolder.thumbImage = (ImageView) convertView
					.findViewById(R.id.thumbImage);
			viewHolder.img_like = (ImageView) convertView
					.findViewById(R.id.img_like);
			viewHolder.title_tag = (TextView) convertView
					.findViewById(R.id.title_tag);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.thumbImage.setImageResource(R.drawable.loading);
		}
		ImageLoader.getInstance().displayImage(rowImage.getThumbnailUrl(),
				viewHolder.thumbImage);
		viewHolder.title_tag.setText(rowImage.getTitle());

		return convertView;
	}

	static class ViewHolder {
		// 缩略图
		ImageView thumbImage;
		// 收藏
		ImageView img_like;
		// 标签
		TextView title_tag;
	}

}
