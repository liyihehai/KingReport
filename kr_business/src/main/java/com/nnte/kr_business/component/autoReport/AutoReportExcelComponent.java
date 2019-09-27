package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.entity.autoReport.XSSFWorkbookAndOPC;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AutoReportExcelComponent extends BaseComponent {
    //打开一个模板文件,文件类型为“.xltx”
    public XSSFWorkbookAndOPC openExcelTemplate(String templateFile) {
        try {
            XSSFWorkbookAndOPC WbAndOpc= new XSSFWorkbookAndOPC();
            WbAndOpc.setPkg(OPCPackage.open(templateFile));//取得文件的读写权限
            WbAndOpc.setWb(new XSSFWorkbook(WbAndOpc.getPkg()));
            return WbAndOpc;
        } catch (IOException | InvalidFormatException e) {
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
}
