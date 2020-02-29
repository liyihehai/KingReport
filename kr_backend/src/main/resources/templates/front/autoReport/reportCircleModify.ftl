<div class="modal" id="reportCircleModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">报表输出循环编辑</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
					<div class="form-group">
                        <label for="reportCircleModify_circleItemType" class="col-sm-3 control-label">循环类型*</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleModify_circleItemType" class="select2 form-control">
                                ${map.LibControlCircleItemTypeOption!''}
                            </select>
                        </aside>
                    </div>
					<div class="form-group">
                        <label for="reportCircleModify_sheetName" class="col-sm-3 control-label">SHEET名称*</label>
                        <aside class="col-sm-8">
                            <input id="reportCircleModify_sheetName" class="form-control" value="" placeholder="查询名称"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="reportCircleModify_queryCode" class="col-sm-3 control-label">查询代码*</label>
                        <div class="col-sm-8">
                            <input id="reportCircleModify_queryCode" class="form-control" value="" placeholder="查询代码"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="ReportCircleModify.saveCircle();">保存循环</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </aside>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    var reportCircleModify = function (){
        this.controlObject=null;    //输出控制的数据对象
        this.circleContainer=null;
        this.controlCircle=null;    //当前操作的循环
        this.onCircleSelectChanged=null;
        this.onCircleEdit=null;
        this.onCircleDelete=null;
        this.initCircleCallback=function (dataObject,selectChanged,onEdit,onDelete) {
            this.controlObject=dataObject;
            this.onCircleSelectChanged=selectChanged;
            this.onCircleEdit=onEdit;
            this.onCircleDelete=onDelete;
        }
        this.initCircle=function (container,circle) {
            this.circleContainer=container;
            this.controlCircle=circle;
            if (circle==null){
                $("#reportCircleModify_circleItemType").val(-1);
                $("#reportCircleModify_sheetName").val("");
                $("#reportCircleModify_queryCode").val("");
            }else{
                $("#reportCircleModify_circleItemType").val(circle.circleItemType);
                $("#reportCircleModify_sheetName").val(circle.sheetName);
                $("#reportCircleModify_queryCode").val(circle.queryCode);
            }
        }
        this.addControlCircle=function(circle){
            var item='<tr class="tableRow">';
            item+='<td>'+circle.circleItemTypeName+'</td>';
            item+='<td>'+circle.sheetName+'</td>';
            item+='<td>'+circle.queryCode+'</td>';
            item+='<td><span class="glyphicon-button glyphicon-pencil circleEdit"></span><span class="glyphicon-button glyphicon-trash circleDelete"></span></td>';
            item+='</tr>';
            this.circleContainer.append(item);
            this.controlObject.push(circle);
            this.loadDynaFunctions();
            this.setCircleSelect(this.circleContainer,this.controlObject.length);
        }
        this.updateControlCircle=function(circle){
            var index=this.getCircleSelect(this.circleContainer);
            if (index>=0){
                this.controlObject[index]=circle;
                var selTr=$(this.circleContainer).find("tr").eq(index);
                var tds=$(selTr).find("td");
                tds.eq(0).text(circle.circleItemTypeName);
                tds.eq(1).text(circle.sheetName);
                tds.eq(2).text(circle.queryCode);
            }
        }
        this.loadDynaFunctions=function () {
            $(".tableRow").off("click").on("click",function (event) {
                var container=$(event.currentTarget).parents("#controlCircleContainer");
                $("#"+container.attr("id")+" > .tableRow").removeClass("selTableRow");
                $(event.currentTarget).addClass("selTableRow");
                var ind=ReportCircleModify.getCircleSelect(container);
                if (ReportCircleModify.onCircleSelectChanged!=null)
                    ReportCircleModify.onCircleSelectChanged(ind);
            });
            $(".circleEdit").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleModify.onCircleEdit!=null)
                    ReportCircleModify.onCircleEdit(index);
            });
            $(".circleDelete").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleModify.onCircleDelete!=null)
                    ReportCircleModify.onCircleDelete(index);
            });
        }
        this.getCircleSelect=function(container){
            if (container){
                var curSelTr=$("#"+container.attr("id")+" > .selTableRow").eq(0);
                if (curSelTr!=undefined){
                    return $(container).find("tr").index(curSelTr);
                }
            }
            return -1;
        }
        this.setCircleSelect=function (container,index) {
            if (index>0){
                var curIndex=this.getCircleSelect(container);
                var ind=index-1;
                if (curIndex<0 || (curIndex>=0 && curIndex!=ind)){
                    this.controlCircle=this.controlObject[ind];
                    var selTr=$(container).find("tr").eq(ind);
                    $(container).find("tr").each(function () {
                        $(this).removeClass("selTableRow");
                    });
                    selTr.addClass("selTableRow");
                    if (this.onCircleSelectChanged!=null)
                        this.onCircleSelectChanged(ind);
                }
            }
        }
		this.saveCircle=function() {
            var doCircle={};
            doCircle.circleItemType=$("#reportCircleModify_circleItemType").val();
            doCircle.circleItemTypeName=$("#reportCircleModify_circleItemType").find("option:selected").text();
            doCircle.sheetName=$("#reportCircleModify_sheetName").val();
            doCircle.queryCode=$("#reportCircleModify_queryCode").val();
            if (this.controlCircle==null){
                //如果是新增操作
                doCircle.circleItemList=[];
                this.addControlCircle(doCircle);
            }else{
                //如果是编辑操作
                this.updateControlCircle(doCircle);
            }
		}
	}
	var ReportCircleModify=new reportCircleModify();
</script>