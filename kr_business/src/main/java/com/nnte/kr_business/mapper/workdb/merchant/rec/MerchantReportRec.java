package com.nnte.kr_business.mapper.workdb.merchant.rec;
import com.nnte.framework.base.BaseModel;
import com.nnte.framework.annotation.DBPKColum;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-10-07 10:16:31>
 */
public class MerchantReportRec extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private String reportCode;
    private String reportTitle;
    private String reportBusiType;
    private String reportClass;
    private String cutName;
    private String cutValue;
    private Integer periodNo;
    private String reportPeriod;
    private Date startTime;
    private Date endTime;
    private String reportName;
    private String reportFileName;
    private Integer reportState;
    private Date updateTime;
    private Long updateOperId;

    public MerchantReportRec(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getReportCode(){ return reportCode;}
    public void setReportCode(String  reportCode){ this.reportCode = reportCode;}
    public String  getReportTitle(){ return reportTitle;}
    public void setReportTitle(String  reportTitle){ this.reportTitle = reportTitle;}
    public String  getReportBusiType(){ return reportBusiType;}
    public void setReportBusiType(String  reportBusiType){ this.reportBusiType = reportBusiType;}
    public String  getReportClass(){ return reportClass;}
    public void setReportClass(String  reportClass){ this.reportClass = reportClass;}
    public String  getCutName(){ return cutName;}
    public void setCutName(String  cutName){ this.cutName = cutName;}
    public String  getCutValue(){ return cutValue;}
    public void setCutValue(String  cutValue){ this.cutValue = cutValue;}
    public Integer  getPeriodNo(){ return periodNo;}
    public void setPeriodNo(Integer  periodNo){ this.periodNo = periodNo;}
    public String  getReportPeriod(){ return reportPeriod;}
    public void setReportPeriod(String  reportPeriod){ this.reportPeriod = reportPeriod;}
    public Date  getStartTime(){ return startTime;}
    public void setStartTime(Date  startTime){ this.startTime = startTime;}
    public Date  getEndTime(){ return endTime;}
    public void setEndTime(Date  endTime){ this.endTime = endTime;}
    public String  getReportName(){ return reportName;}
    public void setReportName(String  reportName){ this.reportName = reportName;}
    public String  getReportFileName(){ return reportFileName;}
    public void setReportFileName(String  reportFileName){ this.reportFileName = reportFileName;}
    public Integer  getReportState(){ return reportState;}
    public void setReportState(Integer  reportState){ this.reportState = reportState;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
    public Long  getUpdateOperId(){ return updateOperId;}
    public void setUpdateOperId(Long  updateOperId){ this.updateOperId = updateOperId;}
}
