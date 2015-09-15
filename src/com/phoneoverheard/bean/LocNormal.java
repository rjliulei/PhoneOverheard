package com.phoneoverheard.bean;

import com.j256.ormlite.field.DatabaseField;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 常态定位表及交互类
 * 
 * @author liulei
 * @date 2015-9-15 上午11:46:59
 * @version 1.0
 */
public class LocNormal extends BmobObject {

	/** 
	 * 
	*/
	private static final long serialVersionUID = 1L;

	private BmobGeoPoint point;
	private String userId;

	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private double lng;
	@DatabaseField
	private double lat;
	@DatabaseField
	private String time;
	@DatabaseField
	private int state;

	public BmobGeoPoint getPoint() {
		return point;
	}

	public void setPoint(BmobGeoPoint point) {
		this.point = point;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {

		this.lng = lng;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
