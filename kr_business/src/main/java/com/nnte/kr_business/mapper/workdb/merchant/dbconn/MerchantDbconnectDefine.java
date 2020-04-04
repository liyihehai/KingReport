package com.nnte.kr_business.mapper.workdb.merchant.dbconn;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nnte.framework.annotation.DBPKColum;
import com.nnte.framework.base.BaseModel;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-09-08 22:40:39>
 */
public class MerchantDbconnectDefine extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private String connCode;
    private String connName;
    private String dbType;
    private String dbIp;
    private Long dbPort;
    private String dbSchema;
    private String dbUser;
    private String dbPassword;
    private Integer connState;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    private Long createOperId;
    private String createOperName;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    public MerchantDbconnectDefine(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getConnCode(){ return connCode;}
    public void setConnCode(String  connCode){ this.connCode = connCode;}
    public String  getConnName(){ return connName;}
    public void setConnName(String  connName){ this.connName = connName;}
    public String  getDbType(){ return dbType;}
    public void setDbType(String  dbType){ this.dbType = dbType;}
    public String  getDbIp(){ return dbIp;}
    public void setDbIp(String  dbIp){ this.dbIp = dbIp;}
    public Long  getDbPort(){ return dbPort;}
    public void setDbPort(Long  dbPort){ this.dbPort = dbPort;}
    public String  getDbSchema(){ return dbSchema;}
    public void setDbSchema(String  dbSchema){ this.dbSchema = dbSchema;}
    public String  getDbUser(){ return dbUser;}
    public void setDbUser(String  dbUser){ this.dbUser = dbUser;}
    public String  getDbPassword(){ return dbPassword;}
    public void setDbPassword(String  dbPassword){ this.dbPassword = dbPassword;}
    public Integer  getConnState(){ return connState;}
    public void setConnState(Integer  connState){ this.connState = connState;}
    public Date  getCreateTime(){ return createTime;}
    public void setCreateTime(Date  createTime){ this.createTime = createTime;}
    public Long  getCreateOperId(){ return createOperId;}
    public void setCreateOperId(Long  createOperId){ this.createOperId = createOperId;}
    public String  getCreateOperName(){ return createOperName;}
    public void setCreateOperName(String  createOperName){ this.createOperName = createOperName;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
}
