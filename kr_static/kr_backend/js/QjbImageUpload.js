/**
 *     __  ___
 *    /  |/  /___   _____ _____ ___   ____   ____ _ ___   _____
 *   / /|_/ // _ \ / ___// ___// _ \ / __ \ / __ `// _ \ / ___/
 *  / /  / //  __/(__  )(__  )/  __// / / // /_/ //  __// /
 * /_/  /_/ \___//____//____/ \___//_/ /_/ \__, / \___//_/
 *                                        /____/
 *
 * @description MessengerJS, a common cross-document communicate solution.
 * @author biqing kwok
 * @version 2.0
 * @license release under MIT license
 */

window.Messenger = (function(){

    // 消息前缀, 建议使用自己的项目名, 避免多项目之间的冲突
    // !注意 消息前缀应使用字符串类型
    var prefix = "upload_image",
        supportPostMessage = 'postMessage' in window;

    // Target 类, 消息对象
    function Target(target, name, prefix){
        var errMsg = '';
        if(arguments.length < 2){
            errMsg = 'target error - target and name are both required';
        } else if (typeof target != 'object'){
            errMsg = 'target error - target itself must be window object';
        } else if (typeof name != 'string'){
            errMsg = 'target error - target name must be string type';
        }
        if(errMsg){
            throw new Error(errMsg);
        }
        this.target = target;
        this.name = name;
        this.prefix = prefix;
    }

    // 往 target 发送消息, 出于安全考虑, 发送消息会带上前缀
    if ( supportPostMessage ){
        // IE8+ 以及现代浏览器支持
        Target.prototype.send = function(msg){
            this.target.postMessage(this.prefix + '|' + this.name + '__Messenger__' + msg, '*');
        };
    } else {
        // 兼容IE 6/7
        Target.prototype.send = function(msg){
            var targetFunc = window.navigator[this.prefix + this.name];
            if ( typeof targetFunc == 'function' ) {
                targetFunc(this.prefix + msg, window);
            } else {
                throw new Error("target callback function is not defined");
            }
        };
    }

    // 信使类
    // 创建Messenger实例时指定, 必须指定Messenger的名字, (可选)指定项目名, 以避免Mashup类应用中的冲突
    // !注意: 父子页面中projectName必须保持一致, 否则无法匹配
    function Messenger(messengerName, projectName){
        this.targets = {};
        this.name = messengerName;
        this.listenFunc = [];
        this.prefix = projectName || prefix;
        this.initListen();
    }

    // 添加一个消息对象
    Messenger.prototype.addTarget = function(target, name){
        var targetObj = new Target(target, name,  this.prefix);
        this.targets[name] = targetObj;
    };

    // 初始化消息监听
    Messenger.prototype.initListen = function(){
        var self = this;
        var generalCallback = function(msg){
            if(typeof msg == 'object' && msg.data){
                msg = msg.data;
            }
            
            var msgPairs = msg.split('__Messenger__');
            var msg = msgPairs[1];
            var pairs = msgPairs[0].split('|');
            var prefix = pairs[0];
            var name = pairs[1];

            for(var i = 0; i < self.listenFunc.length; i++){
                if (prefix + name === self.prefix + self.name) {
                    self.listenFunc[i](msg);
                }
            }
        };

        if ( supportPostMessage ){
            if ( 'addEventListener' in document ) {
                window.addEventListener('message', generalCallback, false);
            } else if ( 'attachEvent' in document ) {
                window.attachEvent('onmessage', generalCallback);
            }
        } else {
            // 兼容IE 6/7
            window.navigator[this.prefix + this.name] = generalCallback;
        }
    };

    // 监听消息
    Messenger.prototype.listen = function(callback){
        var i = 0;
        var len = this.listenFunc.length;
        var cbIsExist = false;
        for (; i < len; i++) {
            if (this.listenFunc[i] == callback) {
                cbIsExist = true;
                break;
            }
        }
        if (!cbIsExist) {
            this.listenFunc.push(callback);
        }
    };
    // 注销监听
    Messenger.prototype.clear = function(){
        this.listenFunc = [];
    };
    // 广播消息
    Messenger.prototype.send = function(msg){
        var targets = this.targets,
            target;
        for(target in targets){
            if(targets.hasOwnProperty(target)){
                targets[target].send(msg);
            }
        }
    };

    return Messenger;
})();

