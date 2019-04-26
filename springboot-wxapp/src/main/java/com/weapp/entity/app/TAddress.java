package com.weapp.entity.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * 用户地址信息
 * @author zk
 *
 */
@Document(collection="t_address")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'openId': 1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class TAddress {
	@Id
	private String id;
	private String openId;
	private String district;
	private String aid;
	private String unionId;
	private String name;
	private String phone;
	private String address;
	private String province;
	private String city;
	private Boolean isDefault;
	

}
