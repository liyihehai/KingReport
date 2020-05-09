function LoginModle(){
    this.userName;
    this.password;
    this.onSetPwsFinished=function(code,msg,username) {};
    this.gAjax=new AppJSGlobAjax();
    this.submitLoginInfo=function (userName,password){
        if(!userName){
            msgbox.showMsgBox("请填写用户名")
            return;
        }
        if(!password){
            msgbox.showMsgBox("请填写密码")
            return;
        }
        this.userName=userName;
        this.password=password;
        var data = JSON.stringify({ userName: userName});
        this.gAjax.AjaxApplicationJson("/main/priCheck",data,function (content) {
            if (content){
                if (content.code==0){
                    loginModle.doLogin(content.tmpKey,loginModle.userName,loginModle.password);
                }else
                    msgbox.showMsgBox(content.msg);
            }else
                msgbox.showMsgBox("校验异常");
        });
    }
    this.doLogin=function(tmpKey,name,pwd){
        var srvPwd=pwd;
        var secret = new SecretModel();
        var aimPwd = secret.AES_ECB_encrypt(srvPwd, tmpKey);
        $("#aimPwd").val(aimPwd);
        var url=this.gAjax.getSessionStorageItem("contextPath")+"/main/loginCheck";
        var isRemember=$("#isRemember").is(":checked");
        if (isRemember)
            this.gAjax.setLocalStorageItem("defaultUserName",name);
        else
            this.gAjax.setLocalStorageItem("defaultUserName","");
        $("#loginForm_id").attr("action",url);
        $("#loginForm_id").submit();
    }
    this.submitSetPws=function(userName,password,onSetPwsFinished){
        this.userName=userName;
        this.password=password;
        this.onSetPwsFinished=onSetPwsFinished;
        var data = JSON.stringify({ userName: userName});
        this.gAjax.AjaxApplicationJson("/main/priCheck",data,function (content) {
            if (content){
                if (content.code==0){
                    loginModle.SetPws(content.tmpKey,loginModle.userName,loginModle.password);
                }else
                    loginModle.onSetPwsFinished(content.code,content.msg,loginModle.userName);
            }else
                loginModle.onSetPwsFinished(9999,"校验异常",loginModle.userName);
        });
    }
    this.SetPws=function(tmpKey,name,pwd){
        var srvPwd=pwd;
        var secret = new SecretModel();
        var aimPwd = secret.AES_ECB_encrypt(srvPwd, tmpKey);
        var data = JSON.stringify({ opeCode: name,setAimPwd:aimPwd});
        this.gAjax.AjaxApplicationJson("/operator/setPws",data,function (content) {
            if (content){
                loginModle.onSetPwsFinished(content.code,content.msg,loginModle.userName);
            }else
                loginModle.onSetPwsFinished(9999,"返回异常",loginModle.userName);
        });
    }
}
var loginModle=new LoginModle();