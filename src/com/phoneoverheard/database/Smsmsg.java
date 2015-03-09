package com.phoneoverheard.database;

public class Smsmsg {
	private int read;
	private int type;
	private String datetime;
	private String address;
	private String body;
	public Smsmsg(int read, int type, String datetime, String address, String body) {
		// TODO Auto-generated constructor stub
		this.read = read;
		this.type = type;
		this.datetime = datetime;
		this.address = address;
		this.body = body;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
