package com.weapp;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.SocketUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.weapp.common.constant.ApiConstant;
import com.weapp.common.properties.WxAuth;
import com.weapp.entity.auth.AppKey;
import com.weapp.repository.AppKeyRepository;
import com.weapp.service.PublicService;

/**
 *
 * @author zk
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(value = "com.weapp")
@EnableConfigurationProperties(value={WxAuth.class})
public class Application implements CommandLineRunner{
	
	@Value("${wxapp.appId}")
	private String appId;
	
	@Value("${wxapp.secret}")
	private String secret;
	
	@Autowired
	private PublicService publicService;
	
	@Autowired
	private AppKeyRepository repository;

	private static ImmutableMap<String, String>errorCodeMap = null;
	static {
		try {
			Properties prop = PropertiesLoaderUtils.loadAllProperties("error_code.properties");
			errorCodeMap = Maps.fromProperties(prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		repository.deleteAll();
		String[] apiNames = new String[]{ApiConstant.GET_IMAGE,ApiConstant.UPLOAD_IMAGE
				,ApiConstant.WX_CODE,ApiConstant.WX_DECODE_USERINFO,ApiConstant.WX_CHECK_USER,
				ApiConstant.WX_SEARCH_ARTICLES,ApiConstant.WX_ARTICLE_SAVE,ApiConstant.WX_GET_ARTICLE,
				ApiConstant.WX_SEARCH_MYARTICLES,ApiConstant.WX_ARTICLES_THUMBS,
				ApiConstant.WX_GET_CONF,ApiConstant.WX_SAVE_CONF,
				ApiConstant.WX_RANK_RESULT,ApiConstant.ADD_USER_ADDRESS,
				ApiConstant.WX_UPDATE_CONF,ApiConstant.WX_EXAMINE_ARTICLE
				,ApiConstant.WX_QUERY_ARTICLES,ApiConstant.WX_GET_CONFS,
				ApiConstant.WX_QUERY_RANKS,ApiConstant.WX_CHECK_TOKEN,ApiConstant.PRIZE_CONF
		 };
		
		Map<String, Map<String,Integer>> apiMap = Maps.newHashMap();
		for(String apiName : apiNames){
			Map<String,Integer>tmpMap = new HashMap<String,Integer>();
			tmpMap.put("calltimes", 0);
			tmpMap.put("alltimes", 500000);
			apiMap.put(apiName, tmpMap);
		}
		repository.save(new AppKey(appId, secret, new Date(), new Date(), "1", false, apiMap));
		publicService.refeshToken();
	}
	@Bean
	public ImmutableMap<String, String> errorCodeMap(){
		return errorCodeMap;
	}
}
