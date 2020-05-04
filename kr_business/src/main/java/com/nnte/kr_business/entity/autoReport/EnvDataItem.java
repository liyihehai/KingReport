package com.nnte.kr_business.entity.autoReport;

import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.kr_business.component.autoReport.AutoReportQueryComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 环境变量类
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvDataItem {
    private KeyValue dataItemOption;
    private KeyValue dataItemType;
    /**
     * 取得所有环境变量定义列表
     * */
    public static List<EnvDataItem> getAllEnvDataItemList(){
        List<EnvDataItem> retList=new ArrayList<>();
        //-------------------------------------------------------------------------------------------
        //AutoReportQueryComponent.ResKeyWord.START_TIME,"报表开始时间";
        //AutoReportQueryComponent.ResKeyWord.END_TIME,"报表结束时间";
        //AutoReportQueryComponent.ResKeyWord.PERIOD_NO,"报表期数";
        //AutoReportQueryComponent.ResKeyWord.CUT_KEY,"报表分割字段值";
        //AutoReportQueryComponent.ResKeyWord.CUT_NAME,"报表分割字段名";
        //--------------------------------------------------------------------------------------------
        //LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_STRING.toString(),"字符串"));
        //LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_INT.toString(),"整数"));
        //LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_FLOT.toString(),"浮点数"));
        //LibCircleItemDataType.add(new KeyValue(DataColDef.DataType.DATA_DATE.toString(),"日期时间"));
        //--------------------------------------------------------------------------------------------
        retList.add(new EnvDataItem(DataLibrary.getLibKVByKey("LibQueryResKeyWord",AutoReportQueryComponent.ResKeyWord.START_TIME),
                DataLibrary.getLibKVByKey("LibCircleItemDataType",DataColDef.DataType.DATA_DATE.toString())));
        retList.add(new EnvDataItem(DataLibrary.getLibKVByKey("LibQueryResKeyWord",AutoReportQueryComponent.ResKeyWord.END_TIME),
                DataLibrary.getLibKVByKey("LibCircleItemDataType",DataColDef.DataType.DATA_DATE.toString())));
        retList.add(new EnvDataItem(DataLibrary.getLibKVByKey("LibQueryResKeyWord",AutoReportQueryComponent.ResKeyWord.PERIOD_NO),
                DataLibrary.getLibKVByKey("LibCircleItemDataType",DataColDef.DataType.DATA_INT.toString())));
        retList.add(new EnvDataItem(DataLibrary.getLibKVByKey("LibQueryResKeyWord",AutoReportQueryComponent.ResKeyWord.CUT_KEY),
                DataLibrary.getLibKVByKey("LibCircleItemDataType",DataColDef.DataType.DATA_STRING.toString())));
        retList.add(new EnvDataItem(DataLibrary.getLibKVByKey("LibQueryResKeyWord",AutoReportQueryComponent.ResKeyWord.CUT_NAME),
                DataLibrary.getLibKVByKey("LibCircleItemDataType",DataColDef.DataType.DATA_STRING.toString())));
        return retList;
    }
}
