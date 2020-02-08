var loginData = new Object();
$(function() {
	init();
})

function init(){
	loginData = JSON.parse(localStorage.getItem("loginData"));
	
	if (loginData == null) {
		alert("Danh tính đã hết hạn, xin vui lòng đăng nhập trở lại");
		window.location.href = "registered.html";
	} else {
		var requestData = new Object();
		requestData.userid = loginData.ui;
		requestData.token = loginData.token;
		loadAjaxPost(
			"HtmlHminte",
			requestData,
			function(data) {
				if (data.error == -1) {
					localStorage.removeItem("loginData");
					alert(data.msg);
					window.location.href = "registered.html";
				}else if(data.error == 100){
					$("#realname").val("Xin chào,"+data.realname);
					$("#idno").val("Số ID:"+data.idno);
					$("#sjshmoney").val(toThousands(data.sjsh_money));
					$("#sjshmoney1").val(toThousands(data.sjsh_money));
					$("#yuqts").val(data.yuq_ts);
					$("#yuqlx").val(toThousands(data.yuq_lx));
					if(data.jkdate == 3){
						$("#jkdate").val("7 Ngày");
					}else{
						$("#jkdate").val("7 Ngày");
					}
					$("#hktime").val(data.hktime);
				}else if(data.error == 101){
					window.location.href = "review.html";
				}else if(data.error == 105){
					alert(data.msg);
					window.location.href = "loan.html";
				}else if(data.error == 200){
					alert(data.msg);
					window.location.href = "approvezalo.html";
				}else if(data.error == 300 || data.error == 400){
					alert(data.msg);
					window.location.href = "approveid.html";
				}else if(data.error == -11 || data.error == -12){
					localStorage.removeItem("loginData");
					alert(data.msg);
					window.location.href = "registered.html";
				}else{
					window.location.href = "registered.html";
				}
				
			});
	}
}
function toThousands(num) {  
	var num = (num || 0).toString(), result = '';  
	while (num.length > 3) {  
		result = ',' + num.slice(-3) + result;  
		num = num.slice(0, num.length - 3);  
	}  
	if (num) { 
		result = num + result; 
	}  
	return result;  
}