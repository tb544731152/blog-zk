package com.weapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.weapp.entity.wxarticle.TThumbsRecord;
/**
 * 点赞记录表
 * @author zk
 *
 */
public interface ThubsRepository extends MongoRepository<TThumbsRecord, String> {
}
