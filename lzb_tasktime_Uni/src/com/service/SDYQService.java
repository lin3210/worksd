package com.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.DateHelper;

public class SDYQService extends BaseService {

	private static Logger logger = Logger.getLogger(DealmakingService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		
		return getJdbcTemplate("web");
	}
	/***
	 * ������ڼ���
	 */
	public void yqjs()
	{
		logger.info("������Ϣ����");
		JdbcTemplate template = new JdbcTemplate();
	    Date  investTime = new Date();		
	    String curdate = DateHelper.formatDate(investTime, "yyyyMMdd");	
	    
		//String sql = "";
		//String id = "select MIN(id) as idmin,MAX(id) as maxid from t_current_invest where DATE_FORMAT(DATE_ADD(investTime,INTERVAL hasDeadline month),'%Y%m%d') = '"+curdate+"' and flag = 0";
		//DataRow numData = template.queryMap(id);
		//��ǰʱ���һ��  �� ���ڼ�Ϣʱ�� ���ĵڶ���Ŀ�ʼ��Ϣ��
	    Calendar calendar  =Calendar.getInstance() ; 
	    //calendar.add(investTime);
	   
	    
	}
	
	  public void insertUserMsg(DataRow row)
	  	{
	  		getJdbcTemplate().insert(" sd_msg", row);
	  	}
	
	
	/**
	 * ��ȡ�ſ�ɹ���δ����Ľ���¼
	 */
	public List<DataRow> getAllYQCxList()
	{
		//��ȡ�ſ�ɹ���δ����Ľ���¼
		String sql = "select s.userid ,s.id,s.yqannualrate , s.sjsh_money ,s.tzjx,s.tzjx_lx,s.tzjx_ts, s.hkyq_time ,s.hkfq_code ,s.hkfq_time ,s.yuq_lx ," +
				" s.yuq_ts  , u.mobilePhone ,u.username ,b.cardUserName  from " +
				" sd_new_jkyx s  left join sd_user u on s.userid = u.id left join " +
				" sd_bankcard b on b.userid =s.userid  where s.sfyhw = 0  and  s.sfyfk =1 ";
		return getJdbcTemplate().query(sql);
	}
	/**
	 * ���½����Ϣ
	 * 
	 */
  	public void updateJKinfo(DataRow jkInfo)
	{
	
		getJdbcTemplate().update("sd_new_jkyx", jkInfo, "id", jkInfo.getInt("id"));
	}
	
	/**
	 * �����Է���
	 * author:����
	 */
	public static void main(String[] args) {
		//��ȡ��ǰʱ���һ��
		Date  data = new  Date() ;
		Calendar calendar =  Calendar.getInstance();

		//�����ڸ�ʽ��
		 String curdate = DateHelper.formatDate(calendar.getTime(), "yyyyMMdd");
		 System.out.println(curdate) ;
		 
		//SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("")
	}
	
	/**
	 */
	public List<DataRow>   getcuishoufendanid(String jkid,String cuishouid) {
		 String sql =" SELECT MAX(id) AS id ,fendan_cs ,cuishou_jine FROM sd_cuishou_fendan  WHERE  jk_id= "+jkid+"  AND cuishou_id = "+ cuishouid+"  GROUP BY jk_id,cuishou_id ";
		 
		 return getJdbcTemplate().query(sql);
	 }
	
/*************************start 更新分单表中的利息***************************/

	/**获取分单表信息
	 */
	public List<DataRow>   getcuishoufendanList(String jkid,String cuishouid) {
		String sql = " SELECT id ,fendan_cs ,cuishou_jine  FROM sd_cuishou_fendan WHERE id IN  ( SELECT MAX(a.id) AS id  FROM sd_cuishou_fendan a WHERE  a.jk_id="+jkid+" AND a.cuishou_id = "+ cuishouid+" )";
		 return getJdbcTemplate().query(sql);
	 }

	/**
	 * 获取借款信息
	 * @param jkid
	 * @return
	 */
	public DataRow  getjkIDataRow(String jkid) {
		String sql = " SELECT * FROM sd_new_jkyx WHERE id ="+jkid;
		
		return getJdbcTemplate().queryMap(sql);
	}

	/**
 * 更新分单表信息
 */
	public void updatecuishoufendaninfo(DataRow fdinfo)
	{
	
		getJdbcTemplate().update("sd_cuishou_fendan", fdinfo, "id", fdinfo.getInt("id"));
	}
	
/*************************end 更新分单表中的利息***************************/
	
}
