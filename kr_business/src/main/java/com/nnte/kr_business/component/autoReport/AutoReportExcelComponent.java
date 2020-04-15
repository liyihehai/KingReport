package com.nnte.kr_business.component.autoReport;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.XSSFWorkbookAndOPC;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.entity.autoReport.ReportControl;
import com.nnte.kr_business.entity.autoReport.ReportControlCircle;
import com.nnte.kr_business.entity.autoReport.ReportControlCircleItem;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AutoReportExcelComponent extends BaseComponent {
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;

    public XSSFWorkbookAndOPC openExcelTemplate(String templateFile) {
        try {
            XSSFWorkbookAndOPC WbAndOpc= new XSSFWorkbookAndOPC();
            FileInputStream fis=new FileInputStream(templateFile);
            String ftype=StringUtils.defaultString(FileUtil.getTypePart(templateFile)).toLowerCase();
            if (ftype.length()<4)
                WbAndOpc.setWb(new HSSFWorkbook(fis));
            else
                WbAndOpc.setWb(new XSSFWorkbook(fis));
            WbAndOpc.setFileType(ftype);
            fis.close();
            return WbAndOpc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //将打开的文件进行保存,上传到文件服务器，返回文件ID
    public String saveExcelFile(XSSFWorkbookAndOPC wao,String type,String excelFile){
        try {
            FileOutputStream fos = new FileOutputStream(new File(excelFile));
            if (fos!=null){
                wao.getWb().write(fos);
                fos.flush();
                fos.close();
                //将文件上传到文件服务器保存，返回文件ID
                //return fdfsClientMgrComponent.uploadFile(config.getConfig("reportConvPdf"),excelFile);
                return fdfsClientMgrComponent.uploadFile(type,excelFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //关闭Excel文件
    public void closeExcelTemplate(XSSFWorkbookAndOPC WbAndOpc){
        if (WbAndOpc!=null){
            if (WbAndOpc.getWb()!=null){
                try {
                    WbAndOpc.getWb().close();
                    WbAndOpc.setWb(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyRows(int startRow, int endRow, int pPosition,
                         HSSFSheet sheet) {
        int pStartRow = startRow ;
        int pEndRow = endRow;
        int targetRowFrom;
        int targetRowTo;
        int columnCount;
        CellRangeAddress region = null;
        int i;
        int j;
        if (pStartRow <= -1 || pEndRow <= -1) {
            return;
        }
        // 拷贝合并的单元格
        for (i = 0; i < sheet.getNumMergedRegions(); i++) {
            region = sheet.getMergedRegion(i);
            if ((region.getFirstRow() >= pStartRow)
                    && (region.getLastRow() <= pEndRow)) {
                targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
                targetRowTo = region.getLastRow() - pStartRow + pPosition;
                CellRangeAddress newRegion = region.copy();
                newRegion.setFirstRow(targetRowFrom);
                newRegion.setFirstColumn(region.getFirstColumn());
                newRegion.setLastRow(targetRowTo);
                newRegion.setLastColumn(region.getLastColumn());
                sheet.addMergedRegion(newRegion);
            }
        }
        // 设置列宽
        for (i = pStartRow; i <= pEndRow; i++) {
            HSSFRow sourceRow = sheet.getRow(i);
            columnCount = sourceRow.getLastCellNum();
            if (sourceRow != null) {
                HSSFRow newRow = sheet.createRow(pPosition - pStartRow + i);
                newRow.setHeight(sourceRow.getHeight());
                for (j = 0; j < columnCount; j++) {
                    HSSFCell templateCell = sourceRow.getCell(j);
                    if (templateCell != null) {
                        HSSFCell newCell = newRow.createCell(j);
                        copyCell(templateCell, newCell);
                    }
                }
            }
        }
    }

    private static void copyCell(HSSFCell srcCell, HSSFCell distCell) {
        distCell.setCellStyle(srcCell.getCellStyle());
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        CellType srcCellType = srcCell.getCellTypeEnum();
        distCell.setCellType(srcCellType);
        if (srcCellType == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
                distCell.setCellValue(srcCell.getDateCellValue());
            } else {
                distCell.setCellValue(srcCell.getNumericCellValue());
            }
        } else if (srcCellType == CellType.STRING) {
            distCell.setCellValue(srcCell.getRichStringCellValue());
        } else if (srcCellType == CellType.BLANK) {
            // nothing21
        } else if (srcCellType == CellType.BOOLEAN) {
            distCell.setCellValue(srcCell.getBooleanCellValue());
        } else if (srcCellType == CellType.ERROR) {
            Integer errCellVal=(int)srcCell.getErrorCellValue();
            distCell.setCellErrorValue(FormulaError.valueOf(errCellVal.toString()));
        } else if (srcCellType == CellType.FORMULA) {
            distCell.setCellFormula(srcCell.getCellFormula());
        } else { // nothing29

        }
    }

    public static int[] getCellRowCol(String cellPoint){
        String c=cellPoint;
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(c);
        String rows = m.replaceAll("");
        String cols = c.replaceAll(rows,"");
        cols=cols.toUpperCase();
        int row= NumberUtil.getDefaultInteger(rows);
        if (row>0)
            row--;
        if (cols==null||cols.length()<=0||cols.length()>2)
            return null;
        int col=0;
        if (cols.length()==1)
            col=(byte)cols.getBytes()[0]-(byte)'A';
        else{
            int c1=(byte)cols.getBytes()[0]-(byte)'A'+ 1;
            int c2=(byte)cols.getBytes()[1]-(byte)'A';
            col = c1 * ((byte)'Z' - (byte)'A' + 1) + c2;
        }
        int[] ret = new int[2];
        ret[0]=row;
        ret[1]=col;
        return ret;
    }
    private void outputDataToCell(Sheet sheet,String cell,String format,Object outObj,int rowoff){
        String sVal=AutoReportQueryComponent.getReplaceContentByFormat(format,outObj);
        int[] cellRowCol=getCellRowCol(cell);
        if (cellRowCol==null || cellRowCol.length!=2)
        {
            BaseNnte.outConsoleLog("没有取得合适的输出位置");
            return;//没有取得合适的输出位置
        }
        Row row=sheet.getRow(cellRowCol[0]+rowoff);
        if (row==null)
        {
            BaseNnte.outConsoleLog("row==null,cell="+cell+",rowno="+ cellRowCol[0]+rowoff);
            return;
        }
        Cell sheet_cell=row.getCell(cellRowCol[1]);
        if (sheet_cell==null)
        {
            BaseNnte.outConsoleLog("cell==null,cellno="+cellRowCol[1]);
            return;
        }
        sheet_cell.setCellValue(sVal);
    }

    //向报表文件输出数据
    public void outputDataToReportFile(XSSFWorkbookAndOPC wao, ReportControl rc){
        //对输出控制进行重新排序，环境输出和单行输出先执行-----
        List<ReportControlCircle> resortCCList=new ArrayList<>();
        for(ReportControlCircle controlCircle:rc.getCircleList()){
            if (controlCircle.getCircleItemType().equals(ReportControlCircle.CircleItemType.CIT_EnvData))
                resortCCList.add(0,controlCircle);
            else{
                List<Map<String,Object>> rows=(List<Map<String,Object>>)rc.getReportDataEnv().get(controlCircle.getQueryCode());
                if (rows.size()>1)
                    resortCCList.add(controlCircle);
                else
                    resortCCList.add(0,controlCircle);
            }
        }
        //--------------------------------------------------
        for(ReportControlCircle controlCircle:resortCCList){
            Sheet sheet=wao.getWb().getSheet(controlCircle.getSheetName());//先确定页面
            if (sheet==null)
                continue; //取不到页面，不能输出数据
            if (controlCircle.getCircleItemType().equals(ReportControlCircle.CircleItemType.CIT_EnvData)){
                //如果是环境数据输出
                for(ReportControlCircleItem circleItem:controlCircle.getCircleItemList()){
                    Object outObj=rc.getReportDataEnv().get(circleItem.getOutText());
                    outputDataToCell(sheet,circleItem.getCellPoint(),circleItem.getFormat(),outObj,0);
                }
            }else if (controlCircle.getCircleItemType().equals(ReportControlCircle.CircleItemType.CIT_NormalTxt)){
                //如果是普通文本输出
                for(ReportControlCircleItem circleItem:controlCircle.getCircleItemList()){
                    outputDataToCell(sheet,circleItem.getCellPoint(),circleItem.getFormat(),circleItem.getOutText(),0);
                }
            }
            else if (controlCircle.getCircleItemType().equals(ReportControlCircle.CircleItemType.CIT_QueryFeild)){
                if (controlCircle.getCircleItemList().size()<=0)
                    continue;//没有行定义，不能输出数据
                //取得开始的行号
                int[] rcPoint=getCellRowCol(controlCircle.getCircleItemList().get(0).getCellPoint());
                if (rcPoint==null)
                    continue;//没找到行，不能输出数据
                int startRow=rcPoint[0];
                //如果是查询结果集输出，每一行要控制循环执行一次
                List<Map<String,Object>> dataRows=(List<Map<String,Object>>)rc.getReportDataEnv().get(controlCircle.getQueryCode());
                int rowCount=dataRows.size();
                CellCopyPolicy policy=null;
                if (rowCount>1){
                    //需要增加rowCount-1行
                    policy=new CellCopyPolicy();
                    policy.setCopyCellValue(false);//不拷贝数据
                    Row targetRow=sheet.getRow(startRow);
                    short srcHeight=targetRow.getHeight();
                    for(int i=1;i<rowCount;i++)
                    {
                        sheet.shiftRows(startRow+1,sheet.getLastRowNum(),1,true,false);
                        Row insertRow=sheet.createRow(startRow+1);
                        insertRow.setHeight(srcHeight);//设置行高
                        for(int cindex=0;cindex<=targetRow.getLastCellNum();cindex++){
                            insertRow.createCell(cindex);
                        }
                        String cl=sheet.getClass().getSimpleName();
                        if (cl.equals("XSSFSheet")) {
                            XSSFSheet xssfSheet = (XSSFSheet) sheet;
                            xssfSheet.copyRows(startRow, startRow, startRow + 1, policy);
                        }else{
                            HSSFSheet hssfSheet = (HSSFSheet) sheet;
                            copyRows(startRow,startRow,startRow + 1,hssfSheet);
                        }
                    }
                }else if (rowCount<=0)
                    continue;//没有数据，本控制不输出
                int rowOff=0;
                for(Map jData:dataRows){
                    for(ReportControlCircleItem circleItem:controlCircle.getCircleItemList()){
                        Object outObj=jData.get(circleItem.getOutText()); //从查询结果中取数据
                        outputDataToCell(sheet,circleItem.getCellPoint(),circleItem.getFormat(),outObj,rowOff);
                    }
                    rowOff++;
                }
            }
        }
    }

    //以下内容为通过OpenOffice将EXCEL文件生成PDF内容

}
