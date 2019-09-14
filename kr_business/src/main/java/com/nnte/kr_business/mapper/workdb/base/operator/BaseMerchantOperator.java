package com.nnte.kr_business.mapper.workdb.base.operator;

import com.nnte.framework.annotation.DBPKColum;
import com.nnte.framework.base.BaseModel;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-08-05 10:11:44>
 */
public class BaseMerchantOperator extends BaseModel {
    @DBPKColum private Long id;
    private Long parMerchantId;
    private String opeCode;
    private String opeName;
    private String opeAccount;
    private String opePassword;
    private String mobilePhone;
    private Integer opeState;
    private Date createTime;
    private Date updateTime;
    private Integer operatorType;

    public BaseMerchantOperator(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getOpeCode(){ return opeCode;}
    public void setOpeCode(String  opeCode){ this.opeCode = opeCode;}
    public String  getOpeName(){ return opeName;}
    public void setOpeName(String  opeName){ this.opeName = opeName;}
    public String  getOpeAccount(){ return opeAccount;}
    public void setOpeAccount(String  opeAccount){ this.opeAccount = opeAccount;}
    public String  getOpePassword(){ return opePassword;}
    public void setOpePassword(String  opePassword){ this.opePassword = opePassword;}
    public String  getMobilePhone(){ return mobilePhone;}
    public void setMobilePhone(String  mobilePhone){ this.mobilePhone = mobilePhone;}
    public Integer  getOpeState(){ return opeState;}
    public void setOpeState(Integer  opeState){ this.opeState = opeState;}
    public Date  getCreateTime(){ return createTime;}
    public void setCreateTime(Date  createTime){ this.createTime = createTime;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
    public Integer  getOperatorType(){ return operatorType;}
    public void setOperatorType(Integer  operatorType){ this.operatorType = operatorType;}
}
