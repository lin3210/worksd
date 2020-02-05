function  qdtc(){
    $("#errorInfo").hide();
	$("#errorInfo1").hide();
   }

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
							window.location.href = "review.html";
						}else if(data.error == 105){
							alert(data.msg);
							window.location.href = "loan.html";
						}else if(data.error == 200){
							window.location.href = "approvezalo.html";
						}else if(data.error == 300 || data.error == 400){
							return null;
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

		function commit() {
			$("#bankrz").attr("disabled", true);
			loginData = JSON.parse(localStorage.getItem("loginData"));
			//用户输入的验证信息
			var u_username = $("#username").val();
			var u_idno = $("#idno").val();
			var u_bankcard = $("#bankcard").val();
			var u_sel1 = $("#sel1").val();
			if(u_username == "" || u_username == null){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("Không thể để trống họ tên")
				$("#bankrz").attr("disabled", true);
				setTimeout(function(){
					$(".gx_alert").css("display","none");
					$("#bankrz").attr("disabled", false);
					},3000);
			}
			if(u_idno == "" || u_idno == null){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("Không thể để trống chứng minh thư")
				$("#bankrz").attr("disabled", true);
				setTimeout(function(){
					$(".gx_alert").css("display","none");
					$("#bankrz").attr("disabled", false);
					},3000);
			}
			if(u_bankcard == "" || u_bankcard == null){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("Không thể để trống tài khoản ngân hàng")
				$("#bankrz").attr("disabled", true);
				setTimeout(function(){
					$(".gx_alert").css("display","none");
					$("#bankrz").attr("disabled", false);
					},3000);
			}
			if(v0and9(u_idno)){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("CMND chỉ có thể nhập số")
				$("#bankrz").attr("disabled", true);
				setTimeout(function(){
					$(".gx_alert").css("display","none");
					$("#bankrz").attr("disabled", false);
					},3000);
			}
			var requestData = new Object();
			requestData.userid = loginData.ui;
			requestData.token = loginData.token;
			requestData.username = u_username;
			requestData.idno = u_idno;
			requestData.bankcard = u_bankcard;
			requestData.cardName = u_sel1;
			loadAjaxPost(
					"GetMofaBankRZ",
					requestData,
					function(data) {
						if(data.error == 0){
							window.location.href = "approvezalo.html";
						}else if(data.error == -1){
							localStorage.removeItem("loginData");
							$(".gx_alert").css("display","block");
							$(".gx_alert_p").html(data.msg)
							window.location.href = "registered.html";
						}else if(data.error == -3){
							$(".gx_alert").css("display","block");
							$(".gx_alert_p").html(data.msg)
							$("#bankrz").attr("disabled", true);
							setTimeout(function(){
								$(".gx_alert").css("display","none");
								$("#bankrz").attr("disabled", false);
								},3000);
						}
					});
		}
