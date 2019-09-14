<div class="modal" id="reportParamBusiTypeModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">商户报表业务分类编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="reportParamBusiTypeModify_busiTypeCode" class="col-sm-3 control-label">业务分类代码*</label>
                        <div class="col-sm-8">
                            <input id="reportParamBusiTypeModify_busiTypeCode" class="form-control" value="" placeholder="业务分类代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="reportParamBusiTypeModify_busiTypeName" class="col-sm-3 control-label">业务分类名称*</label>
                        <aside class="col-sm-8">
                            <input id="reportParamBusiTypeModify_busiTypeName" class="form-control" value="" placeholder="业务分类名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="reportParamBusiTypeModify_onSaveParamBusiType();">保存业务分类</button>
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
    function reportParamBusiTypeModify_collectData(){
        var collect_data = {
            busiTypeCode: $('#reportParamBusiTypeModify_busiTypeCode').val(),
            busiTypeName: $('#reportParamBusiTypeModify_busiTypeName').val()
        };
        return JSON.stringify(collect_data);
    }
    function reportParamBusiTypeModify_onSaveParamBusiType() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/onSaveParamBusiType";
        var data = reportParamBusiTypeModify_collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            msgbox.showMsgBox(content.msg);
        });
    }
</script>