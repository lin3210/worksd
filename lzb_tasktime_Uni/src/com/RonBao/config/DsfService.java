package com.RonBao.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;

/**
 * @ClassName: DsfService
 * @Description: 出金查询
 * @author Eric wangdong@reapal.com
 * @date 2014-6-5 下午1:37:04
 */
public class DsfService extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	@SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String _input_charset = DsfConfig.input_charset;
        String batchBizid = DsfConfig.partner;
        String batchVersion = DsfConfig.batchVersion;
        String batchDate = request.getParameter("batchDate");// sdf.format(new
        // Date());
        String signType = DsfConfig.sign_type;
        String key = DsfConfig.key;
        String batchCurrnum = request.getParameter("batchCurrnum");
        String tradenum = request.getParameter("tradenum");// 明细号
        String qryBatch = request.getParameter("qryBatch");
        Map sPara = new HashMap();
        sPara.put("batchBizid", batchBizid);
        sPara.put("batchVersion", batchVersion);
        sPara.put("batchDate", batchDate);
        sPara.put("batchCurrnum", batchCurrnum);
        String url = "";
        if (StringUtils.isNotBlank(tradenum)) {
            sPara.put("tradenum", tradenum);// 明细查询
            url = DsfConfig.addrsingle;
        } else {
            url = DsfConfig.addr;
        }

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
			if(response.indexOf("status")!=-1){   //失败
				Document doc = DocumentHelper.parseText(response);
				Element root = doc.getRootElement();
				String restatus = root.elementText("status");
				String error = root.elementText("reason");
				System.out.println(error);
				String showError = new String(error.getBytes("UTF-8"),"gbk");  //如果在控制台看到的返回码有问题请用此方法编码
			
				if (restatus != null || (restatus.equals("fail"))) {
					res.setContentType("text/html; charset=gbk"); 
					PrintWriter out = res.getWriter();
					out.flush();
					out.println("<script>");
					out.println("alert('" + error + "！');");
					out.println("history.back();");
					out.println("</script>");
				}
			}else{                           //成功
				byte[] re;
				try {
					re = decoder.decodeBuffer(response);
					String pa = request.getSession().getServletContext()
							.getRealPath("/");
					response = DsfFunction.jim(re, pa);
					show(response, request, res);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			
		} finally {
			method.releaseConnection();
		}
	}

    /**
     * @Title: anly
     * @author Eric wangdong@reapal.com
     * @Description: 生成文本文件
     * @param @param response
     * @param @param request
     * @param @param res
     * @param @throws Exception
     * @return void
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static void anly(String response, HttpServletRequest request,
                            HttpServletResponse res) throws Exception {
        // TODO Auto-generated method stub
        try {
            Document doc = DocumentHelper.parseText(response);
            Element root = doc.getRootElement();
            List attrList = root.elements();
            String file = "";
            Element bo = (Element) attrList.get(5);
            String merchartNo = root.elementText("batchBizid");
            String version = root.elementText("batchVersion");
            String date = root.elementText("batchDate");
            String pcno = root.elementText("batchCurrnum");
            String batchContent = "";
            String detailInfo = root.elementText("detailInfo");

            StringBuffer sb = new StringBuffer();
            if (detailInfo != null) {
                sb.append(detailInfo);
            } else {
                for (int i = 0; i < bo.elements().size(); i++) {
                    Element item = (Element) bo.elements().get(i);
                    sb.append(item.getData() + "|");
                }
                batchContent = sb.toString();
            }

            String inputCharset = root.elementText("_input_charset");
            String key = DsfConfig.key;
            String signR = root.elementText("sign");
            String signTypeR = root.elementText("signType");
            Map map = new HashMap();
            map.put("batchBizid", merchartNo);
            map.put("batchVersion", version);
            map.put("batchDate", date);
            map.put("batchCurrnum", pcno);
            if (!"".equals(batchContent)) {
                map.put("batchContent", batchContent);
            } else {
                map.put("detailInfo", detailInfo);
            }
            map.put("_input_charset", inputCharset);
            Map mapNew = DsfFunction.ParaFilter(map); // 除去数组中的空值和签名参数
            String mySign = DsfFunction.BuildMysign(mapNew, key);// 生成签名结果
            if (!signR.equals(mySign)) {

                res.getWriter().print("fail,sign is diffrent");
                // throw new Exception("验筌错误！");
            } else {
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmm");
                String pa = request.getSession().getServletContext()
                        .getRealPath("/");
                File file1 = new File(pa + "/upload/");
                file1.mkdirs();
                file = file1 + "\\" + sd.format(new Date()) + "F.txt";
                PrintWriter pw = new PrintWriter(new BufferedWriter(
                        new FileWriter(file)));
                if (detailInfo != null) {
                    pw.print(detailInfo);

                } else {
                    pw.print("_input_charset:" + inputCharset + "\r\n");
                    pw.print("batchBizid:" + merchartNo + "\r\n");
                    pw.print("batchVersion:" + version + "\r\n");
                    pw.print("batchDate:" + date + "\r\n");
                    pw.print("batchCurrnum:" + pcno + "\r\n");
                    pw.print("signType:" + signTypeR + "\r\n");
                    pw.print("sign:" + signR + "\r\n");
                    pw.print("明细如下:\r\n");
                    if (batchContent != null) {
                        for (int i = 0; i < batchContent.split("\\|").length; i++) {
                            pw.print(batchContent.split("\\|")[i] + "\r\n");
                        }
                    }

                }
                pw.close();
                res.setContentType("text/html; charset=gbk"); // 转码
                PrintWriter out = res.getWriter();
                out.flush();
                out.println("<script>");
                out.println("alert('成功,文件已保存!" + file.replace("\\", "\\\\")
                        + "！');");
                out.println("history.back();");
                out.println("</script>");
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Title: show
     * @author Eric bjwdong@cn.ibm.com
     * @Description: 展示到页面上
     * @param @param response
     * @param @param request
     * @param @param res
     * @param @throws Exception
     * @return void
     * @throws
     */
    public static void show(String response, HttpServletRequest request,
                            HttpServletResponse res) throws Exception {
        // TODO Auto-generated method stub
        try {
            Document doc = DocumentHelper.parseText(response);
            Element root = doc.getRootElement();
            List attrList = root.elements();
            String file = "";
            Element bo = (Element) attrList.get(5);              //取到 batchContent节点
            String merchartNo = root.elementText("batchBizid");
            String version = root.elementText("batchVersion");
            String date = root.elementText("batchDate");
            String pcno = root.elementText("batchCurrnum");
            String batchContent = "";
            String detailInfo = root.elementText("detailInfo");

            StringBuffer sb = new StringBuffer();
            if (detailInfo != null) {
                sb.append(detailInfo);
            } else {
                for (int i = 0; i < bo.elements().size(); i++) {
                    Element item = (Element) bo.elements().get(i);
                    sb.append(item.getData() + "|");
                }
                batchContent = sb.toString();
            }
            String inputCharset = root.elementText("_input_charset");
            String key = DsfConfig.key;
            String signR = root.elementText("sign");
            String signTypeR = root.elementText("signType");
            Map map = new HashMap();
            map.put("batchBizid", merchartNo);
            map.put("batchVersion", version);
            map.put("batchDate", date);
            map.put("batchCurrnum", pcno);
            if (!"".equals(batchContent)) {
                map.put("batchContent", batchContent);
            } else {
                map.put("detailInfo", detailInfo);
            }
            map.put("_input_charset", inputCharset);
            Map mapNew = DsfFunction.ParaFilter(map); // 除去数组中的空值和签名参数
            String mySign = DsfFunction.BuildMysign(mapNew, key);// 生成签名结果
            if (!signR.equals(mySign)) {

                res.getWriter().print("fail,sign is diffrent");
                // throw new Exception("验筌错误！");
            } else {
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmm");
                String pa = request.getSession().getServletContext()
                        .getRealPath("/");
                res.setContentType("text/html; charset=gbk"); // 转码
                PrintWriter pw = res.getWriter();
                if (detailInfo != null) {
                    pw.print(detailInfo);

                } else {
                    pw.print("<ul>");
//					pw.print("<li>_input_charset:" + inputCharset + "</li>");
//					pw.print("<li>batchBizid:" + merchartNo + "</li>");
//					pw.print("<li>batchVersion:" + version + "</li>");
//					pw.print("<li>batchDate:" + date + "</li>");
//					pw.print("<li>batchCurrnum:" + pcno + "</li>");
//					pw.print("<li>signType:" + signTypeR + "</li>");
//					pw.print("<li>sign:" + signR + "</li>");
//					pw.print("<li>明细如下:</li>");
                    if (batchContent != null) {
                        for (int i = 0; i < batchContent.split("\\|").length; i++) {
                            pw.print("<li>"+batchContent.split("\\|")[i] + "</li>");
                        }
                    }
                    pw.print("</ul>");

                }
                pw.flush();
                pw.close();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }}
