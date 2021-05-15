function loadGetServer(url,param,callback)
{	
   param.url = $("#current_url_value").val();
   $.ajax({
	  type: "GET",
	  url: url,
	  data: param,
	  dataType: "json",
	  timeout: 100000,
	  async:false,
	  success: function(data){if(data==-1){window.location.href="/login.html";}callback(data);},
	  error: function(xhr, type){
	  /* alert('系統错误');*/
	  }
	  
	})
}
function GetQueryString(name){var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)","i");var r=window.location.search.substr(1).match(reg);if(r!=null){return unescape(r[2])}return null}