package com.weapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weapp.entity.app.TUser;
import com.weapp.entity.wxarticle.RankResult;
/**
 * appkey管理操作
 * @author zk
 *
 */
public interface RankResultRepository extends MongoRepository<RankResult, String> {
	
}
