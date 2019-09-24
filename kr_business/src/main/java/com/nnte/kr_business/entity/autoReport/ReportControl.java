package com.nnte.kr_business.entity.autoReport;

import com.nnte.framework.entity.KeyValue;

import java.util.List;

/*
* 报表控制类，可输出为报表控制文件
* */
public class ReportControl {
    private String reportCode;          //报表代码
    private String spliteKeyField;      //分割字段名
    private String spliteNameField;     //分割字段名
    private List<KeyValue> spliteList;  //分割查询结果
    private List<ReportContrlCircle> circleList;    //输出循环列表
}
