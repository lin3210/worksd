package root.current;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.project.service.account.JBDcmsService;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.base.util.StringHelper;
import com.thinkive.web.base.BaseAction;

public class AccessVerifivationBase extends BaseAction{
	
	private static Logger logger = Logger.getLogger(AccessVerifivationBase.class);
	private static JBDcmsService jbdcmsService = new JBDcmsService();
	
	/**
	 * ���ʽӿ��ж� 
	 * @param cmsid   ��̨id
	 * @param cmsip
	 * @return
	 */
	public int checkCMSidAndip(int cmsid,String cmsip) {
		int  return_id=0;
	    
		//IP �ж�
//		if (StringHelper.isEmpty(cmsip)) {
//			String ip =  getipAddr();
//			logger.info("����IP��"+ip);
//		}
		int state =jbdcmsService.getCMSuserState(cmsid+"");
		if(1==state){
			return_id = cmsid;
		}
		if(cmsid != 0) {
			String online =cmsip;
			System.out.println(cmsip);
			String sqlonline = jbdcmsService.getCMSuserOnline(cmsid+"");
			if(!StringHelper.isEmpty(online) && !StringHelper.isEmpty(sqlonline) ){
				if(!online.equals(sqlonline)) {
					return_id =0;
				}
			}
		}
		
		return return_id;	
	}
	
	/**
	 * ��ÿͻ�����ʵIP��ַ
	 * 
	 * @return
	 */
	public String getipAddr() {
		String ip = getRequest().getHeader("X-Real-IP");
		if (StringHelper.isEmpty(ip)) {
			ip = getRemortIP(getRequest());
		}
		return ip;
	}


	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {  
			return request.getRemoteAddr();  
		}  
		   return request.getHeader("x-forwarded-for");  
	}
}
