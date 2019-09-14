package com.nnte.kr_business.mapper.workdb.merchant.query;


import com.nnte.framework.annotation.DBPKColum;
import com.nnte.framework.base.BaseModel;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-08-17 08:29:01>
 */
public class MerchantReportQuery extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private Long reportId;
    private Long connId;
    private String queryCode;
    private String queryName;
    private String cutFlag;
    private String cutTypeName;
    private String queryType;
    private Long maxRowCount;
    private String querySql;
    private String querySqlCols;
    private Date createTime;
    private Long createOperId;
    private String createOperName;
    private Date updateTime;

    public MerchantReportQuery(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public Long  getReportId(){ return reportId;}
    public void setReportId(Long  reportId){ this.reportId = reportId;}
    public Long  getConnId(){ return connId;}
    public void setConnId(Long  connId){ this.connId = connId;}
    public String  getQueryCode(){ return queryCode;}
    public void setQueryCode(String  queryCode){ this.queryCode = queryCode;}
    public String  getQueryName(){ return queryName;}
    public void setQueryName(String  queryName){ this.queryName = queryName;}
    public String  getCutFlag(){ return cutFlag;}
    public void setCutFlag(String  cutFlag){ this.cutFlag = cutFlag;}
    public String  getCutTypeName(){ return cutTypeName;}
    public void setCutTypeName(String  cutTypeName){ this.cutTypeName = cutTypeName;}
    public String  getQueryType(){ return queryType;}
    public void setQueryType(String  queryType){ this.queryType = queryType;}
    public Long  getMaxRowCount(){ return maxRowCount;}
    public void setMaxRowCount(Long  maxRowCount){ this.maxRowCount = maxRowCount;}
    public String  getQuerySql(){ return querySql;}
    public void setQuerySql(String  querySql){ this.querySql = querySql;}
    public String  getQuerySqlCols(){ return querySqlCols;}
    public void setQuerySqlCols(String  querySqlCols){ this.querySqlCols = querySqlCols;}
    public Date  getCreateTime(){ return createTime;}
    public void setCreateTime(Date  createTime){ this.createTime = createTime;}
    public Long  getCreateOperId(){ return createOperId;}
    public void setCreateOperId(Long  createOperId){ this.createOperId = createOperId;}
    public String  getCreateOperName(){ return createOperName;}
    public void setCreateOperName(String  createOperName){ this.createOperName = createOperName;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
}
