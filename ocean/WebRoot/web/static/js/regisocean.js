// initialize Account Kit with CSRF protection
  AccountKit_OnInteractive = function(){
    AccountKit.init(
      {
        appId:"436349787125595", 
        state:"ocean", 
        version:"v1.0",
        fbAppEventsEnabled:true,
        redirect:"https://www.oceanvay.com/web/registered.html",
		debug:true
      }
    );
  };

  // login callback
  function loginCallback(response) {
	  console.log(response.status);
    if (response.status === "PARTIALLY_AUTHENTICATED") {
      var code = response.code;
      var csrf = response.state;
	  proving(code);
      // Send code to server to exchange for access token
    }
    else if (response.status === "NOT_AUTHENTICATED") {
      // handle authentication failure
    }
    else if (response.status === "BAD_PARAMS") {
      // handle bad parameters
    }
  }

  // phone form submission handler
  function smsLogin() {
	  var res = verifyCode.validate(document.getElementById("vcode").value);
            //alert(input_code+"----"+code);  
            if(!res)  
            {   
				alert("Sai mã xác minh bằng hình ảnh");
				return ;
			}
    //var countryCode = document.getElementById("country_code").value;
	var countryCode = "+84";
    var phoneNumber = document.getElementById("mobile").value;
	if(phoneNumber.length != 10 || v0and9(phoneNumber)){
		alert("Vui lòng nhập số điện thoại chính xác");
	}
    AccountKit.login(
      'PHONE', 
      {countryCode: countryCode, phoneNumber: phoneNumber}, // will use default values if not specified
      loginCallback
    );
  }


  // email form submission handler
  function emailLogin() {
    var emailAddress = document.getElementById("email").value;
    AccountKit.login(
      'EMAIL',
      {emailAddress: emailAddress},
      loginCallback
    );
  }
  function proving(code) {
			
			var u = navigator.userAgent, app = navigator.appVersion;
			var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1;
			var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
			var phonetype =0;
			if (isAndroid) {
				phonetype=1; // 跳安卓端下载地址
			}
			 // 是iOS浏览器
			if (isIOS) {
				phonetype=2; // 跳AppStore下载地址
			}
			//用户输入的验证信息
			var u_mobile = $("#mobile").val();
			//var profession=$("input:radio[name='demo-radio']:checked").val();
			var profession=2;
			var token_ = code;
			var para = {"mobile":u_mobile,"phonetype":phonetype,"token":token_,"refferee":"0010"};
			$.ajaxSettings.async = false;
			$.getJSON("/servlet/current/HtmlOceanAction?function=RegisterBookNewFaceHFive&time="+new Date().getTime(),para,function(data){
				console.log(data);
				if(data.error == 0 || data.error == -222){
					localStorage.setItem("loginData", JSON.stringify(data));
					window.location.href = "approveid.html";
					
				}else{
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
		document.onkeypress=function(){
			if(event.keyCode==13){
				return false;
			}
		}
 
    // 是微信浏览器
    function is_weixn(){
      var ua = navigator.userAgent.toLowerCase();
      if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
      } else {
        return false;
      }
    }
 //验证手机号
		function mobileChage(value) {
			if (value.val().length != 10) {
				value.css("background-color", "#e5c5e8");
				value.parent().css("background-color", "#e5c5e8");
				value.next().show();
				$(".findV a").removeAttr("onclick");
				$(".findV a").css("opacity", 0.3);
				return 0;
			} else {
				value.css("background-color", "#FFFFFF");
				value.parent().css("background-color", "#FFFFFF");
				value.next().hide();
				$(".findV a").attr("onclick", "findCode()");
				$(".findV a").css("opacity", 0.9);
				return 1;
			}
		}
 
    function IsPC() {
      var userAgentInfo = navigator.userAgent;
      var Agents = ["Android", "iPhone",
        "SymbianOS", "Windows Phone",
        "iPad", "iPod"];
      var flag = true;
      for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
          flag = false;
          break;
        }
      }
      return flag;
    }
