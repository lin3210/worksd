package com.task;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.lianpay.util.HttpUtil;
import com.service.SDYQService;
import com.service.SelectUserAndBankService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.timerengine.Task;

import net.sf.json.JSONObject;



public class AutoEveryDaySendMsgTask implements Task {

	private static Logger logger = Logger.getLogger(AutoEveryDaySendMsgTask.class);
	private static SDYQService sdyqservice  = new SDYQService();
	private static SelectUserAndBankService selectuserandbankservice = new SelectUserAndBankService();
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		SimpleDateFormat fmtymd  = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmtday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int nsendcount_wrz =0;                //f发送短信数目
		int sendMaxnum =15;                   //限制发送次数
		
		//String phonetable="sduser_new_phone_dxsh";
		//List<DataRow> listphone=  selectuserandbankservice.getnewPhone(phonetable,3000,1000);
		
		String phonetable="";
		String  funtionname = "T_NEW_MSG";
		DataRow rowFun=  selectuserandbankservice.getnewmsg_function(funtionname);
		if(rowFun!=null) {
			int fun_id = rowFun.getInt("id");
			String url=rowFun.getString("content");
			int startnum=rowFun.getInt("start_num");
			int sendnum =rowFun.getInt("send_num");
			int fun_status = rowFun.getInt("fun_status");
			
			if(fun_status>0) {
				if(fun_status==1) {
				    phonetable="sduser_new_phone_dxhk";
				}
//				}else if(fun_status==2) {
//					 phonetable="sduser_new_phone_daily";
//				}else if(fun_status==3) {
//					phonetable="sduser_new_phone_dxsh";
//				}
				int totalphone=selectuserandbankservice.getuserphonenconut(phonetable);
				if(startnum>=totalphone) {
					startnum=0;
				}
				
				//g更新function
				DataRow upFun= new DataRow();
				upFun.set("id",fun_id);
				upFun.set("start_num", startnum+sendnum);
				selectuserandbankservice.updatafunction("sdcms_function_control",upFun);
				
//				startnum=1000;
//				sendnum=3000;
//				phonetable="sduser_new_phone";
				 
				List<DataRow> listphone=  selectuserandbankservice.getnewPhone(phonetable,startnum,sendnum);
				
				for(DataRow row:listphone) {
					System.out.print("第："+nsendcount_wrz);
					System.out.print("----");
					String phone = row.getString("phone");
					
					if(!phone.equals("")) {
						
//						String idno = selectuserandbankservice.getuserphone_idno(phone);
//						int heihu=getUMBaseUserHeiHuZT(idno);
//						System.out.print("----heihu："+heihu);
//						if(heihu==1) {
//							System.out.print("----黑户："+phone);
//							break;
//						}
						
						if(phonetable.equals("sduser_new_phone")) {
							int user_num =selectuserandbankservice.getuserphoneJK(phone);
							if(user_num<=0) {
								DataRow selrow = selectuserandbankservice.getnewPhonerecord(phone);
								if(selrow !=null) {	
									int sendcs=selrow.getInt("send_num");
									if(sendcs<sendMaxnum) {
										sendcs++;
										int id = selrow.getInt("id");
										String send_result= sendMsg(phone,url);
										nsendcount_wrz++;
										
										DataRow uprow_r= new DataRow();
										uprow_r.set("id",id);
										uprow_r.set("last_time", fmtday.format(new Date()));
										uprow_r.set("send_num", sendcs);
										uprow_r.set("send_result", send_result);
										selectuserandbankservice.updateTableData(uprow_r);
									}
								}else {
									nsendcount_wrz++;
									String send_result =sendMsg(phone,url);
									DataRow inrow= new DataRow();
									inrow.set("create_time", fmtday.format(new Date()));
									inrow.set("last_time", fmtday.format(new Date()));
									inrow.set("phone",phone );
									inrow.set("send_num", 1);
									inrow.set("status", 0);
									inrow.set("send_com","onesms" );
									inrow.set("send_result",send_result);
									String tablename ="sduser_new_phone_record";
									selectuserandbankservice.insertTableData(tablename,inrow);
								}
							}
							
						}else {
							int user_num =selectuserandbankservice.getuserphone(phone);
							if(user_num>0) {
								DataRow uprow = new DataRow();
								uprow.set("phone", phone);
								uprow.set("status", 1);
								selectuserandbankservice.updanewPhone(uprow);
								
								DataRow selrow = selectuserandbankservice.getnewPhonerecord(phone);
								if(selrow !=null) {
									int id = selrow.getInt("id");
									DataRow uprow_r= new DataRow();
									uprow_r.set("id",id);
									uprow_r.set("status", 1);
									selectuserandbankservice.updateTableData(uprow_r);
								}
							}else {
								DataRow selrow = selectuserandbankservice.getnewPhonerecord(phone);
								if(selrow !=null) {	
									int sendcs=selrow.getInt("send_num");
									if(sendcs<sendMaxnum) {
										sendcs++;
										int id = selrow.getInt("id");
										String send_result= sendMsg(phone,url);
										nsendcount_wrz++;
										
										DataRow uprow_r= new DataRow();
										uprow_r.set("id",id);
										uprow_r.set("last_time", fmtday.format(new Date()));
										uprow_r.set("send_num", sendcs);
										uprow_r.set("send_result", send_result);
										selectuserandbankservice.updateTableData(uprow_r);
									}
								}else {
									nsendcount_wrz++;
									String send_result =sendMsg(phone,url);
									DataRow inrow= new DataRow();
									inrow.set("create_time", fmtday.format(new Date()));
									inrow.set("last_time", fmtday.format(new Date()));
									inrow.set("phone",phone );
									inrow.set("send_num", 1);
									inrow.set("status", 0);
									inrow.set("send_com","onesms" );
									inrow.set("send_result",send_result);
									String tablename ="sduser_new_phone_record";
									selectuserandbankservice.insertTableData(tablename,inrow);
								}
							}
						}
						
						
					}
					
				}
				
			}
		}
		

