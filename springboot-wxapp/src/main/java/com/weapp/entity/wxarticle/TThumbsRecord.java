package com.weapp.entity.wxarticle;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="t_thumbs_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TThumbsRecord {
	@Id
	private String id;
	
	private String aid;
	
	private String openId;
	
	private Map<String,String> author;
	
	private String createDate;

}
