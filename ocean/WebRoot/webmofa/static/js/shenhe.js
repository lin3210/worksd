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
					window.location.href = "repayment.html";
				}else if(data.error == 101){
					$("#realname").val(data.realname);
					$("#idno").val(data.idno);
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
