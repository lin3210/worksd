<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK" %>
<%
	String keywords = request.getParameter("keywords");
	if(keywords == null)
	{
		keywords = "";
	}
	String description = request.getParameter("description");
	if(description == null)
	{
		description = "";
	}
%>
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=GBK" />
<meta name="Keywords" content="<%=keywords%>" />
<meta name="Description" content="<%=description%>" />
<%
    response.setHeader("pragma", "no-cache");
    response.setHeader("cache-control", "no-cache");
    response.setDateHeader("expires", -10);
%>