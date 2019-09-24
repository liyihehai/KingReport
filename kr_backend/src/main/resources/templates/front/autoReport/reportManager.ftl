<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.2" rel="stylesheet"/>
</head>
<body>
<div class="row level0">
    <div class="col-xs-12">
        <div class="box">
            <div class="box-body">
                <table style="width: 100%; margin-bottom: 20px;">
                    <tr height="50px">
                        <td width="8%"><strong>创建时间：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <input type="text" class="form-control pull-right"
                                       id="createDateRange" placeholder="开始时间－结束时间">
                            </div>
                            <!--/.input group-->
                        </td>
                        <td width="8%"><strong>报表代码：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="报表代码" id="reportCode" style="width: 90%;"></td>
                        <td width="8%"><strong>报表名称：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="报表名称" id="reportName" style="width: 80%;"></td>
                    </tr>
                    <tr>
                        <td width="8%"><strong>报表分割：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="reportClass" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportClassOption!''}
                                </select>
                            </div>
                        </td>
                        <td width="8%"><strong>业务分类：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="reportJTType" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportBusiTypeOption!''}
                                </select>
                            </div>
                        </td>
                        <td width="8%"><strong>报表周期：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="reportPeriod" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportPeriodOption!''}
                                </select>
                            </div>
                        </td>
                    </tr>
                </table>
                <div class="nav-tabs-custom" style="cursor: move;">
                    <!-- Tabs within a box -->
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 50%;">
                        <li class="active"><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="selectState(0)">未启用</a></li>
                        <li><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="selectState(1)">已启用</a></li>
                        <li><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="selectState(2)">已暂停</a></li>
                        <li><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="selectState(9)">已删除</a></li>
                        <li><a href="#" data-toggle="tab" aria-expanded="true"
                               onclick="selectState(-1)">全部</a></li>
                    </ul>
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 50%;">
                        <li style="float: right;">
                            <button type="button" class="btn bg-blue" onclick="add_reprtRec()">新增</button>
                            <button type="button" class="btn bg-blue" onclick="search_me()">查询</button>
                            <button type="button" class="btn bg-maroon" onclick="reportsExport()">导出</button>
                        </li>
                    </ul>
                </div>
                <table id="merReportDefTable" class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th>报表代码</th>
                        <th>报表名称</th>
                        <th>报表分割</th>
                        <th>业务分类</th>
                        <th>报表周期</th>
                        <th>报表状态</th>
                        <th>创建时间</th>
                    </tr>
                    </thead>
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
<#include "./merchantReportDefineModify.ftl">
<#include "./reportTemplateModify.ftl">
<!-- 弹出窗结束 -->
<script>
    var drp=new AppJSGlobDatePicker();
    drp.setDateRangePickerParams($('#createDateRange'),'right',false);
    var optionStat = 0;
    function selectState(option) {
        optionStat = option;
        table.ajax.reload();
    }
    function search_me() {
        table.ajax.reload();
    }
    function reportsExport() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/exportMerchantReportDefs";
        var data = JSON.stringify({ createDateRange: $('#createDateRange').val(),
                                    reportCode:$('#reportCode').val(),
                                    reportName:$('#reportName').val(),
                                    reportClass:$('#reportClass').val(),
                                    reportBusiType:$('#reportJTType').val(),
                                    reportPeriod:$('#reportPeriod').val(),
                                    reportState:optionStat});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0){
                    window.location.href=content.excelFileUrl;
                }else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    var modReport = null;
    //新增项目,弹出模态编界面
    function add_reprtRec() {
        modReport = null;
        $('#merchantReportDefineModify').modal({backdrop: 'static', keyboard: false});
    }
    var dt=new AppJSGlobDataTable();
    table= $('#merReportDefTable').DataTable({
        "iDisplayLength": 10,
        "sAjaxSource": "/autoReport/loadMerchantReportDef",
        "sServerMethod": "POST",
        "sScrollY" : 520,
        "fnServerParams": function ( aoData ) {
            aoData.push(
                { "name":"createDateRange","value":$('#createDateRange').val()},
                { "name":"reportCode","value":$('#reportCode').val()},
                { "name":"reportName","value":$('#reportName').val()},
                { "name":"reportClass","value":$('#reportClass').val()},
                { "name":"reportBusiType","value":$('#reportJTType').val()},
                { "name":"reportPeriod","value":$('#reportPeriod').val()},
                { "name":"reportState","value":optionStat}
            );
        },
        "aoColumns": [
            { "mData": "id" },
            { "mData": "reportCode" },
            { "mData": "reportName" },
            { "mData": "reportClass" },
            { "mData": "reportBusiType" },
            { "mData": "reportPeriod" },
            { "mData": "reportState" },
            { "mData": "createTime" }],
        "oLanguage": dt.dataTableNormalSet("${envData.staticRoot!''}"),
        "columnDefs": [
            {
                "targets": [0],
                "render": function(data, type, full) {
                    return dt.tableAppendMenuFunction('操作',[
                        {funcName:'modifyReport',funcText:'编辑报表',funcId:full.id},
                        {funcName:'setReportTemplate',funcText:'设置模板',funcId:full.id},
                        {funcName:'genNextPeriodReport',funcText:'产生下期报表',funcId:full.id}]);
                }
            },{
                "targets" : [3],
                "data" : "reportClass",
                "render" : function(data, type,full)
                {
                    return (new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibReportClass}'),'未知');
                }
            },{
                "targets" : [4],
                "data" : "reportBusiType",
                "render" : function(data, type,full)
                {
                    return (new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibReportBusiType}'),'未知');
                }
            },{
                "targets" : [5],
                "data" : "reportPeriod",
                "render" : function(data, type,full)
                {
                    return (new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibReportPeriod}'),'未知');
                }
            },{
                "targets" : [6],
                "data" : "reportState",
                "render" : function(data, type,full)
                {
                    var functionHtml=null;
                    if (data==0||data==2)
                        functionHtml=[{funcName:'updateReportStateValid',funcText:'设为有效',funcId:full.id},
                            {funcName:'updateReportStateCancel',funcText:'删除报表',funcId:full.id}];
                    else if (data==1)
                        functionHtml=[{funcName:'updateReportStatePaush',funcText:'设为暂停',funcId:full.id},
                            {funcName:'updateReportStateCancel',funcText:'删除报表',funcId:full.id}];
                    var html=(new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibReportState}'),'未知');
                    if (functionHtml!=null)
                        html=dt.tableAppendMenuFunction('设置',functionHtml)+html;
                    return html;
                }
            }]
    });

    $("#merchantReportDefineModify").on('show.bs.modal',function(){
        if (modReport!=undefined && modReport!=null)
        {
            $('#merchantReportDefineModify_reportCode').attr('disabled','disabled');
            $('#merchantReportDefineModify_reportCode').val(modReport.reportCode);
            $('#merchantReportDefineModify_reportName').val(modReport.reportName);
            $('#merchantReportDefineModify_reportClass').val(modReport.reportClass);
            $('#merchantReportDefineModify_reportYWType').val(modReport.reportBusiType);
            $('#merchantReportDefineModify_reportPeriod').val(modReport.reportPeriod);
            $('#merchantReportDefineModify_StartDate').val((new AppJSGlobUtil()).dateFtt("yyyy-MM-dd",new Date(modReport.startDate)));
        }
        else{
            $('#merchantReportDefineModify_reportCode').removeAttr("disabled");
            $('#merchantReportDefineModify_reportCode').val('');
            $('#merchantReportDefineModify_reportName').val('');
            $('#merchantReportDefineModify_reportClass').val('');
            $('#merchantReportDefineModify_reportYWType').val('');
            $('#merchantReportDefineModify_reportPeriod').val('');
            $('#merchantReportDefineModify_StartDate').val('');
        }
    });

    function setReportTemplateSelect(templateFiles,selectObj,templateFile){
        if (templateFiles!=undefined && templateFiles!=null){
            var selopts = "";
            for (var i=0;i<templateFiles.length;i++){
                selopts+='<option value="'+templateFiles[i]+'">'+templateFiles[i]+'</option>';
            }
            selectObj.html(selopts);
        }
        else
            selectObj.html("");
        selectObj.val(templateFile);
    }

    $("#reportTemplateModify").on('show.bs.modal',function(){
        if (modReport!=undefined && modReport!=null)
        {
            $('#reportTemplateModify_reportCode').attr('disabled','disabled');
            $('#reportTemplateModify_reportCode').val(modReport.reportCode);
            $('#reportTemplateModify_reportName').attr('disabled','disabled');
            $('#reportTemplateModify_reportName').val(modReport.reportName);
            setReportTemplateSelect(modReport.templateFiles,$('#reportTemplateModify_selTemplate'),modReport.templateFile);
        }
        $('.fileinput-remove-button').click();
    })

    function showReportDialg(id,dialog) {
        modReport = null;
        if (id==undefined  || id==null || id<=0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/queryMerchantReportDefine";
        var data = JSON.stringify({id: id});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    modReport = content.merchantReportDefine;
                    modReport.templateFiles = content.templateFiles;
                    dialog.modal({backdrop: 'static', keyboard: false});
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    function modifyReport(id) {
        showReportDialg(id,$('#merchantReportDefineModify'));
    }
    function setReportTemplate(id) {
        showReportDialg(id,$('#reportTemplateModify'));
    }

    function genNextPeriodReport(id){
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/genNextPeriodReport";
        var data = JSON.stringify({id: id});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                msgbox.showMsgBox(content.msg);
            }
        });
    }

    function updateReportState(id,newState) {
        if (id==undefined  || id==null || id<=0 ||
            newState==undefined ||newState==null ||newState<0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/updateReportState";
        var data = JSON.stringify({id: id,reportState: newState});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    msgbox.showMsgBox(content.msg);
                    search_me();
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }

    function updateReportStateValid(id){
        updateReportState(id,1);
    }
    function updateReportStatePaush(id){
        updateReportState(id,2);
    }
    function updateReportStateCancel(id){
        updateReportState(id,9);
    }
    function setReportQuery(id) {
        alert(id);
    }
</script>
</body>
</html>