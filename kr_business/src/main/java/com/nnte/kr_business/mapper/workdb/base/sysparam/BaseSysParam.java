package com.nnte.kr_business.mapper.workdb.base.sysparam;

import com.nnte.framework.annotation.DBPKColum;
import com.nnte.framework.base.BaseModel;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-08-07 09:23:50>
 */
public class BaseSysParam extends BaseModel {
    @DBPKColum private Long id;
    private String paramType;
    private String paramName;
    private Long parMerchantId;
    private String classId;
    private String pkey;
    private String value1;
    private String value2;
    private String value5;
    private String valueText;
    private Integer paramState;
    private Date updateTime;
    private Long updateOpeId;
    private String updateOpeName;

    public BaseSysParam(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public String  getParamType(){ return paramType;}
    public void setParamType(String  paramType){ this.paramType = paramType;}
    public String  getParamName(){ return paramName;}
    public void setParamName(String  paramName){ this.paramName = paramName;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public String  getClassId(){ return classId;}
    public void setClassId(String  classId){ this.classId = classId;}
    public String  getPkey(){ return pkey;}
    public void setPkey(String  pkey){ this.pkey = pkey;}
    public String  getValue1(){ return value1;}
    public void setValue1(String  value1){ this.value1 = value1;}
    public String  getValue2(){ return value2;}
    public void setValue2(String  value2){ this.value2 = value2;}
    public String  getValue5(){ return value5;}
    public void setValue5(String  value5){ this.value5 = value5;}
    public String  getValueText(){ return valueText;}
    public void setValueText(String  valueText){ this.valueText = valueText;}
    public Integer  getParamState(){ return paramState;}
    public void setParamState(Integer  paramState){ this.paramState = paramState;}
    public Date  getUpdateTime(){ return updateTime;}
    public void setUpdateTime(Date  updateTime){ this.updateTime = updateTime;}
    public Long  getUpdateOpeId(){ return updateOpeId;}
    public void setUpdateOpeId(Long  updateOpeId){ this.updateOpeId = updateOpeId;}
    public String  getUpdateOpeName(){ return updateOpeName;}
    public void setUpdateOpeName(String  updateOpeName){ this.updateOpeName = updateOpeName;}
}
