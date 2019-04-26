package com.weapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weapp.entity.app.TAddress;
import com.weapp.entity.app.TUser;
/**
 * 地址管理管理操作
 * @author zk 
 *
 */
public interface AddressRepository extends MongoRepository<TAddress, String> {
	
}
