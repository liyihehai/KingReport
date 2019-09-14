package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.SysParamItem;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.component.base.SysParamComponent;
import com.nnte.kr_business.entity.autoReport.ReportBusiType;
import com.nnte.kr_business.entity.autoReport.ReportPeriodSetting;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.base.sysparam.BaseSysParam;
import com.nnte.kr_business.mapper.workdb.base.sysparam.BaseSysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportParamsComponent {
    @Autowired
    private BaseSysParamService baseSysParamService;
    @Autowired
    private ReportPeriodComponent reportPeriodComponent;
    @Autowired
    private SysParamComponent sysParamComponent;
    //---------------------------------
    @SysParamItem(value = "商户报表业务类型",type = SysParamComponent.ParamType.TYPE_MER_MUL)
    public final static String SYSPARAM_MERCHANT_REPORT_BUSI_TYPE="SYSPARAM_MERCHANT_REPORT_BUSI_TYPE";
    @DataLibItem("参数状态")
    public final static List<KeyValue> LibBusiTypeState = new ArrayList<>();
    static {//0:无效，1：有效
        LibBusiTypeState.add(new KeyValue("0","无效"));
        LibBusiTypeState.add(new KeyValue("1","有效"));
    }
    //---------------------------------
    public BaseSysParam queryReportBusiTypeParam(Long merchantId, String typeCode){
        return sysParamComponent.getMerchantMulParamItem(merchantId,SYSPARAM_MERCHANT_REPORT_BUSI_TYPE,typeCode);
    }
    public ReportBusiType getBusiTypeFromParam(BaseSysParam bsp){
        if (bsp==null)
            return null;
        ReportBusiType ret = new ReportBusiType();
        ret.setParMerchantId(bsp.getParMerchantId());
        ret.setBusiTypeState(bsp.getParamState());
        ret.setBusiTypeCode(bsp.getPkey());
        ret.setBusiTypeName(bsp.getValue1());
        return ret;
    }
    public ReportBusiType queryParamBusiType(Long merchantId,String typeCode){
        return getBusiTypeFromParam(queryReportBusiTypeParam(merchantId,typeCode));
    }
    //根据条件查询商户报表业务类型定义列表
    @DBSrcTranc
    public Map<String, Object> loadReportParamBusiTypes(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("classId",SYSPARAM_MERCHANT_REPORT_BUSI_TYPE);
        paramMap.put("paramType",SysParamComponent.ParamType.TYPE_MER_MUL);
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<ReportBusiType> list=baseSysParamService.queryReportParamBusiTypesList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"未取得商户报表业务类型数据");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"取得商户报表业务类型成功");
        ret.put("list",list);
        ret.put("count",paramMap.get("count"));
        return ret;
    }
    //保存商户报表业务类型
    @DBSrcTranc
    public Map<String, Object> saveParamBusiTypes(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator merchantOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        ReportBusiType saveRBT=(ReportBusiType)paramMap.get("saveRBT");
        if (saveRBT==null|| StringUtils.isEmpty(saveRBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret, 1002,"未取得商户报表业务类型数据");
            return ret;
        }
        BaseSysParam bsp=queryReportBusiTypeParam(loginMerchant.getParMerchantId(),saveRBT.getBusiTypeCode());
        if (bsp==null)
        {
            bsp=new BaseSysParam();
            bsp.setParMerchantId(loginMerchant.getParMerchantId());
            bsp.setParamType(SysParamComponent.ParamType.TYPE_MER_MUL);
            bsp.setParamName(DataLibrary.getSysParamDesc(SysParamComponent.ParamType.TYPE_MER_MUL,
                    SYSPARAM_MERCHANT_REPORT_BUSI_TYPE));
            bsp.setClassId(SYSPARAM_MERCHANT_REPORT_BUSI_TYPE);
            bsp.setPkey(saveRBT.getBusiTypeCode());
            bsp.setValue1(saveRBT.getBusiTypeName());
            bsp.setParamState(1);
            bsp.setUpdateOpeId(merchantOperator.getId());
            bsp.setUpdateOpeName(merchantOperator.getOpeName());
            bsp.setUpdateTime(new Date());
        }else{
            bsp.setValue1(saveRBT.getBusiTypeName());
            bsp.setUpdateOpeId(merchantOperator.getId());
            bsp.setUpdateOpeName(merchantOperator.getOpeName());
            bsp.setUpdateTime(new Date());
        }
        BaseSysParam saveBsp=baseSysParamService.save(cssf,bsp,true);
        if (saveBsp!=null && saveBsp.getId()!=null && saveBsp.getId()>0){
            BaseNnte.setRetTrue(ret,"保存商户报表业务类型数据成功");
            return ret;
        }
        BaseNnte.setRetFalse(ret, 1002,"保存商户报表业务类型数据失败");
        return ret;
    }
    //删除商户报表业务类型
    @DBSrcTranc
    public Map<String, Object> deleteParamBusiType(Map<String, Object> paramMap) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        ReportBusiType deleteRBT=(ReportBusiType)paramMap.get("deleteRBT");
        if (deleteRBT==null|| StringUtils.isEmpty(deleteRBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret, 1002,"未取得商户报表业务类型数据");
            return ret;
        }
        BaseSysParam bsp=queryReportBusiTypeParam(loginMerchant.getParMerchantId(),deleteRBT.getBusiTypeCode());
        if (bsp==null){
            BaseNnte.setRetTrue(ret,"删除商户报表业务类型数据成功(无数据)");
            return ret;
        }
        if (baseSysParamService.deleteModel(cssf,bsp)==1){
            BaseNnte.setRetTrue(ret,"删除商户报表业务类型数据成功");
            return ret;
        }
        BaseNnte.setRetFalse(ret,1002,"删除商户报表业务类型数据失败");
        return ret;
    }

    //更改商户报表业务类型状态
    @DBSrcTranc
    public Map<String, Object> stateParamBusiType(Map<String, Object> paramMap) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator merchantOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        ReportBusiType stateRBT=(ReportBusiType)paramMap.get("stateRBT");
        if (stateRBT==null|| StringUtils.isEmpty(stateRBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret, 1002,"未取得商户报表业务类型数据");
            return ret;
        }
        if (stateRBT.getBusiTypeState()==null||
                stateRBT.getBusiTypeState()<0 || stateRBT.getBusiTypeState()>1){
            BaseNnte.setRetFalse(ret, 1002,"未取得商户报表业务类型状态数据");
            return ret;
        }
        BaseSysParam bsp=queryReportBusiTypeParam(loginMerchant.getParMerchantId(),stateRBT.getBusiTypeCode());
        if (bsp==null){
            BaseNnte.setRetFalse(ret,1002,"未取得商户报表业务类型数据(无数据)");
            return ret;
        }
        bsp.setParamState(stateRBT.getBusiTypeState());
        bsp.setUpdateOpeId(merchantOperator.getId());
        bsp.setUpdateOpeName(merchantOperator.getOpeName());
        bsp.setUpdateTime(new Date());
        if (baseSysParamService.updateModel(cssf,bsp)==1){
            BaseNnte.setRetTrue(ret,"更改商户报表业务类型状态成功");
            return ret;
        }
        BaseNnte.setRetFalse(ret,1002,"更改商户报表业务类型状态失败");
        return ret;
    }

    @DBSrcTranc
    public Map<String, Object> getMerchantReportPeriod(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        ReportPeriodSetting rps = reportPeriodComponent.getMerchantPeriodSetting(loginMerchant.getParMerchantId());
        //--------------------------------
        BaseNnte.setRetTrue(ret,"取得商户报表周期参数成功");
        ret.put("rps",rps);
        return ret;
    }
}
