package com.nnte.kr_business.mapper.workdb.base.merchant;

import com.nnte.framework.annotation.DBPKColum;
import com.nnte.framework.base.BaseModel;

import java.util.Date;
/*
 * 自动代码 请勿更改 <2019-08-05 09:51:15>
 */
public class BaseMerchant extends BaseModel {
    @DBPKColum private Long id;
    private String code;
    private String merchantName;
    private String subMerchantName;
    private Long parMerchantId;
    private Integer merchantType;
    private Long cityDomainId;
    private String cityArea;
    private String storeAddress;
    private String zipcode;
    private String locationCoordinate;
    private Double longitude;
    private Double latitude;
    private String linkName;
    private String relationPhone;
    private String csPhone;
    private String email;
    private String shortIntroduction;
    private String headPic;
    private String pictureUrl;
    private String pictureUrl2;
    private String pictureUrl3;
    private Integer storeType;
    private String remark;
    private String legalName;
    private String legalIdNum;
    private String idcertPic;
    private String bsLicensePic;
    private String taxRegPic;
    private String otherCertPic;
    private Date createTime;
    private Date lastUpdateTime;
    private Integer state;

    public BaseMerchant(){}

    public Long  getId(){ return id;}
    public void setId(Long  id){ this.id = id;}
    public String  getCode(){ return code;}
    public void setCode(String  code){ this.code = code;}
    public String  getMerchantName(){ return merchantName;}
    public void setMerchantName(String  merchantName){ this.merchantName = merchantName;}
    public String  getSubMerchantName(){ return subMerchantName;}
    public void setSubMerchantName(String  subMerchantName){ this.subMerchantName = subMerchantName;}
    public Long  getParMerchantId(){ return parMerchantId;}
    public void setParMerchantId(Long  parMerchantId){ this.parMerchantId = parMerchantId;}
    public Integer  getMerchantType(){ return merchantType;}
    public void setMerchantType(Integer  merchantType){ this.merchantType = merchantType;}
    public Long  getCityDomainId(){ return cityDomainId;}
    public void setCityDomainId(Long  cityDomainId){ this.cityDomainId = cityDomainId;}
    public String  getCityArea(){ return cityArea;}
    public void setCityArea(String  cityArea){ this.cityArea = cityArea;}
    public String  getStoreAddress(){ return storeAddress;}
    public void setStoreAddress(String  storeAddress){ this.storeAddress = storeAddress;}
    public String  getZipcode(){ return zipcode;}
    public void setZipcode(String  zipcode){ this.zipcode = zipcode;}
    public String  getLocationCoordinate(){ return locationCoordinate;}
    public void setLocationCoordinate(String  locationCoordinate){ this.locationCoordinate = locationCoordinate;}
    public Double  getLongitude(){ return longitude;}
    public void setLongitude(Double  longitude){ this.longitude = longitude;}
    public Double  getLatitude(){ return latitude;}
    public void setLatitude(Double  latitude){ this.latitude = latitude;}
    public String  getLinkName(){ return linkName;}
    public void setLinkName(String  linkName){ this.linkName = linkName;}
    public String  getRelationPhone(){ return relationPhone;}
    public void setRelationPhone(String  relationPhone){ this.relationPhone = relationPhone;}
    public String  getCsPhone(){ return csPhone;}
    public void setCsPhone(String  csPhone){ this.csPhone = csPhone;}
    public String  getEmail(){ return email;}
    public void setEmail(String  email){ this.email = email;}
    public String  getShortIntroduction(){ return shortIntroduction;}
    public void setShortIntroduction(String  shortIntroduction){ this.shortIntroduction = shortIntroduction;}
    public String  getHeadPic(){ return headPic;}
    public void setHeadPic(String  headPic){ this.headPic = headPic;}
    public String  getPictureUrl(){ return pictureUrl;}
    public void setPictureUrl(String  pictureUrl){ this.pictureUrl = pictureUrl;}
    public String  getPictureUrl2(){ return pictureUrl2;}
    public void setPictureUrl2(String  pictureUrl2){ this.pictureUrl2 = pictureUrl2;}
    public String  getPictureUrl3(){ return pictureUrl3;}
    public void setPictureUrl3(String  pictureUrl3){ this.pictureUrl3 = pictureUrl3;}
    public Integer  getStoreType(){ return storeType;}
    public void setStoreType(Integer  storeType){ this.storeType = storeType;}
    public String  getRemark(){ return remark;}
    public void setRemark(String  remark){ this.remark = remark;}
    public String  getLegalName(){ return legalName;}
    public void setLegalName(String  legalName){ this.legalName = legalName;}
    public String  getLegalIdNum(){ return legalIdNum;}
    public void setLegalIdNum(String  legalIdNum){ this.legalIdNum = legalIdNum;}
    public String  getIdcertPic(){ return idcertPic;}
    public void setIdcertPic(String  idcertPic){ this.idcertPic = idcertPic;}
    public String  getBsLicensePic(){ return bsLicensePic;}
    public void setBsLicensePic(String  bsLicensePic){ this.bsLicensePic = bsLicensePic;}
    public String  getTaxRegPic(){ return taxRegPic;}
    public void setTaxRegPic(String  taxRegPic){ this.taxRegPic = taxRegPic;}
    public String  getOtherCertPic(){ return otherCertPic;}
    public void setOtherCertPic(String  otherCertPic){ this.otherCertPic = otherCertPic;}
    public Date  getCreateTime(){ return createTime;}
    public void setCreateTime(Date  createTime){ this.createTime = createTime;}
    public Date  getLastUpdateTime(){ return lastUpdateTime;}
    public void setLastUpdateTime(Date  lastUpdateTime){ this.lastUpdateTime = lastUpdateTime;}
    public Integer  getState(){ return state;}
    public void setState(Integer  state){ this.state = state;}
}
