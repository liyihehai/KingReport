package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.SysParamItem;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.component.base.SysParamComponent;
import com.nnte.kr_business.entity.autoReport.ReportPeriodSetting;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class ReportPeriodComponent {
    @Autowired
    private SysParamComponent sysParamComponent;
    //组件数据字典区域
    //------------------------------------------
    @SysParamItem(value = "商户报表周期时间设置",type = SysParamComponent.ParamType.TYPE_MER_SINGLE)
    public final static String REPORT_MERCHANT_PERIOD_SETTING="REPORT_MERCHANT_PERIOD_SETTING";
    public static class Period{
        public static String Day_Report     ="D";
        public static String Week_Report    ="W";
        public static String Month_Report   ="M";
        public static String Quarter_Report ="Q";
        public static String HalfYear_Report="H";
        public static String Year_Report    ="Y";
    }
    @DataLibItem("报表周期")
    public final static List<KeyValue> LibReportPeriod = new ArrayList<>();
    static
    {//报表周期:D:日报表，W:周报表，M：月报表，Q：季报表，HY：半年报，Y年报
        LibReportPeriod.add(new KeyValue(Period.Day_Report,"日报表"));
        LibReportPeriod.add(new KeyValue(Period.Week_Report,"周报表"));
        LibReportPeriod.add(new KeyValue(Period.Month_Report,"月报表"));
        LibReportPeriod.add(new KeyValue(Period.Quarter_Report,"季报表"));
        LibReportPeriod.add(new KeyValue(Period.HalfYear_Report,"半年报"));
        LibReportPeriod.add(new KeyValue(Period.Year_Report,"年报表"));
    }
    //------------------------------------------
    //取得商户的报表周期设置
    public ReportPeriodSetting getMerchantPeriodSetting(Long parMerchantId){
        String jsonPeriod=sysParamComponent.getMerchantSingleParams(parMerchantId,REPORT_MERCHANT_PERIOD_SETTING,
                SysParamComponent.ValCol.VAL_TXT);
        ReportPeriodSetting ret=new ReportPeriodSetting();
        if (StringUtils.isNotEmpty(jsonPeriod)){
            Object obj= JsonUtil.getObject4JsonString(jsonPeriod,ReportPeriodSetting.class);
            if (obj!=null){
                ReportPeriodSetting settingObj=(ReportPeriodSetting) obj;
                setReportPeriodSetting(settingObj,ret);
            }
        }
        return ret;
    }
    public boolean setReportPeriodSetting(ReportPeriodSetting srcPeriod,
                                          ReportPeriodSetting newPeriod){
        if (setPeriodDay(srcPeriod.getEndTimeDay(),newPeriod) &&
                setPeriodWeek(srcPeriod.getEndTimeWeek(),newPeriod) &&
                setPeriodMonth(srcPeriod.getEndTimeMonth(),newPeriod) &&
                setPeriodQuarter(srcPeriod.getEndTimeQuarter(),newPeriod) &&
                setPeriodHalfYear(srcPeriod.getEndTimeHalfYear(),newPeriod) &&
                setPeriodYear(srcPeriod.getEndTimeYear(),newPeriod)
        ){
            return true;
        }
        return false;
    }
    //保存商户周期参数设置
    @DBSrcTranc
    public Map<String, Object> saveReportPeriodSetting(Map<String, Object> paramMap,Long parmerchantId, ReportPeriodSetting period){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator loginOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        if (parmerchantId==null||parmerchantId<=0||period==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误");
            return ret;
        }
        ReportPeriodSetting newPeriod= new ReportPeriodSetting();
        if (!setReportPeriodSetting(period,newPeriod)){
            BaseNnte.setRetFalse(ret,1002,"周期参数数值错误");
            return ret;
        }
        String jsonPeriod=JsonUtil.getJsonStringFromBean(newPeriod);
        if (StringUtils.isEmpty(jsonPeriod)){
            BaseNnte.setRetFalse(ret,1002,"周期参数对象转换错误");
            return ret;
        }
        Integer count=sysParamComponent.saveMerchantSingleParams(cssf,loginMerchant.getParMerchantId(),REPORT_MERCHANT_PERIOD_SETTING,
                SysParamComponent.ValCol.VAL_TXT,jsonPeriod,loginOperator.getId(),loginOperator.getOpeName());
        if (count==null || !count.equals(1)){
            BaseNnte.setRetFalse(ret,1002,"保存周期参数对象错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"保存周期参数对象成功");
        return ret;
    }
    //设置日报截止日期
    public boolean setPeriodDay(String day,ReportPeriodSetting period){
        //00:00:00.000~23:59:59.999
        String times[]=day.split(":");
        if (times==null || times.length!=3)
            return false;
        Integer n1= NumberUtil.getDefaultInteger(times[0]);
        if (n1<0 || n1>23)
            return false;
        Integer n2= NumberUtil.getDefaultInteger(times[1]);
        if (n2<0 || n2>59)
            return false;
        String[] n3String=times[2].split("\\.");
        if (n3String==null || n3String.length!=2)
            return false;
        Integer n3= NumberUtil.getDefaultInteger(n3String[0]);
        if (n2<0 || n2>59)
            return false;
        Integer n4= NumberUtil.getDefaultInteger(n3String[1]);
        if (n4<0 || n4>999)
            return false;
        StringBuffer buffer=new StringBuffer();
        buffer.append(StringUtils.addZeroForNum(n1.toString(),2,1));
        buffer.append(":");
        buffer.append(StringUtils.addZeroForNum(n2.toString(),2,1));
        buffer.append(":");
        buffer.append(StringUtils.addZeroForNum(n3.toString(),2,1));
        buffer.append(".");
        buffer.append(n4.toString());
        period.setEndTimeDay(buffer.toString());
        return true;
    }
    //设置周报截止日期（1~7）（默认7,周日的23:59:59.999）
    public boolean setPeriodWeek(String week,ReportPeriodSetting period){
        Integer n1= NumberUtil.getDefaultInteger(week);
        if (n1<1 || n1>7 )
            return false;
        period.setEndTimeWeek(n1.toString());
        return true;
    }
    //指定月报表的结束日数（1~28或0）（默认0,每月的最后一天的23:59:59.999）
    public boolean setPeriodMonth(String month,ReportPeriodSetting period){
        Integer n1= NumberUtil.getDefaultInteger(month);
        if (n1<0 || n1>28 )
            return false;
        period.setEndTimeMonth(n1.toString());
        return true;
    }
    private String checkPeriodMonthDay(String srcMD,int minMonth,int maxMonth,int minDay,int maxDay){
        String days[]=srcMD.split("-");
        if (days==null||days.length!=2)
            return null;
        Integer n1= NumberUtil.getDefaultInteger(days[0]);
        if (n1<minMonth || n1>maxMonth)
            return null;
        Integer n2= NumberUtil.getDefaultInteger(days[1]);
        if (n2<minDay || n2>maxDay)
            return null;
        if (n1.equals(2) && n2>28) //如果是2月，日期不能大于28号
            return null;
        if ((n1.equals(4)||n1.equals(6)||n1.equals(9)||n1.equals(11)) && n2>30)
            return null;    //非大月日期不能大于30
        return StringUtils.addZeroForNum(n1.toString(),2,1)+"-"+
                StringUtils.addZeroForNum(n2.toString(),2,1);
    }
    //指定季度报表的结束月日（1月1日~3月31日）（默认(03-31)每年1月1日开始，每3个月的最后一天的23:59:59.999）
    public boolean setPeriodQuarter(String quarter,ReportPeriodSetting period){
        String md=checkPeriodMonthDay(quarter,1,3,1,31);
        if (md==null)
            return false;
        period.setEndTimeQuarter(md);
        return true;
    }
    //指定半年报表的结束月日（1月1日~6月30日）（默认(06-30)每年1月1日开始，每6个月的最后一天的23:59:59.999）
    public boolean setPeriodHalfYear(String halfYear,ReportPeriodSetting period){
        String md=checkPeriodMonthDay(halfYear,1,6,1,30);
        if (md==null)
            return false;
        period.setEndTimeHalfYear(md);
        return true;
    }
    //指定年报表的结束月日（1月1日~12月31日）（默认(12-31)每年1月1日开始，到12月31日的23:59:59.999）
    public boolean setPeriodYear(String year,ReportPeriodSetting period){
        String md=checkPeriodMonthDay(year,1,12,1,31);
        if (md==null)
            return false;
        period.setEndTimeYear(md);
        return true;
    }
}
