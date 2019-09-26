package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DBSchemaInterface;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.entity.autoReport.ReportControl;
import com.nnte.kr_business.entity.autoReport.ReportControlCircle;
import com.nnte.kr_business.entity.autoReport.ReportControlCircleItem;
import com.nnte.kr_business.entity.autoReport.ReportPeriodSetting;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefine;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetail;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* 报表服务组件
* */
@Component
@WorkDBAspect
public class AutoReportServerComponent {
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;
    @Autowired
    private ReportPeriodComponent reportPeriodComponent;
    @Autowired
    private AutoReportDBConnComponent autoReportDBConnComponent;

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
            MerchantReportQuery cutQuery=autoReportQueryComponent.getReportQueryByCode(cssf,mrd.getParMerchantId(),mrd.getReportClass());
            if (cutQuery==null){
                BaseNnte.setRetFalse(ret, 1002,"报表未找到有效的分割查询");
                return ret;
            }
            //执行查询，按KEY-NAME取得查询结果
            Map<String,Object> queryRet=execQuerySqlForContent(cutQuery);
            if (!BaseNnte.getRetSuc(queryRet))
                return queryRet;
            if (NumberUtil.getDefaultInteger(queryRet.get("count"))<=0){
                BaseNnte.setRetFalse(ret, 1002,"报表分割查询没有数据，不能生成报表");
                return ret;
            }
            List<JSONObject> cutList=(List<JSONObject>)queryRet.get("rows");
            for(JSONObject jobj:cutList){
                ReportControl RC=new ReportControl();
                RC.setReportCode(mrd.getReportCode());
                RC.setReportDefine(mrd);
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.CUT_KEY,jobj.get(mrd.getCutKeyField()));
                RC.getReportDataEnv().put(AutoReportQueryComponent.ResKeyWord.CUT_NAME,jobj.get(mrd.getCutNameField()));
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
        ReportControlCircleItem cci=new ReportControlCircleItem();
        cci.setOutText(AutoReportQueryComponent.ResKeyWord.CUT_KEY);
        cci.setDataType(DataColDef.DataType.DATA_INT);
        cci.setCellPoint("C2");
        cci.setFormat("");
        cc.getCircleItemList().add(cci);
        ReportControlCircleItem cci1=new ReportControlCircleItem();
        cci1.setOutText(AutoReportQueryComponent.ResKeyWord.CUT_NAME);
        cci1.setDataType(DataColDef.DataType.DATA_STRING);
        cci1.setCellPoint("E2");
        cci1.setFormat("");
        cc.getCircleItemList().add(cci1);
        ReportControlCircleItem cci2=new ReportControlCircleItem();
        cci2.setOutText(AutoReportQueryComponent.ResKeyWord.START_TIME);
        cci2.setDataType(DataColDef.DataType.DATA_DATE);
        cci2.setCellPoint("G2");
        cci2.setFormat("yyyy-MM-dd");
        cc.getCircleItemList().add(cci2);
        ReportControlCircleItem cci3=new ReportControlCircleItem();
        cci3.setOutText(AutoReportQueryComponent.ResKeyWord.END_TIME);
        cci3.setDataType(DataColDef.DataType.DATA_DATE);
        cci3.setCellPoint("I2");
        cci3.setFormat("yyyy-MM-dd");
        cc.getCircleItemList().add(cci3);
        rc.getCircleList().add(cc);
        //---------------------------------------------------------------
        ReportControlCircle cc1=new ReportControlCircle();
        cc1.setCircleItemType(ReportControlCircle.CircleItemType.CIT_QueryFeild);//数据环境循环
        //---------------------------------------------------------------
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

    public Map<String,Object> execQuerySqlForContent(MerchantReportQuery query){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (query==null || query.getId()==null) {
            BaseNnte.setRetFalse(ret, 1002,"查询定义不合法");
            return ret;
        }
        if (query.getConnId()==null|| query.getConnId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"查询的数据连接不合法");
            return ret;
        }
        MerchantDbconnectDefine mdbd=autoReportDBConnComponent.getReportDBConnDefineById(query.getConnId());
        if (mdbd==null){
            BaseNnte.setRetFalse(ret, 1002,"未找到查询的数据连接");
            return ret;
        }
        DBSchemaInterface DBSI= DynamicDatabaseSourceHolder.getSchemaInterfaceByDBType(mdbd.getDbType());
        if (DBSI==null)
        {
            BaseNnte.setRetFalse(ret, 1002,"查询的数据连接类型不合法");
            return ret;
        }
        Connection conn=DBSI.connectNoPoolDataSource(mdbd.getDbIp(),mdbd.getDbPort(),mdbd.getDbSchema(),
                mdbd.getDbUser(),mdbd.getDbPassword());
        try {
            if (conn == null || conn.isClosed() == true) {
                BaseNnte.setRetFalse(ret, 1002,"不能正常连接查询的数据库");
                return ret;
            }
            return DBSI.execSqlForContent(conn,query.getQuerySql());
        }catch (Exception e){
            BaseNnte.setRetFalse(ret, 9999,"执行查询的SQL语句异常");
            return ret;
        }finally {
            DynamicDatabaseSourceHolder.CloseDBConnection(conn);
        }
    }

    public static void main(String[] args){
        MerchantReportDefine mrd=new MerchantReportDefine();
        mrd.setReportPeriodNo(1);
        mrd.setReportPeriod(ReportPeriodComponent.Period.Day_Report);
        mrd.setStartDate(DateUtils.stringToDate("2019-06-07",DateUtils.DF_YMD));
        mrd.setEndTime(DateUtils.stringToDate("2019-06-07 23:59:59.999",DateUtils.DF_YMD_HMSSSS));

        MerchantReportGendetail mrg=new MerchantReportGendetail();
        ReportPeriodSetting RPS=new ReportPeriodSetting();
        RPS.setEndTimeDay("23:59:59.999");
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
