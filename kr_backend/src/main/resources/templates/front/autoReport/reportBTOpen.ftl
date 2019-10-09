<html>
<head>
    <#include "../taglib.ftl">
    <link href="${envData.staticRoot!''}/css/bootstrapModal.css?v=1.2" rel="stylesheet"/>
</head>
<body>
<div class="row level0">
    <div class="col-xs-12">
        <div class="box">
            <div class="box-body">
                <table style="width: 100%; margin-bottom: 20px;">
                    <tr height="50px">
                        <td width="5%"><strong>报表名称：</strong></td>
                        <td width="15%"><input type="text" class="form-control" id="reportName" style="width: 90%;" value="${map.reportDefine.reportName!''}" readonly="readonly"></td>
                        <td width="5%"><strong>报表期数：</strong></td>
                        <td width="30%">
                            <div class="form-inline" style="width: 90%;">
                                <button type="button" class="btn bg-blue" onclick="openPriPeroidReport()">前一期</button>
                                <input type="text" class="form-control" id="curPeriodNo" style="width: 100px;" value="${map.reportDefine.reportPeriodNo!'0'}" readonly="readonly">
                                <button type="button" class="btn bg-blue" onclick="openNextPeroidReport()">后一期</button>
                                <strong>总期数：${map.reportDefine.reportPeriodNo!'0'}</strong>
                            </div>
                        </td>
                        <#if (map.cutKVList??)>
                        <td width="5%"><strong>
                                <#if (map.cutQuery??)>${map.cutQuery.cutTypeName}</#if>：</strong></td>
                        <td width="15%">
                            <div class="input-group" style="width: 100%;">
                                <select id="cutValue" class="select2 form-control">
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
                <div class="nav-tabs-custom" style="cursor: move;">
                    <!-- Tabs within a box -->
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 50%;">
                        <li class="active"><a href="#" data-toggle="tab" aria-expanded="true">报表时间范围：2019-01-20 12:10:01.999~2019-01-20 12:10:01.999</a></li>
                    </ul>
                    <ul class="nav nav-tabs pull-left ui-sortable-handle" style="width: 50%;">
                        <li style="float: right;">
                            <button type="button" class="btn bg-blue" onclick="openSpecReport()">打开</button>
                            <button type="button" class="btn bg-maroon" onclick="reportFileExport()">导出</button>
                        </li>
                    </ul>
                </div>
            </div>
            <iframe src="/openoffice/web/viewer.html?file=/autoReport/previewReport?reportRecId=1" width="100%" height="100%" frameborder="0" scrolling="hidden">
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
    
</script>
</body>
</html>