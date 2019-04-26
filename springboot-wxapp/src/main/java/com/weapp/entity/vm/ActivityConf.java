package com.weapp.entity.vm;

import java.util.Map;

public class ActivityConf {
	//活动名称
	private String name;
	//活动期数
	private String stage;
	//活动描述
	private String discriptions;
	//活动开始时间
	private String startdate;
	//活动结束时间
	private String enddate;
	//活动创建时间
	private String createdate;
	//背景图片
	private String logoImg;
	/**
	 * 排名方式
	   	类型  权重
		1 点赞 * 2
		2 浏览 * 1 
		3 评论 * 3
	 */
	private String rankMethod;
	//查询最终筛选排名数
	private Integer rankNums;
	//查询跳转链接
	private String clickUrl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getDiscriptions() {
		return discriptions;
	}
	public void setDiscriptions(String discriptions) {
		this.discriptions = discriptions;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getLogoImg() {
		return logoImg;
	}
	public void setLogoImg(String logoImg) {
		this.logoImg = logoImg;
	}
	public String getRankMethod() {
		return rankMethod;
	}
	public void setRankMethod(String rankMethod) {
		this.rankMethod = rankMethod;
	}
	public Integer getRankNums() {
		return rankNums;
	}
	public void setRankNums(Integer rankNums) {
		this.rankNums = rankNums;
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}

	
}
