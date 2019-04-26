package com.weapp.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.weapp.entity.auth.AppKey;
import com.weapp.entity.wxarticle.Article;
import com.weapp.repository.AppKeyRepository;
import com.weapp.utils.HttpUtils;


@Service
public class PublicService {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private AppKeyRepository appKeyRepository;
	
	public void refeshToken(){
		 List<AppKey> zyzsSides = appKeyRepository.findAll();
		 
		 for(AppKey side : zyzsSides){
          	String appId = side.getAppId();
          	String secret = side.getSecretKey();
			try {
				String result = HttpUtils.postJsonRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+secret,"");
				JSONObject token = JSONObject.parseObject(result);
				String openurl = HttpUtils.postJsonRequest("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+token.get("access_token")+"&type=jsapi","");
				JSONObject  jsTicket = JSONObject.parseObject(openurl);
				side.setAccessToken(token.get("access_token").toString());
				side.setJsApiTiket(jsTicket.getString("ticket").toString());
				updateAppKey(side);
			} catch (Exception e) {
			}
          	
          }
	}
	
	public void updateAppKey(AppKey appKey){
		WriteResult res = mongoTemplate.updateMulti(query(where("appId").is(appKey.getAppId())), 
				Update.update("accessToken",appKey.getAccessToken()), AppKey.class);
		System.out.println(res.getN());
	}
}
