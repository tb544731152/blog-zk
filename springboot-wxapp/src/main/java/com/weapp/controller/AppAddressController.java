package com.weapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.weapp.common.annotation.Api;
import com.weapp.common.constant.ApiConstant;
import com.weapp.entity.app.TUser;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.result.Result;
import com.weapp.entity.vm.AddressVM;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.service.WxAddressService;

@RestController
@RequestMapping
public class AppAddressController {
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private WxAddressService addressService;
	
	
	@Api(name=ApiConstant.ADD_USER_ADDRESS)
	@RequestMapping(value = "/api/v1/user/address/{sessionId}", method = RequestMethod.POST, produces = "application/json")
	public Result<?>  addAddress(@Valid AddressVM addressVM,@PathVariable String sessionId){
		TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
		if(user==null){
			return Result.msg(CodeMsg.NO_LOGIN);
		}
		addressService.saveAddress(user, addressVM);
		return Result.success("OK");
	}
	
	
}
