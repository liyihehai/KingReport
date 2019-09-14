<div class="modal" id="merchantReportDefineModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">商户报表定义编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="merchantReportDefineModify_reportCode" class="col-sm-3 control-label">报表代码</label>
                        <div class="col-sm-8">
                            <input id="merchantReportDefineModify_reportCode" class="form-control" value="" placeholder="输入报表代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_reportName" class="col-sm-3 control-label">报表名称</label>
                        <aside class="col-sm-8">
                            <input id="merchantReportDefineModify_reportName" class="form-control" value="" placeholder="输入报表名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_reportClass" class="col-sm-3 control-label">报表分割</label>
                        <aside class="col-sm-8">
                            <select id="merchantReportDefineModify_reportClass" class="select2 form-control">
                                ${map.LibReportClassOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_reportYWType" class="col-sm-3 control-label">业务分类</label>
                        <aside class="col-sm-8">
                            <select id="merchantReportDefineModify_reportYWType" class="select2 form-control">
                                ${map.LibReportBusiTypeOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_reportPeriod" class="col-sm-3 control-label">报表周期</label>
                        <aside class="col-sm-8">
                            <select id="merchantReportDefineModify_reportPeriod" class="select2 form-control">
                                ${map.LibReportPeriodOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_StartDate" class="col-sm-3 control-label">起始日期</label>
                        <aside class="col-sm-8">
                            <div class='input-group date' id="merchantReportDefineModify_datetimepicker">
                                <input type='text' id='merchantReportDefineModify_StartDate' class="form-control"
                                       value="" placeholder="输入报表开始计算的日期" readonly="readonly"/>
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-calendar"></span>
                                </span>
                            </div>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantReportDefineModify_connPassword" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="merchantReportDefineModify_onSaveReportRec();">保存报表定义</button>
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
    var dp=new AppJSGlobDatePicker();
    dp.setDatePickerParams($('#merchantReportDefineModify_datetimepicker'),true);

    var postman_token="doccca78-002b-4608-afst-assaassddd";
    function collectData(){
        var collect_data = {
            reportCode: $('#merchantReportDefineModify_reportCode').val(),
            reportName: $('#merchantReportDefineModify_reportName').val(),
            reportClass: $('#merchantReportDefineModify_reportClass').val(),
            reportYWType: $('#merchantReportDefineModify_reportYWType').val(),
            reportPeriod: $('#merchantReportDefineModify_reportPeriod').val(),
            startDate: $('#merchantReportDefineModify_StartDate').val()
        };
        return JSON.stringify(collect_data);
    }
    var funcOnSucceed=function (content){
        if (content.suc && content.code=='1001')
            msgbox.showMsgBox("报表定义保存成功");
        else
            msgbox.showMsgBox(content.msg);
    }
    function merchantReportDefineModify_onSaveReportRec() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveMerchantReportDefine";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,funcOnSucceed);
    }
</script>