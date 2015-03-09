package com.phoneoverheard.database;

public class Smscmd {
	private String smskey;
	private String password;
	private String telnumber;
	public Smscmd(String smskey, String password, String telnumber) {
		// TODO Auto-generated constructor stub
		this.smskey = smskey;
		this.password = password;
		this.telnumber = telnumber;
	}

	public String getSmskey() {
		return smskey;
	}
	public void setSmskey(String smskey) {
		this.smskey = smskey;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTelnumber() {
		return telnumber;
	}
	public void setTelnumber(String telnumber) {
		this.telnumber = telnumber;
	}
	
}
