package com.weapp.entity.auth;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * API访问日志
 * @author zk
 *
 */
@Document(collection="t_access_logs")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'accessDate': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {
	@Id
	private String id;
	/*api名称*/	
	private String apiName;
	/*接口路径*/
	private String uri;
	/*访问时间*/	
	private Date accessDate;
	/*请求参数*/	
	private String reqParam;
	/*返回参数*/	
	private String resParam;
	/*异常内容*/	
	private String exp;

	public AccessLog(String apiName, String uri, Date accessDate, String reqParam, String resParam, String exp) {
		super();
		this.apiName = apiName;
		this.uri = uri;
		this.accessDate = accessDate;
		this.reqParam = reqParam;
		this.resParam = resParam;
		this.exp = exp;
	}
	
}
