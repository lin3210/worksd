package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import com.fuiou.model.Qrytransreq;
import com.service.AotuSDFKService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.StringHelper;

public class SDFKUtil 
{
	
	private static Logger logger = Logger.getLogger(SDFKUtil.class);
	private static AotuSDFKService aotuSDFKService = new AotuSDFKService();
	static String url253 = "https://sms.253.com/msg/send";// Ӧ�õ�ַ
	static String un253 = "N4064842";// �˺�
	static String pw253 = "1O9AgpU3Y257ae";// ����	
	static String rd253 = "0";// �Ƿ���Ҫ״̬���棬��Ҫ1������Ҫ0
	static String ex253 = null;// ��չ��	
	
	
	//����֧��
	public static void fy_query(DataRow data) throws Exception
	{
		 logger.info("[����֧��]�����ָ��д���>>>>>>>");
		
		 String batchCurrnum =data.getString("orderid");
		 String checkTime2 =data.getString("checktime2");		
		 SimpleDateFormat fmtrq2 = new SimpleDateFormat("yyyyMMdd");
		 SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 //��ǰʱ���1��
		 Calendar calendar2 = Calendar.getInstance();    
		 calendar2.setTime(fmtrq2.parse(checkTime2)); 
         calendar2.add(Calendar.DATE, 1);

		//����֧��״̬��ѯ
    	Qrytransreq queryTrans = new Qrytransreq();
    	 queryTrans.setVer("1.0");
    	 queryTrans.setOrderno(batchCurrnum);
    	 queryTrans.setStartdt(checkTime2);
    	 queryTrans.setEnddt(fmtrq2.format(calendar2.getTime()));
    	 queryTrans.setMchntCd(FuyouPay.mchntCd);
    	 queryTrans.setBusicd("AP01");
    	 String queryTrans2 =FuyouPay.queryTrans(queryTrans);
    	 // System.out.println(queryTrans2);
    	  Document doc;   
          doc = DocumentHelper.parseText(queryTrans2);   
          Element root = doc.getRootElement();  
          Element eResponsecode = root.element("ret");
          Element eMemo= root.element("memo");
          logger.info("���ſ�״̬"+eResponsecode.getTextTrim());
          //�û� id
	     String   userid  =data.getString("userid");
	     //ͨ���û�id ������п���
	     DataRow bankInfo= aotuSDFKService.getBankInfo(userid);
	    
         DataRow withdraw = new DataRow();
		 if(eResponsecode.getTextTrim().equals("000000"))
		 {
			  Element etrans = root.element("trans");
	          Element reason= etrans.element("reason");
	          Element state= etrans.element("state");
	          Element result= etrans.element("result");
			if(reason.getTextTrim().equals("���׳ɹ�")||reason.getTextTrim().equals("�ɹ�")||reason.getTextTrim().equals(":��������ɹ�")){ 
				 withdraw.set("status", 2);
				 withdraw.set("id", data.getString("id"));
				 withdraw.set("remarkresult",result.getTextTrim() );
				 withdraw.set("checktime",new Date() );
				 aotuSDFKService.updateWithdraw(withdraw);		 
				 if(data.getString("remark").equals("�ſ�")){
					
			         //ͨ���û������Ϣ
			         DataRow jkxx =  aotuSDFKService.getUserJK(userid);
			         String xmid = jkxx.getString("id");
			         String investor_id =jkxx.getString("investor_id");
			         //����ʱ��		  
			         Date  date = new Date(); 
			         Calendar calendar = Calendar.getInstance();  
			         //��������			       
			         if(jkxx.getString("jk_date").equals("2")){
			        	 //��ǰʱ���30��
			        	  calendar.add(Calendar.DATE, 30);		        	
			         }
			         if(jkxx.getString("jk_date").equals("1")){
			        	 //��ǰʱ���15��
			        	  calendar.add(Calendar.DATE, 15);    	 
			         }
			        System.out.println("����ʱ�䣺"+sf.format(calendar.getTime()));
			         jkxx.set("fkdz_time", new Date()) ; 
			         jkxx.set("hkyq_time", sf.format(calendar.getTime())) ; 
			         jkxx.set("hk_time", sf.format(calendar.getTime())) ; 
			         jkxx.set("sfyfk", "1");
			         aotuSDFKService.updateJk(jkxx);
			         String bankcard =bankInfo.getString("cardno");
			         bankcard =bankcard.substring(bankcard.length()-4,bankcard.length());
			    	 DataRow row3 =  new DataRow();	
					  row3.set("userid", userid);
					  row3.set("title", "�ſ��֪ͨ") ;
					  row3.set("neirong" ,"�װ���"+bankInfo.getString("cardusername") +
							               "������Ľ���Ѿ��ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�");
					  row3.set("fb_time", new Date());
					 aotuSDFKService.insertUserMsg(row3);
					  String appName ="���ٴ�";
					  String userName =bankInfo.getString("username");
					  userName=userName.substring(0,3);
					   if(userName.equals("GSD")){
						   
					    	appName="���ٴ�";					    	
					    }else if(userName.equals("JBD")){	
					    	
					    	appName="��������";
					    } else if(userName.equals("SDX")){	
					    	
					    	appName="������";
					    }
					   
					  //���Ͷ���					
				     String msg253 ="��"+appName+"���װ���"+bankInfo.getString("cardusername")+ ",������Ľ���Ѿ��ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�" ;
					
				     String returnString = HttpSender.batchSend(url253, un253, pw253,bankInfo.getString("mobilephone"), msg253, rd253, ex253);  
				      
				     //��Ͷ����ת����Ϣ����ȥ20������ѣ��ӵ��������������ѣ�
					 //�õ������Ŀ ����Ϣ���						   
					 String sjsh = aotuSDFKService.getSHMoney(xmid) ;
					 String sjdz = aotuSDFKService.getSjdz(xmid);					
					 Double lx = Double.parseDouble(sjsh)-Double.parseDouble(sjdz)-20;
					
					 //��ȡͶ���˵���Ϣ					
					 DataRow investor =  aotuSDFKService.getBankInfo(investor_id);
					 DataRow investor2 =  aotuSDFKService.getInvestorInfo(investor_id);
					 String investorId = investor2.getString("id");
					 //�����û��Ŀ������
						DataRow addorder = new DataRow();
				    	int orderpaytype = 2;
				    	String remark = "Ͷ����Ϣ";
				    	String payNumber = "sd"+System.currentTimeMillis();
				     
					    //��ɶ���
					    addorder.set("userid", jkxx.getString("investor_id"));
					    addorder.set("rechargetime", new Date());
					    addorder.set("rechargetype", 22);//����ֵ
					    addorder.set("rechargemoney", lx);
					    addorder.set("result", 1);
					    addorder.set("rzcode", "0000");
					    addorder.set("paynumber", payNumber);
					    addorder.set("rechargenumber", payNumber);					   
					    addorder.set("remark", remark);
					    addorder.set("ordertype", 1);
					    addorder.set("rechargeId",jkxx.getString("id"));				  
					    addorder.set("orderpaytype", orderpaytype);			 
					    aotuSDFKService.addOrder(addorder);	
					    investor2.set("usablesum", investor2.getDouble("usablesum")+lx);
					    //������ˮ
					    double money =lx ;
					    aotuSDFKService.updateUserJLMoney2(investor2,lx,payNumber ,userid);
					    String appName2 ="���ٴ�";
					    String userName2 =investor2.getString("username");
						   if(userName2.equals("GSD")){
						    	appName2="���ٴ�";					    	
						    }else if(userName2.equals("JBD")){					    	
						    	appName2="��������";
						    } else if(userName2.equals("SDX")){
						    	
						    	appName2="������";
						    }
					    
					  String msg256 ="��"+appName2+"���װ���"+investor.getString("cardusername")+"������һ�ʽ�������"+lx+"Ԫ��ת�浽�Ƹ�����У������Ϊ"+bankInfo.getString("cardusername")+"����ע����գ�";
                      String returnString3 = HttpSender.batchSend(url253, un253, pw253,investor.getString("mobilephone"), msg256, rd253, ex253);  
					 
                     //app �����Ϣ
                     DataRow row4 =  new DataRow();	
					  row4.set("userid", investor.getString("userid"));
					  row4.set("title", "Ͷ����������") ;
					  row4.set("neirong" ,"��"+appName2+"���װ���"+investor.getString("cardusername")+"������һ�ʽ�������"+lx+"Ԫ��ת�浽�Ƹ�����У������Ϊ"+bankInfo.getString("cardusername")+"����ע����գ�");
					  row4.set("fb_time", new Date());
					 aotuSDFKService.insertUserMsg(row4); 
					 
					 
					    //ÿͶ�ʳɹ�һ�� �����˵õ�10Ԫ�Ľ��� 
						//�ж��û��Ƿ����Ƽ��ˣ�����и��Ƽ��û�����10Ԫ�Ľ���
						  String refferCount =aotuSDFKService.findReffer(investorId);
						
						  if(!refferCount.equals("")&&!refferCount.equals(null)){
							  //���ҳ��Ƽ��˻�				
							  DataRow user3 = aotuSDFKService.getRefferUser(refferCount);  
							   if(!user3.getString("id").equals(investor2.getString("id"))&&!StringHelper.isEmpty(user3.getString("id"))){//�ж��Ƽ����Ƿ�Ϊ���?�Ҵ��ڸ��û�								
								DataRow addorder2 = new DataRow();
						    	int orderpaytype2 = 2;
						    	String remark2 = "�Ƽ��û�Ͷ�ʽ���";
						    	String payNumber2 = "sd"+System.currentTimeMillis();
							    //��ɶ���
							    addorder2.set("userid", user3.getString("id"));
							    addorder2.set("rechargetime", new Date());
							    addorder2.set("rechargetype", 56);
							    addorder2.set("rechargemoney", 10);
							    addorder2.set("result", 1);
							    addorder2.set("rzcode", "0000");
							    addorder2.set("paynumber", payNumber2);
							    addorder2.set("rechargenumber", payNumber2);					 	 
							    addorder2.set("remark", remark2);
							    addorder2.set("ordertype", 1);
							    addorder2.set("orderpaytype", orderpaytype2);			 
							    aotuSDFKService.addOrder(addorder2);	
							    user3.set("usablesum",user3.getDouble("usablesum")+10);//���Ƽ��û�
							    //������ˮ
							    double money2 =10 ;
							    aotuSDFKService.updateUserJLMoney(user3, money2,payNumber2 ,investorId);							   
							   } 
							   
						  }			 					  
				 }else if(data.getString("remark").equals("����")){
					 String bankcard =bankInfo.getString("cardno");
				     bankcard =bankcard.substring(bankcard.length()-4,bankcard.length());
					 DataRow row3 =  new DataRow();	
					  row3.set("userid", userid);
					  row3.set("title", "���ֵ���֪ͨ") ;
					  row3.set("neirong" ,"�װ���"+bankInfo.getString("cardusername") +
				               "�����������"+data.getString("sum")+"�Ѿ��ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�");
					  row3.set("fb_time", new Date());
					  aotuSDFKService.insertUserMsg(row3);	
					   String userName =bankInfo.getString("username");
					    userName =userName.substring(0,3);	
					    String appName ="���ٴ�";
					    if(userName.equals("GSD")){
					    	appName="���ٴ�";					    	
					    }else if(userName.equals("JBD")){					    	
					    	appName="��������";
					    } else if(userName.equals("SDX")){
					    	
					    	appName="������";
					    }					   
					 //���Ͷ���					
				     String msg253 ="��"+appName+"���װ���"+bankInfo.getString("cardusername") +
				              "�����������"+data.getString("sum")+"�Ѿ��ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�" ;
					 String returnString = HttpSender.batchSend(url253, un253, pw253,bankInfo.getString("mobilephone"), msg253, rd253, ex253);  
					  
				 }else if(data.getString("remark").equals("Ͷ�ʻ���")){
					  //Ͷ�ʻ��� +��Ϣ ���˼��
					 String bankcard =bankInfo.getString("cardno");
					  //�����Ŀ��id �ҳ����������
					  String jkUsername = aotuSDFKService.getJKname(data.getString("versoin"));
				     bankcard =bankcard.substring(bankcard.length()-4,bankcard.length());
					 DataRow row3 =  new DataRow();	
					  row3.set("userid", userid);
					  row3.set("title", "Ͷ�ʻ��� ") ;
					  row3.set("neirong" ,"�װ���"+bankInfo.getString("cardusername") +
				               "��"+jkUsername+"�ѽ�"+data.getString("sum")+"�ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�");
					  row3.set("fb_time", new Date());
					  aotuSDFKService.insertUserMsg(row3);	
					   String userName =bankInfo.getString("username");
					    userName =userName.substring(0,3);	
					    String appName ="���ٴ�";
					    if(userName.equals("GSD")){
					    	appName="���ٴ�";					    	
					    }else if(userName.equals("JBD")){					    	
					    	appName="��������";
					    } else if(userName.equals("SDX")){
					    	
					    	appName="������";
					    }					   
					  //���Ͷ���					
				     String msg253 ="��"+appName+"���װ���"+bankInfo.getString("cardusername") +
				               "��"+jkUsername+"�ѽ�"+data.getString("sum")+"�ɹ���������β��Ϊ"+ bankcard+"�����п��ϣ���ע��������Ϣ��1-2Сʱ���ˣ�" ;
					 String returnString = HttpSender.batchSend(url253, un253, pw253,bankInfo.getString("mobilephone"), msg253, rd253, ex253);
					 
				 }
			} else {
				
				if(state.getTextTrim().equals("0")){
					 withdraw.set("status", 4);
					 withdraw.set("id", data.getString("id"));
					 withdraw.set("remarkresult",result.getTextTrim() );
					 withdraw.set("checktime",new Date() );
					 aotuSDFKService.updateWithdraw(withdraw);
				}
					
			}
		 }else if(eResponsecode.getTextTrim().equals("111167")){				 
			 withdraw.set("status", 6);
			 withdraw.set("id", data.getString("id"));
			 withdraw.set("remarkresult",eMemo.getTextTrim() );
			 aotuSDFKService.updateWithdraw(withdraw);
			 
		 }else{			 
			 withdraw.set("status", 5);
			 withdraw.set("id", data.getString("id"));
			 withdraw.set("remarkresult",eMemo.getTextTrim() );
			 aotuSDFKService.updateWithdraw(withdraw); 
		 }
       
		 
		
	}
	
	
	public static void main(String[] args) throws DocumentException {
//<?xml version="1.0" encoding="UTF-8" standalone="yes"?><qrytransrsp><ret>000000</ret><memo>�ɹ�</memo><trans><merdt>20170405</merdt><orderno>sd1491374576833</orderno><accntno>6228481098930864676</accntno><accntnm>�����</accntnm><amt>68000</amt><entseq></entseq><memo></memo><state>1</state><result>�����ʽ����Ѹ���,�����ѷ���</result><reason>���׳ɹ�</reason></trans></qrytransrsp>
		  String userName ="JBD2";
		  userName=userName.substring(0,3);
		  System.out.println(userName);
       
	} 
		
}
