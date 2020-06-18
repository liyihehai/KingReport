var FileUploadUtil = function (settings){
    this.settings=settings;
    this.xhr=null;
    this.upbrowser=function(){
        var u = navigator.userAgent.toLowerCase();
        return {
            trident: u.indexOf('trident') > -1, //IE内核
            presto: u.indexOf('presto') > -1, //opera内核
            webKit: u.indexOf('applewebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('gecko') > -1 && u.indexOf('khtml') == -1, //火狐内核
            mobile: !!u.match(/applewebkit.*mobile.*/)||!!u.match(/applewebkit/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('android') > -1 || u.indexOf('linux') > -1, //android终端或者uc浏览器
            iPhone: u.indexOf('iphone') > -1, //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('ipad') > -1, //是否iPad
            webapp: u.indexOf('safari') == -1, //是否web应该程序，没有头部与底部
            weixin: u.indexOf("micromessenger")>-1
        }
    }
    this.check= function(){
        if (this.upbrowser().android){
            return "android";
        } else if ((this.upbrowser().iPhone || this.upbrowser().iPad)){
            return "ios";
        } else if (this.upbrowser().weixin){
            return "weixin";
        } else {
            return "web";
        }
    }
    this.getPageTop= function() {
        var iBodyHeight = document.documentElement.clientHeight;
        var iScrollHeight = $(document.body).scrollTop();
        var iTop = (iScrollHeight + (iBodyHeight/2-270));
        if (iTop < 0){
            iTop = 2;
        }
        return iTop;
    }
    this.getPageLeft= function() {
        var iBodyWidth = document.documentElement.clientWidth;
        var iScrollWidth = $(document.body).scrollLeft();
        var iLeft = ( iScrollWidth + (iBodyWidth/2-325) );
        if (iLeft < 0){
            iLeft = 5;
        }
        return iLeft;
    }
    this.showImageCropper=function(){
        var version = this.check();
        var appJSGlobAjax=new AppJSGlobAjax();
        var frameURL = appJSGlobAjax.UrlAppendToken(this.settings.cropperUrl);
        var iframeStype = "";
        var iTop = 0, iLeft = 0;
        if (version=="weixin" || version=="android" || version=="ios"){
            iTop = $(document.body).scrollTop();
            iframeStype = "top:"+iTop+"px;left:0;height:100%;width:100%;";
        }else{
            iTop = this.getPageTop(), iLeft = this.getPageLeft();
            iframeStype = "left:"+iLeft+"px;top:"+iTop+"px;height:528px;width:640px;border-radius:6px";
        }
        var iframeName = "dynamic_creation_upload_iframe";
        if ( $("#"+iframeName) && $("#"+iframeName).length>0) {
            $("#"+iframeName).remove();
        }
        $('<iframe id="'+iframeName+'" name="'+iframeName+'" src="'+frameURL+'" style="display:none;border:0;margin:0;position:absolute;z-index:9999;-webkit-transform: scale(1);-webkit-transform-origin: 100% 100%;'+iframeStype+'"></iframe>').appendTo(document.body);
        $("#"+iframeName).show();
        if (version=="weixin" || version=="android" || version=="ios"){
            document.body.style.overflow='hidden';
        }
        this.setCropperMessager(iframeName);
    }
    this.setCropperMessager=function(iframeName){
        this.cropper_iframe = document.getElementById(iframeName);
        this.cropperMessenger = new Messenger("parentWindow", "UploadCropperMessenger");
        this.cropperMessenger.addTarget(this.cropper_iframe.contentWindow, iframeName);
        this.cropperMessenger.listen(function(msg){
            if (msg=="OK"){
                return;
            }
            if (msg==""){
                $.fn.FileUploadUtil.closeCropperIframe();
                return;
            }
            var obj = JSON.parse(msg);
            if(obj && obj.imgBody){
                $.fn.FileUploadUtil.runCropperUpload(obj.imgBody,obj.imgType);
            }
            $.fn.FileUploadUtil.closeCropperIframe();
        });
    }
    this.closeCropperIframe=function(){
        $.fn.FileUploadUtil.cropperMessenger.clear();
        $($.fn.FileUploadUtil.cropper_iframe).remove();
        document.body.style.overflow="";
    }
    this.runCropperUpload= function (base64,imgType) {
        $.fn.FileUploadUtil.xhr = new XMLHttpRequest();
        var formdata = new FormData();
        formdata.append("uploadtype", "0");//表示上传的是裁剪内容
        formdata.append("version", "v1.0");
        if ($.fn.FileUploadUtil.settings.scale==undefined ||
            $.fn.FileUploadUtil.settings.scale==true){
            formdata.append("scale", 1);
        }else{
            formdata.append("scale", 0);
        }
        formdata.append("cropper", base64);
        formdata.append("imgtype", imgType);
        var appendJson = ($.fn.FileUploadUtil.settings.jsonFunction)? $.fn.FileUploadUtil.settings.jsonFunction():{};
        formdata.append("appendJson", appendJson);
        $.fn.FileUploadUtil.doUploadPost($.fn.FileUploadUtil.xhr,formdata);
    }
    this.showFileSelector=function(accept){
        if ( $("#dynamic_creation_form_upload_file") && $("#dynamic_creation_form_upload_file").length>0) {
            $("#dynamic_creation_form_upload_file").remove();
        }
        $('<div id="dynamic_creation_form_upload_file"><input type="file" id="hidden-FileUploadForm" name="hidden-FileUploadForm" accept="'+accept+'" style="width:1px;height:1px;border:0;margin:-1px;overflow:hidden;"></div>').appendTo(document.body);
        $("#hidden-FileUploadForm").off("change");
        $("#hidden-FileUploadForm").val("");
        $("#hidden-FileUploadForm").on("change",this.runFileUpload);
        $("#hidden-FileUploadForm").click();
    }
    this.doUploadPost=function(xhr,formdata){
        xhr.onreadystatechange = $.fn.FileUploadUtil.onreadystatechange;
        xhr.open("POST", $.fn.FileUploadUtil.settings.upUrl);
        var appJSGlobAjax = new AppJSGlobAjax();
        xhr.setRequestHeader("Postman-Token",appJSGlobAjax.getSessionStorageItem("userToken"));
        xhr.send(formdata);
    }
    this.runFileUpload= function () {
        var files = this.files;
        var uploadfile = files[0];
        if (uploadfile.size > 5*1024*1024){
            alert("图片太大，最大支持5M");
            return;
        }
        var outtime = outtime || 15000;
        $.fn.FileUploadUtil.xhr = new XMLHttpRequest();
        var formdata = new FormData();
        formdata.append("uploadtype", "1");//表示上传的是文件
        formdata.append("version", "v1.0");
        if ($.fn.FileUploadUtil.settings.scale==undefined ||
            $.fn.FileUploadUtil.settings.scale==true){
            formdata.append("scale", 1);
        }else{
            formdata.append("scale", 0);
        }
        formdata.append("uploadfile", uploadfile);
        var appendJson = ($.fn.FileUploadUtil.settings.jsonFunction)? $.fn.FileUploadUtil.settings.jsonFunction($.fn.FileUploadUtil.settings.eventId):{};
        formdata.append("appendJson", appendJson);
        $.fn.FileUploadUtil.doUploadPost($.fn.FileUploadUtil.xhr,formdata);
    }
    this.onreadystatechange = function () {
        if($.fn.FileUploadUtil.xhr.readyState === 4){
            var MsgBox = new messageBox();
            if($.fn.FileUploadUtil.xhr.status === 200){
                var ret = JSON.parse($.fn.FileUploadUtil.xhr.responseText);
                ret.eventId=$.fn.FileUploadUtil.settings.eventId;
                if (ret.code==0 && ret.fileUrl && $.fn.FileUploadUtil.settings.callback){
                    $.fn.FileUploadUtil.settings.callback(ret);
                }else{
                    MsgBox.showMsgBox(ret.msg);
                }
            }else{
                MsgBox.showMsgBox("上传失败，请检查网络");
            }
        }
    }
    $.fn.FileUploadUtil=this;
}