package com.task;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import com.service.AotuZDSHALLService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;
import com.util.PartnerRequestDemo;
import com.util.ZmxyGSUtils;
/**
 * 自动审核借款
 * @author Administrator
 *
 */
public class AotuZDSHALLTask implements Task
{

	private static Logger logger = Logger.getLogger(AotuZDSHALLTask.class);
	private static AotuZDSHALLService aotuZDSHAllService = new AotuZDSHALLService();
	@Override
	public void execute() 
	{
		//查询所有待审核借款项目
		List<DataRow> list = aotuZDSHAllService.getAllSHList();
		Calendar calendar =Calendar.getInstance();
		SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");			
		for (DataRow dataRow : list) 
		{
		  try {
			String userid =	dataRow.getString("userid");//借款用户id
			
			int userid2 =	dataRow.getInt("userid");//借款用户id
		   
			String jkid = dataRow.getString("id"); //借款id ;
		   
		  //判断该用户是否为黑户
			String heihu_zt = aotuZDSHAllService.getUserZt(userid);
			String xypf = aotuZDSHAllService.getUserXYPF(jkid);
			String refferee = aotuZDSHAllService.getRef(userid);
			String mobilephone = aotuZDSHAllService.getMobilephone(userid);
			String refferee1 ="";
			int myjkcode = aotuZDSHAllService.getUserMYJK(jkid);
			int fsdxcode = aotuZDSHAllService.getUserFSDX(jkid);
			
			int code=0;
		    String idno = aotuZDSHAllService.getIdno(userid2);
		    String realname = aotuZDSHAllService.getRealname(userid2);
		    int sfcount = aotuZDSHAllService.getSFCount(idno,realname); 
		    
		    
		    int sfzu [] = new int[sfcount];
		    int jkcountzu [] = new int[sfcount];
		    int codejkcf = 0;
		    if(sfcount>1){
		    	List<DataRow> listjk = aotuZDSHAllService.getALLshenfen(idno,realname);
		    	for(int j =0; j<sfcount;j++){
		    		DataRow row11 = listjk.get(j);
		    		if(row11.getInt("userid") != userid2){
		    			sfzu [j] = row11.getInt("userid");
			    		jkcountzu [j] =  aotuZDSHAllService.getJKCount(sfzu [j]);
			    		if(jkcountzu [j]>0){
			    			codejkcf=1;
			    		}
		    		}
		    		
		    	}
		    }
		    
		    int bankcardcode = 0;
		    String cardName = aotuZDSHAllService.getCardName(userid);
		    String napascode = aotuZDSHAllService.getNapasCode(userid);
		    String bankzu[] = {"Sacombank","VietcomBank","VietinBank","Techcombank","BIDV","MaritimeBank","VPBank","Eximbank","DongA Bank","VIB","MB Bank","Viet Capital Bank","OceanBank","VietABank","TPBank","HDBank","SCB","LienVietPostBank","SeABank","ABBank","Nam A Bank","OCB","GBBank","PG Bank","SHBank","Saigon Bank","Kien Long Bank","NCB","BacABank","PVcomBank","VRB","Vietbank","BVB","Wooribank"};
		    String userbankname ="";
		    if(!TextUtils.isEmpty(cardName)){
		    	userbankname = cardName.substring(0, cardName.indexOf("-")).toLowerCase().trim();
		    }
		    for(int kk =0 ;kk<bankzu.length;kk++){
		    	if(userbankname.equals(bankzu[kk].toLowerCase())){
		    		bankcardcode =1;
		    	}
		    }
		    
		    
		    if (refferee!=null) {
				 refferee1 = refferee;
			}
				 if(heihu_zt.equals("1")){
				    	//更新借款项目
					 logger.info("黑名单用户");
				 		
					 dataRow.set("id", jkid);
					 dataRow.set("cl_status","3");
					 dataRow.set("cl_yj","黑名单用户");	
					 dataRow.set("cl_ren",888);				
					 dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
					 //aotuZDSHAllService.updateJKInfo(jkid, dataRow);
					 aotuZDSHAllService.updateUserJk(dataRow);
				     	//更新消息	
						DataRow row3 =  new DataRow();	
					    row3.set("userid", userid);
					    row3.set("title", "Thông báo thẩm định") ;//审核通知
					    row3.set("neirong" ,"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả khoản vay không cao, tỷ lệ nợ quá cao; 2. Công việc hoặc thu nhập không ổn định; 3. Lịch sử tín dụng không tốt; 4. Không cung cấp thông tin theo yêu cầu hoặc cung cấp thông tin không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");
					    row3.set("fb_time", fmtrq.format(calendar.getTime()));
					    aotuZDSHAllService.insertUserMsg(row3);
		
			     	
				} /*
					 * else if(xypf.equals("-2")){ dataRow.set("id", jkid);
					 * dataRow.set("cl_status","3"); dataRow.set("cl_yj","信用评分差的用户");
					 * dataRow.set("cl_ren",888);
					 * dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
					 * //aotuZDSHAllService.updateJKInfo(jkid, dataRow);
					 * aotuZDSHAllService.updateUserJk(dataRow); //逾期的用户更新成为黑户 DataRow row2 = new
					 * DataRow() ; row2.set("id", userid ); row2.set("heihu_zt",1);
					 * aotuZDSHAllService.updateUserInfo(row2); //更新消息 DataRow row3 = new DataRow();
					 * row3.set("userid", userid); row3.set("title", "Thông báo thẩm định") ;//审核通知
					 * row3.set("neirong"
					 * ,"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả khoản vay không cao, tỷ lệ nợ quá cao; 2. Công việc hoặc thu nhập không ổn định; 3. Lịch sử tín dụng không tốt; 4. Không cung cấp thông tin theo yêu cầu hoặc cung cấp thông tin không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ."
					 * ); row3.set("fb_time", fmtrq.format(calendar.getTime()));
					 * aotuZDSHAllService.insertUserMsg(row3); }
					 *//*
						 * else if(codejkcf == 1){ logger.info("重复借款的用户"); dataRow.set("id", jkid);
						 * dataRow.set("cl_status","3"); dataRow.set("cl_yj","Repeated borrowing");
						 * dataRow.set("cl_ren",888);
						 * dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
						 * //aotuZDSHAllService.updateJKInfo(jkid, dataRow);
						 * aotuZDSHAllService.updateUserJk(dataRow); //逾期的用户更新成为黑户
						 * 
						 * }
						 */else if(bankcardcode == 1 && "0".equals(napascode)){
							 String username = aotuZDSHAllService.getUserName(userid);
							  String mobilePhone = aotuZDSHAllService.getMobilePhone(userid);
							  String appName ="F458/" ; //APP名字
							    if(username.substring(0,4).equals("F168")){
							    	appName="F168";					    	
							    }
				logger.info("银行没有认证Napas");
				 dataRow.set("id", jkid);
				 dataRow.set("cl_status","3");
				 dataRow.set("cl_yj","Chưa xác minh Napas");	
				 dataRow.set("cl_ren",888);				
				 dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
				//aotuZDSHAllService.updateJKInfo(jkid, dataRow);
				aotuZDSHAllService.updateUserJk(dataRow);
				
				DataRow row2 = new DataRow() ;
				row2.set("id", userid );
				row2.set("yhbd",0);
				aotuZDSHAllService.updateUserInfo(row2);
				 

				DataRow row3 =  new DataRow();	
			    row3.set("userid", userid);
			    row3.set("title", "Thông báo thẩm định") ;//审核通知
			    row3.set("neirong" ,"Do hệ thống ngân hàng được nâng cấp mới nên phiền bạn xác minh lại thông tin ngân hàng, sau đó đề xuất khoản vay.");
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));
			    aotuZDSHAllService.insertUserMsg(row3);
			    
			    String content   =  appName+" xin chao quy khach, do ngan hang cap nhat thong tin moi, vui long dang nhap vao app "+appName+" xac minh lai so tai khoan ngan hang de hoan tat thu tuc vay";
				SendFTP sms = new SendFTP();
				//String  response = sms.sendMessageFTP(content,mobilePhone);
				//logger.info(response);
				
			}else if(myjkcode == 1 && userid2<1){
				logger.info("认证没借款老一批的用户");
				 dataRow.set("id", jkid);
				 dataRow.set("cl_status","3");
				 dataRow.set("cl_yj","Thời gian gửi đề xuất vay quá lâu, trả về gửi đề xuất lại");	
				 dataRow.set("cl_ren",888);				
				 dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
				//aotuZDSHAllService.updateJKInfo(jkid, dataRow);
				aotuZDSHAllService.updateUserJk(dataRow);
				
				DataRow row2 = new DataRow() ;
				row2.set("id", userid );
				row2.set("islianxi",0);
				aotuZDSHAllService.updateUserInfo(row2);
				 
				DataRow row3 =  new DataRow();	
			    row3.set("userid", userid);
			    row3.set("title", "Thông báo thẩm định") ;//审核通知
			    row3.set("neirong" ,"Mofa xin chao - Vui long cap nhat lai thong tin nguoi lien he. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 02862769688.");
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));
			    aotuZDSHAllService.insertUserMsg(row3);
				//逾期的用户更新成为黑户
			    /*String smscontent="Olava xin chao - Vui long cap nhat lai thong tin nguoi lien he. Moi thac mac vui long inbox http://bit.ly/2zPb4v1, hotline: 02873008816.";
				
				String content = "[{\"PhoneNumber\":\""+mobilephone+"\",\"Message\":\""+smscontent+"\",\"SmsGuid\":\""+mobilephone+"\",\"ContentType\":1}]";
				String con = URLEncoder.encode(content, "utf-8");
				SendMsg sendMsg = new SendMsg();
				String returnString = SendMsg.sendMessageByGet(con,mobilephone);*/
			}else if(fsdxcode == 1 && userid2<1){
				logger.info("不接电话的用户");
				 dataRow.set("id", jkid);
				 dataRow.set("cl_status","3");
				 dataRow.set("cl_yj","Không nghe điện thoại");	
				 dataRow.set("cl_ren",888);				
				 dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
				//aotuZDSHAllService.updateJKInfo(jkid, dataRow);
				aotuZDSHAllService.updateUserJk(dataRow);
				
				DataRow row2 = new DataRow() ;
				row2.set("id", userid );
				row2.set("islianxi",0);
				aotuZDSHAllService.updateUserInfo(row2);
				 
				DataRow row3 =  new DataRow();	
			    row3.set("userid", userid);
			    row3.set("title", "Thông báo thẩm định") ;//审核通知
			    row3.set("neirong" ,"Mofa xin chao - Vui long cap nhat lai thong tin nguoi lien he. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 02862769688.");
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));
			    aotuZDSHAllService.insertUserMsg(row3);
				//逾期的用户更新成为黑户
			   /* String smscontent="Olava xin chao - Vui long cap nhat lai thong tin nguoi lien he. Moi thac mac vui long inbox http://bit.ly/2zPb4v1, hotline: 02873008816.";
				
				String content = "[{\"PhoneNumber\":\""+mobilephone+"\",\"Message\":\""+smscontent+"\",\"SmsGuid\":\""+mobilephone+"\",\"ContentType\":1}]";
				String con = URLEncoder.encode(content, "utf-8");
				SendMsg sendMsg = new SendMsg();
				String returnString = SendMsg.sendMessageByGet(con,mobilephone);*/
			}/*else if(refferee1.equals("77778")){
				logger.info("77778用户");
				//直接到第二步审核
				  DataRow row = new DataRow() ;
				  System.out.println("借款id："+jkid);
					row.set("id", jkid);
					row.set("cl_status",1);
					row.set("cl_yj","审核成功");	
					row.set("cl_ren",888);
					row.set("jyfk_money","0");	
					row.set("cl_time",new Date());
					aotuZDSHAllService.updateUserJk(row);				
				
				 DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "审核通知") ;
				    row3.set("neirong" ,"初审审核通过，请耐心等待！");
				    row3.set("fb_time", new Date());
				    aotuZDSHAllService.insertUserMsg(row3);
			}*/
			/*else {			
				
				//判断该用户是否有成功借款记录
				int successfulLoanCount =aotuZDSHAllService.getSuccessfulLoanCount(userid);//成功借款记录
				int successfulLoanCountYQ =aotuZDSHAllService.getSuccessfulLoanCountYQ(userid);//成功借款但是逾期用户
				if(successfulLoanCount >0 ){
					logger.info("成功借款用户");
					//直接到第二步审核
					  DataRow row = new DataRow() ;
					  System.out.println("借款id："+jkid);
						row.set("id", jkid);
						row.set("cl_status",1);
						row.set("cl_yj","有过成功借款的用户");	
						row.set("cl_ren",888);
						row.set("jyfk_money","0");	
						row.set("cl_time",fmtrq.format(calendar.getTime()));
						aotuZDSHAllService.updateUserJk(row);				
					
					 DataRow row3 =  new DataRow();	
					    row3.set("userid", userid);
					    row3.set("title", "Thông báo thẩm định") ;
					    row3.set("neirong" ,"Thẩm định lần 1 đã thông qua, xin vui lòng chờ đợi bước tiếp theo.");
					    row3.set("fb_time", fmtrq.format(calendar.getTime()));
					    aotuZDSHAllService.insertUserMsg(row3);
				 }*/
				 /*else if(successfulLoanCount >0 && successfulLoanCountYQ >0){ 
						// 成功借款但是逾期用户
					 logger.info("逾期用户"); 
					
					 dataRow.set("id", jkid);
					 dataRow.set("cl_status","3");
					 dataRow.set("cl_yj","逾期用户");	
					 dataRow.set("cl_ren",161);				
					 dataRow.set("cl_time",new Date());
					aotuZDSHAllService.updateJKInfo(jkid, dataRow);
					//逾期的用户更新成为黑户
					DataRow row2 = new DataRow() ;
					row2.set("id", userid );
					row2.set("heihu_zt",1);
					 aotuZDSHAllService.updateUserInfo(row2);
															
			     	//更新消息	
					DataRow row3 =  new DataRow();	
				    row3.set("userid", userid);
				    row3.set("title", "审核通知") ;
				    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
				    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
				    row3.set("fb_time", new Date());
				    aotuZDSHAllService.insertUserMsg(row3);			
				}*/
				/*else {
						
						//判断芝麻分是否大于550
						int zm_score = aotuZDSHAllService.getZmscore(userid);
						if(zm_score <570){
							 logger.info("芝麻分小于550的用户");						
							 dataRow.set("id", jkid);
							 dataRow.set("cl_status","3");
							 dataRow.set("cl_yj","综合评分不足。");	
							 dataRow.set("cl_ren",161);				
							 dataRow.set("cl_time",new Date());
							aotuZDSHAllService.updateJKInfo(jkid, dataRow);
			
					     	//更新消息	
							DataRow row3 =  new DataRow();	
						    row3.set("userid", userid);
						    row3.set("title", "审核通知") ;
						    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
						    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
						    row3.set("fb_time", new Date());
						    aotuZDSHAllService.insertUserMsg(row3);										
						  }*//*else {
							  //判断该用户通话记录是否 大于800 条
							  int tonghuajilu  = aotuZDSHAllService.getTonghuats(userid);
							    if(tonghuajilu < 400){
							    	
							    	logger.info("通话记录少于800条的用户");
								    //根据用户id 查询出用户的通话记录条数
							    	//更新借款项目
					
							 		
							    	dataRow.set("id", jkid);
							    	dataRow.set("cl_status","3");
							    	dataRow.set("cl_yj","通话记录条数过少。");	
							    	dataRow.set("cl_ren",161);				
							    	dataRow.set("cl_time",new Date());
									aotuZDSHAllService.updateJKInfo(jkid, dataRow);
							
							     	//更新消息	
									DataRow row3 =  new DataRow();	
								    row3.set("userid", userid);
								    row3.set("title", "审核通知") ;
								    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
								    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
								    row3.set("fb_time", new Date());
								    aotuZDSHAllService.insertUserMsg(row3);
								    
							   }*/
									else {
									  //判断该用户通话记录是否 大于800 条
									  int tongxunlu  = aotuZDSHAllService.getTongxunluts(userid);
									  String username = aotuZDSHAllService.getUserName(userid);
									    if(tongxunlu < 20 && tongxunlu !=0 && !"TAFA".equals(username.substring(0, 4))){
									    	
									    	logger.info("通话记录少于20条的用户");
										    //根据用户id 查询出用户的通话记录条数
									    	//更新借款项目
							
									 		
									    	dataRow.set("id", jkid);
									    	dataRow.set("cl_status","3");
									    	dataRow.set("cl_yj","Limit 15 days");	
									    	dataRow.set("cl_ren",888);				
									    	dataRow.set("cl_time",fmtrq.format(new Date()));
											aotuZDSHAllService.updateUserJk(dataRow);
									
									     	//更新消息	
											DataRow row3 =  new DataRow();	
										    row3.set("userid", userid);
										    row3.set("title", "Thông báo thẩm định") ;
										    row3.set("neirong" ,"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");
										    row3.set("fb_time", fmtrq.format(new Date()));
										    aotuZDSHAllService.insertUserMsg(row3);
										    
									   }
									    /*else {
								   //判断联系人通话条数是否大于40 
								   DataRow  dataRow3 =aotuZDSHAllService.getAllZMSHList(userid); 
									String phone1 = dataRow3.getString("contactphone").replaceAll(" ", "").replaceAll("-", ""); //联系人01手机号
									String phone2 = dataRow3.getString("contactphone02").replaceAll(" ", "").replaceAll("-", ""); //联系人02手机号
								    //根据用户id 得到联系人的通话条数
									 int thtszj01 =  aotuZDSHAllService.getLxThtszj(userid ,phone1); //联系人01主叫
									 int thtsbj01 =  aotuZDSHAllService.getLxThtsbj(userid ,phone1); //联系人01被叫
									 int thtszj02 =  aotuZDSHAllService.getLxThtszj(userid ,phone2); //联系人02主叫
									 int thtsbj02 =  aotuZDSHAllService.getLxThtsbj(userid ,phone2); //联系人02被叫
									
									 if((thtszj01 + thtsbj01)<20 && (thtszj02 + thtsbj02)<20){  //thtszj01 < 20 || thtsbj01< 20 || thtszj02 < 20 || thtsbj02 < 20
										 logger.info("联系人通话记录较少的用户");
										//更新借款项目
								    	Date  date = new Date();
										Calendar calendar =Calendar.getInstance();
										SimpleDateFormat fmtrq  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
								 		
										dataRow.set("id", jkid);
										dataRow.set("cl_status","3");
										dataRow.set("cl_yj","提供的亲属朋友联系人通话记录次数过少，建议更换联系人！!。");	
										dataRow.set("cl_ren",161);				
										dataRow.set("cl_time",fmtrq.format(calendar.getTime()));
										aotuZDSHAllService.updateJKInfo(jkid, dataRow);
										
										//更新用户信息（将用户联系人状态打回）
										DataRow row2 = new DataRow() ;
										row2.set("id", userid );
										row2.set("isLianxi",0);
										 aotuZDSHAllService.updateUserInfo(row2);
								     	//更新消息	
										DataRow row3 =  new DataRow();	
									    row3.set("userid", userid);
									    row3.set("title", "审核通知") ;
									    row3.set("neirong" ,"信息审核不通过： 提供的亲属朋友联系人通话记录次数过少，建议更换联系人！。");
									    row3.set("fb_time", new Date());
									    aotuZDSHAllService.insertUserMsg(row3);
									 }*//*else {
										 
										//判断芝麻是否存在未还清的状态
										String  yqmx = "false" ; //是否存在逾期未还清的情况 
										DataRow  zmxyInfo = aotuZDSHAllService.getUserZmfs(userid);
										if(zmxyInfo !=null )
										{		
								 			ZmxyGSUtils  result = new ZmxyGSUtils() ;
								 			boolean jianquan =result.testZhimaAuthInfoAuthquery(zmxyInfo.getString("open_id"),
								 					zmxyInfo.getString("app_id"));						 			
								 			 if(jianquan)
								 			 {
								 				String zmyqjl =result.testZhimaCreditWatchlistiiGet(zmxyInfo.getString("app_id"),
								 	 					zmxyInfo.getString("open_id"));
			
								 					    //解析芝麻验证结果
								 				com.alibaba.fastjson.JSONObject reqObject1 = (com.alibaba.fastjson.JSONObject)JSON.parse(zmyqjl);          
								 			            String is_matched = reqObject1.getString("is_matched");
								 			            if(is_matched.equals("true"))
								 			             {
								 			            	//得到逾期明细       
								 			            	String yq_detail =reqObject1.getString("details");
								 			            	com.alibaba.fastjson.JSONArray details = reqObject1.getJSONArray("details");            
								 			            	com.alibaba.fastjson.JSONObject detail=(com.alibaba.fastjson.JSONObject) details.get(0);
								 			            	  yqmx = detail.getString("settlement");//是否还清逾期金额
								 			              }								 				 			 
								 			  }
										 }
										
										if("true".equals(yqmx)){ 
											// 表示存在逾期未还清的情况
											 logger.info("芝麻存在逾期 未还清用户"); 
											
											 dataRow.set("id", jkid);
											 dataRow.set("cl_status","3");
											 dataRow.set("cl_yj","存在逾期情况未还清。");	
											 dataRow.set("cl_ren",161);				
											 dataRow.set("cl_time",new Date());
											aotuZDSHAllService.updateJKInfo(jkid, dataRow);
											//逾期的用户更新成为黑户
											DataRow row2 = new DataRow() ;
											row2.set("id", userid );
											row2.set("heihu_zt",1);
											 aotuZDSHAllService.updateUserInfo(row2);
																					
									     	//更新消息	
											DataRow row3 =  new DataRow();	
										    row3.set("userid", userid);
										    row3.set("title", "审核通知") ;
										    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
										    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
										    row3.set("fb_time", new Date());
										    aotuZDSHAllService.insertUserMsg(row3);			
										}*//*else {
											//借款平台数
											//查询有盾 扫描 和活体的数据		
									 		DataRow personYdInfo = aotuZDSHAllService.getPersonYdInfo(userid);	 		
									 		//有盾扫描反馈信息
									 		String ydInfoL ="" ;
									 		PartnerRequestDemo ydInfo =new  PartnerRequestDemo();
									 		String loanplatformcount = "0" ;
									 		String repaymentplatformcount = "0";
									        double numberPlatforms = 0;
									        Boolean ifDishonesty =false ;
									 	    com.alibaba.fastjson.JSONObject user_detail = null  ;  //有盾反馈的个人信息
									 	    com.alibaba.fastjson.JSONArray loan_industrys =null ;  //有盾反馈的行业借贷情况
									 	    com.alibaba.fastjson.JSONArray devices =null ;//有盾个人设备使用信息
									 	    com.alibaba.fastjson.JSONArray user_features =null ;//有盾个人设备使用信息
									 	  
									 		ydInfoL =ydInfo.getYdfkInfo(personYdInfo.getString("idnumber"), personYdInfo.getString("orderid"));
									 	   if(ydInfoL.equals("fail")){		   
									 		  ydInfoL =ydInfo.getYdfkInfo(personYdInfo.getString("idnumber"), personYdInfo.getString("orderid")); 		   
									 	   }
									 		com.alibaba.fastjson.JSONObject reqObject6 = (com.alibaba.fastjson.JSONObject) JSON.parse(ydInfoL);
										   if(reqObject6.containsKey("header"))
										   {	
											 String header =reqObject6.getString("header");	
											//解析消息
											 com.alibaba.fastjson.JSONObject reqObject7 = (com.alibaba.fastjson.JSONObject) JSON.parse(header);		
											if(reqObject7.getString("ret_code").equals("000000"))
											{
												    //解析数据
												   String body1 =reqObject6.getString("body");												
											       com.alibaba.fastjson.JSONObject reqObject2 = (com.alibaba.fastjson.JSONObject) JSON.parse(body1); 
													//得到用户的基本信息	  
											       if(reqObject2.containsKey("user_detail"))
											       {
													   user_detail = reqObject2.getJSONObject("user_detail");			   
													   String user_detail2=reqObject2.getString("user_detail");
													   com.alibaba.fastjson.JSONObject reqObject8 = (com.alibaba.fastjson.JSONObject) JSON.parse(user_detail2); 
													   loanplatformcount= reqObject8.getString("loan_platform_count"); //借款平台数
													   repaymentplatformcount = reqObject8.getString("repayment_platform_count");//还款平台数
													  
											          if(user_detail.containsKey("user_features"))
											          {
											        	  
											        	  user_features =user_detail.getJSONArray("user_features"); //用户特征
											        	  //遍历 json 集合
											        	  com.alibaba.fastjson.JSONObject user_feature;
											        
																 for (int i = 0; i < user_features.size(); i++) 
																 {  
																	 DataRow  taobaosh = new DataRow();
																	 user_feature =(com.alibaba.fastjson.JSONObject) user_features.get(i); 
																			 if(user_feature.getString("user_feature_type").equals("12")||
																					 user_feature.getString("user_feature_type").equals("16")||
																					 user_feature.getString("user_feature_type").equals("7")||
																					 user_feature.getString("user_feature_type").equals("6"))
																			 {
																				     ifDishonesty =true;
																				 
																			 }
	
																 }
											            }
											       
											       }
											       if(reqObject2.containsKey("devices"))
											        {
													   //得到用户移动设备使用情况	
											    	   com.alibaba.fastjson.JSONObject device;
													   devices = reqObject2.getJSONArray("devices"); 
														   for (int i = 0; i < devices.size(); i++) 
														    {  
															   device = (com.alibaba.fastjson.JSONObject) devices.get(i); 
															   String device_detail = device.getString("device_detail");														  
															   com.alibaba.fastjson.JSONObject reqObject12 = (com.alibaba.fastjson.JSONObject) JSON.parse(device_detail); 												 
															   if(reqObject12.containsKey("app_instalment_count")){
																      numberPlatforms = numberPlatforms+Double.parseDouble(reqObject12.getString("app_instalment_count"));
																     }
														    }
											        }			
													
											    }
											
											 } else {
												
												 loanplatformcount = personYdInfo.getString("loanplatformcount") ;
												 repaymentplatformcount = personYdInfo.getString("repaymentplatformcount");
										 	  			
										   }*/
										  /* //根据有盾判断用户借款信息
										   if(Double.parseDouble(loanplatformcount) > 30 ) {  //&& (Double.parseDouble(repaymentplatformcount)) / (Double.parseDouble(loanplatformcount)) < 0.5
											   //借款平台大于20家 
											   logger.info("借款平台大于30家");	//5家用户且还款不足50%"										 
											   dataRow.set("id", jkid);
											   dataRow.set("cl_status","3");
											   dataRow.set("cl_yj","借款平台大于30家");	//5家用户且还款不足50%！
											   dataRow.set("cl_ren",161);				
											   dataRow.set("cl_time",new Date());
												aotuZDSHAllService.updateJKInfo(jkid, dataRow);
												
											
										     	//更新消息	
												DataRow row3 =  new DataRow();	
											    row3.set("userid", userid);
											    row3.set("title", "审核通知") ;
											    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
											    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
											    row3.set("fb_time", new Date());
											    aotuZDSHAllService.insertUserMsg(row3);		
											   
										   }else */
											/*if(ifDishonesty) {
											   //高风险失信用户
											   logger.info("高风险失信用户");
											 
											   dataRow.set("id", jkid);
											   dataRow.set("cl_status","3");
											   dataRow.set("cl_yj","命中高风险失信特征");	
											   dataRow.set("cl_ren",161);				
											   dataRow.set("cl_time",new Date());
											   aotuZDSHAllService.updateJKInfo(jkid, dataRow);
												
											
										     	//更新消息	
												DataRow row3 =  new DataRow();	
											    row3.set("userid", userid);
											    row3.set("title", "审核通知") ;
											    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
											    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
											    row3.set("fb_time", new Date());
											    aotuZDSHAllService.insertUserMsg(row3);	*/
											   
										  /* }else if(numberPlatforms >30){
											   //安装平台大于25
											   logger.info("安装APP大于 30家的记录");											  
											   dataRow.set("id", jkid);
											   dataRow.set("cl_status","3");
											   dataRow.set("cl_yj","安装借款平台大于30家");	
											   dataRow.set("cl_ren",161);				
											   dataRow.set("cl_time",new Date());
												aotuZDSHAllService.updateJKInfo(jkid, dataRow);
												
											
										     	//更新消息	
												DataRow row3 =  new DataRow();	
											    row3.set("userid", userid);
											    row3.set("title", "审核通知") ;
											    row3.set("neirong" ,"抱歉，本次审核未通过，可能由以下一个或多个原因造成：" +
											    		"1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。");
											    row3.set("fb_time", new Date());
											    aotuZDSHAllService.insertUserMsg(row3);	*/
											   
										   /*}else {
											   //信息合适直接跳到第二步
											   logger.info("条件符合 进入第二步用户");
											   DataRow row = new DataRow() ;											 
												row.set("id", jkid);
												row.set("cl_status",1);
												row.set("cl_yj","审核成功");	
												row.set("cl_ren",161);
												row.set("jyfk_money","500");	
												row.set("cl_time",new Date());
												aotuZDSHAllService.updateUserJk(row);		
                                               DataRow row3 =  new DataRow();	
												row3.set("userid", userid);
											    row3.set("title", "审核通知") ;
												row3.set("neirong" ,"初审审核通过，请耐心等待！");
											    row3.set("fb_time", new Date());
											    aotuZDSHAllService.insertUserMsg(row3);
	   
										   }
										   
										   
										}*/
										
									}
								   
								 
							   
					  
				        
				   
	
			}
		
		  catch(Exception e) {
			  
			  e.printStackTrace() ;
		  }
		}	
	}
    
	
	
	
}
