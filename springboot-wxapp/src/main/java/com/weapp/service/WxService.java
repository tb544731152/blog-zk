package com.weapp.service;

import java.security.InvalidAlgorithmParameterException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.weapp.common.aes.AES;
import com.weapp.common.properties.WxAuth;
import com.weapp.common.util.HttpRequest;
import com.weapp.entity.app.TUser;
import com.weapp.entity.result.CodeMsg;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.repository.UserRepository;
import com.weapp.utils.JsonUtil;

@Service
public class WxService {
	@Autowired
	private WxAuth wxAuth;
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * 根据小程序登录返回的code获取openid和session_key
	 * https://mp.weixin.qq.com/debug/wxadoc/dev/api/api-login.html?t=20161107
	 * @param wxcode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object>getWxSession(String wxCode){
		StringBuffer sb = new StringBuffer();
		sb.append("appid=").append(wxAuth.getAppId());
		sb.append("&secret=").append(wxAuth.getSecret());
		sb.append("&js_code=").append(wxCode);
		sb.append("&grant_type=").append(wxAuth.getGrantType());
		String res = HttpRequest.sendGet(wxAuth.getSessionHost(), sb.toString());
		if(res == null || res.equals("")){
			return null;
		}
		return JSON.parseObject(res, Map.class);
	}
	
	public CodeMsg checkToken(String token){
		TUser userSession = redisService.get(SessionKey.session, token, TUser.class);
		if(userSession!=null){
			return CodeMsg.GET_SESSION_SUCCESS.fillArgsToken(token);
		}
		return CodeMsg.GET_SESSION_FAIL;
	}
	
	
	/**
	 * 缓存微信openId和session_key
	 * @param wxOpenId		微信用户唯一标识
	 * @param wxSessionKey	微信服务器会话密钥
	 * @param expires		会话有效期, 以秒为单位, 例如2592000代表会话有效期为30天
	 * @return
	 */
	public CodeMsg create3rdSession(Map<String,Object> wxSessionMap){
		String wxOpenId = (String)wxSessionMap.get("openid");
		String wxSessionKey = (String)wxSessionMap.get("session_key");
		TUser user = userRepository.findByOpenId(wxOpenId);
		if(user!=null){
			TUser userSession = redisService.get(SessionKey.session, wxOpenId, TUser.class);
			if(userSession!=null){
				return CodeMsg.GET_SESSION_SUCCESS.fillArgsToken(wxOpenId);
			}
		}else{
			String userSession = redisService.get(SessionKey.session, wxOpenId, String.class);
			if(userSession!=null){
				StringBuffer sb = new StringBuffer();
				sb.append(wxSessionKey).append("#").append(wxOpenId);
				redisService.set(SessionKey.session, wxOpenId, sb.toString());
				return CodeMsg.GET_SESSION_NOSUB.fillArgsToken(wxOpenId);
			}
		}
		if(user!=null){
			redisService.set(SessionKey.session, wxOpenId,user);
			return CodeMsg.GET_SESSION_SUCCESS.fillArgsToken(wxOpenId);
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append(wxSessionKey).append("#").append(wxOpenId);
			redisService.set(SessionKey.session, wxOpenId, sb.toString());
			return CodeMsg.GET_SESSION_NOSUB.fillArgsToken(wxOpenId);
		}
	}
	
	public CodeMsg saveUser(String encryptedData,String iv,String sessionId,Object wxSessionObj ) throws Exception{
		String wxSessionStr = (String)wxSessionObj;
		String sessionKey = wxSessionStr.split("#")[0];
		String openId = wxSessionStr.split("#")[1];
		AES aes = new AES();
		byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData),
				Base64.decodeBase64(sessionKey), 
				Base64.decodeBase64(iv));
		if(null != resultByte && resultByte.length > 0){
			String userInfo = new String(resultByte, "UTF-8");
			TUser user = (TUser) JsonUtil.stringToObj(userInfo, TUser.class);
			redisService.set(SessionKey.session, openId,user);
			userRepository.save(user);
			return CodeMsg.DECODE_SAVE_SUCCESS.fillArgsToken(openId);
		}
		return CodeMsg.DECODE_SAVE_FAIL;
	}
}
