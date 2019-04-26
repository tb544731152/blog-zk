package com.weapp.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import com.weapp.entity.conf.TCreativeCompetition;
import com.weapp.repository.CreatetiveCompetitionRepository;

@Service
public class ConfigService {
	@Autowired
	private CreatetiveCompetitionRepository createtiveCompetitionRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	/**
	 * 查询所有活动配置
	 * @return
	 */
	public List<TCreativeCompetition> getConfs(){
		return createtiveCompetitionRepository.findAll();
	}
	/**
	 * 保存或修改配置
	 * @param tConf
	 * @return
	 */
	public boolean updateConf(String stage,TCreativeCompetition tConf){
		//条件
		Query query = new Query(Criteria.where("stage").is(stage));
		Update update = new Update();
		if (tConf==null) {
			return false;
		}else {
			//活动期数
			if (tConf.getStage()!=null && !tConf.getStage().equals("")) {
				update.set("stage", tConf.getStage());
			}
			//_class
			update.set("_class", "com.weapp.entity.conf.TCreativeCompetition");
			//活动名称
			if (tConf.getName()!=null && !tConf.getName().equals("")) {
				update.set("name", tConf.getName());
			}
			//活动描述
			if (tConf.getDiscriptions()!=null && !tConf.getDiscriptions().equals("")) {
				update.set("discriptions", tConf.getDiscriptions());
			}
			//活动开始时间
			if (tConf.getStartdate()!=null && !tConf.getStartdate().equals("")) {
				update.set("startdate", tConf.getStartdate());
			}
			//活动结束时间
			if (tConf.getEnddate()!=null && !tConf.getEnddate().equals("")) {
				update.set("enddate", tConf.getEnddate());
			}
			//背景图片
			if (tConf.getLogoImg()!=null && !tConf.getLogoImg().equals("")) {
				update.set("logoImg", tConf.getLogoImg());
			}
			//排名方式
			if (tConf.getRankMethod()!=null && tConf.getRankMethod().size()>0) {
				update.set("rankMethod", tConf.getRankMethod());
			}
			//查询最终筛选排名数
			if (tConf.getRankNums()!=null) {
				update.set("rankNums", tConf.getRankNums());
			}
			//查询跳转链接
			if (tConf.getClickUrl()!=null && !tConf.getClickUrl().equals("")) {
				update.set("clickUrl", tConf.getClickUrl());
			}
		}
		
		WriteResult result = mongoTemplate.upsert(query,update,"t_confs"); 
		return true;
	}
}
