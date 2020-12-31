package com.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.SDYQService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

public class SDYQTask implements Task  {

	private static Logger logger = Logger.getLogger(SDYQTask.class);
	private static SDYQService service = new SDYQService();
	static String url253 = "https://sms.253.com/msg/send";// 应用地址
	static String un253 = "N5186211";// 账号
	static String pw253 = "fA5nv7cI9";// 密码	
	static String rd253 = "0";// 是否需要状态报告，需要1，不需要0
	static String ex253 = null;// 扩展码	
	
	/**
	 * 逾期利息计算 (和短信提醒 ）
	 */
	@Override
	public void execute() 
	{
		logger.info("开始执行逾期计算："+new Date().toLocaleString());
		//提现需要查询的数据
		 List<DataRow> list = service.getAllYQCxList();//获取所有
		 //获取当前 +3天的时间
		 SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		 SimpleDateFormat fmtrq2 = new SimpleDateFormat("dd-MM-yyyy");
		 DecimalFormat famt = new DecimalFormat("###,###");
		 Calendar calendar = Calendar.getInstance();  
		 calendar.add(Calendar.DATE, 3);	
		 Calendar calendar2 = Calendar.getInstance(); 	
		 Date nowDate = new Date() ;
		 String jkyq_time ;//还款截止时间
		 int yqannualrate ;//逾期利率
		 String sjsh_money ;//借款金额
		 String yuq_lx ; //逾期利息
		 String tzjx_lx ; //停止计息利息
		 int sjsh =0;
		 int yuq =0;
		 int tzjxlx =0;
		 int tzjx =0;
		 int zjine = 0;
		 String zje;
		 String mobilePhone ; //手机号
		 String cardUserName ; //真实姓名 
		 int yuq_ts ;//逾期天数
		 int tzjx_ts ;//停止计息天数
		 String jkid ; //借款id 
		 String userName ; //用户名
		 String appName ="OCEAN" ; //APP名字
		 String userId ; //用户id
		 int threenum = 0;
		 int dtnum = 0;
		 
		 for (DataRow dataRow : list) 			 
				{
					try {
						//计算逾期利息			
						if(dataRow.getInt("hkfq_code") == 1){
							jkyq_time = dataRow.getString("hkfq_time");
						}else{
							jkyq_time =dataRow.getString("hkyq_time");
						}
					    yqannualrate =dataRow.getInt("yqannualrate");
						sjsh_money =dataRow.getString("sjsh_money").replace(",", "");
						yuq_lx =dataRow.getString("yuq_lx").replace(",", "");
						tzjx_lx =dataRow.getString("tzjx_lx").replace(",", "");
						sjsh = Integer.parseInt(sjsh_money);
						yuq = Integer.parseInt(yuq_lx);
						tzjxlx = Integer.parseInt(tzjx_lx);
						zjine = sjsh + yuq ;
						zje = famt.format(zjine);
						yuq_ts =dataRow.getInt("yuq_ts");
						tzjx_ts =dataRow.getInt("tzjx_ts");
						tzjx =dataRow.getInt("tzjx");
						mobilePhone =dataRow.getString("mobilephone") ; //手机号
					    cardUserName =dataRow.getString("cardusername") ; //真实姓名 	
					    jkid =dataRow.getString("id");
					    userId =dataRow.getString("userid");
					    Date date_01 = fmtrq.parse(jkyq_time);
					    //用户名
					    userName =dataRow.getString("username");
					    userName =userName.substring(0,4);					  
					    if(userName.equals("OCEAN")){
					    	appName="OCEAN";					    	
					    }
					   
						//截取截止还款时间的年月日
						jkyq_time = jkyq_time.substring(0,10);
						   String st=fmtrq2.format(calendar.getTime());
						//提前三天提醒用户还款
						if(jkyq_time.equals(st)){
							//String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\"Chao! Vui long tt khoan vay "+zje+", ngay dao han "+jkyq_time+". Vui long mo app Mofa xem thong tin ck, noi dung ck la: Ho ten, so CMND/CCCD tra vay. Mofa khuyen khich ban thanh toan dung han de tang han muc lan vay sau. Chi tiet vui long lien he http://bit.ly/2QJAh16, hotline 02862769688.\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
							 //短信提醒用户去还款
							//xiong2-20170717-提前三天提醒客户到期时间和金额
							String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\"Chao! Vui long tt khoan vay "+zje+", ngay dao han "+jkyq_time+". Vui long mo app Mofa xem thong tin ck, noi dung ck la: Ho ten, so CMND/CCCD tra vay. Mofa khuyen khich ban thanh toan dung han de tang han muc lan vay sau. Chi tiet vui long lien he  http://bit.ly/2Lj6dao, hotline  0967045871.\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
							String con = URLEncoder.encode(content, "utf-8");
							SendMsg sendMsg = new SendMsg();
							//String returnString = SendMsg.sendMessageByGet(con);	
							/*String msg253 ="【"+appName+"】亲爱的"+cardUserName+"，您的借款将于"+jkyq_time+"到期，还款金额为"+(sjsh_money+yuq_lx)+"元，请及时登录"+appName+"APP还款，谢谢!"; 
							String returnString = com.util.HttpSender.batchSend(url253, un253, pw253, mobilePhone, msg253, rd253, ex253);						   						   */
						     //手机APP 生成提示消息
							 DataRow row3 =  new DataRow();	
							  row3.set("userid", userId);
							  row3.set("title", "Trả nợ nhắc nhở") ;
							  row3.set("neirong" ,"【"+appName+"】Dear "+cardUserName+"，Khoản vay của bạn sẽ hết hạn vào ngày "+jkyq_time+"，số tiền phải trả là "+zje+" VNĐ, vui lòng đăng nhập App "+appName+" để trả nợ. Cảm ơn bạn!");
							  row3.set("fb_time", fmtrq.format(new Date()));
							  service.insertUserMsg(row3);
							  threenum ++ ;
							  //xiong2-20190717-提前三天提醒客户到期时间和金额
							  /*
							  SendFTP sendFTP = new SendFTP();
							  try {
								  //sendFTP.sendMessageFTP(""+appName+" chao! "+jkyq_time+" la den han thanh toan "+zje+".Vui long mo app xem thong tin chuyen khoan, co the chuyen qua ngan hang, Viettel (Luu y ghi ma so giao dich Viettel) ,Buu dien (24/7), noi dung chuyen tien: Ho ten, so CMND tra vay. Hotline : 0968571641",mobilePhone);
								  	sendFTP.sendMessageFTP(""+appName+" thong bao "+jkyq_time+" la den han thanh toan "+zje+".Vui long mo app xem thong tin chuyen khoan, co the chuyen qua ngan hang, Viettel (Luu y ghi ma so giao dich Viettel) ,Buu dien (24/7), noi dung chuyen tien: Ho ten, so CMND tra vay. Hotline : 0968571641",mobilePhone);
							  } catch (IOException e) {
								// TODO: handle exception
								continue;
							} 
							*/
						}
						//到期当天提醒用户还款
						if(jkyq_time.equals(fmtrq2.format(calendar2.getTime()))){
							//短信提醒用户去还款
							String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\""+appName+" xin thong bao: Khoan vay cua ban se het han ngay hom nay, so tien phai tra la "+zje+" VND, vui long dang nhap App "+appName+" de tra no. Cam on ban!\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
							String con = URLEncoder.encode(content, "utf-8");
							SendMsg sendMsg = new SendMsg();
							//String returnString = SendMsg.sendMessageByGet(con);
							/*String content1 = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\""+appName+" thong bao : Xin tra vay dung han de tranh anh huong cuoc song cong viec ban va nguoi than. Neu khong, Olava se gui ho so cho ben thu hoi no giai quyet!\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
							String con1 = URLEncoder.encode(content1, "utf-8");
							SendMsg sendMsg1 = new SendMsg();
							String returnString1 = SendMsg.sendMessageByGet(con1,mobilePhone);*/
							/*String msg253 ="【"+appName+"】亲爱的"+cardUserName+"，您的借款将于今天到期，还款金额为"+sjsh_money+"元，请及时登录"+appName+"APP还款，谢谢!"; 
							String returnString = com.util.HttpSender.batchSend(url253, un253, pw253, mobilePhone, msg253, rd253, ex253);*/
                            //手机APP 生成提示消息
							 DataRow row3 =  new DataRow();	
							  row3.set("userid", userId);
							  row3.set("title", "Trả nợ nhắc nhở") ;
							  row3.set("neirong" ,"【"+appName+"】Dear "+cardUserName+"，Khoản vay của bạn sẽ hết hạn ngày hôm nay, số tiền phải trả là "+zje+" VNĐ, vui lòng đăng nhập App "+appName+" để trả nợ. Cảm ơn bạn!");
							  row3.set("fb_time", fmtrq.format(new Date()));
							  service.insertUserMsg(row3);
							  dtnum++;
						}
						//超过还款截止时间（加上逾期利息）
						if(date_01.before(nowDate)){	
							if(tzjx == 1){
								 DataRow jkInfo = new DataRow() ;
								 jkInfo.set("id",jkid);
								 jkInfo.set("tzjx_ts",tzjx_ts+1);
								 logger.info(zjine*yqannualrate/3000);
								 jkInfo.set("tzjx_lx",famt.format(tzjxlx+sjsh*yqannualrate*5/3000));							
								 //更新借款信息
								 service.updateJKinfo(jkInfo); 
								 
//								 if(!"01-".equals(fmtrq2.format(nowDate).substring(0,3))) {
//								 }
								// 20190717 lin更新增加分单表中利息
								 DataRow jkDataRow = service.getjkIDataRow(jkid);
							    String cuishouid =jkDataRow.getString("cuishou_id");
								List<DataRow> cuishoufendanList =service.getcuishoufendanList(jkid,cuishouid);
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int cuishoujine = datafendan.getInt("cuishou_jine");
								int totalcuishoujine = cuishoujine +sjsh*yqannualrate*5/3000;  //原来入催金额+ 每天利息
								logger.info("cuishoujine"+cuishoujine+"  totalcuishoujine:"+totalcuishoujine);
								
								DataRow dataRowfd = new DataRow();
								dataRowfd.set("id", fendanid);
								dataRowfd.set("cuishou_jine",totalcuishoujine);
								logger.info("fendanid"+fendanid + " cuishouid "+ cuishouid);
								//logger.info("dataRowfd"+dataRowfd);
								service.updatecuishoufendaninfo(dataRowfd);

							}else{
							 //获取当前逾期利息 和逾期
							 DataRow jkInfo = new DataRow() ;
							 jkInfo.set("id",jkid);
							 jkInfo.set("hkfq_code",0);
							 jkInfo.set("tzjx_ts",0);
							 jkInfo.set("tzjx_lx","0");
							 jkInfo.set("yuq_ts",tzjx_ts+yuq_ts+1); 
							 logger.info(zjine*yqannualrate/3000);
							 jkInfo.set("yuq_lx",famt.format(tzjxlx+yuq+sjsh*yqannualrate*5/3000));							
							 //更新借款信息
							 service.updateJKinfo(jkInfo);
							 
							 if(!"01-".equals(fmtrq2.format(nowDate).substring(0,3))) {
								// 20190717 lin更新增加分单表中利息
								 DataRow jkDataRow = service.getjkIDataRow(jkid);
							    String cuishouid =jkDataRow.getString("cuishou_id");
								List<DataRow> cuishoufendanList =service.getcuishoufendanList(jkid,cuishouid);
								DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
								int fendanid = datafendan.getInt("id");
								int cuishoujine = datafendan.getInt("cuishou_jine");
								int totalcuishoujine = cuishoujine +sjsh*yqannualrate*5/3000;  //原来入催金额+ 每天利息
								logger.info("cuishoujine"+cuishoujine+"  totalcuishoujine:"+totalcuishoujine);
								
								DataRow dataRowfd = new DataRow();
								dataRowfd.set("id", fendanid);
								dataRowfd.set("cuishou_jine",totalcuishoujine);
								logger.info("fendanid"+fendanid + " cuishouid "+ cuishouid);
								//logger.info("dataRowfd"+dataRowfd);
								service.updatecuishoufendaninfo(dataRowfd);
							   }
							
							}
						}
							
					} catch (Exception e) {
						
						logger.error(e);
					}
				}
	/*   try {
			String content1 = "[{\"PhoneNumber\":\"‭0961111012\",\"Message\":\"SALO xin thong bao : three days : "+threenum+".\",\"SmsGuid\":\"‭0961111012\",\"ContentType\":1}]";
			String con1 = URLEncoder.encode(content1, "utf-8");
			SendMsg sendMsg1 = new SendMsg();
			String returnString1 = SendMsg.sendMessageByGet(con1);
			String content2 = "[{\"PhoneNumber\":\"‭01627749285‬\",\"Message\":\"SALO xin thong bao : today : "+dtnum+".\",\"SmsGuid\":\"‭01627749285‬\",\"ContentType\":1}]";
			String con2 = URLEncoder.encode(content2, "utf-8");
			SendMsg sendMsg2 = new SendMsg();
			String returnString2 = SendMsg.sendMessageByGet(con2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		logger.info("结束执行逾期计算："+new Date().toLocaleString());
	}
	
	public static void main(String[] args) {
		
//		SDYQTask sd =new SDYQTask();
//		sd.execute();
		
//		logger.info("开始执行逾期计算："+new Date().toLocaleString());
//		//提现需要查询的数据
//		 List<DataRow> list = service.getAllYQCxList();//获取所有
//		 //获取当前 +3天的时间
//		 SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//		 SimpleDateFormat fmtrq2 = new SimpleDateFormat("dd-MM-yyyy");
//		 DecimalFormat famt = new DecimalFormat("###,###");
//		 Calendar calendar = Calendar.getInstance();  
//		 calendar.add(Calendar.DATE, 3);	
//		 Calendar calendar2 = Calendar.getInstance(); 	
//		 Date nowDate = new Date() ;
//		 String jkyq_time ;//还款截止时间
//		int yqannualrate ;//逾期利率
//		 String sjsh_money ;//借款金额
//		 String yuq_lx ; //逾期利息
//
//		 String tzjx_lx ; //停止计息利息
//		 int sjsh =0;
//		 int yuq =0;
//		 int zjine = 0;
//
//		 int tzjxlx =0;
//		 int tzjx =0;
//		 String zje;
//		 String mobilePhone ; //手机号
//		 String cardUserName ; //真实姓名 
//		 int yuq_ts ;//逾期天数
//
//		 int tzjx_ts ;//停止计息天数
//		 String jkid ; //借款id 
//		 String userName ; //用户名
//		 String appName ="OLAVA" ; //APP名字
//		 String userId ; //用户id
//		 for (DataRow dataRow : list) 			 
//				{
//					try {
//						//计算逾期利息							
//						if(dataRow.getInt("hkfq_code") == 1){
//							jkyq_time = dataRow.getString("hkfq_time");
//						}else{
//							jkyq_time =dataRow.getString("hkyq_time");
//						}
//					    yqannualrate =dataRow.getInt("yqannualrate");
//					    sjsh_money =dataRow.getString("sjsh_money").replace(",", "");
//						yuq_lx =dataRow.getString("yuq_lx").replace(",", "");
//
//						tzjx_lx =dataRow.getString("tzjx_lx").replace(",", "");
//						sjsh = Integer.parseInt(sjsh_money);
//						yuq = Integer.parseInt(yuq_lx);
//
//						tzjxlx = Integer.parseInt(tzjx_lx);
//						zjine = sjsh + yuq ;
//						zje = famt.format(zjine);
//						yuq_ts =dataRow.getInt("yuq_ts");
//
//						tzjx_ts =dataRow.getInt("tzjx_ts");
//						tzjx =dataRow.getInt("tzjx");
//						mobilePhone =dataRow.getString("mobilephone") ; //手机号
//					    cardUserName =dataRow.getString("cardusername") ; //真实姓名 	
//					    jkid =dataRow.getString("id");
//					    userId =dataRow.getString("userid");
//					    Date date_01 = fmtrq.parse(jkyq_time);
//					    //用户名
//					    userName =dataRow.getString("username");
//					    userName =userName.substring(0,4);					  
//					    if(userName.equals("MOVA")){
//					    	appName="MOVA";					    	
//					    }else if(userName.equals("OLVA")){					    	
//					    	appName="OLAVA";
//					    } else if(userName.equals("SALO")){
//					    	
//					    	appName="SALO";
//					    }
//					   
//						//截取截止还款时间的年月日
//						jkyq_time = jkyq_time.substring(0,10);
//						//提前三天提醒用户还款
//						if(jkyq_time.equals(fmtrq2.format(calendar.getTime()))){
//							 //短信提醒用户去还款
//							/*String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\""+appName+" xin thong bao: Khoan vay cua ban se het han ngay hom nay, so tien phai tra la "+zje+" VND, vui long dang nhap App "+appName+" de tra no. Cam on ban!\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
//							String con = URLEncoder.encode(content, "utf-8");
//							SendMsg sendMsg = new SendMsg();
//							String returnString = SendMsg.sendMessageByGet(con,mobilePhone);*/
//							//String msg253 ="【"+appName+"】亲爱的"+cardUserName+"，您的借款将于"+jkyq_time+"到期，还款金额为"+(sjsh_money+yuq_lx)+"元，请及时登录"+appName+"APP还款，以免影响征信记录，谢谢!"; 
//							//String returnString = com.util.HttpSender.batchSend(url253, un253, pw253, mobilePhone, msg253, rd253, ex253);						   						   
//						     //手机APP 生成提示消息
//							 DataRow row3 =  new DataRow();	
//							  row3.set("userid", userId);
//							  row3.set("title", "Trả nợ nhắc nhở") ;
//							  row3.set("neirong" ,"【"+appName+"】Dear "+cardUserName+"，Khoản vay của bạn sẽ hết hạn vào ngày "+jkyq_time+"，số tiền phải trả là "+zje+" VNĐ, vui lòng đăng nhập App "+appName+" để trả nợ. Cảm ơn bạn!");
//							  row3.set("fb_time", fmtrq.format(new Date()));
//							  service.insertUserMsg(row3);	 
//						}
//						//到期当天提醒用户还款
//						if(jkyq_time.equals(fmtrq2.format(calendar2.getTime()))){
//							//短信提醒用户去还款
//							/*String content = "[{\"PhoneNumber\":\""+mobilePhone+"\",\"Message\":\""+appName+" xin thong bao: Khoan vay cua ban se het han ngay hom nay, so tien phai tra la "+zje+" VND, vui long dang nhap App "+appName+" de tra no. Cam on ban!\",\"SmsGuid\":\""+mobilePhone+"\",\"ContentType\":1}]";
//							String con = URLEncoder.encode(content, "utf-8");
//							SendMsg sendMsg = new SendMsg();
//							String returnString = SendMsg.sendMessageByGet(con,mobilePhone);*/
//							//String msg253 ="【"+appName+"】亲爱的"+cardUserName+"，您的借款将于今天到期，还款金额为"+sjsh_money+"元，请及时登录"+appName+"APP还款，以免影响征信记录，谢谢!"; 
//						//	String returnString = com.util.HttpSender.batchSend(url253, un253, pw253, mobilePhone, msg253, rd253, ex253);
//                            //手机APP 生成提示消息
//							 DataRow row3 =  new DataRow();	
//							  row3.set("userid", userId);
//							  row3.set("title", "Trả nợ nhắc nhở") ;
//							  row3.set("neirong" ,"【"+appName+"】Dear "+cardUserName+"，Khoản vay của bạn sẽ hết hạn ngày hôm nay, số tiền phải trả là "+zje+" VNĐ, vui lòng đăng nhập App "+appName+" để trả nợ. Cảm ơn bạn!");
//							  row3.set("fb_time",fmtrq.format(new Date()));
//							  service.insertUserMsg(row3);	  
//						}
//						//超过还款截止时间（加上逾期利息）
//						
//						if(date_01.before(nowDate)){	
//							if(tzjx == 1){
//								 DataRow jkInfo = new DataRow() ;
//								 jkInfo.set("id",jkid);
//								 jkInfo.set("tzjx_ts",tzjx_ts+1);
//								 logger.info(zjine*yqannualrate/3000);
//								 jkInfo.set("tzjx_lx",famt.format(tzjxlx+sjsh*yqannualrate*2/3000));							
//								 //更新借款信息
//								 service.updateJKinfo(jkInfo); 
//								 
//								 if(!"01-".equals(fmtrq2.format(nowDate).substring(0,3))) {
//									// 20190717 lin更新增加分单表中利息
//									 DataRow jkDataRow = service.getjkIDataRow(jkid);
//									    String cuishouid =jkDataRow.getString("cuishou_id");
//										List<DataRow> cuishoufendanList =service.getcuishoufendanList(jkid,cuishouid);
//										DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
//										int fendanid = datafendan.getInt("id");
//										int cuishoujine = datafendan.getInt("cuishou_jine");
//										int totalcuishoujine = cuishoujine +sjsh*yqannualrate*2/3000;  //原来入催金额+ 每天利息
//										logger.info("cuishoujine"+cuishoujine+"  totalcuishoujine:"+totalcuishoujine);
//										
//										DataRow dataRowfd = new DataRow();
//										dataRowfd.set("id", fendanid);
//										dataRowfd.set("cuishou_jine",totalcuishoujine);
//										logger.info("fendanid"+fendanid + " cuishouid "+ cuishouid);
//										//logger.info("dataRowfd"+dataRowfd);
//										service.updatecuishoufendaninfo(dataRowfd);
//								 }
//								
//							}else{
//							 //获取当前逾期利息 和逾期
//							 DataRow jkInfo = new DataRow() ;
//							 jkInfo.set("id",jkid);
//							 jkInfo.set("yuq_ts",tzjx_ts+yuq_ts+1);
//							 logger.info(zjine*yqannualrate/3000);
//							 jkInfo.set("yuq_lx",famt.format(tzjxlx+yuq+sjsh*yqannualrate*2/3000));							
//							 //更新借款信息
//							 service.updateJKinfo(jkInfo);
//							 
//							 if(!"01-".equals(fmtrq2.format(nowDate).substring(0,3))) {
//								// 20190717 lin更新增加分单表中利息
//								 DataRow jkDataRow = service.getjkIDataRow(jkid);
//								    String cuishouid =jkDataRow.getString("cuishou_id");
//									List<DataRow> cuishoufendanList =service.getcuishoufendanList(jkid,cuishouid);
//									DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
//									int fendanid = datafendan.getInt("id");
//									int cuishoujine = datafendan.getInt("cuishou_jine");
//									int totalcuishoujine = cuishoujine +sjsh*yqannualrate*2/3000;  //原来入催金额+ 每天利息
//									logger.info("cuishoujine"+cuishoujine+"  totalcuishoujine:"+totalcuishoujine);
//									
//									DataRow dataRowfd = new DataRow();
//									dataRowfd.set("id", fendanid);
//									dataRowfd.set("cuishou_jine",totalcuishoujine);
//									logger.info("fendanid"+fendanid + " cuishouid "+ cuishouid);
//									//logger.info("dataRowfd"+dataRowfd);
//									service.updatecuishoufendaninfo(dataRowfd);
//							  }
//							 
//							} 							 							
//						}
//						
//						
//					} catch (Exception e) {
//						
//						logger.error(e);
//					}
//				}
//		logger.info("结束执行逾期计算："+new Date().toLocaleString());
	}
	
}
