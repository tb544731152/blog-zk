package com.weapp.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.weapp.entity.app.TAddress;
import com.weapp.entity.app.TUser;
import com.weapp.entity.vm.AddressVM;
import com.weapp.entity.wxarticle.RankResult;
import com.weapp.redis.RedisService;
import com.weapp.repository.AddressRepository;

@Service
public class WxAddressService {
	@Autowired
	private RedisService redisService;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	public void saveAddress(TUser user,AddressVM vm){
		TAddress address = new TAddress();
		address.setAid(vm.getAid());
		address.setDistrict(vm.getDistrict());
		address.setAddress(vm.getAddress());
		address.setCity(vm.getCity());
		address.setIsDefault(vm.getIsDefault());
		address.setName(vm.getName());
		address.setOpenId(user.getOpenId());
		address.setPhone(vm.getPhone());
		address.setProvince(vm.getProvince());
		if(user.getUnionId()!=null){
			address.setUnionId(user.getUnionId());
		}
		addressRepository.save(address);
		
	}
	
	//通过期数Id查询排名结果 
	public List<TAddress> queryAwardByAid(String aid){
			Query query = new Query();
			if (aid!=null&&aid!="") {
				query.addCriteria(
					    new Criteria().andOperator(
					        Criteria.where("aid").is(aid)
					        )
					    );
	        }
			return mongoTemplate.find(query, TAddress.class);
	}
		
	
}
