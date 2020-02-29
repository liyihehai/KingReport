<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.2" rel="stylesheet"/>
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
                        <tr id="dataEnvOutSpan" class="switchOutSpan">
                            <td width="40%">
                                <table class="table table-bordered table-hover">
                                    <thead>
                                    <th class="table-head-2">循环类型</th>
                                    <th class="table-head-2">SHEET名称</th>
                                    <th class="table-head-2">查询代码</th>
                                    <th class="table-head-2">
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
                                    <th class="table-head-2">输出位置</th>
                                    <th class="table-head-2">数据类型</th>
                                    <th class="table-head-2">变量/字段</th>
                                    <th class="table-head-2">格式化</th>
                                    <th class="table-head-2">
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
    var reportContrlObj=[];

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
        if (index>0 && index<reportContrlObj.length){
            var circle=reportContrlObj[index];
            for(var ind=0;ind<circle.circleItemList.length;ind++){
                var circleItem=circle.circleItemList[ind];
                ReportCircleItemModify.addControlCircleItem(circleItem,false);
            }
            ReportCircleItemModify.loadDynaFunctions();
        }
    }
    function onEditControlCircle(index) {
        var circle=reportContrlObj[index];
        ReportCircleModify.initCircle($("#controlCircleContainer"),circle);
        $('#reportCircleModify').modal({backdrop: 'static', keyboard: false});
    }

    function onDeleteControlCircle(index) {
        if (index>=0 && index<reportContrlObj.length){
            var circle=reportContrlObj[index];
            msgbox.showComfireBox("确认循环["+circle.circleItemTypeName+"]定义吗？该循环下所有明细也将被删除！",index,function (index){
                $("#controlCircleContainer").find(".tableRow").eq(index).remove();
                reportContrlObj.splice(index,1);
                if (reportContrlObj.length>0){
                    ReportCircleModify.setCircleSelect($("#controlCircleContainer"),1);
                }
            });
        }
    }

    function addCircleItem(){
        var cueSel=ReportCircleModify.getCircleSelect($("#controlCircleContainer"));
        if (cueSel>=0){
            ReportCircleItemModify.initCircleItem($("#circleItemContainer"),reportContrlObj[cueSel],null);
            $('#reportCircleItemModify').modal({backdrop: 'static', keyboard: false});
        }
    }

    $(function(){
        ReportCircleModify.initCircleCallback(reportContrlObj,onControlCircleSelectChanged,
            onEditControlCircle,onDeleteControlCircle);
    });
</script>
</body>
</html>