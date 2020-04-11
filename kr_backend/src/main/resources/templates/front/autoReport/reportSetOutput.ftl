<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.22" rel="stylesheet"/>
    <style>
        .tabble-head-1{
            background-color: #cccccc;
        }
        .glyphicon-button{
            font-family: 'Glyphicons Halflings';
            font-style: normal;
            font-weight: 400;
            cursor:pointer
        }
        .table-head-2 {
            background-color: blueviolet;
        }
        .table-col-opt {
            width: 20px;
        }
        .selTableRow {
            background-color: aquamarine;
        }
    </style>
</head>
<body>
<div class="row level0">
    <div class="col-xs-12">
        <div class="box">
            <div class="box-body">
                <table class="table">
                    <thead>
                        <th class="tabble-head-1">数据环境输出</th>
                        <th class="tabble-head-1" style="text-align: right">
                            <div onclick="switchOutSpan(1);">
                                <span id="dataEnvSwitch" class="glyphicon-button glyphicon-triangle-top"></span>
                            </div>
                        </th>
                    </thead>
                    <tbody>
                        <tr id="dataEnvOutSpanHead" class="switchOutSpan">
                            <td width="40%">
                                <button class="btn bg-maroon" data-toggle="button" onclick="saveReportOutput();">保存输出</button>
                            </td>
                        </tr>
                        <tr id="dataEnvOutSpan" class="switchOutSpan">
                            <td width="40%">
                                <table class="table table-bordered table-hover">
                                    <thead>
                                    <th class="table-head-2 table-col-opt"><span class="glyphicon-button glyphicon-tags"/></th>
                                    <th class="table-head-2">循环类型</th>
                                    <th class="table-head-2">SHEET名称</th>
                                    <th class="table-head-2">查询名称</th>
                                    <th class="table-head-2 table-col-opt">
                                        <div onclick="addControlCircle();">
                                            <span class="glyphicon-button glyphicon-plus"></span>
                                        </div>
                                    </th>
                                    </thead>
                                    <tbody id="controlCircleContainer">
                                    </tbody>
                                </table>
                            </td>
                            <td width="60%">
                                <table class="table table-bordered table-hover">
                                    <thead>
                                    <th class="table-head-2 table-col-opt"><span class="glyphicon-button glyphicon-tags"/></th>
                                    <th class="table-head-2">输出位置</th>
                                    <th class="table-head-2">数据类型</th>
                                    <th class="table-head-2">变量/字段</th>
                                    <th class="table-head-2">格式化</th>
                                    <th class="table-head-2 table-col-opt">
                                        <div onclick="addCircleItem();">
                                            <span class="glyphicon-button glyphicon-plus"></span>
                                        </div>
                                    </th>
                                    </thead>
                                    <tbody id="circleItemContainer">

                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!-- /.box-body -->
        </div>
        <!-- /.box -->
    </div>
    <!-- /.col -->
