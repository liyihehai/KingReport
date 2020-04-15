package com.nnte.kr_business.entity.autoReport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nnte.framework.utils.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
* 报表输出循环
* */
@Getter
@Setter
@NoArgsConstructor
public class ReportControlCircle {
    private CircleItemType circleItemType;  //输出项类型
    private String sheetName="sheet1";      //页面名称，默认sheet1
    private String queryCode;               //查询代码 circleItemType=CIT_QueryFeild有效
    @JsonDeserialize(as = List.class,contentAs = ReportControlCircleItem.class)
    @JsonSerialize(using= JsonUtil.ListJsonSerializer.class)
    private List<ReportControlCircleItem> circleItemList=new ArrayList<>();
    //------------------------------------------------------
    public enum CircleItemType {
        CIT_QueryFeild,     //查询字段
        CIT_EnvData,        //上下文环境数据
        CIT_NormalTxt;      //普通文本
        private CircleItemType() {
        }
    }
}
