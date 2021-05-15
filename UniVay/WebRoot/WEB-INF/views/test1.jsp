<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%-- 加载公用页头文件 开始 --%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- 加载公用页头文件 结束 --%>

<%-- 加载页头html标签 开始 --%>
<jsp:include flush="true" page="/WEB-INF/views/include/pageDeclare.jsp" />
<%-- 加载页头html标签 结束 --%>

	<head>
		<%-- 加载页头title 开始 --%>
		<jsp:include page="/WEB-INF/views/include/title.jsp" flush="true">
			<jsp:param name="title" value="前台-测试" />
		</jsp:include>
		<%-- 加载页头title标签 开始 --%>
		<%-- 加载页头meta标签 开始 --%>
		<jsp:include flush="true" page="/WEB-INF/views/include/meta.jsp" >
			<jsp:param name="keywords" value="前台测试,前台,测试" />
			<jsp:param name="description" value="前台测试DEMO"  />
		</jsp:include>
		<%-- 加载页头meta标签 结束 --%>
		<%-- 加载页头js及css标签 开始 --%>
		<jsp:include flush="true" page="/WEB-INF/views/include/jsAndCss.jsp" />
		<%-- 加载页头js及css标签 开始 --%>
		
		<f>
	
<script type="text/javascript">
//页面嵌入流量统计脚本
/*
		var _aq = _aq || [];
		_aq.push(['_setSiteId', 10000]);
		_aq.push(['_trackPageview']);
		
		(function() {
		    var ga = document.createElement( 'script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src='http://localhost:8081/stat/stat.js';
		    var s = document.getElementsByTagName( 'script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
  
		$(document).ready(function(){
		});
*/
</script>

		
	</head>
	<body>
	 阿里斯柯达家啊路上看到；阿莱克斯接电话；啊会计师的更好；啊数据库更好的拉开建设规划
		
	</body>
	<script type="text/javascript"> 
	
		function test(flag){
		 
			var forwardUrl ="/servlet/Test?function=test&flag="+flag;
			
			ajaxLoad("info",forwardUrl,"post");
			
		}
		
	</script>
</html>
