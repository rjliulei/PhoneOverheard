package com.phoneoverheard.database;

public class Sendaudio {
	private int sendstate;
	private long audiosize;
	private String datetime;
	private String audiourl;
	private String audioname;
	public Sendaudio(int sendstate, long audiosize, String datetime, String audiourl, String audioname) {
		// TODO Auto-generated constructor stub
		this.sendstate = sendstate;
		this.audiosize = audiosize;
		this.datetime = datetime;
		this.audiourl = audiourl;
		this.audioname = audioname;
	}
	public int getSendstate() {
		return sendstate;
	}
	public void setSendstate(int sendstate) {
		this.sendstate = sendstate;
	}
	public long getAudiosize() {
		return audiosize;
	}
	public void setAudiosize(long audiosize) {
		this.audiosize = audiosize;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getAudiourl() {
		return audiourl;
	}
	public void setAudiourl(String audiourl) {
		this.audiourl = audiourl;
	}
	public String getAudioname() {
		return audioname;
	}
	public void setAudioname(String audioname) {
		this.audioname = audioname;
	}
	
}
