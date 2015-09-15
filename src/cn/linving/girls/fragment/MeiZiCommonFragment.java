package cn.linving.girls.fragment;

import com.phoneoverheard.phone.R;

public class MeiZiCommonFragment extends BaseFragment {

	enum NameOfFragment {

		ALL(R.id.item_quanbu, "全部"), XIAOQINGXIN(R.id.item_xiaoqingxin, "小清新"), BIJINI(
				R.id.item_bijini, "比基尼"), CHANGFA(R.id.item_changfa, "长发"), CHANGTUI(
				R.id.item_chuangtui, "长腿"), CHEMO(R.id.item_chemo, "车模"), DUANFA(
				R.id.item_duanfa, "短发"), FEIZHULIU(R.id.item_feizhuliu, "非主流"), GAOYAYOUFAN(
				R.id.item_gaoyaoyoufan, "高雅大气很有范"), GUDIANMEINV(
				R.id.item_gudianmeinv, "古典美女"), KEAI(R.id.item_keai, "可爱"), LUOLI(
				R.id.item_luoli, "嫩萝莉"), QINGCHUN(R.id.item_qingchun, "清纯"), QIZHI(
				R.id.item_qizhi, "气质"), SHISHANG(R.id.item_shishang, "时尚"), SUYAN(
				R.id.item_suyan, "素颜"), TIANSUCHUN(R.id.item_tiansuchun, "甜素纯"), WANGLUOMEINV(
				R.id.item_wangluomeinv, "网络美女"), WEIMEI(R.id.item_weimei, "唯美"), XIAOHUA(
				R.id.item_xiaohua, "校花"), XIEZHEN(R.id.item_xiezhen, "写真"), XINGGAN(
				R.id.item_xingan, "性感美女"), YOUHUO(R.id.item_youhuo, "诱惑"), ZUQIUBAOBEI(
				R.id.item_zuqiubaobei, "足球宝贝");

		private int index;
		private String name;

		private NameOfFragment(int index, String name) {
			this.index = index;
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		/**
		 * 根据索引获取枚举值
		 * 
		 * @author liulei
		 * @date 2015-3-28
		 * @param index
		 * @return NameOfFragment
		 */
		public static NameOfFragment getEnumByIndex(int index) {

			for (NameOfFragment name : NameOfFragment.values()) {

				if (name.getIndex() == index) {
					return name;
				}
			}

			return ALL;
		}
	}

	public static String TAG = MeiZiCommonFragment.class.getSimpleName();

	public MeiZiCommonFragment(String tag) {
		super(tag);
	}

	public MeiZiCommonFragment() {
		super();
	}

	public MeiZiCommonFragment setTagName(NameOfFragment name) {

		setTag(name.getName());
		return this;
	}
	
	public MeiZiCommonFragment setTagName(String name) {

		setTag(name);
		return this;
	}

	public MeiZiCommonFragment setTagName(int index) {
		setTag(NameOfFragment.getEnumByIndex(index).getName());
		return this;
	}
}
