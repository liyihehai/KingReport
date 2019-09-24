package com.nnte.kr_business.entity.autoReport;

public class ReportContrlCircleItem {
    private CircleItemType circleItemType;      //输出项类型
    private String fieldName;                   //输出项字段名
    private String rowNo;                       //输出位置：行号
    private String colNo;                       //输出位置：列号

    public enum CircleItemType {
        CIT_QueryFeild,     //查询字段
        CIT_EnvData,        //上下文环境数据
        CIT_NormalTxt;      //普通文本
        private CircleItemType() {
        }
    }
}
