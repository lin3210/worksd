package com.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;
import com.thinkive.base.util.DateHelper;
import com.thinkive.base.util.StringHelper;
import com.util.SendMsg;

/**
 * �����Ϣ����
 * @author Administrator
 *
 */
public class DealmakingService extends BaseService
{
	private static Logger logger = Logger.getLogger(DealmakingService.class);
	public static String code1Str = "���ڻ�����Ϣ";
	public static String code1 = "100";
	public static String code2Str = "��Ʒ��ڸ�Ϣ";
	public static String code2 = "101";
	public static String code3Str = "�����ȡ��Ϣ";
	public static String code3 = "103";
	
	
	public static void main(String[] args) 
	{	
		new DealmakingService().chdc();
//		new DealmakingService().chhq();
	}
	/***
	 * ��϶��������Ϣ����
	 */
	public void chdc()
	{
		logger.info("������Ϣ����");
		JdbcTemplate template = new JdbcTemplate();
	    Date  investTime = new Date();		
	    String curdate = DateHelper.formatDate(investTime, "yyyyMMdd");		
		String sql = "";
		String id = "select MIN(id) as idmin,MAX(id) as maxid from t_current_invest where DATE_FORMAT(DATE_ADD(investTime,INTERVAL hasDeadline month),'%Y%m%d') = '"+curdate+"' and flag = 0";
		DataRow numData = template.queryMap(id);
		if(numData!=null)
		{
			
			int idmin = numData.getInt("idmin");
			int maxid = numData.getInt("maxid");
			int tempnum = idmin+100;
			sql = getDcSql(curdate,idmin,tempnum);
			logger.info(tempnum+"-----"+maxid);
			List<DataRow> list = template.query(sql);
			logger.info("list.size:"+list.size());
			while(tempnum<maxid||list.size()>0)
			{
				logger.info("������Ϣ�������~~~");
				updateLx(list,curdate,template);
				sql = getDcSql(curdate,tempnum,tempnum=tempnum+100);
				list = template.query(sql);
				logger.info("list.size:"+list.size());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * ��ȡ����SQL
	 * @param curdate
	 * @param idmin
	 * @param max
	 * @return
	 */
	public String getDcSql(String curdate,int idmin,int max)
	{
		logger.info(idmin+"----"+max);
		return "select * from t_current_invest where DATE_FORMAT(DATE_ADD(investTime,INTERVAL hasDeadline month),'%Y%m%d') = '"+curdate+"' and flag = 0 and id >= "+idmin+" and id <= "+max;
	}
	
	/**
	 * ��ȡ���������ȡ
	 * @param curdate
	 * @param idmin
	 * @param max
	 * @return
	 */
	public String getHqSql(String nlv,int idmin,int max)
	{
		logger.info(idmin+"----"+max);
		return "select a.userid,a.money,(a.lx+a.money) bj,a.lx,a.sxdate,a.investtime from (select userid,money,ROUND((money*"+nlv+"/100/365),2) lx,DATE_FORMAT(sxdate,'%Y%m%d') as sxdate,DATE_FORMAT(investtime,'%Y%m%d') as investtime   from t_current_scsq_user  where id >= "+idmin+" and id <= "+max+" ) a";
	}
	
	/**
	 * ���м�����Ϣ
	 * @param list
	 * @param repayDate
	 */
	public void updateLx(List<DataRow> list,String repayDate,JdbcTemplate template)
	{
		for (DataRow dataRow : list) 
		{
			//��ȡ��ǰ���������Ƿ��Ƿ�����Ϣת������У�����Ǽ��㵱����Ϣ���޸��ѻ�������
			//�����һ���µ�������Ϣ���������Ϣ���뵽����
			//����Ƕ��ڷ��ڷ�����Ϣ������㵱����Ϣ���޸��ѻ�������
			//�����ڵ��ں�������Ϣ�󽫱�������һ����Ϣ���뵽����
			String borrow_id = dataRow.getString("id");
			//���Ͷ��id��ȡͶ���û������� ��Ͷ����Ŀ��� �ֻ�� 
			String  realName = template.queryString("select cardUserName  from t_bankcard  left join  t_current_invest  on t_bankcard.userId  =   t_current_invest.investor where  t_current_invest.id= "+borrow_id);
		    String  productName = template.queryString("select t_current.name from  t_current_invest  left join t_current on t_current_invest.currentId = t_current.id  where   t_current_invest.id = "+borrow_id);	
			String  mobilePhone = template.queryString("select t_user.mobilePhone from  t_current_invest  left join t_user on t_current_invest.investor  = t_user.id  where   t_current_invest.id =" +borrow_id);
		    logger.info("��ʵ����"+realName +"��Ʒ��ƣ�"+productName+"Ͷ���˵绰���룺"+mobilePhone);
		    if(dataRow.getInt("deadline")==1)
			{
		    	
				logger.info("1����ƽ�����Ϣ");
                try 
                {
                	dataRow.set("haspi", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));//���ձ�Ϣ
    				dataRow.set("hasdeadline", 1);//���ձ�Ϣ
    				dataRow.set("hasprincipal", dataRow.getDouble("recivedprincipal"));//���ձ���
    				dataRow.set("hasinterest", dataRow.getDouble("recievedinterest"));//������Ϣ
    				dataRow.set("repaystatus", 2);//�Ƿ���
    				dataRow.set("flag", 1);//�ѽ������
    				dataRow.set("sxdate", new Date());//��Ϣ����
                	//�������״̬
                	template.update("t_current_invest", dataRow, "id", dataRow.getInt("id"));
                	
                	//�û��ʽ𷵻�
                	logger.info("�û��ʽ𷵻�");
                	updateUserMoney(dataRow,template);
                	SendMsg.tplSendSms(1342213,"#name#="+realName+"&#product#="+productName,mobilePhone);
                
                	
                } catch (Exception e) {
                	DataRow log = new DataRow();
                	log.set("name", "�û���ƽ�����Ϣ");
                	log.set("userid", dataRow.getString("investor"));
                	log.set("xg_id", dataRow.getString("id"));
                	log.set("xg_table", "t_current_invest");
                	log.set("time", new Date());
                	log.set("cwms", e.toString());
                	template.insert("t_current_log", log);
                	e.printStackTrace();
					logger.error(e);
				}
                try {
                	DataRow fundrecord = new DataRow();//����ʽ�����
                	fundrecord.set("userId", dataRow.getString("investor"));
                	fundrecord.set("fundmode", code1Str);
                	fundrecord.set("handleSum", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));
                	fundrecord.set("usableSum", 0);
                	fundrecord.set("freezeSum", 0);
                	fundrecord.set("dueinSum", 0);
                	fundrecord.set("recordTime", new Date());
                	fundrecord.set("operatetype", code1);
                	fundrecord.set("income", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));
                	fundrecord.set("borrow_id", borrow_id);
                	template.insert("t_fundrecord", fundrecord);
				} catch (Exception e) {
					DataRow log = new DataRow();
                	log.set("name", "�û�����ʽ�����");
                	log.set("userid", dataRow.getString("investor"));
                	log.set("xg_id", -1);
                	log.set("xg_table", "t_fundrecord");
                	log.set("time", new Date());
                	log.set("cwms", e.toString());
                	template.insert("t_current_log", log);
                	e.printStackTrace();
					logger.error(e);
				}
				//����΢��֪ͨ
				
				
				
			}
			else
			{
				//���з��ڷ�����Ϣ
				if(dataRow.getInt("isautobid")==1)
				{
					
					double deadline = dataRow.getDouble("deadline");//����
					logger.info(deadline+"����Ʒ��ڽ�����Ϣ");
					double recievedInterest = dataRow.getDouble("recievedinterest");//��Ϣ
					DecimalFormat df = new DecimalFormat("#.##");
					double d = recievedInterest/deadline;
					String hasInterest = df.format(d);
					double  hasinterest = dataRow.getDouble("hasinterest");
					String type = "";
					try 
					{
	    				dataRow.set("hasinterest", dataRow.getDouble("hasinterest")+Double.valueOf(hasInterest));//������Ϣ
	    				logger.info(dataRow.getString("deadline")+"??????????????"+dataRow.getString("hasdeadline"));
//	    				if((hasinterest+Double.valueOf(hasInterest))>=recievedInterest)
	    				if(dataRow.getString("deadline").equals(dataRow.getString("hasdeadline") ))
	    				{
	    					dataRow.set("repaystatus", 2);//�Ƿ���
	        				dataRow.set("flag", 1);//�ѽ������
	        				//���𽫹黹
	        				dataRow.set("hasprincipal", dataRow.getDouble("recivedprincipal"));
	        				type = "2";
	        				SendMsg.tplSendSms(1342213,"#name#="+realName+"&#product#="+productName,mobilePhone);
	    				}
	    				else
	    				{
	    					dataRow.set("hasdeadline", dataRow.getInt("hasdeadline")+1);//������Ϣ����
	    					type = "1";
	    				}
	    				dataRow.set("sxdate", new Date());//��Ϣ����
	                	//�������״̬
	                	template.update("t_current_invest", dataRow, "id", dataRow.getInt("id"));
	                	
	                	//�û��ʽ𷵻�
	                	logger.info("�û��ʽ𷵻�");
	                	updateUserMoneyLx(dataRow, template, type , hasInterest,realName,productName,mobilePhone);
					} catch (Exception e) {
						DataRow log = new DataRow();
	                	log.set("name", "�û���Ʒ��ڽ�����Ϣ");
	                	log.set("userid", dataRow.getString("investor"));
	                	log.set("xg_id", dataRow.getString("id"));
	                	log.set("xg_table", "t_current_invest");
	                	log.set("time", new Date());
	                	log.set("cwms", e.toString());
	                	template.insert("t_current_log", log);
	                	e.printStackTrace();
						logger.error(e);
					}
					try {
						DataRow fundrecord = new DataRow();//����ʽ�����
	                	fundrecord.set("userId", dataRow.getString("investor"));
	                	fundrecord.set("fundmode", code2Str);
	                	fundrecord.set("handleSum", hasInterest);
	                	fundrecord.set("usableSum", 0);
	                	fundrecord.set("freezeSum", 0);
	                	fundrecord.set("dueinSum", 0);
	                	fundrecord.set("recordTime", new Date());
	                	fundrecord.set("operatetype", code2);
	                	fundrecord.set("income", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));
	                	fundrecord.set("borrow_id", borrow_id);
	                	template.insert("t_fundrecord", fundrecord);
					} catch (Exception e) {
						DataRow log = new DataRow();
	                	log.set("name", "�û�����ʽ�����");
	                	log.set("userid", dataRow.getString("investor"));
	                	log.set("xg_id", -1);
	                	log.set("xg_table", "t_fundrecord");
	                	log.set("time", new Date());
	                	log.set("cwms", e.toString());
	                	template.insert("t_current_log", log);
	                	e.printStackTrace();
						logger.error(e);
					}
				}
				//�����Ƿ��ѵ�һ���Ի���Ϣ�Ľ�������
				else
				{
				   String tempdate = DateHelper.formatDate(DateHelper.parseString(dataRow.getString("repaydate")), "yyyyMMdd");
				   if(repayDate.equals(tempdate))
				   {
					   try 
		                {
						    logger.info(dataRow.getDouble("deadline")+"����ƻ�����Ϣ������Ϣ");
		                	dataRow.set("haspi", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));//���ձ�Ϣ
		    				dataRow.set("hasdeadline", dataRow.getInt("hasdeadline"));//���ձ�Ϣ
		    				dataRow.set("hasprincipal", dataRow.getDouble("recivedprincipal"));//���ձ���
		    				dataRow.set("hasinterest", dataRow.getDouble("recievedinterest"));//������Ϣ
		    				dataRow.set("repaystatus", 2);//�Ƿ���
		    				dataRow.set("flag", 1);//�ѽ������
		    				dataRow.set("sxdate", new Date());//��Ϣ����
		                	//�������״̬
		                	template.update("t_current_invest", dataRow, "id", dataRow.getInt("id"));
		                	
		                	//�û��ʽ𷵻�
		                	logger.info("�û��ʽ𷵻�");
		                	updateUserMoney(dataRow, template);
		                	SendMsg.tplSendSms(1342213,"#name#="+realName+"&#product#="+productName,mobilePhone);
		                } catch (Exception e) {
		                	DataRow log = new DataRow();
		                	log.set("name", "�û���ƽ�����Ϣ");
		                	log.set("userid", dataRow.getString("investor"));
		                	log.set("xg_id", dataRow.getString("id"));
		                	log.set("xg_table", "t_current_invest");
		                	log.set("time", new Date());
		                	log.set("cwms", e.toString());
		                	template.insert("t_current_log", log);
		                	e.printStackTrace();
							logger.error(e);
						}
					   try {
		                	DataRow fundrecord = new DataRow();//����ʽ�����
		                	fundrecord.set("userId", dataRow.getString("investor"));
		                	fundrecord.set("fundmode", code1Str);
		                	fundrecord.set("handleSum", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));
		                	fundrecord.set("usableSum", 0);
		                	fundrecord.set("freezeSum", 0);
		                	fundrecord.set("dueinSum", 0);
		                	fundrecord.set("recordTime", new Date());
		                	fundrecord.set("operatetype", code1);
		                	fundrecord.set("income", dataRow.getDouble("recivedprincipal")+dataRow.getDouble("recievedinterest"));
		                	fundrecord.set("borrow_id", borrow_id);
		                	template.insert("t_fundrecord", fundrecord);
						} catch (Exception e) {
							DataRow log = new DataRow();
		                	log.set("name", "�û�����ʽ�����");
		                	log.set("userid", dataRow.getString("investor"));
		                	log.set("xg_id", -1);
		                	log.set("xg_table", "t_fundrecord");
		                	log.set("time", new Date());
		                	log.set("cwms", e.toString());
		                	template.insert("t_current_log", log);
		                	e.printStackTrace();
							logger.error(e);
						}
				   }
				   else
				   {
					   //һ�λ�����Ϣ��������
					   dataRow.set("hasdeadline", dataRow.getInt("hasdeadline")+1);//������Ϣ����
					   template.update("t_current_invest", dataRow, "id", dataRow.getInt("id"));
				   }
				}
			}
		}
	}
	
	/**
	 * �����û��ʽ�
	 */
	public void updateUserMoney(DataRow row,JdbcTemplate template)
	{
		DataRow user = template.queryMap("select  * from t_user where id = "+row.getString("investor"));
		user.set("usablesum", user.getDouble("usablesum")+row.getDouble("investamount")+row.getDouble("recievedinterest"));
		user.set("freezesum", user.getDouble("freezesum")-row.getDouble("investamount"));
		template.update("t_user", user, "id", user.getString("id"));
	}

	/**
	 * �����û��ʽ�(������Ϣ)
	 * @throws IOException 
	 */
	public void updateUserMoneyLx(DataRow row,JdbcTemplate template,String type,String hasInterest,String realName ,String productName ,String mobilePhone ) throws IOException
	{
		
		DataRow user = template.queryMap("select  * from t_user where id = "+row.getString("investor"));
		String money ="";
		if(type.equals("1"))//ֻ������Ϣ
		{
			user.set("usablesum", user.getDouble("usablesum")+Double.valueOf(hasInterest));
			money = Double.valueOf(hasInterest)+"";
		}
		else if(type.equals("2"))//�������һ����Ϣ�ӱ���
		{
			user.set("usablesum", user.getDouble("usablesum")+Double.valueOf(hasInterest)+row.getDouble("investamount"));
			user.set("freezesum", user.getDouble("freezesum")-row.getDouble("investamount"));
			money =Double.valueOf(hasInterest)+"";
		}
		template.update("t_user", user, "id", user.getString("id"));
		if(money.length()>money.indexOf(".")+3 &&money.indexOf(".")!=-1){
		     money = money.substring(0,money.indexOf(".")+3);
		 }
		SendMsg.tplSendSms(1524196,"#name#="+realName+"&#product#="+productName+"&#money#="+money,mobilePhone); 
	}
	
	/**
	 * ���������ȡ������Ϣ
	 * @param list
	 * @param repayDate
	 */
	public void updateHqLx(List<DataRow> list,String repayDate,String nlv)
	{
		Session session = getSession();
		session.beginTrans();
		try {
			for (DataRow dataRow : list) 
			{
				String investtime = dataRow.getString("investtime");
				//���Ϊ�����ǵ�һ��Ҫ��Ϣ
				if(StringHelper.isEmpty(dataRow.getString("sxdate")))
				{
					if(investtime.equals(repayDate))
					{
						double money = dataRow.getDouble("money");
						DataRow hquser = new DataRow();
						hquser.set("money", dataRow.getDouble("bj"));
						hquser.set("sxdate", repayDate);//��Ϣ����
						session.update("t_current_scsq_user", hquser, "userid", dataRow.getInt("userid"));
						DataRow fundrecord = new DataRow();//����ʽ�����
			        	fundrecord.set("userid", dataRow.getInt("userid"));
			        	fundrecord.set("fundmode", "������Ϣ����");
			        	fundrecord.set("fundcode", 105);
			        	fundrecord.set("handlesum", dataRow.getDouble("lx"));
			        	fundrecord.set("usablesum", money);
			        	fundrecord.set("dqlv", nlv);
			        	fundrecord.set("czdate", repayDate);
			        	session.insert("t_current_scsq_list", fundrecord);
					}
					else
					{
						logger.info("�û���һ�ν��㲻�ڵ�ǰ��Ϣ����");
					}
				}
				//��ǰ���ڲ�������Ϣ���������
				else 
				{
					if(!repayDate.equals(dataRow.getString("sxdate")))
					{
						double money = dataRow.getDouble("money");
						DataRow hquser = new DataRow();
						hquser.set("money", dataRow.getDouble("bj"));
						hquser.set("sxdate", repayDate);//��Ϣ����
						session.update("t_current_scsq_user", hquser, "userid", dataRow.getInt("userid"));
						DataRow fundrecord = new DataRow();//����ʽ�����
			        	fundrecord.set("userid", dataRow.getInt("userid"));
			        	fundrecord.set("fundmode", "������Ϣ����");
			        	fundrecord.set("fundcode", 105);
			        	fundrecord.set("handlesum", dataRow.getDouble("lx"));
			        	fundrecord.set("usablesum", money);
			        	fundrecord.set("dqlv", nlv);
			        	fundrecord.set("czdate", repayDate);
			        	session.insert("t_current_scsq_list", fundrecord);
					}
		        	else
					{
						logger.info("���û��Ѽ������Ϣ��"+dataRow);
					}
				}
				
			}
			session.commitTrans();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.rollbackTrans();
			logger.error(e);
		}finally{session.close();}
		
	}
	
	/**
	 * ��ϻ���
	 */
	public void chhq()
	{
		/*try {
			JdbcTemplate template = new JdbcTemplate();
			//String curdate = "2015-03-20 10:32:51";//
			//Date date = DateHelper.parseString(new Date());
			
			String sxdate = DateHelper.formatDate(DateHelper.getDataDiff(new Date(), 1), "yyyyMMdd");//DateHelper.formatDate(new Date(),"yyyyMMdd");
			logger.info("sxdate:"+sxdate);
//			sxdate = "20150929";
			String id = "select MIN(id) as idmin,MAX(id) as maxid from t_current_scsq_user";
			DataRow numData = template.queryMap(id);
	//		if(StringHelper.isNotEmpty(isjx))
	//		{
	//			logger.info(sxdate+"������Ϣ�Ѽ��㲻��Ҫ�ٴμ���");
	//			return;
	//		}
			if(numData!=null)
			{
				int idmin = numData.getInt("idmin");
				int maxid = numData.getInt("maxid");
				int tempnum = idmin+100;
				String nlv = template.queryString("select nlv from t_current_nlv_list where DATE_FORMAT(time,'%Y%m%d') = '"+sxdate+"'");
				String sql = getHqSql(nlv,idmin,tempnum=tempnum+100);
				List<DataRow> list = template.query(sql);
				logger.info("������Ϣ�����ҳlist.size:"+list.size());
				int count = 0;
				while(tempnum<maxid||list.size()>0)
				{
					logger.info("���»������~~~");
					updateHqLx(list,sxdate,nlv);
					sql = getHqSql(nlv,tempnum,tempnum=tempnum+100);
					list = template.query(sql);
					logger.info("list.size:"+list.size());
					Thread.sleep(5000);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}*/
	}
}
