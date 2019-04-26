package com.weapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.spring.web.json.Json;

import com.weapp.common.annotation.Api;
import com.weapp.common.constant.ApiConstant;
import com.weapp.common.util.MongoPageable;
import com.weapp.entity.app.TUser;
import com.weapp.entity.conf.TCreativeCompetition;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.result.Result;
import com.weapp.entity.vm.ActivityConf;
import com.weapp.entity.vm.ArticleResult;
import com.weapp.entity.vm.ArticleVM;
import com.weapp.entity.vm.QueryArticleVm;
import com.weapp.entity.wxarticle.Article;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.repository.CreatetiveCompetitionRepository;
import com.weapp.service.WxArticleService;
import com.weapp.service.WxService;
import com.weapp.utils.JsonUtil;
/**
 * 微信小程序--文章
 * @author zk
 *
 */
@RestController
public class WxArticleController{
	@Autowired
	private CreatetiveCompetitionRepository confDao;
	
	@Autowired
	private WxArticleService wxArticleService;
	
	@Autowired
	private WxService wxService;
	
	@Autowired
	private RedisService redisService;
	
	private static final Integer pageCount = 9;
	
	/**
	 * 查询文章详情
	 * @param aid 文章id
	 * @return
	 */
	@Api(name = ApiConstant.WX_GET_ARTICLE)
	@RequestMapping(value = "/api/v1/getwxarticle/{sessionId}/{aid}", method = RequestMethod.GET, produces = "application/json")
	public Result<?>  getArticle(@PathVariable String aid,@PathVariable String sessionId){
		ArticleResult res = wxArticleService.getArticle(aid, sessionId);
		return Result.success(res);
	}
	/**
	 * 查询文章前N
	 * @param id
	 * @param page
	 * @return
	 */
	@Api(name = ApiConstant.WX_SEARCH_ARTICLES)
	@RequestMapping(value = "/api/v1/wxarticles/rank/{sessionId}/{page}", method = RequestMethod.GET, produces = "application/json")
	public Result<?> getArticles(@PathVariable String sessionId,@PathVariable Integer page){
		//根据page计算页数
		Integer start = getstart(page);
		Integer end = start + pageCount;
		Map<String,List<ArticleResult>> res  = wxArticleService.getRank(page,start, end, sessionId);
		return Result.success(res);
	}
	
	public int getstart(Integer page){
		Integer start = 0;
		if(page==1){
			return start;
		}
		return (page-1)*pageCount+1;
	}
	
