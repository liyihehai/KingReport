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
                        <label for="reportCircleItemModify_circleTypeName" class="col-sm-3 control-label">循环类型</label>
                        <aside class="col-sm-8">
                            <input id="reportCircleItemModify_circleTypeName" class="form-control" value="" readonly="readonly"/>
                        </aside>
                    </div>
                    <div class="form-group noNormalText">
                        <label for="reportCircleItemModify_outText" class="col-sm-3 control-label">变量/字段</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleItemModify_outText" class="select2 form-control" onchange="ReportCircleItemModify.onItemOutTextChange();">
                            </select>
                        </aside>
                    </div>
					<div class="form-group">
                        <label for="reportCircleItemModify_dataType" class="col-sm-3 control-label">数据类型</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleItemModify_dataType" class="select2 form-control"
                                    onchange="ReportCircleItemModify.onItemDataTypeChange();" disabled="disabled">
                                ${map.LibCircleItemDataTypeOption!''}
                            </select>
                        </aside>
                    </div>
                    <div class="form-group noNormalText">
                        <label for="reportCircleItemModify_format" class="col-sm-3 control-label">输出格式</label>
                        <aside class="col-sm-8">
                            <select id="reportCircleItemModify_format" class="select2 form-control">
                            </select>
                        </aside>
                    </div>
                    <div class="form-group normalText">
                        <label for="reportCircleItemModify_normalTextBody" class="col-sm-3 control-label">文本内容</label>
                        <aside class="col-sm-8">
                            <input id="reportCircleItemModify_normalTextBody" class="form-control" value="" placeholder="文本内容"/>
                        </aside>
                    </div>
                    <div class="form-group">
                        <label for="" class="col-sm-3 control-label"></label>
                        <aside class="col-sm-8">
                            <button class="btn bg-maroon" data-toggle="button" onclick="ReportCircleItemModify.saveCircleItem();" data-dismiss="modal">保存循环单元</button>
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
        this.circleItemIndex=-1;
        this.onCircleItemEdit=null;
        this.onCircleItemDelete=null;
        this.reportObject=null;
        this.initCircleItemCallback=function (onEdit,onDelete) {
            this.onCircleItemEdit=onEdit;
            this.onCircleItemDelete=onDelete;
        }
        this.getCurCircleItem=function(){
            if (this.circleItemIndex>=0 && this.circle.circleItemList.length>this.circleItemIndex)
                return this.circle.circleItemList[this.circleItemIndex];
            return null;
        }
        this.setCurCircleItemFormat=function(){
            var item = this.getCurCircleItem();
            if (item !=null){
                $("#reportCircleItemModify_format").val(item.format);
            }
        }
        this.setDataTypeSel=function(selText){
            $("#reportCircleItemModify_dataType").attr("disabled",false);
            $("#reportCircleItemModify_dataType").val(selText);
            $("#reportCircleItemModify_dataType").attr("disabled",true);
            this.refreshFormatOptions(selText);
            this.setCurCircleItemFormat();
        }
        this.initCircleItem=function (container,circle,circleItemIndex,reportObject) {
            this.circleItemContainer=container;
            this.circle=circle;
            this.circleItemIndex=circleItemIndex;
            this.reportObject=reportObject;
            $("#reportCircleItemModify_circleTypeName").val(this.circle.circleItemTypeName);
            $("#reportCircleItemModify_outText").html('');
            if (this.circle.circleItemType=='CIT_NormalTxt'){
                $('.normalText').show();
                $('.noNormalText').hide();
                this.setDataTypeSel("DATA_STRING");
            }else{
                $('.normalText').hide();
                $('.noNormalText').show();
                if (this.circle.circleItemType=='CIT_EnvData'){
                    $("#reportCircleItemModify_outText").html(reportObject.LibQueryResKeyWordOption);
                }else if (this.circle.circleItemType=='CIT_QueryFeild'){
                    if (this.reportObject.queryColsMap!=undefined && this.reportObject.queryColsMap!=null){
                        var colMap=this.reportObject.queryColsMap.get(circle.queryCode);
                        if (colMap!=undefined && colMap!=null){
                            $("#reportCircleItemModify_outText").html(colMap.get("[Select_Option]"));
                        }
                    }
                }
            }
            if (circleItemIndex<0){
                $("#reportCircleItemModify_cellPoint").val("");
                $("#reportCircleItemModify_outText").val(-1);
                $("#reportCircleItemModify_format").val("");
                $("#reportCircleItemModify_normalTextBody").val("");
                $("#reportCircleItemModify_dataType").val(-1);
                if (circle!=null && circle.circleItemType=='CIT_NormalTxt'){
                    $("#reportCircleItemModify_dataType").val("DATA_STRING");
                }
            }else{
                var circleItem = circle.circleItemList[circleItemIndex];
                $("#reportCircleItemModify_cellPoint").val(circleItem.cellPoint);
                this.setDataTypeSel(circleItem.dataType);
                if (circle.circleItemType!='CIT_NormalTxt')
                    $("#reportCircleItemModify_outText").val(circleItem.outText);
                else
                    $("#reportCircleItemModify_normalTextBody").val(circleItem.outText);
                $("#reportCircleItemModify_format").val(circleItem.format);
            }
        }
        this.addControlCircleItem=function(circleItem,isAddData){
            var jsUtil=new AppJSGlobUtil();
            var item='<tr class="tableRow">';
            item+='<td>'+globalCtrl.iconButtonHtml("circleItemEdit","glyphicon-button glyphicon-pencil")+'</td>';
            item+='<td>'+jsUtil.getS('',circleItem.cellPoint)+'</td>';
            item+='<td>'+jsUtil.getS('',circleItem.dataTypeName)+'</td>';
            item+='<td>'+jsUtil.getS('',circleItem.outText)+'</td>';
            item+='<td>'+jsUtil.getS('',circleItem.formatName)+'</td>';
            item+='<td>'+globalCtrl.iconButtonHtml("circleItemDelete","glyphicon-button glyphicon-trash")+'</td>';
            item+='</tr>';
            this.circleItemContainer.append(item);
            if (isAddData) {
                this.circle.circleItemList.push(circleItem);
                this.loadDynaFunctions();
            }
        }
        this.updateControlCircleItem=function(circleItem,index){
            var jsUtil=new AppJSGlobUtil();
            var selTr=$(this.circleItemContainer).find("tr").eq(index);
            var tds=$(selTr).find("td");
            tds.eq(1).text(jsUtil.getS('',circleItem.cellPoint));
            tds.eq(2).text(jsUtil.getS('',circleItem.dataTypeName));
            tds.eq(3).text(jsUtil.getS('',circleItem.outText));
            tds.eq(4).text(jsUtil.getS('',circleItem.formatName));
        }
        this.loadDynaFunctions=function () {
            $(".circleItemEdit").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleItemModify.onCircleItemEdit!=null)
                    ReportCircleItemModify.onCircleItemEdit(ReportCircleItemModify.circle,index);
            });
            $(".circleItemDelete").off("click").on("click",function (event) {
                var clickTr=$(event.currentTarget).parents('.tableRow');
                var index = clickTr.parent().children("tr").index(clickTr);
                if (ReportCircleItemModify.onCircleItemDelete!=null)
                    ReportCircleItemModify.onCircleItemDelete(ReportCircleItemModify.circle,index);
            });
        }
		this.saveCircleItem=function() {
            if (this.circleItemIndex<0){
                //如果是新增操作
                var newCircleItem={};
                newCircleItem.cellPoint=$("#reportCircleItemModify_cellPoint").val();
                newCircleItem.dataType=$("#reportCircleItemModify_dataType").val();
                newCircleItem.dataTypeName=$("#reportCircleItemModify_dataType").find("option:selected").text();
                if (this.circle.circleItemType!='CIT_NormalTxt')
                    newCircleItem.outText=$("#reportCircleItemModify_outText").val();
                else
                    newCircleItem.outText=$("#reportCircleItemModify_normalTextBody").val();
                newCircleItem.format=$("#reportCircleItemModify_format").val();
                newCircleItem.formatName=$("#reportCircleItemModify_format").find("option:selected").text();
                this.addControlCircleItem(newCircleItem,true);
            }else{
                //如果是编辑操作
                if (this.circleItemIndex>=0 && this.circle.circleItemList.length>this.circleItemIndex){
                    var circleItem = this.circle.circleItemList[this.circleItemIndex];
                    circleItem.cellPoint=$("#reportCircleItemModify_cellPoint").val();
                    circleItem.dataType=$("#reportCircleItemModify_dataType").val();
                    circleItem.dataTypeName=$("#reportCircleItemModify_dataType").find("option:selected").text();
                    if (this.circle.circleItemType!='CIT_NormalTxt')
                        circleItem.outText=$("#reportCircleItemModify_outText").val();
                    else
                        circleItem.outText=$("#reportCircleItemModify_normalTextBody").val();
                    circleItem.format=$("#reportCircleItemModify_format").val();
                    circleItem.formatName=$("#reportCircleItemModify_format").find("option:selected").text();
                    this.updateControlCircleItem(circleItem,this.circleItemIndex);
                }
            }
		}
		this.onItemOutTextChange=function () {
            var outText=$("#reportCircleItemModify_outText").val();
            if (this.circle==undefined || this.circle==null){
                return;
            }
            if (this.circle.circleItemType=='CIT_EnvData'){
                if (this.reportObject.envDataMap!=undefined && this.reportObject.envDataMap!=null){
                    var envDataType=this.reportObject.envDataMap.get(outText);
                    if (envDataType!=undefined && envDataType!=null && envDataType!=''){
                        this.setDataTypeSel(envDataType);
                        return;
                    }
                }
            }else if (this.circle.circleItemType=='CIT_QueryFeild'){
                if (this.reportObject.queryColsMap!=undefined && this.reportObject.queryColsMap!=null){
                    var colMap=this.reportObject.queryColsMap.get(this.circle.queryCode);
                    if (colMap!=undefined && colMap!=null){
                        this.setDataTypeSel(colMap.get(outText));
                        return;
                    }
                }
            }
        }
        this.refreshFormatOptions=function(dataType){
            if (dataType=='DATA_STRING'){
                $("#reportCircleItemModify_format").html("");
            }else if (dataType=='DATA_INT'){
                $("#reportCircleItemModify_format").html(this.reportObject.LibDataOutputFmtINTOption);
            }else if (dataType=='DATA_FLOT'){
                $("#reportCircleItemModify_format").html(this.reportObject.LibDataOutputFmtFLOATOption);
            }else if (dataType=='DATA_DATE'){
                $("#reportCircleItemModify_format").html(this.reportObject.LibDataOutputFmtDATEOption);
            }
        }
		this.onItemDataTypeChange=function(){
            var dataType=$("#reportCircleItemModify_dataType").val();
            $("#reportCircleItemModify_format").val('');
            this.refreshFormatOptions(dataType);
        }
	}
	var ReportCircleItemModify=new reportCircleItemModify();
</script>