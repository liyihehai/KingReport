package com.nnte.kr_business.base;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.Excel.ExcelConfig;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public static class XSSFWorkbookAndOPC{
        private OPCPackage pkg;
        private XSSFWorkbook wb;
        private ExcelConfig ec;

        public OPCPackage getPkg() { return pkg; }
        public void setPkg(OPCPackage pkg) { this.pkg = pkg; }
        public XSSFWorkbook getWb() { return wb; }
        public void setWb(XSSFWorkbook wb) { this.wb = wb; }
        public ExcelConfig getEc() { return ec; }
        public void setEc(ExcelConfig ec) { this.ec = ec; }
    }
    //打开一个模板文件,文件类型为“.xltx”
    public static XSSFWorkbookAndOPC openExcelTemplate(ExcelConfig ec,String templateFile) {
        try {
            XSSFWorkbookAndOPC WbAndOpc= new XSSFWorkbookAndOPC();
            String templatePathFile=ec.getTemplatePathFile(templateFile);
            WbAndOpc.setEc(ec);
            WbAndOpc.setPkg(OPCPackage.open(templatePathFile));//取得文件的读写权限
            WbAndOpc.setWb(new XSSFWorkbook(WbAndOpc.getPkg()));
            return WbAndOpc;
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
    //将打开的文件进行保存
    public static boolean saveExcelFile(XSSFWorkbookAndOPC wao,String excelFile){
        try {
            String excelPathFile=wao.getEc().getReportExcelPathFile(excelFile);
            FileOutputStream fos = new FileOutputStream(new File(excelPathFile));
            if (fos!=null){
                wao.getWb().write(fos);
                fos.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //关闭Excel文件
    public static void closeExcelTemplate(XSSFWorkbookAndOPC WbAndOpc){
        if (WbAndOpc!=null){
            if (WbAndOpc.getPkg()!=null && WbAndOpc.getWb()!=null){
                try {
                    WbAndOpc.getPkg().close();
                    WbAndOpc.setWb(null);
                    WbAndOpc.setPkg(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //创建一个用于数据导出的sheet
    public static HSSFSheet createExportExcelSheet(HSSFWorkbook workbook,String sheetName, List<ExpotColDef> colList){
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 设置表格默认列宽度为15个字节
        //sheet.setDefaultColumnWidth(15);
        // 生成一个样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        // 设置这些样式
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(headerStyle.getFillPatternEnum().SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        HSSFFont headerFont = workbook.createFont();
        headerFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < colList.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            HSSFRichTextString text = new HSSFRichTextString(colList.get(i).getColTitle());
            cell.setCellValue(text);
        }
        return sheet;
    }
    /*创建一个用于数据导出的Excel文档
    * @colDefList 列定义队列
    * @refClass   数据对象类信息
    */
    public static HSSFWorkbook createExportExcelBook(List<ExpotColDef> colDefList,Class<?> refClass){
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        for(ExpotColDef colDef:colDefList){
            colDef.setColDataDesc(refClass);
        }
        return workbook;
    }
    //生成一个文本填充样式
    public static HSSFCellStyle makeExportExcelTextStyle(HSSFWorkbook workbook,HorizontalAlignment align){
        HSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);
        textStyle.setBorderTop(BorderStyle.THIN);
        textStyle.setAlignment(align);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont textFont = workbook.createFont();
        textFont.setBold(false);
        textStyle.setFont(textFont);
        return textStyle;
    }
    //向Excel文档中写入导出的数据
    public static Integer writeExportExcelSheet(List<?> dataList,List<ExpotColDef> colList,HSSFSheet sheet,
                                                Integer startNo,Integer endNo){
        HSSFWorkbook workbook=sheet.getWorkbook();
        HSSFCellStyle textLeftStyle=makeExportExcelTextStyle(workbook,HorizontalAlignment.LEFT);
        HSSFCellStyle textRightStyle=makeExportExcelTextStyle(workbook,HorizontalAlignment.RIGHT);
        Integer exporRow=0;
        try {
            for (int r = startNo - 1; r < endNo; r++) {
                Object rowObj = dataList.get(r);
                HSSFRow row = sheet.createRow(r-startNo+2);
                //将一行数据导出至文件
                for (int c=0;c<colList.size();c++) {
                    ExpotColDef colDef = colList.get(c);
                    Field f=rowObj.getClass().getDeclaredField(colDef.getColName());
                    if (f==null)
                        return 0;
                    HSSFCell cell = row.createCell(c);
                    Method method=colDef.getGetMethod();
                    Object value = method.invoke(rowObj);
                    if (value!=null){
                        if (colDef.getValTranMap()!=null){
                            //如果需要值转换，则采用转换后的文本替换当前输出值
                            cell.setCellStyle(textLeftStyle);   //文本靠左
                            cell.setCellValue(StringUtils.defaultString(colDef.getValTranMap().get(value.toString())));
                            continue;
                        }
                        //如果不需要转换，则直接输出变量值
                        if (colDef.getDataType().equals(DataColDef.DataType.DATA_STRING)){
                            cell.setCellStyle(textLeftStyle);   //文本靠左
                            cell.setCellValue(value.toString());
                        }
                        else if (colDef.getDataType().equals(DataColDef.DataType.DATA_INT)){
                            cell.setCellStyle(textRightStyle);   //数值靠右
                            cell.setCellValue(value.toString());
                        }
                        else if (colDef.getDataType().equals(DataColDef.DataType.DATA_FLOT)){
                            cell.setCellStyle(textRightStyle);   //数值靠右
                            if (StringUtils.isNotEmpty(colDef.getFlotFormat())){
                                //如果需要格式化
                                cell.setCellValue(NumberUtil.formatNumber(Double.valueOf(value.toString()),colDef.getFlotFormat()));
                            }else
                                cell.setCellValue(value.toString());
                        }
                        else if (colDef.getDataType().equals(DataColDef.DataType.DATA_DATE)){
                            cell.setCellStyle(textRightStyle);   //日期时间靠右
                            if (StringUtils.isNotEmpty(colDef.getDatetimeFormat())){
                                cell.setCellValue(DateUtils.dateToString((Date)value,colDef.getDatetimeFormat()));
                            }else
                                //采用默认的格式转换
                                cell.setCellValue(DateUtils.dateToString((Date)value,"yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
                exporRow++;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return exporRow;
    }
    //将生成的Excel文档保存成文件
    public static boolean saveExportExcelBook(HSSFWorkbook workbook, String excelFile){
        boolean issuc=false;
        try {
            File newFile = new File(excelFile);
            workbook.write(newFile);
            issuc=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return issuc;
    }

    public static void closeExcelBook(HSSFWorkbook workbook){
        try {
            if (workbook!=null)
                workbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
    * 将数据列表导出到指定的Excel文件
    * @dataList 需要导出的数据列表
    * @colDefList 数据列表导出的定义及控制信息列表
    * @sheetcount 数据导出分在不同的Excel Sheet页，本参数指定每页导出多少行数据
    * @excelFileName 导出的Excel文件的路径文件名
    * */
    public static Map<String,Object> ExportDataToExcel(List<?> dataList, List<ExpotColDef> colDefList,
                                        Integer sheetcount, String excelFileName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (dataList.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"无数据导出");
            return ret;
        }
        Object data0=dataList.get(0);
        Class<?> refClass=data0.getClass();
        int expNo=0;
        int sheetCount=sheetcount;
        HSSFWorkbook wb=ExcelUtil.createExportExcelBook(colDefList, refClass);
        do{
            int startNo=expNo+1;
            if (startNo<=dataList.size()) {
                int endNo = startNo + sheetCount - 1;
                if (endNo >= dataList.size())
                    endNo = dataList.size();
                String sheetName=startNo+"-"+endNo;
                HSSFSheet sheet=ExcelUtil.createExportExcelSheet(wb,sheetName,colDefList);
                Integer rowCount=ExcelUtil.writeExportExcelSheet(dataList,colDefList,sheet,startNo,endNo);
                if (rowCount<=0){
                    BaseNnte.setRetFalse(ret, 1002,"生成导出文件页面时错误");
                    closeExcelBook(wb);
                    return ret;
                }
                expNo+=rowCount;
            }
        }while(expNo<dataList.size());
        if (!ExcelUtil.saveExportExcelBook(wb,excelFileName)){
            BaseNnte.setRetFalse(ret, 1002,"生成导出文件错误");
            closeExcelBook(wb);
            return ret;
        }
        closeExcelBook(wb);
        BaseNnte.setRetTrue(ret,"生成导出文件成功");
        return ret;
    }
}
