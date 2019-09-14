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
                        <td width="8%"><strong>查询代码：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="查询代码" id="queryCode" style="width: 90%;"></td>
                        <td width="8%"><strong>查询名称：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="查询名称" id="queryName" style="width: 90%;"></td>
                        <td width="8%"><strong>查询类型：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="queryType" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportQueryTypeOption!''}
                                </select>
                            </div>
                        </td>
                    </tr>
                    <tr height="50px">
                        <td width="8%"><strong>所属报表：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="reportId" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibMerchantReportOption!''}
                                </select>
                            </div>
                        </td>
                        <td width="8%"><strong>数据连接：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="reportId" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportDBConnOption!''}
                                </select>
                            </div>
                        </td>
                        <td width="8%"><strong>分割标志：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="cutFlag" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibReportQueryCutFlagOption!''}
                                </select>
                            </div>
                        </td>
                    </tr>
                </table>
                <div class="nav-tabs-custom" style="cursor: move;">
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 100%;">
                        <li style="float: right;">
                            <button type="button" class="btn bg-blue" onclick="add_Query()">新增</button>
                            <button type="button" class="btn bg-blue" onclick="search_me()">查询</button>
                            <button type="button" class="btn bg-maroon" onclick="queryExport()">导出</button>
                        </li>
                    </ul>
                </div>
                <table id="merReportQueryTable" class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th>查询代码</th>
                        <th>查询名称</th>
                        <th>查询类型</th>
                        <th>报表名称</th>
                        <th>连接名称</th>
                        <th>最大行数</th>
                        <th>分割标志</th>
                        <th>分割名称</th>
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
<#include "./reportQueryModify.ftl">
<!-- 弹出窗结束 -->
<script>
    function search_me() {
        table.ajax.reload();
    }
    function queryExport() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/exportMerchantReporQuerys";
        var data = JSON.stringify({ queryCode:$('#queryCode').val(),
                                    queryName:$('#queryName').val(),
                                    queryType:$('#queryType').val(),
                                    reportId:$('#reportId').val(),
                                    connId:$('#connId').val(),
                                    cutFlag:$('#cutFlag').val()});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0){
                    window.location.href=content.excelFileUrl;
                }else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    var modReportQuery = null;
    var LibReportQueryType=JSON.parse('${map.LibReportQueryType}');
    var LibMerchantReport=JSON.parse('${map.LibMerchantReport}');
    var LibReportDBConn=JSON.parse('${map.LibReportDBConn}');
    var LibReportQueryCutFlag=JSON.parse('${map.LibReportQueryCutFlag}');
    function dataKey2Val(data,lib) {
        return (new AppJSGlobUtil()).parasColKeyValList(data,lib,'未知')
    }
    //新增项目,弹出模态编界面
    function add_Query() {
        modReportQuery = null;
        $('#reportQueryModify').modal({backdrop: 'static', keyboard: false});
    }
    var dt=new AppJSGlobDataTable();
    table= $('#merReportQueryTable').DataTable({
        "iDisplayLength": 10,
        "sAjaxSource": "/autoReport/loadMerchantReportQuerys",
        "sServerMethod": "POST",
        "sScrollY" : 520,
        "fnServerParams": function ( aoData ) {
            aoData.push(
                { "name":"queryCode","value":$('#queryCode').val()},
                { "name":"queryName","value":$('#queryName').val()},
                { "name":"queryType","value":$('#queryType').val()},
                { "name":"reportId","value":$('#reportId').val()},
                { "name":"connId","value":$('#connId').val()},
                { "name":"cutFlag","value":$('#cutFlag').val()}
            );
        },
        "aoColumns": [
            { "mData": "id" },
            { "mData": "queryCode" },
            { "mData": "queryName" },
            { "mData": "queryType" },
            { "mData": "reportId" },
            { "mData": "connId" },
            { "mData": "maxRowCount" },
            { "mData": "cutFlag" },
            { "mData": "cutTypeName" }],
        "oLanguage": dt.dataTableNormalSet("${envData.staticRoot!''}"),
        "columnDefs": [
            {
                "targets": [0],
                "render": function(data, type, full) {
                    return dt.tableAppendMenuFunction('操作',[
                        {funcName:'modifyQuery',funcText:'编辑查询',funcId:full.id},
                        {funcName:'setQueryBody',funcText:'设置查询体',funcId:full.id},
                        {funcName:'deleteQuery',funcText:'删除查询',funcId:full.id}]);
                }
            },{
                "targets" : [3],
                "data" : "queryType",
                "render" : function(data, type,full)
                {
                    return dataKey2Val(data,LibReportQueryType);
                }
            },{
                "targets" : [4],
                "data" : "reportId",
                "render" : function(data, type,full)
                {
                    return dataKey2Val(data,LibMerchantReport);
                }
            },{
                "targets" : [5],
                "data" : "connId",
                "render" : function(data, type,full)
                {
                    return dataKey2Val(data,LibReportDBConn);
                }
            },{
                "targets" : [7],
                "data" : "cutFlag",
                "render" : function(data, type,full)
                {
                    return dataKey2Val(data,LibReportQueryCutFlag);
                }
            }]
    });

    $("#reportQueryModify").on('show.bs.modal',function(){
        if (modReportQuery!=undefined && modReportQuery!=null)
        {
            $('#reportQueryModify_queryCode').attr('disabled','disabled');
            $('#reportQueryModify_queryCode').val(modReportQuery.queryCode);
            $('#reportQueryModify_queryName').val(modReportQuery.queryName);
            $('#reportQueryModify_queryType').val(modReportQuery.queryType);
            $('#reportQueryModify_reportId').val(modReportQuery.reportId);
            $('#reportQueryModify_connId').val(modReportQuery.connId);
            $('#reportQueryModify_maxRowCount').val(modReportQuery.maxRowCount);
            $('#reportQueryModify_cutFlag').val(modReportQuery.cutFlag);
            $('#reportQueryModify_cutTypeName').val(modReportQuery.cutTypeName);
        }
        else{
            $('#reportQueryModify_queryCode').removeAttr("disabled");
            $('#reportQueryModify_queryCode').val('');
            $('#reportQueryModify_queryName').val('');
            $('#reportQueryModify_queryType').val('');
            $('#reportQueryModify_reportId').val('');
            $('#reportQueryModify_connId').val('');
            $('#reportQueryModify_maxRowCount').val('2000');
            $('#reportQueryModify_cutFlag').val('');
            $('#reportQueryModify_cutTypeName').val('');
        }
    });

    function showReportQueryDialg(id,dialog) {
        modReportQuery = null;
        if (id==undefined  || id==null || id<=0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/queryReportQueryDefine";
        var data = JSON.stringify({id: id});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    modReportQuery = content.entity;
                    dialog.modal({backdrop: 'static', keyboard: false});
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    function modifyQuery(id) {
        showReportQueryDialg(id,$('#reportQueryModify'));
    }
    function setQueryBody(id){
        modReportQuery = null;
        if (id==undefined  || id==null || id<=0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/queryReportQueryDefine";
        var data = JSON.stringify({id: id});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    modReportQuery = content.entity;
                    msgbox.showUrlPage("/autoReport/reportQueryBodySet?id="+id,function () {
                        $('#reportQueryBodySet_queryCode').val(modReportQuery.queryCode);
                        $('#reportQueryBodySet_queryName').val(modReportQuery.queryName);
                        $('#reportQueryBodySet_queryType').val(dataKey2Val(modReportQuery.queryType,LibReportQueryType));
                        $('#reportQueryBodySet_reportId').val(dataKey2Val(modReportQuery.reportId,LibMerchantReport));
                        $('#reportQueryBodySet_connId').val(dataKey2Val(modReportQuery.connId,LibReportDBConn));
                        $('#reportQueryBodySet_maxRowCount').val(modReportQuery.maxRowCount);
                        $('#reportQueryBodySet_querySql').val(modReportQuery.querySql);
                        var jsonCols=JSON.parse(modReportQuery.querySqlCols);
                        reportQueryBodySet_setBodyColTable(jsonCols);
                    });
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
</script>
</body>
</html>