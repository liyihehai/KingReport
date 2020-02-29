<div class="modal" id="reportCircleItemModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">输出循环明细编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
					<div class="form-group">
                        <label for="reportCircleItemModify_cellPoint" class="col-sm-3 control-label">输出位置</label>
                        <aside class="col-sm-8">
                            <input id="reportCircleItemModify_cellPoint" class="form-control" value="" placeholder="输出位置"/>
                        </aside>
                    </div>
					<div class="form-group">
                        <label for="reportCircleItemModify_dataType" class="col-sm-3 control-label">数据类型</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleItemModify_dataType" class="select2 form-control">
                                ${map.LibCircleItemDataTypeOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportCircleItemModify_outText" class="col-sm-3 control-label">变量/字段</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleItemModify_outText" class="select2 form-control">
                            </select>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportCircleItemModify_format" class="col-sm-3 control-label">输出格式</label>
                        <aside class="col-sm-8">
                            <input id="reportCircleItemModify_format" class="form-control" value="" placeholder="输出格式"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="ReportCircleItemModify.saveCircleItem();">保存循环</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </aside>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    var reportCircleItemModify = function (){
        this.circleItemContainer=null;
        this.circle=null;
        this.circleItem=null;
        this.onCircleItemEdit=null;
        this.onCircleItemDelete=null;
        this.initCircleItemCallback=function (onEdit,onDelete) {
            this.onCircleItemEdit=onEdit;
            this.onCircleItemDelete=onDelete;
        }
        this.initCircleItem=function (container,circle,circleItem) {
            this.circleItemContainer=container;
            this.circle=circle;
            this.circleItem=circleItem;
            if (circleItem==null){
                $("#reportCircleItemModify_cellPoint").val("");
                $("#reportCircleItemModify_dataType").val(-1);
                $("#reportCircleItemModify_outText").val(-1);
                $("#reportCircleItemModify_format").val("");
            }else{
                $("#reportCircleItemModify_cellPoint").val(circleItem.cellPoint);
                $("#reportCircleItemModify_dataType").val(circleItem.dataType);
                $("#reportCircleItemModify_outText").val(circleItem.outText);
                $("#reportCircleItemModify_format").val(circleItem.format);
            }
        }
        this.addControlCircleItem=function(circleItem,isAddData){
            var item='<tr class="tableRow">';
            item+='<td>'+circleItem.cellPoint+'</td>';
            item+='<td>'+circleItem.dataTypeName+'</td>';
            item+='<td>'+circleItem.outText+'</td>';
            item+='<td>'+circleItem.format+'</td>';
            item+='<td><span class="glyphicon-button glyphicon-pencil circleItemEdit"></span><span class="glyphicon-button glyphicon-trash circleItemDelete"></span></td>';
            item+='</tr>';
            this.circleItemContainer.append(item);
            if (isAddData) {
                this.circle.circleItemList.push(circleItem);
                this.loadDynaFunctions();
            }
        }
        this.loadDynaFunctions=function () {
            $(".circleItemEdit").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleItemModify.onCircleItemEdit!=null)
                    ReportCircleItemModify.onCircleItemEdit(index);
            });
            $(".circleItemDelete").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleItemModify.onCircleItemDelete!=null)
                    ReportCircleItemModify.onCircleItemDelete(index);
            });
        }
		this.saveCircleItem=function() {
            if (this.circleItem==null){
                //如果是新增操作
                var newCircleItem={};
                newCircleItem.cellPoint=$("#reportCircleItemModify_cellPoint").val();
                newCircleItem.dataType=$("#reportCircleModify_circleItemType").val();
                newCircleItem.dataTypeName=$("#reportCircleModify_circleItemType").find("option:selected").text();
                newCircleItem.outText=$("#reportCircleItemModify_outText").val();
                newCircleItem.format=$("#reportCircleItemModify_format").val();
                this.addControlCircleItem(newCircleItem,true);
            }else{
                //如果是编辑操作
            }
		}
	}
	var ReportCircleItemModify=new reportCircleItemModify();
</script>