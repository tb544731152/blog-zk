package com.weapp.controller;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.weapp.common.annotation.Api;
import com.weapp.common.constant.ApiConstant;
import com.weapp.entity.result.CodeMsg;
import com.weapp.entity.result.Result;
import com.weapp.redis.RedisService;
import com.weapp.redis.SessionKey;
import com.weapp.service.WxService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
/**
 * 微信用户认证相关
 * @author zk
 *
 */
@RestController
public class WxAuthController{
	@Autowired
	private WxService wxService;
	@Autowired
	private RedisService redisService;

	/**
	 * 根据客户端传过来的code从微信服务器获取appid和session_key，然后生成3rdkey返回给客户端，后续请求客户端传3rdkey来维护客户端登录态
	 * @param wxCode	小程序登录时获取的code
	 * @return
	 */
	@ApiOperation(value = "获取sessionId", notes = "小用户允许登录后，使用code 换取 session_key api，将 code 换成 openid 和 session_key")
	@ApiImplicitParam(name = "code", value = "用户登录回调内容会带上 ", required = true, dataType = "String")
	@Api(name = ApiConstant.WX_CODE)
	@RequestMapping(value = "/api/v1/wx/getSession", method = RequestMethod.GET, produces = "application/json")
	public Result<?> createSssion(@RequestParam(required = true,value = "code")String wxCode){
		Map<String,Object> wxSessionMap = wxService.getWxSession(wxCode);
		if(null == wxSessionMap){
			return Result.msg(CodeMsg.GET_SESSION_FAIL);
		}
		//获取异常
		if(wxSessionMap.containsKey("errcode")){
			return Result.msg(CodeMsg.GET_SESSION_FAIL);
		}
		CodeMsg msg = wxService.create3rdSession(wxSessionMap);
	    return Result.msg(msg);
	}


	/**
	 * 校验token
	 * @param token	
	 * @return
	 */
	@ApiOperation(value = "获取token", notes = "校验token")
	@ApiImplicitParam(name = "token", value = "用户在小程序中获取 ", required = true, dataType = "String")
	@Api(name = ApiConstant.WX_CHECK_TOKEN)
	@RequestMapping(value = "/api/v1/wx/checkToken", method = RequestMethod.GET, produces = "application/json")
	public Result<?> checkToken(@RequestParam(required = true,value = "token")String token){
		CodeMsg msg = wxService.checkToken(token);
	    return Result.msg(msg);
	}
	
	
	
	/**
	 * 验证用户信息完整性
	 * @param rawData	微信用户基本信息
	 * @param signature	数据签名
	 * @param sessionId	会话ID
	 * @return
	 */
	@Api(name = ApiConstant.WX_CHECK_USER)
	@RequestMapping(value = "/api/v1/wx/checkUserInfo", method = RequestMethod.GET, produces = "application/json")
	public Result<?> checkUserInfo(@RequestParam(required = true,value = "rawData")String rawData,
			@RequestParam(required = true,value = "signature")String signature,
			@RequestParam(required = true,defaultValue = "sessionId")String sessionId){
		Object wxSessionObj = redisService.get(SessionKey.session, sessionId, String.class);
		if(null == wxSessionObj){
			return Result.msg(CodeMsg.FAIL_NOSESSION);
		}
		String wxSessionStr = (String)wxSessionObj;
		String sessionKey = wxSessionStr.split("#")[0];
		StringBuffer sb = new StringBuffer(rawData);
		sb.append(sessionKey);

		byte[] encryData = DigestUtils.sha1(sb.toString());
		byte[] signatureData = signature.getBytes();
		Boolean checkStatus = Arrays.equals(encryData, signatureData);
		if(checkStatus){
			return Result.msg(CodeMsg.CHECK_SUCCESS.fillArgsToken(checkStatus));	
		}
		return Result.msg(CodeMsg.CHECK_FAIL);
	}

	/**
	 * 获取用户openId和unionId数据(如果没绑定微信开放平台，解密数据中不包含unionId)
	 * @param encryptedData 加密数据
	 * @param iv			加密算法的初始向量	
	 * @param sessionId		会话ID
	 * @return
	 */
	@Api(name = ApiConstant.WX_DECODE_USERINFO)
	@RequestMapping(value = "/api/v1/wx/decodeUserInfo", method = RequestMethod.GET, produces = "application/json")
	public Result<?> decodeUserInfo(@RequestParam(required = true,value = "encryptedData")String encryptedData,
			@RequestParam(required = true,defaultValue = "iv")String iv,
			@RequestParam(required = true,defaultValue = "sessionId")String sessionId){
		System.out.println(encryptedData);
		System.out.println(iv);
		//从缓存中获取session_key
		Object wxSessionObj = redisService.get(SessionKey.session, sessionId, String.class);
		if(null == wxSessionObj){
			return Result.msg(CodeMsg.FAIL_NOSESSION);
		}
		try {
			CodeMsg msg = wxService.saveUser(encryptedData, iv, sessionId, wxSessionObj);
			return Result.msg(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.msg(CodeMsg.DECODE_SAVE_FAIL);
	}
}
