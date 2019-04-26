package com.weapp.redis;

import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@Service
public class RedisService {
	
	@Autowired
	JedisPool jedisPool;
	
	/**
	 * 获取当个对象
	 * */
	public <T> T get(KeyPrefix prefix, String key,  Class<T> clazz) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 //生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 System.out.println(realKey);
			 String  str = jedis.get(realKey);
			 T t =  stringToBean(str, clazz);
			 return t;
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 设置对象
	 * */
	public <T> boolean set(KeyPrefix prefix, String key,  T value) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 String str = beanToString(value);
			 if(str == null || str.length() <= 0) {
				 return false;
			 }
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 int seconds =  prefix.expireSeconds();
			 if(seconds <= 0) {
				 jedis.set(realKey, str);
			 }else {
				 jedis.setex(realKey, seconds, str);
			 }
			 return true;
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 判断key是否存在
	 * */
	public <T> boolean exists(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.exists(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 删除
	 * */
	public boolean delete(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			long ret =  jedis.del(realKey);
			return ret > 0;
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.incr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.decr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	
	public boolean delete(KeyPrefix prefix) {
		if(prefix == null) {
			return false;
		}
		List<String> keys = scanKeys(prefix.getPrefix());
		if(keys==null || keys.size() <= 0) {
			return true;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(keys.toArray(new String[0]));
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public List<String> scanKeys(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<String> keys = new ArrayList<String>();
			String cursor = "0";
			ScanParams sp = new ScanParams();
			sp.match("*"+key+"*");
			sp.count(100);
			do{
				ScanResult<String> ret = jedis.scan(cursor, sp);
				List<String> result = ret.getResult();
				if(result!=null && result.size() > 0){
					keys.addAll(result);
				}
				//再处理cursor
				cursor = ret.getStringCursor();
			}while(!cursor.equals("0"));
			return keys;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public static <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		 if(jedis != null) {
			 jedis.close();
		 }
	}
	
	
	/**
	 * 查询此人具体排名
	 * @param tableName
	 * @param member
	 * @return
	 */
	public Rank queryRank(final String tableName,String member){
		Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 Long count= jedis.zcard(tableName);
			 Long myrank=jedis.zrank(tableName, member);
			Long rank = 0L;
			if(myrank!=null){
				rank = count - myrank;
			}		
			Double score=jedis.zscore(tableName, member);
			if(score==null){ 
				score = new Double(0) ;
			}
			return new Rank(rank, score, member);
		 }finally {
			  returnToPool(jedis);
		 }
	}
	/**
	 * 判断是否存在排名
	 * @param tableName
	 * @param member
	 * @return
	 */
	public boolean existRand(String tableName,String member){
		Jedis jedis =  jedisPool.getResource();
		Long myrank=jedis.zrank(tableName, member);
		if (myrank!=null) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public Long zcard(final String tableName){
		Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 Long count= jedis.zcard(tableName);
			 return count;
		 }finally {
			  returnToPool(jedis);
		 }
	}
	//---------------------zset操作-------------------------------------
		/**
		 * 增加 zset
		 * @param tableName
		 * @param member
		 * @param score
		 * @return
		 */
		public <T> Long zSetAdd(String tableName,String member,int score) {
			 Jedis jedis = null;
			 try {
				 jedis =  jedisPool.getResource();
				return  jedis.zadd(tableName, score, member);
			 }finally {
				  returnToPool(jedis);
			 }
		}
		/**
		 * 更新zset
		 * @param tableName
		 * @param member
		 * @param score
		 * @return
		 */
		public <T> Double updateRank(String tableName,String member,int score){
			Jedis jedis = null;
			 try {
				 jedis =  jedisPool.getResource();
				return  jedis.zincrby(tableName, score, member);
			 }finally {
				  returnToPool(jedis);
			 }
		}
		/**
		 * 查询排名 分页
		 * @param tableName
		 * @param startOffset
		 * @param endOffset
		 * @return
		 */
		public Set<Tuple>  getZset(String tableName,long startOffset,long endOffset){
			Jedis jedis = null;
			 try {
				 jedis =  jedisPool.getResource();
				 //倒序 jedis.zrevrangeWithScores(tableName, startOffset, endOffset);
				 //正序  jedis.zrangeWithScores(tableName, startOffset, endOffset)
				return  jedis.zrevrangeWithScores(tableName, startOffset, endOffset);
			 }finally {
				  returnToPool(jedis);
			 }
		}

}
