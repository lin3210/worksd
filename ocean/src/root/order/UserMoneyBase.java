package root.order;

import java.util.List;

import org.apache.log4j.Logger;

import com.project.bean.JBDUserBaseServiceBean;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DataRow;

import net.sf.json.JSONObject;
import root.current.HttpUtil;

/**
 *  Public use method linC
 * @author Administrator
 *
 */
public class UserMoneyBase  {
	
	private static JBDUserBaseServiceBean  jbdUserBaseServiceBean = new JBDUserBaseServiceBean();
	private static Logger logger = Logger.getLogger(UserMoneyBase.class);
	String jiami = "G0eHIW3op8dYIWsdsdeqFSDeafhklRG";
	/**
	 * * Calculate product interest
	 * @param jkmoney
	 * @param nProductType  1--15, 2--30, 3--7, 4--14
	 * @param userid  
	 * @param usernam
	 * @return
	 */
	public  int getUMBaseCalculateProductInterest(int jkmoney,int nProductType,int userid,String username) {
		
		if(username.isEmpty() || username.length()<5) {
			username =jbdUserBaseServiceBean.getusername(userid+"");
		}
		// Ocean lx     7day  30 %
		int lx=35;;
		if (1==nProductType) {    //15
			lx=35;
		}else if(2==nProductType) {  //30
			lx=35;
		}else if(3==nProductType) {  //7
			lx=35;
		}else if(4==nProductType) {  //14
			 lx =35;
		}
		
		return lx;
	}
	

