<style>
    .form-control {
        width: 230px;
    }
    <#--
.modal-content{
	height: 570px;
}-->
</style>
<div class="col-md-12">
	<div class="box box-danger">
		<div class="box-header with-border">
		    <h3 class="box-title">报表查询体设置(${merchant.merchantName!''}${merchant.storeName!''})</h3>
			<div class="box-tools pull-right">
				<!--<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>-->
				<button class="btn btn-box-tool" data-widget="remove">
					<i class="fa fa-times"></i>
				</button>
			</div>
		</div>
		<div class="box-body chart-responsive">
            <table class="table">
                <tr>
                    <td>查询代码</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_queryCode" readonly="readonly" value="${merchant.bankAccountName!''}">
                    </td>
                    <td>查询名称</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_queryName" readonly="readonly" value="${merchant.companyAddr!''}">
                    </td>
                    <td>查询类型</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_queryType" readonly="readonly" value="${merchant.companyAddr!''}">
                    </td>
                </tr>
                <tr>
                    <td>所属报表</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_reportId" readonly="readonly" value="${merchant.companyAddr!''}">
                    </td>
                    <td>连接名称</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_connId" readonly="readonly" value="${merchant.companyAddr!''}">
                    </td>
                    <td>最大行数</td>
                    <td>
                        <input type="text" class="form-control" id="reportQueryBodySet_maxRowCount" readonly="readonly" value="${merchant.companyAddr!''}">
                    </td>
                </div>
                </tr>
                <tr>
                    <td>查询语句</td>
                    <td colspan="3">
                        <textarea class="form-control" id="reportQueryBodySet_querySql" name="querySql" rows="16" style="min-width: 95%"></textarea>
                    </td>
                    <td>查询结果列</td>
                    <td>
                        <table id="queryColsTable" class="table table-striped table-bordered table-hover" width="100%">
                        </table>
                    </td>
                </tr>
            </table>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-7" ">
                    <input type="button" class="btn btn-default" onclick="saveQueryBody()"value="保存">
                    <input type="button" class="btn btn-default" onclick="backSetIndex();" value="取消">
                </div>
            </div>
        </div>
        </div>
    </div>
</div>
<script>
    $(function () {
        var tableColumns = [
            {field: 'colName', title: '列名称', sortable: false},
            {field: 'colType', title: '类型', sortable: false}];
        var data=[];
    //  $('#queryColsTable').bootstrapTable('destroy');   //动态加载表格之前，先销毁表格
        $("#queryColsTable").bootstrapTable({columns: tableColumns,  //表头
            data:data, //通过ajax返回的数据
            width:200,
            height:350,
            method: 'get',
            pageSize: 150,
            pageNumber: 1,
            pageList: [],
            cache: false,
            striped: true,
            pagination: true,
            sidePagination: 'client',
            search: false,
            showRefresh: false,
            showExport: false,
            showFooter: false,
            clickToSelect: true});
    });

    function onColTableDataChanged(data) {
        if (data)
            $('#queryColsTable').bootstrapTable('load', data);
        else
            $('#queryColsTable').bootstrapTable('destroy');   //动态加载表格之前，先销毁表格
    }

    function reportQueryBodySet_collectData(){
        var collect_data = {
            queryCode: $('#reportQueryBodySet_queryCode').val(),
            querySql: $('#reportQueryBodySet_querySql').val()
        };
        return JSON.stringify(collect_data);
    }

    function reportQueryBodySet_setBodyColTable(JsonCols){
        if (JsonCols && JsonCols.length){
            var cols=[];
            for (var i=0;i<JsonCols.length;i++){
                var col={colName:JsonCols[i].colName,colType:JsonCols[i].dataType};
                cols.push(col);
            }
            onColTableDataChanged(cols);
        }
    }

    function saveQueryBody() {
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/saveQueryBodySet";
        var data = reportQueryBodySet_collectData();
        ajga.AjaxApplicationJson(url,data,function (content){
            if (content && content.code==0){
                reportQueryBodySet_setBodyColTable(content.cols);
            }
            msgbox.showMsgBox(content.msg);
        });
    }
</script>
