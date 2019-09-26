package com.nnte.kr_business.entity.autoReport;

import java.util.ArrayList;
import java.util.List;

/*
* 报表输出循环
* */
public class ReportControlCircle {
    private CircleItemType circleItemType;  //输出项类型
    private String queryCode;               //查询代码 circleItemType=CIT_QueryFeild有效
    private List<ReportControlCircleItem> circleItemList=new ArrayList<>();
    //------------------------------------------------------
    public enum CircleItemType {
        CIT_QueryFeild,     //查询字段
        CIT_EnvData,        //上下文环境数据
        CIT_NormalTxt;      //普通文本
        private CircleItemType() {
        }
    }
    //-------------------------------------------------------
    public CircleItemType getCircleItemType() {
        return circleItemType;
    }
    public void setCircleItemType(CircleItemType circleItemType) {
        this.circleItemType = circleItemType;
    }
    public String getQueryCode() {
        return queryCode;
    }
    public void setQueryCode(String queryCode) {
        this.queryCode = queryCode;
    }
    public List<ReportControlCircleItem> getCircleItemList() {
        return circleItemList;
    }
}
