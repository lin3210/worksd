package root.current;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import root.PostHttp;
import root.SendFTP;
import root.SendMsg;
import root.order.UserMoneyBase;
import root.role.roleAuthorityMangement;
import sun.misc.BASE64Decoder;
import com.alibaba.fastjson.JSON;
import com.project.service.account.JBDLLpayService;
import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcms3Service;
import com.project.service.account.JBDcmsService;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

/**
 * 后台报表
 * 
 * @author lilei
 * 
 */
public class JBDcms3Action extends BaseAction {

	private static Logger logger = Logger.getLogger(JBDcms3Action.class);
	private static JBDcms3Service jbdcms3Service = new JBDcms3Service();
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	private static JBDLLpayService jbdLLpayService = new JBDLLpayService();
	private static JBDUserService jbdUserService = new JBDUserService();
	private static AccessVerifivationBase accessVeritifivationbase = new AccessVerifivationBase();
	private static roleAuthorityMangement roleauthoritymangement  = new roleAuthorityMangement();

	static final String CHARSET_UTF_8 = "UTF-8";
	static final boolean IS_DEBUG = true;
	
	String jiami = "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh";

	private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3+bWQP/qqpjzT2XruGDwhbWDl"
			+ "ofPNuvufFm5+vrQvG8WhBrN63hfwg+NhR2NQxvLRaGNoklWKp4hUZa3rm6myMLaB"
			+ "GKV3BEuXl1CSWf1TBJSBMT1zy5C/G+3w1/jrf779QtX0gUixQ9YdwHShJTcBE+0q"
			+ "hS7MqF+y5Z5NXy+fLwIDAQAB";

	private static final String DEFAULT_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALf5tZA/+qqmPNPZ"
			+ "eu4YPCFtYOWh8826+58Wbn6+tC8bxaEGs3reF/CD42FHY1DG8tFoY2iSVYqniFRl"
			+ "reubqbIwtoEYpXcES5eXUJJZ/VMElIExPXPLkL8b7fDX+Ot/vv1C1fSBSLFD1h3A"
			+ "dKElNwET7SqFLsyoX7Llnk1fL58vAgMBAAECgYBo5NDmW/QZlAqeZyM12U9/Z5OV"
			+ "mc8d/3wzamC5lxW4vkbh1qZCaZqQoUHlVwSDK8uKJdB38Ocg1QBfzlFpQilv3egS"
			+ "lKcrk8YkgsUmpJcn4omKItB4iGEvYY8bJusLzaQEpRBmeM9plxA76aC20DFEDUR+"
			+ "9z01rWuhRz5UXk56wQJBANpvUtxj7RnJMK0/0GDGYn5pj2Z6GcNy8VI9ntxYQMRF"
			+ "xFnvYSfEib2oE4hfG0pHBWvBtDrXDi5XD851bo8eGx8CQQDXnUvTzhvoVSezChMz"
			+ "1Zvfl8b4TiyTSuZWVuhrlnQqQgAGLOKfdBsz2A0jfDfzktjLbAoVzpPpvVaSI/Qy"
			+ "nAnxAkEAwGyTPS0WIMIYjHaL1cTN3XiWZ/smGQR3zDAWcxuXqo+fQm7bUpITmSyo"
			+ "UFkgDFX2U4/nenIavv3ZIdJXW+J0lwJBAK3pLOlJXM84KE5MORLdH93ocU+E1oVz"
			+ "q3hGny9waoBPPe+9MonEv9BAWtCdeA/aCU2C9luChWHKG1LC90v++jECQETxyI7l"
			+ "6UDwfky+s4pAhbrvsWdv09d/zsyQe9g899op6XHIchAXAHve1QgLxWOJfWrww924"
			+ "c21h3y9MuDyTsmY=";
	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;

	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;

	/**
	 * 获得客户的真实IP地址
	 * 
	 * @return
	 */
	public String getipAddr() {
		String online =SessionHelper.getString("online", getSession());
		return online;
	}
	
