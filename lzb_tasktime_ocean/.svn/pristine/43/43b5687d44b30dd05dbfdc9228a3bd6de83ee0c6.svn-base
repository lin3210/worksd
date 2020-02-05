package com.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class WxUpdateService  extends BaseService
{

    private static Logger logger = Logger.getLogger(WxUpdateService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
	/**
	 * 获取微信用户
	 * @return
	 */
	public List<DataRow> getWxUserList()
	{
		String sql = "select id,w_openid from t_weixin_info";
		return getJdbcTemplate().query(sql);
	}
	
	/**
	 * 批量更新微信用户
	 */
	public void UpdateWxByList(List<DataRow> list)
	{
		Session session = getSession();
		try {
			session.beginTrans();
			int i = 0;
			for (DataRow dataRow : list) 
			{
				session.update("t_weixin_info", dataRow, "id", dataRow.getInt("id"));
				i++;
				if(i%10==0)
				{
					session.commitTrans();
				}
			}
			session.commitTrans();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			session.rollbackTrans();
		}finally{session.close();}
		
	}
}
