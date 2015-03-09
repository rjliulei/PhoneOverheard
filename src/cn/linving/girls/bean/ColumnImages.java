package cn.linving.girls.bean;

import java.io.Serializable;
import java.util.List;

/***
 * ����json
 * 
 * @author ving
 *
 */
public class ColumnImages implements Serializable{
	private String col;
	private String tag;
	private String tag3;
	private String sort;
	private int totalNum;
	private int startIndex;
	private int returnNumber;
	private List<RowImage> imgs;
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag3() {
		return tag3;
	}
	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getReturnNumber() {
		return returnNumber;
	}
	public void setReturnNumber(int returnNumber) {
		this.returnNumber = returnNumber;
	}
	public List<RowImage> getImgs() {
		return imgs;
	}
	public void setImgs(List<RowImage> imgs) {
		this.imgs = imgs;
	}
	@Override
	public String toString() {
		return "ColumnImages [col=" + col + ", tag=" + tag + ", tag3=" + tag3
				+ ", sort=" + sort + ", totalNum=" + totalNum + ", startIndex="
				+ startIndex + ", returnNumber=" + returnNumber + ", imgs="
				+ imgs + "]";
	}

}
