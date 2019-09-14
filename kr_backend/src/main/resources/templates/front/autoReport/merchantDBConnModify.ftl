<div class="modal" id="merchantDBConnModify">
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
                        <label for="merchantDBConnModify_connCode" class="col-sm-3 control-label">连接代码*</label>
                        <div class="col-sm-8">
                            <input id="merchantDBConnModify_connCode" class="form-control" value="" placeholder="输入连接代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_connName" class="col-sm-3 control-label">连接名称*</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_connName" class="form-control" value="" placeholder="连接名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbType" class="col-sm-3 control-label">数据库类型*</label>
                        <aside class="col-sm-8">
                            <select id="merchantDBConnModify_dbType" class="select2 form-control">
                                ${map.LibDBConnTypeOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbIp" class="col-sm-3 control-label">数据库IP</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_dbIp" class="form-control" value="" placeholder="数据库IP地址"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbPort" class="col-sm-3 control-label">数据库端口</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_dbPort" class="form-control" value="" placeholder="数据库IP地址端口"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbSchema" class="col-sm-3 control-label">数据库名称*</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_dbSchema" class="form-control" value="" placeholder="数据库名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbUser" class="col-sm-3 control-label">数据库用户名</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_dbUser" class="form-control" value="" placeholder="数据库用户名"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="merchantDBConnModify_dbPassword" class="col-sm-3 control-label">数据库密码</label>
                        <aside class="col-sm-8">
                            <input id="merchantDBConnModify_dbPassword" class="form-control" value="" placeholder="数据库密码"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="merchantDBConnModify_onSaveReportDBconn();">保存连接定义</button>
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
            connCode: $('#merchantDBConnModify_connCode').val(),
            connName: $('#merchantDBConnModify_connName').val(),
            dbType: $('#merchantDBConnModify_dbType').val(),
            dbIp: $('#merchantDBConnModify_dbIp').val(),
            dbPort: $('#merchantDBConnModify_dbPort').val(),
            dbSchema: $('#merchantDBConnModify_dbSchema').val(),
            dbUser: $('#merchantDBConnModify_dbUser').val(),
            dbPassword: $('#merchantDBConnModify_dbPassword').val()
        };
        return JSON.stringify(collect_data);
    }
    function merchantDBConnModify_onSaveReportDBconn() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveReportDBconn";
        var data = collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            msgbox.showMsgBox(content.msg);
        });
    }
</script>