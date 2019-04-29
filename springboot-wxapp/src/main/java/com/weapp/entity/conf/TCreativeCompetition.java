package com.weapp.entity.conf;

import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="t_confs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCreativeCompetition {
	@Id
	private String id;
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
	
	private String prizeName;
	/**
	 * 排名方式
	   	类型  权重
		1 点赞 * 2
		2 浏览 * 1 
		3 评论 * 3
	 */
	private Map<String,Integer> rankMethod;
	
	//查询最终筛选排名数
	private Integer rankNums;
	//查询跳转链接
	private String clickUrl;
	//是否生成中奖名单
	private Boolean generate;
	
	
	
	
}