	@Api(name=ApiConstant.WX_ARTICLE_SAVE)
	@RequestMapping(value = "/api/v1/wxarticle/save/{sessionId}", method = RequestMethod.POST, produces = "application/json")
	public Result<?> saveArticle(@Valid ArticleVM articleVM,@PathVariable String sessionId){
		TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
		if(user==null){
			return Result.msg(CodeMsg.NO_LOGIN);
		}
		boolean res = wxArticleService.saveArticle(user,articleVM);
		return  Result.success(res);
	}
	
	
	/**
	 * 查询自己所发文章
	 * @param id
	 * @param page
	 * @return
	 */
	@Api(name = ApiConstant.WX_SEARCH_MYARTICLES)
	@RequestMapping(value = "/api/v1/wxarticles/ourself/{sessionId}/pageNo", method = RequestMethod.GET, produces = "application/json")
	public Result<?> getOurSelfArticles(@PathVariable String sessionId,@PathVariable(value="pageNo",required=false) Integer pageNo){
		MongoPageable page = new MongoPageable();
		page.setPagenumber(pageNo);
		List<Article> list = wxArticleService.getBySession(sessionId, page);
		return Result.success(list);
	}
	/**
	 * 更新排名
	 * @param sessionId 
	 * @param method
	 * @param uid
	 * @return
	 */
	@Api(name = ApiConstant.WX_ARTICLES_THUMBS)
	@RequestMapping(value = "/api/v1/wxarticles/{sessionId}/{method}/{aid}", method = RequestMethod.GET, produces = "application/json")
	public Result<?> articlesThumbs(@PathVariable String sessionId,@PathVariable String method,@PathVariable String aid){
		CodeMsg msg = wxArticleService.articlesRank(sessionId, method, aid);
		return Result.msg(msg);
	}
	
	
	/**
	 * 查询配置信息
	 * @param aid 文章id
	 * @return
	 */
	@Api(name = ApiConstant.WX_GET_CONF)
	@RequestMapping(value = "/api/v1/get/conf", method = RequestMethod.GET, produces = "application/json")
	public Result<?>  getCONF(){
		TCreativeCompetition conf = wxArticleService.getLasterConf();
		return Result.success(conf);
	}
	
	
	/**
	 * 保存配置
	 * @param
	 * @return
	 */
	@Api(name = ApiConstant.WX_SAVE_CONF)
	@RequestMapping(value = "/api/v1/save/conf", method = RequestMethod.POST, produces = "application/json")
	public Result<?>  saveCONF(@Valid ActivityConf conf){
		 TCreativeCompetition tempconf = new TCreativeCompetition();
		 tempconf.setName(conf.getName());
		 tempconf.setStage(conf.getStage());
		 tempconf.setClickUrl(conf.getClickUrl());
		 tempconf.setCreatedate(conf.getCreatedate());
		 tempconf.setDiscriptions(conf.getDiscriptions());
		 tempconf.setStartdate(conf.getStartdate());
		 tempconf.setEnddate(conf.getEnddate());
		 tempconf.setLogoImg(conf.getLogoImg());
		 tempconf.setRankMethod((Map)JsonUtil.stringToObj(conf.getRankMethod(), HashMap.class));
		 tempconf.setRankNums(conf.getRankNums());
		 tempconf.setGenerate(false);
		confDao.save(tempconf);
		return Result.success(conf);
	}
	
	/**
	 * 查询活动是排名结果
	 * @param
	 * @return
	 */
	@Api(name = ApiConstant.WX_RANK_RESULT)
	@RequestMapping(value = "/api/v1/rank/result/{sessionId}", method = RequestMethod.GET, produces = "application/json")
	public Result<?>  rankResult(@PathVariable String sessionId){
		TCreativeCompetition conf = wxArticleService.getStartConf();
		if(conf!=null){
			return Result.error(CodeMsg.ACTIVITY_STARTING);
		}
		Map<String,Object> res = wxArticleService.generateRankRessult(sessionId);
		return Result.success(res);
	}
	
	/**
	 * 审核文章
	 * @param aid
	 * @param status
	 * @param auditpeople
	 * @return
	 */
	@Api(name = ApiConstant.WX_EXAMINE_ARTICLE)
	@RequestMapping(value = "/api/v1/wxarticles/examine/{aid}/{status}/{auditpeople}/{reason}", method = RequestMethod.GET, produces = "application/json")
	public Result<?>  examineArticle(@PathVariable String aid
			,@PathVariable String status,@PathVariable String auditpeople,@PathVariable String reason){
		boolean res = wxArticleService.examineArticle(aid, status, auditpeople,reason);
		return Result.success(res);
	}
	/**
	 * 根据条件查询文章
	 * @param page
	 * @param param
	 * @return
	 */
	@Api(name=ApiConstant.WX_QUERY_ARTICLES)
	@RequestMapping(value = "/api/v1/wxarticle/query/{page}/{count}", method = RequestMethod.POST, produces = "application/json")
	public Result<?> getArticleByCondition(@PathVariable Integer page,@PathVariable Integer count,@Valid QueryArticleVm articleVm){
		Map<String, Object> articleMap = wxArticleService.getArticleByCondition(page, count,articleVm);
		return Result.success(articleMap);
	}
	/**
	 * 根据条件查询文章
	 * @param page
	 * @param param
	 * @return
	 */
	@Api(name=ApiConstant.WX_QUERY_RANKS)
	@RequestMapping(value = "/api/v1/query/ranks/{page}/{count}/{periods}", method = RequestMethod.GET, produces = "application/json")
	public Result<?> getRankRsult(@PathVariable Integer page,
			@PathVariable Integer count,@PathVariable Integer periods){
		Map<String, Object> rankMap = wxArticleService.getRankList(page, count,periods);
		return Result.success(rankMap);
	}
	
}
