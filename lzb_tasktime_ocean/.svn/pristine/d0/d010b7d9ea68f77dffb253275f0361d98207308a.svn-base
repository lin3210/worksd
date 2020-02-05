package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;

public class WxSendService extends BaseService{
	
	private static Logger logger = Logger.getLogger(WxSendService.class);
		
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	/**
	 * 获取待发送的推送模板
	 * @return
	 */
	public DataRow getPush() {
		String sql = " select * from t_push where status = 1 ";
		return getJdbcTemplate().queryMap(sql);
	}
	/**
	 * 获取微信用户
	 * @return
	 */
	public List<DataRow> getWxUserList() {
		String sql = "select id,w_openid from t_weixin_info where w_type = 0";
//		String sql = "select w_openid from t_weixin_info where id in (29,227,30)";
//		String sql = "select w_openid from t_weixin_info where id = 29";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * 获取模板内容
	 * @param id
	 * @return
	 */
	public String getTemplate(String id)
	{
		return getJdbcTemplate().queryString("select content from t_template where template_id = '"+id+"'");
	}
	/**
	 * 设置推送模板已发送
	 * @param data
	 */
	public void updatePush(String type, DataRow data) {
		// TODO Auto-generated method stub
		String sql = "UPDATE t_push SET status = "+type+" where id = " + data.getString("id");
//		getJdbcTemplate().update("t_push", data, "id", data.getString("id"));
		getJdbcTemplate().update(sql);
	}
	public void updatewxinfo(DataRow d) {
		// TODO Auto-generated method stub
		getJdbcTemplate().update("t_weixin_info", d, "w_openid", d.getString("w_openid"));
	}

}
