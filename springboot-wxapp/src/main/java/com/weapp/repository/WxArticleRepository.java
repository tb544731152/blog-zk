package com.weapp.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.weapp.common.util.MongoPageable;
import com.weapp.entity.wxarticle.Article;

public interface WxArticleRepository extends MongoRepository<Article, String> {


	List<Article> findByTitleLike(String title, MongoPageable page);
	
	List<Article> findByAid(String aid);
	
	List<Article> findByOpenId(String openId);
	
	List<Article> findByOpenId(String openId, MongoPageable page);
	
	
}
