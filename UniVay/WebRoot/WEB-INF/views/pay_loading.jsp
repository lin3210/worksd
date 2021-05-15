<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>匹凸匹-互联网安全高收益理财平台</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="#ffa74d">
<meta name="format-detection" content="telephone=no">
<link href="/front/css/g.css" rel="stylesheet" media="screen">
</head>
<script>
function getQueryString(name) 
{
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}
</script>
<script charset="utf-8" type="text/javascript" src="/main/saved_resource"></script>
<link href="/front/css/svg.css" rel="stylesheet" media="screen">
<body class="bodyf">
<div class="headerfx js-fixed-node "  > 
    <header class="header">
        <div class="content pr">
          <h1 id="header_title">支付跳转</h1>
        </div>
    </header>
</div>
<c:if test="${msg.retCode eq '0000'}"> 
<div class="l-wrapper">
	<svg viewBox="0 0 120 120" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
		<g id="circle" class="g-circles g-circles--v1">
			<circle id="12" transform="translate(35, 16.698730) rotate(-30) translate(-35, -16.698730) " cx="35" cy="16.6987298" r="10"></circle>
			<circle id="11" transform="translate(16.698730, 35) rotate(-60) translate(-16.698730, -35) " cx="16.6987298" cy="35" r="10"></circle>
			<circle id="10" transform="translate(10, 60) rotate(-90) translate(-10, -60) " cx="10" cy="60" r="10"></circle>
			<circle id="9" transform="translate(16.698730, 85) rotate(-120) translate(-16.698730, -85) " cx="16.6987298" cy="85" r="10"></circle>
			<circle id="8" transform="translate(35, 103.301270) rotate(-150) translate(-35, -103.301270) " cx="35" cy="103.30127" r="10"></circle>
			<circle id="7" cx="60" cy="110" r="10"></circle>
			<circle id="6" transform="translate(85, 103.301270) rotate(-30) translate(-85, -103.301270) " cx="85" cy="103.30127" r="10"></circle>
			<circle id="5" transform="translate(103.301270, 85) rotate(-60) translate(-103.301270, -85) " cx="103.30127" cy="85" r="10"></circle>
			<circle id="4" transform="translate(110, 60) rotate(-90) translate(-110, -60) " cx="110" cy="60" r="10"></circle>
			<circle id="3" transform="translate(103.301270, 35) rotate(-120) translate(-103.301270, -35) " cx="103.30127" cy="35" r="10"></circle>
			<circle id="2" transform="translate(85, 16.698730) rotate(-150) translate(-85, -16.698730) " cx="85" cy="16.6987298" r="10"></circle>
			<circle id="1" cx="60" cy="10" r="10"></circle>
		</g>
		<use xlink:href="#circle" class="use"/>
	</svg>
	<p style="color: #ccc">跳转中...请稍候</p>
</div> 
<script>
window.location.href = "${msg.url}";
</script>
</c:if>
<c:if test="${msg.retCode != '0000'}">
<div style="text-align: center;height: 300px;font-size: 16px;">
    <p style="padding-top:100px;">订单错误：${msg.retMsg}</p> 
</div>
<div class="content">
        <a href="/wx/main/index.shtml" class="btn mb10">点击返回理财</a>
</div>
</c:if>
</body>
</html>
