package com.weapp.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import redis.clients.jedis.Tuple;

import com.alibaba.fastjson.JSON;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.weapp.common.constant.ApiConstant;
import com.weapp.common.util.MongoPageable;
import com.weapp.entity.app.TAddress;
import com.weapp.entity.app.TPrize;
import com.weapp.entity.app.TUser;
import com.weapp.entity.auth.AppKey;
import com.weapp.entity.conf.TCreativeCompetition;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.vm.ArticleResult;
import com.weapp.entity.vm.ArticleVM;
import com.weapp.entity.vm.QueryArticleVm;
import com.weapp.entity.vm.WxSendMsg;
import com.weapp.entity.wxarticle.Article;
import com.weapp.entity.wxarticle.RankResult;
import com.weapp.entity.wxarticle.TThumbsRecord;
import com.weapp.redis.Rank;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.repository.AppKeyRepository;
import com.weapp.repository.CreatetiveCompetitionRepository;
import com.weapp.repository.RankResultRepository;
import com.weapp.repository.ThubsRepository;
import com.weapp.repository.WxArticleRepository;
import com.weapp.utils.WxMessageUtils;

@Service
public class WxArticleService {
	@Autowired
	private WxArticleRepository articleRepository;
	
	@Autowired
	private PrizeService prizeService;
	
	@Autowired
	private AppKeyRepository appKeyRepository;
	
	@Autowired
	private ThubsRepository thubsRepository;
	
	@Autowired
	private RankResultRepository rankResultRepository;
	
