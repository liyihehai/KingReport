var MerchantSetting = {
    getMerchantStateName:function (state){
        //-1下架  0未认证  1可服务 2暂停服务
        if (state==undefined || state==null || state<-1 || state>2)
            return '未知';
        else if (state == -1)
            return '下架';
        else if (state == 0)
            return '未认证';
        else if (state == 1)
            return '可服务';
        else if (state == 2)
            return '暂停服务';
    },
    getTemplatePath:function () {
        return glob_JS_Static_Host+"/js/front/merchant/setting/merchantSetting.html";
    },
    mountActions:function(){
        var tradeLib = new TradeLib();
        var globalCtrl = new GlobalCtrl();
        $("#merchantDetail-pmBusiType").html(globalCtrl.loadLibOption(tradeLib.libMap));
    },
    selectTab:function(tabId){
        $(".merchantDetail-tab").hide();
        $("."+tabId).show();
    },
    setMerchantInfo:function (merchant,merchantExpand) {
        $("#merchantDetail-pmName").val(merchant.pmName);
        $("#merchantDetail-pmCompanyPerson").val(merchant.pmCompanyPerson);
        /*
        $("#merchantDetail-applyWays").val(merchant.applyWays);
        $("#merchantDetail-applyerName").val(merchant.applyerName);
        $("#merchantDetail-applyMemo").val(merchant.applyMemo);
        $("#merchantDetail-applyState").val(merchant.applyState);
        $("#merchantDetail-opeName").val(merchant.opeName);
        $("#merchantDetail-lockTime").val(merchant.lockTime);
        $("#merchantDetail-checkerCode").val(merchant.checkerCode);
        $("#merchantDetail-checkerName").val(merchant.checkerName);
        $("#merchantDetail-checkTime").val(merchant.checkTime);
        $(":radio[name='merchantDetail-confirmType'][value='"+merchant.confirmType+"']").prop('checked',true);
        this.setVerifyInput(merchant.confirmType);
        if (!merchantExpand)
            merchantExpand = new Object();
        else
            merchantExpand = JSON.parse(merchantExpand);*/
        $("#merchantDetail-pmEmail").val((merchantExpand.pmEmail)?merchantExpand.pmEmail:"");
        MerchantSetting.mountActions();
        var cityselutil = new citySelUtil();
        cityselutil.initSelUtil(2,cityData3,$("#merchantDetail-pmProvince"),merchantExpand.pmProvince,
            $("#merchantDetail-pmCity"),merchantExpand.pmCity,
            $("#merchantDetail-pmArea"),merchantExpand.pmArea);
        $("#merchantDetail-pmBusiType").val((merchantExpand.pmBusiType)?merchantExpand.pmBusiType:"");
        $("#merchantDetail-pmCountry").val((merchantExpand.pmCountry)?merchantExpand.pmCountry:"");
        $("#merchantDetail-pmAddress").val((merchantExpand.pmAddress)?merchantExpand.pmAddress:"");
        $("#merchantDetail-pmZipcode").val((merchantExpand.pmZipcode)?merchantExpand.pmZipcode:"");
        $("#merchantDetail-pmCoordinate").val((merchantExpand.pmCoordinate)?merchantExpand.pmCoordinate:"");
        $("#merchantDetail-pmLongitude").val((merchantExpand.pmLongitude)?merchantExpand.pmLongitude:"");
        $("#merchantDetail-pmLatitude").val((merchantExpand.pmLatitude)?merchantExpand.pmLatitude:"");
        $("#merchantDetail-pmLinkName").val((merchantExpand.pmLinkName)?merchantExpand.pmLinkName:"");
        $("#merchantDetail-pmLinkPhone").val((merchantExpand.pmLinkPhone)?merchantExpand.pmLinkPhone:"");
        $("#merchantDetail-pmCsrPhone").val((merchantExpand.pmCsrPhone)?merchantExpand.pmCsrPhone:"");
        $("#merchantDetail-pmIntroduce").val((merchantExpand.pmIntroduce)?merchantExpand.pmIntroduce:"");
        $("#merchantDetail-pm_logo_img").attr("src",(merchantExpand.pmLogo)?merchantExpand.pmLogo:"");
        $("#merchantDetail-pmPic1").attr("src",(merchantExpand.pmPic1)?merchantExpand.pmPic1:glob_JS_Static_Host+"/images/custom/addImg.png");
        $("#merchantDetail-pmPic2").attr("src",(merchantExpand.pmPic2)?merchantExpand.pmPic2:glob_JS_Static_Host+"/images/custom/addImg.png");
        $("#merchantDetail-pmPic3").attr("src",(merchantExpand.pmPic3)?merchantExpand.pmPic3:glob_JS_Static_Host+"/images/custom/addImg.png");
        if (merchant.pmCompanyPerson==1){
            $("#merchantDetail-title-pmLegalName").html("法人姓名");
            $("#merchantDetail-title-pmLegalIdNum").html("工商登记号");
            $("#merchantDetail-title-pmCertificatePic1").html("工商登记证1");
            $("#merchantDetail-title-pmCertificatePic2").html("工商登记证2");
        }else{
            $("#merchantDetail-title-pmLegalName").html("个人姓名");
            $("#merchantDetail-title-pmLegalIdNum").html("身份证号");
            $("#merchantDetail-title-pmCertificatePic1").html("身份证正面");
            $("#merchantDetail-title-pmCertificatePic2").html("身份证反面");
        }
        $("#merchantDetail-pmLegalName").val((merchantExpand.pmLegalName)?merchantExpand.pmLegalName:"");
        $("#merchantDetail-pmLegalIdNum").val((merchantExpand.pmLegalIdNum)?merchantExpand.pmLegalIdNum:"");
        $("#merchantDetail-pmCertificatePic1").attr("src",(merchantExpand.pmCertificatePic1)?merchantExpand.pmCertificatePic1:glob_JS_Static_Host+"/images/custom/addImg.png");
        $("#merchantDetail-pmCertificatePic2").attr("src",(merchantExpand.pmCertificatePic2)?merchantExpand.pmCertificatePic2:glob_JS_Static_Host+"/images/custom/addImg.png");
        $("#merchantDetail-pmCertificatePic3").attr("src",(merchantExpand.pmCertificatePic3)?merchantExpand.pmCertificatePic3:glob_JS_Static_Host+"/images/custom/addImg.png");
        $("#merchantDetail-pmCertificatePic4").attr("src",(merchantExpand.pmCertificatePic4)?merchantExpand.pmCertificatePic4:glob_JS_Static_Host+"/images/custom/addImg.png");

    },
    onContentLoad:function (params) {
        var ajga=new AppJSGlobAjax();
        var url="/merchant/merchantSetting/queryMerchantDetail";
        var collect_data = {id: params.id};
        var queryData = JSON.stringify(collect_data);
        ajga.AjaxApplicationJson(url,queryData,function (content){
            if (content.code==0){
                MerchantSetting.mountActions();
                MerchantSetting.setMerchantInfo(content.merchant,content.merchantExpand);
                ImgViewDialog.setImgClickView();
            }else
                msgbox.showMsgBox(content.msg);
        });
        /*
        if (GlobalUtil.isNotEmpty(params)){
            MerchantSetting.setMerchantInfo(params.merchant,params.merchantExpand);
        }*/
    }
}