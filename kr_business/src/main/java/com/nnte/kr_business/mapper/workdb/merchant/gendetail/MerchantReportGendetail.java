package com.nnte.kr_business.mapper.workdb.merchant.gendetail;
import com.nnte.framework.base.BaseModel;
import com.nnte.framework.annotation.DBPKColum;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-09-23 13:27:56>
 */
public class MerchantReportGendetail extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private String reportCode;
    private String reportBusiType;
    private String cutName;
    private String cutValue;
    private Integer periodNo;
    private String reportPeriod;
    private Date startTime;
    private Date endTime;
    private String reportName;
    private String reportFileName;
    private Date createStartTime;
    private Long createOperId;
    private Date createEndTime;

    public MerchantReportGendetail(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getReportCode(){ return reportCode;}
    public void setReportCode(String  reportCode){ this.reportCode = reportCode;}
    public String  getReportBusiType(){ return reportBusiType;}
    public void setReportBusiType(String  reportBusiType){ this.reportBusiType = reportBusiType;}
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
    public Date  getCreateStartTime(){ return createStartTime;}
    public void setCreateStartTime(Date  createStartTime){ this.createStartTime = createStartTime;}
    public Long  getCreateOperId(){ return createOperId;}
    public void setCreateOperId(Long  createOperId){ this.createOperId = createOperId;}
    public Date  getCreateEndTime(){ return createEndTime;}
    public void setCreateEndTime(Date  createEndTime){ this.createEndTime = createEndTime;}
}
