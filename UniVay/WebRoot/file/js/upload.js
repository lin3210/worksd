var policyText = {
	"expiration": "2021-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
	"conditions": [
		["content-length-range", 0, 1048576000] // 设置上传文件的大小限制
	]
};

accessid = '';
accesskey = '';
host = 'http://mofah5.oss-cn-hongkong.aliyuncs.com';

var policyBase64 = Base64.encode(JSON.stringify(policyText))
message = policyBase64
var bytes = Crypto.HMAC(Crypto.SHA1, message, accesskey, {
	asBytes: true
});
var signature = Crypto.util.bytesToBase64(bytes);

//文件夹名
var folder = "H5="
//用户id
//var loginData = JSON.parse(localStorage.getItem("loginData"));
//var userId = loginData.ui +"=";
var userId = "888" +"=";

//时间戳
var timestamp = new Date().getTime();
//原文件名
var imgName = '${filename}';
//后缀
var fileH = '.jpg';
//上传的文件名
var p1 = folder + userId + timestamp + "p1" + fileH;
var p2 = folder + userId + timestamp + "p2" + fileH;
var p3 = folder + userId + timestamp + "p3" + fileH;
//判断用户以前是否有图片
if($("#p1").val() != "" && $("#p2").val() != "" && $("#p3").val() != "" ){
	p1 = $("#p1").val();
	p2 = $("#p2").val();
	p3 = $("#p3").val();
}


//创建对象并配置参数
var uploader1 = new plupload.Uploader({

	browse_button: 'selectfiles1', 
	url: host, 
	filters: {
		mime_types: [ 
			{
				title: "Image files",
				extensions: "jpg,png"
			}
		],
		max_file_size: '10000kb', 
		prevent_duplicates: true //不允许选取重复文件
	},
	max_retries: 1,
	multi_selection: false,
	resize: {
		// width: 100,
		// height: 100,
		// crop: true,
		quality: 50,
		preserve_headers: false
	},
	runtimes: 'html5,flash,silverlight,html4',
	container: document.getElementById('container1'),
	flash_swf_url: 'lib/plupload-2.1.2/js/Moxie.swf',
	silverlight_xap_url: 'lib/plupload-2.1.2/js/Moxie.xap',

	multipart_params: {
		'Filename': '${filename}',
		'key': p1,		//上传文件的object名称
		'policy': policyBase64,
		'OSSAccessKeyId': accessid, //oss的keyId
		'success_action_status': '200', //让服务端返回200,不然，默认会返回204
		'signature': signature,		//根据Access Key Secret和policy计算的签名信息
	},
	
	init: {
		PostInit: function() {
			document.getElementById('ossfile1').innerHTML = '';
			document.getElementById('postfiles1').onclick = function() {
				uploader1.start();
				return false;
			};
		},
		/* 会在文件上传过程中不断触发 */
		UploadProgress: function(up, file) {
			var d = document.getElementById(file.id);
			d.getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";

			var prog = d.getElementsByTagName('div')[0];
			var progBar = prog.getElementsByTagName('div')[0];
			progBar.style.width = 2 * file.percent + 'px';
			progBar.setAttribute('aria-valuenow', file.percent);
		},
		
		/* 当文件添加到上传队列后触发 */
		FilesAdded: function(up, files) {
			plupload.each(files, function(file) {
				document.getElementById('ossfile1').innerHTML = '<div id="' + file.id + '">' +
					file.name +
					' (' + plupload.formatSize(file.size) + ')<b></b>' +
					'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>' +
					'</div>';
				
				var preloader = new mOxie.Image();
				preloader.onload = function () {
					preloader.downsize(680, 412);//先压缩一下要预览的图片
					
					//得到图片src,实质为一个base64编码的数据
					var imgsrc = preloader.getAsDataURL('image/jpeg', 60);
					
					file.imgsrc = imgsrc;
					$("#selectfiles1 img").attr("src", imgsrc);
					preloader.destroy();
					preloader = null;
				};
				preloader.load(file.getSource());
				
				if (up.files.length > 1) {
					up.removeFile(up.files[0]);
					return;
				}
			});
		},
		
		/* 当队列中的某一个文件上传完成后触发 */
		FileUploaded: function(up, file, info) {
			if (info.status == 200 ) {
				$("#console1").text("Tải lên thành công!");
				$("#console1").css({"font-size":"3em","color":"green"});
				$("#ossfile1").next().next().attr("src", $("#selectfiles1 img").attr("src"));
				$("#p1").val("H5="+userId + timestamp + "p1.jpg");
				$("#ossfile1").hide();
				
				$("#container1").hide();
				vp1 = 1;
			}
		},
		
		Error: function(up, err) {
			if(err.response != null){
				$("#console1").text("Tải lên thất bại! Xin vui lòng thử lại");
				$("#console1").css({"font-size":"2em","color":"red"})
			}
		}
	}
});

