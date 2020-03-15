package com.nnte.kr_business.mapper.workdb.merchant.report;
import com.nnte.framework.base.BaseModel;
import com.nnte.framework.annotation.DBPKColum;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2020-03-12 11:02:51>
 */
public class MerchantReportDefine extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private String reportCode;
    private String reportName;
    private String reportClass;
    private String reportBusiType;
    private String reportPeriod;
    private Date startDate;
    private String templateFile;
    private String tempfileCollect;
    private Integer reportState;
    private Date createTime;
    private Long createOperId;
    private String createOperName;
    private Date updateTime;
    private Date opeTime;
    private Integer opeNum;
    private Integer reportPeriodNo;
    private Date startTime;
    private Date endTime;
    private String opeMsg;
    private String cutKeyField;
    private String cutNameField;
    private String cutKeyType;
    private String cutNameType;

    public MerchantReportDefine(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getReportCode(){ return reportCode;}
    public void setReportCode(String  reportCode){ this.reportCode = reportCode;}
    public String  getReportName(){ return reportName;}
    public void setReportName(String  reportName){ this.reportName = reportName;}
    public String  getReportClass(){ return reportClass;}
    public void setReportClass(String  reportClass){ this.reportClass = reportClass;}
    public String  getReportBusiType(){ return reportBusiType;}
    public void setReportBusiType(String  reportBusiType){ this.reportBusiType = reportBusiType;}
    public String  getReportPeriod(){ return reportPeriod;}
    public void setReportPeriod(String  reportPeriod){ this.reportPeriod = reportPeriod;}
    public Date  getStartDate(){ return startDate;}
    public void setStartDate(Date  startDate){ this.startDate = startDate;}
    public String  getTemplateFile(){ return templateFile;}
    public void setTemplateFile(String  templateFile){ this.templateFile = templateFile;}
    public String  getTempfileCollect(){ return tempfileCollect;}
    public void setTempfileCollect(String  tempfileCollect){ this.tempfileCollect = tempfileCollect;}
    public Integer  getReportState(){ return reportState;}
    public void setReportState(Integer  reportState){ this.reportState = reportState;}
    public Date  getCreateTime(){ return createTime;}
    public void setCreateTime(Date  createTime){ this.createTime = createTime;}
    public Long  getCreateOperId(){ return createOperId;}
    public void setCreateOperId(Long  createOperId){ this.createOperId = createOperId;}
    public String  getCreateOperName(){ return createOperName;}
    public void setCreateOperName(String  createOperName){ this.createOperName = createOperName;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
    public Date  getOpeTime(){ return opeTime;}
    public void setOpeTime(Date  opeTime){ this.opeTime = opeTime;}
    public Integer  getOpeNum(){ return opeNum;}
    public void setOpeNum(Integer  opeNum){ this.opeNum = opeNum;}
    public Integer  getReportPeriodNo(){ return reportPeriodNo;}
    public void setReportPeriodNo(Integer  reportPeriodNo){ this.reportPeriodNo = reportPeriodNo;}
    public Date  getStartTime(){ return startTime;}
    public void setStartTime(Date  startTime){ this.startTime = startTime;}
    public Date  getEndTime(){ return endTime;}
    public void setEndTime(Date  endTime){ this.endTime = endTime;}
    public String  getOpeMsg(){ return opeMsg;}
    public void setOpeMsg(String  opeMsg){ this.opeMsg = opeMsg;}
    public String  getCutKeyField(){ return cutKeyField;}
    public void setCutKeyField(String  cutKeyField){ this.cutKeyField = cutKeyField;}
    public String  getCutNameField(){ return cutNameField;}
    public void setCutNameField(String  cutNameField){ this.cutNameField = cutNameField;}
    public String  getCutKeyType(){ return cutKeyType;}
    public void setCutKeyType(String  cutKeyType){ this.cutKeyType = cutKeyType;}
    public String  getCutNameType(){ return cutNameType;}
    public void setCutNameType(String  cutNameType){ this.cutNameType = cutNameType;}
}