	public static void main(String []arg ) {
		

		int userid =5715;
		int return_Maxmoney = 2000000;
	    List<DataRow> listjkDataMax = jbdUserBaseServiceBean.getUserjkMoneyMax(userid+"");
	   
	    int cgjk_cs =0;
		if( null != listjkDataMax) {
			for(DataRow jkDataMax:listjkDataMax) {
				int hkcs=listjkDataMax.size();
				
				String sjdzMoney = jkDataMax.getString("sjds_money"); // Mas money
				String  jklx = jkDataMax.getString("lx");    //loan interset
				int yuqts = jkDataMax.getInt("yuq_ts");  //  overdue days
				int nMaxmoney =Integer.parseInt(sjdzMoney.replace(",", "").replace(".", ""))+ Integer.parseInt(jklx.replace(",", "").replace(".", ""));
				
				if(nMaxmoney>=return_Maxmoney) {
					return_Maxmoney = nMaxmoney;
					cgjk_cs++;
					if(nMaxmoney==2000000) {
						if(yuqts <15) {
							
							return_Maxmoney +=500000;
						}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
							return_Maxmoney -=1000000;
						}else if ( yuqts > 60   ) {
							return_Maxmoney =2000000;
						}
					}else if(nMaxmoney==2500000 ) {
						if(cgjk_cs >=3) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
					}else if(nMaxmoney==3000000 ) {
						if(cgjk_cs >=5) {
							if(cgjk_cs >=2) {
								if(yuqts <15) {
									return_Maxmoney +=500000;
								}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
									return_Maxmoney -=1000000;
								}else if ( yuqts > 60   ) {
									return_Maxmoney =2000000;
								}
							}
						}
					}else if(nMaxmoney==3500000 ) {
						if(cgjk_cs >=8) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}else if(nMaxmoney==4000000) {
						if(cgjk_cs >=12) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}else if(nMaxmoney==4500000) {
						if(cgjk_cs >=22) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}
					
				}
			}
		}
		logger.info("userid--return_Maxmoney:"+return_Maxmoney);
		if(return_Maxmoney >5000000 ) {
			return_Maxmoney=5000000; 
		}
		return ;
	
	}
	/**
	 * Maximum loan amount
	 * @param userid 
	 * @return
	 */
     public int getUMBaseMaxLoanMoney(int userid) {
		
		int return_Maxmoney = 2000000;
	    List<DataRow> listjkDataMax = jbdUserBaseServiceBean.getUserjkMoneyMax(userid+"");
	   
	    int cgjk_cs =0;
		if( null != listjkDataMax) {
			for(DataRow jkDataMax:listjkDataMax) {
				int hkcs=listjkDataMax.size();
				
				String sjdzMoney = jkDataMax.getString("sjds_money"); // Mas money
				String  jklx = jkDataMax.getString("lx");    //loan interset
				int yuqts = jkDataMax.getInt("yuq_ts");  //  overdue days
				int nMaxmoney =Integer.parseInt(sjdzMoney.replace(",", "").replace(".", ""))+ Integer.parseInt(jklx.replace(",", "").replace(".", ""));
				
				if(nMaxmoney>=return_Maxmoney) {
					return_Maxmoney = nMaxmoney;
					cgjk_cs++;
					if(nMaxmoney==2000000) {
						if(yuqts <15) {
							
							return_Maxmoney +=500000;
						}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
							return_Maxmoney -=1000000;
						}else if ( yuqts > 60   ) {
							return_Maxmoney =2000000;
						}
					}else if(nMaxmoney==2500000 ) {
						if(cgjk_cs >=3) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
					}else if(nMaxmoney==3000000 ) {
						if(cgjk_cs >=5) {
							if(cgjk_cs >=2) {
								if(yuqts <15) {
									return_Maxmoney +=500000;
								}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
									return_Maxmoney -=1000000;
								}else if ( yuqts > 60   ) {
									return_Maxmoney =2000000;
								}
							}
						}
					}else if(nMaxmoney==3500000 ) {
						if(cgjk_cs >=8) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}else if(nMaxmoney==4000000) {
						if(cgjk_cs >=12) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}else if(nMaxmoney==4500000) {
						if(cgjk_cs >=22) {
							if(yuqts <15) {
								return_Maxmoney +=500000;
							}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
								return_Maxmoney -=1000000;
							}else if ( yuqts > 60   ) {
								return_Maxmoney =2000000;
							}
						}
						
					}
					
				}
			}
		}
		logger.info("userid--return_Maxmoney:"+return_Maxmoney);
		if(return_Maxmoney >5000000 ) {
			return_Maxmoney=5000000; 
		}
		return return_Maxmoney;
	}
	
     /**
  	 * Maximum loan amount  APP显示金额
  	 * @param userid 
  	 * @return
  	 */
       public int getUMBaseMaxLoanMoney_showApp(int userid) {
  		
  		int return_Maxmoney = 12000000;
//  	    List<DataRow> listjkDataMax = jbdUserBaseServiceBean.getUserjkMoneyMax(userid+"");
//  	   
//  		if( null != listjkDataMax) {
//  			for(DataRow jkDataMax:listjkDataMax) {
//  				String sjdzMoney = jkDataMax.getString("sjds_money"); // Mas money
//  				String  jklx = jkDataMax.getString("lx");    //loan interset
//  				int yuqts = jkDataMax.getInt("yuq_ts");  //  overdue days
//  				int nMaxmoney =Integer.parseInt(sjdzMoney.replace(",", "").replace(".", ""))+ Integer.parseInt(jklx.replace(",", "").replace(".", ""));
//  				
//  				if(nMaxmoney>=return_Maxmoney) {
//  					return_Maxmoney = nMaxmoney;
//  					
//  					if(yuqts <15) {
// 						return_Maxmoney +=500000;
// 					}else if (yuqts <= 60 && yuqts >= 30 && nMaxmoney >3000000 ) {
// 						return_Maxmoney -=1000000;
// 					}else if ( yuqts > 60   ) {
// 						return_Maxmoney =2000000;
// 					}
//  					
//  				}
//  			}
//  		}
  		
  		if(return_Maxmoney >12000000) {
  			return_Maxmoney=12000000;
  		}

  		return return_Maxmoney;
  	}
       
	/**
	 *  success loan Number
	 * @param userid
	 * @param maxMoney
	 * @return
	 */
	public int  getUMBaseSuccessfulLoanNum(int userid,String maxMoney) {
		int SuccessNum =0;
		
		if(userid>0) {
			SuccessNum =jbdUserBaseServiceBean.getUserSuccessLoanTotal(userid+"",maxMoney);
		}
		return SuccessNum;
	}
	
	/**
	 * * Calculate product  Overdue interest
	 * @param jkmoney
	 * @param nProductType  1--15, 2--30, 3--7, 4--14
	 * @param userid  
	 * @param usernam
	 * @return
	 */
	public  int getUMBaseCalculateProductOverdueInterest(int jkmoney,int nProductType,int userid,String username) {
		int yqlx =2;
		
		return yqlx;
	}
	
    public int getUMBaseFindjkAccount(String userIDCard,String userPhone,String name,String Modetype){
		
		int jk_cs = 0 ;
		String sfmiwen = Encrypt.MD5(jiami+"");
		if(!userIDCard.isEmpty()) {
			sfmiwen = Encrypt.MD5(jiami+userIDCard);
		}
		
		JSONObject jsonObject = new JSONObject();

		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("secret", sfmiwen);
		json.put("idno", userIDCard);
		json.put("phone", userPhone);
		json.put("name", name);

		String url = "";
		
		if("MOFA".equals(Modetype)) {
			url = "http://www.mofavn.vn/servlet/current/M99User1?function=GetM99jkAccount";
		}else if("M99".equals(Modetype)) {
			url = "http://www.m99vn.com/servlet/current/M99User1?function=GetM99jkAccount";
		}else {
			url = "http://app.m99vn.com/servlet/current/M99User1?function=GetM99jkAccount";
		}

		String response = HttpUtil.doPost(url, json, "UTF-8");
		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
		// String code = json1.getString("status");

		int error = json1.getInteger("error");
		if (error == 1) {
			String idno = json1.getString("idno");
			jk_cs = json1.getInteger("jkcs");
		
			return jk_cs;
		} else {

			return jk_cs;
	   }
	}
	
	/**
	 * �û�ƥ�����
	 * @param userIDCard
	 * @return
	 */
	public int getUMBaseCMNDpingfen(String userIDCard ) {
	
		int idno_cs =0;
				
		if(!"".equals(userIDCard)) {
			String sfmiwen = Encrypt.MD5(jiami+userIDCard);
	
			JSONObject jsonObject = new JSONObject();
			com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			json.put("secret", sfmiwen);
			json.put("idno", userIDCard);
			
			String url = "http://app.m99vn.com/servlet/current/M99User2?function=GetusercmndAccount";
			String response = HttpUtil.doPost(url, json, "UTF-8");
			com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
			
			int error = json1.getInteger("error");
			if (error == 1) {
				 idno_cs = json1.getInteger("idnocs");
				 logger.info("userIDCard:"+userIDCard);
				 logger.info("idno_cs:"+idno_cs);
	
				return idno_cs;
			} else {
	
				return idno_cs;
		   }
			
		}
	
		return idno_cs;
	}

	/**
	 * �û�����
	 * @param goodjkNum
	 * @param badjkNum
	 * @return
	 */
	public int  getUMBaseUserpingfen(int goodjkNum,int badjkNum) {
		int re_pf =0;
		if(1==goodjkNum) {
			re_pf =60;
		}else if(2==goodjkNum) {
			re_pf=70;
		}else if(3==goodjkNum) {
			re_pf=75;
		}else if(4==goodjkNum) {
			re_pf=80;
		}else if(5==goodjkNum) {
			re_pf=90;
		}else if(6<=goodjkNum) {
			re_pf=100;
		}
		
		return re_pf;
	}
	
	/**
	 * �����û����
	 * @param userIDCard
	 * @return
	 */
	public int getUMBasepingfencmndedu(int userpf) {
	
		int re_pfedu =2000000;
		
		//�����û����
		if(userpf>=70 && userpf<=80 ) {
			re_pfedu =3000000;
			
		}else if(userpf>80 && userpf<100 ) {
			re_pfedu =4000000;
			
		}else if( userpf>=100) {
			re_pfedu =4500000;
			
		}else {
			
			re_pfedu =2000000;
		}
		
		return re_pfedu;
	}

}