uploader1.init();


//创建对象并配置参数
var uploader2 = new plupload.Uploader({

	browse_button: 'selectfiles2', 
	url: host, 
	filters: {
		mime_types: [ 
			{
				title: "Image files",
				extensions: "jpg,png"
			}
		],
		max_file_size: '10000kb', 
		prevent_duplicates: true //不允许选取重复文件
	},
	max_retries: 1,
	multi_selection: false,
	resize: {
		// width: 100,
		// height: 100,
		// crop: true,
		quality: 60,
		preserve_headers: false
	},
	runtimes: 'html5,flash,silverlight,html4',
	container: document.getElementById('container2'),
	flash_swf_url: 'lib/plupload-2.1.2/js/Moxie.swf',
	silverlight_xap_url: 'lib/plupload-2.1.2/js/Moxie.xap',

	multipart_params: {
		'Filename': '${filename}',
		'key': p2,		//上传文件的object名称
		'policy': policyBase64,
		'OSSAccessKeyId': accessid, //oss的keyId
		'success_action_status': '200', //让服务端返回200,不然，默认会返回204
		'signature': signature,		//根据Access Key Secret和policy计算的签名信息
	},
	
	init: {
		PostInit: function() {
			document.getElementById('ossfile2').innerHTML = '';
			document.getElementById('postfiles2').onclick = function() {
				uploader2.start();
				return false;
			};
		},

		UploadProgress: function(up, file) {
			var d = document.getElementById(file.id);
			d.getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";

			var prog = d.getElementsByTagName('div')[0];
			var progBar = prog.getElementsByTagName('div')[0]
			progBar.style.width = 2 * file.percent + 'px';
			progBar.setAttribute('aria-valuenow', file.percent);
		},
		
		FilesAdded: function(up, files) {
			plupload.each(files, function(file) {
				document.getElementById('ossfile2').innerHTML = '<div id="' + file.id + '">' +
					file.name +
					' (' + plupload.formatSize(file.size) + ')<b></b>' +
					'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>' +
					'</div>';
					
				var preloader = new mOxie.Image();
				preloader.onload = function () {
					preloader.downsize(680, 412);//先压缩一下要预览的图片
					
					//得到图片src,实质为一个base64编码的数据
					var imgsrc = preloader.getAsDataURL('image/jpeg', 60);
					
					file.imgsrc = imgsrc;
					$("#selectfiles2 img").attr("src", imgsrc);
					preloader.destroy();
					preloader = null;
				};
				preloader.load(file.getSource());
				if (up.files.length > 1) {
					up.removeFile(up.files[0]);
					return;
				}
			});
		},
		
		FileUploaded: function(up, file, info) {
			if (info.status >= 200 || info.status < 200) {
				$("#console2").text("Tải lên thành công!");
				$("#console2").css({"font-size":"3em","color":"green"});
				$("#ossfile2").next().next().attr("src", $("#selectfiles2 img").attr("src"));
				$("#p2").val("H5="+userId + timestamp + "p2.jpg");
				$("#ossfile2").hide();
				$("#container2").hide();
				vp2 = 1;
			}
		},
		
		Error: function(up, err) {
			if(err.response != null){
				$("#console2").text("Tải lên thất bại! Xin vui lòng thử lại");
				$("#console2").css({"font-size":"2em","color":"red"})
			}
		}
	}
});

