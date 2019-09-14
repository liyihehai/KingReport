<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.2" rel="stylesheet"/>
    <style>
        .table_label_col{
            width: 150px;
            vertical-align: middle;
            text-align: right;
            background-color: #9b9999;
            font-size:medium;
        }
        .table_right_col{
            text-align: right;
        }
        .table tbody tr td{
            vertical-align: middle;
        }
    </style>
</head>
<body>
<div class="row level0">
    <div class="col-xs-12">
        <div class="box">
            <div class="box-body">
                <p class="well">
                    <span class="label label-primary">商户名称</span><span> ${map.envData.loginMerchant.merchantName!''}</span>
                </p>
                <div class="nav-tabs-custom" style="cursor: move;">
                    <!-- Tabs within a box -->
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 50%;">
                        <li class="active"><a href="#" data-toggle="tab" aria-expanded="true"
                                              onclick="setReportPeriod()">报表周期</a></li>
                        <li><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="setReportBusiType()">业务分类</a></li>
                    </ul>
                </div>
            </div>
            <div class="box-body">
                <div id="reportPeriod" class="panel panel-default">
                    <div class="panel-body">
                        <table class="table">
                            <tr>
                                <td class="table_label_col">日报表截止时间</td>
                                <td><input id="reportParamMgr_endTimeDay" class="form-control" value="${map.rps.endTimeDay}" placeholder="指定日报表的每天结束时间[00:00:00.000~23:59:59.999(默认)]"/></td>
                            </tr>
                            <tr>
                                <td class="table_label_col">周报表截止星期</td>
                                <td><input id="reportParamMgr_endTimeWeek" class="form-control" value="${map.rps.endTimeWeek!''}" placeholder="指定周报表的结束星期数[1~7(默认)]"/></td>
                            </tr>
                            <tr>
                                <td class="table_label_col">月报表截止日期</td>
                                <td><input id="reportParamMgr_endTimeMonth" class="form-control" value="${map.rps.endTimeMonth!''}" placeholder="指定月报表的结束日数[1~28或0]（默认0,每月的最后一天)"/></td>
                            </tr>
                            <tr>
                                <td class="table_label_col">季报表截止日期</td>
                                <td><input id="reportParamMgr_endTimeQuarter" class="form-control" value="${map.rps.endTimeQuarter!''}" placeholder="指定季度报表的结束月日[01-01~03-31（默认)]"/></td>
                            </tr>
                            <tr>
                                <td class="table_label_col">半年报截止日期</td>
                                <td><input id="reportParamMgr_endTimeHalfYear" class="form-control" value="${map.rps.endTimeHalfYear!''}" placeholder="指定半年报表的结束月日[01-01~06-30（默认)]"/></td>
                            </tr>
                            <tr>
                                <td class="table_label_col">年报截止日期</td>
                                <td><input id="reportParamMgr_endTimeYear" class="form-control" value="${map.rps.endTimeYear!''}" placeholder="指定年报表的结束月日[01-01~12-31（默认)]"/></td>
                            </tr>
                        </table>
                    </div>
                    <div class="panel-footer" style="text-align: center;">
                        <input type="button" class="btn btn-primary" onclick="saveMerchantPeriodSetting()" value="保存周期参数">
                        <input type="button" class="btn btn-default" onclick="defaultMerchantPeriodSetting()" value="设为默认值">
                    </div>
                </div>
                <div id="reportBusiType" class="panel panel-default" style="display: none">
                    <div class="panel-heading" style="text-align: center;">
                        <table width="100%">
                            <tr>
                                <td>报表业务分类列表</td>
                                <td class="table_right_col">
                                    <input type="button" class="btn btn-primary" onclick="addParamBusiType();" value="增加分类">
                                    <input type="button" class="btn btn-primary" onclick="refreshBusiTypeTable();" value="刷新列表">
                                </td>
                            </tr>
                        </table>
                    </div>
                    <table id="merReportQueryTable" class="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th>操作</th>
                            <th>分类代码</th>
                            <th>分类名称</th>
                            <th>分类状态</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <!-- /.box-body -->
        </div>
        <!-- /.box -->
    </div>
    <!-- /.col -->
