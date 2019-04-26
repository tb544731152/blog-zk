package com.weapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.weapp.entity.app.TUser;
/**
 * appkey管理操作
 * @author zk
 *
 */
public interface UserRepository extends MongoRepository<TUser, String> {
	TUser findByOpenId(String OpenId);
}