	/**
	 * 实现线下支付
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doOfflinePaymentlTest() throws Exception {
		
		logger.info("进入线下支付还款结果返回");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
	    
		int operatorId = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (operatorId == 0) {
			jsonObject.put("error", -1);
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + operatorId);
		String jkid = getStrParameter("rec_id"); // 接收到的还款id
		logger.info(jkid);
		String hkmoney = getStrParameter("hkmoney").replace(",", ""); // 还款金额
		logger.info(hkmoney);
		boolean result = hkmoney.matches("[0-9]+");
		if (result == false) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Xin nhập đúng format thời gian");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String sjsh_money = jbdcms3Service.getsjsh(jkid);

		// = jbdcms3Service.getHKhongbaojine(hkhongbaoid);
		String hkqd = jbdcms3Service.gethkqd(jkid);
		int yqts = jbdcms3Service.getyqts(jkid);
		int jkdate = jbdcms3Service.gethkdate(jkid);
		if (jkdate == 0) {
			jsonObject.put("error", -4);
			jsonObject.put("msg", "Thời hạn vay của KH trống, vui lòng liên hệ IT kiểm tra");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String yuq_lx = jbdcms3Service.getYQLX(jkid);// 逾期利息
		String yuq_yhlx = jbdcms3Service.getYQYH(jkid); // 逾期利息
		String sjds = jbdcms3Service.getSJDS(jkid); // 实际到手
		String lx = jbdcms3Service.getLX(jkid); // 利息
		String newsjsh_money = sjsh_money.replace(",", ""); // 逾期利息
		String newsjds_money = sjds.replace(",", ""); // 逾期利息
		String newlx_money = lx.replace(",", ""); // 逾期利息
		String newyuq_lx = yuq_lx.replace(",", ""); // 逾期利息
		String newyuq_yhlx = yuq_yhlx.replace(",", ""); // 逾期利息
		int sjsh = Integer.parseInt(newsjsh_money);
		int zjsjsh = Integer.parseInt(newsjds_money)
				+ Integer.parseInt(newlx_money); // 200万基数
		int hkm = Integer.parseInt(hkmoney);
		int yqlx = Integer.parseInt(newyuq_lx);
		int yqyhlx = Integer.parseInt(newyuq_yhlx);
		int sum = sjsh + yqlx;
		DecimalFormat df = new DecimalFormat("###,###");
		String sum1 = df.format(sum);
		int hkbank = getIntParameter("hkbank"); // 接收到的用户还款银行选择
		String bankorderid = getStrParameter("bankorderid");
		if (TextUtils.isEmpty(bankorderid)) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "Mã đơn là 0");
			this.getWriter().write(jsonObject.toString());
			return null;
		}else{
			bankorderid =bankorderid.replace("&acute;", "/").replace("&nbsp;", " ").trim(); // 接收到的用户还款银行订单号
		}
		int hkbankid = jbdcms3Service.getHKOrder(hkbank,bankorderid);
		if(hkbankid != 0){
			jsonObject.put("error", -4);
			jsonObject.put("msg", "Trùng mã đơn, ID trùng là"+hkbankid);
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String userId = getStrParameter("user_id"); // 接收到的用户id
		int state = getIntParameter("state"); // 接收到还款选择
		int yqstate = getIntParameter("yqstate"); // 接收到延期期限选择

		String HKtime = getStrParameter("hksj").replace("&nbsp;", " ");// 接收还款时间
		Date dt = new Date();
		Long time = dt.getTime();// 这就是距离1970年1月1日0点0分0秒的毫秒数
		String payOrder = "mova" + time;
		logger.info("还款订单：" + payOrder);
		logger.info(userId);
		logger.info(HKtime);
		SimpleDateFormat fmtnyr = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmtyn = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证 日期，比如2007/02/29会被接受，
			// 并转换成2007/03/01
			fmtrq.setLenient(false);
			Date date = fmtrq.parse(HKtime);

		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			jsonObject.put("error", -3);
			jsonObject.put("msg", "thời gian hiện tại");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String hkyqdate = jbdcms3Service.getHKYQtime(jkid).substring(0, 10);
		String hkfqdate = jbdcms3Service.getHKFQtime(jkid);
		SimpleDateFormat fmtrqcz = new SimpleDateFormat("dd-MM-yyyy");
		long millionSeconds1 = fmtrqcz.parse(HKtime).getTime();// 实际还款时间
		long millionSeconds2 = fmtrqcz.parse(hkyqdate).getTime();// 还款时间
		long millionSeconds3 = fmtrqcz.parse(fmtrqcz.format(new Date())).getTime();// 当前时间
		if (!TextUtils.isEmpty(hkfqdate)) {
			millionSeconds2 = fmtrqcz.parse(hkfqdate).getTime();// 还款时间
		}
		if(millionSeconds1 > millionSeconds3){
			jsonObject.put("error", -5);
			jsonObject.put("msg", "Xin kiểm tra thời gian trả vay, vượt quá thời gian hiện tại！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String date2 = HKtime;
		DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userId));
		String username = dataRow2.getString("username");
		DataRow dataRowjk = jbdcms3Service.getUserJKInfo(jkid);
		SimpleDateFormat fmtrq111 = new SimpleDateFormat("yyyy-MM-dd");
		String time111 = fmtrq111.format(new Date());
		String cuishouid = dataRowjk.getString("cuishou_id");
		String cuishoum0 = dataRowjk.getString("cuishou_m0");
		String cuishoum1 = dataRowjk.getString("cuishou_m1");
		String cuishoum2 = dataRowjk.getString("cuishou_m2");
		String cuishoum3 = dataRowjk.getString("cuishou_m3");
		DataRow row6 = new DataRow();
		// 支付成功（表示还款成功）
		DataRow addorder = new DataRow();
		int orderpaytype = 3; // 线下还款为3
		String remark = "";
		if (state == 1) {
			remark = "全额还款";
		} else if (state == 2) {
			if (yqlx >= hkm) {
				remark = "逾期利息还款";
			} else {
				remark = "逾期利息还完，部分本金还款";
			}
		} else if (state == 3) {
			remark = "完成还款，部分逾期利息还款";
		} else if (state == 4) {
			remark = "延期还款";
			addorder.set("yqdate", yqstate);
		}

		String payNumber = payOrder;
		// 生成订单
		addorder.set("userid", userId);
		addorder.set("rechargetime", fmtrq.format(new Date()));
		addorder.set("userhktime", HKtime);
		addorder.set("bankjztime", getStrParameter("hkjz").replace("&nbsp;", " "));
		addorder.set("rechargetype", 33);
		addorder.set("result", 1);
		addorder.set("operatorId", operatorId);
		addorder.set("rzcode", "0000");
		addorder.set("paynumber", payNumber);
		addorder.set("rechargenumber", jkid);
		addorder.set("remark", remark);
		
		addorder.set("ordertype", 1);
		addorder.set("hkbank", hkbank);
		addorder.set("bankorderid", bankorderid);
		addorder.set("resultInfo", hkqd);
		addorder.set("orderpaytype", orderpaytype);
		addorder.set("cuishouid", dataRowjk.getString("cuishou_id"));

		if (state == 1) {
			logger.info("------------------1-------");
			//不管用户有没有延期，在延期和正常的时间内还款，都是扣掉催收的总金额
			if (millionSeconds2 >= millionSeconds1) {
				// 算出产生多的逾期天数
				addorder.set("dqyqts", 0);
				long chazhi = millionSeconds3 - millionSeconds2;
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("jksfwc", 1);
				jkxm2.set("sfyhw", 1);
				if(ddday < 0){
					sum = sjsh + yqlx ;
					addorder.set("dqyqlx", yqlx);
				}else{
					sum = sjsh + yqlx - ddyqlx ;
					addorder.set("dqyqlx", yqlx - ddyqlx);
					jkxm2.set("yuq_lx", df.format(yqlx - ddyqlx));
					jkxm2.set("yuq_ts", yqts - ddday);
				}
				int qxqxqx = jbdcms3Service.getQxcaiwu(operatorId);
				int hkrow = jbdcms3Service.getHKRow(userId,HKtime,df.format(sum));
				if(hkrow != 0 && qxqxqx == 0){
					jsonObject.put("error", -5);
					jsonObject.put("msg", "Hiện tại đã có 1 món trả vay cùng số tiền, vui lòng kiểm tra, xác nhận không có vấn đề thì báo IT mở quyền hạn.");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				addorder.set("rechargemoney",df.format(sum));
				
				jkxm2.set("hk_time", date2);
				
				jbdcms3Service.updateJKHK(jkxm2);
				// 用户没有逾期，这里的钱不加在催收里面，并在催收总额里面去掉
				if(ddday > 0){
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid, time111);
					if (datacs111 == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -sum);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - sum);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
				
				//lic 2019年7月30日   分单表的数据更新  //***1::全额还款用户
				if(yqts >0) {
					int jk_yqts = yqts;
					int rm_dqyqts =  0;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							
							logger.info("------------------11-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								//,d当前催人的入催金额改为0
								String fendan_id =datarodq.getString("id");
								int recharge_money = datarodq.getInt("recharge_money");
								
								jbdcms3Service.updatefendaninfoid(0,recharge_money, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("全额还款  01 当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id"));
							}
							
							//a实际催收人
							DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
							if(datarowsj!=null) {
								String fendan_id =datarowsj.getString("id");
								int recharge_money = datarowsj.getInt("recharge_money");
	
								jbdcms3Service.updatefendaninfoid(0,recharge_money, fendan_id,cuishouid);
								logger.info("全额还款  01 实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " sum:"+sum);
							}
							
						}
						logger.info("cuishouid"+ cuishouid); 
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
					
				}
				
			}
			// 在规定时间外还款（也就是逾期用户）
			else {
				logger.info("------------------1-------");
				// 算出产生多的逾期天数，当前的时间只会大于等于实际还款时间
				long chazhi = millionSeconds3 - millionSeconds1;
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				addorder.set("rechargemoney", df.format(sjsh + yqlx - ddyqlx));
				addorder.set("dqyqts", yqts - ddday);
				addorder.set("dqyqlx", yqlx - ddyqlx);
				sum = sjsh + yqlx - ddyqlx ;
				
				int qxqxqx = jbdcms3Service.getQxcaiwu(operatorId);
				int hkrow = jbdcms3Service.getHKRow(userId,HKtime,df.format(sum));
				if(hkrow != 0 && qxqxqx == 0){
					jsonObject.put("error", -5);
					jsonObject.put("msg", "Hiện tại đã có 1 món trả vay cùng số tiền, vui lòng kiểm tra, xác nhận không có vấn đề thì báo IT mở quyền hạn.");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("jksfwc", 1);
				jkxm2.set("sfyhw", 1);
				jkxm2.set("yuq_lx", df.format(yqlx - ddyqlx));
				jkxm2.set("yuq_ts", yqts - ddday);
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 逾期天数>0，增加催收的催回金额
				if ((yqts - ddday) > 0) {
					if((yqts - ddday) < 16){
						cuishouid = cuishoum1;
					}else if((yqts - ddday) > 15 && (yqts - ddday) < 46){
						cuishouid = cuishoum2;
					}
					DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid,time111);
					if (datacs == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("ycsjine", sum);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs.getDouble("ycsjine");
						int cuiid = datacs.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("ycsjine", ysje + sum);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
				
				//lic 2019年7月30日   分单表的数据更新1  //***全额还款用户
				if(yqts >0) {
					
					int jk_yqts = yqts;
					int rm_dqyqts =  yqts - ddday;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							
							logger.info("------------------12-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								//,d当前催人的入催金额改为0
								String fendan_id =datarodq.getString("id");
								int recharge_money = datarodq.getInt("recharge_money");
								
								jbdcms3Service.updatefendaninfoid(0,recharge_money, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("全额还款  01 当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id"));
							}
							
							//a实际催收人
							if(rm_dqyqts>0) {
								DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
								if(datarowsj!=null) {
									String fendan_id =datarowsj.getString("id");
									int cuishoujine = datarowsj.getInt("cuishou_jine");
									int recharge_money = datarowsj.getInt("recharge_money")+sum;
		
									jbdcms3Service.updatefendaninfoid(cuishoujine,recharge_money, fendan_id,cuishouid);
									logger.info("全额还款  01 实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " sum:"+sum);
								}else {
									
									DataRow row = new DataRow();
									row.set("user_id", userId);
									row.set("jk_id", jkid);
									row.set("cuishou_id", cuishouid);
									row.set("fendan_time", fmtnyr.format(new Date()));
									row.set("cuishou_jine", sum);
									row.set("cuishou_z", dqyuts_z);
									row.set("fendan_cs", 3);
									row.set("recharge_money", sum);
									jbdcms3Service.insertCuishoufenan(row);
									logger.info(" 插入全额还款  01 实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " sum:"+sum);
								}
							}
							
						}
						logger.info("cuishouid"+ cuishouid); 
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
					
				}
			}
			
		} else if (state == 2) {
			logger.info("------------------2------");
			addorder.set("rechargemoney", df.format(hkm));
			int qxqxqx = jbdcms3Service.getQxcaiwu(operatorId);
			int hkrow = jbdcms3Service.getHKRow(userId,HKtime,df.format(hkm));
			if(hkrow != 0 && qxqxqx == 0){
				jsonObject.put("error", -5);
				jsonObject.put("msg", "Hiện tại đã có 1 món trả vay cùng số tiền, vui lòng kiểm tra, xác nhận không có vấn đề thì báo IT mở quyền hạn.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			// 在规定的时间内还款,都不算给催收，从催收总金额减掉
			if (millionSeconds2 >= millionSeconds1) {
				addorder.set("dqyqts", 0);
				long chazhi = millionSeconds3 - millionSeconds2;
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				//算出原有的天数和利息
				//差值小于0，表示在规定时间内还款的
				int yylx = 0;
				int xsjsh = 0;
				int xyqlx = 0;
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				
				if(ddday < 0){
					addorder.set("dqyqlx", yqlx);
					if (hkm <= yqlx) {
						jkxm2.set("yuq_yhlx", df.format(hkm + yqyhlx));
						jkxm2.set("yuq_lx", df.format(yqlx - hkm));
					} else {
						xsjsh = sjsh+yqlx-hkm;
						jkxm2.set("sjsh_money", df.format(xsjsh));
						jkxm2.set("yuq_yhlx", df.format(yqlx + yqyhlx));
						jkxm2.set("yuq_lx", 0);
					}
				}else{
					addorder.set("dqyqlx", yqlx - ddyqlx);
					yylx = yqlx - ddyqlx;
					if (hkm <= yylx) {
						xsjsh = sjsh;
						xyqlx = 0;
						if(jkdate ==3 || jkdate ==4){
							xyqlx = yylx + (xsjsh * 4 / 100) * ddday;
						}else{
							xyqlx = yylx + (xsjsh * 4 / 100) * ddday;
						}
						jkxm2.set("yuq_yhlx", df.format(hkm + yqyhlx));
						jkxm2.set("yuq_lx", df.format(xyqlx - hkm));
					} else {
						xsjsh = sjsh + yylx - hkm;
						xyqlx = 0;
						if(jkdate ==3 || jkdate ==4){
							xyqlx = (xsjsh * 4 / 100) * ddday;
						}else{
							xyqlx = (xsjsh * 4 / 100) * ddday;
						}
						jkxm2.set("sjsh_money", df.format(xsjsh));
						jkxm2.set("yuq_yhlx", df.format(yylx + yqyhlx));
						jkxm2.set("yuq_lx", df.format(xyqlx));
					}
				}
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额从催收总额里面去掉
				if(ddday > 0){
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid, time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}	
				
				//lic 2019年7月30日   分单表的数据更新  //***2 ：：未还完-----还部分、或利息
				if(yqts >0) {
					int jk_yqts = yqts;
					int rm_dqyqts = 0;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(ddday < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							logger.info("------------------21-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								String fendan_id =datarodq.getString("id");
								int cuishoujine = datarodq.getInt("cuishou_jine") - hkm;
								int rechargemoney = datarodq.getInt("recharge_money") - hkm;
								//,d当前催人的入催金额 减少
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("未还完-----还部分、或利息  01 当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" hkm:"+hkm);
							}
							
							//a实际催收人
							DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
							if(datarowsj!=null) {
								String fendan_id =datarowsj.getString("id");
								int cuishoujine = datarowsj.getInt("cuishou_jine")-hkm;
								int rechargemoney = datarowsj.getInt("recharge_money") - hkm;
								
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,cuishouid);
								
								logger.info("未还完-----还部分、或利息  实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
							}
						}
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
					
				}
				
			}
			// 在规定时间外还款（也就是逾期用户）
			else {
				logger.info("------------------2-------");
				// 有没有延期不影响最终的逾期状态
				long chazhi = millionSeconds3 - millionSeconds1;// 当前时间距离实际还款时间的差值
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				int yylx = yqlx - ddyqlx;
				int yyts = yqts - ddday;
				addorder.set("dqyqts", yyts);
				addorder.set("dqyqlx", yqlx - ddyqlx);
				int xsjsh = 0;
				int xyqlx = 0;
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				if (hkm >= yylx) {
					xsjsh = sjsh - hkm + yylx;
					if(jkdate == 3 || jkdate == 4){
						xyqlx = (xsjsh * 4 / 100) * ddday;
					}else{
						xyqlx = (xsjsh * 4 / 100) * ddday;
					}
					
					jkxm2.set("sjsh_money", df.format(xsjsh));
					jkxm2.set("yuq_yhlx", df.format(yylx + yqyhlx));
					jkxm2.set("yuq_lx", df.format(xyqlx));
				} else {
					if(jkdate == 3 || jkdate == 4){
						xyqlx = yylx - hkm + (sjsh * 4 / 100) * ddday;
					}else{
						xyqlx = yylx - hkm + (sjsh * 4 / 100) * ddday;
					}
					jkxm2.set("yuq_yhlx", df.format(hkm + yqyhlx));
					jkxm2.set("yuq_lx", df.format(xyqlx));
				}
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额加到催回金额里面
				if(yyts > 0){
					if(yyts < 16 ){
						cuishouid = cuishoum1 ;
					}
					else if((yqts - ddday) > 15 && (yqts - ddday) < 46){
						cuishouid = cuishoum2;
					}
					DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid, time111);
					if (datacs == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("ycsjine", hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs.getDouble("ycsjine");
						int cuiid = datacs.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("ycsjine", ysje + hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
				
				//lic 2019年7月30日   分单表的数据更新  //***2 ：：未还完-----还部分、或利息
				if(yqts >0) {
					int jk_yqts = yqts;
					int rm_dqyqts = yyts;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							logger.info("------------------22-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								String fendan_id =datarodq.getString("id");
								int cuishoujine = datarodq.getInt("cuishou_jine") - hkm;
								int rechargemoney = datarodq.getInt("recharge_money") - hkm;
								//,d当前催人的入催金额 减少
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("未还完-----还部分、或利息  当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" hkm:"+hkm);
							}
							
							//a实际催收人
							if(rm_dqyqts>0) {
								DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
								if(datarowsj!=null) {
									String fendan_id =datarowsj.getString("id");
									int cuishoujine = datarowsj.getInt("cuishou_jine");
									int rechargemoney = datarowsj.getInt("recharge_money") + hkm;
									
									jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,cuishouid);
									logger.info("未还完-----还部分、或利息    实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
								}else {
									DataRow row = new DataRow();
									row.set("user_id", userId);
									row.set("jk_id", jkid);
									row.set("cuishou_id", cuishouid);
									row.set("fendan_time", fmtnyr.format(new Date()));
									row.set("cuishou_jine", hkm);
									row.set("cuishou_z", dqyuts_z);
									row.set("fendan_cs", 3);
									row.set("recharge_money", hkm);
									jbdcms3Service.insertCuishoufenan(row);
									logger.info(" 插入 未还完-----还部分、或利息  01 实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
								}
							}
						}
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
				}
			}
		} else if (state == 3) {
			logger.info("------------------3-------");
			addorder.set("rechargemoney", df.format(hkm));
			int qxqxqx = jbdcms3Service.getQxcaiwu(operatorId);
			int hkrow = jbdcms3Service.getHKRow(userId,HKtime,df.format(hkm));
			if(hkrow != 0 && qxqxqx == 0){
				jsonObject.put("error", -5);
				jsonObject.put("msg", "Hiện tại đã có 1 món trả vay cùng số tiền, vui lòng kiểm tra, xác nhận không có vấn đề thì báo IT mở quyền hạn.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			// 在规定的时间内还款
			if (millionSeconds2 >= millionSeconds1) {
				// 有延期,有没有逾期都没有分催收员
				addorder.set("dqyqts", 0);
				
				long chazhi = millionSeconds3 - millionSeconds2;
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				// 有逾期天数和利息,算出原有的天数和利息
				int yylx = 0;
				int yyts = 0;
				if(ddday < 0){
					addorder.set("dqyqlx", yqlx);
					yylx = yqlx;
					yyts = yqts;
				}else{
					if(yqlx - ddyqlx>0) {
						yylx = yqlx - ddyqlx;
					}
					addorder.set("dqyqlx", yylx);
					yylx = yqlx - ddyqlx;
					yyts = yqts - ddday;
				}
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("jksfwc", 1);
				jkxm2.set("sfyhw", 1);
				if(hkm - sjsh<0) {
					jkxm2.set("yuq_whlx", df.format(yylx + sjsh - hkm));
				}else {
					jkxm2.set("yuq_whlx", 0);
				}
				if(hkm - sjsh<=0) {
					jkxm2.set("yuq_lx", 0);
				}else {
					jkxm2.set("yuq_lx", df.format(hkm - sjsh));
				}
				jkxm2.set("yuq_ts", yyts);
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额从催收总额里面去掉
				if(ddday > 0){
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}

				//lic 2019年7月30日   分单表的数据更新  //***3 ：：完成还款，部分逾期利息还款
				if(yqts >0) {
					
					int jk_yqts = yqts;
					int rm_dqyqts = 0;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							logger.info("------------------31-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								String fendan_id =datarodq.getString("id");
								int cuishoujine = datarodq.getInt("cuishou_jine") - hkm;
								int rechargemoney = datarodq.getInt("recharge_money") - hkm;
								//,d当前催人的入催金额 减少
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
								
								logger.info("完成还款，部分逾期利息还款  当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" hkm:"+hkm);
							}
							
							//a实际催收人
							DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
							if(datarowsj!=null) {
								String fendan_id =datarowsj.getString("id");
								int cuishoujine = datarowsj.getInt("cuishou_jine") - hkm;
								int rechargemoney = datarowsj.getInt("recharge_money") - hkm;
								
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,cuishouid);
								logger.info("完成还款，部分逾期利息还款  实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
							}
						}
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
				
				}
				
			}
			// 在规定时间外还款（也就是逾期用户）
			else {
				logger.info("------------------3-------");
				// 有没有延期不影响最终的逾期状态
				long chazhi = millionSeconds3 - millionSeconds1;// 当前时间距离实际还款时间的差值
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				int yylx = 0;
				if(yqlx - ddyqlx>0) {
					yylx = yqlx - ddyqlx;
				}
				int yyts = yqts - ddday;
				addorder.set("dqyqts", yyts);
				addorder.set("dqyqlx", yylx);

				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("jksfwc", 1);
				jkxm2.set("sfyhw", 1);
				if(hkm - sjsh<0) {
					jkxm2.set("yuq_whlx", df.format(yylx + sjsh - hkm));
				}else {
					jkxm2.set("yuq_whlx", 0);
				}
				if(hkm - sjsh<=0) {
					jkxm2.set("yuq_lx", 0);
				}else {
					jkxm2.set("yuq_lx", df.format(hkm - sjsh));
				}
				jkxm2.set("yuq_ts", yyts);
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额加到催回金额里面
				if(yyts > 0){
					if(yyts < 16){
						cuishouid = cuishoum1 ;
					}else if((yqts - ddday) > 15 && (yqts - ddday) < 46){
						cuishouid = cuishoum2;
					}
					DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid, time111);
					if (datacs == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("ycsjine", hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs.getDouble("ycsjine");
						int cuiid = datacs.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("ycsjine", ysje + hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
				
				//lic 2019年7月30日   分单表的数据更新  //***3 ：：完成还款，部分逾期利息还款
				if(yqts >0) {
					
					int jk_yqts = yqts;
					int rm_dqyqts = yyts;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							logger.info("------------------32-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								String fendan_id =datarodq.getString("id");
								int cuishoujine = datarodq.getInt("cuishou_jine") - hkm;
								int rechargemoney = datarodq.getInt("recharge_money") - hkm;
								//,d当前催人的入催金额 减少
								jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("完成还款，部分逾期利息还款  当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" hkm:"+hkm);
							}
							
							//a实际催收人
							if(rm_dqyqts>0) {
								DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
								if(datarowsj!=null) {
									String fendan_id =datarowsj.getString("id");
									int cuishoujine = datarowsj.getInt("cuishou_jine");
									int rechargemoney = datarowsj.getInt("recharge_money") + hkm;
									
									jbdcms3Service.updatefendaninfoid(cuishoujine,rechargemoney, fendan_id,cuishouid);
									logger.info("完成还款，部分逾期利息还款  实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
								}else {
									DataRow row = new DataRow();
									row.set("user_id", userId);
									row.set("jk_id", jkid);
									row.set("cuishou_id", cuishouid);
									row.set("fendan_time", fmtnyr.format(new Date()));
									row.set("cuishou_jine", hkm);
									row.set("cuishou_z", dqyuts_z);
									row.set("fendan_cs", 3);
									row.set("recharge_money", hkm);
									jbdcms3Service.insertCuishoufenan(row);
									logger.info(" 插入 完成还款，部分逾期利息还款 01 实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
								}
							}
						}
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
				}
			}

		} else if (state == 4) {
			logger.info("------------------4-------");
			addorder.set("rechargemoney", df.format(hkm));
			int qxqxqx = jbdcms3Service.getQxcaiwu(operatorId);
			int hkrow = jbdcms3Service.getHKRow(userId,HKtime,df.format(hkm));
			if(hkrow != 0 && qxqxqx == 0){
				jsonObject.put("error", -5);
				jsonObject.put("msg", "Hiện tại đã có 1 món trả vay cùng số tiền, vui lòng kiểm tra, xác nhận không có vấn đề thì báo IT mở quyền hạn.");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			int yqyqlx = 0;
			if (yqstate == 0) {
				jsonObject.put("error", 0);
				jsonObject.put("msg", "请选择延期期限！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}else {
				int yqjklx = userMoneyBase.getUMBaseCalculateProductInterest(sjsh, yqstate, Integer.parseInt(userId), username);
				yqyqlx = sjsh * yqjklx / 100;
			}
//			} else if (yqstate == 1) {
//			if (sjsh <= 1000000) {
//				yqyqlx = sjsh * 30 / 100;
//			} else {
//				yqyqlx = sjsh * 20 / 100;
//			}
//		    } else if (yqstate == 2) {
//			yqyqlx = sjsh * 30 / 100;
//		   } else if (yqstate == 3) {
//			yqyqlx = sjsh * 14 / 100;
//		   } else if (yqstate == 4) {
//			yqyqlx = sjsh * 28 / 100;
//		  }
			int yqtsadd = 0;
			// 规定时间内的正常还款
			if (millionSeconds2 >= millionSeconds1) {
				addorder.set("dqyqts", 0);
				long chazhi = millionSeconds3 - millionSeconds2;
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				// 有逾期天数和利息,算出原有的天数和利息
				int yylx = 0;
				int yyts = 0;
				if(ddday < 0){
					addorder.set("dqyqlx", yqlx);
					yylx = yqlx;
					yyts = yqts;
				}else{
					addorder.set("dqyqlx", yqlx - ddyqlx);
					yylx = yqlx - ddyqlx;
					yyts = yqts - ddday;
				}
				
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				if (hkm > yqyqlx) {
					if ((hkm - yqyqlx) >= yylx) {
						jkxm2.set("sjsh_money",df.format(sjsh - (hkm - yqyqlx - yylx)));
						jkxm2.set("yuq_yhlx", df.format(yylx+yqyhlx));
						jkxm2.set("yuq_lx", 0);
						//减掉用户还的逾期利息天数
						int yqjdts = 0;
						if(jkdate==3 || jkdate == 4){
							yqjdts = yylx / (sjsh * 4 / 100) ;
						}else{
							yqjdts = yylx / (sjsh * 4 / 100) ;
						}
						jkxm2.set("yuq_ts", yyts - yqjdts);
						yqtsadd = yqtsadd + yqjdts;
					} else {
						jkxm2.set("yuq_yhlx", df.format(hkm - yqyqlx +yqyhlx));
						jkxm2.set("yuq_lx", df.format(yylx - (hkm - yqyqlx)));
						//减掉用户还的逾期利息天数
						int yqjdts = 0 ;
						if(jkdate==3 || jkdate == 4){
							yqjdts = (hkm - yqyqlx) / (sjsh * 4 / 100) ;
						}else{
							yqjdts = (hkm - yqyqlx) / (sjsh * 4 / 100) ;
						}
						jkxm2.set("yuq_ts", yyts- yqjdts);
						yqtsadd = yqtsadd + yqjdts;
					}
				} else if (hkm < yqyqlx) {
					jkxm2.set("yuq_lx", df.format(yylx + yqyqlx - hkm));
					jkxm2.set("yuq_ts", yyts);
				} else if (hkm == yqyqlx) {
					jkxm2.set("yuq_lx", df.format(yylx));
					jkxm2.set("yuq_ts", yyts);
				}
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额从催收总额里面去掉
				if(ddday > 0){
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,time111);
					if (datacs111 == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -sum);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - sum);
						jbdcms3Service.updateCuiBG(row11);
					}
				}

				//linc 2019年7月31日   逾期再延期用户
                if(yqts >0) {
					int jk_yqts = yqts;
					int rm_dqyqts = 0;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							
							logger.info("------------------41-------");
							//a当前催收人
							DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
							if(datarodq!=null) {
								String fendan_id =datarodq.getString("id");
								int cuishoujine = datarodq.getInt("cuishou_jine") - sum;
								int rechargemoney = datarodq.getInt("recharge_money");
								//,d当前催人的入催金额 减少
								jbdcms3Service.updatefendaninfoid(0,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
								logger.info("  逾期再延期   当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" sum:"+sum);
							}
							
							//a实际催收人
							DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
							if(datarowsj!=null) {
								String fendan_id =datarowsj.getString("id");
								int cuishoujine = datarowsj.getInt("cuishou_jine");
								int rechargemoney = datarowsj.getInt("recharge_money");
								
								jbdcms3Service.updatefendaninfoid(0,rechargemoney, fendan_id,cuishouid);
								logger.info("逾期再延期     实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
							}
							
						}
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}
				}
				
			}
			// 在规定时间外还款（也就是逾期用户）
			else {
				logger.info("------------------4-------");
				// 有没有延期不影响最终的逾期状态
				long chazhi = millionSeconds3 - millionSeconds1;// 当前时间距离实际还款时间的差值
				int ddday = (int) (chazhi / (1000 * 60 * 60 * 24));// 产生的多的逾期天数
				int ddyqlx = 0 ;
				if(jkdate == 3 || jkdate == 4 ){
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}else{
					ddyqlx = (sjsh * 4 / 100) * ddday; // 产生的多的逾期利息
				}
				int yylx = yqlx - ddyqlx;
				int yyts = yqts - ddday;
				addorder.set("dqyqts", yyts);
				addorder.set("dqyqlx", yqlx - ddyqlx);
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				if (hkm > yqyqlx) {
					if ((hkm - yqyqlx) >= yylx) {
						jkxm2.set("sjsh_money",df.format(sjsh - (hkm - yqyqlx - yylx)));
						jkxm2.set("yuq_yhlx", df.format(yylx+yqyhlx));
						jkxm2.set("yuq_lx", 0);
						//减掉用户还的逾期利息天数
						int yqjdts = 0;
						if(jkdate ==3 || jkdate ==4){
							yqjdts = yylx / (sjsh * 4 / 100) ;
						}else{
							yqjdts = yylx / (sjsh * 4 / 100) ;
						}
						jkxm2.set("yuq_ts", yyts - yqjdts);
						yqtsadd = yqtsadd + yqjdts;
					} else {
						jkxm2.set("yuq_yhlx", df.format(hkm - yqyqlx+yqyhlx));
						jkxm2.set("yuq_lx", df.format(yylx - (hkm - yqyqlx)));
						//减掉用户还的逾期利息天数
						int yqjdts = 0;
						if(jkdate ==3 || jkdate ==4){
							yqjdts = (hkm - yqyqlx) / (sjsh * 4 / 100) ;
						}else{
							yqjdts = (hkm - yqyqlx) / (sjsh * 4 / 100) ;
						}
						jkxm2.set("yuq_ts", yyts - yqjdts);
						yqtsadd = yqtsadd + yqjdts;
					}
				} else if (hkm < yqyqlx) {
					jkxm2.set("yuq_lx", df.format(yylx + yqyqlx - hkm));
					jkxm2.set("yuq_ts", yyts);
				} else if (hkm == yqyqlx) {
					jkxm2.set("yuq_lx", df.format(yylx));
					jkxm2.set("yuq_ts", yyts);
				}
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				// 把金额从催收总额里面去掉
				if(yyts > 0){
					if(yyts < 16){
						cuishouid = cuishoum1 ;
					}else if((yqts - ddday) > 15 && (yqts - ddday) < 46){
						cuishouid = cuishoum2;
					}
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,time111);
					if (datacs111 == null) {
						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -sum);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - sum);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
				
				//linc 2019年7月31日   逾期再延期用户
                if(yqts >0) {
					int jk_yqts = yqts;
					int rm_dqyqts = yyts;
					int dqyuts_z =-1;
					int yuqts_z =-1;
					
					if(rm_dqyqts < jk_yqts){
						if(jk_yqts > 0 && jk_yqts <4) {
							yuqts_z = 4;	
						}else if(jk_yqts > 3 && jk_yqts <16) {
							yuqts_z = 1;
						}else if(jk_yqts >15 && jk_yqts <46) {
							yuqts_z = 2;
						}else if(jk_yqts >45) {
							yuqts_z = 3;
							if("0".equals(cuishoum3)) {
								yuqts_z = 2;
							}
						}else if(0 == jk_yqts) {
							yuqts_z = 0;				
						}
						
						if(rm_dqyqts > 0 && rm_dqyqts <4) {
							dqyuts_z = 4;
							cuishouid = cuishoum0;
						}else if(rm_dqyqts > 3 && rm_dqyqts <16) {
							dqyuts_z = 1;
							cuishouid = cuishoum1;
						}else if(rm_dqyqts >15 && rm_dqyqts <46) {
							dqyuts_z = 2;
							cuishouid = cuishoum2;
						}else if(rm_dqyqts >45) {
							dqyuts_z = 3;
							cuishouid = cuishoum3;
							if("0".equals(cuishoum3)) {
								dqyuts_z = 2;
								cuishouid = cuishoum2;
							}
						}else {
							dqyuts_z = 0;
							cuishouid = "0";	
						}
						
						if(dqyuts_z != yuqts_z) {
							//a实际催收人
							DataRow datarowsj = jbdcms3Service.getfendaninfo(userId,jkid,cuishouid);
							if(datarowsj!=null) {
								String fendan_id =datarowsj.getString("id");
								int cuishoujine = datarowsj.getInt("cuishou_jine");
								int rechargemoney = datarowsj.getInt("recharge_money");
								
								jbdcms3Service.updatefendaninfoid(0,rechargemoney, fendan_id,cuishouid);
								logger.info("逾期再延期     实际催收人:jkid: "+jkid+" cuishouid:"+cuishouid+ " hkm:"+hkm);
							}
							
						}
						
						addorder.set("cuishouid", cuishouid);   //a更新为对应崔收人
					}

					logger.info("------------------42-------");
					//a当前催收人
					DataRow datarodq = jbdcms3Service.getfendaninfo(userId,jkid,dataRowjk.getString("cuishou_id"));
					if(datarodq!=null) {
						String fendan_id =datarodq.getString("id");
						int cuishoujine = datarodq.getInt("cuishou_jine") - sum;
						int rechargemoney = datarodq.getInt("recharge_money");
						//,d当前催人的入催金额 减少
						jbdcms3Service.updatefendaninfoid(0,rechargemoney, fendan_id,dataRowjk.getString("cuishou_id"));
						logger.info("  逾期再延期   当前催收人催收人:jkid: "+jkid+" cuishouid:"+dataRowjk.getString("cuishou_id")+" hkm:"+hkm);
						
					}
				}
				
			}
			DataRow jkxm2 = new DataRow();
			int tianshu = 0;
			String hkyqtime = dataRowjk.getString("hkyq_time");
			String hkfqtime = dataRowjk.getString("hkfq_time");
			String hkfqcishu = dataRowjk.getString("hkfq_cishu");
			String hkfqlx = dataRowjk.getString("hkfq_lx");
			String hkfqzlx = dataRowjk.getString("hkfqzlx");
			int fqlx = 0;
			int fqzlx = 0;
			if (!"".equals(hkfqlx)) {
				String aa = hkfqlx.replace(",", "");
				String bb = hkfqzlx.replace(",", "");
				fqlx = Integer.parseInt(aa);
				fqzlx = Integer.parseInt(bb);
			}
			if (yqstate == 1) {
				tianshu = 15 + yqtsadd;
			} else if (yqstate == 2) {
				tianshu = 30 + yqtsadd;
			} else if (yqstate == 3) {
				tianshu = 7  + yqtsadd;
			} else if (yqstate == 4) {
				tianshu = 14 + yqtsadd;
			}
			Date time1 = new Date();
			if (!"".equals(hkfqtime)) {
				time1 = fmtrq.parse(hkfqtime);
			} else {
				time1 = fmtrq.parse(hkyqtime);
			}
			Calendar cl = Calendar.getInstance();
			cl.setTime(time1);
			cl.add(Calendar.DATE, tianshu);
			String fenqitime = "";
			fenqitime = fmtrq.format(cl.getTime());
			jkxm2.set("id", jkid);
			int yyyqts = jbdcms3Service.getyqts(jkid);
			if (yyyqts <= 15) {
				jkxm2.set("cuishou_id", 0);
			}
			jkxm2.set("hkfq_code", 1);
			jkxm2.set("hkfq_time", fenqitime);
			jkxm2.set("hkfq_day", tianshu);
			if (yqyqlx >= hkm) {
				jkxm2.set("hkfqzlx", df.format(fqzlx + hkm));
			} else {
				jkxm2.set("hkfqzlx", df.format(fqzlx + yqyqlx));
			}
			jkxm2.set("hkfq_lx", df.format(fqlx + hkm));
			jkxm2.set("hkfq_cishu", hkfqcishu + yqstate);
			jkxm2.set("hk_time", date2);
			jkxm2.set("hkqd", 0);
			jkxm2.set("tzjx_ts", 0);
			jkxm2.set("tzjx_lx", 0);
			jkxm2.set("tzjx", 0);
			jbdcms3Service.updateJKHK(jkxm2);
			row6.set("neirong","Đề xuất gia hạn đã được duyệt, chúng tôi đã nhận được phí gia hạn "+ df.format(hkm) + ", thời hạn trả vay đổi thành "+ fenqitime);
		}
		jbdcms3Service.addOrder(addorder);
		String money = "0";
		row6.set("userid", userId);
		row6.set("title", "Thông báo về việc trả vay"); // 还款通知
		if (state == 1) {
			row6.set("neirong", "Trả vay thành công " + df.format(sum));
			money = df.format(sum);
		} else if (state == 2 || state == 3) {
			row6.set("neirong", "Trả vay thành công " + df.format(hkm));
			money = df.format(hkm);
		}// 成功还款多少钱！
		row6.set("fb_time", date2);
		jbdcms3Service.insertUserMsg(row6);
		// 给还款人短信
		String userName = dataRow2.getString("username").substring(0, 7);
		String appName = "OCEAN";
		if (userName.equals("OCEAN")) {
			appName = "OCEAN";
		}
		/*
		 * String content =
		 * "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+
		 * appName+" xin thong bao: Ban da thanh toan so tien "
		 * +money+". Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.\",\"SmsGuid\":\""
		 * +dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]"; String con =
		 * URLEncoder.encode(content, "utf-8"); SendMsg sendMsg = new SendMsg();
		 */
		//String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
		String realname = jbdcms3Service.getRealname(userId);

