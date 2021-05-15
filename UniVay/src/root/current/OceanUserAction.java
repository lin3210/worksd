package root.current;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import root.SendFTP;
import root.SendMsg;
import root.img.WebConstants;
import root.order.UserMoneyBase;

import com.alibaba.fastjson.JSON;
import com.project.constant.IConstants;
import com.project.service.account.JBDcmsService;
import com.project.service.account.MofaUserService;

import com.project.utils.MemCachedUtil;
import com.shove.security.Encrypt;
import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.CookieHelper;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

public class OceanUserAction extends BaseAction {
	private static Logger logger = Logger.getLogger(OceanUserAction.class);
	private static UserMoneyBase  userMoneyBase = new UserMoneyBase();
	/* private static UserService userService = new UserService(); */
	private static MofaUserService mofaUserService = new MofaUserService();
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	String jiami = "GKHJD83df21IKkjdefu82ik82VSh";
	long time = 1000 * 60 * 3;

	@Override
	public ActionResult doDefault() throws Exception {
		JSONObject jsonObject = new JSONObject();
		int userid;
		String phone = "";
		int p = getIntParameter("type");
		if (p == 1) {
			userid = getIntParameter("userid");
		} else {
			userid = SessionHelper.getInt("userid", getSession());
//			phone = SessionHelper.getString("mobilephone", getSession());
		}
		logger.info("AuthAction~~~~" + "userid~~~~" + userid + ",phone~~~"
				+ SessionHelper.getString("mobilephone", getSession()) + ",username~~~"
				+ SessionHelper.getString("username", getSession()));
		if (userid > 0) {
			// 鑾峰彇韬唤璇佷俊鎭�
			jsonObject.put("error", 0);
			DataRow drIdCard = mofaUserService.findUserFinance(userid + "");

			List<DataRow> bank = mofaUserService.findBankList();
			JSONArray jsonArray = new JSONArray();
			for (Object object : bank) {
				jsonArray.add(JSONObject.fromBean(object));
			}

			jsonObject.put("banklist", jsonArray);
			if (drIdCard != null) {
				jsonObject.put("realname", drIdCard.getString("realname"));// 鐪熷疄濮撳悕
				jsonObject.put("idno", drIdCard.getString("idno"));// 韬唤璇佸彿
				jsonObject.put("status", drIdCard.getString("status"));// 鐘舵��
				phone = drIdCard.getString("cellphone");
				if (StringHelper.isNotEmpty(phone)) {

					jsonObject.put("mobilephone", phone);// 鎵嬫満
				} else {

					jsonObject.put("mobilephone", "");// 鎵嬫満
				}

			} else {
				jsonObject.put("realname", "");// 鐪熷疄濮撳悕
				jsonObject.put("idno", "");// 韬唤璇佸彿
				jsonObject.put("status", 0);// 鐘舵��
				jsonObject.put("mobilephone", "");// 鎵嬫満
			}
			// 鑾峰彇閾惰淇℃伅
			DataRow drBankcard = mofaUserService.findUserBankcard(userid + "");
			if (drBankcard != null) {

				jsonObject.put("bankname", drBankcard.getString("cardname"));// 寮�鎴疯
				jsonObject.put("cardno", drBankcard.getString("cardno").substring(
						drBankcard.getString("cardno").length() - 4, drBankcard.getString("cardno").length()));// 閾惰鍗″彿
				jsonObject.put("cardstatus", drBankcard.getString("cardstatus"));// 鐘舵��
			} else {
				jsonObject.put("bankname", "");// 寮�鎴疯
				jsonObject.put("cardno", "");// 閾惰鍗″彿
				jsonObject.put("cardstatus", 0);// 鐘舵��
			}
		} else {
			jsonObject.put("error", -1);
			jsonObject.put("username", "");
		}
		System.out.println(jsonObject);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	/**
	 * 鑾峰緱瀹㈡埛鐨勭湡瀹濱P鍦板潃
	 *
	 * @return
	 */
	public String getipAddr() {
		String ip = getRequest().getHeader("X-Real-IP");
		if (StringHelper.isEmpty(ip)) {
			ip = mofaUserService.getRemortIP(getRequest());
		}
		return ip;
	}

	/**
	 * 璁剧疆杩斿洖娑堟伅
	 * 
	 * @param error
	 */
	public void setError(DataRow error) {
		JSONObject object = JSONObject.fromBean(error);
		this.getWriter().write(object.toString());
	}
	public ActionResult doMofaHomedateFD() {
		logger.info("璇锋眰ip" + getipAddr());
		String miwen = getStrParameter("token");

		int id = getIntParameter("jkld", 0);

		String jiamiwen = Encrypt.MD5(id + jiami);

		if (jiamiwen.equals(miwen)) {

			String userName = getStrParameter("userid").trim().replaceAll(" ", "");
			logger.info("鐢ㄦ埛璇锋眰鎵嬫満鍙�" + userName);
			DataRow row = new DataRow();
			if (id != 0) {
				DataRow data = mofaUserService.findUserById(id + "");
				DataRow data1 = mofaUserService.findUserZPById(id + "");
				DataRow data4 = mofaUserService.findUserHKQD(id + "");
				DataRow rz = mofaUserService.getRZBK(id);
				DataRow rzsf = mofaUserService.getRZSF(id);
				long date111 = System.currentTimeMillis();
				String date1111 = date111 + "";
				SimpleDateFormat sdfsd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// 鑾峰彇鐢ㄦ埛鐨勫�熸淇℃伅
				DataRow dataJK = mofaUserService.findUserJKByuserid(id + "");
				DataRow datarz = mofaUserService.findUserRZuserid(id + ""); // 璁よ瘉鏃堕棿
				if (datarz != null) {
					String rzdatesj = datarz.getString("create_time");
					long rzdatesjsj = 0;
					try {
						rzdatesjsj = sdfsd.parse(rzdatesj).getTime();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					long chazhi = date111 - rzdatesjsj;
					long jiben = 15 * 24 * 60 * 60 * 1000L;
					if (chazhi >= jiben) {
						// row.set("retake", 1); 1涓洪噸鏂拌幏鍙�
						row.set("retake", 1);
						row.set("retaketime", rzdatesjsj);
					} else {
						row.set("retake", 0);
						row.set("retaketime", rzdatesjsj);
					}
				} else {
					row.set("retake", -1);
					row.set("retaketime", 0);
				}
				// 鍒ゆ柇鏄惁鏈夋湭璇绘秷鎭�
				int wdXiaoXi = mofaUserService.getWdXiaoXiaoCount(id);

				if (data1 != null) {
					row.set("p1", data1.getString("p1"));
					row.set("p2", data1.getString("p2"));
					row.set("p3", data1.getString("p3"));
				} else {
					row.set("p1", "");
					row.set("p2", "");
					row.set("p3", "");
				}

				if (data != null) {
					String username = data.getString("username");
					String phone = data.getString("mobilephone");
					String lastdate = data.getString("lastdate");
					phone = phone.substring(0, 5) + "***" + phone.substring(9, phone.length());
					row.set("username", username);
					row.set("lastdate", lastdate);
					row.set("logincount", data.getInt("logincount"));

					row.set("shownofition", data.getString("shownofition"));
					row.set("showtitle", data.getString("showtitle"));

					DataRow rowlogin = new DataRow();
					rowlogin.set("id", id);
					rowlogin.set("shownofition", "0");
					rowlogin.set("logincount", data.getInt("logincount") + 1);
					mofaUserService.updateUser(id + "", rowlogin);

					row.set("phone", phone);

					row.set("mobilephone", data.getString("mobilephone"));
					row.set("creditlimit", data.getString("creditlimit"));
					row.set("isyhbd", data.getString("yhbd"));
					row.set("isshenfen", data.getString("isshenfen"));
					row.set("isjob", data.getString("isjop"));
					row.set("isschool", data.getString("isschool"));
					row.set("islianxi", data.getString("islianxi"));
					row.set("isfacebook", data.getString("isfacebook"));
					row.set("profession", data.getString("profession"));
					if (data4 != null) {
						row.set("hkqd", data4.getString("hkqd"));
					} else {
						row.set("hkqd", "0");
					}
					row.set("day15", "0.1955");
					row.set("day30", "0.291");
					row.set("TSdayJE", "1");// 100涓�
					row.set("TSday15", "0.2955");
					row.set("TSday30", "0.291");

					String rzstatus = "0";
					if (rz != null) {
						row.set("rzbankname", rz.getString("cardusername"));
						rzstatus = rz.getString("status");
						if (rz.getString("cardno").length() > 9) {
							row.set("rzcard", "********" + rz.getString("cardno")
									.substring(rz.getString("cardno").length() - 4, rz.getString("cardno").length()));

						} else {
							row.set("rzcard", "********" + rz.getString("cardno")
									.substring(rz.getString("cardno").length() - 4, rz.getString("cardno").length()));

						}

					} else {

						row.set("rzname", "");
						row.set("rzcard", "");
						row.set("rzbankname", "");

					}
					if (rzsf != null) {
						row.set("rzname", rzsf.getString("realname"));
						row.set("rzcard2", rzsf.getString("idno"));
						row.set("sfemail", rzsf.getString("email"));
						row.set("sfage", rzsf.getString("age"));
						row.set("sfsex", rzsf.getString("sex"));
						row.set("sfhomeaddress", rzsf.getString("homeaddress"));
						row.set("sfaddress", rzsf.getString("address"));
						row.set("sfphonetype", rzsf.getString("phonetype"));
					} else {
						row.set("rzname", "");
						row.set("rzcard2", "");
						row.set("sfemail", "");
						row.set("sfage", "");
						row.set("sfsex", "");
						row.set("sfhomeaddress", "");
						row.set("sfaddress", "");
						row.set("sfphonetype", "");
					}
					row.set("rzstatus", rzstatus);
					if (wdXiaoXi > 0) {
						row.set("wdXiaoXi", 1);
					} else {
						row.set("wdXiaoXi", 0);
					}
				}
				if (dataJK != null) {
					String newdateSH = dataJK.getString("sjsh_money").replace(",", "");

					row.set("dataSH", Integer.parseInt(newdateSH));

				} else {
					row.set("dataSH", 0);
				}
				row.set("dataJK", dataJK);
				row.set("date1", date1111);

			}

			JSONObject jsonObject = JSONObject.fromBean(row);

			this.getWriter().write(jsonObject.toString());
			return null;
		} else {
			return null;
		}

	}
	private com.alibaba.fastjson.JSONObject getRequestJson(HttpServletRequest request) throws IOException {
		InputStream in = request.getInputStream();
		byte[] b = new byte[10240];
		int len;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = in.read(b)) > 0) {
			baos.write(b, 0, len);
		}
		String bodyText1 = new String(baos.toByteArray(), "UTF-8");
		bodyText1 = bodyText1.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		String bodyText = URLDecoder.decode(bodyText1, "UTF-8");
		com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.parse(bodyText);
		if (true) {
			logger.info("浼犺緭鎴愬姛锛�");
		}
		return json;
	}

