package root.current;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import root.SendMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
//import com.aliyun.oss.OSSClient;
import com.lianlianpay.security.utils.LianLianPaySecurity;
import com.lianpay.api.util.TraderRSAUtil;
import com.project.service.account.JBDLLpayService;
import com.project.service.account.OLVSacombankService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;
/**
 * 连连支付 业务逻辑处理
 * @author lilei
 *
 */
public class JBDLLpayAction extends BaseAction{
	
	
	private static Logger logger = Logger.getLogger(JBDLLpayAction.class);
	private static JBDLLpayService jbdLLpayService = new JBDLLpayService();
	private static OLVSacombankService olvsacombankservice = new OLVSacombankService();
	private static JBDcmsAction jbdcmsaction = new JBDcmsAction();

	String url253 = "https://sms.253.com/msg/send";// 应用地址
	String un253 = "N5186211";// 账号
	String pw253 = "fA5nv7cI9";// 密码	
	String rd253 = "0";// 是否需要状态报告，需要1，不需要0
	String ex253 = null;// 扩展码	
	long time = 1000*60*3;
	//static String url ="http://1s491797d3.51mypc.cn/";
	static String url ="http://www.shandkj.com/";
	
	static final String CHARSET_UTF_8 = "UTF-8";
	static final boolean IS_DEBUG = true;
	private  com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
			  InputStream in = request.getInputStream();
			  byte[] b = new byte[10240];
			  int len;
			  ByteArrayOutputStream baos = new ByteArrayOutputStream();
			  while ((len = in.read(b)) > 0) {
			      baos.write(b, 0, len);
			  }
			  String bodyText = new String(baos.toByteArray(), CHARSET_UTF_8);
			  com.alibaba.fastjson.JSONObject json =  (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
			  if (IS_DEBUG) {
				  logger.info("received notify message:");
				  logger.info(JSON.toJSONString(json, true));
			  }
			  return json;
	  }
	public static void DelectVideo(String videoname){
		// Endpoint以杭州为例，其它Region请按实际情况填写。
		String endpoint = "http://oss-cn-hongkong.aliyuncs.com";
		// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
		String accessKeyId = "LTAI8qRUNoC1lemw";
		String accessKeySecret = "nS892NsXqfrglmHzMsST2SEJns6LBR";
		String bucketName = "htmlhtml";
		String objectName = videoname;
		System.out.println(objectName);
		// 创建OSSClient实例。
//		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
//
//		// 删除文件。
//		ossClient.deleteObject(bucketName, objectName);
//
//		// 关闭OSSClient。
//		ossClient.shutdown();
		logger.info("视频删除成功");
	}
		 //提交第三步审核(视频审核放款)
	    public ActionResult doGetShenhThreeYN() throws Exception {    	
	    	
	    	logger.info("进入到审核第三步");
	    	JSONObject jsonObject = new JSONObject();	
			int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
			 if(cmsuserid == 0){	    		
			   jsonObject.put("error", -1);
			   jsonObject.put("msg", "Vui lòng đăng nhập trước.");//请先登录
			   this.getWriter().write(jsonObject.toString());	
			   return null;
			 }
			 logger.info("请求ID:"+cmsuserid);
			 
			DecimalFormat famt = new DecimalFormat("###,###");
	    	Date  date = new Date();
			Calendar calendar =Calendar.getInstance();
			SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");	
	    	String rec_id = getStrParameter("rec_id");
	    	String state =getStrParameter("state");
	    	String  remarks = getStrParameter("remarks").replace("&nbsp;"," ");
	    	int  userid = getIntParameter("userid",0);
	    	int shqx = jbdLLpayService.getSHQX(cmsuserid);
	    	//判断该项目是否已经被审核（得到第三步审核的状态）
			int cl03_status = jbdLLpayService.getDSBResult(rec_id);
			DataRow datajkinfo = jbdLLpayService.getUserJK(rec_id);
			int shjjqx = jbdLLpayService.getSHJJQX(cmsuserid);
			
			if(cl03_status ==1){
				
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Mục này không còn được xét duyệt nữa. Vui lòng làm mới.");//此项目已不在审核中状态，请刷新
				this.getWriter().write(jsonObject.toString());	
			    return null;
			}
			//判断该用户是否存在未完成的项目
			int hangCount = jbdLLpayService.getHangCount(userid);
	        if( hangCount >1){			
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Tài khoản này có các mục chưa hoàn thành! Vui lòng phản hồi tới bộ phận kỹ thuật để được xử lý.");//该用户存在未完成的项目！请反馈给技术处理
				this.getWriter().write(jsonObject.toString());	
			    return null;
			}
	        
	    	System.out.println(" rec_id:"+rec_id+ "state:"+state+" remarks:"+remarks);
	    	if(state.equals("1")){ //审核成功
	    		int pingfen = jbdLLpayService.getPingFen(rec_id);
				if(pingfen == -1 || pingfen == -2 && shqx == 0){
					jsonObject.put("error", 1);
					jsonObject.put("msg","vui lòng gửi Trưởng phòng Thẩm định xét duyệt！");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
	    		//通过userid获取用户的信息	
	    		
	    		DataRow user = jbdLLpayService.getUserRecThreeInfo(userid); 
	    		String sjsh_money = jbdLLpayService.getSjshsh(rec_id);
	    		int sjsh = Integer.parseInt(sjsh_money.replace(",", ""));
	    		String lx = jbdLLpayService.getLX(rec_id);
	    		int lxlx = Integer.parseInt(lx.replace(",", ""));
		        String merchOrderId = "sd" + System.currentTimeMillis();
		    	DataRow row4 =  new DataRow();	
                row4.set("userid",userid);
	            row4.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
	            row4.set("neirong","Kính gửi"+user.getString("realname")+"Đề xuất vay của bạn đã thông qua bước thẩm định video, chúng tôi sẽ cho giải ngân càng sớm càng tốt, vui lòng kiên nhẫn chờ đợi.");// 亲爱的，您的借款申请已通过视频审核，我们将尽快给您放款，请耐心等待！    		    
	            row4.set("fb_time", fmtrq.format(calendar.getTime()));				
	            jbdLLpayService.insertUserMsg(row4); 
			    DataRow row3 =  new DataRow();	
			    row3.set("userid",userid);
			    row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
			    row3.set("neirong","Kính gửi"+user.getString("realname")+"Đề xuất vay của bạn đã được chấp thuận, khoản tiền vay sẽ được ghi có vào tài khoản của bạn trong vòng 24 giờ, vui lòng kiểm tra tài khoản.");		//您的借款申请已通过，借款款项将在24小时内进入您的账户，请及时查收。             
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
			    jbdLLpayService.insertUserMsg(row3);
				DataRow row = new DataRow() ;
	    		row.set("id", rec_id);
	    		row.set("cl03_status","1");
	    		row.set("cl03_yj",remarks);	
	    		row.set("cl03_ren",cmsuserid);
	    		row.set("spzt","1");
	    		row.set("sfyfk","2");
	    		row.set("cl03_time",fmtrq.format(calendar.getTime()));
	    		jbdLLpayService.updateUserJk(row);
	    		
	    		/*if(hongbaoid !=0){
	    			DataRow rowhong = new DataRow();
		    		rowhong.set("hongbaoid", hongbaoid);
		    		rowhong.set("sfsy", 1);
		    		rowhong.set("usertime", fmtrq.format(calendar.getTime()));
		    		jbdLLpayService.updateUserHongbao(rowhong);
	    		}*/
	    	 }
	    	 if(state.equals("2")){ //视频打回
	    		logger.info("视频打回");
	    		String videoname = jbdLLpayService.getVideoName(rec_id);
	 			DelectVideo(videoname);
	         	DataRow row2 = new DataRow() ;
	     		row2.set("id", rec_id);
	     		row2.set("cl03_status","0");
	     		row2.set("spzt","0");   
	     		row2.set("spdz","");
	     		row2.set("cl03_yj",remarks);	
	     		row2.set("cl03_ren",cmsuserid);     	
	     		row2.set("cl03_time",fmtrq.format(calendar.getTime()));
	     		jbdLLpayService.updateUserJk(row2); 
	     		
	     	   DataRow row3 =  new DataRow();	
				  row3.set("userid", userid);
				  row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
				  row3.set("neirong" ,"Không ghi hình theo yêu cầu,"+remarks+"vui lòng ghi hình lại.");
				  row3.set("fb_time", fmtrq.format(calendar.getTime()));
				  jbdLLpayService.insertUserMsg(row3);
	     	}
	    	if(state.equals("3") || state.equals("4")){
	 			List<DataRow> list = jbdLLpayService.getAllUserJK(userid+"");
	 			int usermark = jbdLLpayService.getUserMark(userid);
	 			if(usermark == 2 && shqx == 0) {
	 				jsonObject.put("error", -1);
 					jsonObject.put("msg","Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này ！");
 					this.getWriter().write(jsonObject.toString());
 					return null;
	 			}
	 			int gzyuqts = jbdLLpayService.getYQTSGZJJ();
	 			int gzyuqcs = jbdLLpayService.getYQCSGZJJ();
	 			for (int i = 0; i < list.size(); i++) {
	 				DataRow row = list.get(i);
	 				int yuqts = row.getInt("yuq_ts");
	 				int yuqjs = 0;
	 				if (yuqts > 0) {
	 					yuqjs++;
	 				}
	 				if (yuqts < gzyuqts && shqx == 0) {
	 					jsonObject.put("error", 1);
	 					jsonObject.put("msg","Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này ！");
	 					this.getWriter().write(jsonObject.toString());
	 					return null;
	 				} else if (yuqjs < gzyuqcs && shqx == 0) {
	 					jsonObject.put("error", 1);
	 					jsonObject.put("msg","Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này !");
	 					this.getWriter().write(jsonObject.toString());
	 					return null;
	 				}
	 			}
	 			
	 			//新用户，有借款记录的 2019年8月6日 lic
	 			if(null != datajkinfo) {
	 				String cl02_ren = datajkinfo.getString("cl02_ren");
	 				
	 				if("888".equals(cl02_ren) && shjjqx != 1) {
	 					jsonObject.put("error", -1);
	 					jsonObject.put("msg","Quyền hạn từ trưởng phòng thẩm định trở lên mới được từ chối KH này !");
	 					this.getWriter().write(jsonObject.toString());
	 					return null;
	 				}
	 			}
	 		}
	        if(state.equals("3")){ //审核失败
	        	DataRow row2 = new DataRow() ;
	    		row2.set("id", rec_id);
	    		row2.set("cl03_status","3");
	    		row2.set("cl03_yj",remarks);
	    		row2.set("spzt","1");
	    		row2.set("cl03_ren",cmsuserid);    	   
	    		row2.set("cl03_time",fmtrq.format(calendar.getTime()));
	    		jbdLLpayService.updateUserJk(row2);  
	    		 DataRow row3 =  new DataRow();	
				  row3.set("userid", userid);
				  row3.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
				  row3.set("neirong" ,"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");
				  row3.set("fb_time", fmtrq.format(calendar.getTime()));
				  jbdLLpayService.insertUserMsg(row3);
	    	}
	       if(state.equals("4")){ //拉入黑名单
	    	   DataRow row5 = new DataRow() ;
	   		   row5.set("id", rec_id);
	   		   row5.set("cl03_status","3");
	   		   row5.set("cl03_yj",remarks);	
	   		   row5.set("spzt","1");
	   		   row5.set("cl03_ren",cmsuserid);
	   		   row5.set("cl03_time",fmtrq.format(calendar.getTime()));
	   		   jbdLLpayService.updateUserJk(row5);    		
	    	   DataRow row3= new DataRow() ;
				 row3.set("id",userid);
				 row3.set("heihu_zt",1);
				 jbdLLpayService.updateUserInfoH(row3);
				 
				 DataRow row6=  new DataRow();	
				  row6.set("userid", userid);
				  row6.set("title", "Thông báo chấp nhận đề nghị vay");//审核通知
				  row6.set("neirong" ,"Rất tiếc, khoản vay lần này không được chấp nhận, có thể là do một hoặc nhiều lý do sau: 1. Khả năng hoàn trả không đủ, tỷ lệ nợ quá cao; 2. Tính ổn định của công việc và thu nhập không đủ; 3. Lịch sử tín dụng xấu; 4. Không cung cấp thông tin theo yêu cầu hoặc các thông tin được cung cấp không đầy đủ; 5. Số điểm đánh giá từ hệ thống không đủ.");//抱歉，本次审核未通过，可能由以下一个或多个原因造成： 1.还款能力不足，负债比过高；2.工作和收入稳定性不足；3.信用记录不良；4.未按要求提供资料或提供的资料不够详尽；5.系统综合评分不足。
				  row6.set("fb_time", fmtrq.format(calendar.getTime()));
				  jbdLLpayService.insertUserMsg(row6);
	    	}
	       
	       jsonObject.put("error", 1);	  
		   this.getWriter().write(jsonObject.toString());	
		   return null ;
	    }
		 //提交第三步审核(视频审核放款)
	    public ActionResult doGetShenhThreeCHG() throws Exception {    	
	    	
	    	logger.info("进入放款");
	    	JSONObject jsonObject = new JSONObject();	
			int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
			 if(cmsuserid == 0){	    		
			   jsonObject.put("error", -1);
			   jsonObject.put("msg", "Vui lòng đăng nhập trước");
			   this.getWriter().write(jsonObject.toString());	
			   return null;
			 }
			 logger.info("请求ID:"+cmsuserid);
	    	
	    	Date  date = new Date();
	    	SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;
	    	SimpleDateFormat fmtrqday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
	    	SimpleDateFormat famatday = new SimpleDateFormat("dd-MM-yyyy");
	    	String timeday = famatday.format(new Date());
	    	DecimalFormat famt = new DecimalFormat("###,###");
			Calendar calendar =Calendar.getInstance();
	    	String rec_id = getStrParameter("rec_id");
	    	logger.info(rec_id);
	    	int jkid = jbdLLpayService.getUserID(rec_id);
	    	int jkdate = jbdLLpayService.getJK(jkid);
	    	DataRow user = jbdLLpayService.getUserRecThreeInfoYN(rec_id); 
			String sjds_money = jbdLLpayService.getSjdzYN(jkid);
			String merchOrderId = "mova" + System.currentTimeMillis();
	    	//判断该项目是否已经被审核（得到第三步审核的状态）
			String sfyfk = jbdLLpayService.getCHGResult(jkid+"");
			int fkjl = jbdLLpayService.getAllFKcount(rec_id,timeday);
			if(fkjl == 1){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Hôm nay đã giải ngân rồi, vui lòng liên hệ IT kiểm tra");
				this.getWriter().write(jsonObject.toString());	
				return null;
			}
			int hongbaoid = jbdLLpayService.getHongbaoid(rec_id);
			int appflyer = jbdLLpayService.getAppflyer(rec_id);
			int jkhongbaoid = jbdLLpayService.getHongbaoidJK(jkid+"");
			String sjsh_money = jbdLLpayService.getSjshsh(jkid+"");
    		int sjsh = Integer.parseInt(sjsh_money.replace(",", ""));
    		String lx = jbdLLpayService.getLX(jkid+"");
    		int lxlx = Integer.parseInt(lx.replace(",", ""));
			int jkhongbaojine = 0;
			if(jkhongbaoid != 0){ 
				//hongbaojine = jbdcmsService.getHongbaojine(hongbaoid);
				jkhongbaojine = 1;
			}
			int code = 0;
			String name = user.getString("username").substring(5, 8);
	    	String name1 = user.getString("username").substring(0, 4);
	    	String registration_id = user.getString("registration_id");
	    	String altertz = "Số tiền bạn đề xuất vay đã được giải ngân đến TK của bạn, vui lòng kiểm tra lại. Trả vay đúng hạn để tăng hạn mức vay cho lần sau nhé.";
	    	
	    	if(name.equals("AND")){
	    		code=1;
	    	}else{
	    		code=2;
	    	}
			if(sfyfk.equals("1")){
				
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Người dùng này không còn ở đây, vui lòng làm mới！");
				this.getWriter().write(jsonObject.toString());	
			    return null;
			}
			
			else{ //审核成功
	    		//通过userid获取用户的信息	
			    DataRow row3 =  new DataRow();	
			    row3.set("userid",rec_id);
			    row3.set("title", "Thông báo chấp thuận đề xuất vay");//审核通知
			    row3.set("neirong","Khoản vay của bạn đã được giải ngân vào tài khoản của bạn, vui lòng kiểm tra tài khoản.");	//您的借款已经汇入您的账户，请及时查收。	             
			    row3.set("fb_time", fmtrq.format(calendar.getTime()));				
			    jbdLLpayService.insertUserMsg(row3); 
		         //还款期限			   
			    DataRow row = new DataRow() ;
			    DataRow row1 = new DataRow() ;
			    row.set("fkdz_time", fmtrq.format(calendar.getTime())) ; 
			    row.set("fkdz_time_day", fmtrqday.format(calendar.getTime())) ; 
			    row.set("fkr_time",fmtrq.format(calendar.getTime()));
			    row1.set("checktime", fmtrq.format(calendar.getTime())) ; 
			    if(jkdate == 2){
		        	 //当前时间加30天
		        	  calendar.add(Calendar.DATE, 30);		        	
		         }
		         if(jkdate == 1){
		        	 //当前时间加15天
		        	  calendar.add(Calendar.DATE, 15);    	 
		         }
		         if(jkdate == 3){
		        	 //当前时间加15天
		        	 calendar.add(Calendar.DATE, 7);    	 
		         }
		         if(jkdate == 4){
		        	 //当前时间加15天
		        	 calendar.add(Calendar.DATE, 14);    	 
		         }
	    		row.set("id", jkid);
	    		row.set("hkyq_time", fmtrq.format(calendar.getTime())) ; 
	    		row.set("hk_time", fmtrq.format(calendar.getTime())) ; 
	    		row.set("sfyfk","1");
	    		if(jkhongbaoid ==1){
	    			row.set("sjsh_money",famt.format(sjsh-10000));
		    		row.set("lx",famt.format(lxlx-10000));
	    		}
	    		row.set("fkr",cmsuserid);
	    		jbdLLpayService.updateUserJk(row);	
				row1.set("name", user.getString("cardusername"));
				row1.set("cellphone", user.getString("mobilephone"));
				row1.set("acount", user.getString("cardno"));
				row1.set("sum", sjds_money);
	    		row1.set("userid", rec_id);
	    		row1.set("remark", "放款") ; 
	    		row1.set("checkid", cmsuserid) ; 
	    		row1.set("orderid", merchOrderId) ; 
	    		jbdLLpayService.updateUserFK(row1);
	    		if(hongbaoid == 0 && jkhongbaoid ==1){
	    			DataRow rowhong = new DataRow();
		    		rowhong.set("id", rec_id);
		    		rowhong.set("hongbao", 1);
		    		jbdLLpayService.updateUserHongbao(rowhong);
	    		}
	    		if(appflyer == 2){
	    			DataRow rowhong = new DataRow();
		    		rowhong.set("id", rec_id);
		    		rowhong.set("appflyer", 1);
		    		jbdLLpayService.updateUserHongbao(rowhong);
	    		}
	    	 }
			DataRow rowhong111 = new DataRow();
			int oldyonghu = jbdLLpayService.getOLDYH(rec_id);
    		rowhong111.set("id", rec_id);
    		rowhong111.set("oldyonghu", oldyonghu+1);
    		jbdLLpayService.updateUserHongbao(rowhong111);
			String userName =user.getString("username");
			  String appName ="OCEAN";
			    userName =userName.substring(0,4);					  
			    if(userName.equals("OCEAN")){
			    	appName="OCEAN";					    	
			    }
		   String content = "[{\"PhoneNumber\":\""+user.getString("mobilephone")+"\",\"Message\":\""+appName+" thong bao: So tien vay "+sjds_money+" da chuyen khoan den TK Ngan hang so cuoi "+user.getString("cardno").substring(user.getString("cardno").length()-4, user.getString("cardno").length())+".Neu sau 24 tieng chua nhan duoc tien, vui long goi hotline: 1900234558, inbox http://bit.ly/2QJAh16.\",\"SmsGuid\":\""+user.getString("mobilephone")+"\",\"ContentType\":1}]";
		   String con = URLEncoder.encode(content, "utf-8");
		   SendMsg sendMsg = new SendMsg();
		   //String returnString = SendMsg.sendMessageByGet(con,user.getString("mobilephone")); 

	       jsonObject.put("error", 1);
	       jsonObject.put("msg","Thành công");//成功 
		   this.getWriter().write(jsonObject.toString());	
		   return null ;
	    }
	    //提交第三步审核(视频审核放款)
	    public ActionResult doGetShenhThreeSB() throws Exception {    	
	    	
	    	logger.info("进入失败");
	    	JSONObject jsonObject = new JSONObject();	
			int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());//后台登录账户
			 if(cmsuserid == 0){	    		
			   jsonObject.put("error", -1);
			   jsonObject.put("msg", "Vui lòng đăng nhập trước.");//请先登录
			   this.getWriter().write(jsonObject.toString());	
			   return null;
			 }
			 logger.info("请求ID:"+cmsuserid);
	    	Date  date = new Date();
			Calendar calendar =Calendar.getInstance();
			SimpleDateFormat fmtrq  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");	
	    	String rec_id = getStrParameter("rec_id");
	    	int jkid = jbdLLpayService.getUserID(rec_id);
	    	int jkdate = jbdLLpayService.getJK(jkid);
	    	//判断该项目是否已经被审核（得到第三步审核的状态）
			int fkr = jbdLLpayService.getSBResult(jkid+"");
			if(fkr != 0){
				
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Mục này không còn được xét duyệt nữa. Vui lòng làm mới.");//此项目已不在审核中状态，请刷新
				this.getWriter().write(jsonObject.toString());	
			    return null;
			}
			else{ //审核成功 
				DataRow row = new DataRow() ;
	    		row.set("id", jkid);
	    		row.set("sfyfk","3");
	    		row.set("fkr",cmsuserid);
	    		row.set("fkr_time",fmtrq.format(calendar.getTime()));
	    		jbdLLpayService.updateUserJk(row);	        
	    	 }
	    	 
	       jsonObject.put("error", 1);
	       jsonObject.put("msg","Thành công");//成功 
		   this.getWriter().write(jsonObject.toString());	
		   return null ;
	    }
}
