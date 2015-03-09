package cn.linving.girls.view;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * 
 * @author ving
 *
 */
public class AttacherImageView extends ImageView {

	private PhotoViewAttacher mViewAttacher = null;

	public AttacherImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mViewAttacher = new PhotoViewAttacher(this);
	}

	public AttacherImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mViewAttacher = new PhotoViewAttacher(this);
	}

	public AttacherImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mViewAttacher = new PhotoViewAttacher(this);
	}

	@Override
	protected void drawableStateChanged() {
		mViewAttacher.update();
		super.drawableStateChanged();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mViewAttacher.update();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setPhotoViewAttacher(PhotoViewAttacher mViewAttacher) {
		this.mViewAttacher = mViewAttacher;
	}

	public PhotoViewAttacher getmViewAttacher() {
		return mViewAttacher;
	}

}
