package com.weapp.entity.wxarticle;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="t_rank_result")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'openId': 1,'aid': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class RankResult {
	private String openId;
	private String aid;
	//期数
	private String periods;
	private Map<String,String> author;
	//排名
	private Long rank;
	private String prizeName;
	private String createTime;
}
