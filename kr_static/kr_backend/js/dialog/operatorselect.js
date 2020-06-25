var OperatorSelect= {
    queryContent : function(data,onOperatorSelected){
        var url = "/global/dialog/operatorSelect";
        $.extend(data,{title:"选择操作员"},data||{});
        UrlDialog.queryContent(url,data,onOperatorSelected);
    }
}