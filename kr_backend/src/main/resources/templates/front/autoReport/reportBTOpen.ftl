<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.2" rel="stylesheet"/>
</head>
<body>
<div class="row level0">
    <div class="col-xs-12">
        <p class="box">
            <div class="box-body">
                <table style="width: 100%; margin-bottom: 20px;">
                    <tr height="50px">
                        <td width="5%"><strong>报表名称：</strong></td>
                        <td width="15%"><input type="text" class="form-control" id="reportName" style="width: 90%;" value="${map.reportDefine.reportName!''}" readonly="readonly"></td>
                        <td width="5%"><strong>报表期数：</strong></td>
                        <td width="30%">
                            <div class="form-inline" style="width: 90%;">
                                <button type="button" class="btn bg-blue" onclick="openPeroidReport(-1)">前一期</button>
                                <input type="text" class="form-control" id="curPeriodNo" style="width: 100px;" value="${map.reportDefine.reportPeriodNo!'0'}" readonly="readonly">
                                <button type="button" class="btn bg-blue" onclick="openPeroidReport(1)">后一期</button>
                                <strong>总期数：${map.reportDefine.reportPeriodNo!'0'}</strong>
                            </div>
                        </td>
                        <#if (map.cutKVList??)>
                        <td width="5%"><strong>
                                <#if (map.cutQuery??)>${map.cutQuery.cutTypeName}</#if>：</strong></td>
                        <td width="15%">
                            <div class="input-group" style="width: 100%;">
                                <select id="cutValue" class="select2 form-control" onchange="openPeroidReport(0);">
                                    <#list map.cutKVList as cutKV>
                                    <option value="${cutKV.key!''}" <#if (cutKV_index==0)>selected="selected"</#if>>${cutKV.value!''}</option>
                                    </#list>
                                </select>
                            </div>
                        </td>
                        <#else>
                            <#if (map.reportDefine.reportClass?default("")?trim?length gt 1)>
                            <td width="10%">
                            <div class="input-group" style="width: 100%;">
                                <strong>没有取到报表分割选项！</strong>
                            </div>
                            </td>
                            </#if>
                        </#if>
                    </tr>
                </table>
            </div>
            <p class="well st-well" style="height: 50px;padding: 9px;">
                <table style="width: 100%;">
                <tr>
                    <td style="width: 100px;">
                        报表时间范围：
                    </td>
                    <td>
                        <a id="repDateRange">2019-01-20 12:10:01.999~2019-01-20 12:10:01.999</a>
                    </td>
                    <td style="text-align:-webkit-right">
                        <button type="button" class="btn bg-blue" onclick="downloadFile('excel')">下载Excel</button>
                        <button type="button" class="btn bg-maroon" onclick="downloadFile('pdf')">下载PDF</button>
                    </td>
                </ul>
                </tr>
                </table>
            </p>
            <iframe id="reportContainer" src="" width="100%" height="100%" frameborder="0" scrolling="hidden">
            </iframe>
            <!-- /.box-body -->
        </div>
        <!-- /.box -->
    </div>
    <!-- /.col -->
</div>
<!-- /.row -->
<!-- 弹出窗 项目新增，更改-->
<!-- 弹出窗结束 -->
<script>
    var postman_token="doccca78-002b-4608-afst-assaassddd";
    var reportCode="${map.reportDefine.reportCode!''}";
    function openPeroidReport(off){
        var param={
            reportCode:reportCode,
            periodNo:$("#curPeriodNo").val(),
            cutValue:$("#cutValue").val(),
            off:off
        };
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/priOpenPeroidReport";
        var data = JSON.stringify(param);
        ajga.AjaxApplicationJson(url,data,function (content){
            if (content.code==0){
                if (content.merchantReportRec && content.merchantReportRec.id){
                    var glob_util=new AppJSGlobUtil();
                    var startDate=glob_util.dateFtt("yyyy-MM-dd hh:mm:ss.S",new Date(content.merchantReportRec.startTime));
                    var endDate=glob_util.dateFtt("yyyy-MM-dd hh:mm:ss.S",new Date(content.merchantReportRec.endTime));
                    $("#repDateRange").empty().append(startDate+"~"+endDate);
                    $("#curPeriodNo").val(content.merchantReportRec.periodNo);
                    var src="/openoffice/web/viewer.html?file=/autoReport/previewReport"+encodeURIComponent("?reportRecId="+content.merchantReportRec.id);
                    $("#reportContainer").attr("src",src);
                }
            }else
                msgbox.showMsgBox(content.msg);
        });
    }
    function downloadFile(fileType){
        var param={
            reportCode:reportCode,
            periodNo:$("#curPeriodNo").val(),
            cutValue:$("#cutValue").val(),
            fileType:fileType
        };
        var ajga=new AppJSGlobAjax(postman_token);
        var url="/autoReport/downloadReportFile";
        var data = JSON.stringify(param);
        ajga.AjaxApplicationJson(url,data,function (content){
            if (content){
                if (content.code==0){
                    window.location.href=content.downloadFileUrl;
                }else
                    msgbox.showMsgBox(content.msg);
            }
        });
    }
    $(document).ready(function () {
        openPeroidReport(0);});
</script>
</body>
</html>