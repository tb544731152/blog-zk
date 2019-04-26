package com.weapp.entity.app;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * wxApp用户实体类
 * @author zk
 *
 */
@Document(collection="t_users")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'openId': 1,'unionId': 1, 'createDate': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class TUser {
	@Id
	private String id;
	private String nickName;
	private String gender;
	private String language;
	private String openId;
	private String unionId;
	private String province;
	private String city;
	private String avatarUrl;
	private Map<String, Object> watermark;
	private String phone;
	private String createDate;
	
}
