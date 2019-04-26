package com.weapp.utils;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.weapp.entity.auth.AppKey;
import com.weapp.entity.vm.WxSendMsg;


public class WxMessageUtils {
	
	public static JSONObject sendMsg(WxSendMsg msg,AppKey appkey){
		JSONObject json = new JSONObject();
		WxTemplate t = new WxTemplate();
		t.setTouser(msg.getOpenId());
		t.setPage("pages/competition/competition");
		t.setForm_id(msg.getForm_id());
        t.setTemplate_id(msg.templateId);
        
        Map<String,TemplateData> m = new HashMap<String,TemplateData>();  
        TemplateData keyword1 = new TemplateData();  
        keyword1.setColor("#000000");  
        keyword1.setValue(msg.getResult()+"\n");  
        m.put("keyword1", keyword1);  
        
        TemplateData keyword2 = new TemplateData();  
        keyword2.setColor("#000000");  
        keyword2.setValue(msg.getDate()+"\n");  
        m.put("keyword2", keyword2);
        
        if (msg.getReason()!=null && !msg.getReason().equals("")) {
        	TemplateData keyword3 = new TemplateData();  
        	keyword3.setColor("#000000");  
        	keyword3.setValue(msg.getReason()+"\n");  
        	m.put("keyword3", keyword3);
		}
        t.setData(m);
        String result = HttpUtils.postJsonRequest("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="+appkey.getAccessToken(), json.toJSONString(t).toString());
		JSONObject results = JSONObject.parseObject(result);
		return results;
	}

}
