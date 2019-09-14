package com.nnte.kr_business.entity.autoReport;
//报表周期设置，用于设置每种报表周期的最后时刻
public class ReportPeriodSetting {
    private String endTimeDay="23:59:59.999";//指定日报表的每天结束时间（00:00:00.000~23:59:59.999）（默认23:59:59.999）
    private String endTimeWeek="7";          //指定周报表的结束星期数（1~7）（默认7,周日的23:59:59.999）
    private String endTimeMonth="0";         //指定月报表的结束日数（1~28或0）（默认0,每月的最后一天的23:59:59.999）
    private String endTimeQuarter="03-31";   //指定季度报表的结束月日（1月1日~3月31日）（默认(03-31)每年1月1日开始，每3个月的最后一天的23:59:59.999）
    private String endTimeHalfYear="06-30";  //指定半年报表的结束月日（1月1日~6月30日）（默认(06-30)每年1月1日开始，每6个月的最后一天的23:59:59.999）
    private String endTimeYear="12-31";      //指定年报表的结束月日（1月1日~12月31日）（默认(12-31)每年1月1日开始，到12月31日的23:59:59.999）


    public String getEndTimeDay() {
        return endTimeDay;
    }

    public void setEndTimeDay(String endTimeDay) {
        this.endTimeDay = endTimeDay;
    }

    public String getEndTimeWeek() {
        return endTimeWeek;
    }

    public void setEndTimeWeek(String endTimeWeek) {
        this.endTimeWeek = endTimeWeek;
    }

    public String getEndTimeMonth() {
        return endTimeMonth;
    }

    public void setEndTimeMonth(String endTimeMonth) {
        this.endTimeMonth = endTimeMonth;
    }

    public String getEndTimeQuarter() {
        return endTimeQuarter;
    }

    public void setEndTimeQuarter(String endTimeQuarter) {
        this.endTimeQuarter = endTimeQuarter;
    }

    public String getEndTimeHalfYear() {
        return endTimeHalfYear;
    }

    public void setEndTimeHalfYear(String endTimeHalfYear) {
        this.endTimeHalfYear = endTimeHalfYear;
    }

    public String getEndTimeYear() {
        return endTimeYear;
    }

    public void setEndTimeYear(String endTimeYear) {
        this.endTimeYear = endTimeYear;
    }
}