</div>
<!-- /.row -->
<!-- 弹出窗 项目新增，更改-->
<#include "./reportCircleModify.ftl">
<#include "./reportCircleItemModify.ftl">
<!-- 弹出窗结束 -->
<script>
    var reportObject = new Object();
    var postman_token="doccca78-002b-4608-afst-assaassddd";

    function refreshSpan(buutonSpan) {
        if (buutonSpan.is(":visible")){
            buutonSpan.addClass("glyphicon-triangle-top");
            buutonSpan.removeClass("glyphicon-triangle-bottom");
        }
        else{
            buutonSpan.removeClass("glyphicon-triangle-top");
            buutonSpan.addClass("glyphicon-triangle-bottom");
        }
    }
    function switchOutSpan(switchType) {
        if (switchType==1){
            $("#dataEnvOutSpan").toggle();
            refreshSpan($("#dataEnvSwitch"));
        }
    }
    function addControlCircle() {
        ReportCircleModify.initCircle($("#controlCircleContainer"),null);
        $('#reportCircleModify').modal({backdrop: 'static', keyboard: false});
    }

    function onControlCircleSelectChanged(index){
        $("#circleItemContainer").empty();
        if (index>=0 && index<reportObject.reportContrlObj.length){
            var circle=reportObject.reportContrlObj[index];
            ReportCircleItemModify.circle=circle;
            if (circle.circleItemList==undefined || circle.circleItemList==null ||
                circle.circleItemList=='')
                circle.circleItemList=[];
            for(var ind=0;ind<circle.circleItemList.length;ind++){
                var circleItem=circle.circleItemList[ind];
                ReportCircleItemModify.addControlCircleItem(circleItem,false);
            }
            ReportCircleItemModify.loadDynaFunctions();
        }
    }
    function onEditControlCircle(index) {
        var circle=reportObject.reportContrlObj[index];
        ReportCircleModify.initCircle($("#controlCircleContainer"),circle);
        $('#reportCircleModify').modal({backdrop: 'static', keyboard: false});
    }

    function onDeleteControlCircle(index) {
        if (index>=0 && index<reportObject.reportContrlObj.length){
            var circle=reportObject.reportContrlObj[index];
            msgbox.showComfireBox("确认循环["+circle.circleItemTypeName+"]定义吗？该循环下所有明细也将被删除！",index,function (index){
                $("#controlCircleContainer").find(".tableRow").eq(index).remove();
                reportObject.reportContrlObj.splice(index,1);
                if (reportObject.reportContrlObj.length>0){
                    ReportCircleModify.setCircleSelect($("#controlCircleContainer"),0);
                }else{
                    $("#circleItemContainer").empty();
                }
            });
        }
    }

    function addCircleItem(){
        var cueSel=ReportCircleModify.getCircleSelect($("#controlCircleContainer"));
        if (cueSel>=0){
            ReportCircleItemModify.initCircleItem($("#circleItemContainer"),reportObject.reportContrlObj[cueSel],-1,reportObject);
            $('#reportCircleItemModify').modal({backdrop: 'static', keyboard: false});
        }
    }

    function onCircleItemEdit(circle,index){
        if (index!=undefined && index>=0 && circle!=undefined && circle!=null && circle.circleItemList!=undefined &&
            circle.circleItemList.length>index){
            ReportCircleItemModify.initCircleItem($("#circleItemContainer"),circle,index,reportObject);
            $('#reportCircleItemModify').modal({backdrop: 'static', keyboard: false});
        }
    }

    function onCircleItemDelete(circle,index){
        if (index!=undefined && index>=0 && circle!=undefined && circle!=null && circle.circleItemList!=undefined &&
                circle.circleItemList.length>index){
            $("#circleItemContainer").find(".tableRow").eq(index).remove();
            circle.circleItemList.splice(index,1);
        }
    }
    function collectData(){
        var collect_data = {
            reportCode: reportObject.reportCode,
            reportContrlObj:reportObject.reportContrlObj
        };
        return JSON.stringify(collect_data);
    }

    function saveReportOutput(){
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReportSetOutput/saveReportOutput";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            msgbox.showMsgBox(content.msg);
        });
    }

    function queryReportDataQuerysForOption(){
        reportObject.reportCode='${map.reportCode!""}';
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReportSetOutput/queryReportDataQuerysForOption";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            onReportOptionsLoad(content);
        });
    }

    function onReportOptionsLoad(content){
        if (content.code==0){
            reportObject.reportContrlObj=JSON.parse(content.OutputControl);
            ReportCircleModify.initCircleCallback(reportObject,onControlCircleSelectChanged,
                onEditControlCircle,onDeleteControlCircle);
            ReportCircleModify.circleContainer=$("#controlCircleContainer");
            ReportCircleItemModify.circleItemContainer=$("#circleItemContainer");
            ReportCircleItemModify.initCircleItemCallback(onCircleItemEdit,onCircleItemDelete);

            if (reportObject.reportContrlObj!=undefined && reportObject.reportContrlObj!=null
                && reportObject.reportContrlObj!=''){
                for(var i=0;i<reportObject.reportContrlObj.length;i++){
                    var controlCtircle=reportObject.reportContrlObj[i];
                    ReportCircleModify.addControlCircle(controlCtircle,true);
                }
                if (reportObject.reportContrlObj.length>0){
                    ReportCircleModify.setCircleSelect(ReportCircleModify.circleContainer,0);
                }
            }
            reportObject.CircleQueryOption=content.CircleQueryOption;
            reportObject.sheetNamesOption=content.sheetNamesOption;
            reportObject.LibQueryResKeyWordOption=content.LibQueryResKeyWordOption;
            reportObject.LibDataOutputFmtINTOption=content.LibDataOutputFmtINTOption;
            reportObject.LibDataOutputFmtFLOATOption=content.LibDataOutputFmtFLOATOption;
            reportObject.LibDataOutputFmtDATEOption=content.LibDataOutputFmtDATEOption;
            reportObject.envDataMap=new Map();
            if (content.envDataItemList!=undefined && content.envDataItemList!=null &&
                    content.envDataItemList.length>0){
                for(var i=0;i<content.envDataItemList.length;i++){
                    var dataItem=content.envDataItemList[i];
                    reportObject.envDataMap.set(dataItem.dataItemOption.key,
                        dataItem.dataItemType.key);
                }
            }
            reportObject.queryColsMap=new Map();
            if (content.queryColsList!=undefined && content.queryColsList!=null &&
                content.queryColsList.length>0){
                for(var i=0;i<content.queryColsList.length;i++){
                    var queryColOption=content.queryColsOptionList[i];
                    var queryItem=content.queryColsList[i];
                    var queryCode=queryItem.key;
                    var queryColList=queryItem.value;
                    if (queryColList!=undefined && queryColList!=null && queryColList.length>0){
                        var colMap=new Map();
                        for(var x=0;x<queryColList.length;x++){
                            var col=queryColList[x];
                            colMap.set(col.key,col.value);
                        }
                        colMap.set("[Select_Option]",queryColOption.value);
                        reportObject.queryColsMap.set(queryCode,colMap);
                    }
                }
            }
        }else
            msgbox.showMsgBox(content.msg);
    }

    $(function(){
        queryReportDataQuerysForOption();
    });
</script>
</body>
</html>