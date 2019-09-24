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
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.ReportTemplateFileFilter;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.entity.autoReport.ReportControl;
import com.nnte.kr_business.entity.autoReport.ReportPeriodSetting;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetail;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefineService;
import org.apache.catalina.startup.Catalina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

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
    @Autowired
    private ReportPeriodComponent reportPeriodComponent;
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
    public Map<String, Object> generatorReportFile(Map<String, Object> paramMap, MerchantReportDefine mrd) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        if (mrd==null){
            BaseNnte.setRetFalse(ret, 1002,"没有找到指定的报表定义");
            return ret;
        }
        return genReportControl(mrd,cssf);
    }

    //monthCount>=1 ,表示指定日期向后推算的月数
    public static String getNextMonth(Date date,int monthCount){
        int year=DateUtils.getYear(date);
        int startMonth=DateUtils.getMonth(date);
        int nextm=startMonth+monthCount;
        int years = (nextm-1) / 12;
        year += years;
        int nextMonth;
        if (years>0){
            nextMonth=nextm % 12;
            if (nextMonth==0)
                nextMonth=12;
        }else
            nextMonth=nextm;
        String yearMonth=StringUtils.integer2String(year)+"-"+String.format("%02d",nextMonth);
        return yearMonth;
    }

    public static boolean isDay1AfterDay2(String day1,String day2){
        return DateUtils.stringToDate(day1,DateUtils.DF_YMD).after(DateUtils.stringToDate(day2,DateUtils.DF_YMD));
    }

    public static void setMonthReportEndTime(Date startDate,MerchantReportGendetail mrg,ReportPeriodSetting RPS,
                                             int periodNo){
        int startDay=DateUtils.getDay(startDate);
        int endTimeMonth=NumberUtil.getDefaultInteger(RPS.getEndTimeMonth());
        if (endTimeMonth==0){
            //如果是按自然月计算月末
            String nextMonthFirstDay=getNextMonth(startDate,1)+"-01";
            String sReportEndDate=DateUtils.nDaysAfterOneDateString(nextMonthFirstDay,-1);
            mrg.setEndTime(DateUtils.stringToDate(sReportEndDate +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
        }else {
            if (startDay == endTimeMonth) {
                //如果起始日期就是当月的截止日期，则本期的报表计算天数只有一天
                if (periodNo<=1) {
                    mrg.setEndTime(DateUtils.stringToDate(DateUtils.dateToString(startDate, DateUtils.DF_YMD)
                            + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
                }else{
                    //如果不是第一期，当前开始日期和截止日期相同，则报表结束日期为下月的截止日期
                    String nextMonthEndDay=getNextMonth(startDate,1)+"-"+String.format("%02d",endTimeMonth);
                    mrg.setEndTime(DateUtils.stringToDate(nextMonthEndDay +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
                }
            }else if (startDay < endTimeMonth){
                //如果起始日期小于当月的截止日期，则截止日期就是参数限制的日期
                String endDay=StringUtils.integer2String(DateUtils.getYear(startDate))+"-"+
                        String.format("%02d",DateUtils.getMonth(startDate))+"-"+
                        String.format("%02d",endTimeMonth);
                mrg.setEndTime(DateUtils.stringToDate(endDay +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
            }else{
                //如果起始日期大于当月的截止日期，则截止日期为下月的参数限制日期
                String nextMonthEndDay=getNextMonth(startDate,1)+"-"+String.format("%02d",endTimeMonth);
                mrg.setEndTime(DateUtils.stringToDate(nextMonthEndDay +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
            }
        }
    }
    //取得下一个季度的结束日期
    //quarterEndDay=当前季度的结束日期
    public static String getNextQuarterEndDay(String quarterEndDay,ReportPeriodSetting RPS,int quarterCount){
        int day=31;
        if (quarterCount==1)
            day=Integer.valueOf(RPS.getEndTimeQuarter().split("-")[1]);
        else if (quarterCount==2)
            day=Integer.valueOf(RPS.getEndTimeHalfYear().split("-")[1]);
        String nextQuarterFirstDay = getNextMonth(DateUtils.stringToDate(quarterEndDay, DateUtils.DF_YMD), 3*quarterCount) + "-01";
        String nnFirstDay = getNextMonth(DateUtils.stringToDate(nextQuarterFirstDay, DateUtils.DF_YMD), 1) + "-01";
        String nextQuarterEndDay = DateUtils.nDaysAfterOneDateString(nnFirstDay, -1);
        int nextEndDay = Integer.valueOf(nextQuarterEndDay.split("-")[2]);
        int endDay = (day >= nextEndDay) ? nextEndDay : day;
        return getNextMonth(DateUtils.stringToDate(quarterEndDay, DateUtils.DF_YMD), 3*quarterCount) + "-" + String.format("%02d", endDay);
    }

    public static void setQuarterReportEndTime( String sReportDate,MerchantReportGendetail mrg,ReportPeriodSetting RPS,
                                             int periodNo){
        int year=DateUtils.getYear(mrg.getStartTime());
        String firstEndDay=StringUtils.integer2String(year)+"-"+RPS.getEndTimeQuarter();
        if (firstEndDay.equals(sReportDate)){
            if (periodNo<=1) {
                //如果第一期报表截止日期与报表开始时间相同，则报表结束时间只有一天
                mrg.setEndTime(DateUtils.stringToDate(sReportDate
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }else{
                //如果不是第一期，报表截止日期与报表开始时间相同，则报表的结束时间为下一个季度的结束时间
                mrg.setEndTime(DateUtils.stringToDate(getNextQuarterEndDay(firstEndDay,RPS,1)
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }
        }else if (DateUtils.stringToDate(firstEndDay,DateUtils.DF_YMD).after(
                DateUtils.stringToDate(sReportDate,DateUtils.DF_YMD))){
            //如果当年报表截止日期大于报表开始时间，则报表结束时间就是截止日期时间
            mrg.setEndTime(DateUtils.stringToDate(firstEndDay
                    + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
        }else{
            //如果当年报表截止日期小于报表开始时间，则需要一个季度一个季度的往后推，直到大于开始时间为止
            boolean continueNext;
            do {
                String nextQuarterEndDay = getNextQuarterEndDay(firstEndDay,RPS,1);
                if ((nextQuarterEndDay.equals(sReportDate) && periodNo<=1)||isDay1AfterDay2(nextQuarterEndDay, sReportDate)) {
                    mrg.setEndTime(DateUtils.stringToDate(nextQuarterEndDay
                            + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
                    continueNext = false;
                } else {
                    firstEndDay = nextQuarterEndDay;
                    continueNext = true;
                }
            }while(continueNext);
        }
    }

    public static void setWeekReportEndTime( String sReportDate,MerchantReportGendetail mrg,ReportPeriodSetting RPS,
                                                int periodNo){
        Integer startWeekDay=DateUtils.getWeekNumByDate(sReportDate);
        startWeekDay = (startWeekDay==0)?7:startWeekDay;
        Integer weekno=NumberUtil.getDefaultInteger(RPS.getEndTimeWeek());
        if (startWeekDay.equals(weekno)){
            if (periodNo<=1)
                mrg.setEndTime(DateUtils.stringToDate(sReportDate +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
            else {
                String sReportEndDate=DateUtils.nDaysAfterOneDateString(sReportDate,7);
                mrg.setEndTime(DateUtils.stringToDate(sReportEndDate +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
            }
        }else{
            int addDays;
            if (startWeekDay<weekno)
                addDays = weekno - startWeekDay;
            else
                addDays = weekno + 7 - startWeekDay;
            String sReportEndDate=DateUtils.nDaysAfterOneDateString(sReportDate,addDays);
            mrg.setEndTime(DateUtils.stringToDate(sReportEndDate +" 23:59:59.999",DateUtils.DF_YMD_HMSSSS));
        }
    }

    public static void setHalfYearReportEndTime( String sReportDate,MerchantReportGendetail mrg,ReportPeriodSetting RPS,
                                             int periodNo){
        int year=DateUtils.getYear(mrg.getStartTime());
        String firstEndDay=StringUtils.integer2String(year)+"-"+RPS.getEndTimeHalfYear();
        if (firstEndDay.equals(sReportDate)){
            if (periodNo<=1) {
                //如果第一期的开始日期与截止日期相同，则第一期只有一天
                mrg.setEndTime(DateUtils.stringToDate(firstEndDay
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }else{
                //如果不是第一期，且开始日期与截止日期相同，则报表结束时间为下一个半年的结束时间
                String nextHalfYearEndDay;
                if (RPS.getEndTimeHalfYear().equals("06-30")){
                    nextHalfYearEndDay=StringUtils.integer2String(year)+"-12-31";
                }else {
                    nextHalfYearEndDay = getNextQuarterEndDay(firstEndDay, RPS, 2);//2个季度为半年
                }
                mrg.setEndTime(DateUtils.stringToDate(nextHalfYearEndDay
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }
        }else if (isDay1AfterDay2(firstEndDay,sReportDate)){
            //如果第一期的开始日期小于截止日期，则截止日期为报表结束日期
            mrg.setEndTime(DateUtils.stringToDate(firstEndDay
                    + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
        }else{
            //如果第一期的开始日期大于截止日期，则报表结束日期为下一个半年或下下一个半年的结束日期
            String nextHalfYearEndDay;
            if (RPS.getEndTimeHalfYear().equals("06-30")){
                nextHalfYearEndDay=StringUtils.integer2String(year)+"-12-31";
            }else{
                nextHalfYearEndDay = getNextQuarterEndDay(firstEndDay,RPS,2);//2个季度为半年
                if (!isDay1AfterDay2(nextHalfYearEndDay,sReportDate))
                    nextHalfYearEndDay = getNextQuarterEndDay(nextHalfYearEndDay,RPS,2);//下下一个半年
            }
            mrg.setEndTime(DateUtils.stringToDate(nextHalfYearEndDay
                    + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
        }
    }
    public static void setYearReportEndTime( String sReportDate,MerchantReportGendetail mrg,ReportPeriodSetting RPS,
                                                 int periodNo){
        int year=DateUtils.getYear(mrg.getStartTime());
        String firstEndDay=StringUtils.integer2String(year)+"-"+RPS.getEndTimeYear();
        if (firstEndDay.equals(sReportDate)){
            if (periodNo<=1) {
                //如果第一期截止时间与开始时间相同，则本期只有一天
                mrg.setEndTime(DateUtils.stringToDate(firstEndDay
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }else{
                //如果不是第一期，截止时间与开始时间相同，则本期结束时间为下一年的截止时间
                year++;
                mrg.setEndTime(DateUtils.stringToDate(StringUtils.integer2String(year)+"-"+RPS.getEndTimeYear()
                        + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
            }
        }else if (isDay1AfterDay2(firstEndDay,sReportDate)){
            //如果当期截止时间大于开始日期，则本期结束时间就是当期截止时间
            mrg.setEndTime(DateUtils.stringToDate(firstEndDay
                    + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
        }else{
            //如果当期截止时间小于开始日期，则本期结束时间就是下一年的当期截止时间
            year++;
            mrg.setEndTime(DateUtils.stringToDate(StringUtils.integer2String(year)+"-"+RPS.getEndTimeYear()
                    + " 23:59:59.999", DateUtils.DF_YMD_HMSSSS));
        }
    }
    public static Map<String, Object> setControlDate(MerchantReportDefine mrd,MerchantReportGendetail mrg,
                                                     ReportPeriodSetting RPS){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (NumberUtil.getDefaultInteger(mrd.getReportPeriodNo())>0){
            //如果已经有历史日期了,上期结束时间为本期开始时间
            mrg.setPeriodNo(mrd.getReportPeriodNo()+1);//表示是下一期报表
            mrg.setStartTime(mrd.getEndTime());        //报表上上期结束时间为本期开始时间
            if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Day_Report)){
                String sReportPriDate=DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD);
                String sReportDate=DateUtils.nDaysAfterOneDateString(sReportPriDate,1);
                mrg.setEndTime(DateUtils.stringToDate(sReportDate +" "+ RPS.getEndTimeDay(),DateUtils.DF_YMD_HMSSSS));
            }else if(mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Week_Report)){
                setWeekReportEndTime(DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD),
                        mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Month_Report)){
                setMonthReportEndTime(mrg.getStartTime(),mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Quarter_Report)){
                setQuarterReportEndTime(DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD),
                        mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.HalfYear_Report)){
                setHalfYearReportEndTime(DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD),
                        mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Year_Report)){
                setYearReportEndTime(DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD),
                        mrg,RPS,mrg.getPeriodNo());
            }
        }else{
            //如果没有历史日期，要从报表的起始日期开始计算
            if (mrd.getStartDate()==null){
                BaseNnte.setRetFalse(ret, 1002,"报表起始日期不合法");
                return ret;
            }
            mrg.setPeriodNo(1);//表示是第一期报表
            if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Day_Report)){
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                String sReportPriDate=DateUtils.nDaysAfterOneDateString(sReportDate,-1);
                mrg.setStartTime(DateUtils.stringToDate(sReportPriDate +" "+ RPS.getEndTimeDay(),DateUtils.DF_YMD_HMSSSS));
                mrg.setEndTime(DateUtils.stringToDate(sReportDate +" "+ RPS.getEndTimeDay(),DateUtils.DF_YMD_HMSSSS));
            }else if(mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Week_Report)){
                //如果是周报表,报表起始时间为第一期的第一天
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                mrg.setStartTime(DateUtils.stringToDate(sReportDate +" 00:00:00.000",DateUtils.DF_YMD_HMSSSS));
                setWeekReportEndTime(sReportDate,mrg,RPS,mrg.getPeriodNo());
            }else if(mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Month_Report)){
                //如果是月报,报表起始时间为第一期报表的第一天，计算报表的最后一天
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                mrg.setStartTime(DateUtils.stringToDate(sReportDate +" 00:00:00.000",DateUtils.DF_YMD_HMSSSS));
                setMonthReportEndTime(mrg.getStartTime(),mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Quarter_Report)){
                //如果是季报，报表起始时间为第一期报表的第一天，计算报表的最后一天（3个月后）
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                mrg.setStartTime(DateUtils.stringToDate(sReportDate +" 00:00:00.000",DateUtils.DF_YMD_HMSSSS));
                setQuarterReportEndTime(sReportDate,mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.HalfYear_Report)){
                //如果是半年报，报表起始时间为第一期报表的第一天，计算报表的最后一天（半年后）
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                mrg.setStartTime(DateUtils.stringToDate(sReportDate +" 00:00:00.000",DateUtils.DF_YMD_HMSSSS));
                setHalfYearReportEndTime(sReportDate,mrg,RPS,mrg.getPeriodNo());
            }else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Year_Report)){
                //如果是年报，报表起始时间为第一期报表的第一天，计算报表的最后一天（一年后）
                String sReportDate=DateUtils.dateToString(mrd.getStartDate(),DateUtils.DF_YMD);
                mrg.setStartTime(DateUtils.stringToDate(sReportDate +" 00:00:00.000",DateUtils.DF_YMD_HMSSSS));
                setYearReportEndTime(sReportDate,mrg,RPS,mrg.getPeriodNo());
            }
        }
        BaseNnte.setRetTrue(ret,"设置报表日期成功");
        return ret;
    }
    //产生一个报表控制
    public Map<String, Object> genReportControl(MerchantReportDefine mrd,ConnSqlSessionFactory cssf){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (StringUtils.isEmpty(mrd.getReportPeriod())){
            BaseNnte.setRetFalse(ret, 1002,"报表周期不合法");
            return ret;
        }
        //先计算本报表的起止时间,需要先查看报表的产生记录-----------
        MerchantReportGendetail mrg=new MerchantReportGendetail();
        mrg.setParMerchantId(mrd.getParMerchantId());
        mrg.setReportCode(mrd.getReportCode());
        mrg.setReportBusiType(mrd.getReportBusiType());
        mrg.setReportPeriod(mrd.getReportPeriod());
        ReportPeriodSetting RPS=reportPeriodComponent.getMerchantPeriodSetting(mrd.getParMerchantId());
        Map<String, Object> controlDateRet = setControlDate(mrd,mrg,RPS);
        if (!BaseNnte.getRetSuc(controlDateRet))
            return controlDateRet;
        return ret;
    }

    public static void main(String[] args){
        MerchantReportDefine mrd=new MerchantReportDefine();
        mrd.setReportPeriodNo(1);
        mrd.setReportPeriod(ReportPeriodComponent.Period.Year_Report);
        mrd.setStartDate(DateUtils.stringToDate("2018-12-26",DateUtils.DF_YMD));
        mrd.setEndTime(DateUtils.stringToDate("2020-02-29 23:50:50.000",DateUtils.DF_YMD_HMSSSS));

        MerchantReportGendetail mrg=new MerchantReportGendetail();
        ReportPeriodSetting RPS=new ReportPeriodSetting();
        RPS.setEndTimeDay("23:50:50.000");
        RPS.setEndTimeWeek("5");
        RPS.setEndTimeMonth("25");
        RPS.setEndTimeQuarter("03-30");
        RPS.setEndTimeHalfYear("12-30");
        RPS.setEndTimeYear("02-28");

        Map<String, Object> controlDateRet = setControlDate(mrd,mrg,RPS);
        if (BaseNnte.getRetSuc(controlDateRet)){
            System.out.println(mrg.getPeriodNo());
            System.out.println(DateUtils.dateToString(mrg.getStartTime(),DateUtils.DF_YMD_HMSSSS));
            System.out.println(DateUtils.dateToString(mrg.getEndTime(),DateUtils.DF_YMD_HMSSSS));
        }
    }
}
