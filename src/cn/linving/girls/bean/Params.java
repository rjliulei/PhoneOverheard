package cn.linving.girls.bean;

import cn.linving.girls.tools.URLParamsTools;

/**
 * 
 * 其他参数 不懂了
 * 
 * @author ving
 *
 */
// http://image.baidu.com/data/imgs?col=美女&tag=小清新&sort=0&tag3=&pn=1&rn=60&p=channel&from=1
public class Params {
	// 总标签
	private String col;
	// 子标签
	private String tag;
	// 从那一条数据开始拿
	private String pn;
	// 拿多少条
	private String rn;

	public String getCol() {
		return col;
	}

	public String getPn() {
		return pn;
	}

	public String getRn() {
		return rn;
	}

	public String getTag() {
		return tag;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return URLParamsTools.BASE_URL + "col="
				+ URLParamsTools.EncodeString(getCol()) + "&tag="
				+ URLParamsTools.EncodeString(getTag()) + "&sort=0&tag3=&pn="
				+ getPn() + "&rn=" + getRn() + "&p=channel&from=1";
	}

}