/**
 * 使用方法：
 * 	$(".btn-upload").QjbImageUpload({
 *			domain: "backend",  //应用名称、域名
 *			aspectRatio: "1:1", //宽高比例
 *			success: function(data){
 *				if (data!=undefined && data.url!=undefined && data.url!=""){
 *					var sHtml = '';
 *					sHtml += '<img src="'+data.url+'" style="width:100%;height:100%;">';
 *					$(sHtml).appendTo( document.body );
 *			  }
 *			}
 *		});
 *  $(".btn-upload").QjbImageUpload({
 * 			formUpload: true,
 * 			scale: false,
 *			domain: "backend",  //应用名称、域名
 *			success: function(data){
 *				if (data!=undefined && data.url!=undefined && data.url!=""){
 *					var sHtml = '';
 *					sHtml += '<img src="'+data.url+'" style="width:100%;height:100%;">';
 *					$(sHtml).appendTo( document.body );
 *			  }
 *			}
 *		});
 * 
 **/
var QjbImageUploadFrame = {
	upbrowser: function(){
		var u = navigator.userAgent.toLowerCase();
		return {
			trident: u.indexOf('trident') > -1, //IE内核
			presto: u.indexOf('presto') > -1, //opera内核
			webKit: u.indexOf('applewebKit') > -1, //苹果、谷歌内核
			gecko: u.indexOf('gecko') > -1 && u.indexOf('khtml') == -1, //火狐内核
			mobile: !!u.match(/applewebkit.*mobile.*/)||!!u.match(/applewebkit/), //是否为移动终端
			ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
			android: u.indexOf('android') > -1 || u.indexOf('linux') > -1, //android终端或者uc浏览器
//			iPhone: u.indexOf('iphone') > -1 || u.indexOf('mac') > -1, //是否为iPhone或者QQHD浏览器
			iPhone: u.indexOf('iphone') > -1, //是否为iPhone或者QQHD浏览器
			iPad: u.indexOf('ipad') > -1, //是否iPad
			webapp: u.indexOf('safari') == -1, //是否web应该程序，没有头部与底部
			weixin: u.indexOf("micromessenger")>-1
		}
	},
	check: function(){
		if (this.upbrowser().android){
			return "android";
		} else if ((this.upbrowser().iPhone || this.upbrowser().iPad)){
			return "ios";
		} else if (this.upbrowser().weixin){
			return "weixin";
		} else {
			return "web";
		}
	},
	getPageTop: function() {
		var iBodyHeight = document.documentElement.clientHeight;
		var iScrollHeight = $(document.body).scrollTop();
		var iTop = (iScrollHeight + (iBodyHeight/2-270));
		if (iTop < 0){
			iTop = 2;
		}
		return iTop;
	},
	getPageLeft: function() {
    var iBodyWidth = document.documentElement.clientWidth;
    var iScrollWidth = $(document.body).scrollLeft();
    var iLeft = ( iScrollWidth + (iBodyWidth/2-325) );
    if (iLeft < 0){
    	iLeft = 5;
    }
    return iLeft;
	},
	show: function(settings){
    var d = new Date();
    var _timestamp = "1.0";
//		var _timestamp = (d.getFullYear()+""+(d.getMonth()+1)+""+d.getDate()+""+d.getHours()+""+d.getMinutes()+""+d.getSeconds()+""+d.getMilliseconds());
		var version = this.check();
		var frameRoot = "http://upload.qjbian.com/";
		if (settings.test!=undefined && settings.test){
			frameRoot = "http://192.168.237.79/";
		}
		var frameURL = frameRoot + "cropperForMobile.action?v="+_timestamp;
		var iframeStype = "";
		var iTop = 0, iLeft = 0;
		if (version=="weixin" || version=="android" || version=="ios"){
			iTop = $(document.body).scrollTop();
			iframeStype = "top:"+iTop+"px;left:0;height:100%;width:100%;";
		}else{
			iTop = this.getPageTop(), iLeft = this.getPageLeft();
			frameURL = frameRoot + "cropperForWeb.action?v="+_timestamp;
			iframeStype = "left:"+iLeft+"px;top:"+iTop+"px;height:528px;width:640px;border-radius:6px";
		}
		var iframeName = "dynamic_creation_upload_iframe";
		$('<iframe id="'+iframeName+'" name="'+iframeName+'" src="'+frameURL+'" style="display:none;border:0;margin:0;position:absolute;z-index:9999;-webkit-transform: scale(1);-webkit-transform-origin: 100% 100%;'+iframeStype+'"></iframe>').appendTo(document.body);
		var shtml = '';
		shtml+='<script id="dynamic_creation_upload_script_after">\n';
		shtml+='var g_qjb_resp_num=0; upload_iframe = document.getElementById("'+iframeName+'");\n';
		shtml+='var messenger = new Messenger("parentWindow", "UploadCropperMessenger")\n';
    shtml+='messenger.addTarget(upload_iframe.contentWindow, "'+iframeName+'");\n';
    shtml+='messenger.listen(function(msg){\n';
    shtml+= 'console.log("request.listen..."+msg);\n';
    shtml+= 'if (msg=="OK"){g_qjb_resp_num++;return;}\n';
    shtml+= 'if (msg!=""&&msg!="OK"){\n';
    shtml+=  'var obj = JSON.parse(msg);\n';
    shtml+=  'if(obj && obj.url && obj.url!="" && callback){callback(obj)}\n';
    shtml+= '}\n';
    shtml+= 'messenger.clear();';
    shtml+= '$("#'+iframeName+'").remove();$(".qjb-page").show();$("#dynamic_creation_upload_script_after").remove();document.body.style.overflow="";\n';
    shtml+='});\n';
    shtml+='function sendMessage(oMsg){messenger.targets[\''+iframeName+'\'].send(JSON.stringify(oMsg));console.log("request.send."+JSON.stringify(oMsg));};\n';
    shtml+='function sendMsg(){sendMessage({"domain":"'+(settings.domain?settings.domain:"unknown")+'","aspectRatio":"'+(settings.aspectRatio ? settings.aspectRatio : "1:1" )+'","thumbnail":['+((settings.thumbnail && settings.thumbnail.length>0)?settings.thumbnail:'')+']})};\n';
    shtml+='function qjb_count_down(){if(g_qjb_resp_num==0){sendMsg();setTimeout("qjb_count_down()", 250);}}\n';
		shtml+='qjb_count_down();\n';
    shtml+='var callback = '+settings.success;
		shtml+='<\/script>';
		$(shtml).appendTo(document.body);
		$(".qjb-page").hide();
		$("#"+iframeName).show();
		if (version=="weixin" || version=="android" || version=="ios"){
			document.body.style.overflow='hidden';
		}
	},
	upload: function(settings) {
		if ( !($("#dynamic_creation_form_upload_file") && $("#dynamic_creation_form_upload_file").length>0)){
			$('<div id="dynamic_creation_form_upload_file"><input type="file" id="qjbFormUploadImage" name="qjbFormUploadImage" accept="image/*" style="width:1px;height:1px;border:0;margin:-1px;overflow:hidden;"></div>').appendTo(document.body);
			var shtml = '';
			shtml+='<script id="dynamic_creation_form_upload_script_after">';
			shtml+='document.getElementById("qjbFormUploadImage").onchange=function(){';
			shtml+= 'var files = this.files; var qjbfile = files[0];';
			shtml+= 'if (qjbfile.size > 5*1024*1024){alert("图片太大，最大支持5M");return;}';
	    shtml+= 'var outtime = outtime || 15000;var xhr = new XMLHttpRequest();var formdata = new FormData();';
	    shtml+= 'formdata.append("version", "v1.0");';
	    shtml+= 'formdata.append("domain", "'+settings.domain+'");';
	    if (settings.scale==undefined || settings.scale==true){
	    	shtml+='formdata.append("scale", 1);';
	    }else{
	    	shtml+='formdata.append("scale", 0);';
	    }
	    shtml+= 'formdata.append("file", qjbfile);';
	    shtml+= 'xhr.onreadystatechange = function(){';
	    shtml+=  'if(xhr.readyState === 4){';
	    shtml+=   'if(xhr.status === 200){';
	    shtml+=    'console.log("result="+xhr.responseText);';
	    shtml+=    'var ret = JSON.parse(xhr.responseText);';
	    shtml+=    'if (ret.result=="1001" && ret.content!="" && callback){';
	    shtml +=    'var obj = JSON.parse(ret.content);';
	    shtml+=     'callback(obj);';
	    shtml+=     '$("#dynamic_creation_form_upload_file").remove();$("#dynamic_creation_form_upload_script_after").remove();';
	    shtml+=    '}else{alert(ret.message);}';
	    shtml+=   '}else{alert("上传失败，请检查网络");}';
	    shtml+=  '}';
	    shtml+= '};';
	    if (settings.test!=undefined && settings.test){
				shtml+= 'xhr.open("POST", "http://192.168.237.79/formUpload.action");';
			}else{
	    	shtml+= 'xhr.open("POST", "http://upload.qjbian.com/formUpload.action");';
	   }
	    shtml+= 'xhr.timeout = outtime;';
	    shtml+= 'xhr.ontimeout = function(){timeout && timeout.apply(null);};';
	    shtml+= 'xhr.send(formdata);';
			shtml+='};';
			shtml+='$("#qjbFormUploadImage").click();';
			shtml+='var callback='+settings.success;
			shtml+='</script>';
			$(shtml).appendTo(document.body);
		}else{
			$("#qjbFormUploadImage").click();
		}
	}
};
$.fn.QjbImageUpload = function(settings){
	var self = $(this);
	settings = $.extend({}, settings || {});
	this.on("click", function(){
		if (settings.formUpload){
			QjbImageUploadFrame.upload(settings);
		}else{
			QjbImageUploadFrame.show(settings);
		}
	});
}