	@Autowired
	private CreatetiveCompetitionRepository createtiveCompetitionRepository;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private WxAddressService addressService;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	/**
	 * 根据文章ID查询文章信息
	 * @param aid
	 * @return
	 */
	public ArticleResult getArticle(String aid,String sessionId){
		List<Article> articles = articleRepository.findByAid(aid);
		if(articles!=null){
			TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
			Article article = articles.get(0);
			Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(), article.getAid());
			if(rank!=null){
				Long tempRank = rank.getRank();
				article.setIsthumbs(false);
				if(user!=null){
					List<TThumbsRecord> myrecords = queryThumbsRecords(article.getAid(), user.getOpenId());
					if(myrecords!=null&&myrecords.size()>0){
						article.setIsthumbs(true);
					}
				}
				article.setRank(tempRank);
			}
			return new ArticleResult(article);
		}else{
			return null;
		}
	}
	/**
	 * 保存文章
	 * @throws IOException
	 */
	public Boolean saveArticle(TUser user,ArticleVM vm){
		List<Article> list = new ArrayList<Article>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Article article = new Article();
		article.setForm_id(vm.getForm_id());
		article.setOpenId(user.getOpenId());
		String uid = UUID.randomUUID().toString().replace("-", "");
		article.setAid(uid);
		article.setThumbs(0);
		Map<String, String> authorMap = new HashMap<String, String>();
		authorMap.put("headimg", user.getAvatarUrl());
		authorMap.put("nickname", user.getNickName());
		article.setAuthor(authorMap);
		article.setBrowers(0);
		//查询此时存在的期数
		TCreativeCompetition conf = getStartConf();
		if(conf!=null){
			article.setPeriods(conf.getStage());
		}		
		article.setComments(0);
		article.setThumbs(0);
		article.setContent(vm.getContent());
		article.setCreateTime(sdf.format(new Date()));
		article.setTitle(vm.getTitle());
		article.setStatus("0");//待审核
		article.setImgs(vm.getImgs());
		//redisService.zSetAdd(ApiConstant.WX_ARTICLES_RANK_DB,"uid"+i, i);
		list.add(article);
		List<?> res =articleRepository.save(list);
		if(res!=null){
			return true;
		}
		return false;
	}
	/**
	 * 根据文章标题模糊搜索（分页）
	 * @param title
	 * @param page
	 * @return
	 */
	public List<Article> getByTitle(String title, MongoPageable page) {
		return articleRepository.findByTitleLike(title,page);
	}
	/**
	 * 根据文章id查询文章
	 * @param title
	 * @param page
	 * @return
	 */
	public List<Article> getByAid(String aid) {
		return articleRepository.findByAid(aid);
	}
	
	/**
	 * 查询此人所有文章
	 * @param title
	 * @param page
	 * @return
	 */
	public List<Article> getByOpenId(String openId) {
		return articleRepository.findByOpenId(openId);
	}
	
	
	public Map<String,List<ArticleResult>> getRank(Integer page,Integer start,Integer end,String sessionId){
		Map<String,List<ArticleResult>> res = new HashMap<String, List<ArticleResult>>();
		TCreativeCompetition conf = getLasterConf();
		TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
		if(page==1){
			//获取用户信息
			if(user!=null){
				List<Article> ourselfs = getByOpenId(user.getOpenId());
				if(ourselfs!=null&&ourselfs.size()>0){
					for(Article article : ourselfs){
						article.setIsthumbs(false);
						if(!article.getStatus().equals("0")){
							Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(), article.getAid());
							if(rank!=null){
								Long tempRank = rank.getRank();
								if(user!=null){
									List<TThumbsRecord> myrecords = queryThumbsRecords(article.getAid(), user.getOpenId());
									if(myrecords!=null&&myrecords.size()>0){
										article.setIsthumbs(true);
									}
								}
								Long count = redisService.zcard(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods());
								if(conf!=null){
									if(count > conf.getRankNums() && tempRank > conf.getRankNums()){
										Set<Tuple> set = redisService.getZset(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(),conf.getRankNums()-1, conf.getRankNums()-1);
										Double tenScore = new Double(0);
										for (Tuple temp : set) {
											tenScore = temp.getScore();
										}
										article.setRemark(getInt((tenScore-rank.getScore()))+"");
									}
								}
								article.setRank(tempRank);
							}
						}
					}
				}
				res.put("ourselfs",artChangeResult(ourselfs));
			}
		}
		List<Article> ranks=new ArrayList<Article>();
		if(conf!=null){
			Set<Tuple> set = redisService.getZset(ApiConstant.WX_ARTICLES_RANK_DB_prefix+conf.getStage(), start, end);
			for (Tuple temp : set) {
				String aid = temp.getElement();
				List<Article> tempArticles = articleRepository.findByAid(aid);
				if(tempArticles!=null&&tempArticles.size()>0){
					Article tempArticle =tempArticles.get(0);
					Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+conf.getStage(), tempArticle.getAid());
					Long tempRank = rank.getRank();
					tempArticle.setIsthumbs(false);
					if(user!=null){
						List<TThumbsRecord> myrecords = queryThumbsRecords(aid, user.getOpenId());
						if(myrecords!=null&&myrecords.size()>0){
							tempArticle.setIsthumbs(true);
						}
					}
					tempArticle.setRank(tempRank);
					ranks.add(tempArticle);
				}
			}
		}
		res.put("ranks", artChangeResult(ranks));
		return res;
	}
	
	/**
	 * 查询用户所发全部文章
	 * @param sessionId
	 * @param page
	 * @return
	 */
	public List<Article> getBySession(String sessionId,MongoPageable page){
		//获取用户信息
		TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
		if(user==null){
			return null;
		}
		List<Article> articles = articleRepository.findByOpenId(user.getOpenId(),page);
		return articles;
	}
	
	/**
	 * 点赞
	 * @param sessionId
	 * @param page
	 * @return
	 */
	public CodeMsg articlesRank(String sessionId,String method,String aid){
		//获取用户信息
		TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
		if(user==null){
			return null;
		}
		List<Article> arts =  getByAid(aid);
		if(arts ==null || arts.size()==0){
			return CodeMsg.NO_Article;
		}
		//点赞
		if(method.equals("1")){
			//查询是否点过赞
			List<TThumbsRecord> myrecords = queryThumbsRecords(aid, user.getOpenId());
			if(myrecords!=null && myrecords.size()>0){
				return CodeMsg.CLICK_Thumbs_FAIL;
			}
			//每天点赞限制
			List<TThumbsRecord> myDAYrecords = queryThumbsRecordsDAY(user.getOpenId());
			if(myDAYrecords!=null&&myDAYrecords.size()>2){
				return CodeMsg.CLICK_Thumbs_DAY_LIMIT;
			}
			//保存点赞记录
			saveThubsRecord(user, aid);
		}
		Article art = arts.get(0);
		if(art.getPeriods()!=null){
			//查询此文章对应配置
			TCreativeCompetition conf = createtiveCompetitionRepository.findByStage(art.getPeriods());
			Map<String,Integer> rankMethod = conf.getRankMethod();
			Integer weight = rankMethod.get(method);
			Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+art.getPeriods(), art.getAid());
			//活动未结束
			if(rank!=null&&!isEND(conf)){
				Double score = rank.getScore();
				score+=weight;
				redisService.updateRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+art.getPeriods(), aid,score.intValue());
			}
		}
		updateRank(method, art);
		return CodeMsg.SUCCESS;
	}
	
	public void saveThubsRecord(TUser user,String aid){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TThumbsRecord thubsR= new TThumbsRecord();
		Map<String,String> author = new HashMap<String, String>();
		author.put("headimg", user.getAvatarUrl());
		author.put("nickname", user.getNickName());
		thubsR.setAuthor(author);
		thubsR.setCreateDate(sdf.format(new Date()));
		thubsR.setOpenId(user.getOpenId());
		thubsR.setAid(aid);
		thubsRepository.save(thubsR);
	}
	
	
	public TCreativeCompetition getStartConf(){
		List<TCreativeCompetition> confs = queryConfOrderByCreateDate();
		if(confs!=null){
			for(TCreativeCompetition conf : confs ){
				if(!isEND(conf)){
					return conf;
				}
			}
		}
		return null;
	}
	
	public TCreativeCompetition getLasterConf(){
		List<TCreativeCompetition> confs = queryConfOrderByCreateDate();
		if(confs!=null){
			return confs.get(0);
		}
		return null;
	}
	
	
	/**
	 * 判断活动是否结束
	 * @param conf
	 * @return
	 */
	public boolean isEND(TCreativeCompetition conf){
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date  startDate = sdf.parse(conf.getStartdate());
			Date endDate = sdf.parse(conf.getEnddate());
			Date now = new Date();
			if(now.before(startDate)){
				return false;
			}
			if(now.after(startDate)&&now.before(endDate)){
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 *更新文章排行
	 * @param method
	 * @param aid
	 * 计算分数--更新排行
		1 点赞 * 2
		2 浏览 * 1 
		3 评论 * 3
	*/
	public void updateRank(String method,Article article){
		switch (method) {
		case "1":
			updateThumbs(article);
			break;
		case "2":
			updateBrowers(article);
			break ;
		case "3":
			updateComments(article);
			break;
		}
	}
	
	
	public void updateThumbs(Article article){
		WriteResult res = mongoTemplate.updateMulti(query(where("aid").is(article.getAid())), 
				Update.update("thumbs", article.getThumbs()+1), Article.class);
		System.out.println(res.getN());
	}
	public void updateBrowers(Article article){
		mongoTemplate.updateMulti(query(where("aid").is(article.getAid())),
				Update.update("browers", article.getBrowers()+1), Article.class);
	}
	public void updateComments(Article article){
		mongoTemplate.updateMulti(query(where("aid").is(article.getAid())),
				Update.update("comments", article.getComments()+1), Article.class);
	}
	/**
	 * 查询点赞记录
	 * @param aid
	 * @param openId
	 * @return
	 */
	public List<TThumbsRecord> queryThumbsRecords(String aid,String openId){
		Query query = new Query();
		if (aid!=null&&aid!="" && openId!=""&&openId!=null) {
			query.addCriteria(
				    new Criteria().andOperator(
				        Criteria.where("aid").is(aid),
				        Criteria.where("openId").is(openId)
				        )
				    );
        }
		return mongoTemplate.find(query, TThumbsRecord.class);
	}
	
	/**
	 *查询每天点赞次数
	 */
	public List<TThumbsRecord> queryThumbsRecordsDAY(String openId){
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Query query = new Query();
		if(openId!=""&&openId!=null){
			Criteria criteria = Criteria.where("openId").
					is(openId).
					and("createDate").
					gt(sdf.format(new Date()));
			query.addCriteria(criteria);
		}
		System.out.println(sdf.format(new Date()));
		return mongoTemplate.find(query, TThumbsRecord.class);
	}
	
	public List<TCreativeCompetition> queryConfOrderByCreateDate(){
		Query query = new Query();
		query.with(new Sort(new Order(Direction.DESC,"createdate")));
		return mongoTemplate.find(query, TCreativeCompetition.class);
	}
	//通过期数Id查询排名结果
	public List<RankResult> queryRankResultByPeriods(String periods){
		Query query = new Query();
		if (periods!=null&&periods!="") {
			query.addCriteria(
				    new Criteria().andOperator(
				        Criteria.where("periods").is(periods)
				        )
				    );
        }
		return mongoTemplate.find(query, RankResult.class);
	}
	
	
	
	public Map<String,Object> generateRankRessult( String sessionId){
		Map<String,Object> rankResult = new HashMap<String, Object>();
		List<TCreativeCompetition> confs = queryConfOrderByCreateDate();
		if(confs!=null&&confs.size()>0){
			TCreativeCompetition latelyConf = confs.get(0);
			List<RankResult> result = new ArrayList<RankResult>();
			Map<String,String> sort= new HashMap<String, String>();
			if(latelyConf.getGenerate()){
				result = queryRankResultByPeriods(latelyConf.getStage());
				for(RankResult res : result){
					sort.put(res.getOpenId(), res.getAid());
				}
			}else{
				generateRank(sort,0, latelyConf.getRankNums()-1, result, latelyConf);
			}
			rankResult.put("ranks",result);
			Map<String,String> myselfResult = new HashMap<String, String>();
			myselfResult.put("code","0");
			myselfResult.put("msg", "很遗憾，您的排名有些靠后~");
			rankResult.put("myself",myselfResult);
			//获取用户信息
			TUser user = redisService.get(SessionKey.session, sessionId, TUser.class);
			if(user!=null){
				if(sort.containsKey(user.getOpenId())){
					myselfResult.put("code","1");
					myselfResult.put("msg", "您在排名之内！赶快去领奖吧！");
					myselfResult.put("aid", sort.get(user.getOpenId()));
					rankResult.put("myself",myselfResult);
					//查询是否已领取
					List<TAddress> awards = addressService.queryAwardByAid(sort.get(user.getOpenId()));
					if(awards!=null && awards.size()>0){
						myselfResult.put("code","2");
						myselfResult.put("msg", "您已领取过");
						rankResult.put("myself",myselfResult);
					}
				}
			}
		}
		return rankResult;
	}
	
	public List<RankResult> generateRank(Map<String,String> sort ,Integer start,Integer end,List<RankResult> result,TCreativeCompetition latelyConf){
		//生成中奖名单
		Set<Tuple> set = redisService.getZset(ApiConstant.WX_ARTICLES_RANK_DB_prefix+latelyConf.getStage(),start,end);
		for (Tuple temp : set) {
			String aid = temp.getElement();
			List<Article> tempArticles = articleRepository.findByAid(aid);
			if(tempArticles!=null&&tempArticles.size()>0){
				Article tempArticle =tempArticles.get(0);
				Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+tempArticle.getPeriods(), tempArticle.getAid());
				Long tempRank = rank.getRank();
				RankResult res = new RankResult();
				res.setAid(aid);
				res.setAuthor(tempArticle.getAuthor());
				res.setCreateTime(getDateFormat());
				res.setOpenId(tempArticle.getOpenId());
				res.setPeriods(tempArticle.getPeriods());
				res.setRank(tempRank);
				//保存结果
				if(!sort.containsKey(res.getOpenId())){
					sort.put(res.getOpenId(), res.getAid());
					Integer tempRankRes = sort.values().size();
					res.setRank(tempRankRes.longValue());
					res.setPrizeName(getPrizeName(latelyConf.getStage(), tempRankRes.longValue()));
					rankResultRepository.save(res);
					result.add(res);
				}
			}
		}
		if(set.size() == (end - start + 1) && sort.values().size() != latelyConf.getRankNums()){
			//继续 
			Integer tempStart = end+1;
			Integer tempEnd =  tempStart +  (latelyConf.getRankNums() - sort.values().size() - 1 );
			generateRank(sort, tempStart, tempEnd, result, latelyConf);
		}
		//更新活动配置
		updateConf(latelyConf);
		return result;
	}
	
	public String getPrizeName(String stage,Long rank){
		List<TPrize> prizes = prizeService.queryByStage(stage);
		if(prizes!=null){
			for (TPrize prize : prizes) {
				if(rank.intValue()>=prize.getStart() && rank.intValue() <=prize.getEnd()){
					return prize.getPrizeName();
				}
			}
		}
		return "暂无";
	}
	
	
	public String getDateFormat(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * 通过openId和期数查询
	 * @param title
	 * @param page
	 * @return
	 */
	public List<Article> getArticleByOpenIdAndStage(String openId,String periods) {
		Query query = new Query();
		if (openId!=""&&openId!=null && periods!=null&&periods!="") {
			query.addCriteria(
				    new Criteria().andOperator(
				        Criteria.where("openId").is(openId),
				        Criteria.where("periods").is(periods)
				        )
				    );
        }
		return mongoTemplate.find(query, Article.class);
	}
	
	public String iswin(TUser user ,TCreativeCompetition latelyConf){
		List<Article> ourselfs = getArticleByOpenIdAndStage(user.getOpenId(),latelyConf.getStage());
		if(ourselfs!=null&&ourselfs.size()>0){
			for(Article article : ourselfs){
				Rank rank = redisService.queryRank(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(), article.getAid());
				if(rank!=null){
					Long tempRank = rank.getRank();
					if(tempRank!=0l && tempRank<=latelyConf.getRankNums()){
						return rank.getMember();
					}
				}
			}
		}
		return null;
	}
	
	
	public void updateConf(TCreativeCompetition conf){
		mongoTemplate.updateMulti(query(where("_id").is(conf.getId())),
				Update.update("generate",true), TCreativeCompetition.class);
	}
	 public  int getInt(double number){
		    BigDecimal bd=new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
		    return Integer.parseInt(bd.toString()); 
     } 
	 
	public List<ArticleResult> artChangeResult(List<Article> artlist){
		List<ArticleResult> list = new ArrayList<ArticleResult>();
		for (Article art : artlist) {
			if(!art.getStatus().equals("2")){
				list.add(new ArticleResult(art));
			}
		}
		return list;
	 } 
	/**
	 * 审核文章
	 * @param aid 文章id
	 * @param status 要修改的状态
	 * @param auditpeople 审核人
	 * @return
	 */
	public boolean examineArticle(String aid,String status,String auditpeople,String reason){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(date);
		//条件
		Query query = new Query(Criteria.where("aid").is(aid));
		//
		Update update = new Update();
		update.set("status", status)
		.set("auditpeople", auditpeople).set("auditdate", today).
		set("remark", reason);
		List<Article> articles = articleRepository.findByAid(aid);
		if (articles==null || articles.size()==0) {
			return false;
		}else {
			Article article = articles.get(0);
			WriteResult result = mongoTemplate.updateMulti(query, update, Article.class);
			if (status.equals("1")) { //审核通过加入排行
				boolean isExist = 
						redisService.existRand(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(), aid);
				if (!isExist) {
					redisService.zSetAdd(ApiConstant.WX_ARTICLES_RANK_DB_prefix+article.getPeriods(), aid, 0);
				}
			}
			sendTemplate(article,status,reason);
		}
		return true;
	}
	private void sendTemplate(Article article,String status,String reason) {
		List<AppKey> zyzsSides  = appKeyRepository.findAll();
		AppKey ak = zyzsSides.get(0);
		WxSendMsg msg = new WxSendMsg();
		msg.setOpenId(article.getOpenId());
		msg.setTitle("创意大赛");
		if(status.equals("1")){
			msg.setResult("创意大赛文章审核【通过】");
		}else{
			msg.setResult("创意大赛文章审核【不通过】");
			msg.setReason(reason);
		}
		 SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		msg.setDate(myFmt.format(new Date()));
		msg.setForm_id(article.getForm_id());
		WxMessageUtils.sendMsg(msg , ak);
	}
	/**
	 * 根据条件查询文章
	 * @param page 页数
	 * @param param 参数
	 * @return
	 */
	public Map<String, Object> getArticleByCondition(Integer page,Integer count,QueryArticleVm articleVm){
		Map<String, Object> result = new HashMap<String, Object>();
		Query query = new Query();
		Criteria criteria = new Criteria();
		if (articleVm!=null) {
			if (articleVm.getOpenId()!=null && !articleVm.getOpenId().equals("")) {
				criteria.and("openId").is(articleVm.getOpenId());
			}
			if (articleVm.getAid()!=null && !articleVm.getAid().equals("")) {
				criteria.and("aid").is(articleVm.getAid());
			}
			if (articleVm.getStatus()!=null && !articleVm.getStatus().equals("")) {
				String status = articleVm.getStatus();
				criteria.and("status").is(articleVm.getStatus());
				query.with(new Sort(Direction.DESC, "auditdate"));
			}
		}
		if (page<=0) {
			page = 1;
		}
		query.addCriteria(criteria);
		query.skip((page-1)*count).limit(count);
		List<Article> articles = mongoTemplate.find(query, Article.class);
		Long recordCount = mongoTemplate.count(query, Article.class);
		result.put("articles", articles);
		result.put("recordCount", recordCount);
		return result;
	}
	/**
	 * 后台查询中奖名单
	 * @param page
	 * @param count
	 * @param periods
	 * @return
	 */
	public Map<String, Object> getRankList(Integer page,Integer count,Integer periods){
		Map<String, Object> result = new HashMap<String, Object>();
		Query query = new Query();
		Criteria criteria = new Criteria();
		if (page<=0) {
			page = 1;
		}
		query.query(Criteria.where("periods").is(periods));
		query.skip((page-1)*count).limit(count);
		//List<RankResult> rankResults = mongoTemplate.find(query, RankResult.class);
		Long recordCount = mongoTemplate.count(query, RankResult.class);
//		Aggregation aggregation1 = 
//				Aggregation.newAggregation(Aggregation.lookup("t_articles", "aid", "aid", "article"));
//		AggregationResults<BasicDBObject> outputTypeCount1 = 
//				mongoTemplate.aggregate(aggregation1, RankResult.class, BasicDBObject.class);
//		for (Iterator<BasicDBObject> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
//		    DBObject obj = iterator.next();
//		    System.out.println(JSON.toJSONString(obj));
//		}
		List<DBObject> pipeline = new ArrayList<>();
		DBObject dbObject1 = new BasicDBObject().append("$lookup", 
				new BasicDBObject().append("from", "t_articles").append("localField", "aid")
				.append("foreignField", "aid").append("as", "article")); 
		DBObject dbObject2 = new BasicDBObject().append("$lookup", 
				new BasicDBObject().append("from", "t_address").append("localField", "aid")
				.append("foreignField", "aid").append("as", "address")); 
		DBObject dbObject3 = new BasicDBObject().append("$skip",(page-1)*count);
		DBObject dbObject4 = new BasicDBObject().append("$limit",count);
		pipeline.add(dbObject1);
		pipeline.add(dbObject2);
		pipeline.add(dbObject3);
		pipeline.add(dbObject4);
		List<DBObject> rankResults = new ArrayList<DBObject>();
		Cursor cursor = 
				mongoTemplate.getCollection("t_rank_result").aggregate(pipeline,AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
		while(cursor.hasNext()){  
		      DBObject document = cursor.next();  
		      rankResults.add(document);
		  } 
		result.put("rankResults", rankResults);
		result.put("recordCount", recordCount);
		return result;
	}
	
}
