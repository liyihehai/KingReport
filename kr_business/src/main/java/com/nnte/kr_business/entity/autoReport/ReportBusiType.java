package com.nnte.kr_business.entity.autoReport;

public class ReportBusiType {

    private Long parMerchantId;
    private String busiTypeCode;
    private String busiTypeName;
    private Integer busiTypeState;

    public Long getParMerchantId() {
        return parMerchantId;
    }

    public void setParMerchantId(Long parMerchantId) {
        this.parMerchantId = parMerchantId;
    }

    public String getBusiTypeCode() {
        return busiTypeCode;
    }

    public void setBusiTypeCode(String busiTypeCode) {
        this.busiTypeCode = busiTypeCode;
    }

    public String getBusiTypeName() {
        return busiTypeName;
    }

    public void setBusiTypeName(String busiTypeName) {
        this.busiTypeName = busiTypeName;
    }

    public Integer getBusiTypeState() {
        return busiTypeState;
    }

    public void setBusiTypeState(Integer busiTypeState) {
        this.busiTypeState = busiTypeState;
    }
}
