<div class="modal" id="reportTemplateModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">报表记录编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="reportTemplateModify_reportCode" class="col-sm-3 control-label">报表代码</label>
                        <div class="col-sm-8">
                            <input id="reportTemplateModify_reportCode" class="form-control" value="" placeholder="输入报表代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="reportTemplateModify_reportName" class="col-sm-3 control-label">报表名称</label>
                        <aside class="col-sm-8">
                            <input id="reportTemplateModify_reportName" class="form-control" value="" placeholder="输入报表名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportTemplateModify_selTemplate" class="col-sm-3 control-label">选择模板</label>
                        <aside class="col-sm-8">
                            <select id="reportTemplateModify_selTemplate" class="select2 form-control">
                                <option value=""></option>
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportTemplateModify_upReportTemplateFile" class="col-sm-3 control-label">上传模板</label>
                        <aside class="col-sm-8">
                            <input id="reportTemplateModify_upReportTemplateFile" name="ReportTemplateFile" type="file" class="file" data-show-preview="false">
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="onSaveReportTemplate();">保存报表模板</button>
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

    $('#reportTemplateModify_upReportTemplateFile').fileinput({
        theme: 'fas',
        language: 'zh',
        uploadUrl: '/autoReport/uploadReportTemplateFile',
        showCancel: false,
        uploadExtraData: function () {
            return {reportCode:$('#reportTemplateModify_reportCode').val()};
        },
        allowedFileExtensions: ['xlxt', 'xltx']
    });

    $('#reportTemplateModify_upReportTemplateFile').on('fileuploaded',function (event, data, previewId, index) {
        if(data== undefined || data == null ||
            data.response== undefined || data.response== null){
            msgbox.showMsgBox("文件上传异常！");
            $('.fileinput-remove-button').click();
            return false;
        }
        if (data.response.code==undefined||data.response.code==null||
            data.response.code!='0'){
            msgbox.showMsgBox("文件上传失败："+data.response.msg);
            $('.fileinput-remove-button').click();
            return false;
        }else{
            setReportTemplateSelect(data.response.templateFiles,
                $('#reportTemplateModify_selTemplate'),data.response.templateFile);
        }
    });

    function onSaveReportTemplate() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveReportTemplate";
        var data = JSON.stringify({reportCode:$('#reportTemplateModify_reportCode').val(),
            templateFile:$('#reportTemplateModify_selTemplate').val()});
        ajga.AjaxApplicationJson(url,data,function (content) {
            msgbox.showMsgBox(content.msg);
        });
    }
</script>