uploader2.init();

//创建对象并配置参数
var uploader3 = new plupload.Uploader({

	browse_button: 'selectfiles3', 
	url: host, 
	filters: {
		mime_types: [ 
			{
				title: "Image files",
				extensions: "jpg,png"
			}
		],
		max_file_size: '10000kb', 
		prevent_duplicates: true //不允许选取重复文件
	},
	multi_selection: false,
	max_retries: 1,
	resize: {
		// width: 100,
		// height: 100,
		// crop: true,
		quality: 60,
		preserve_headers: false
	},
	runtimes: 'html5,flash,silverlight,html4',
	container: document.getElementById('container3'),
	flash_swf_url: 'lib/plupload-2.1.2/js/Moxie.swf',
	silverlight_xap_url: 'lib/plupload-2.1.2/js/Moxie.xap',

	multipart_params: {
		'Filename': '${filename}',
		'key': p3,		//上传文件的object名称
		'policy': policyBase64,
		'OSSAccessKeyId': accessid, //oss的keyId
		'success_action_status': '200', //让服务端返回200,不然，默认会返回204
		'signature': signature,		//根据Access Key Secret和policy计算的签名信息
	},
	
	init: {
		PostInit: function() {
			document.getElementById('ossfile3').innerHTML = '';
			document.getElementById('postfiles3').onclick = function() {
				uploader3.start();
				return false;
			};
		},

		UploadProgress: function(up, file) {
			var d = document.getElementById(file.id);
			d.getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";

			var prog = d.getElementsByTagName('div')[0];
			var progBar = prog.getElementsByTagName('div')[0]
			progBar.style.width = 2 * file.percent + 'px';
			progBar.setAttribute('aria-valuenow', file.percent);
		},
		
		FilesAdded: function(up, files) {
			plupload.each(files, function(file) {
				document.getElementById('ossfile3').innerHTML = '<div id="' + file.id + '">' +
					file.name +
					' (' + plupload.formatSize(file.size) + ')<b></b>' +
					'<div class="progress"><div class="progress-bar" style="width: 0%"></div></div>' +
					'</div>';
				var preloader = new mOxie.Image();
				preloader.onload = function () {
					preloader.downsize(680, 412);//先压缩一下要预览的图片
					
					//得到图片src,实质为一个base64编码的数据
					var imgsrc = preloader.getAsDataURL('image/jpeg', 60);
					
					file.imgsrc = imgsrc;
					$("#selectfiles3 img").attr("src", imgsrc);
					preloader.destroy();
					preloader = null;
				};
				preloader.load(file.getSource());
				if (up.files.length > 1) {
					up.removeFile(up.files[0]);
					return;
				}
			});
		},
		
		FileUploaded: function(up, file, info) {
			if (info.status >= 200 || info.status < 200) {
				$("#console3").text("Tải lên thành công!");
				$("#console3").css({"font-size":"3em","color":"green"});
				$("#ossfile3").next().next().attr("src", $("#selectfiles3 img").attr("src"));
				$("#p3").val("H5="+userId + timestamp + "p3.jpg");
				$("#ossfile3").hide();
				$("#container3").hide();
				vp3 = 1;
			}
		},
		
		Error: function(up, err) {
			if(err.response != null){
				$("#console3").text("Tải lên thất bại! Xin vui lòng thử lại");
				$("#console3").css({"font-size":"2em","color":"red"})
			}
		}
	}
});

uploader3.init();