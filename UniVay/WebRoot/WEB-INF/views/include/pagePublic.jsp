<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK" %>
<%@ taglib uri="/tags/jstl-format" prefix="fmt" %>
<%@ taglib uri="/tags/jstl-function" prefix="fn" %>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<jsp:useBean id="configid" class="com.thinkive.base.config.Configuration"></jsp:useBean>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="uri" value="${pageContext.request.requestURI}" scope="request" />
<jsp:directive.page import="com.thinkive.base.config.Configuration"/>

