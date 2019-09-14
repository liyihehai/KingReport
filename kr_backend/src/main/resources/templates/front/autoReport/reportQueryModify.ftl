<div class="modal" id="reportQueryModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">商户数据连接定义编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="reportQueryModify_queryCode" class="col-sm-3 control-label">查询代码*</label>
                        <div class="col-sm-8">
                            <input id="reportQueryModify_queryCode" class="form-control" value="" placeholder="查询代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_queryName" class="col-sm-3 control-label">查询名称*</label>
                        <aside class="col-sm-8">
                            <input id="reportQueryModify_queryName" class="form-control" value="" placeholder="查询名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_queryType" class="col-sm-3 control-label">查询类型*</label>
                        <aside class="col-sm-8">
                            <select id="reportQueryModify_queryType" class="select2 form-control">
                                ${map.LibReportQueryTypeOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_reportId" class="col-sm-3 control-label">所属报表</label>
                        <aside class="col-sm-8">
                            <select id="reportQueryModify_reportId" class="select2 form-control">
                                ${map.LibMerchantReportOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_connId" class="col-sm-3 control-label">数据连接</label>
                        <aside class="col-sm-8">
                            <select id="reportQueryModify_connId" class="select2 form-control">
                                ${map.LibReportDBConnOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_maxRowCount" class="col-sm-3 control-label">最大行数</label>
                        <aside class="col-sm-8">
                            <input id="reportQueryModify_maxRowCount" class="form-control" value="" placeholder="最大行数"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_cutFlag" class="col-sm-3 control-label">分割标志*</label>
                        <aside class="col-sm-8">
                            <select id="reportQueryModify_cutFlag" class="select2 form-control">
                                ${map.LibReportQueryCutFlagOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportQueryModify_cutTypeName" class="col-sm-3 control-label">分割名称</label>
                        <aside class="col-sm-8">
                            <input id="reportQueryModify_cutTypeName" class="form-control" value="" placeholder="分割名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="reportQueryModify_onSaveReportQuery();">保存报表查询</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </aside>
                    </div>
                </form>
            </div>
            <!--
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
            -->
        </div>
    </div>
</div>
<script>
    var postman_token="doccca78-002b-4608-afst-assaassddd";
    function collectData(){
        var collect_data = {
            queryCode: $('#reportQueryModify_queryCode').val(),
            queryName: $('#reportQueryModify_queryName').val(),
            queryType: $('#reportQueryModify_queryType').val(),
            reportId: $('#reportQueryModify_reportId').val(),
            connId: $('#reportQueryModify_connId').val(),
            maxRowCount: $('#reportQueryModify_maxRowCount').val(),
            cutFlag: $('#reportQueryModify_cutFlag').val(),
            cutTypeName: $('#reportQueryModify_cutTypeName').val()
        };
        return JSON.stringify(collect_data);
    }
    function reportQueryModify_onSaveReportQuery() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveReportQuery";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            msgbox.showMsgBox(content.msg);
        });
    }
</script>