
var loginData = new Object();

$(function() {
	init();
})

function init(){
	gradeChange();
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
					window.location.href = "review.html";
				}else if(data.error == 105){
					return null;
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

function gradeChange() {
	var jk_money =2000000;
	var money = $("#jk_money").val();
	if(money == 0){
		jk_money =2000000;
	}else if(money == 1){
		jk_money =2500000;
	}else if(money == 2){
		jk_money =3000000;
	}else if(money == 3){
		jk_money =3500000;
	}else if(money == 4){
		jk_money =4000000;
	}
	var jk_date = $("#jk_date").val();
	//测试
	if (jk_date == 3) {
		/* 展示的金额 = 申请金额 - （ 十五天的服务费 + 十五天利息） x 申请金额 */
		var showManey = jk_money * 271 / 1000;
		var showlixi = jk_money * 9 / 1000;
		var showsjds = jk_money * 72 / 100;
		//设置格式
		$("#fuwufei").val(toThousands(showManey));
		$("#lixi").val(toThousands(showlixi));
		$("#sjds").val(toThousands(showsjds));
	}else if (jk_date == 1) {
		var showManey = jk_money * 271 / 1000;
		var showlixi = jk_money * 9 / 1000;
		var showsjds = jk_money * 72 / 100;
		//设置格式
		$("#fuwufei").val(toThousands(showManey));
		$("#lixi").val(toThousands(showlixi));
		$("#sjds").val(toThousands(showsjds));
	}
}
/* commit user loan data  */
function commitData() {
	var jk_money =2000000;
	var money = $("#jk_money").val();
	if(money == 0){
		jk_money =2000000;
	}else if(money == 1){
		jk_money =2500000;
	}else if(money == 2){
		jk_money =3000000;
	}else if(money == 3){
		jk_money =3500000;
	}else if(money == 4){
		jk_money =4000000;
	}
	loginData = JSON.parse(localStorage.getItem("loginData"));
	//用户输入的验证信息
	var u_money = toThousands(jk_money);
	var jk_date = $("#jk_date").val();

	var para = {"userid":loginData.ui,"token":loginData.token,"jk_money":u_money,"jk_date":jk_date};
	$.ajaxSettings.async = false;
	$.getJSON("/servlet/current/HtmlOceanAction?function=HtmlTJJK&time="+new Date().getTime(),para,function(data){

		if(data.error == 0){
			window.location.href = "review.html";
			
		}else if(data.error < 0){
			$(".gx_alert").css("display","block");
			$(".gx_alert_p").html(data.msg)
			$("#bankrz").attr("disabled", true);
			setTimeout(function(){
				$(".gx_alert").css("display","none");
				$("#bankrz").attr("disabled", false);
				},3000);
		}
	})
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