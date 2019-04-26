package com.weapp.entity.conf;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 结果记录实体类
 * @author zk
 *
 */
@Document(collection="t_rank_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TRankResult {
	@Id
	private String id;
	

}
