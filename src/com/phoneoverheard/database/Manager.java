package com.phoneoverheard.database;

public class Manager {
	private String smskey;
	private String password;
	private String telnumber;
	private String cmdsql;
	private String audiofolder;
	private String imgfolder;
	private String serverport;
	private String serveraddress;
	private String hostip;
	private String hostmac;
	private String mailsendto;
	private String mailpassword;
	private String mailfrom;
	
	public Manager(String smskey,String password,String telnumber,String cmdsql,String audiofolder,String imgfolder,String serverport,String serveraddress,String hostip,String hostmac,String mailsendto,String mailpassword,String mailfrom){
		// TODO Auto-generated constructor stub
		this.smskey = smskey;
		this.password = password;
		this.telnumber = telnumber;
		this.cmdsql = cmdsql;
		this.audiofolder = audiofolder;
		this.imgfolder = imgfolder;
		this.serverport = serverport;
		this.serveraddress = serveraddress;
		this.hostip = hostip;
		this.hostmac = hostmac;
		this.mailsendto = mailsendto;
		this.mailpassword = mailpassword;
		this.mailfrom = mailfrom;
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

	public String getCmdsql() {
		return cmdsql;
	}

	public void setCmdsql(String cmdsql) {
		this.cmdsql = cmdsql;
	}

	public String getAudiofolder() {
		return audiofolder;
	}

	public void setAudiofolder(String audiofolder) {
		this.audiofolder = audiofolder;
	}

	public String getImgfolder() {
		return imgfolder;
	}

	public void setImgfolder(String imgfolder) {
		this.imgfolder = imgfolder;
	}

	public String getServerport() {
		return serverport;
	}

	public void setServerport(String serverport) {
		this.serverport = serverport;
	}

	public String getServeraddress() {
		return serveraddress;
	}

	public void setServeraddress(String serveraddress) {
		this.serveraddress = serveraddress;
	}

	public String getHostip() {
		return hostip;
	}

	public void setHostip(String hostip) {
		this.hostip = hostip;
	}

	public String getHostmac() {
		return hostmac;
	}

	public void setHostmac(String hostmac) {
		this.hostmac = hostmac;
	}

	public String getMailsendto() {
		return mailsendto;
	}

	public void setMailsendto(String mailsendto) {
		this.mailsendto = mailsendto;
	}

	public String getMailpassword() {
		return mailpassword;
	}

	public void setMailpassword(String mailpassword) {
		this.mailpassword = mailpassword;
	}

	public String getMailfrom() {
		return mailfrom;
	}

	public void setMailfrom(String mailfrom) {
		this.mailfrom = mailfrom;
	}


}
