package com.weapp.entity.result;


public class CodeMsg {
	
	private int code;
	private String msg;
	private String token;
	public static CodeMsg GET_SESSION_SUCCESS = new CodeMsg(1000, "获取session成功","%s");
	public static CodeMsg GET_SESSION_NOSUB = new CodeMsg(1001, "获取session成功,用户未关注，需要解密获取","%s");
	public static CodeMsg GET_SESSION_FAIL = new CodeMsg(1002, "获取session失败","%s");
	public static CodeMsg CHECK_SUCCESS = new CodeMsg(2000, "校验成功","%s");
	public static CodeMsg FAIL_NOSESSION = new CodeMsg(2001, "校验失败-NOSession","%s");
	public static CodeMsg CHECK_FAIL = new CodeMsg(2002, "校验失败","%s");
	public static CodeMsg DECODE_SAVE_SUCCESS = new CodeMsg(3000, "解析并保存成功","%s");
	public static CodeMsg DECODE_SAVE_FAIL = new CodeMsg(3001, "解析并保存失败","%s");
	public static CodeMsg SUCCESS = new CodeMsg(4000, "成功");
	public static CodeMsg NO_LOGIN = new CodeMsg(4001, "未登录");
	public static CodeMsg NO_Article = new CodeMsg(5001, "无此文章");
	
	public static CodeMsg UPLOAD_FILE_SUCCESS = new CodeMsg(5000, "上传文件成功","%s");
	public static CodeMsg UPLOAD_FILE_FAIL = new CodeMsg(5001, "文件为空、保存失败");
	
	public static CodeMsg CLICK_Thumbs_FAIL = new CodeMsg(6001, "已经点过赞");
	public static CodeMsg CLICK_Thumbs_DAY_LIMIT = new CodeMsg(6002, "每天最多点赞3次");
	
	public static CodeMsg ACTIVITY_STARTING = new CodeMsg(7001, "活动正在进行，活动结束才可查看排名！");
	
	
	private CodeMsg( int code,String msg,String token) {
		this.code = code;
		this.msg = msg;
		this.token = token;
	}
	
	private CodeMsg( int code,String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}
	
	public CodeMsg fillArgsToken(Object... args) {
		int code = this.code;
		String message = this.msg;
		String token = String.format(this.token, args);
		return new CodeMsg(code, message,token);
	}
	
	public CodeMsg fillMsg(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		String token = this.token;
		return new CodeMsg(code, message,token);
	}
	
	
	public CodeMsg fillArgsMsgAndToken(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args[0]);
		String token = String.format(this.token, args[1]);
		return new CodeMsg(code, message,token);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + ", token=" + token
				+ "]";
	}

	
	
	
}
