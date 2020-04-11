package com.nnte.kr_business.entity.autoReport;

import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 报表控制类，可输出为报表控制文件
* */
@Getter
@Setter
@NoArgsConstructor
public class ReportControl {
    private String reportCode;          //报表代码
    private MerchantReportDefine reportDefine;                      //报表定义
    private Map<String,Object> reportDataEnv=new HashMap<>();       //报表数据环境
    private List<ReportControlCircle> circleList=new ArrayList<>();  //输出循环列表
}