	/**
	 * 鍚戞寚瀹歎RL鍙戦�丟ET鏂规硶鐨勮姹�
	 * 
	 * @param url   鍙戦�佽姹傜殑URL
	 * @param param 璇锋眰鍙傛暟锛岃姹傚弬鏁板簲璇ユ槸name1=value1&name2=value2鐨勫舰寮忋��
	 * @return URL鎵�浠ｈ〃杩滅▼璧勬簮鐨勫搷搴�
	 */

	public static String sendGet(String lat, String lng) {
		String url = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyA-w4Gq2lAEe15VHVK5y7T4JZEi5PgDMqY&latlng="
				+ lat + "," + lng + "&language=EN&sensor=false";
		JSONObject jsonObject = null;
		StringBuilder json = new StringBuilder();
		String nonce = null;
		try {
			URL getUrl = new URL(url);
			// 杩斿洖URLConnection瀛愮被鐨勫璞�
			HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
			// 杩炴帴
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			// 浣跨敤Reader璇诲彇杈撳叆娴�
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String lines;
			while ((lines = reader.readLine()) != null) {
				json.append(lines);
			}
			reader.close();
			// 鏂紑杩炴帴
			connection.disconnect();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public ActionResult doGCLPV() {
		JSONObject jsonObject = new JSONObject();
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String time = fmtrq.format(new Date());
		String nowtime = time.substring(11, 13);
		logger.info("杩涘叆鐨勭紪鍙�:" + getIntParameter("czybh"));
		logger.info("鎵撳紑鐨勬椂闂�:" + nowtime);
		String timecode = "0";
		if ((Integer.parseInt(nowtime) > 7 && Integer.parseInt(nowtime) < 20)) {
			timecode = "1";
		}
		jsonObject.put("changecode", "1");
		jsonObject.put("timecode", timecode);
		this.getWriter().write(jsonObject.toString());
		return null;
	}

	// 鏇存柊涓婁紶鐓х墖
	public ActionResult doCPLD() throws Exception {
		logger.info("璇锋眰ip" + getipAddr());
		int userid = getIntParameter("jykd");
		int cmsuserid = getIntParameter("czybh");
		JSONObject jsonObject = new JSONObject();
		String miwen = getStrParameter("hvrng");
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String jiamiwen = Encrypt.MD5(userid + "wud" + jiami);
		if (jiamiwen.equals(miwen)) {
			if (("1234".equals(getStrParameter("qrm")) && cmsuserid == 2001)
					|| ("123456".equals(getStrParameter("qrm")) && cmsuserid == 9)) {
				String p1 = mofaUserService.getUserpicP1(userid);
				String p2 = mofaUserService.getUserpicP2(userid);
				String p3 = mofaUserService.getUserpicP3(userid);

				String ggp1 = getStrParameter("ggp1");
				String ggp2 = getStrParameter("ggp2");
				String ggp3 = getStrParameter("ggp3");

				DataRow row = new DataRow();
				row.set("userid", userid);
				row.set("p1", p1);
				row.set("p2", p2);
				row.set("p3", p3);
				row.set("ggp1", ggp1);
				row.set("ggp2", ggp2);
				row.set("ggp3", ggp3);
				row.set("cmsuserid", cmsuserid);
				row.set("createtime", fmtrq.format(new Date()));
				mofaUserService.insertChangePictureLoad(row);
				String ui = mofaUserService.getUI(userid+"");
				if("".equals(ui)) {
					DataRow row5 = new DataRow();
					row5.set("userid", userid);
					row5.set("p1", ggp1);
					row5.set("p2", ggp2);
					row5.set("p3", ggp3);
					row5.set("create_time", fmtrq.format(new Date()));
					mofaUserService.addUserZhaoPian(row5);
				}else {
					DataRow row1 = new DataRow();
					row1.set("userid", userid);
					row1.set("p1", ggp1);
					row1.set("p2", ggp2);
					row1.set("p3", ggp3);
					row1.set("create_time", fmtrq.format(new Date()));
					mofaUserService.updateUserZhaoPian(row1);
				}
				jsonObject.put("error", 1);
				jsonObject.put("msg", "Th脿nh c么ng");
				this.getWriter().write(jsonObject.toString());
				return null;
			} else {
				jsonObject.put("error", -2);
				jsonObject.put("msg", "Sai m茫 x谩c nh岷璶锛�");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		} else {
			return null;
		}

	}

	// 鏇存柊涓婁紶瑙嗛
	public ActionResult doCVLD() throws Exception {
		logger.info("璇锋眰ip" + getipAddr());
		int userid = getIntParameter("jykd");
		int cmsuserid = getIntParameter("czybh");
		JSONObject jsonObject = new JSONObject();
		String miwen = getStrParameter("hvrng");
		SimpleDateFormat fmtrq = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String jiamiwen = Encrypt.MD5(userid + "wud" + jiami);

		if (jiamiwen.equals(miwen)) {
			if (("1234".equals(getStrParameter("qrm")) && cmsuserid == 2001)
					|| ("123456".equals(getStrParameter("qrm")) && cmsuserid == 9)) {
				int jkid = getIntParameter("jkid");
				String spzt = mofaUserService.getUserpicVideoZT(userid, jkid);
				if (TextUtils.isEmpty(spzt)) {
					jsonObject.put("error", -1);
					jsonObject.put("msg", "Kh么ng c贸 KH vay n脿y !");
					this.getWriter().write(jsonObject.toString());
					return null;
				}
				String video = mofaUserService.getUserpicVideo(userid, jkid);

				String ggvideo = getStrParameter("ggvideo");

				DataRow row = new DataRow();
				row.set("video", video);
				row.set("ggvideo", ggvideo);
				row.set("cmsuserid", cmsuserid);
				row.set("createtime", fmtrq.format(new Date()));
				mofaUserService.insertChangePictureLoad(row);

				DataRow row1 = new DataRow();
				row1.set("id", jkid);
				row1.set("spdz", ggvideo);
				row1.set("spzt", 1);
				row1.set("spsj", fmtrq.format(new Date()));
				mofaUserService.updateUserLoadVideo(row1);

				jsonObject.put("error", 1);
				jsonObject.put("msg", "Th脿nh c么ng");
				this.getWriter().write(jsonObject.toString());
				return null;
			} else {
				jsonObject.put("error", -2);
				jsonObject.put("msg", "Sai m茫 x谩c nh岷璶锛�");
				this.getWriter().write(jsonObject.toString());
				return null;
			}
		} else {
			return null;
		}

	}
}
