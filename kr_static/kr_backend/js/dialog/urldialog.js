var UrlDialog= {
    appendData:{},
    tmpId:"",
    pushDataFunction:function(callback){
        if (UrlDialog.tmpId!=undefined && UrlDialog.tmpId!=null && UrlDialog.tmpId!=''){
            var dialog=DialogGlobal.getRegisterDialog(UrlDialog.tmpId);
            if (dialog!=undefined && dialog!=null){
                dialog.getConfirmData=callback;
                UrlDialog.tmpId=null;
            }
        }
    },
    localContent:function(contentBody,callContent,onConfirm){
        var content = contentBody;
        if ((content==undefined || content==null || content=='') && callContent!=undefined && callContent!=null)
            content=callContent();
        if (!content){
            return;
        }
        if (content.code!=0){
            msgbox.showMsgBox(content.msg);
            return;
        }
        var params = {};
        params.content = content.htmlBody;
        if (UrlDialog.appendData.titleLogo){
            params.title = "<span class=\"glyphicon "+UrlDialog.appendData.titleLogo+"\" style=\"top: 2px;\"></span>"
                + "<span style=\"width:100px;font-size:medium;font-weight:600;padding-left:6px;\">"+UrlDialog.appendData.title+"</span>";
        }
        else
            params.title = UrlDialog.appendData.title;
        var size = new Object();
        size.width = "600px";
        size.height = "310px";
        if (UrlDialog.appendData.winSize)
            size = UrlDialog.appendData.winSize;
        var closeable = true;
        if (UrlDialog.appendData.closeable!=undefined && UrlDialog.appendData.closeable!=null)
            closeable = UrlDialog.appendData.closeable;
        UrlDialog.showModal(params,size,closeable,onConfirm);
    },
    queryContent:function(aimUrl,data,onConfirm){
        var ajax=new AppJSGlobAjax();
        var url = aimUrl;
        UrlDialog.appendData = data;
        ajax.AjaxApplicationJson(url,JSON.stringify(UrlDialog.appendData),function (content) {
            UrlDialog.localContent(content,null,onConfirm);
        });
    },
    showModal:function(params, size,isCloseable,onConfirm) {
        var dialog_buttons=[];
        if (onConfirm!=undefined && onConfirm!=null){
            dialog_buttons.push({
                label: '确定',
                action: function (dialog) {
                    if (!GlobalUtil.isEmpty(dialog.getConfirmData)){
                        var confData=dialog.getConfirmData();
                        if (!GlobalUtil.isEmpty(confData)){
                            dialog.close();
                            if (onConfirm){
                                onConfirm(UrlDialog.appendData,confData);
                            }
                        }else
                            msgbox.showMsgBox("未取得可操作的数据!");
                    }else
                        msgbox.showMsgBox("取得可操作的数据回调函数无效!");
                }
            });
        }
        dialog_buttons.push({
            label: '关闭',
            action: function (dialog) {
                dialog.close();
            }
        });
        var dialog = new BootstrapDialog({
            title: params.title,
            message: function () {
                var $message = $('<div>' + params.content + '</div>');
                return $message;
            },
            closable: isCloseable,
            buttons: dialog_buttons,
            onhide:function (dialog) {
                DialogGlobal.unRegisterDialog(dialog.dialogId);
            },
            draggable: true
        });
        dialog.realize();
        DialogGlobal.dialogStyle(dialog,size);
        var appJSGlobUtil =new AppJSGlobUtil();
        UrlDialog.tmpId = appJSGlobUtil.generateUUID();
        dialog.dialogId = UrlDialog.tmpId;
        DialogGlobal.registerDialog(dialog);
        dialog.open();
    }
}