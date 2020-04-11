package com.nnte.kr_business.entity.autoReport;

import com.nnte.framework.entity.DataColDef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportControlCircleItem {
    private DataColDef.DataType dataType;       //数据类型
    private String outText;                     //输出项字段名
    private String cellPoint;                   //输出位置：如C2
    //如果为整数或浮点数据，采用String.Format()函数格式化
    //如果为日期数据，采用DateUtil支持的格式化
    private String format;                      //输出格式
    public ReportControlCircleItem(DataColDef.DataType dataType,String outText,
                                   String cellPoint,String format){
        setDataType(dataType);
        setOutText(outText);
        setCellPoint(cellPoint);
        setFormat(format);
    }
}
