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
                        <td width="8%"><strong>连接代码：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="连接代码" id="connCode" style="width: 90%;"></td>
                        <td width="8%"><strong>连接名称：</strong></td>
                        <td width="25%"><input type="text" class="form-control"
                                   placeholder="连接名称" id="connName" style="width: 90%;"></td>
                        <td width="8%"><strong>数据库类型：</strong></td>
                        <td width="25%">
                            <div class="input-group" style="width: 90%;">
                                <select id="dbType" class="select2 form-control">
                                    <option value="" selected="selected">全部</option>
                                    ${map.LibDBConnTypeOption!''}
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
                            <button type="button" class="btn bg-blue" onclick="add_DBConn()">新增</button>
                            <button type="button" class="btn bg-blue" onclick="search_me()">查询</button>
                            <button type="button" class="btn bg-maroon" onclick="dbconnsExport()">导出</button>
                        </li>
                    </ul>
                </div>
                <table id="merDBConnectDefTable" class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th>连接代码</th>
                        <th>连接名称</th>
                        <th>数据库类型</th>
                        <th>数据库IP</th>
                        <th>端口号</th>
                        <th>数据库名称</th>
                        <th>连接状态</th>
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
<#include "./merchantDBConnModify.ftl">
<!-- 弹出窗结束 -->
<script>
    var optionStat = 0;
    function selectState(option) {
        optionStat = option;
        table.ajax.reload();
    }
    function search_me() {
        table.ajax.reload();
    }
    function dbconnsExport() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/exportMerchantReportConnDefs";
        var data = JSON.stringify({ connCode:$('#connCode').val(),
                                    connName:$('#connName').val(),
                                    dbType:$('#dbType').val(),
                                    connState:optionStat});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0){
                    window.location.href=content.excelFileUrl;
                }else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    var modReportDBConn = null;
    //新增项目,弹出模态编界面
    function add_DBConn() {
        modReportDBConn = null;
        $('#merchantDBConnModify').modal({backdrop: 'static', keyboard: false});
    }
    var dt=new AppJSGlobDataTable();
    table= $('#merDBConnectDefTable').DataTable({
        "iDisplayLength": 10,
        "sAjaxSource": "/autoReport/loadMerchantReportConnDefs",
        "sServerMethod": "POST",
        "sScrollY" : 520,
        "fnServerParams": function ( aoData ) {
            aoData.push(
                { "name":"connCode","value":$('#connCode').val()},
                { "name":"connName","value":$('#connName').val()},
                { "name":"dbType","value":$('#dbType').val()},
                { "name":"connState","value":optionStat}
            );
        },
        "aoColumns": [
            { "mData": "id" },
            { "mData": "connCode" },
            { "mData": "connName" },
            { "mData": "dbType" },
            { "mData": "dbIp" },
            { "mData": "dbPort" },
            { "mData": "dbSchema" },
            { "mData": "connState" }],
        "oLanguage": dt.dataTableNormalSet("${envData.staticRoot!''}"),
        "columnDefs": [
            {
                "targets": [0],
                "render": function(data, type, full) {
                    return dt.tableAppendMenuFunction('操作',[
                        {funcName:'modifyConn',funcText:'编辑连接',funcId:full.id}]);
                }
            },{
                "targets" : [3],
                "data" : "dbType",
                "render" : function(data, type,full)
                {
                    return (new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibDBConnType}'),'未知');
                }
            },{
                "targets" : [7],
                "data" : "connState",
                "render" : function(data, type,full)
                {
                    var functionHtml=null;
                    if (data==0||data==2)
                        functionHtml=[{funcName:'updateReportConnStateValid',funcText:'设为有效',funcId:full.id},
                            {funcName:'updateReportConnStateCancel',funcText:'删除报表',funcId:full.id}];
                    else if (data==1)
                        functionHtml=[{funcName:'updateReportConnStatePaush',funcText:'设为暂停',funcId:full.id},
                            {funcName:'updateReportConnStateCancel',funcText:'删除报表',funcId:full.id}];
                    var html=(new AppJSGlobUtil()).parasColKeyValList(data,
                        JSON.parse('${map.LibReportDBConnState}'),'未知');
                    if (functionHtml!=null)
                        html=dt.tableAppendMenuFunction('设置',functionHtml)+html;
                    return html;
                }
            }]
    });

    $("#merchantDBConnModify").on('show.bs.modal',function(){
        if (modReportDBConn!=undefined && modReportDBConn!=null)
        {
            $('#merchantDBConnModify_connCode').attr('disabled','disabled');
            $('#merchantDBConnModify_connCode').val(modReportDBConn.connCode);
            $('#merchantDBConnModify_connName').val(modReportDBConn.connName);
            $('#merchantDBConnModify_dbType').val(modReportDBConn.dbType);
            $('#merchantDBConnModify_dbIp').val(modReportDBConn.dbIp);
            $('#merchantDBConnModify_dbPort').val(modReportDBConn.dbPort);
            $('#merchantDBConnModify_dbSchema').val(modReportDBConn.dbSchema);
            $('#merchantDBConnModify_dbUser').val(modReportDBConn.dbUser);
            $('#merchantDBConnModify_dbPassword').val(modReportDBConn.dbPassword);
        }
        else{
            $('#merchantDBConnModify_connCode').removeAttr("disabled");
            $('#merchantDBConnModify_connCode').val('');
            $('#merchantDBConnModify_connName').val('');
            $('#merchantDBConnModify_dbType').val('');
            $('#merchantDBConnModify_dbIp').val('');
            $('#merchantDBConnModify_dbPort').val('');
            $('#merchantDBConnModify_dbSchema').val('');
            $('#merchantDBConnModify_dbUser').val('');
            $('#merchantDBConnModify_dbPassword').val('');
        }
    });

    function showReportDBConnDialg(id,dialog) {
        modReportDBConn = null;
        if (id==undefined  || id==null || id<=0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/queryReportDBConnDefine";
        var data = JSON.stringify({id: id});
        ajga.AjaxApplicationJson(url,data,function (content) {
            if (content){
                if (content.code==0)
                {
                    modReportDBConn = content.entity;
                    dialog.modal({backdrop: 'static', keyboard: false});
                }
                else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    function modifyConn(id) {
        showReportDBConnDialg(id,$('#merchantDBConnModify'));
    }

    function updateDBConnState(id,newState) {
        if (id==undefined  || id==null || id<=0 ||
            newState==undefined ||newState==null ||newState<0)
            return;
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/updateDBConnState";
        var data = JSON.stringify({id: id,connState: newState});
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

    function updateReportConnStateValid(id){
        updateDBConnState(id,1);
    }
    function updateReportConnStatePaush(id){
        updateDBConnState(id,2);
    }
    function updateReportConnStateCancel(id){
        updateDBConnState(id,9);
    }
</script>
</body>
</html>