package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.entity.autoReport.XSSFWorkbookAndOPC;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AutoReportExcelComponent extends BaseComponent {
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
    //将打开的文件进行保存
    public boolean saveExcelFile(XSSFWorkbookAndOPC wao,String fpath,String excelFile){
        try {
            if (!FileUtil.isPathExists(fpath))
                FileUtil.makeDirectory(fpath);
            String pathFile=StringUtils.pathAppend(fpath,excelFile);
            FileOutputStream fos = new FileOutputStream(new File(pathFile));
            if (fos!=null){
                wao.getWb().write(fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
}
