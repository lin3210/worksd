function loadAjaxPost(url, data, resultData) {
	
	var path = "https://www.oceanvay.com/servlet/current/HtmlOceanAction?function=";

	$.ajax({
		type: "POST",
		url: path + url,
		data: data,
		dataType: "json",
		success: function(data) {
			resultData(data)
		},
		error: function() {
			console.info("访问：" + path + url + "出错");
		}
	})
}

//获取home 数据
function findHomeInit(userId, token) {
	var requestData = new Object();
	requestData.jkld = userId;
	requestData.token = token;
	console.log(userId);
	console.log(token);
	loadAjaxPost(
		"HtmlHminte",
		requestData,
		function(data) {
			if (data != null) {
				return data;
			}
		});

}


//表单数组转json字符串
function arrayToJsonString(a) {
	var o = {};
	$.each(a, function() {
		if (o[this.name]) {
			if (!o[this.name].push) {
				o[this.name] = [o[this.name]];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
}

//验证0~9
function v0and9(num) {
	if (/(^[0-9]\d*$)/.test(num)) {
		return false;
	} else {
		return true;
	}
}
//验证密码格式
function vPassword(str) {
	if (/(^[A-Za-z0-9]+$)/.test(str)) {
		return false;
	} else {
		return true;
	}
}

//
function vBirthday(str) {
	if (/^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-([1-2][0-9][0-9][0-9])$/.test(str)) {
		return true;
	} else {
		return false;
	}
}


//判断是  Android 还是 iOS
function findAi() {
	var u = navigator.userAgent;
	var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
	var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
	if (isAndroid) {
		return 1;
	}
	if (isiOS) {
		return 2;
	}
	return "";
}

/* 
 * formatMoney(s,type) 
 * 功能：金额按千位逗号分隔
 * 参数：s，需要格式化的金额数值. 
 * 参数：type,判断格式化后的金额是否需要小数位. 
 * 返回：返回格式化后的数值字符串. 
 */
function formatMoney(s, type) {
	if (/[^0-9\.]/.test(s))
		return "0";
	if (s == null || s == "null" || s == "")
		return "0";
	s = s.toString().replace(/^(\d*)$/, "$1.");
	s = (s + "00").replace(/(\d*\.\d\d)\d*/, "$1");
	s = s.replace(".", ",");
	var re = /(\d)(\d{3},)/;
	while (re.test(s))
		s = s.replace(re, "$1,$2");
	s = s.replace(/,(\d\d)$/, ".$1");
	if (type == 0) {
		var a = s.split(".");
		if (a[1] == "00") {
			s = a[0];
		}
	}
	return s;
}
