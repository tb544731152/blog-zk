package com.weapp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class JsonUtil {
	public static String objToString(Object obj) {
		return JSON.toJSONString(obj);
	}

	public static Object stringToObj(String text, Class clazz) {
		return JSON.parseObject(text, clazz);
	}

	public static JSONObject strToJSON(String text) {
		return JSON.parseObject(text);
	}

	public static Object jsonToObj(JSON json, Class clazz) {
		return JSON.toJavaObject(json, clazz);
	}

	public static String getValueFromString(String text, String key) {
		return (String) JSON.parseObject(text).get(key);
	}

	public static JSONArray strToJSONArray(String text) {
		return JSONArray.parseArray(text);
	}
}
