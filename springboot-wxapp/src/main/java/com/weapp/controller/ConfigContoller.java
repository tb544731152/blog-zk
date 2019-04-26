package com.weapp.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.WriteResult;
import com.weapp.common.annotation.Api;
import com.weapp.common.constant.ApiConstant;
import com.weapp.entity.app.TUser;
import com.weapp.entity.conf.TCreativeCompetition;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.result.Result;
import com.weapp.entity.vm.ArticleVM;
import com.weapp.redis.SessionKey;
import com.weapp.service.ConfigService;
/***
 * 活动配置
 * @author dsk_zyzs@aliyun.com
 * 2019年3月15日
 */
@RestController
public class ConfigContoller {
	@Autowired
	private ConfigService confService;
	/**
	 * 查询所有的活动那个配置
	 * @return
	 */
	@Api(name = ApiConstant.WX_GET_CONFS)
	@RequestMapping(value = "/api/v1/getconfs", method = RequestMethod.GET, produces = "application/json")
	public Result<?>  getConfs(){
		List<TCreativeCompetition> confList = confService.getConfs();
		return Result.success(confList);
	}
	/**
	 * 修改活动配置
	 * @param stage
	 * @param tConf
	 * @return
	 */
	@Api(name=ApiConstant.WX_UPDATE_CONF)
	@RequestMapping(value = "/api/v1/confs/update/{stage}", method = RequestMethod.POST, produces = "application/json")
	public Result<?> saveArticle(@PathVariable String stage,@Valid TCreativeCompetition tConf){
		boolean res = confService.updateConf(stage,tConf);
		return  Result.success(res);
	}
}
