package com.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lianpay.api.util.TraderRSAUtil;
import com.lianpay.constant.PaymentConstant;

public class SignUtil {

	private static Logger logger = LoggerFactory.getLogger(SignUtil.class);


	public static String genRSASign(JSONObject reqObj) {
		// 生成待签名串
		String sign_src = genSignData(reqObj);		
		logger.info("商户[" + reqObj.getString("oid_partner") + "]待签名原串" + sign_src);
		return TraderRSAUtil.sign("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALPoMsWGg5gIyk/z"+
				"Wa4wcMok3Z4fW0A1ZBnPLY3wmJ7FCzyxoTGpCIvWXtT4A73HPiZN7Qzqj8HxM0lB"+
				"/jyy+WHtuyjSwG2cKO+IGTGI8LAV58gnV0M9PrzOBOKE6lzQF7a3L5R9u4qS7NHH"+
				"xGTAbYAdffzyI479LawQgFE37BVBAgMBAAECgYBPOhlpzUQUZwKZVOSQhjqVesiy"+
				"AsMPsrODfi5kjKjZepLpRpxjHzppQp1+kj4rjBu9iKG1B3MJiKv6Pfq1Rmf1z5B0"+
				"g+ycsuvcu8WFGVmIPdNhsiybf48aKf+1JB1A1+zFXcrsiLNGd475PCQNMcfVvq5u"+
				"6E+ir2yC6tPYY4U8hQJBAOrFoiT8Y9+go6QOsxihOum/tQ51fC4IwWoWC0tXKhxt"+
				"GZI/SgAiljqCKst+kDq6wTlyE84dxZ9Oi+jk+NTtOwMCQQDELJUEs1SBtbSfkge7"+
				"Nnlowqq+kFpa10T+WoyTYZsOlqLDT80kRLRrhxwB9v+kLPZOfrqcRQ6bLhNKQWsS"+
				"oHlrAkAP3a1YjIn/We7VLn0iA/tkQqVsxbnPrp3LmpPG0qww4ZqhzI8mtS+r4pIb"+
				"0IDUxzw5sqDuBAsP+hHwelDqquGbAkAUnv8XHGawr9IJyAbqBgLjITtjhrcIv4Iw"+
				"HoKSZ3suIGWBlFzjCBnTB8PI7RbYQiWuAKJLFPNBGqnKb2/66EV7AkBxlcWYW0+8"+
				"dqwun9gtueYYtnQWD/+aKD81KK2/MttY9+KVK8f3ksw8D0cGy85AoyzrivQEWNdd"+
				"4Pvqll9NWYhS", sign_src);
	}

	/**
	 * 生成待签名串
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String genSignData(JSONObject jsonObject) {
		StringBuffer content = new StringBuffer();

		// 按照key做首字母升序排列
		List<String> keys = new ArrayList<String>(jsonObject.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			// sign 和ip_client 不参与签名
			if ("sign".equals(key)) {
				continue;
			}
			String value = jsonObject.getString(key);
			// 空串不参与签名
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			content.append((i == 0 ? "" : "&") + key + "=" + value);

		}
		String signSrc = content.toString();
		if (signSrc.startsWith("&")) {
			signSrc = signSrc.replaceFirst("&", "");
		}
		return signSrc;
	}

}
