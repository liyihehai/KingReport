package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.ReportTemplateFileFilter;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportComponent extends BaseComponent {

    public static class ReportState{
        public final static Integer UN_USER=0;
        public final static Integer USED=1;
        public final static Integer PAUSE=2;
        public final static Integer DELETED=9;
    };
    @Autowired
    private MerchantReportDefineService merchantReportDefineService;
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
    //保存报表模板文件
    private void saveReportTemplateFile(Long merchantId,String reportCode,String fileName, byte[] content){
        String fpath=StringUtils.pathAppend(StringUtils.pathAppend(appConfig.getReportRoot(),
                merchantId.toString()),reportCode);
        if (!FileUtil.isPathExists(fpath))
            FileUtil.makeDirectory(fpath);
        String pfn=StringUtils.pathAppend(fpath,fileName);
        FileUtil.writeFile(pfn,content);
    }
    //查询报表模板文件名列表
    public List<String> getReportTemplateFileNames(Long merchantId,String reportCode) {
        String fpath = StringUtils.pathAppend(StringUtils.pathAppend(appConfig.getReportRoot(),
                merchantId.toString()), reportCode);
        File[] files=FileUtil.listAll(new File(fpath),new ReportTemplateFileFilter());
        if (files!=null && files.length>0){
            List<String> list = new ArrayList<>();
            for(File f:files){
                list.add(f.getName());
            }
            return list;
        }
        return null;
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
        saveReportTemplateFile(merchantId,reportCode,fileName,content);
        BaseNnte.setRetTrue(ret,"保存模板文件成功");
        List<String> templateFiles=getReportTemplateFileNames(merchantId,reportCode);
        ret.put("templateFiles",templateFiles);
        ret.put("templateFile",fileName);
        return ret;
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
    //产生报表文件
    @DBSrcTranc
    public Map<String, Object> generatorReportFile(Map<String, Object> paramMap, Integer reportCode,
                                                   String fileName, Byte[] content) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();

        return ret;
    }
}
