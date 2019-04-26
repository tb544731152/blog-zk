package com.weapp.entity.wxarticle;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="t_commont_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCommontRecord {
	@Id
	private String id;
	
	//评论Id 一级评论和文章id一致
	private String cid;
	
	//文章Id
	private String aid;
	
	//创建时间
	private String createDate;
	//审核时间
	private String auditDate;
	//审核人
	private String auditPeople;
	//审核状态
	private String audiStatus;
	//评论内容
	private String content;
	//评论人信息
	private Map<String,String> author;
	
	

}