		String msg_url=" Hotline: 09230087819.";
		String SEND_INFO_TYPE="HK_success";
		DataRow row_hk = jbdcms3Service.getSendinfoSET(SEND_INFO_TYPE,"");
		if(row_hk!=null){
			int hk_state = row_hk.getInt("fun_status");
			if(1==hk_state) {
				if(!"".contains(row_hk.getString("content"))) {
					msg_url=row_hk.getString("content");
				}
//				String content   =  appName+" chao "+realname+",  cam on ban da thanh toan so tien "+money+". Moi thac mac lien he"+msg_url;
//				SendFTP sms = new SendFTP();
//				String  response = sms.sendMessageFTP(content,dataRow2.getString("mobilephone"));
			}
		}
		
		
		DataRow user = jbdLLpayService.getUserRecThreeInfoYN(userId);
		/*String registration_id = user.getString("registration_id");
		String altertz = "OLAVA xin thông báo: Lần trả vay này đã hoàn tất. Mọi thắc mắc vui lòng inbox ox http://bit.ly/2zPb4v1, hot hotline: 02873008816.";*/
		String name = user.getString("username").substring(5, 8);
		int code = 0;
		if (name.equals("AND")) {
			code = 1;
		} else {
			code = 2;
		}
		/*jbdcmsaction.testSendPushOLVA(registration_id, "Trả vay hoàn tất",
				altertz, code);*/

