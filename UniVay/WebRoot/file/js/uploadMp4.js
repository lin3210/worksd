var policyText = {
    "expiration": "2100-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
    "conditions": [
    ["content-length-range", 0, 1048576000000] // 设置上传文件的大小限制
    ]
};



accessid = 'LTAI4FmJjtKEwbMf4rRrda51';
accesskey = 'qI4a7hGEt88y6MupRp0WjuTA2OAJcm';
host = 'http://yn-ocean.oss-cn-hongkong.aliyuncs.com';


var policyBase64 = Base64.encode(JSON.stringify(policyText))
message = policyBase64
var bytes = Crypto.HMAC(Crypto.SHA1, message, accesskey, { asBytes: true }) ;
var signature = Crypto.util.bytesToBase64(bytes);

//文件夹名
var folder = "H5="
//用户id
//var loginData = JSON.parse(localStorage.getItem("loginData"));
//var userId = loginData.ui +"=";
var userId = "1=";
//时间戳
var timestamp = new Date().getTime();
//原文件名
var imgName = '${filename}';
//上传的文件名
var mp4Name = folder + userId + timestamp + ".mp4";

//文件名
var fileName = "";
//视频
var uploader = new plupload.Uploader({
	browse_button : 'selectfiles', 
	url : host,
	filters: {
		mime_types: [ 
			{
				title: "Image files",
				extensions: "mp4,png,jpg"
			}
		],
		max_file_size: '3000000000kb', 
		prevent_duplicates: false //不允许选取重复文件
	},
	resize: {
		// width: 100,
		// height: 100,
		// crop: true,
		quality: 60,
		preserve_headers: false
	},
	max_retries: 2,
	multi_selection: true,
	runtimes : 'html5,flash,silverlight,html4',
	container: document.getElementById('container'),
	flash_swf_url : 'lib/plupload-2.1.2/js/Moxie.swf',
	silverlight_xap_url : 'lib/plupload-2.1.2/js/Moxie.xap',

    

	multipart_params: {
		'Filename': '${filename}', 
        'key' : mp4Name,
		'policy': policyBase64,
        'OSSAccessKeyId': accessid, 
        'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
		'signature': signature,
	},

	init: {
		PostInit: function() {
			document.getElementById('ossfile').innerHTML = '';
			document.getElementById('postfiles').onclick = function() {
				uploader.start();
				return false;
			};
		},

		FilesAdded: function(up, files) {
			console.log(files);
			fileName = files[0].name
			timestamp =new Date().getTime()
			uploader.settings.multipart_params.key = $("#fileuserid").val()+"="+timestamp+"."+fileName.substring(fileName.lastIndexOf('.') + 1);
			plupload.each(files, function(file) {
				
				document.getElementById('ossfile').innerHTML += '<div id="' + file.id + '">' 
				+ file.name
				+ ' (' + plupload.formatSize(file.size) + ')<b></b>'
				+'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>'
				+'</div>';
			});
		},
		//BeforeUpload: function(up, file){
		//	console.log("BeforeUpload")
		//	fileName = file.name
		//	timestamp =new Date().getTime()
		//	uploader.settings.multipart_params.key = $("#fileuserid").val()+"="+timestamp+"."+fileName.substring(fileName.lastIndexOf('.') + 1);
		//	uploader.start();
		
		//},

		UploadProgress: function(up, file) {
			console.log("UploadProgress")
	
			var d = document.getElementById(file.id);
			//console.log("12h :"+d)
			//d.getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
            
           // var prog = d.getElementsByTagName('div')[0];
			//var progBar = prog.getElementsByTagName('div')[0]
			//progBar.style.width= 2*file.percent+'px';
			//progBar.setAttribute('aria-valuenow', file.percent);
		},

		FileUploaded: function(up, file, info) {
			document.getElementById('ossfile').innerHTML = ""
           if (info.status == 200 ) {
           		var formurl = $("#fileUrl").val();
           		//参数
           		var para = {
           			userId :  $("#fileuserid").val(),
           			name : $("#fileuserid").val()+"="+timestamp+"."+fileName.substring(fileName.lastIndexOf('.') + 1),
           			filetype : $("#filetype").val()
           		}
           		loadGetServer(formurl,para,function(data){
      
           		  if(data.error < 1 ){
				     alert(data.msg);
			      }

		        })
		   }
		},

		Error: function(up, err) {
			if(err.response != null){
				document.getElementById('console').innerHTML="Tải lên thất bại";
			}
		}
	}
});
$("#selectfiles").click(function(){
	console.log("点击")
	uploader.start();
	if(uploader == null){
		uploader.init();
	}
});
uploader.init();