</div>
<!-- /.row -->
<!-- 弹出窗 项目新增，更改-->
<#include "./reportParamBusiTypeModify.ftl">
<!-- 弹出窗结束 -->
<script>
    var postman_token="doccca78-002b-4608-afst-assaassddd";
    var modParamBusiType = null;
    var tablepack=new tablePack();
    function setReportPeriod() {
        $("#reportPeriod").show();
        $("#reportBusiType").hide();
    }
    function setReportBusiType() {
        tablepack.initLocalTable($("#merReportQueryTable"));
        $("#reportPeriod").hide();
        $("#reportBusiType").show();
    }
    function refreshBusiTypeTable() {
        if (tablepack.table!=null)
            tablepack.table.ajax.reload();
    }

    function dataKey2Val(data,lib) {
        return (new AppJSGlobUtil()).parasColKeyValList(data,lib,'未知')
    }
    function tablePack() {
        this.table=null;
        this.isTableInit = function (tableObj) {
            return tableObj.attr("init")=="1";
        }
        this.setTableInit = function (tableObj) {
            tableObj.attr("init","1");
        }
        this.initLocalTable = function (tableObj) {
            if (!this.isTableInit(tableObj)){
                this.setTableInit(tableObj);
                var LibBusiTypeState=JSON.parse('${map.LibBusiTypeState}');
                var dt=new AppJSGlobDataTable();
                this.table= tableObj.DataTable({
                    "iDisplayLength": 10,
                    "sAjaxSource": "/autoReport/loadReportParamBusiTypes",
                    "sServerMethod": "POST",
                    "sScrollY" : 480,
                    "fnServerParams": function ( aoData ) {
                        aoData.push(
                            { "name":"parMerchantId","value":$('#parMerchantId').val()},
                            { "name":"busiTypeCode","value":$('#busiTypeCode').val()},
                            { "name":"busiTypeName","value":$('#busiTypeName').val()},
                            { "name":"busiTypeState","value":$('#busiTypeState').val()}
                        );
                    },
                    "aoColumns": [
                        { "mData": "parMerchantId" },
                        { "mData": "busiTypeCode" },
                        { "mData": "busiTypeName" },
                        { "mData": "busiTypeState" }],
                    "oLanguage": dt.dataTableNormalSet("${envData.staticRoot!''}"),
                    "columnDefs": [
                        {
                            "targets": [0],
                            "render": function(data, type, full) {
                                var opes=[
                                    {funcName:'modifyBusiType',funcText:'编辑分类',funcId:"'"+full.busiTypeCode+"'"},
                                    {funcName:'deleteBusiType',funcText:'删除分类',funcId:"'"+full.busiTypeCode+"'"}];
                                if (full.busiTypeState==1){
                                    opes.push({funcName:'stateBusiTypeInvalid',funcText:'设为无效',funcId:"'"+full.busiTypeCode+"'"});
                                }else{
                                    opes.push({funcName:'stateBusiTypeValid',funcText:'设为有效',funcId:"'"+full.busiTypeCode+"'"});
                                }
                                return dt.tableAppendMenuFunction('操作',opes);
                            }
                        },{
                            "targets" : [3],
                            "data" : "queryType",
                            "render" : function(data, type,full)
                            {
                                return dataKey2Val(data,LibBusiTypeState);
                            }
                        }]
                });
            }
        }
    }

    function collectData(){
        var collect_data = {
            endTimeDay: $('#reportParamMgr_endTimeDay').val(),
            endTimeWeek: $('#reportParamMgr_endTimeWeek').val(),
            endTimeMonth: $('#reportParamMgr_endTimeMonth').val(),
            endTimeQuarter: $('#reportParamMgr_endTimeQuarter').val(),
            endTimeHalfYear: $('#reportParamMgr_endTimeHalfYear').val(),
            endTimeYear: $('#reportParamMgr_endTimeYear').val()
        };
        return JSON.stringify(collect_data);
    }

    function saveMerchantPeriodSetting() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveMerchantPeriodSetting";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            msgbox.showMsgBox(content.msg);
        });
    }
    function defaultMerchantPeriodSetting() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/defaultMerchantPeriodSetting";
        var data = [];
        ajga.AjaxApplicationJson(url,data,function (content){
            if(content.settings){
                $('#reportParamMgr_endTimeDay').val(content.settings.endTimeDay);
                $('#reportParamMgr_endTimeWeek').val(content.settings.endTimeWeek);
                $('#reportParamMgr_endTimeMonth').val(content.settings.endTimeMonth);
                $('#reportParamMgr_endTimeQuarter').val(content.settings.endTimeQuarter);
                $('#reportParamMgr_endTimeHalfYear').val(content.settings.endTimeHalfYear);
                $('#reportParamMgr_endTimeYear').val(content.settings.endTimeYear);
            }
        });
    }

    $("#reportParamBusiTypeModify").on('show.bs.modal',function(){
        if (modParamBusiType!=undefined && modParamBusiType!=null)
        {
            $('#reportParamBusiTypeModify_busiTypeCode').attr('disabled','disabled');
            $('#reportParamBusiTypeModify_busiTypeCode').val(modParamBusiType.busiTypeCode);
            $('#reportParamBusiTypeModify_busiTypeName').val(modParamBusiType.busiTypeName);
        }
        else{
            $('#reportParamBusiTypeModify_busiTypeCode').removeAttr("disabled");
            $('#reportParamBusiTypeModify_busiTypeCode').val('');
            $('#reportParamBusiTypeModify_busiTypeName').val('');
        }
    });

    function addParamBusiType() {
        modParamBusiType = null;
        $('#reportParamBusiTypeModify').modal({backdrop: 'static', keyboard: false});
    }

    function modifyBusiType(code) {
        showParamBusiTypeDialg(code,$('#reportParamBusiTypeModify'));
    }

    function showParamBusiTypeDialg(code,dialog) {
        modParamBusiType = null;
        if (code==undefined  || code==null || code<=0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/queryParamBusiType";
        var data = JSON.stringify({busiTypeCode: code});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    modParamBusiType = content.entity;
                    dialog.modal({backdrop: 'static', keyboard: false});
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }

    function deleteBusiType(code) {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/deleteParamBusiType";
        var data = JSON.stringify({busiTypeCode: code});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                msgbox.showMsgBox(content.msg);
                refreshBusiTypeTable();
            }
        });
    }
    function stateBusiTypeInvalid(code) {
        stateBusiType(code,0);
    }
    function stateBusiTypeValid(code) {
        stateBusiType(code,1);
    }
    function stateBusiType(code,state) {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/stateParamBusiType";
        var data = JSON.stringify({ busiTypeCode: code,
                                    busiTypeState:state});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                msgbox.showMsgBox(content.msg);
                refreshBusiTypeTable();
            }
        });
    }
</script>
</body>
</html>