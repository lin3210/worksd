<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%-- 加载公用页头文件 开始 --%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
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
	
		<div>
			<div>
			请输入显示类型<input id="flag" name="flag" type="text" value="" />
			<input type="button" value="提交" onclick="test()"/>
			</div>
			<div id="info">
			<ul>
				<c:choose>
					<c:when test="${not empty page.data}">
						<%-- 循环取出数据 开始 --%>
									
						<c:forEach var="item" items="${page.data}">
							<li><c:out value="${ item.title }" default="-"/></li>
						</c:forEach>
						
						<%-- 循环取出数据 结束 --%>
				
					</c:when>
					<c:otherwise>暂无数据</c:otherwise>
				</c:choose>
				
			</ul>
			</div>
			
		</div>
		
	</body>
	<script type="text/javascript"> 
	
		function test(flag){
		 
			var forwardUrl ="/servlet/Test?function=test&flag="+flag;
			
			ajaxLoad("info",forwardUrl,"post");
			
		}
		
	</script>
</html>
