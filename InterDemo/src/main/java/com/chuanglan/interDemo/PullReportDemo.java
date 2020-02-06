package com.chuanglan.interDemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chuanglan.interDemo.util.HttpUtil;

public class PullReportDemo {

	private static final Logger logger = LogManager.getLogger(HttpUtil.class);
	
	public static void main(String[] args){
		//请求地址
		String url="http://intapi.253.com/pull/report";
		
		//API账号，50位以内。必填
		String account="I2170326";	
		
		//API账号对应密钥，联系客服获取。必填
		String password="*******";
		
		//拉取个数（最大100，默认20），选填
		String count = "20";
		
		//组装请求参数
		JSONObject map=new JSONObject();
		map.put("account", account);
		map.put("password", password);
		map.put("count", count);

		String params=map.toString();
		
		logger.info("请求参数为:" + params);
		try {
			String result=HttpUtil.post(url, params);
			
			logger.info("返回参数为:" + result);
			
			JSONObject jsonObject =  JSON.parseObject(result);
			JSONArray resultArray = jsonObject.getJSONArray("result");
			
			for(Object jsonObj : resultArray) {
				JSONObject obj = (JSONObject) jsonObj ;
				logger.info(obj.toString());				
			}
		} catch (Exception e) {
			logger.error("请求异常：" + e);
		}
	}
}
