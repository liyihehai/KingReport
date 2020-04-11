package com.nnte.kr_business.base;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.ExcelUtil;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.component.base.SysParamComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseComponent {
    @Autowired
    @ConfigLoad
    public KRConfigInterface appConfig;
    @Autowired
    public SysParamComponent sysParamComponent;
    //生成数据导出Excel文件的静态资源访问相对路径
    public String genExportExcelRelPath(Date date,Long parMerchantId){
        if (parMerchantId==null)
            return null;
        String p1= StringUtils.pathAppend(appConfig.getConfig("expotExcelTmpRoot"),parMerchantId.toString());
        String yearMonth= DateUtils.dateToString(date,"yyyyMM");
        String path=StringUtils.pathAppend(p1,yearMonth);
        return path;
    }
    //生成数据导出Excel文件的绝对路径
    public String genExportExcelAbPath(Date date,Long parMerchantId){
        if (parMerchantId==null)
            return null;
        String path=StringUtils.pathAppend(appConfig.getConfig("abStaticRoot"),genExportExcelRelPath(date,parMerchantId));
        if (!FileUtil.isPathExists(path))
        {
            if (!FileUtil.makeDirectory(path))
                return null;
        }
        return path;
    }
    /*
     *生成数据导出Excel文件的文件名,返回值为String[5]
     *  ret[0]=abPathFileName;  //绝对路径文件名
        ret[1]=relFileUrl;      //静态文件名访问url
        ret[2]=fileName;        //文件名
        ret[3]=abPath;          //绝对路径
        ret[4]=relPath;         //相对路径
     * */
    public String[] genExportExcelFileName(Date date,Long parMerchantId){
        String relPath=genExportExcelRelPath(date,parMerchantId);
        String abPath=genExportExcelAbPath(date,parMerchantId);
        if (abPath==null)
            return null;
        String id= UUID.randomUUID().toString();
        String fileName = "exportexcel"+id+".xls";
        String abPathFileName = StringUtils.pathAppend(abPath,fileName);
        String relFileUrl = StringUtils.pathAppend(appConfig.getConfig("staticRoot"),StringUtils.pathAppend(relPath,fileName));
        String[] ret = new String[5];
        ret[0]=abPathFileName;  //绝对路径文件名
        ret[1]=relFileUrl;      //静态文件名访问url
        ret[2]=fileName;        //文件名
        ret[3]=abPath;          //绝对路径
        ret[4]=relPath;         //相对路径
        if ("true".equalsIgnoreCase(appConfig.getConfig("debug")))
            BaseNnte.outConsoleLog(relFileUrl);
        return ret;
    }

    //查询数据导出到Excel文件中，通过excelFileUrl返回前端EXCEL文件访问的url路径
    public Map<String,Object> DataExport(Long parMerchantId, List<?> dataList, List<ExpotColDef> colDefList,Integer sheetCount){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        Date cueDate = new Date();
        String[] excelFileNames = genExportExcelFileName(cueDate,parMerchantId);
        if (StringUtils.isEmpty(excelFileNames)){
            BaseNnte.setRetFalse(ret, 1002,"创建导出文件错误");
            return ret;
        }
        ret= ExcelUtil.ExportDataToExcel(dataList,colDefList,sheetCount,excelFileNames[0]);
        if (BaseNnte.getRetSuc(ret))
            ret.put("excelFileUrl",excelFileNames[1]);
        return ret;
    }
}
