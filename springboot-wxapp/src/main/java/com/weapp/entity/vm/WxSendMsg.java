package com.weapp.entity.vm;

public class WxSendMsg {
	
	//public final static String  templateId = "VEoNroIRZVJZHYLGPXkNZRkWDZuEhdxxwAq7svfa-ik";
	public final static String  templateId = "lqhIAicQPfk0E7NwnKvZC1OY7B1GGZX3NhjQfyghdpY";

	private String openId;
	private String title;
	private String result;
	private String date;
	private String form_id;
	private String reason;
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
}
