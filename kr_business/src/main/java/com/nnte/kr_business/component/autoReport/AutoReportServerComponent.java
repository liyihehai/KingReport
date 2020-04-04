package com.nnte.kr_business.component.autoReport;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.entity.ObjKeyValue;
import com.nnte.framework.entity.TKeyValue;
import com.nnte.framework.entity.XSSFWorkbookAndOPC;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.KRConfigInterface;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.entity.autoReport.*;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetail;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import com.nnte.kr_business.mapper.workdb.merchant.rec.MerchantReportRec;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* 报表服务组件
* */
@Component
@WorkDBAspect
public class AutoReportServerComponent extends BaseComponent {
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;
    @Autowired
    private ReportPeriodComponent reportPeriodComponent;
    @Autowired
    private AutoReportExcelComponent autoReportExcelComponent;
    @Autowired
    private AutoReportComponent autoReportComponent;
    @Autowired
    private AutoReportGenDetailComponent autoReportGenDetailComponent;
    @Autowired
    private AutoReportRecComponent autoReportRecComponent;
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;

    @Autowired
    @ConfigLoad
    public KRConfigInterface config;

    //monthCount>=1 ,表示指定日期向后推算的月数
    public static String getNextMonth(Date date,int monthCount){
        int year= DateUtils.getYear(date);
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
        int endTimeMonth= NumberUtil.getDefaultInteger(RPS.getEndTimeMonth());
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
        try {
            if (NumberUtil.getDefaultInteger(mrd.getReportPeriodNo()) > 0) {
                //如果已经有历史日期了,上期结束时间为本期开始时间
                mrg.setPeriodNo(mrd.getReportPeriodNo() + 1);//表示是下一期报表
                mrg.setStartTime(mrd.getEndTime());        //报表上上期结束时间为本期开始时间
                if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Day_Report)) {
                    String sReportPriDate = DateUtils.dateToString(mrg.getStartTime(), DateUtils.DF_YMD);
                    String sReportDate = DateUtils.nDaysAfterOneDateString(sReportPriDate, 1);
                    mrg.setEndTime(DateUtils.stringToDate(sReportDate + " " + RPS.getEndTimeDay(), DateUtils.DF_YMD_HMSSSS));
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Week_Report)) {
                    setWeekReportEndTime(DateUtils.dateToString(mrg.getStartTime(), DateUtils.DF_YMD),
                            mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Month_Report)) {
                    setMonthReportEndTime(mrg.getStartTime(), mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Quarter_Report)) {
                    setQuarterReportEndTime(DateUtils.dateToString(mrg.getStartTime(), DateUtils.DF_YMD),
                            mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.HalfYear_Report)) {
                    setHalfYearReportEndTime(DateUtils.dateToString(mrg.getStartTime(), DateUtils.DF_YMD),
                            mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Year_Report)) {
                    setYearReportEndTime(DateUtils.dateToString(mrg.getStartTime(), DateUtils.DF_YMD),
                            mrg, RPS, mrg.getPeriodNo());
                }
            } else {
                //如果没有历史日期，要从报表的起始日期开始计算
                if (mrd.getStartDate() == null) {
                    BaseNnte.setRetFalse(ret, 1002, "报表起始日期不合法");
                    return ret;
                }
                mrg.setPeriodNo(1);//表示是第一期报表
                if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Day_Report)) {
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    String sReportPriDate = DateUtils.nDaysAfterOneDateString(sReportDate, -1);
                    mrg.setStartTime(DateUtils.stringToDate(sReportPriDate + " " + RPS.getEndTimeDay(), DateUtils.DF_YMD_HMSSSS));
                    mrg.setEndTime(DateUtils.stringToDate(sReportDate + " " + RPS.getEndTimeDay(), DateUtils.DF_YMD_HMSSSS));
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Week_Report)) {
                    //如果是周报表,报表起始时间为第一期的第一天
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    mrg.setStartTime(DateUtils.stringToDate(sReportDate + " 00:00:00.000", DateUtils.DF_YMD_HMSSSS));
                    setWeekReportEndTime(sReportDate, mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Month_Report)) {
                    //如果是月报,报表起始时间为第一期报表的第一天，计算报表的最后一天
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    mrg.setStartTime(DateUtils.stringToDate(sReportDate + " 00:00:00.000", DateUtils.DF_YMD_HMSSSS));
                    setMonthReportEndTime(mrg.getStartTime(), mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Quarter_Report)) {
                    //如果是季报，报表起始时间为第一期报表的第一天，计算报表的最后一天（3个月后）
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    mrg.setStartTime(DateUtils.stringToDate(sReportDate + " 00:00:00.000", DateUtils.DF_YMD_HMSSSS));
                    setQuarterReportEndTime(sReportDate, mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.HalfYear_Report)) {
                    //如果是半年报，报表起始时间为第一期报表的第一天，计算报表的最后一天（半年后）
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    mrg.setStartTime(DateUtils.stringToDate(sReportDate + " 00:00:00.000", DateUtils.DF_YMD_HMSSSS));
                    setHalfYearReportEndTime(sReportDate, mrg, RPS, mrg.getPeriodNo());
                } else if (mrd.getReportPeriod().equals(ReportPeriodComponent.Period.Year_Report)) {
                    //如果是年报，报表起始时间为第一期报表的第一天，计算报表的最后一天（一年后）
                    String sReportDate = DateUtils.dateToString(mrd.getStartDate(), DateUtils.DF_YMD);
                    mrg.setStartTime(DateUtils.stringToDate(sReportDate + " 00:00:00.000", DateUtils.DF_YMD_HMSSSS));
                    setYearReportEndTime(sReportDate, mrg, RPS, mrg.getPeriodNo());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            BaseNnte.setRetFalse(ret, 9999, "获取报表起始日期异常");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"设置报表日期成功");
        return ret;
    }
    //产生一个报表的报表控制列表
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
        if (mrg.getEndTime().after(new Date())){
            //如果结束时间还没达到
            BaseNnte.setRetFalse(ret, 1002,"报表尚未达到可以生成的时间");
            return ret;
        }
        //---------------------------------------------
        //用于生成一张报表的报表控制，由于有自动分割，可能一次产生多张报表
        List<ReportControl> reportControlList=new ArrayList<>();
        //---------------------------------------------
        //如果报表生成时间满足了，查看报表是否有分割查询
        if (StringUtils.isNotEmpty(mrd.getReportClass())){
            Map<String,Object> queryRet=autoReportQueryComponent.getReportCutQueryContent(cssf,mrd);
            if (!BaseNnte.getRetSuc(queryRet))
                return queryRet;
            List<ObjKeyValue> cutList=(List<ObjKeyValue>)queryRet.get("cutContentList");
            for(ObjKeyValue jobj:cutList){
                ReportControl RC=new ReportControl();
                RC.setReportCode(mrd.getReportCode());
                RC.setReportDefine(mrd);
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.CUT_KEY,jobj.getKey());
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.CUT_NAME, jobj.getValue());
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.START_TIME,mrg.getStartTime());
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.END_TIME,mrg.getEndTime());
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.PERIOD_NO,mrg.getPeriodNo());
                setReportOutputControl(RC);
                reportControlList.add(RC);
            }
        }else{
            ReportControl RC=new ReportControl();
            RC.setReportCode(mrd.getReportCode());
            RC.setReportDefine(mrd);
            RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.START_TIME,mrg.getStartTime());
            RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.END_TIME,mrg.getEndTime());
            RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.PERIOD_NO,mrg.getPeriodNo());
            setReportOutputControl(RC);
            reportControlList.add(RC);
        }
        ret.put("reportControlList",reportControlList);
        BaseNnte.setRetTrue(ret,"生成报表控制成功");
        return ret;
    }
    //设置报表输出控制信息
    public void setReportOutputControl(ReportControl rc){
        ReportControlCircle cc=new ReportControlCircle();
        cc.setCircleItemType(ReportControlCircle.CircleItemType.CIT_EnvData);//数据环境循环
        //---------------------------------------------------------------
        cc.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_INT,
                AutoReportQueryComponent.ResKeyWord.CUT_KEY,"C2",""));
        cc.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_STRING,
                AutoReportQueryComponent.ResKeyWord.CUT_NAME,"E2",""));
        cc.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_DATE,
                AutoReportQueryComponent.ResKeyWord.START_TIME,"G2","yyyy-MM-dd"));
        cc.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_DATE,
                AutoReportQueryComponent.ResKeyWord.END_TIME,"I2","yyyy-MM-dd"));
        rc.getCircleList().add(cc);
        //---------------------------------------------------------------
        ReportControlCircle cc1=new ReportControlCircle();
        cc1.setCircleItemType(ReportControlCircle.CircleItemType.CIT_QueryFeild);//多行查询数据循环
        cc1.setQueryCode("MDXSM");
        //---------------------------------------------------------------
        cc1.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_INT,
                "USER_ID","B4",""));
        cc1.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_STRING,
                "USER_NAME","D4",""));
        cc1.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_FLOT,
                "SUM_AMOUNT","G4","%.2f"));
        rc.getCircleList().add(cc1);
        //---------------------------------------------------------------
        ReportControlCircle cc2=new ReportControlCircle();
        cc2.setCircleItemType(ReportControlCircle.CircleItemType.CIT_QueryFeild);//单行查询数据循环
        cc2.setQueryCode("MDXSMHZ");
        //---------------------------------------------------------------
        cc2.getCircleItemList().add(new ReportControlCircleItem(DataColDef.DataType.DATA_FLOT,
                "SUMAMOUNT","G5","%.2f"));
        rc.getCircleList().add(cc2);
    }
    //产生报表文件
    @DBSrcTranc
    public Map<String, Object> generatorReportFile(Map<String, Object> paramMap, MerchantReportDefine mrd) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        BaseMerchantOperator loginMerchantOperator= KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        //--------------------------------
        if (mrd==null){
            BaseNnte.setRetFalse(ret, 1002,"没有找到指定的报表定义");
            return ret;
        }
        //取得报表控制列表
        Map<String, Object> retRC = genReportControl(mrd,cssf);
        if (!BaseNnte.getRetSuc(retRC)){
            return retRC;
        }
        List<ReportControl> reportControlList=(List<ReportControl>)retRC.get("reportControlList");
        //如果报表控制列表取得成功，需要依据报表控制产生报表文件
        return genReportFiles(loginMerchantOperator,mrd,cssf,reportControlList);
    }
    //依据报表控制列表产生多张报表
    public Map<String, Object> genReportFiles(BaseMerchantOperator operator,MerchantReportDefine mrd,ConnSqlSessionFactory cssf,
                                             List<ReportControl> reportControlList){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        //取得数据查询定义
        List<MerchantReportQuery> queryList=autoReportQueryComponent.queryReportDataQuerys(mrd.getParMerchantId(),mrd,cssf);
        Date startTime=null,endTime=null;
        Integer periodno=null,succount=0;
        for (ReportControl rc:reportControlList){
            Map<String, Object> genRet=genReportFile(operator,cssf,queryList,rc);
            if (!BaseNnte.getRetSuc(genRet))
                return genRet;
            startTime=AutoReportGenDetailComponent.getReportStartTimeFromReportControl(rc);
            endTime=AutoReportGenDetailComponent.getReportEndTimeFromReportControl(rc);
            periodno=AutoReportGenDetailComponent.getReportPeriodNoFromReportControl(rc);
            succount++;
        }
        MerchantReportDefine updateDto=new MerchantReportDefine();
        updateDto.setId(mrd.getId());
        updateDto.setOpeTime(new Date());
        updateDto.setOpeNum(succount);
        updateDto.setReportPeriodNo(periodno);
        updateDto.setStartTime(startTime);
        updateDto.setEndTime(endTime);
        if (autoReportComponent.getMerchantReportDefineService().updateModel(cssf,updateDto)!=1){
            BaseNnte.setRetFalse(ret, 1002,"保存报表产生信息时失败");
            return ret;//如果没有取得查询数据，返回错误
        }
        BaseNnte.setRetTrue(ret,"产生报表文件成功");
        return ret;
    }

    //产生一张具体的报表
    public Map<String, Object> genReportFile(BaseMerchantOperator operator,ConnSqlSessionFactory cssf,List<MerchantReportQuery> queryList,ReportControl rc){
        Date createStartTime=new Date();
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        //先生成查询数据
        for(MerchantReportQuery query:queryList){
            //先对查询的SQL语句进行替换-----------------
            String srcQuery=query.getQuerySql();
            query.setQuerySql(autoReportQueryComponent.replaceQuerySql(query.getQuerySql(),rc,null,null));
            //执行查询取得数据--------------------------
            Map<String, Object> retQuery = autoReportQueryComponent.execQuerySqlForContent(query);
            query.setQuerySql(srcQuery);
            if (!BaseNnte.getRetSuc(retQuery)){
                BaseNnte.setRetFalse(ret, 1002,"没有取得查询数据");
                return ret;//如果没有取得查询数据，返回错误
            }
            rc.getReportDataEnv().put(query.getQueryCode(),retQuery.get("rows"));
        }
        //数据生成后按报表控制进行数据输出
        //从文件服务器下载模板文件,生成临时文件----
        String fn=downloadReportTemplateFile(rc.getReportDefine());
        //---------------------------------------
        XSSFWorkbookAndOPC wao=autoReportExcelComponent.openExcelTemplate(fn);
        if (wao==null){
            BaseNnte.setRetFalse(ret, 1002,"不能按模板文件生成报表文件");
            FileUtil.deleteFile(fn);
            return ret;//不能按模板文件生成报表文件
        }
        String outfn=null;
        String outpfn=null;
        try {
            //--文件已打开，进行数据输出-----------
            autoReportExcelComponent.outputDataToReportFile(wao, rc);
            //--数据输出结束，保存文件-------------
            outfn = FileUtil.genTmpFileName(FileUtil.getExtention(fn));
            outpfn = autoReportExcelComponent.saveExcelFile(wao,config.getConfig("reportConvPdf"), outfn); //返回报表文件的绝对路径
        }catch (Exception e){
            e.printStackTrace();
            BaseNnte.setRetFalse(ret, 9999,"向Excel文件输出数据时异常");
            autoReportExcelComponent.closeExcelTemplate(wao);
            FileUtil.deleteFile(fn);
            return ret;
        }
        autoReportExcelComponent.closeExcelTemplate(wao);
        FileUtil.deleteFile(fn);
        //Excel文件生成成功，生成响应的PDF文件
        //Map converMap=convertPDF(outpfn);
        //--本地转换改为通过服务器转换并提交文件服务器---
        Map<String,Object> converMap=KingReportComponent.convExcelToPdf(config.getConfig("convPdfUrl"),
                config.getConfig("convPdfType"),outfn);
        FileUtil.deleteFile(outfn);//删除临时生成的Excel文件
        //--------------------------------------------
        if (!BaseNnte.getRetSuc(converMap)){
            BaseNnte.setRetFalse(ret, 1002,"Excel文件生成PDF文件失败("+converMap.get("msg")+")");
            return ret;
        }
        String pdfFile=StringUtils.defaultString(converMap.get("pdfFile"));
        String officeFile=StringUtils.defaultString(converMap.get("officeFile"));
        ret.put("pdfFileName",pdfFile);
        //文件保存结束，记录报表生成明细-------
        Map<String,Object> genMap=autoReportGenDetailComponent.saveReportGenDetail(cssf,operator,rc,officeFile,pdfFile,createStartTime);
        if (!BaseNnte.getRetSuc(genMap))
            return genMap;
        MerchantReportGendetail gendetail=(MerchantReportGendetail)genMap.get("gendetail");
        //保存报表记录
        Map<String,Object> recMap=autoReportRecComponent.saveReportRecFromGen(cssf,gendetail,rc.getReportDefine());
        if (!BaseNnte.getRetSuc(recMap))
            return recMap;
        BaseNnte.setRetTrue(ret,"生成报表成功");
        //-----------------------------------
        return ret;
    }

    @DBSrcTranc
    public Map<String,Object> onOpenBusiTypeReport(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        MerchantReportDefine reportDefine=autoReportComponent.getReportRecordByCode(cssf,loginMerchant.getId(),StringUtils.defaultString(paramMap.get("reportCode")));
        ret.put("reportDefine",reportDefine);
        if (StringUtils.isNotEmpty(reportDefine.getReportClass())){
            Map<String,Object> queryRet=autoReportQueryComponent.getReportCutQueryContent(cssf,reportDefine);
            if (BaseNnte.getRetSuc(queryRet)) {
                List<ObjKeyValue> cutContentList = (List<ObjKeyValue>) queryRet.get("cutContentList");
                List<TKeyValue<String, String>> TKVList = new ArrayList<>();
                for (ObjKeyValue okv : cutContentList) {
                    TKeyValue<String, String> ssKV = new TKeyValue<>(AutoReportQueryComponent.getReplaceContentByFormat(null, okv.getKey()),
                            AutoReportQueryComponent.getReplaceContentByFormat(null, okv.getValue()));
                    TKVList.add(ssKV);
                }
                ret.put("cutQuery",queryRet.get("cutQuery"));
                ret.put("cutKVList", TKVList);
            }
        }
        BaseNnte.setRetTrue(ret,"获取数据成功");
        return ret;
    }

    @DBSrcTranc
    public Map<String,Object> priOpenPeroidReport(Map<String, Object> paramMap, MerchantReportRec queryDto,
                                                  Integer off){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        queryDto.setParMerchantId(loginMerchant.getId());
        MerchantReportRec mmr=autoReportRecComponent.priOpenPeroidReport(cssf,queryDto,off);
        if (mmr==null){
            BaseNnte.setRetFalse(ret,1002,"没有找到特定的报表记录["+queryDto.getReportCode()+"]");
        }
        ret.put("merchantReportRec",mmr);
        BaseNnte.setRetTrue(ret,"获取数据成功");
        return ret;
    }
    //下载报表模板文件，生成临时文件，返回临时文件路径文件名
    public String downloadReportTemplateFile(MerchantReportDefine mrd){
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
                byte[] content=fdfsClientMgrComponent.downloadFile(config.getConfig("reportTemplate"),
                        ti.getSubmitName());
                if (content!=null){
                    return FileUtil.saveBufToTmpFile(content,FileUtil.getExtention(fileName));
                }
            }
        }
        return null;
    }
}
