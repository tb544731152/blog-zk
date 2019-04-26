package com.weapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weapp.entity.auth.ApiInfo;

/**
 * api管理操作
 * @author zk
 *
 */
public interface ApiInfoRepository extends MongoRepository<ApiInfo, String> {

	ApiInfo findByName(String apiName);

}