		jsonObject.put("error", 1);
		jsonObject.put("msg", "Thành công");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	/**
	 * 获取私钥
	 * 
	 * @return 当前的私钥对象
	 */
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * 获取公钥
	 * 
	 * @return 当前的公钥对象
	 */
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public void loadPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	public void loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			this.privateKey = (RSAPrivateKey) keyFactory
					.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 加密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public String encrypt(RSAPublicKey publicKey, String plainText)
			throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] bytes = plainText.getBytes();
			ByteArrayInputStream read = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream write = new ByteArrayOutputStream();
			byte[] buf = new byte[117];
			int len = 0;
			while ((len = read.read(buf)) != -1) {
				byte[] buf1 = null;
				if (buf.length == len) {
					buf1 = buf;
				} else {
					buf1 = new byte[len];
					for (int i = 0; i < len; i++) {
						buf1[i] = buf[i];
					}
				}
				byte[] bytes1 = cipher.doFinal(buf1);
				write.write(bytes1);
			}
			return Base64.encode(write.toByteArray());
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 解密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public String decrypt(RSAPrivateKey privateKey, String miwen)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] bytes = Base64.decode(miwen);
			ByteArrayInputStream read = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream write = new ByteArrayOutputStream();
			byte[] buf = new byte[128];
			int len = 0;
			while ((len = read.read(buf)) != -1) {
				byte[] buf1 = null;
				if (buf.length == len) {
					buf1 = buf;
				} else {
					buf1 = new byte[len];
					for (int i = 0; i < len; i++) {
						buf1[i] = buf[i];
					}
				}
				byte[] bytes1 = cipher.doFinal(buf1);
				write.write(bytes1);
			}
			return new String(write.toByteArray());

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/**
	 * 实现线下支付
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doOfflinePaymentl() throws Exception {
		logger.info("进入线下支付还款结果返回");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		
		int operatorId = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (operatorId == 0) {
			jsonObject.put("error", -1);
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + operatorId);
		String jkid = getStrParameter("rec_id"); // 接收到的还款id
		logger.info(jkid);
		String hkmoney = getStrParameter("hkmoney").replace(",", ""); // 还款金额
		logger.info(hkmoney);
		boolean result = hkmoney.matches("[0-9]+");
		if (result == false) {
			jsonObject.put("error", -2);
			jsonObject.put("msg", "请输入正确的还款金额");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String sjsh_money = jbdcms3Service.getsjsh(jkid);
		int hkhongbaoid = jbdcms3Service.getHKhongbaoid(jkid);
		int hongbaoid = jbdcms3Service.gethongbaoid(jkid);
		int hkhongbaojine = 0;
		if (hkhongbaoid != 0) {
			hkhongbaojine = 5;
		}
		// = jbdcms3Service.getHKhongbaojine(hkhongbaoid);
		String hkqd = jbdcms3Service.gethkqd(jkid);
		int yqts = jbdcms3Service.getyqts(jkid);
		String yuq_lx = getStrParameter("yuqlx").replace(",", ""); // 逾期利息
		String yuq_yhlx = jbdcms3Service.getYQYH(jkid); // 逾期利息
		String sjds = jbdcms3Service.getSJDS(jkid); // 实际到手
		String lx = jbdcms3Service.getLX(jkid); // 利息
		String newhkmoney = hkmoney.replace(",", ""); // 逾期利息
		String newsjsh_money = sjsh_money.replace(",", ""); // 逾期利息
		String newsjds_money = sjds.replace(",", ""); // 逾期利息
		String newlx_money = lx.replace(",", ""); // 逾期利息
		String newyuq_lx = yuq_lx.replace(",", ""); // 逾期利息
		String newyuq_yhlx = yuq_yhlx.replace(",", ""); // 逾期利息
		int sjsh = Integer.parseInt(newsjsh_money);
		int zjsjsh = Integer.parseInt(newsjds_money)
				+ Integer.parseInt(newlx_money); // 200万基数
		int hkm = Integer.parseInt(hkmoney);
		int yqlx = Integer.parseInt(newyuq_lx);
		int yqyhlx = Integer.parseInt(newyuq_yhlx);
		int sum = sjsh + yqlx;
		int sumhongbao = sjsh + yqlx - hkhongbaojine * 10000;
		DecimalFormat df = new DecimalFormat("###,###");
		String sum1 = df.format(sumhongbao);
		String userId = getStrParameter("user_id"); // 接收到的用户id
		int state = getIntParameter("state"); // 接收到还款选择
		int yqstate = getIntParameter("yqstate"); // 接收到延期期限选择
		int jsyqts = getIntParameter("jsyqts"); // 接收到延期期限选择
		String HKtime = getStrParameter("hksj").replace("&nbsp;", " ");// 接收还款时间
		Date dt = new Date();
		Long time = dt.getTime();// 这就是距离1970年1月1日0点0分0秒的毫秒数
		String payOrder = "mova" + time;
		logger.info("还款订单：" + payOrder);
		logger.info(userId);
		logger.info(HKtime);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证 日期，比如2007/02/29会被接受，
			// 并转换成2007/03/01
			fmtrq.setLenient(false);
			Date date = fmtrq.parse(HKtime);

		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			jsonObject.put("error", -3);
			jsonObject.put("msg", "请输入正确的时间格式");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String date2 = HKtime;
		DataRow dataRow2 = jbdcmsService.getUserInfo(Integer.parseInt(userId));
		String username = dataRow2.getString("username");
		DataRow dataRowjk = jbdcms3Service.getUserJKInfo(jkid);
		DataRow row6 = new DataRow();
		// 支付成功（表示还款成功）
		DataRow addorder = new DataRow();
		int orderpaytype = 3; // 线下还款为3
		String remark = "";
		if (state == 1) {
			remark = "全额还款";
		} else if (state == 2) {
			if (yqlx >= hkm) {
				remark = "逾期利息还款";
			} else {
				remark = "逾期利息还完，部分本金还款";
			}
		} else if (state == 3) {
			remark = "完成还款，部分逾期利息还款";
		} else if (state == 4) {
			remark = "延期还款";
			addorder.set("yqdate", yqstate);
		}

		String payNumber = payOrder;
		// 生成订单
		addorder.set("userid", userId);
		addorder.set("rechargetime", fmtrq.format(new Date()));
		addorder.set("rechargetype", 33);
		addorder.set("jsyqts", jsyqts);
		addorder.set("result", 1);
		addorder.set("operatorId", operatorId);
		addorder.set("rzcode", "0000");
		addorder.set("paynumber", payNumber);
		addorder.set("rechargenumber", jkid);
		addorder.set("remark", remark);
		addorder.set("hkhongbaoid", hkhongbaoid);
		addorder.set("hongbaoid", hongbaoid);
		addorder.set("ordertype", 1);
		addorder.set("resultInfo", hkqd);
		addorder.set("orderpaytype", orderpaytype);
		addorder.set("cuishouid", dataRowjk.getString("cuishou_id"));

		SimpleDateFormat fmtrq111 = new SimpleDateFormat("yyyy-MM-dd");
		String time111 = fmtrq111.format(new Date());
		String cuishouid = dataRowjk.getString("cuishou_id");
		String cuishoum1 = dataRowjk.getString("cuishou_m1");
		String cuishoum2 = dataRowjk.getString("cuishou_m2");

		if (state == 3) {
			if (yqts - jsyqts <= 15) {
				cuishouid = cuishoum1;
			} else {
				cuishouid = cuishoum2;
			}
		} else if (state == 2) {
			if (yqts - jsyqts <= 15) {
				cuishouid = cuishoum1;
			} else {
				if (!"0".equals(cuishoum2)) {
					cuishouid = cuishoum2;
				} else {
					cuishouid = cuishoum1;
					jsyqts = 1;
				}
			}
		}

		DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid, time111);
		if (state == 1) {
			if (yqts > 0) {
				if (datacs == null) {

					DataRow row11 = new DataRow();
					row11.set("csid", cuishouid);
					row11.set("ycsjine", sum);
					row11.set("time", time111);
					jbdcms3Service.insertCuiBG(row11);
				} else {
					double ysje = datacs.getDouble("ycsjine");
					int cuiid = datacs.getInt("id");
					DataRow row11 = new DataRow();
					row11.set("id", cuiid);
					row11.set("ycsjine", ysje + sum);
					jbdcms3Service.updateCuiBG(row11);
				}
			}

		} else if (state == 2) {
			if ((yqts - jsyqts) > 0) {
				if (datacs == null) {

					DataRow row11 = new DataRow();
					row11.set("csid", cuishouid);
					row11.set("ycsjine", hkm);
					row11.set("time", time111);
					jbdcms3Service.insertCuiBG(row11);
				} else {
					double ysje = datacs.getDouble("ycsjine");
					int cuiid = datacs.getInt("id");
					DataRow row11 = new DataRow();
					row11.set("id", cuiid);
					row11.set("ycsjine", ysje + hkm);
					jbdcms3Service.updateCuiBG(row11);
				}
			} else {
				if (yqts > 0) {
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,
							time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
			}

			if ((yqts - jsyqts) == 15 || (yqts - jsyqts) == 14) {
				cuishouid = cuishoum2;
				if (!"0".equals(cuishouid)) {
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,
							time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}

			}

		} else if (state == 4) {
			if (yqts > 0) {
				if (datacs == null) {

					DataRow row11 = new DataRow();
					row11.set("csid", cuishouid);
					row11.set("totaljine", -sum);
					row11.set("time", time111);
					jbdcms3Service.insertCuiBG(row11);

				} else {
					double ysje = datacs.getDouble("totaljine");
					int cuiid = datacs.getInt("id");
					DataRow row11 = new DataRow();
					row11.set("id", cuiid);
					row11.set("totaljine", ysje - sum);
					jbdcms3Service.updateCuiBG(row11);
				}
			}

		} else if (state == 3) {
			if (yqts - jsyqts > 0) {
				if (datacs == null) {

					DataRow row11 = new DataRow();
					row11.set("csid", cuishouid);
					row11.set("ycsjine", hkm);
					row11.set("time", time111);
					jbdcms3Service.insertCuiBG(row11);
				} else {
					double ysje = datacs.getDouble("ycsjine");
					int cuiid = datacs.getInt("id");
					DataRow row11 = new DataRow();
					row11.set("id", cuiid);
					row11.set("ycsjine", ysje + hkm);
					jbdcms3Service.updateCuiBG(row11);
				}
			} else {
				if (!"0".equals(cuishouid)) {
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,
							time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}
			}
			if ((yqts - jsyqts) == 15 || (yqts - jsyqts) == 14) {
				cuishouid = cuishoum2;
				if (!"0".equals(cuishouid)) {
					DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishouid,
							time111);
					if (datacs111 == null) {

						DataRow row11 = new DataRow();
						row11.set("csid", cuishouid);
						row11.set("totaljine", -hkm);
						row11.set("time", time111);
						jbdcms3Service.insertCuiBG(row11);
					} else {
						double ysje = datacs111.getDouble("totaljine");
						int cuiid = datacs111.getInt("id");
						DataRow row11 = new DataRow();
						row11.set("id", cuiid);
						row11.set("totaljine", ysje - hkm);
						jbdcms3Service.updateCuiBG(row11);
					}
				}

			}
		}

		if (state == 1) {
			addorder.set("rechargemoney", sum1);
			addorder.set("dqyqts", yqts);

			DataRow jkxm2 = new DataRow();
			jkxm2.set("id", jkid);
			jkxm2.set("jksfwc", 1);
			jkxm2.set("sfyhw", 1);
			jkxm2.set("hk_time", date2);
			jbdcms3Service.updateJKHK(jkxm2);
			if (hkhongbaoid != 0) {
				DataRow hkrow = new DataRow();
				hkrow.set("id", userId);
				hkrow.set("hongbao", 1);
				jbdcms3Service.updateHKhongbaoid(hkrow);
			}
			/*
			 * if(hkhongbaoid != 0){ DataRow hkrow = new DataRow();
			 * hkrow.set("hongbaoid",hkhongbaoid); hkrow.set("sfsy",1);
			 * hkrow.set("usertime",date2);
			 * jbdcms3Service.updateHKhongbaoid(hkrow); }
			 */
		} else if (state == 2) {
			addorder.set("rechargemoney", df.format(hkm));
			addorder.set("dqyqts", yqts - jsyqts);

			if (yqlx >= hkm) {
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("yuq_yhlx", df.format(yqyhlx + hkm));
				jkxm2.set("yuq_lx", df.format(yqlx - hkm));
				jkxm2.set("hk_time", date2);
				jkxm2.set("hkqd", 0);
				jbdcms3Service.updateJKHK(jkxm2);
			} else {
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("yuq_yhlx", df.format(yqyhlx + yqlx));
				jkxm2.set("yuq_lx", "0");
				jkxm2.set("sjsh_money", df.format(sjsh - (hkm - yqlx)));
				jkxm2.set("hk_time", date2);
				jkxm2.set("hkqd", 0);
				jbdcms3Service.updateJKHK(jkxm2);
			}
		} else if (state == 3) {
			addorder.set("rechargemoney", df.format(hkm));

			DataRow jkxm2 = new DataRow();
			jkxm2.set("id", jkid);

			if (jsyqts == 1) {
				addorder.set("dqyqts", yqts - 1);

				jkxm2.set("yuq_ts", yqts - 1);
				jkxm2.set("yuq_whlx", df.format(sum - (sjsh * 2 / 100) - hkm));
				if (hkm >= sjsh) {
					jkxm2.set("yuq_lx", df.format(hkm - sjsh));
				} else {
					jkxm2.set("yuq_lx", "0");
				}
			} else if (jsyqts == 2) {
				addorder.set("dqyqts", yqts - 2);

				jkxm2.set("yuq_ts", yqts - 2);
				jkxm2.set("yuq_whlx", df.format(sum - (sjsh * 4 / 100) - hkm));
				if (hkm >= sjsh) {
					jkxm2.set("yuq_lx", df.format(hkm - sjsh));
				} else {
					jkxm2.set("yuq_lx", "0");
				}
			} else {
				addorder.set("dqyqts", yqts);

				jkxm2.set("yuq_whlx", df.format(sum - hkm));
				if (hkm >= sjsh) {
					jkxm2.set("yuq_lx", df.format(hkm - sjsh));
				} else {
					jkxm2.set("yuq_lx", "0");
				}
			}
			jkxm2.set("jksfwc", 1);
			jkxm2.set("sfyhw", 1);
			jkxm2.set("hk_time", date2);
			jbdcms3Service.updateJKHK(jkxm2);
		} else if (state == 4) {
			addorder.set("dqyqts", yqts);
			int yqyqlx = 0;
			if (yqstate == 0) {
				jsonObject.put("error", 0);
				jsonObject.put("msg", "请选择延期期限！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}else {
				int yqjklx = userMoneyBase.getUMBaseCalculateProductInterest(zjsjsh, yqstate, Integer.parseInt(userId), username);
				yqyqlx = zjsjsh * yqjklx / 100;
			}
//			} else if (yqstate == 1) {
//				if (zjsjsh <= 1000000) {
//					yqyqlx = zjsjsh * 30 / 100;
//				} else {
//					yqyqlx = zjsjsh * 20 / 100;
//				}
//			} else if (yqstate == 2) {
//				yqyqlx = zjsjsh * 30 / 100;
//			}
			addorder.set("rechargemoney", df.format(hkm));
			DataRow jkxm2 = new DataRow();
			if (hkm >= yqyqlx) {
				if (yqlx != 0 && (hkm - yqyqlx) <= yqlx) {
					jkxm2.set("yuq_yhlx", df.format(hkm - yqyqlx));
					jkxm2.set("yuq_lx", df.format(yqlx - (hkm - yqyqlx)));
				} else if ((hkm - yqyqlx) > yqlx) {
					jkxm2.set("yuq_yhlx", yqlx);
					jkxm2.set("yuq_lx", 0);
					jkxm2.set("sjsh_money",
							df.format(sjsh - (hkm - yqyqlx - yqlx)));
				}

			} else {
				jkxm2.set("yuq_lx", df.format(yqlx + (yqyqlx - hkm)));
			}
			int tianshu = 0;
			String hkyqtime = dataRowjk.getString("hkyq_time");
			String hkfqtime = dataRowjk.getString("hkfq_time");
			String hkfqcishu = dataRowjk.getString("hkfq_cishu");
			String hkfqlx = dataRowjk.getString("hkfq_lx");
			String hkfqzlx = dataRowjk.getString("hkfqzlx");
			int fqlx = 0;
			int fqzlx = 0;
			if (!"".equals(hkfqlx)) {
				String aa = hkfqlx.replace(",", "");
				String bb = hkfqzlx.replace(",", "");
				fqlx = Integer.parseInt(aa);
				fqzlx = Integer.parseInt(bb);
			}
			if (yqstate == 1) {
				tianshu = 15;
			} else if (yqstate == 2) {
				tianshu = 30;
			} else if (yqstate ==3) {
				tianshu = 7;
			} else if (yqstate == 4) {
				tianshu = 14;
			}
			Date time1 = new Date();
			if (!"".equals(hkfqtime)) {
				time1 = fmtrq.parse(hkfqtime);
			} else {
				time1 = fmtrq.parse(hkyqtime);
			}
			Calendar cl = Calendar.getInstance();
			cl.setTime(time1);
			cl.add(Calendar.DATE, tianshu);
			String fenqitime = "";
			fenqitime = fmtrq.format(cl.getTime());
			jkxm2.set("id", jkid);
			if (yqts <= 15) {
				jkxm2.set("cuishou_id", 0);
			}
			jkxm2.set("hkfq_code", 1);
			jkxm2.set("hkfq_time", fenqitime);
			jkxm2.set("hkfq_day", tianshu);
			if (yqyqlx >= hkm) {
				jkxm2.set("hkfqzlx", df.format(fqzlx + hkm));
			} else {
				jkxm2.set("hkfqzlx", df.format(fqzlx + yqyqlx));
			}
			jkxm2.set("hkfq_lx", df.format(fqlx + hkm));
			jkxm2.set("hkfq_cishu", hkfqcishu + yqstate);
			jkxm2.set("hk_time", date2);
			jkxm2.set("hkqd", 0);
			jkxm2.set("tzjx_ts", 0);
			jkxm2.set("tzjx_lx", 0);
			jkxm2.set("tzjx", 0);
			jbdcms3Service.updateJKHK(jkxm2);
			row6.set("neirong",
					"Đề xuất gia hạn đã được duyệt, chúng tôi đã nhận được phí gia hạn "
							+ df.format(hkm) + ", thời hạn trả vay đổi thành "
							+ fenqitime);
		}
		jbdcms3Service.addOrder(addorder);
		String money = "0";
		row6.set("userid", userId);

		row6.set("title", "Thông báo về việc trả vay"); // 还款通知
		if (state == 1) {
			row6.set("neirong", "Trả vay thành công " + df.format(sum));
			money = df.format(sum);
		} else if (state == 2 || state == 3) {
			row6.set("neirong", "Trả vay thành công " + df.format(hkm));
			money = df.format(hkm);
		}// 成功还款多少钱！
		row6.set("fb_time", date2);
		jbdcms3Service.insertUserMsg(row6);
		// 给还款人短信
		String userName = dataRow2.getString("username").substring(0, 4);
		String appName = "OCEAN";
		if (userName.equals("OCEAN")) {
			appName = "OCEAN";
		} 
		
		String SEND_INFO_TYPE="HK_success";
		DataRow row_hk = jbdcms3Service.getSendinfoSET(SEND_INFO_TYPE,"");
		if(row_hk!=null){
			int hk_state = row_hk.getInt("fun_status");
			String send_url=  "hotline: 1900234558.";
			if(1==hk_state) {
				if(!"".contains(row_hk.getString("content"))) {
					send_url=row_hk.getString("content");
				}
				String content = "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+appName+" xin thong bao: Ban da thanh toan so tien "+money+". Moi thac mac vui long inbox "+send_url +"\",\"SmsGuid\":\""+dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]";
				String con = URLEncoder.encode(content, "utf-8");
				SendMsg sendMsg = new SendMsg();
				//String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
			}
			
		}
		

		DataRow user = jbdLLpayService.getUserRecThreeInfoYN(userId);

		//用户还款后，提示用户已成为VIP用户
		if(state == 1 ||state == 3) {
			String SEND_INFO_TYPE_2="HK_success_VIP";
			DataRow row_hkvip = jbdcms3Service.getSendinfoSET(SEND_INFO_TYPE_2,"");
			if(null!=row_hkvip) {
				int zcjk_state= row_hkvip.getInt("fun_status");
				String send_url2=  row_hkvip.getString("content");
				if(1==zcjk_state) {
					String mobilePhone = dataRow2.getString("mobilephone");
					String returnString = "";
					DataRow rowfe= jbdcms3Service.getuserfinance(userId+"");
					String user_realname= rowfe.getString("realname");
					int last_num =user_realname.lastIndexOf(" ");
					String user_namelast=user_realname.substring(last_num);
//					//oneSMS
					String MSG_COM ="onesms"; //d短信公司
					int MSG_TYPE=2; //a短信类型 :未再次提交
					SendFTP sendFTP = new SendFTP();
					  try {
						  returnString = sendFTP.sendMessageFTP(""+appName+" chao! "+user_namelast+" "+send_url2,mobilePhone);
						  if(returnString.length()>70) {
							  returnString = returnString.substring(70,returnString.lastIndexOf("</"));
						  }  
					} catch (IOException e) {
						// TODO: handle exception
					}
					
					DataRow inrow =new DataRow();
					inrow.set("userid", userId);
					inrow.set("operateid", operatorId);
					inrow.set("msgtype", MSG_TYPE);
					inrow.set("msg_com", MSG_COM);
					inrow.set("msg_return", returnString);
					inrow.set("create_time", fmtrq.format(new Date()));
					jbdcms3Service.intsertUserMsg(inrow);
				}
				
			}
		}

		jsonObject.put("error", 1);
		jsonObject.put("msg", "Thành công");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	/**
	 * 实现线上支付
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doOnlinePayOlavamentl() throws Exception {
		logger.info("进入线上支付还款结果返回");

		HttpServletRequest request = getRequest();
		JSONObject jsonObject = new JSONObject();
		com.alibaba.fastjson.JSONObject jsonObj = getRequestJson(request);
		String token = jsonObj.getString("token");
		JBDcms3Action olavapay = new JBDcms3Action();
		olavapay.loadPrivateKey(olavapay.DEFAULT_PRIVATE_KEY);
		String res = olavapay.decrypt(olavapay.privateKey, token);

		com.alibaba.fastjson.JSONObject resjsonObj = com.alibaba.fastjson.JSONObject
				.parseObject(res);
		int userid = 0;
		int hkmoney = 0;
		int state = 0;
		int yqstate = 0;
		// int jsyqts = getIntParameter("jsyqts"); //接收到延期期限选择
		String miwen = "";
		String jiamiwen = "";
		try {
			userid = resjsonObj.getInteger("userid");

			hkmoney = resjsonObj.getInteger("hkmoney");
			logger.info("用户：" + userid + "还款金额：" + hkmoney);

			state = resjsonObj.getInteger("state");

			yqstate = resjsonObj.getInteger("yqstate");

			miwen = resjsonObj.getString("edg");
			jiamiwen = Encrypt.MD5(userid + "we" + jiami);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObject.put("error", -1);
			jsonObject.put("msg",
					"Lỗi hệ thống, vui lòng liên hệ CSKH: 1900234558");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (jiamiwen.equals(miwen)) {
			String jkid = jbdcms3Service.getJKID(userid);
			String sjsh_money = jbdcms3Service.getsjsh(jkid);
			int hkhongbaoid = jbdcms3Service.getHKhongbaoid(jkid);
			int hongbaoid = jbdcms3Service.gethongbaoid(jkid);
			int hkhongbaojine = 0;
			if (hkhongbaoid != 0) {
				hkhongbaojine = 5;
			}
			// = jbdcms3Service.getHKhongbaojine(hkhongbaoid);
			String hkqd = jbdcms3Service.gethkqd(jkid);
			int yqts = jbdcms3Service.getyqts(jkid);
			String yuq_lx = jbdcms3Service.getYQLX(jkid); // 逾期利息
			String yuq_yhlx = jbdcms3Service.getYQYH(jkid); // 逾期利息
			String sjds = jbdcms3Service.getSJDS(jkid); // 实际到手
			String lx = jbdcms3Service.getLX(jkid); // 利息

			String newsjsh_money = sjsh_money.replace(",", ""); // 逾期利息
			String newsjds_money = sjds.replace(",", ""); // 逾期利息
			String newlx_money = lx.replace(",", ""); // 逾期利息
			String newyuq_lx = yuq_lx.replace(",", ""); // 逾期利息
			String newyuq_yhlx = yuq_yhlx.replace(",", ""); // 逾期利息
			int sjsh = Integer.parseInt(newsjsh_money);
			// int zjsjsh = Integer.parseInt(newsjds_money) +
			// Integer.parseInt(newlx_money); //200万基数
			int hkm = hkmoney;
			int yqlx = Integer.parseInt(newyuq_lx);
			int yqyhlx = Integer.parseInt(newyuq_yhlx);

			int sumhongbao = sjsh + yqlx - hkhongbaojine * 10000;
			DecimalFormat df = new DecimalFormat("###,###");
			String sum1 = df.format(sumhongbao);

			Date dt = new Date();
			// Long time= dt.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数
			String payOrder = "olava" + resjsonObj.getString("parorder");
			logger.info("还款订单：" + payOrder);

			SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			String date2 = fmtrq.format(dt);
			DataRow dataRow2 = jbdcms3Service.getUserInfo(userid);
			String username = dataRow2.getString("username");
			DataRow dataRowjk = jbdcms3Service.getUserJKInfo(jkid);

			DataRow row6 = new DataRow();
			// 支付成功（表示还款成功）
			int orderpaytype = 3; // 线下还款为3
			String remark = "";
			if (state == 0) {
				remark = "线上全额还款";
			} else if (state == 1) {

				remark = "线上部分还款";

			} else if (state == 2) {
				remark = "线上延期还款";
			}
			int lsorder = jbdcms3Service.getPayOrder(payOrder);
			if (lsorder > 0) {
				DataRow addorder = new DataRow();
				addorder.set("userid", userid);
				addorder.set("rechargetime", date2);
				addorder.set("rechargetype", 33);
				addorder.set("result", 1);
				addorder.set("operatorId", "888");
				addorder.set("rechargemoney", df.format(hkm));
				addorder.set("rzcode", "0000");
				addorder.set("paynumber", payOrder);
				addorder.set("rechargenumber", jkid);
				addorder.set("remark", remark);
				addorder.set("hkhongbaoid", hkhongbaoid);
				addorder.set("hongbaoid", hongbaoid);
				addorder.set("ordertype", 1);
				addorder.set("resultInfo", hkqd);
				addorder.set("orderpaytype", orderpaytype);
				addorder.set("dqyqts", yqts);
				jbdcms3Service.addOrderCF(addorder);
				logger.info("错误的还款订单号：" + payOrder);
				jsonObject.put("error", -1);
				jsonObject.put("msg",
						"System error!vui lòng liên hệ CSKH: 1900234558");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			// 生成订单
			DataRow addorder = new DataRow();
			addorder.set("userid", userid);
			addorder.set("rechargetime", date2);
			addorder.set("rechargetype", 33);
			addorder.set("result", 1);
			addorder.set("operatorId", "888");
			addorder.set("rzcode", "0000");
			addorder.set("paynumber", payOrder);
			addorder.set("rechargenumber", jkid);
			addorder.set("remark", remark);
			addorder.set("hkhongbaoid", hkhongbaoid);
			addorder.set("hongbaoid", hongbaoid);
			addorder.set("ordertype", 1);
			addorder.set("resultInfo", hkqd);
			addorder.set("orderpaytype", orderpaytype);
			addorder.set("dqyqts", yqts);
			addorder.set("rechargemoney", df.format(hkm));

			if (state == 0) {
				addorder.set("rechargemoney", sum1);
				DataRow jkxm2 = new DataRow();
				jkxm2.set("id", jkid);
				jkxm2.set("jksfwc", 1);
				jkxm2.set("sfyhw", 1);
				jkxm2.set("hk_time", date2);
				jbdcms3Service.updateJKHK(jkxm2);
				if (hkhongbaoid != 0) {
					DataRow hkrow = new DataRow();
					hkrow.set("id", userid);
					hkrow.set("hongbao", 1);
					jbdcms3Service.updateHKhongbaoid(hkrow);
				}

			} else if (state == 1) {
				addorder.set("rechargemoney", df.format(hkm));
				if (yqlx >= hkm) {
					DataRow jkxm2 = new DataRow();
					jkxm2.set("id", jkid);
					jkxm2.set("yuq_yhlx", df.format(yqyhlx + hkm));
					jkxm2.set("yuq_lx", df.format(yqlx - hkm));
					jkxm2.set("hk_time", date2);
					jkxm2.set("hkqd", 0);
					jkxm2.set("hkstate", 0);
					jbdcms3Service.updateJKHK(jkxm2);
				} else {
					DataRow jkxm2 = new DataRow();
					jkxm2.set("id", jkid);
					jkxm2.set("yuq_yhlx", df.format(yqyhlx + yqlx));
					jkxm2.set("yuq_lx", "0");
					jkxm2.set("sjsh_money", df.format(sjsh - (hkm - yqlx)));
					jkxm2.set("hk_time", date2);
					jkxm2.set("hkqd", 0);
					jkxm2.set("hkstate", 0);
					jbdcms3Service.updateJKHK(jkxm2);
				}
			} else if (state == 2) {
				int yqyqlx = 0;
				int yqjklx = userMoneyBase.getUMBaseCalculateProductInterest(sjsh, yqstate,userid, username);
				yqyqlx = sjsh * yqjklx / 100;
//				if (yqstate == 1) {
//					yqyqlx = sjsh * 20 / 100;
//				} else if (yqstate == 2) {
//					yqyqlx = sjsh * 30 / 100;
//				}
				addorder.set("rechargemoney", df.format(hkm));
				DataRow jkxm2 = new DataRow();

				jkxm2.set("yuq_yhlx", df.format(yqyhlx + yqlx));
				jkxm2.set("yuq_lx", 0);

				int tianshu = 0;
				String hkyqtime = dataRowjk.getString("hkyq_time");
				String hkfqtime = dataRowjk.getString("hkfq_time");
				String hkfqcishu = dataRowjk.getString("hkfq_cishu");
				String hkfqlx = dataRowjk.getString("hkfq_lx");
				String hkfqzlx = dataRowjk.getString("hkfqzlx");
				int fqlx = 0;
				int fqzlx = 0;
				if (!"".equals(hkfqlx)) {
					String aa = hkfqlx.replace(",", "");
					String bb = hkfqzlx.replace(",", "");
					fqlx = Integer.parseInt(aa);
					fqzlx = Integer.parseInt(bb);
				}
				if (yqstate == 1) {
					tianshu = 15;
				} else if (yqstate == 2) {
					tianshu = 30;
				} else if (yqstate ==3) {
					tianshu = 7;
				} else if (yqstate == 4) {
					tianshu = 14;
				}
				Date time1 = new Date();
				if (!"".equals(hkfqtime)) {
					time1 = fmtrq.parse(hkfqtime);
				} else {
					time1 = fmtrq.parse(hkyqtime);
				}
				Calendar cl = Calendar.getInstance();
				cl.setTime(time1);
				cl.add(Calendar.DATE, tianshu);
				String fenqitime = "";
				fenqitime = fmtrq.format(cl.getTime());
				jkxm2.set("id", jkid);
				jkxm2.set("hkfq_code", 1);
				jkxm2.set("hkfq_time", fenqitime);
				jkxm2.set("hkfq_day", tianshu);

				jkxm2.set("hkfqzlx", df.format(fqzlx + yqyqlx));

				jkxm2.set("hkfq_lx", df.format(fqlx + hkm));
				jkxm2.set("hkfq_cishu", hkfqcishu + yqstate);
				jkxm2.set("hk_time", date2);
				jkxm2.set("hkqd", 0);
				jkxm2.set("hkstate", 0);
				jkxm2.set("tzjx_ts", 0);
				jkxm2.set("tzjx_lx", 0);
				jkxm2.set("tzjx", 0);
				jbdcms3Service.updateJKHK(jkxm2);
				row6.set("neirong",
						"Đề xuất gia hạn đã được duyệt, chúng tôi đã nhận được phí gia hạn "
								+ df.format(hkm)
								+ ", thời hạn trả vay đổi thành " + fenqitime);
			}
			jbdcms3Service.addOrder(addorder);
			String money = "0";
			row6.set("userid", userid);

			row6.set("title", "Thông báo về việc trả vay"); // 还款通知
			row6.set("neirong", "Trả vay thành công " + df.format(hkm));// 成功还款多少钱！
			row6.set("fb_time", date2);
			jbdcms3Service.insertUserMsg(row6);
			// 给还款人短信
			String userName = dataRow2.getString("username").substring(0, 4);
			String appName = "OCEAN";
			if (userName.equals("OCEAN")) {
				appName = "OCEAN";
			}
			String content = "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+appName+" xin thong bao: Ban da thanh toan so tien "+money+". Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.\",\"SmsGuid\":\""+dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]";
			String con = URLEncoder.encode(content, "utf-8");
			SendMsg sendMsg = new SendMsg();
			//String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
			jsonObject.put("error", 0);
			jsonObject.put("msg", "Thành công");
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", -1);
			jsonObject.put("msg",
					"Lỗi hệ thống, vui lòng liên hệ CSKH: 1900234558");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	/**
	 * 还款渠道
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doOfflineSB() throws Exception {
		logger.info("进入还款渠道");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);

		int userid = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (userid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");// 请先登录
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + userid);
		String jkid = getStrParameter("jkid"); // 接收到的还款id
		String userId = jbdcms3Service.getuserid(jkid);
		DataRow user = jbdcms3Service.getRow(jkid);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String hkqd = "";
		// 失败返回
		if (user.getString("hkqd").equals("1")) {
			hkqd = "Cty tnhh dich vu cam do Xuong Thinh";
		} else if (user.getString("hkqd").equals("2")) {
			hkqd = "渠道2";
		} else if (user.getString("hkqd").equals("3")) {
			hkqd = "渠道3";
		} else if (user.getString("hkqd").equals("4")) {
			hkqd = "渠道4";
		}
		DataRow row6 = new DataRow();
		row6.set("userid", userId);
		row6.set("title", "Thông báo về việc trả nợ"); // 还款通知
		row6.set(
				"neirong",
				"So tien" + hkqd + "khong hoan tra thanh cong thong qua"
						+ user.getString("sjsh_money")
						+ "，Vui long lua chon lai phuong thuc thanh toan.");
		row6.set("fb_time", fmtrq.format(calendar.getTime()));
		jbdcms3Service.insertUserMsg(row6);
		int kefuzu[] = { 5 };
		Random random = new Random();
		int xiabiao = random.nextInt(kefuzu.length);
		int kefu = kefuzu[xiabiao];
		for (int i = 0; i < kefuzu.length; i++) {

		}
		DataRow row = new DataRow();
		row.set("id", jkid);
		row.set("hksb", "0");
		row.set("tzjx", 0);
		row.set("kfhksb", kefu);
		jbdcms3Service.updatehkqd(row);
		jsonObject.put("error", 1);
		jsonObject.put("msg", "Thành công");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	/**
	 * 还款渠道
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doOfflineTZJX() throws Exception {
		logger.info("进入停止计息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int userid = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (userid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");// 请先登录
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + userid);
		String jkid = getStrParameter("jkid"); // 接收到的还款id
		String userId = jbdcms3Service.getuserid(jkid);
		DataRow user = jbdcms3Service.getRow(jkid);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		DataRow row = new DataRow();
		row.set("id", jkid);
		row.set("tzjx", 1);
		jbdcms3Service.updatehkqd(row);
		jsonObject.put("error", 1);
		jsonObject.put("msg", "Thành công");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	private com.alibaba.fastjson.JSONObject getRequestJson(
			HttpServletRequest request) throws IOException {
		InputStream in = request.getInputStream();
		byte[] b = new byte[10240];
		int len;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = in.read(b)) > 0) {
			baos.write(b, 0, len);
		}
		String bodyText = new String(baos.toByteArray(), CHARSET_UTF_8);
		com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON
				.parse(bodyText);
		if (IS_DEBUG) {
			logger.info("received notify message:");
			logger.info(JSON.toJSONString(json, true));
		}
		return json;
	}

	@Override
	public ActionResult doDefault() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "userid");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doGetRepaymentList() throws Exception {

		logger.info("还款列表");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuserid =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuserid =accessVeritifivationbase.checkCMSidAndip(cmsuserid, getipAddr());
	    if(cmsuserid==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuserid);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-"
					+ sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-"
					+ sourceStrArray2[0];
		}
		String hkstat = getStrParameter("hkstat");
		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String idCard = "";

		logger.info("temp:" + temp + "startDate:" + startDate + " endDate"
				+ endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			idCard = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcms3Service.getRepaymentList(curPage, 15, userId,
				realName, phone, startDate, endDate, commit, idCard, hkstat);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);
		row.set("hkstat", hkstat);

		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;

	}

	/**
	 * 还款成功订单列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doGetRepayPlanList() throws Exception {

		logger.info("还款成功订单列表");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int temp = getIntParameter("temp", 0);

		int temp1 = getIntParameter("temp1", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate = getStrParameter("startDate");
		String endDate = getStrParameter("endDate");

		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String jkid = "";
		String orderid = "";
		String phone = "";
		String cmsuserid = "";
		String hkbank = "";
		String idno = "";
		String time = "";
		String funpay = "";
		String viettel = "";
		int hkcode = 0;
		int yqcode = 0;
		logger.info("temp:" + temp + "startDate:" + startDate + " endDate"
				+ endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			jkid = tempVelue;
		}
		if (temp == 3) {

			orderid = tempVelue.replace("&acute;", "/").replace("&nbsp;", " ").trim(); // 接收到的用户还款银行订单号;
		}
		if (temp == 4) {

			phone = tempVelue;
		}
		if (temp == 5) {

			hkcode = 1;
		}
		if (temp == 6) {

			hkcode = 2;
		}
		if (temp == 7) {

			hkcode = 3;
		}
		if (temp == 8) {

			hkcode = 4;
		}
		if (temp == 9) {
			
			cmsuserid = tempVelue;
		}
		if (temp == 10) {
			
			hkbank = tempVelue;
		}
		if (temp == 11) {
			
			idno = tempVelue;
		}
		if (temp == 12) {
			
			time = tempVelue;
		}
		if (temp == 13) {
			
			funpay = tempVelue;
		}
		if (temp == 14) {
			
			viettel = tempVelue;
		}
		
		if (temp1 == 1) {

			yqcode = 1;
		}
		if (temp1 == 2) {

			yqcode = 2;
		}
		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcms3Service.getRepayPlanListHK(curPage, 15, userId,
				jkid, phone, hkcode, yqcode, startDate, endDate, commit,
				orderid,cmsuserid,hkbank,idno,time,funpay,viettel);
		List<DataRow> list = jbdcms3Service.getRepayPlanListRow(userId, jkid,
				phone, hkcode, yqcode, startDate, endDate, commit, orderid,cmsuserid,hkbank,idno,time,funpay,viettel);
		
		double total = 0;
		double totalfee = 0;
		for (DataRow dataRow : list) {
			String a = dataRow.getString("rechargemoney").replace(",", "");
			String aa = dataRow.getString("fee").replace(",", "");
			total += Integer.parseInt(a);
			totalfee += Integer.parseInt(aa);

		}
		DecimalFormat df = new DecimalFormat("###,###");
		String sum = df.format(total);
		String sumfee = df.format(totalfee);
		DataRow row = new DataRow();
		row.set("list", page);
		if (!StringHelper.isEmpty(startDate)) {
			DBPage page2 = jbdcms3Service.getRepayPlanListHK(curPage,100000, userId,
					jkid, phone, hkcode, yqcode, startDate, endDate, commit,
					orderid,cmsuserid,hkbank,idno,time,funpay,viettel);
			row.set("list2", page2);
		}else{
			row.set("list2", page);
		}
		
		row.set("temp", temp);
		row.set("temp1", temp1);
		row.set("hkcount", sum);
		row.set("fee", sumfee);
		row.set("tempvalu", tempVelue);

		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}
	/**
	 * 还款渠道
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActionResult doQRHKorder() throws Exception {
		logger.info("进入确认还款");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int userid = SessionHelper.getInt("cmsuserid", getSession()); // 操作员ID
		if (userid == 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Vui lòng đăng nhập trước");// 请先登录
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int xrcode = jbdcms3Service.getSHQX(userid);
		if(xrcode == 0){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "không có quyền thao tác！"); 
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + userid);
		String hkid = getStrParameter("hkid"); // 接收到的还款id
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		DataRow row = new DataRow();
		row.set("id", hkid);
		row.set("qrhkcode", userid);
		row.set("qrhktime", fmtrq.format(new Date()));
		jbdcms3Service.updatehkqr(row);
		jsonObject.put("error", 1);
		jsonObject.put("msg", "Thành công");
		this.getWriter().write(jsonObject.toString());
		return null;
	}
	/**
	 * 待还款用户
	 */
	public ActionResult doGetRecordLoadList() throws Exception {

		logger.info("待还款用户");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");
		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-"
					+ sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-"
					+ sourceStrArray2[0];
		}
		// 审核状态
		String commit = getStrParameter("commit");
		// 定义用户选择条件
		String userId = "";
		String jkid = "";
		String orderid = "";
		String phone = "";

		logger.info("temp:" + temp + "startDate:" + startDate + " endDate"
				+ endDate);
		if (temp == 1) {

			userId = tempVelue;
		}

		if (temp == 2) {

			jkid = tempVelue;
		}
		if (temp == 3) {

			orderid = tempVelue;
		}
		if (temp == 4) {

			phone = tempVelue;
		}

		// 默认第一页
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcms3Service.getRepayPlanList(curPage, 15, userId,
				jkid, phone, startDate, endDate, commit, orderid);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("tempvalu", tempVelue);

		JSONObject object = JSONObject.fromBean(row);
		this.getWriter().write(object.toString());
		return null;
	}

	/**
	 * 放款成功订单
	 */
	public ActionResult doGetFKFindOrderList() throws Exception {

		logger.info("放款成功订单");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl");

		String startDate1 = getStrParameter("startDate");
		String endDate1 = getStrParameter("endDate");
		String startDate = "";
		String endDate = "";
		if (!TextUtils.isEmpty(startDate1) && !TextUtils.isEmpty(endDate1)) {
			String[] sourceStrArray1 = startDate1.split("-");
			String[] sourceStrArray2 = endDate1.split("-");
			startDate = sourceStrArray1[2] + "-" + sourceStrArray1[1] + "-"
					+ sourceStrArray1[0];
			endDate = sourceStrArray2[2] + "-" + sourceStrArray2[1] + "-"
					+ sourceStrArray2[0];
		}

		// 定义用户选择条件
		String userId = "";
		String realName = "";
		String phone = "";
		String orderId = "";

		if (temp == 1) {

			userId = tempVelue;
		}
		if (temp == 2) {

			realName = tempVelue;
		}
		if (temp == 3) {

			phone = tempVelue;
		}
		if (temp == 4) {

			orderId = tempVelue;
		}

		int curPage = getIntParameter("curPage", 1);

		DBPage page = jbdcms3Service.getFindOrderList(curPage, 15, phone,
				orderId, realName, startDate, endDate, userId);

		// 放款总金额
		String fkcount = jbdcms3Service.getfkCount(curPage, 15, phone, orderId,
				realName, startDate, endDate, userId);
		DataRow row = new DataRow();
		row.set("list", page);
		row.set("temp", temp);
		row.set("fkcount", fkcount);
		row.set("tempvalu", tempVelue);
		JSONObject object = JSONObject.fromBean(row);

		this.getWriter().write(object.toString());
		return null;
	}

	// 高级权限
	public ActionResult doGetGaoji() throws Exception {
		JSONObject jsonObject = new JSONObject();
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
		int roleid = jbdcmsService.getCMSuserRoleid(cmsuserid);
		if (roleid != 1 && roleid != 2 && roleid != 17 && roleid != 19 && roleid != 21 && roleid != 24 && roleid != 26 && roleid != 20 
	    		&& roleid != 50 && roleid != 51 && roleid != 52 ) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "ERROR!"); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 1);
			jsonObject.put("msg", "通过"); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doGetMjzxPhoneXH() throws Exception {

		logger.info("进入到数据分析页面");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		DataRow row = new DataRow();
		// 通话总条数
		List<DataRow> sjxh = jbdcms3Service.getSJXH(userid + "");
		String macaddress[] = new String[sjxh.size()];
		String hqphonetype[] = new String[sjxh.size()];
		String phonebrand[] = new String[sjxh.size()];
		String isemulator[] = new String[sjxh.size()];
		for (int k = 0; k < sjxh.size(); k++) {
			DataRow dataRow = sjxh.get(k);
			hqphonetype[k] = dataRow.getString("hqphonetype");
		}
		for (int j = 0; j < sjxh.size(); j++) {
			DataRow dataRow = sjxh.get(j);
			phonebrand[j] = dataRow.getString("phonebrand");
		}
		for (int m = 0; m < sjxh.size(); m++) {
			DataRow dataRow = sjxh.get(m);
			if ("none".equals(dataRow.getString("macaddress"))
					|| "".equals(dataRow.getString("macaddress"))
					|| dataRow.getString("macaddress") == null) {
				macaddress[m] = "00";
			} else {
				macaddress[m] = dataRow.getString("macaddress");
			}

		}
		for (int b = 0; b < sjxh.size(); b++) {
			DataRow dataRow = sjxh.get(b);
			if ("true".equals(dataRow.getString("isemulator"))) {
				isemulator[b] = "YES(是)";
				DataRow row111 = new DataRow();
			} else {
				isemulator[b] = "NO(否)";
			}

		}
		List<DataRow> list = jbdcms3Service.getAllsjxhID(macaddress, userid);
		if (macaddress.length == 0) {
			row.set("xtuserid", 0);
		} else {
			String xtuserid[] = new String[list.size()];
			int cgjkcscs[] = new int[list.size()];
			String cgjkcs[] = new String[list.size()];
			int cgjkcscsyq[] = new int[list.size()];
			String cgjkcsyq[] = new String[list.size()];
			int sqjkcscs[] = new int[list.size()];
			String sqjkcs[] = new String[list.size()];
			int sqjkcscsbj[] = new int[list.size()];
			String sqjkcsbj[] = new String[list.size()];
			String hhzt[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				DataRow row2 = list.get(i);
				if (i == list.size() - 1) {
					xtuserid[i] = row2.getString("userid");
				} else {
					xtuserid[i] = row2.getString("userid") + ",";
				}
				cgjkcscs[i] = jbdcms3Service.getCGJKCountYN(row2
						.getInt("userid"));
				cgjkcscsyq[i] = jbdcms3Service.getCGJKYQCountYN(row2
						.getInt("userid"));
				sqjkcscs[i] = jbdcms3Service.getSQJKCountYN(row2
						.getInt("userid"));
				sqjkcscsbj[i] = jbdcms3Service.getSQJKBJCountYN(row2
						.getInt("userid"));
				if (jbdcms3Service.getHHZTYN(row2.getInt("userid")) == 1) {
					hhzt[i] = row2.getString("userid") + ",";
				}
				if (i == list.size() - 1) {
					cgjkcs[i] = cgjkcscs[i] + "";
				} else {
					cgjkcs[i] = cgjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					cgjkcsyq[i] = cgjkcscsyq[i] + "";
				} else {
					cgjkcsyq[i] = cgjkcscsyq[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcs[i] = sqjkcscs[i] + "";
				} else {
					sqjkcs[i] = sqjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcsbj[i] = sqjkcscsbj[i] + "";
				} else {
					sqjkcsbj[i] = sqjkcscsbj[i] + ",";
				}
			}
			row.set("xtuserid", xtuserid);
			row.set("cgjkcs", cgjkcs);
			row.set("cgjkcsyq", cgjkcsyq);
			row.set("sqjkcs", sqjkcs);
			row.set("sqjkcsbj", sqjkcsbj);
			row.set("hhzt", hhzt);
		}

		// 获取用户的通话记录
		row.set("phonebrand", phonebrand);
		row.set("hqphonetype", hqphonetype);
		row.set("isemulator", isemulator);
		JSONObject object = JSONObject.fromBean(row);
		// System.out.println(object.toString());
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetMjzxPhoneYN() throws Exception {

		logger.info("进入到数据分析页面");
		int userid = getIntParameter("user_id", 0);
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);

		String phone = getStrParameter("th_phone");
		int thts = getIntParameter("thts");
		int thsc = getIntParameter("thsc");
		int thsj = getIntParameter("thsj");
		int code = 0;
		if (phone.equals("0")) {
			phone = "";
		}
		if (thts == 1) {
			code = 1;
		} else if (thsc == 1) {
			code = 2;
		} else if (thsj == 1) {
			code = 3;
		}
		DataRow row = new DataRow();
		// 通话总条数
		int tonghuaCount = jbdcms3Service.getTonghuaCountYN(userid);
		int xttonghuaCount = jbdcms3Service.getXTTonghuaCountYN(userid);
		int bl = 0;

		if (tonghuaCount != 0) {
			bl = xttonghuaCount * 100 / tonghuaCount;
		}
		List<DataRow> listnum = jbdcms3Service.getAllthjlNum(userid);

		String number[] = new String[listnum.size()];
		for (int k = 0; k < listnum.size(); k++) {
			DataRow dataRow = listnum.get(k);
			number[k] = dataRow.getString("number");
		}
		if (number.length == 0) {
			row.set("xtuserid", 0);
		} else {
			List<DataRow> list = jbdcms3Service.getAllthjlID(number, userid);
			String xtuserid[] = new String[list.size()];
			int cgjkcscs[] = new int[list.size()];
			String cgjkcs[] = new String[list.size()];
			int cgjkcscsyq[] = new int[list.size()];
			String cgjkcsyq[] = new String[list.size()];
			int sqjkcscs[] = new int[list.size()];
			String sqjkcs[] = new String[list.size()];
			int sqjkcscsbj[] = new int[list.size()];
			String sqjkcsbj[] = new String[list.size()];
			String hhzt[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				DataRow row2 = list.get(i);
				if (i == list.size() - 1) {
					xtuserid[i] = row2.getString("userid");
				} else {
					xtuserid[i] = row2.getString("userid") + ",";
				}
				cgjkcscs[i] = jbdcms3Service.getCGJKCountYN(row2
						.getInt("userid"));
				cgjkcscsyq[i] = jbdcms3Service.getCGJKYQCountYN(row2
						.getInt("userid"));
				sqjkcscs[i] = jbdcms3Service.getSQJKCountYN(row2
						.getInt("userid"));
				sqjkcscsbj[i] = jbdcms3Service.getSQJKBJCountYN(row2
						.getInt("userid"));
				if (jbdcms3Service.getHHZTYN(row2.getInt("userid")) == 1) {
					hhzt[i] = row2.getString("userid") + ",";
				}
				if (i == list.size() - 1) {
					cgjkcs[i] = cgjkcscs[i] + "";
				} else {
					cgjkcs[i] = cgjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					cgjkcsyq[i] = cgjkcscsyq[i] + "";
				} else {
					cgjkcsyq[i] = cgjkcscsyq[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcs[i] = sqjkcscs[i] + "";
				} else {
					sqjkcs[i] = sqjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcsbj[i] = sqjkcscsbj[i] + "";
				} else {
					sqjkcsbj[i] = sqjkcscsbj[i] + ",";
				}
			}
			row.set("xtuserid", xtuserid);
			row.set("cgjkcs", cgjkcs);
			row.set("cgjkcsyq", cgjkcsyq);
			row.set("sqjkcs", sqjkcs);
			row.set("sqjkcsbj", sqjkcsbj);
			row.set("hhzt", hhzt);
		}

		// 获取用户的通话记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserThYNGJ(curPage, 15, userid, phone,
				code);
		DataRow lxrhm = jbdcmsService.personYN(userid);
		row.set("list", page);
		row.set("tonghuaCount", tonghuaCount);

		row.set("lxrhm", lxrhm);
		row.set("th_phone", phone);
		row.set("xttonghuaCount", xttonghuaCount);
		row.set("bl", bl);
		DataRow row11 = new DataRow();
		row11.set("id", userid);
		row11.set("thjlbl", bl);
		jbdcmsService.updateUserBL(row11);
		JSONObject object = JSONObject.fromBean(row);
		// System.out.println(object.toString());
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetMjzxTXL() throws Exception {

		logger.info("进入到数据分析页面");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);

		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
		if (cmsuserid == 0) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + cmsuserid);
		String phone = "";
		if (temp == 1) {

			phone = tempVelue;
		}

		DataRow row = new DataRow();
		// 通话总条数
		int tonghuaCount = jbdcms3Service.getTXLCountYN(userid);
		int xttonghuaCount = jbdcms3Service.getXTTXLCountYN(userid);
		int bl = 0;

		if (tonghuaCount != 0) {
			bl = xttonghuaCount * 100 / tonghuaCount;
		}
		List<DataRow> listnum = jbdcms3Service.getAlltxlNum(userid);

		String number[] = new String[listnum.size()];
		for (int k = 0; k < listnum.size(); k++) {
			DataRow dataRow = listnum.get(k);
			number[k] = dataRow.getString("phone");
		}
		if (number.length == 0) {
			row.set("xtuserid", 0);
		} else {
			List<DataRow> list = jbdcms3Service.getAlltxlID(number, userid);
			String xtuserid1[] = new String[list.size()];
			int cgjkcscs[] = new int[list.size()];
			String cgjkcs1[] = new String[list.size()];
			int cgjkcscsyq[] = new int[list.size()];
			String cgjkcsyq1[] = new String[list.size()];
			int sqjkcscs[] = new int[list.size()];
			String sqjkcs1[] = new String[list.size()];
			int sqjkcscsbj[] = new int[list.size()];
			String sqjkcsbj1[] = new String[list.size()];
			String hhzt1[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				DataRow row2 = list.get(i);
				if (i == list.size() - 1) {
					xtuserid1[i] = row2.getString("userid");
				} else {
					xtuserid1[i] = row2.getString("userid") + ",";
				}
				cgjkcscs[i] = jbdcms3Service.getCGJKCountYN1(row2
						.getInt("userid"));
				cgjkcscsyq[i] = jbdcms3Service.getCGJKYQCountYN1(row2
						.getInt("userid"));
				sqjkcscs[i] = jbdcms3Service.getSQJKCountYN1(row2
						.getInt("userid"));
				sqjkcscsbj[i] = jbdcms3Service.getSQJKBJCountYN1(row2
						.getInt("userid"));
				if (jbdcms3Service.getHHZTYN1(row2.getInt("userid")) == 1) {
					hhzt1[i] = row2.getString("userid") + ",";
				}
				if (i == list.size() - 1) {
					cgjkcs1[i] = cgjkcscs[i] + "";
				} else {
					cgjkcs1[i] = cgjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					cgjkcsyq1[i] = cgjkcscsyq[i] + "";
				} else {
					cgjkcsyq1[i] = cgjkcscsyq[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcs1[i] = sqjkcscs[i] + "";
				} else {
					sqjkcs1[i] = sqjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcsbj1[i] = sqjkcscsbj[i] + "";
				} else {
					sqjkcsbj1[i] = sqjkcscsbj[i] + ",";
				}
			}

			row.set("xtuserid1", xtuserid1);
			row.set("cgjkcs1", cgjkcs1);
			row.set("cgjkcsyq1", cgjkcsyq1);
			row.set("sqjkcs1", sqjkcs1);
			row.set("sqjkcsbj1", sqjkcsbj1);
			row.set("hhzt1", hhzt1);
		}

		// 获取用户的通话记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserTongxunlu(curPage, 15, userid,
				phone, "", "");
		DataRow lxrhm = jbdcmsService.personYN(userid);
		row.set("list", page);
		row.set("tonghuaCount1", tonghuaCount);
		row.set("lxrhm", lxrhm);
		row.set("th_phone", phone);
		row.set("xttonghuaCount1", xttonghuaCount);
		row.set("bl1", bl);
		DataRow row11 = new DataRow();
		row11.set("id", userid);
		row11.set("txlbl", bl);
		jbdcmsService.updateUserBL(row11);
		JSONObject object = JSONObject.fromBean(row);
		// System.out.println(object.toString());
		this.getWriter().write(object.toString());
		return null;
	}

	public ActionResult doGetMjzxMsg() throws Exception {

		logger.info("进入到数据分析页面");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		
		int temp = getIntParameter("temp", 0);
		String tempVelue = getStrParameter("tempvl").replace("&nbsp;", " ");
		int userid = getIntParameter("user_id", 0);

		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
		if (cmsuserid == 0) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		logger.info("请求ID:" + cmsuserid);
		String content = "";
		String phone = "";
		if (temp == 1) {

			content = tempVelue;
		}
		if (temp == 2) {

			phone = tempVelue;
		}

		DataRow row = new DataRow();
		// 通话总条数
		int tonghuaCount2 = jbdcms3Service.getSMSCountYN(userid);
		int xttonghuaCount2 = jbdcms3Service.getXTSMSCountYN(userid);
		int bl2 = 0;

		if (tonghuaCount2 != 0) {
			bl2 = xttonghuaCount2 * 100 / tonghuaCount2;
		}
		List<DataRow> listnum = jbdcms3Service.getAllsmsNum(userid);

		String number[] = new String[listnum.size()];
		for (int k = 0; k < listnum.size(); k++) {
			DataRow dataRow = listnum.get(k);
			number[k] = dataRow.getString("phone");
		}
		if (number.length == 0) {
			row.set("xtuserid", 0);
		} else {
			List<DataRow> list = jbdcms3Service.getAllsmsID(number, userid);
			String xtuserid2[] = new String[list.size()];
			int cgjkcscs[] = new int[list.size()];
			String cgjkcs2[] = new String[list.size()];
			int cgjkcscsyq[] = new int[list.size()];
			String cgjkcsyq2[] = new String[list.size()];
			int sqjkcscs[] = new int[list.size()];
			String sqjkcs2[] = new String[list.size()];
			int sqjkcscsbj[] = new int[list.size()];
			String sqjkcsbj2[] = new String[list.size()];
			String hhzt2[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				DataRow row2 = list.get(i);
				if (i == list.size() - 1) {
					xtuserid2[i] = row2.getString("userid");
				} else {
					xtuserid2[i] = row2.getString("userid") + ",";
				}
				cgjkcscs[i] = jbdcms3Service.getCGJKCountYN2(row2
						.getInt("userid"));
				cgjkcscsyq[i] = jbdcms3Service.getCGJKYQCountYN2(row2
						.getInt("userid"));
				sqjkcscs[i] = jbdcms3Service.getSQJKCountYN2(row2
						.getInt("userid"));
				sqjkcscsbj[i] = jbdcms3Service.getSQJKBJCountYN2(row2
						.getInt("userid"));
				if (jbdcms3Service.getHHZTYN2(row2.getInt("userid")) == 1) {
					hhzt2[i] = row2.getString("userid") + ",";
				}
				if (i == list.size() - 1) {
					cgjkcs2[i] = cgjkcscs[i] + "";
				} else {
					cgjkcs2[i] = cgjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					cgjkcsyq2[i] = cgjkcscsyq[i] + "";
				} else {
					cgjkcsyq2[i] = cgjkcscsyq[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcs2[i] = sqjkcscs[i] + "";
				} else {
					sqjkcs2[i] = sqjkcscs[i] + ",";
				}
				if (i == list.size() - 1) {
					sqjkcsbj2[i] = sqjkcscsbj[i] + "";
				} else {
					sqjkcsbj2[i] = sqjkcscsbj[i] + ",";
				}
			}
			row.set("xtuserid2", xtuserid2);
			row.set("cgjkcs2", cgjkcs2);
			row.set("cgjkcsyq2", cgjkcsyq2);
			row.set("sqjkcs2", sqjkcs2);
			row.set("sqjkcsbj2", sqjkcsbj2);
			row.set("hhzt2", hhzt2);
		}

		// 获取用户的通话记录
		int curPage = getIntParameter("curPage", 1);
		DBPage page = jbdcmsService.getUserMsgGJ(curPage, 50, userid, content,
				phone);
		DataRow lxrhm = jbdcmsService.personYN(userid);
		row.set("list", page);
		row.set("tonghuaCount2", tonghuaCount2);
		
		row.set("lxrhm", lxrhm);
		row.set("th_phone", phone);
		row.set("xttonghuaCount2", xttonghuaCount2);
		row.set("bl2", bl2);
		DataRow row11 = new DataRow();
		row11.set("id", userid);
		row11.set("smsbl", bl2);
		jbdcmsService.updateUserBL(row11);
		JSONObject object = JSONObject.fromBean(row);
		// System.out.println(object.toString());
		this.getWriter().write(object.toString());
		return null;
	}

	// 催收信息
	public ActionResult doGetCuishou() throws Exception {

		logger.info("进入查看催收信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);

		String recid = getStrParameter("recid"); // 借款项目的id ;
		// 根据项目的id 返回审核项目的信息
		DataRow jkInfo = jbdcms3Service.getCuishouid(recid);
		if (jkInfo != null) {
			jsonObject.put("error", 1);
			jsonObject.put("jkinfo", jkInfo);
		} else {

			jsonObject.put("error", -1);
			jsonObject.put("msg", "没有此用户！联系技术");

		}
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 第二步审核信息
	public ActionResult doTJCuishou() throws Exception {

		logger.info("进入提交催收ID");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		

		String recid = getStrParameter("rec_id"); // 借款项目的id ;
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		String user_id = getStrParameter("user_id"); // 用户id ;
		String cuishouid = getStrParameter("csid"); // 改为新的催收 人员id;
		// 根据项目的id 返回审核项目的信息
		
		int roleid = jbdcms3Service.getCSRoleID(cuishouid);
		int cuishouidstate = jbdcms3Service.getCSRoleIDState(cuishouid);
		if(cuishouidstate == 0){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "该催收员已被禁用，请检查！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		//xiong20190703
		if(roleid !=19 && roleid!=20 && roleid !=32 && roleid!=21 && roleid!=22 && roleid!=23 && roleid!=25 && roleid!=50 && roleid!=51 && roleid!=24 && roleid!=54 && roleid!=26){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "先分组再分单！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		SimpleDateFormat fmtrq = new SimpleDateFormat("yyyy-MM-dd");
		String time111 = fmtrq.format(new Date());
		SimpleDateFormat fmt_qr = new SimpleDateFormat("dd-MM-yyyy");
		String time222 = fmt_qr.format(new Date());
		

		String cuishouzz = jbdcms3Service.getCuishouzz(recid);
		String cuishou_id = jbdcms3Service.getCuishouID(recid);//以前的催收人员
		String cuishou_m1 = jbdcms3Service.getCuishouM1(recid);
		int m1status = jbdcms3Service.getCuishouM1State(cuishou_m1);

		if (cuishou_id.equals(cuishouid)) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "不能更换给自己！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

		String sjshjine = jbdcms3Service.getSJSHJE(recid);
		String yuqlx = jbdcms3Service.getYQLX(recid);
		int yuqts = jbdcms3Service.getYQTS(recid);
		/*int yuqts = jbdcms3Service.getYQTS(recid);
			if(yuqts > 10){
			jsonObject.put("error", -1);
			jsonObject.put("msg", "逾期天数大于10天，通知技术检查！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}*/
		int zje = Integer.parseInt(sjshjine.replace(",", "")) + Integer.parseInt(yuqlx.replace(",", ""));
		int cuishouidm1 = 0;
		int cuishouidm2 = 0;
		int cuishou_idm1 = 0;
		int cuishou_idm2 = 0;
		//获取M1的老员工
		List<DataRow> cuishoum1 = jbdcms3Service.getAllcuishouM1();
		//获取M2的老员工
		List<DataRow> cuishoum2 = jbdcms3Service.getAllcuishouM2();
		int code = 0;

		for (int i = 0; i < cuishoum1.size(); i++) {
			DataRow rowm1 = cuishoum1.get(i);
			//新的催收人员和是不是在m1
			if (cuishouid.equals(rowm1.getString("user_id"))) {
				cuishouidm1 = 1;
			}
			//旧的催收人员是不是m1
			if (cuishou_id.equals(rowm1.getString("user_id"))) {
				cuishou_idm1 = 1;
				code = 1;
			}
		}
		for (int j = 0; j < cuishoum2.size(); j++) {
			DataRow rowm1 = cuishoum2.get(j);
			if (cuishouid.equals(rowm1.getString("user_id"))) {
				cuishouidm2 = 1;
			}
			if (cuishou_id.equals(rowm1.getString("user_id"))) {
				cuishou_idm2 = 1;
				code = 1;
			}
		}
		/*if ("32".equals(cuishou_id) || "5003".equals(cuishou_id)
				|| "5004".equals(cuishou_id) || "5005".equals(cuishou_id)
				|| "5006".equals(cuishou_id)) {
			if (!"0".equals(cuishou_m1) && m1status == 1) {
				cuishouid = cuishou_m1;
			}
		}*/
//		if ((cuishouidm1 == 1 && cuishou_idm2 == 1)
//				|| (cuishou_idm1 == 1 && cuishouidm2 == 1)) {
//			jsonObject.put("error", -1);
//			jsonObject.put("msg",
//					"M1 chỉ phân được cho M1, M2 chỉ phân được cho M2 ");
//			this.getWriter().write(jsonObject.toString());
//			return null;
//		} else {
			//查询到了sd_accountuserhk信息
			DataRow datacs = jbdcms3Service.getCuishouBG(cuishouid, time111);
			//添加和更新到sd_accountuserhk表中
			if (datacs == null) {

				DataRow row11 = new DataRow();
				row11.set("csid", cuishouid);
				row11.set("totaljine", zje);
				row11.set("time", time111);
				jbdcms3Service.insertCuiBG(row11);

			} else {
				double ysje = datacs.getDouble("totaljine");
				int cuiid = datacs.getInt("id");
				DataRow row11 = new DataRow();
				row11.set("id", cuiid);
				row11.set("totaljine", ysje + zje);
				jbdcms3Service.updateCuiBG(row11);
			}
			//更改sd_accountuserhk这个表的信息
			if (code == 1) {
				DataRow datacs111 = jbdcms3Service.getCuishouBG(cuishou_id,
						time111);
				if (datacs111 == null) {
					DataRow row11 = new DataRow();
					row11.set("csid", cuishou_id);
					row11.set("totaljine", -zje);
					row11.set("time", time111);
					jbdcms3Service.insertCuiBG(row11);
				} else {
					double ysje = datacs111.getDouble("totaljine");
					int cuiid = datacs111.getInt("id");
					DataRow row11 = new DataRow();
					row11.set("id", cuiid);
					row11.set("totaljine", ysje - zje);
					jbdcms3Service.updateCuiBG(row11);
				}
			}
			
			//更改催收人员sd_new_jkyx
			DataRow row = new DataRow();
			DataRow rowre = new DataRow();
			row.set("id", recid);
			row.set("cuishou_id", cuishouid);
			int cuishou_z =0; // c催收组M1\M2\M3
			if(yuqts >0 && yuqts <=3 ) {
				cuishou_z =4; 
				row.set("cuishou_m0", cuishouid);
	 		}else if(yuqts >3 && yuqts <=15) {
	 			cuishou_z =1; 
				row.set("cuishou_m1", cuishouid);
	 		}else if(yuqts >15 && yuqts <=45 ) {
	 			cuishou_z =2; 
				row.set("cuishou_m2", cuishouid);
	 		}else if(yuqts >45 ) {
	 			cuishou_z =3; 
				row.set("cuishou_m3", cuishouid);
	 		}
			row.set("cuishouzz", cuishouzz + "," + cuishouid);
			jbdcms3Service.updateCuishouID(row);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			rowre.set("user_id",user_id);
			rowre.set("rec_id",recid);
			rowre.set("cmsuser_id",cuishouid);
			rowre.set("create_time",df.format(new Date()));
			rowre.set("cmsuserld_id",cmsuserid);
			//添加信息sd_cuishou_record
			jbdcms3Service.insertCuishouRecord(rowre);
			
			//xiong-20190703-手动更改催收人员，修改sd_cuishou_fendan信息的				
//			if(cuishouidm1 ==1) {
//				//m1
//				int a=jbdcms3Service.updateSdCuishouFendan(cuishouid, recid, "1");
//				logger.info("手动修改催收人员订单sd_cuishou_fendan修改了m1成功为1"+a);
//			}else if(cuishouidm2==1) {	
//				//m2
//				int b=jbdcms3Service.updateSdCuishouFendan(cuishouid, recid, "2");
//				logger.info("手动修改催收人员订单sd_cuishou_fendan修改了m2成功为1"+b);
//			}else {
//				//m3
//				int c=jbdcms3Service.updateSdCuishouFendan(cuishouid, recid, "3");				
//				logger.info("手动修改催收人员订单sd_cuishou_fendan修改了m3成功为1"+c);
//							
//			}
			//修改sd_cuishou_fendan信息的
			//sd_cuishou_fendan 表 lin
			List<DataRow> cuishoufendanList =jbdcms3Service.getcuishoufendanid(recid,cuishou_id);
			int fendancs =0;
			if(cuishoufendanList.size()>0) {
				DataRow datafendan = cuishoufendanList.get(0);   //只获取最大id，（最新的数据）
				int fendanid = datafendan.getInt("id");
			    fendancs =datafendan.getInt("fendan_cs");
				double cuihuijine_old = jbdcms3Service.getfendancuihuijine(recid,cuishou_id,time111.substring(0, 7));  //以前催回金额
		 		double cuihuijinezs = jbdcms3Service.getrechargeMoneyAccount(recid,cuishou_id,time222.substring(3, 10));  //a总催回金额
		 		 //f 分出人更新
		 		DataRow  dataRow1 = new DataRow();
		 		dataRow1.set("id",fendanid);
		 		dataRow1.set("cuishou_jine",cuihuijinezs-cuihuijine_old);
		 		dataRow1.set("recharge_money",cuihuijinezs-cuihuijine_old);
		 		jbdcms3Service.updatefendandata(dataRow1);
			}
			
//	 		int cuishou_z =0; // c催收组M1\M2\M3
//	 		//f分入催收单插入数据;
//	 		if(cuishouidm1 ==1) {
//	 			cuishou_z =1;
//	 		}else if(cuishouidm2==1) {
//	 			cuishou_z =2;
//	 		}else {
//	 			cuishou_z =3;
//	 		}
	 		DataRow  dataRow2 = new DataRow();
	 		dataRow2.set("user_id",user_id);
	 		dataRow2.set("jk_id",recid);
	 		dataRow2.set("cuishou_id",cuishouid);
	 		dataRow2.set("fendan_time",time111);
	 	    dataRow2.set("cuishou_jine",zje);
	 		dataRow2.set("cuishou_z",cuishou_z);
	 		dataRow2.set("fendan_cs",fendancs);
	 		dataRow2.set("recharge_money",0);
	 		jbdcms3Service.insertSdCuishouFendan(dataRow2);
			
			jsonObject.put("error", 1);
			jsonObject.put("msg", "成功");
			this.getWriter().write(jsonObject.toString());
			return null;
//		}
	}

	// 审核信息
	public ActionResult doGetShenHe() throws Exception {

		logger.info("进入查看审核信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		//一组
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		//二组
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		//三组
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		//四组
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		if (!shenhezuzz1.equals(cmsuserid+"") && !shenhezuzz2.equals(cmsuserid+"") && !shenhezuzz3.equals(cmsuserid+"") && !shenhezuzz4.equals(cmsuserid+"") 
//				&& cmsuserid != 8 && cmsuserid != 888 && cmsuserid != 6 && cmsuserid != 222 && cmsuserid !=9999 && cmsuserid !=8888 && cmsuserid !=2038
				&&!roleauthoritymangement.getRoleAM_SHlist(cmsuserid+"") ) {
			jsonObject.put("error", -3);
			jsonObject.put("msg", "Quyền hạn của chủ quản！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		String recid = getStrParameter("recid"); // 借款项目的id ;
		// 根据项目的id 返回审核项目的信息
		DataRow jkInfo = jbdcms3Service.getShenheid(recid);

		if (jkInfo.getInt("cl_status") != 0) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", "Khách hàng này đã được thẩm định！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (jkInfo != null) {
			jsonObject.put("error", 1);
			jsonObject.put("jkinfo", jkInfo);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {

			jsonObject.put("error", -2);
			jsonObject.put("msg", "Lỗi hệ thống, vui lòng liên hệ kỹ thuật！");
			this.getWriter().write(jsonObject.toString());
			return null;

		}
	}

	public ActionResult doGetFSDX() throws Exception {

		logger.info("进入查看审核信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		SimpleDateFormat famt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = famt.format(new Date());
		String recid = getStrParameter("recid"); // 借款项目的id ;
		int userId = jbdcmsService.getUserID(recid);
		DataRow dataRow2 = jbdcmsService.getUserInfo(userId);
		String userName = dataRow2.getString("username");
		String appName = "OCEAN";
		userName = userName.substring(0, 4);
		if (userName.equals("OCEAN")) {
			appName = "OCEAN";
		}
		String realname = jbdcmsService.getUserName(userId);
		String content = "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+appName+" xin chao "+realname+".Vui long nghe may tu Mofa de hoan tat xac minh. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.\",\"SmsGuid\":\""+dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]";
		String con = URLEncoder.encode(content, "utf-8");
		SendMsg sendMsg = new SendMsg();
		//String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
		DataRow row = new DataRow();
		row.set("userid", userId);
		row.set("msg",
				"mofa xin chao "+ realname
						+ ". Vui long nghe may tu OCEAN de hoan tat xac minh. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.");
		row.set("msgtype", "未接短信");
		row.set("create_time", time);
		row.set("cl_ren", cmsuserid);
		row.set("jkid", recid);
		jbdcms3Service.insertUserFSDXMsg(row);
		DataRow row2 = new DataRow();
		row2.set("id", recid);
		row2.set("fsdxtime", time);
		row2.set("fsdxcode", 1);
		jbdcms3Service.updateJKHK(row2);
		jsonObject.put("error", 1);
		jsonObject.put("msg", "成功");
		this.getWriter().write(jsonObject.toString());
		return null;

	}

	public ActionResult doGetFSDXGJ() throws Exception {

		logger.info("进入发送错误信息");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		SimpleDateFormat famt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = famt.format(new Date());
		String recid = getStrParameter("recid"); // 短信的选择;
		int dxnr = getIntParameter("bhstate"); // 短信的选择;

		int userId = jbdcmsService.getUserID(recid);
		DataRow dataRow2 = jbdcmsService.getUserInfo(userId);
		String userName = dataRow2.getString("username");
		String appName = "OCEAN";
		userName = userName.substring(0, 4);
		if (userName.equals("OCEAN")) {
			appName = "OCEAN";
		}
		String realname = jbdcmsService.getUserName(userId);
		if (dxnr == 0) {
			return null;
		} else {
			String smscontent = "";
			 if(dxnr == 1){
				   smscontent =appName+" xin chao "+realname+" Xin cap nhat lai thoi han vay 15 hoac 30 ngay. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.";
			   }else if(dxnr == 2){
				   smscontent =appName+" xin chao "+realname+" Vui long chup lai ro net CMND ban goc mat truoc, mat sau, hinh chan dung cam CMND. Thac mac vui long goi 1900234558.";
			   }else if(dxnr == 3){
				   smscontent =appName+" xin chao "+realname+" Vui long cap nhat lai chinh xac thong tin ngan hang. Thac mac xin inbox http://bit.ly/2QJAh16, hotline: 1900234558.";
			   }else if(dxnr == 4){
				   smscontent =appName+" xin chao "+realname+" Vui long cap nhat lai thong tin cong viec. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.";
			   }else if(dxnr == 5){
				   smscontent =appName+" xin chao "+realname+" Vui long cap nhat lai thong tin nguoi lien he. Moi thac mac vui long inbox http://bit.ly/2QJAh16, hotline: 1900234558.";
			   }else if(dxnr == 6){
				   smscontent ="Cam on "+realname+" da su dung dich vu cua "+appName+". Ban co the gui de xuat vay moi bat cu luc nao khi co nhu cau. Tran trong !";
			   }
			String content = "[{\"PhoneNumber\":\""+dataRow2.getString("mobilephone")+"\",\"Message\":\""+smscontent+"\",\"SmsGuid\":\""+dataRow2.getString("mobilephone")+"\",\"ContentType\":1}]";
			String con = URLEncoder.encode(content, "utf-8");
			SendMsg sendMsg = new SendMsg();
			//String returnString = SendMsg.sendMessageByGet(con,dataRow2.getString("mobilephone"));
			DataRow row = new DataRow();
			row.set("userid", userId);
			row.set("msg", smscontent);
			row.set("msgtype", "信息错误短信");
			row.set("create_time", time);
			row.set("cl_ren", cmsuserid);
			row.set("jkid", recid);
			jbdcms3Service.insertUserFSDXMsg(row);
			DataRow row2 = new DataRow();
			row2.set("id", recid);
			row2.set("fsdxtime", time);
			jbdcms3Service.updateJKHK(row2);
			jsonObject.put("error", 1);
			jsonObject.put("msg", "成功");
			this.getWriter().write(jsonObject.toString());
			return null;
		}

	}

	// 第二步审核信息
	public ActionResult doTJShenHe() throws Exception {

		logger.info("进入提交审核ID");
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		String recid = getStrParameter("rec_id"); // 借款项目的id ;
		String user_id = getStrParameter("user_id"); // 借款项目的id ;
		int shenheid = getIntParameter("shid"); // 借款项目的id ;
		if(cmsuserid == 2001 || cmsuserid == 2015 || cmsuserid == 2004) {
			jsonObject.put("error", -3);
			jsonObject.put("msg", "Tạm thời không thể chia hồ sơ！");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		System.out.println(shenheid);
		//一组
		String shenhezu1 = jbdcmsService.getPJGZSHENHE1();
		String shenhezuzz1 = jbdcmsService.getPJGZSHENHEZZ1();
		String shzu1[] = shenhezu1.split(",");
		//二组
		String shenhezu2 = jbdcmsService.getPJGZSHENHE2();
		String shenhezuzz2 = jbdcmsService.getPJGZSHENHEZZ2();
		String shzu2[] = shenhezu2.split(",");
		//三组
		String shenhezu3 = jbdcmsService.getPJGZSHENHE3();
		String shenhezuzz3 = jbdcmsService.getPJGZSHENHEZZ3();
		String shzu3[] = shenhezu3.split(",");
		//四组
		String shenhezu4 = jbdcmsService.getPJGZSHENHE4();
		String shenhezuzz4 = jbdcmsService.getPJGZSHENHEZZ4();
		String shzu4[] = shenhezu4.split(",");
		int code = 0;
		for (int i = 0; i < shzu1.length; i++) {
			if((shenheid+"").equals(shzu1[i])){
				code=1;
			}
		}
		for (int ii = 0; ii < shzu2.length; ii++) {
			if((shenheid+"").equals(shzu2[ii])){
				code=2;
			}
		}
		for (int iii = 0; iii < shzu3.length; iii++) {
			if((shenheid+"").equals(shzu3[iii])){
				code=3;
			}
		}
		for (int iiii = 0; iiii < shzu4.length; iiii++) {
			if((shenheid+"").equals(shzu4[iiii])){
				code=4;
			}
		}
		System.out.println(code);
		if(!String.valueOf(cmsuserid).equals("2038") ) {
			if((((cmsuserid+"").equals(shenhezuzz1) && code !=1) || ((cmsuserid+"").equals(shenhezuzz2) && code !=2) || ((cmsuserid+"").equals(shenhezuzz3) && code !=3) || ((cmsuserid+"").equals(shenhezuzz4) && code !=4))){
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Thẩm định viên này không thuộc nhóm bạn,Không thể chia case！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		}
		
		// 根据项目的id 返回审核项目的信息
		int onesh = jbdcms3Service.getOneSH(recid);
		/*if(onesh == 50 || onesh == 98 || onesh == 99 || onesh == 100){
			shenheid = onesh ;
		}*/
		DataRow row = new DataRow();
		row.set("id", recid);
		row.set("onesh", shenheid);
		row.set("twosh", shenheid);
		
		/*if (shenheid == 17 || shenheid == 1006 || shenheid == 1007
				|| shenheid == 1008 || shenheid == 1010 
				|| shenheid == 1012 || shenheid == 1014
				|| shenheid == 1016 || shenheid == 1018 || shenheid == 1019 || shenheid == 1020) {
			row.set("threesh", 53);
		} else {*/
			row.set("threesh", shenheid);
		//}
		jbdcms3Service.updateCuishouID(row);
		DataRow row111 = new DataRow();
		row111.set("jkid", recid);
		row111.set("beforeid", onesh);
		row111.set("afterid", shenheid);
		jbdcms3Service.insertUserSH(row111);
		jsonObject.put("error", 1);
		jsonObject.put("msg", "成功");
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	public ActionResult doShangChuanTHJL() {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		String recid = getStrParameter("rec_id"); // 借款项目的id ;
		String user_id = getStrParameter("user_id"); // 借款项目的id ;
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (cmsuserid != 14 && cmsuserid != 15 && cmsuserid != 18
				&& cmsuserid != 102 && cmsuserid != 13 && cmsuserid != 16) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			jsonObject.put("msg", "Không đủ quyền");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		List<DataRow> list = jbdcms3Service.getAllTHJL(user_id);
		JSONObject json = new JSONObject();
		com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
		for (int i = 0; i < list.size(); i++) {
			com.alibaba.fastjson.JSONObject lan1 = new com.alibaba.fastjson.JSONObject();
			DataRow datarow = list.get(i);
			lan1.put("name", datarow.getString("name"));
			lan1.put("phone", datarow.getString("number"));
			array.add(lan1);
		}
		int codeid = 0;
		if (cmsuserid == 14) {
			codeid = 109;
		} else if (cmsuserid == 15) {
			codeid = 104;
		} else if (cmsuserid == 17) {
			codeid = 103;
		} else if (cmsuserid == 18) {
			codeid = 105;
		} else if (cmsuserid == 102) {
			codeid = 102;
		} else if (cmsuserid == 13) {
			codeid = 107;
		} else if (cmsuserid == 16) {
			codeid = 108;
		}
		json.put("secret", "79391f6897c8b4875f7d2eb6794a7fb5");
		json.put("customerId", user_id);
		json.put("phoneList", array);
		json.put("agent", codeid);

		// Postaliyun postviettel = new Postaliyun();
		String url = "http://apps.worldfone.vn/worldfone4x/api/olava_diallistServices/importDiallistDetail";
		// String param = json.toString();

		String response = PostHttp.getJsonData(json, url);
		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject.parseObject(response);
		String code = json1.getString("status");
		String msg = json1.getString("message");
		if ("FAILED".equals(code)) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", msg);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			/*
			 * postviettel.sendPost(url, param);
			 * System.out.println(postviettel.sendPost(url, param));
			 */
			jsonObject.put("error", 1);
			jsonObject.put("msg", "Thành công");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doShangChuanTXL() {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		String recid = getStrParameter("rec_id"); // 借款项目的id ;
		String user_id = getStrParameter("user_id"); // 借款项目的id ;
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		if (cmsuserid != 14 && cmsuserid != 15 && cmsuserid != 18
				&& cmsuserid != 102 && cmsuserid != 13 && cmsuserid != 16) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			jsonObject.put("msg", "Không đủ quyền");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		List<DataRow> list = jbdcms3Service.getAllTXL(user_id);
		JSONObject json = new JSONObject();
		com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
		for (int i = 0; i < list.size(); i++) {
			com.alibaba.fastjson.JSONObject lan1 = new com.alibaba.fastjson.JSONObject();
			DataRow datarow = list.get(i);
			lan1.put("name", datarow.getString("name"));
			lan1.put("phone", datarow.getString("phone"));
			array.add(lan1);
		}
		int codeid = 0;
		if (cmsuserid == 14) {
			codeid = 109;
		} else if (cmsuserid == 15) {
			codeid = 104;
		} else if (cmsuserid == 17) {
			codeid = 103;
		} else if (cmsuserid == 18) {
			codeid = 105;
		} else if (cmsuserid == 102) {
			codeid = 102;
		} else if (cmsuserid == 13) {
			codeid = 107;
		} else if (cmsuserid == 16) {
			codeid = 108;
		}
		json.put("secret", "79391f6897c8b4875f7d2eb6794a7fb5");
		json.put("customerId", user_id);
		json.put("phoneList", array);
		json.put("agent", codeid);

		// Postaliyun postviettel = new Postaliyun();
		String url = "http://apps.worldfone.vn/worldfone4x/api/olava_diallistServices/importDiallistDetail";
		// String param = json.toString();

		String response = PostHttp.getJsonData(json, url);
		com.alibaba.fastjson.JSONObject json1 = com.alibaba.fastjson.JSONObject
				.parseObject(response);
		String code = json1.getString("status");
		String msg = json1.getString("message");
		if ("FAILED".equals(code)) {
			jsonObject.put("error", -1);
			jsonObject.put("msg", msg);
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			/*
			 * postviettel.sendPost(url, param);
			 * System.out.println(postviettel.sendPost(url, param));
			 */
			jsonObject.put("error", 1);
			jsonObject.put("msg", "Thành công");
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}
	
	public String ChangPhone(String phone) {
		String newphone = "";
		if("84".equals(phone.substring(0, 2))){
			phone = "0" + phone.substring(2);
		}
		String changphone = phone.substring(0, 4);
		System.out.println(changphone);
		if ("0120".equals(changphone)) {
			newphone = "070" + phone.substring(4);
		} else if ("0121".equals(changphone)) {
			newphone = "079" + phone.substring(4);
		} else if ("0122".equals(changphone)) {
			newphone = "077" + phone.substring(4);
		} else if ("0126".equals(changphone)) {
			newphone = "076" + phone.substring(4);
		} else if ("0128".equals(changphone)) {
			newphone = "078" + phone.substring(4);
		} else if ("0123".equals(changphone)) {
			newphone = "083" + phone.substring(4);
		} else if ("0124".equals(changphone)) {
			newphone = "084" + phone.substring(4);
		} else if ("0125".equals(changphone)) {
			newphone = "085" + phone.substring(4);
		} else if ("0127".equals(changphone)) {
			newphone = "081" + phone.substring(4);
		} else if ("0129".equals(changphone)) {
			newphone = "082" + phone.substring(4);
		} else if ("0162".equals(changphone)) {
			newphone = "032" + phone.substring(4);
		} else if ("0163".equals(changphone)) {
			newphone = "033" + phone.substring(4);
		} else if ("0164".equals(changphone)) {
			newphone = "034" + phone.substring(4);
		} else if ("0165".equals(changphone)) {
			newphone = "035" + phone.substring(4);
		} else if ("0166".equals(changphone)) {
			newphone = "036" + phone.substring(4);
		} else if ("0167".equals(changphone)) {
			newphone = "037" + phone.substring(4);
		} else if ("0168".equals(changphone)) {
			newphone = "038" + phone.substring(4);
		} else if ("0169".equals(changphone)) {
			newphone = "039" + phone.substring(4);
		} else if ("0186".equals(changphone)) {
			newphone = "056" + phone.substring(4);
		} else if ("0188".equals(changphone)) {
			newphone = "058" + phone.substring(4);
		} else if ("0199".equals(changphone)) {
			newphone = "059" + phone.substring(4);
		} else {
			newphone = phone;
		}
		return newphone;
	}

	public ActionResult doDHGSphone() throws ConnectException {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		String phone = getStrParameter("phone").replace("&nbsp;", "").replace("-", "").trim(); // 借款项目的id
																				// ;
		System.out.println(phone);
		String newphone = ChangPhone(phone);
		int code = getIntParameter("code");
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
		if (cmsuserid == 0) {
			jsonObject.put("error", -1); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		int dhid = jbdcms3Service.getDHID(cmsuserid);
		
		  if (code == 1) { 
			  newphone = "51" + newphone; 
		  } else if (code == 2) { 
			  newphone = "51" + newphone;
		  } else if (code == 3) { 
			  newphone = "53" + newphone;
		  }
		 
		logger.info("进入到拨打电话");
		logger.info("请求ID:" + cmsuserid);
		// Postaliyun postviettel = new Postaliyun();
		/*
		 * String url = "http://mpbx.voicecloud.vn/api/CallControl/dial/from_number/" +
		 * dhid + "/to_number/" + newphone +
		 * "/key/03c324f38b6796c3358bbe49881473ab/domain/providencefinancial.com";
		 */
		
		String url = "http://apps.worldfone.vn/externalcrm/makecall2.php?callernum="+dhid+"&destnum="+newphone+"&secrect=72c44ac5a9911f6e4569f6b8322311ff";
		// String param = json.toString();
		//boolean vphone = SendMsg.isVINAPHONE(newphone);
		logger.info(url);
		String response = PostHttp.sendGet(url);
		logger.info(response);
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		  if ("200".equals(response)) { 
			  if(phone.length()>11) {
				  jsonObject.put("error", -1); 
				  jsonObject.put("msg", "Thất bại" + phone);
			  }else {
				  jsonObject.put("error", 1);
				  jsonObject.put("msg", "Thành công");
			  }
			  this.getWriter().write(jsonObject.toString());
			  return null; 
		  } else {
			  jsonObject.put("error", -1); 
			  jsonObject.put("msg", "Thất bại" + phone); 
			  DataRow row = new DataRow(); 
			  row.set("phone", "SB" + newphone);
			  row.set("cmsuserid", cmsuserid);
			  row.set("createtime", fmtrq.format(new Date()));
			  jbdcms3Service.insertPHONE(row);
			  this.getWriter().write(jsonObject.toString());
			  return null; 
		  }
	}

	public ActionResult doGetChangeCS() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//		if (cmsuserid == 888 || cmsuserid == 8 || cmsuserid == 6|| cmsuserid == 222) {
		if(roleauthoritymangement.getRoleAM_CSlist(cmsuserid+"")){
			jsonObject.put("error", 1); // Vui lòng đăng nhập trước
			jsonObject.put("aa", "催收统计列表");
			jsonObject.put("bb", "筛选条件");
			jsonObject.put("cc", "全部");
			jsonObject.put("dd", "审核人姓名");
			jsonObject.put("ee", "手机号码");
			jsonObject.put("eeeeee", "逾期天数");
			jsonObject.put("ff", "统计时间");
			jsonObject.put("gg", "催收统计列表");
			jsonObject.put("hh", "催收人编号");
			jsonObject.put("ii", "催收人姓名");
			jsonObject.put("jj", "手机号码");
			jsonObject.put("kk", "当天笔数统计");
			jsonObject.put("ll", "总笔数统计");
			jsonObject.put("mm", "总笔数");
			jsonObject.put("nnn", "提醒还款笔数");
			jsonObject.put("nn", "M1（逾期1-3天）笔数");
			jsonObject.put("oo", "M2（逾期3-7天）笔数");
			jsonObject.put("pp", "M3（逾期7-30天）笔数");
			jsonObject.put("qq", "M4（逾期>30天）笔数");
			jsonObject.put("rr", "总笔数");
			jsonObject.put("ss", "M1（逾期1-3天）笔数");
			jsonObject.put("tt", "M1（逾期1-3天）金额");
			jsonObject.put("uu", "M2（逾期3-7天）笔数");
			jsonObject.put("vv", "M2（逾期3-7天）金额");
			jsonObject.put("ww", "M3（逾期7-30天）笔数");
			jsonObject.put("xx", "M3（逾期7-30天）金额");
			jsonObject.put("yy", "M4（逾期>30天）笔数");
			jsonObject.put("zz", "M4（逾期>30天）金额");
			jsonObject.put("aaa", "总催收金额(不包含延期)");
			jsonObject.put("bbb", "总已催收金额");
			jsonObject.put("ccc", "总已催收比例");
			jsonObject.put("hhh", "总催收金额(包含延期)");
			jsonObject.put("hhhh", "总延期笔数");
			jsonObject.put("iii", "已延期金额");
			jsonObject.put("jjj", "延期比例");
			jsonObject.put("kkk", "当月新增逾期笔数");
			jsonObject.put("lll", "当月新增总催收金额");
			jsonObject.put("mmm", "当月已催收金额");
			jsonObject.put("nnn", "当月催回比例");
			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 0); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doGetChangeSH() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//		if (cmsuserid == 888 || cmsuserid == 8 || cmsuserid == 6|| cmsuserid == 222) {
		if(roleauthoritymangement.getRoleAM_SHlist(cmsuserid+"")){
			jsonObject.put("error", 1); // Vui lòng đăng nhập trước
			jsonObject.put("aa", "审核统计列表");
			jsonObject.put("bb", "筛选条件");
			jsonObject.put("cc", "全部");
			jsonObject.put("dd", "审核人姓名");
			jsonObject.put("ee", "手机号码");
			jsonObject.put("fff", "统计时间");
			jsonObject.put("ff", "审核统计列表");
			jsonObject.put("gg", "审核人编号");
			jsonObject.put("hh", "审核人姓名");
			jsonObject.put("ii", "手机号码");
			jsonObject.put("jj", "当天笔数统计");
			jsonObject.put("kk", "总笔数统计");
			jsonObject.put("ll", "客服查询次数");
			jsonObject.put("mm", "总笔数-拒绝");
			jsonObject.put("mmm", "实际总人数");
			jsonObject.put("nn", "一审笔数-拒绝");
			jsonObject.put("nnnn", "一审信息错误笔数");
			jsonObject.put("nnnnnn", "一审拒绝比例");
			jsonObject.put("nnnnnnnn", "借款老用户");
			jsonObject.put("oo", "二审笔数");
			jsonObject.put("pp", "三审笔数");
			jsonObject.put("qq", "总笔数-拒绝");
			jsonObject.put("qqq", "实际总人数");
			jsonObject.put("rr", "一审笔数-拒绝");
			jsonObject.put("rrrr", "一审信息错误笔数");
			jsonObject.put("rrrrrr", "一审拒绝比例");
			jsonObject.put("rrr", "一审通过比例");
			jsonObject.put("ss", "二审笔数-拒绝");
			jsonObject.put("tt", "三审笔数-拒绝");
			jsonObject.put("uu", "三审通过笔数");
			jsonObject.put("uuu", "二审通过笔数-到期应还");
			jsonObject.put("vv", "逾期笔数");
			jsonObject.put("ww", "逾期比例");
			jsonObject.put("xxx", "逾期（未还）笔数-可点击查看");
			jsonObject.put("xx", "逾期（未还）比例");
			jsonObject.put("yy", "总次数");

			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 0); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doGetChangeDHK() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//		if (cmsuserid == 888 || cmsuserid == 8 || cmsuserid == 6|| cmsuserid == 222) {
		if(roleauthoritymangement.getRoleAM_CWlist(cmsuserid+"")){
			jsonObject.put("error", 1); // Vui lòng đăng nhập trước
			jsonObject.put("aa", "待还款列表");
			jsonObject.put("bb", "筛选条件");
			jsonObject.put("cc", "全部");
			jsonObject.put("dd", "用户编号");
			jsonObject.put("ee", "真实姓名");
			jsonObject.put("ff", "身份证号码");
			jsonObject.put("gg", "还款渠道");
			jsonObject.put("hh", "到账时间");
			jsonObject.put("ii", "还款状态");
			jsonObject.put("jj", "请选择");
			jsonObject.put("kk", "成功");
			jsonObject.put("ll", "失败");
			jsonObject.put("mm", "待还款列表");
			jsonObject.put("nn", "用户编号");
			jsonObject.put("oo", "用户姓名");
			jsonObject.put("pp", "身份证号");
			jsonObject.put("qq", "还款渠道");
			jsonObject.put("rr", "未付款");
			jsonObject.put("ss", "还款凭证");
			jsonObject.put("tt", "还款金额");
			jsonObject.put("uu", "逾期利息");
			jsonObject.put("vv", "总金额");
			jsonObject.put("ww", "借款期限");
			jsonObject.put("xx", "到账时间");
			jsonObject.put("yy", "还款时间");
			jsonObject.put("zz", "操作");
			jsonObject.put("aaa", "上传还款凭证时间");
			jsonObject.put("bbb", "停止计息");
			jsonObject.put("ccc", "延期期限");
			jsonObject.put("ddd", "延期还款时间");

			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 0); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doGetChangeHK() throws Exception {
		JSONObject jsonObject = new JSONObject(); // 后台登录账户
	    int cmsuser_id =SessionHelper.getInt("cmsuserid", getSession());
	    cmsuser_id =accessVeritifivationbase.checkCMSidAndip(cmsuser_id, getipAddr());
	    if(cmsuser_id==0){
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Vui lòng đăng nhập trước");
				this.getWriter().write(jsonObject.toString());	
				return null;		
		}
		logger.info("请求ID:" + cmsuser_id);
		
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//		if (cmsuserid == 888 || cmsuserid == 8 || cmsuserid == 6|| cmsuserid == 222) {
		if(roleauthoritymangement.getRoleAM_CWlist(cmsuserid+"")){
			jsonObject.put("error", 1); // Vui lòng đăng nhập trước
			jsonObject.put("aa", "成功还款列表");
			jsonObject.put("bb", "筛选条件");
			jsonObject.put("cc", "全部");
			jsonObject.put("ccc", "全部");
			jsonObject.put("dd", "用户编号");
			jsonObject.put("ddd", "正常用户");
			jsonObject.put("ee", "借款编号");
			jsonObject.put("eee", "逾期用户");
			jsonObject.put("ff", "订单编号");
			jsonObject.put("gg", "手机号码");
			jsonObject.put("ggg", "全额还款");
			jsonObject.put("gggg", "部分还款");
			jsonObject.put("ggggg", "免除剩余逾期利息");
			jsonObject.put("gggggg", "延期还款");
			jsonObject.put("hh", "还款时间");
			jsonObject.put("ii", "成功还款列表");
			jsonObject.put("jj", "总还款金额");
			jsonObject.put("ll", "借款编号");
			jsonObject.put("mm", "用户姓名");
			jsonObject.put("oo", "还款金额");
			jsonObject.put("ooo", "红包金额");
			jsonObject.put("ppp", "还款方式");
			jsonObject.put("qq", "借款期限");
			jsonObject.put("rr", "银行账号");
			jsonObject.put("ss", "用户还款时间");
			jsonObject.put("tt", "逾期天数");
			jsonObject.put("uu", "逾期利息");

			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 0); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}

	public ActionResult doGetChangeDFK() throws Exception {
		JSONObject jsonObject = new JSONObject();
		int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());
//		if (cmsuserid == 888 || cmsuserid == 8 || cmsuserid == 6 || cmsuserid == 222) {
		if(roleauthoritymangement.getRoleAM_CWlist(cmsuserid+"")){
			jsonObject.put("error", 1); // Vui lòng đăng nhập trước
			jsonObject.put("aa", "待放款列表");
			jsonObject.put("bb", "筛选条件");
			jsonObject.put("cc", "全部");
			jsonObject.put("dd", "用户编号");
			jsonObject.put("ee", "真实姓名");
			jsonObject.put("ff", "手机号码");
			jsonObject.put("gg", "身份证号码");
			jsonObject.put("hh", "审核时间");
			jsonObject.put("jj", "待放款列表");
			jsonObject.put("kk", "用户编号");
			jsonObject.put("ll", "真实姓名");
			jsonObject.put("mm", "手机号码");
			jsonObject.put("nn", "身份证号码");
			jsonObject.put("oo", "银行账号");
			jsonObject.put("pp", "银行名称");
			jsonObject.put("qq", "可借金额");
			jsonObject.put("rr", "实际到账金额");
			jsonObject.put("ss", "借款期限");
			jsonObject.put("tt", "三审时间");
			jsonObject.put("uu", "三审人");
			jsonObject.put("vv", "操作");
			jsonObject.put("ww", "失败");

			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			jsonObject.put("error", 0); // Vui lòng đăng nhập trước
			this.getWriter().write(jsonObject.toString());
			return null;
		}
	}
	public void test3(){ 
		   
		  String tempStr = ""; 
		  String str2 = Integer.toBinaryString(10); 
		    //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0 
		    int bit = 8-str2.length(); 
		    if(str2.length()<8){ 
		      for(int j=0; j<bit; j++){ 
		    	  str2 = "0"+str2; 
		      } 
		    } 
		    tempStr += str2; 
		    System.out.println(tempStr); 
		} 	
	public static String GetGUID()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}
	 public static void main(String[] args) throws Exception{
		 String tokentoken = "AQAZSqRL4WD8GxAHmftzfSrZim3Ww-FvItwtqb5CLhz87hxjr-cDIcx4gBuAqKPwGAGq6WOPISUsZqyD2RJzqVQ5AXKI4iXTxbh2BvNFDl0SzEB77hqR0LnQEbQn2EQeS4AjAz9mTGInYPDIurxFzZiRzI5h9VG9DZ9CNuQhUtW0X1Lq73994kLhoQ6d02GMKpn0fehMwmRL4l-x_jeOitpowPZmYkaL_9aL9nBrPQRFwjQRCCVQqG6g7A0cfBOMT-jdFw7ply9ismsYhxP1N6k0";
		 String url = "https://graph.accountkit.com/v1.3/access_token?grant_type=authorization_code&"
					+"code="+tokentoken+"&access_token=AA|436349787125595|d1876a12a25b4abb24787ceb73524f65";
			
		    StringBuilder json = new StringBuilder();
		    
		    try {
		        URL getUrl = new URL(url);
		        // 返回URLConnection子类的对象
		        
		        HttpsURLConnection connection = (HttpsURLConnection) getUrl.openConnection();
		        // 连接
		        connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
		        connection.connect();
		        InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
		        // 使用Reader读取输入流
		        BufferedReader reader = new BufferedReader(inputStreamReader);
		        String lines;
		        while ((lines = reader.readLine()) != null) {
		            json.append(lines);
		        }
		        reader.close();
		        // 断开连接
		        connection.disconnect();
		    } catch (IOException ioException) {
		        ioException.printStackTrace();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		   System.out.println(json.toString());
		   com.alibaba.fastjson.JSONObject jsonString = com.alibaba.fastjson.JSONObject.parseObject(json.toString());
		   String accesstoken = jsonString.getString("access_token");
		   System.out.println(accesstoken);
	 }
	 
	// xiong-ajax动态催收信息-20190711
		public ActionResult doGetCuishouAj() throws Exception {

			logger.info("进入查看催动态获取催收人员信息");
			// 借款项目的id ;
			String recid = getStrParameter("recid");
			// 根据项目的id 返回审核项目的信息
			DataRow jkInfo = jbdcms3Service.getCuishouidRoleid(recid);
			
			JSONObject jsonObject = new JSONObject();
			
			if (jkInfo != null) {
				jsonObject.put("error", 1);
				jsonObject.put("jkinfo", jkInfo);
			} else {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "没有此用户！联系技术");
			}
				// 获取原来订单的催收人员id
				int roleid = jkInfo.getInt("roleid");
				int csid = jkInfo.getInt("cuishou_id");
				int yuq_ts = jkInfo.getInt("yuq_ts");
				
				List<DataRow> list = new ArrayList<DataRow>();
				// 通过角色ID查找
//				if(roleid == 19 || roleid == 20) {
//					list = jbdcms3Service.selectUserLeaverList(csid, 4);
//				}else if (roleid == 50 || roleid == 21) {
//					list = jbdcms3Service.selectUserLeaverList(csid, 1);
//				} else if (roleid == 51 || roleid == 24) {
//					list = jbdcms3Service.selectUserLeaverList(csid, 2);
//				} else if (roleid == 26 ||roleid == 54 || roleid == 60 ||roleid == 61) {
//					list = jbdcms3Service.selectUserLeaverList(csid, 3);
//				}
				
				if(yuq_ts>0 && yuq_ts <4) {
					list = jbdcms3Service.selectUserLeaverList(csid, 4);
				}else if (yuq_ts>3 && yuq_ts <16) {
					list = jbdcms3Service.selectUserLeaverList(csid, 1);
				} else if (yuq_ts>15 && yuq_ts <46) {
					list = jbdcms3Service.selectUserLeaverList(csid, 2);
				} else if (yuq_ts>45) {
					list = jbdcms3Service.selectUserLeaverList(csid, 3);
				}else {
					list = jbdcms3Service.selectUserLeaverList(csid, -1);
				}
				
				List<TreeMap> list2 = new ArrayList<TreeMap>();
				//TreeMap<String, String> map = new TreeMap<String, String>();
				for (int i = 0; i < list.size(); i++) {
				TreeMap<String, String> map = new TreeMap<String, String>();
					DataRow dr = list.get(i);
					String cuishouid = dr.getString("user_id");
					String name = dr.getString("name");
					map.put("cuishouid", cuishouid);
					map.put("name", name);
					list2.add(map);
				}
										
				jsonObject.put("csidlist", list2);
							
			this.getWriter().write(jsonObject.toString());
			return null;
		}
		
		// xiong-ajax动态催-审核人员-20190816
		public ActionResult doGetShenheAj() throws Exception {
			
			logger.info("进入查看审核信息");
			JSONObject jsonObject = new JSONObject();
			int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());		
//			if ( cmsuserid != 8 && cmsuserid != 888 && cmsuserid != 6 && cmsuserid != 222 && cmsuserid !=9999 && cmsuserid !=8888 && cmsuserid !=2038) {
			if(!roleauthoritymangement.getRoleAM_SHlist(cmsuserid+"")){
				jsonObject.put("error", -3);
				jsonObject.put("msg", "Quyền hạn của chủ quản！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}	
			List<DataRow>  list = jbdcms3Service.getShenheFdqxList();
			
			String recid = getStrParameter("recid"); // 借款项目的id ;
			
			// 根据项目的id 返回审核项目的信息
			DataRow jkInfo = jbdcms3Service.getShenheid(recid);
			if (jkInfo.getInt("cl_status") != 0) {
				jsonObject.put("error", -1);
				jsonObject.put("msg", "Khách hàng này đã được thẩm định！");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
			if (jkInfo != null) {
				jsonObject.put("error", 1);
				jsonObject.put("jkinfo", jkInfo);
				jsonObject.put("shlist",list);
			} else {
				jsonObject.put("error", -2);
				jsonObject.put("msg", "Lỗi hệ thống, vui lòng liên hệ kỹ thuật！");
				this.getWriter().write(jsonObject.toString());
				return null;

			}
									
			this.getWriter().write(jsonObject.toString());
			return null;
		}
			
	}

