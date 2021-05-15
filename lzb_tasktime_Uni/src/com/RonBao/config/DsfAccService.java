package com.RonBao.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import sun.misc.BASE64Decoder;

public class DsfAccService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
	@SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String _input_charset = DsfConfig.input_charset;
        String batchBizid = DsfConfig.partner;
        String batchVersion = DsfConfig.batchVersion;
        String batchDate = sdf.format(new Date());
        String signType = DsfConfig.sign_type;
        String key = DsfConfig.key;
        Map sPara = new HashMap();
        sPara.put("batchBizid", batchBizid);
        sPara.put("batchVersion", batchVersion);
        sPara.put("batchDate", batchDate);
        String url = DsfConfig.dsfacc;

        sPara.put("_input_charset", _input_charset);
        String interAddrPara = DsfFunction.CreateLinkString(sPara);
        url = url + interAddrPara;
        Map sParaNew = DsfFunction.ParaFilter(sPara); // 除去数组中的空值和签名参数
        String sign = DsfFunction.BuildMysign(sParaNew, key);// 生成签名结果

        httpClient(url, sign, signType, request, response);
    }

    /**
     * 后台发送接收
     *
     * @param url
     * @param sign
     * @param signType
     * @param request
     * @param res
     */
    public static void httpClient(String url, String sign, String signType,
                                  HttpServletRequest request, HttpServletResponse res) {

        HttpClient client = new HttpClient();
        BASE64Decoder decoder = new BASE64Decoder();
        String ht = url + "&signType=" + signType + "&sign=" + sign;
        HttpMethod method = new GetMethod(ht);
        String response = "";
        try {
            client.executeMethod(method);
            response = method.getResponseBodyAsString();
            Document doc = DocumentHelper.parseText(response);
            Element root = doc.getRootElement();
            String restatus = root.elementText("status");
            String error = root.elementText("reason");
            res.setContentType("text/html; charset=gbk"); // 转码
            PrintWriter out = res.getWriter();
            out.flush();
            out.println("<script>");
            out.println("alert('" + error + "！');");
            out.println("history.back();");
            out.println("</script>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }
}
