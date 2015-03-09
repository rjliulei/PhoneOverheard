package com.phoneoverheard.database;

public class Sendimg {
	private int sendstate;
	private long imgsize;
	private String datetime;
	private String imgurl;
	private String imgname;
	public Sendimg(int sendstate, long imgsize, String datetime, String imgurl, String imgname) {
		// TODO Auto-generated constructor stub
		this.sendstate = sendstate;
		this.imgsize = imgsize;
		this.datetime = datetime;
		this.imgurl = imgurl;
		this.imgname = imgname;
	}
	public int getSendstate() {
		return sendstate;
	}
	public void setSendstate(int sendstate) {
		this.sendstate = sendstate;
	}
	public long getImgsize() {
		return imgsize;
	}
	public void setImgsize(int imgsize) {
		this.imgsize = imgsize;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getImgname() {
		return imgname;
	}
	public void setImgname(String imgname) {
		this.imgname = imgname;
	}
	
}
