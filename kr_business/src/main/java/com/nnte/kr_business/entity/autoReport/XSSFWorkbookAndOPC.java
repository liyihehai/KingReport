package com.nnte.kr_business.entity.autoReport;
import org.apache.poi.ss.usermodel.Workbook;

public class XSSFWorkbookAndOPC {
    private Workbook wb;
    private String fileType;
    public Workbook getWb() { return wb; }
    public void setWb(Workbook wb) { this.wb = wb; }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
