package com.weapp.interceptor;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.weapp.entity.auth.AccessLog;
import com.weapp.entity.auth.ApiInfo;
import com.weapp.entity.auth.AppKey;
import com.weapp.service.AccessLogService;
import com.weapp.service.ApiInfoService;
import com.weapp.service.AppKeyService;

/**
 * api接口拦截处理
 * @author zk
 *
 */
public class ApiInterceptor implements HandlerInterceptor {
	private static ImmutableMap<String,Integer>methodMap = ImmutableMap.of("GET", 1, "POST", 2, "PUT", 4, "DELETE", 8);
	@Autowired
	private AppKeyService appKeyService;
	@Autowired
	private ApiInfoService apiInfoService;
	@Autowired
	private ImmutableMap<String, String> errorCodeMap;
	@Autowired
	private AccessLogService accessLogService;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception exp)
			throws Exception {
		String apiName = request.getParameter("apiName");
		if(!StringUtils.isEmpty(apiName)){
			AccessLog accessLog = new AccessLog();
			accessLog.setAccessDate(new Date());
			accessLog.setApiName(apiName);
			accessLog.setUri(request.getRequestURI());
			if(exp != null){
				accessLog.setExp(exp.getMessage());
			}
			//拼接请求参数，key1=value1&key2=value2的形式
			String paramStr = "";
			Map<String,String[]> params = request.getParameterMap();
			if (params != null && params.size() > 0) {
				for(Map.Entry<String, String[]> p : params.entrySet()){
					if(p.getValue() == null || p.getValue().length == 0){
						continue;
					}
					paramStr += p.getKey() + "=" + p.getValue()[0] + "&";
				}
			}
			accessLog.setReqParam(paramStr.substring(0, paramStr.length() - 1));
			System.out.println(accessLog.getReqParam());
			accessLogService.save(accessLog);
		}
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		String uri =request.getRequestURI();
		if(uri.contains("file")){
			return true;
		}
		String apiName = request.getParameter("apiName");
		if(null == apiName || "".equals(apiName)){
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		ApiInfo apiInfo = apiInfoService.getByApiName(apiName);
		if(null == apiInfo){
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("application/json; charset=utf-8"); 
		PrintWriter out = null;
		//判断接口状态；
		if(apiInfo.isDisabled()){
			//接口已禁用
			out = response.getWriter();
			out.append(getResStr("40003"));
			out.flush();
			out.close();
			return false;
		}

		String method = request.getMethod();
		if(Integer.compare(methodMap.get(method), apiInfo.getCrud()) != 0){
			//http method不匹配 apiInfo.getCrud()
			out = response.getWriter();
			out.append(getResStr("40005"));
			out.flush();
			out.close();
			return false;
		}
		String appId = request.getParameter("appId");
		if(null == appId || "".equals(appId)){
			out = response.getWriter();
			out.append(getResStr("40001"));
			out.flush();
			out.close();
			return false;
		}
		//获取appid,请求是否合法
		AppKey appKey = appKeyService.getByAppId(appId);
		if(null == appKey){
			out = response.getWriter();
			out.append(getResStr("40001"));
			out.flush();
			out.close();
			return false;
		}
		//判断是否有接口调用权限
		Map<String,Map<String,Integer>>apiMap = appKey.getApis();
		if(null == apiMap || apiMap.size() == 0 || !apiMap.containsKey(apiName)){
			//无调用权限
			out = response.getWriter();
			out.append(getResStr("40006"));
			out.flush();
			out.close();
			return false;
		}
		Map<String,Integer> methodInfo = apiMap.get(apiName);
	/*	//调用次数是否超出上限；
		if(methodInfo.get("calltimes") > methodInfo.get("alltimes")){
			//超出调用次数
			out = response.getWriter();
			out.append(getResStr("40007"));
			out.flush();
			out.close();
			return false;
		}*/
		//参数校验

		//通过，更新调用次数
		methodInfo.put("calltimes", methodInfo.get("calltimes") + 1);
		apiMap.put(apiName, methodInfo);
		appKey.setApis(apiMap);
		//记录访问日志
		appKeyService.update(appKey);
		return true;
	}
	private String getResStr(String errorCode){
		return "{\"errorCode\":" + errorCode + ",\"msg\":\"" + errorCodeMap.get(errorCode) + "\"}";
	}
}
