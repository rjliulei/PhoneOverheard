package cn.linving.girls.activity;

import com.phoneoverheard.phone.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.linving.girls.Config;
import cn.linving.girls.MyApplication;
import cn.linving.girls.tools.MySharePreference;
import cn.linving.girls.tools.SystemTools;

public class AboutUsActivity extends BaseActivity {
	private ImageView m_toggle, m_setting;
	private TextView top_bar_title, not_version, not_sinaweibo, src_see,
			src_address;
	private MySharePreference sp;
	int c;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MyApplication.addActivity(this);
		setContentView(R.layout.activity_about_layout);
		sp = new MySharePreference(getApplicationContext());
		c = sp.getInt(Config.COUNT, 0);

		initView();
	}

	private void initView() {
		m_toggle = (ImageView) findViewById(R.id.m_toggle);
		m_setting = (ImageView) findViewById(R.id.m_setting);
		top_bar_title = (TextView) findViewById(R.id.top_bar_title);
		top_bar_title.setText("关于我们");
		not_version = (TextView) findViewById(R.id.not_version);
		not_version.setText("软件版本："
				+ SystemTools.getAppVersion(this).versionName);
		not_sinaweibo = (TextView) findViewById(R.id.not_sinaweibo);
		not_sinaweibo.setMovementMethod(LinkMovementMethod.getInstance());
		not_sinaweibo.setText(Html.fromHtml("<b>CSDN BLOG: </b>"
				+ "<a href=\"http://blog.csdn.net/rjliulei\">@rjliulei</a>"));
		// 源码
		src_see = (TextView) findViewById(R.id.src_see);
		src_see.setText("查看源码(" + c + ")");
		src_address = (TextView) findViewById(R.id.src_address);
		src_address.setVisibility(View.GONE);
		src_address.setMovementMethod(LinkMovementMethod.getInstance());

		src_address
				.setText(Html
						.fromHtml("<b>源码地址: </b>"
								+ "<a href=\"http://blog.csdn.net/rjliulei\">http://blog.csdn.net/rjliulei</a>"));

		src_see.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (c >= 125) {
					src_address.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getApplicationContext(), "请查看125张美女大图,哈哈",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		m_toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		m_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(AboutUsActivity.this, SettingActivity.class);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}
