<div class="modal" id="reportCircleModify">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header" style="width: 599px;background:#08c">
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
                            <select id="reportCircleModify_sheetName" class="select2 form-control">
                            </select>
                        </aside>
                    </div>
                    <div class="form-group circleItemType-dataOption" style="display: none">
                        <label for="reportCircleModify_dataOption" class="col-sm-3 control-label">数据选项*</label>
                        <div class="col-sm-8">
                            <input id="reportCircleModify_dataOption" class="form-control" value="" placeholder="数据选项"/>
                        </div>
                    </div>
                    <div class="form-group circleItemType-queryCode">
                        <label for="reportCircleModify_queryCode" class="col-sm-3 control-label">查询代码*</label>
                        <div class="col-sm-8">
                            <select id="reportCircleModify_queryCode" class="select2 form-control">
                            </select>
                        </div>
                    </div>
                    <div class="form-group circleItemType-normalText" style="display: none">
                        <label for="reportCircleModify_normalText" class="col-sm-3 control-label">文本内容*</label>
                        <div class="col-sm-8">
                            <input id="reportCircleModify_normalText" class="form-control" value="" placeholder="文本内容"/>
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
        this.reportObject=null;    //输出控制的数据对象
        this.circleContainer=null;
        this.controlCircle=null;    //当前操作的循环
        this.onCircleSelectChanged=null;
        this.onCircleEdit=null;
        this.onCircleDelete=null;
        this.dialogObj=$("#reportCircleModify");
        this.initCircleCallback=function (reportObject,selectChanged,onEdit,onDelete) {
            this.reportObject=reportObject;
            this.onCircleSelectChanged=selectChanged;
            this.onCircleEdit=onEdit;
            this.onCircleDelete=onDelete;
        }
        this.initCircle=function (container,circle) {
            this.circleContainer=container;
            this.controlCircle=circle;
            $('#reportCircleModify_queryCode').html(reportObject.CircleQueryOption);
            $("#reportCircleModify_sheetName").html(reportObject.sheetNamesOption);
            if (circle==null){
                $("#reportCircleModify_circleItemType").val(-1);
                $("#reportCircleModify_sheetName").val("");
                $("#reportCircleModify_queryCode").val("");
                this.setCircleItemTypeDisable(false);
            }else{
                this.setCircleItemTypeDisable(true);
                $("#reportCircleModify_circleItemType").val(circle.circleItemType);
                $("#reportCircleModify_sheetName").val(circle.sheetName);
                $("#reportCircleModify_queryCode").val(circle.queryCode);
                if (circle.circleItemType=='CIT_EnvData')
                    $("#reportCircleModify_dataOption").val(circle.queryCode);
                else if (circle.circleItemType=='CIT_QueryFeild')
                    $("#reportCircleModify_queryCode").val(circle.queryCode);
                else if (circle.circleItemType=='CIT_NormalTxt')
                    $("#reportCircleModify_normalText").val(circle.queryCode);
                this.onCircleItemTypeChanged(circle.circleItemType);
            }
            this.loadDynaFunctions();
        }
        this.addControlCircle=function(circle,isLoad){
            var jsUtil=new AppJSGlobUtil();
            var item='<tr class="tableRow">';
            item+='<td>'+globalCtrl.iconButtonHtml("circleEdit","glyphicon-button glyphicon-pencil")+'</td>';
            item+='<td>'+jsUtil.getS('',circle.circleItemTypeName)+'</td>';
            item+='<td>'+jsUtil.getS('',circle.sheetName)+'</td>';
            item+='<td>'+jsUtil.getS('',circle.queryName)+'</td>';
            item+='<td>'+globalCtrl.iconButtonHtml("circleDelete","glyphicon-button glyphicon-trash")+'</td>';
            item+='</tr>';
            this.circleContainer.append(item);
            if (!isLoad) {//如果是加载则不增加控制循环对象
                this.reportObject.reportContrlObj.push(circle);
                this.setCircleSelect(this.circleContainer,this.reportObject.reportContrlObj.length-1);
            }
            this.loadDynaFunctions();
        }
        this.updateControlCircle=function(circle){
            var index=this.getCircleSelect(this.circleContainer);
            if (index>=0){
                var jsUtil=new AppJSGlobUtil();
                var srcCircle=this.reportObject.reportContrlObj[index];
                srcCircle.circleItemType=circle.circleItemType;
                srcCircle.circleItemTypeName=circle.circleItemTypeName;
                srcCircle.sheetName=circle.sheetName;
                srcCircle.queryCode=circle.queryCode;
                srcCircle.queryName=circle.queryName;
                var selTr=$(this.circleContainer).find("tr").eq(index);
                var tds=$(selTr).find("td");
                tds.eq(1).text(jsUtil.getS('',circle.circleItemTypeName));
                tds.eq(2).text(jsUtil.getS('',circle.sheetName));
                tds.eq(3).text(jsUtil.getS('',circle.queryName));
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
            $("#reportCircleModify_circleItemType").off("change").on("change",function (event) {
                ReportCircleModify.onCircleItemTypeChanged($("#reportCircleModify_circleItemType").val());
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
            if (index>=0){
                var curIndex=this.getCircleSelect(container);
                if (curIndex<0 || (curIndex>=0 && curIndex!=index)){
                    this.controlCircle=this.reportObject.reportContrlObj[index];
                    var selTr=$(container).find("tr").eq(index);
                    $(container).find("tr").each(function () {
                        $(this).removeClass("selTableRow");
                    });
                    selTr.addClass("selTableRow");
                    if (this.onCircleSelectChanged!=null)
                        this.onCircleSelectChanged(index);
                }
            }
        }
        this.onCircleItemTypeChanged=function(val) {
            if (val=='CIT_EnvData'){
                $(".circleItemType-queryCode").hide();
                $(".circleItemType-dataOption").hide();
                $(".circleItemType-normalText").hide();
            }else if (val=='CIT_QueryFeild'){
                $(".circleItemType-queryCode").show();
                $(".circleItemType-dataOption").hide();
                $(".circleItemType-normalText").hide();
            }else if (val=='CIT_NormalTxt'){
                $(".circleItemType-queryCode").hide();
                $(".circleItemType-dataOption").hide();
                $(".circleItemType-normalText").hide();
            }
        }
        this.setCircleItemTypeDisable=function(disable){
            if (disable==true)
                $('#reportCircleModify_circleItemType').attr("disabled","disabled");
            else
                $('#reportCircleModify_circleItemType').removeAttr("disabled");
        }
        this.doSaveCitrcle=function(win){
            var doCircle={};
            doCircle.circleItemType=$("#reportCircleModify_circleItemType").val();
            doCircle.circleItemTypeName=$("#reportCircleModify_circleItemType").find("option:selected").text();
            doCircle.sheetName=$("#reportCircleModify_sheetName").val();
            var jsUtil=new AppJSGlobUtil();
            if (jsUtil.getS('',doCircle.circleItemType)==''){
                msgbox.showMsgBox("请选择循环类型");
                return;
            }
            if (doCircle.circleItemType=='CIT_EnvData')
                doCircle.queryCode=$("#reportCircleModify_dataOption").val();
            else if (doCircle.circleItemType=='CIT_QueryFeild')
            {
                doCircle.queryCode=$("#reportCircleModify_queryCode").val();
                doCircle.queryName=$("#reportCircleModify_queryCode").find("option:selected").text();
            }
            else if (doCircle.circleItemType=='CIT_NormalTxt')
                doCircle.queryCode=$("#reportCircleModify_normalText").val();
            if (win.controlCircle==null){
                //如果是新增操作
                doCircle.circleItemList=[];
                win.addControlCircle(doCircle,false);
            }else{
                //如果是编辑操作
                win.updateControlCircle(doCircle);
            }
            win.closeDialog();
        }
        this.closeDialog=function(){
            $('#reportCircleModify').modal('hide');
        }
		this.saveCircle=function() {
            msgbox.showComfireBox("你确定要保存报表输出信息吗？",this,this.doSaveCitrcle);
		}
	}
	var ReportCircleModify=new reportCircleModify();
</script>