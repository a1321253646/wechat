package jackzheng.study.com.wechat.net;


import jackzheng.study.com.wechat.regular.SscBean;

public class ResponseDateRangeBean {
	public SscBean date;
	public String user;
	public long id;


	
	public SscBean getDate() {
		return date;
	}



	public void setDate(SscBean date) {
		this.date = date;
	}



	public String getUser() {
		return user;
	}



	public void setUser(String user) {
		this.user = user;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}

}
