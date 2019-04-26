package com.weapp.entity.wxarticle;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="t_articles")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'openId': 1,'aid': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class Article {
	@Id
	private String id;
	
	private String form_id;
	//人物标识
	private String openId;
	//文章标识
	private String aid;
	//直接访问路径
	private String path;
	//期数
	private String periods;
	//标题
	private String title;
	//摘要
	private String digest;
	//创建时间
	private String createTime;
	//点赞
	private Integer thumbs;
	//浏览
	private Integer browers;
	//评论
	private Integer comments;
	//排名
	private Long rank;
	//分数
	private Integer score;
	
	//作者包含头像信息--作者名称
	/**
	  { 
 		"headimg" : "",
 		"nickname" : "",
 		"remark" : "" 
	  }
	*/
	private Map<String,String> author;
	
	//文章审核状态
	private String status;
	//图片
	private String imgs;
	
	private String content;
	//审核时间
	private String auditdate;
	//审核人
	private String auditpeople;
	//评论
	private String remark;
	
	private boolean isthumbs;
	
	
	
}
