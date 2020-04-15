package com.nnte.kr_business.component.autoReport;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.entity.XSSFWorkbookAndOPC;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.ExcelUtil;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.NConfig;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.entity.autoReport.ReportBusiType;
import com.nnte.kr_business.entity.autoReport.ReportControlCircle;
import com.nnte.kr_business.entity.autoReport.TemplateItem;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefineService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/*
* 报表定义组件
* */
@Component
@WorkDBAspect
@DataLibType
public class AutoReportComponent extends BaseComponent {

    public static class ReportState{
        public final static Integer UN_USER=0;
        public final static Integer USED=1;
        public final static Integer PAUSE=2;
        public final static Integer DELETED=9;
    }
    @Autowired
    private MerchantReportDefineService merchantReportDefineService;
    @Autowired
    private AutoReportParamsComponent autoReportParamsComponent;
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;
    @Autowired
    private AutoReportExcelComponent autoReportExcelComponent;
    //组件数据字典区域
    //------------------------------------------
    @DataLibItem("报表启用状态")
    public final static List<KeyValue> LibReportState = new ArrayList<>();
    static {//0：未启用，1：启用：2：删除
        LibReportState.add(new KeyValue(ReportState.UN_USER.toString(),"未启用"));
        LibReportState.add(new KeyValue(ReportState.USED.toString(),"启用"));
        LibReportState.add(new KeyValue(ReportState.PAUSE.toString(),"暂停"));
        LibReportState.add(new KeyValue(ReportState.DELETED.toString(),"删除"));
    }
    @DataLibItem("报表循环类型")
    public final static List<KeyValue> LibControlCircleItemType = new ArrayList<>();
    static {
        LibControlCircleItemType.add(new KeyValue(ReportControlCircle.CircleItemType.CIT_EnvData.toString(),"环境数据"));
        LibControlCircleItemType.add(new KeyValue(ReportControlCircle.CircleItemType.CIT_QueryFeild.toString(),"查询字段"));
        LibControlCircleItemType.add(new KeyValue(ReportControlCircle.CircleItemType.CIT_NormalTxt.toString(),"普通文本"));
    }
    @DataLibItem("控制循环明细数据类型")
    public final static List<KeyValue> LibCircleItemDataType = new ArrayList<>();
    static {
        LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_STRING.toString(),"字符串"));
        LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_INT.toString(),"整数"));
        LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_FLOT.toString(),"浮点数"));
        LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_DATE.toString(),"日期时间"));
    }
    //------------------------------------------
    //查询商户定义的所有使用状态的报表
    public List<MerchantReportDefine> getUsedReportsByMerchant(Long parMerchantId) {
        if (parMerchantId==null || parMerchantId<=0)
            return null;
        MerchantReportDefine dto = new MerchantReportDefine();
        dto.setParMerchantId(parMerchantId);
        dto.setReportState(ReportState.USED);
        return merchantReportDefineService.findModelList(dto);
    }
    //查询商户定义的所有使用状态的报表
    public List<KeyValue> getUsedReportsByMerchantKV(Long parMerchantId) {
        List<MerchantReportDefine> list=getUsedReportsByMerchant(parMerchantId);
        if (list==null || list.size()<=0)
            return null;
        List<KeyValue> ret =new ArrayList<>();
        for(MerchantReportDefine MRD:list){
            ret.add(new KeyValue(MRD.getId().toString(),MRD.getReportName()));
        }
        return ret;
    }

    public MerchantReportDefineService getMerchantReportDefineService(){
        return merchantReportDefineService;
    }

    /*
    * 取得商户有效的报表业务分类及其下查询定义
    * isOnlyUsed=true,表示只有有效的报表分类和有效的报表才显示
    * */
    @DBSrcTranc
    public Map<String,Object> getMerchantBTReportList(Map<String, Object> paramMap,boolean isOnlyUsed){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        Map<String,Object> busiMap=autoReportParamsComponent.loadReportParamBusiTypes(paramMap,isOnlyUsed);
        if (!BaseNnte.getRetSuc(busiMap))
            return busiMap;
        List<Map<String,Object>> retlist=new ArrayList<>();
        List<ReportBusiType> list=(List<ReportBusiType>)busiMap.get("list");
        for(ReportBusiType rbt:list){
            Map<String,Object> BTRepListMap=new HashMap<>();
            BTRepListMap.put("ReportBusiType",rbt);
            BTRepListMap.put("ReportList",getReportRecordByBusiType(cssf,loginMerchant.getId(),rbt.getBusiTypeCode(),isOnlyUsed));
            retlist.add(BTRepListMap);
        }
        ret.put("BTReportList",retlist);
        BaseNnte.setRetTrue(ret,"报表保存成功");
        return ret;
    }
    //通过BusiType查找报表记录
    public List<MerchantReportDefine> getReportRecordByBusiType(ConnSqlSessionFactory cssf, Long merchantId,
                                                                String busiType,boolean isOnlyUsed) {
        MerchantReportDefine dto = new MerchantReportDefine();
        dto.setParMerchantId(merchantId);
        dto.setReportBusiType(busiType);
        if (isOnlyUsed)
            dto.setReportState(ReportState.USED);
        List<MerchantReportDefine> list = merchantReportDefineService.findModelList(cssf, dto);
        return list;
    }

    @DBSrcTranc
    public Map<String, Object> getReportRecordByCode(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        MerchantReportDefine merchantReportDefine=getReportRecordByCode(cssf,loginMerchant.getId(),
                StringUtils.defaultString(paramMap.get("reportCode")));
        if (merchantReportDefine==null||merchantReportDefine.getId()==null||
                merchantReportDefine.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"取得报表定义错误");
            return ret;
        }
        ret.put("merchantReportDefine",merchantReportDefine);
        BaseNnte.setRetTrue(ret,"取得报表定义成功");
        return ret;
    }

    @DBSrcTranc
    public Map<String, Object> saveReportOutput(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        MerchantReportDefine merchantReportDefine=(MerchantReportDefine)paramMap.get("merchantReportDefine");
        if (merchantReportDefine==null||merchantReportDefine.getId()==null||
                merchantReportDefine.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"取得报表定义错误");
            return ret;
        }
        MerchantReportDefine updateMRD=new MerchantReportDefine();
        updateMRD.setId(merchantReportDefine.getId());
        updateMRD.setOutputControl(StringUtils.defaultString(paramMap.get("output")));
        if (!merchantReportDefineService.updateModel(cssf,updateMRD).equals(1))
        {
            BaseNnte.setRetFalse(ret, 1002,"保存报表输出错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"保存报表输出成功");
        return ret;
    }

    //通过CODE查找报表记录
    public MerchantReportDefine getReportRecordByCode(ConnSqlSessionFactory cssf, Long merchantId, String code) {
        MerchantReportDefine dto = new MerchantReportDefine();
        dto.setParMerchantId(merchantId);
        dto.setReportCode(code);
        List<MerchantReportDefine> list = merchantReportDefineService.findModelList(cssf, dto);
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    //通过ID查找报表记录
    public MerchantReportDefine getReportRecordById(Long reportId) {
        return merchantReportDefineService.findModelByKey(reportId);
    }

    //保存报表定义
    @DBSrcTranc
    public Map<String, Object> saveMerchantReportDefine(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator loginMerchantOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");

        String reportCode= StringUtils.defaultString(paramMap.get("reportCode"));
        MerchantReportDefine dto = getReportRecordByCode(cssf,loginMerchant.getParMerchantId(),reportCode);
        if (dto == null) {
            //如果没有查到记录，则新增一条
            dto = new MerchantReportDefine();
            dto.setParMerchantId(loginMerchant.getParMerchantId());
            dto.setReportCode(reportCode);
            dto.setReportName(StringUtils.defaultString(paramMap.get("reportName")));
            dto.setReportClass(StringUtils.defaultString(paramMap.get("reportClass")));
            dto.setReportBusiType(StringUtils.defaultString(paramMap.get("reportBusiType")));
            dto.setReportPeriod(StringUtils.defaultString(paramMap.get("reportPeriod")));
            dto.setStartDate((Date)paramMap.get("startDate"));
            dto.setReportState(0);
            dto.setCreateTime(new Date());
            dto.setCreateOperId(loginMerchantOperator.getId());
            dto.setCreateOperName(loginMerchantOperator.getOpeName());
            dto.setUpdateTime(dto.getCreateTime());
            dto = merchantReportDefineService.save(cssf,dto,false);
        }else{//如果有，则更改记录
            dto.setReportName(StringUtils.defaultString(paramMap.get("reportName")));
            dto.setReportClass(StringUtils.defaultString(paramMap.get("reportClass")));
            dto.setReportBusiType(StringUtils.defaultString(paramMap.get("reportBusiType")));
            dto.setReportPeriod(StringUtils.defaultString(paramMap.get("reportPeriod")));
            dto.setStartDate((Date)paramMap.get("startDate"));
            dto.setUpdateTime(new Date());
            dto = merchantReportDefineService.save(cssf,dto,false);
        }
        if (dto==null||dto.getId()==null||dto.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"报表保存错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"报表保存成功");
        return ret;
    }

    //查询报表模板文件名列表
    public List<String> getReportTemplateFileNames(MerchantReportDefine mrd) {
        JSONArray jArray=MerchantReportDefineService.initReportTempfileCollect(mrd);
        if (jArray==null || jArray.size()<=0)
            return null;
        List<String> list = new ArrayList<>();
        for(int i=0;i<jArray.size();i++){
            JSONObject jobj=jArray.getJSONObject(i);
            list.add(StringUtils.defaultString(jobj.get("fileName")));
        }
        return list;
    }
    //上传并保存模板文件
    @DBSrcTranc
    public Map<String, Object> uploadTemplateFile(Map<String, Object> paramMap, Long merchantId,
                                                  String reportCode, String fileName, byte[] content) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (merchantId==null || merchantId<0){
            BaseNnte.setRetFalse(ret, 1002,"没有确定商户");
            return ret;
        }
        if (StringUtils.isEmpty(reportCode)){
            BaseNnte.setRetFalse(ret, 1002,"报表代码错误");
            return ret;
        }
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //检查reportCode是否有效
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        MerchantReportDefine dto = getReportRecordByCode(cssf,loginMerchant.getParMerchantId(), reportCode);
        if (dto == null) {
            BaseNnte.setRetFalse(ret, 1002,"未取得报表定义");
            return ret;
        }
        //替换原有将模板文件保存在应用服务器,模板文件改为保存在文件服务器中
        NConfig config=(NConfig)paramMap.get("KingReportConfig");
        String templateType=config.getConfig("templateType");
        String submitName=fdfsClientMgrComponent.uploadFile(templateType,content,FileUtil.getExtention(fileName));
        if (StringUtils.isNotEmpty(submitName)){
            BaseNnte.setRetTrue(ret,"保存模板文件成功");
            String srcSubmitName=MerchantReportDefineService.addFileToCollect(dto,fileName,submitName);
            merchantReportDefineService.saveReportTempfileCollect(cssf,dto);//在数据库中保存模板集更改
            List<String> templateFiles=getReportTemplateFileNames(dto);
            ret.put("templateFiles",templateFiles);
            ret.put("templateFile",fileName);
            if (StringUtils.isNotEmpty(srcSubmitName)){
                //如果存在被替换的模板文件名，需要在文件服务器端删除原始的模板文件
                fdfsClientMgrComponent.deleteFile(templateType,srcSubmitName);
            }
        }else{
            BaseNnte.setRetTrue(ret,"保存模板文件失败");
        }
        //------------------------------------------------------
        return ret;
    }
    //下载报表模板文件，生成临时文件，返回临时文件路径文件名
    public String downloadReportTemplateFile(MerchantReportDefine mrd,String type){
        if (mrd==null || StringUtils.isEmpty(mrd.getTemplateFile()) ||
                StringUtils.isEmpty(mrd.getTempfileCollect()))
            return null;
        JSONArray jarray=JSONArray.fromObject(mrd.getTempfileCollect());
        if (jarray==null || jarray.size()<=0)
            return null;
        String fileName=mrd.getTemplateFile();
        for(int i=0;i<jarray.size();i++){
            TemplateItem ti= (TemplateItem) JSONObject.toBean(jarray.getJSONObject(i),TemplateItem.class);
            if (ti.getFileName().equals(fileName)){
                String[] typeAndSubmitName=ti.getSubmitName().split(":");
                if (typeAndSubmitName==null || typeAndSubmitName.length!=2)
                    return null;
                String submitName=typeAndSubmitName[1];
                byte[] content=fdfsClientMgrComponent.downloadFile(type,submitName);
                if (content!=null){
                    return FileUtil.saveBufToTmpFile(content,FileUtil.getExtention(fileName));
                }
            }
        }
        return null;
    }
    //装载模板文件信息
    public String[] loadTemplateFileInfo(MerchantReportDefine mrd,String type){
        //从文件服务器下载模板文件,生成临时文件----
        String fn=downloadReportTemplateFile(mrd,type);
        //---------------------------------------
        XSSFWorkbookAndOPC wao=autoReportExcelComponent.openExcelTemplate(fn);
        if (wao==null){
            FileUtil.deleteFile(fn);
            return null;//不能打开模板文件
        }
        try {
            //--读取模板文件信息-----------
            String[] retNames=new String[wao.getWb().getNumberOfSheets()];
            for(int i=0;i<retNames.length;i++){
                retNames[i]=wao.getWb().getSheetName(i);
            }
            autoReportExcelComponent.closeExcelTemplate(wao);
            FileUtil.deleteFile(fn);
            return retNames;
        }catch (Exception e){
            e.printStackTrace();
            autoReportExcelComponent.closeExcelTemplate(wao);
            FileUtil.deleteFile(fn);
            return null;
        }
    }
    //保存报表模板文件设置
    @DBSrcTranc
    public Map<String, Object> saveReportTemplate(Map<String, Object> paramMap,String reportCode,String template){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        MerchantReportDefine dto = getReportRecordByCode(cssf,loginMerchant.getParMerchantId(), reportCode);
        if (dto == null) {
            BaseNnte.setRetFalse(ret, 1002,"未取得报表定义");
            return ret;
        }
        dto.setTemplateFile(template);
        if (!merchantReportDefineService.updateModel(cssf,dto).equals(1)){
            BaseNnte.setRetFalse(ret, 1002,"更改报表模板设置失败");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"更改报表模板设置成功");
        return ret;
    }

    //根据条件查询商户报表定义列表
    @DBSrcTranc
    public Map<String, Object> loadMerchantReportDef(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantReportDefine> list=merchantReportDefineService.queryModelList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"未取得报表定义列表数据");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"取得报表定义列表成功");
        ret.put("list",list);
        ret.put("count",paramMap.get("count"));
        return ret;
    }
    //更改指定的商户报表的状态
    @DBSrcTranc
    public Map<String, Object> updateMerchantReportState(Map<String, Object> paramMap,Long reportId,int newState){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        MerchantReportDefine dto = getReportRecordById(reportId);
        if (dto == null) {
            BaseNnte.setRetFalse(ret, 1002,"未取得报表定义");
            return ret;
        }
        MerchantReportDefine updateDto=new MerchantReportDefine();
        updateDto.setId(dto.getId());
        updateDto.setReportState(newState);
        if (!merchantReportDefineService.updateModel(cssf,updateDto).equals(1)){
            BaseNnte.setRetFalse(ret, 1002,"更改商户报表状态失败");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"更改商户报表状态成功");
        return ret;
    }
    //导出商户报表定义到Excel中
    @DBSrcTranc
    public Map<String, Object> exportMerchantReportDefs(Map<String, Object> paramMap) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantReportDefine> list=merchantReportDefineService.queryModelList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"没有可导出数据");
            return ret;
        }
        List<KeyValue> LibReportBusiType=sysParamComponent.getMerchantMulKVParams(loginMerchant.getParMerchantId(),"SYSPARAM_MERCHANT_REPORT_BUSI_TYPE");
        //准备输出列定义
        //---------------------------------
        List<ExpotColDef> colDefList=new ArrayList<>();
        colDefList.add(new ExpotColDef("reportCode","报表代码",null,"",""));
        colDefList.add(new ExpotColDef("reportName","报表名称",null,"",""));
        colDefList.add(new ExpotColDef("reportClass","报表分级", DataLibrary.getLibKeyValueList("LibReportClass"),"",""));
        colDefList.add(new ExpotColDef("reportBusiType","业务分类", LibReportBusiType,"",""));
        colDefList.add(new ExpotColDef("reportPeriod","报表周期",DataLibrary.getLibKeyValueList("LibReportPeriod"),"",""));
        colDefList.add(new ExpotColDef("reportState","报表状态", DataLibrary.getLibKeyValueList("LibReportState"),"",""));
        colDefList.add(new ExpotColDef("createTime","创建时间", null,"", DateUtils.DF_YMD));
        //---------------------------------
        ret=DataExport(loginMerchant.getParMerchantId(),list,colDefList,
                sysParamComponent.getMerchantExportSheetCount(loginMerchant.getParMerchantId()));
        return ret;
    }
}
