package com.phoneoverheard.database;

public class Simmsg {
	private String tel;
	private String deviceid;
	private String imei;
	private String imsi;
	private int simstate;
	public Simmsg(String tel, String deviceid, String imei, String imsi, int simstate) {
		// TODO Auto-generated constructor stub
		this.tel = tel;
		this.deviceid = deviceid;
		this.imei = imei;
		this.imsi = imsi;
		this.simstate = simstate;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public int getSimstate() {
		return simstate;
	}
	public void setSimstate(int simstate) {
		this.simstate = simstate;
	}
	
}
