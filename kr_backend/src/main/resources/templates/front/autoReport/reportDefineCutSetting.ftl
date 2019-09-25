<div class="modal" id="reportDefineCutSetting">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">商户报表分割编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="reportDefineCutSetting_reportCode" class="col-sm-3 control-label">报表代码</label>
                        <div class="col-sm-8">
                            <input id="reportDefineCutSetting_reportCode" class="form-control" value=""/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="reportDefineCutSetting_reportName" class="col-sm-3 control-label">报表名称</label>
                        <aside class="col-sm-8">
                            <input id="reportDefineCutSetting_reportName" class="form-control" value=""/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportDefineCutSetting_reportClass" class="col-sm-3 control-label">报表分割</label>
                        <aside class="col-sm-8">
                            <input id="reportDefineCutSetting_reportClass" class="form-control" value=""/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportDefineCutSetting_cutKeyField" class="col-sm-3 control-label">分割KEY字段</label>
                        <aside class="col-sm-8">
                            <select id="reportDefineCutSetting_cutKeyField" class="select2 form-control">
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportDefineCutSetting_cutNameField" class="col-sm-3 control-label">分割NAME字段</label>
                        <aside class="col-sm-8">
                            <select id="reportDefineCutSetting_cutNameField" class="select2 form-control">
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportDefineCutSetting_connPassword" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="reportDefineCutSetting_onSaveReportRec();">保存报表分割设置</button>
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
    function reportDefineCutSetting_collectData(){
        var collect_data = {
            reportCode: $('#reportDefineCutSetting_reportCode').val(),
            reportName: $('#reportDefineCutSetting_reportName').val(),
            reportClass: $('#reportDefineCutSetting_reportClass').val(),
            cutKeyField: $('#reportDefineCutSetting_cutKeyField').val(),
            cutNameField: $('#reportDefineCutSetting_cutNameField').val()
        };
        return JSON.stringify(collect_data);
    }
    var reportDefineCutSetting_funcOnSucceed=function (content){
        if (content.suc && content.code=='1001')
            msgbox.showMsgBox("报表分割定义保存成功");
        else
            msgbox.showMsgBox(content.msg);
    }
    function reportDefineCutSetting_onSaveReportRec() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveReportDefineCutSetting";
        var data = reportDefineCutSetting_collectData();
        ajga.AjaxApplicationJson(url,data,reportDefineCutSetting_funcOnSucceed);
    }
</script>