		logger.info("Today发送短信数目："+nsendcount_wrz);
		//g更新function_record
		DataRow upFun_record= new DataRow();
		upFun_record.set("create_time", new Date());
		upFun_record.set("number",nsendcount_wrz);
		upFun_record.set("msg_name", phonetable);
		selectuserandbankservice.insertTableData("sduser_new_record",upFun_record);
		
   }
	
	public String sendMsg(String mobilePhone ,String url) {
		String appname ="VANA";
		String returnString= "";
		String MSG_COM ="onesms";
		SendFTP sendFTP = new SendFTP();
		try {
			//returnString = sendFTP.sendMessageFTP(""+appname+" chao!  vui long vao App hoan tat thu tuc de nhan ngay khoan vay len den 10.0000.000vnd. Lien he:https://url.cn/50VlebA, hotline: 0923087819.",mobilePhone);
			
			if(!"".equals(url)) {
				//returnString = sendFTP.sendMessageFTP("Ban da thoa dieu kien vay 10 trieu vnd tai ung dung VANA-APP,tai ngay:https://url.cn/5bkcqPT .Nhan tien trong 10 phut.Hotline:0923087819",mobilePhone);	
				 //returnString = sendFTP.sendMessageFTP(""+appname+" chao!  vui long vao App hoan tat thu tuc de nhan ngay khoan vay len den 10.0000.000vnd."+url,mobilePhone);	
				returnString = sendFTP.sendMessageFTP("Ban da thoa dieu kien vay 10 trieu vnd tai ung dung VANA. Dang ky ngay tai: https://www.newappvn.com/webvana/registered.html. Hotline: 0923087819 ",mobilePhone);	
		    }else {
		    	returnString = sendFTP.sendMessageFTP(""+appname+" chao!  vui long vao App hoan tat thu tuc de nhan ngay khoan vay len den 10.0000.000vnd. Lien he: https://url.cn/5bkcqPT , hotline: 0923087819.",mobilePhone);
		    }
			  if(returnString.length()>70) {
				  //returnString = returnString.substring(70,returnString.lastIndexOf("</"));
				  returnString = returnString.substring(70,81);
				  System.out.println(mobilePhone+"-------"+returnString);
			  }  
		} catch (IOException e) {
			// TODO: handle exception
		}
		 return returnString;
	}
	
	/**
	 * 用户黑户状态
	 * @param userIDCard
	 * @return
	 */
	public int getUMBaseUserHeiHuZT(String idno ) {
		String jiami = "G0eHIW3op8dYIWsdsdeqFSDeafhklRG";
		int heihu_zt =-1;
				
		if(!"".equals(idno)) {
			String sfmiwen = Encrypt.MD5(jiami+idno);
	
			JSONObject jsonObject = new JSONObject();
			com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			// com.alibaba.fastjson. JSONObject reqObject1 = (com.alibaba.fastjson.JSONObject) JSON.parse(response3);
			json.put("secret", sfmiwen);
			json.put("idno", idno);
			
			String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetUserHeiHustatus";
			String response = HttpUtil.doPost(url, json, "UTF-8");
			com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
			
			int error = json1.getInteger("error");
			if (error == 1) {
				 heihu_zt = json1.getInteger("heihu_zt");
				 int pingtai = json1.getInteger("pingtai");
				 logger.info("idno:"+idno);
				 logger.info("heihu_zt:"+heihu_zt);
				 logger.info("pingtai:"+pingtai);
	
				return heihu_zt;
			} else {
	
				return heihu_zt;
		   }
			
		}
	
		return heihu_zt;
	}
	
	
	public static void main(String []arg) {
			AutoEveryDaySendMsgTask auto = new AutoEveryDaySendMsgTask();
			auto.execute();
			
	}
	
	
}
