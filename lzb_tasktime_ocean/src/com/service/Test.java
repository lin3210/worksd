package com.service;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.connection.Configure;
import com.thinkive.base.util.DateHelper;

/**
 * 添加测试数据
 * @author Administrator
 *
 */
public class Test 
{
	/**
	 * 添加测试记录
	 */
	public void insert()
	{
		Configure.getInstance();
		JdbcTemplate template = new JdbcTemplate();
		List<DataRow> list = template.query("select * from t_current where id = 1");//012
		List<DataRow> ulist = template.query("select * from t_user where id >= 8000");//012
		for (int i = 1200; i <= 1400; i++) 
		{
			int ran = new Random().nextInt(2);
			
			DataRow user = ulist.get(new Random().nextInt(200));
			int tzrid = user.getInt("id");
			if(tzrid!=0)
			{
				int  usablesum = (int)user.getDouble("usablesum");
				int je = new Random().nextInt(usablesum);
				if(je==0)
				{
					je = 3000;
				}
				if(usablesum-je>0)
				{
					DataRow dataRow = list.get(0);
					DataRow row = new DataRow();
					row.set("id", i);
					row.set("investamount", je);
					row.set("monthRate", dataRow.getDouble("nlv"));
					row.set("investor", tzrid);
					row.set("currentId", dataRow.getInt("id"));
					int datei = new Random().nextInt(30)+1;
					Date  investTime = DateHelper.getDataDiff(new Date(),datei);
					row.set("investtime", investTime);
					row.set("deadline", dataRow.getInt("period_month"));
					row.set("recivedprincipal", je);
					Double double1 = (dataRow.getDouble("nlv")/100/12)*je;
					row.set("recievedInterest", double1);
					Date repayDate = DateHelper.getDataDiff(new Date(),datei);
					repayDate.setMonth(repayDate.getMonth()+dataRow.getInt("period_month"));
					row.set("repayDate", repayDate);
					template.insert("t_current_invest", row);
					//更新用户
					user.set("usablesum", usablesum - je);
					double freezesum = je+user.getDouble("freezesum");
					user.set("freezesum", freezesum);
					template.update("t_user", user, "id", user.getInt("id"));
					DataRow userdata = new JdbcTemplate().queryMap("select * from t_user where id = "+tzrid);
					DataRow fundrecord = new DataRow();//添加资金流向
	            	fundrecord.set("userid", tzrid);
	            	fundrecord.set("fundmode", "活期随存随取");
	            	fundrecord.set("handlesum", je);
	            	fundrecord.set("usablesum", userdata.getDouble("usablesum"));
	            	fundrecord.set("freezesum", userdata.getDouble("freezesum"));
	            	fundrecord.set("dueinsum", 0);
	            	fundrecord.set("recordtime", new Date());
	            	fundrecord.set("operatetype", 103);
	            	fundrecord.set("spending", je);
	            	fundrecord.set("borrow_id", i);
	            	template.insert("t_fundrecord", fundrecord);
				}
			}
			
		}
	}
	
	/**
	 * 添加测试记录
	 */
	public void insertHq()
	{
		Configure.getInstance();
		JdbcTemplate template = new JdbcTemplate();
		List<DataRow> list = template.query("select * from t_current where id = 1");//012
		List<DataRow> ulist = template.query("select * from t_user where id >= 8000");//012
		for (int i = 1200; i <= 1400; i++) 
		{
			int ran = new Random().nextInt(2);
			
			DataRow user = ulist.get(new Random().nextInt(200));
			int tzrid = user.getInt("id");
			if(tzrid!=0)
			{
				int  usablesum = (int)user.getDouble("usablesum");
				int je = new Random().nextInt(usablesum);
				if(je==0)
				{
					je = 3000;
				}
				if(usablesum-je>0)
				{
					DataRow dataRow = list.get(0);
					DataRow row = new DataRow();
					row.set("id", i);
					row.set("userid", tzrid);
					row.set("money", je);
					row.set("currentid", dataRow.getInt("id"));
					row.set("investtime", new Date());
					row.set("status", 1);
					template.insert("t_current_scsq_user", row);
					DataRow fundrecord = new DataRow();//添加资金流向
	            	fundrecord.set("userid", tzrid);
	            	fundrecord.set("fundmode", "活期转入金额");
	            	fundrecord.set("fundcode", 104);
	            	fundrecord.set("handlesum", je);
	            	fundrecord.set("usablesum", je);
	            	fundrecord.set("dqlv", dataRow.getDouble("nlv"));
	            	fundrecord.set("czdate", new Date());
	            	template.insert("t_current_scsq_list", fundrecord);
				}
			}
			
		}
	}
	
	/**
	 * 添加测试记录
	 */
	public void insertUser()
	{
		Configure.getInstance();
		JdbcTemplate template = new JdbcTemplate();
		for (int i = 8000; i <= 8200; i++) 
		{
			int r1 = new Random().nextInt(4955)+5;
			int r2 = new Random().nextInt(3000);
			int mon = new Random().nextInt(50000)+50000;
			DataRow row = new DataRow();
			row.set("id", i);
			row.set("email", "59"+r1+"7"+r2+"@qq.com");
			row.set("username", "lctest"+i);
			row.set("password", "7dc9d4ea59794c6351818c075b2543ac");
			row.set("dealpwd", "7dc9d4ea59794c6351818c075b2543ac");
			row.set("mobilePhone", 186+"4"+i+"123");
			row.set("createTime", new Date());
			row.set("usableSum", mon);
			row.set("isAdmin", 1);
			template.insert("t_user", row);
		}
	}
	
	public static void main(String[] args) {
//		new Test().insertUser();
		new Test().insertHq();
		
	}
}
