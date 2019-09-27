package com.nnte.kr_business.entity.autoReport;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFWorkbookAndOPC {
    private OPCPackage pkg;
    private XSSFWorkbook wb;

    public OPCPackage getPkg() { return pkg; }
    public void setPkg(OPCPackage pkg) { this.pkg = pkg; }
    public XSSFWorkbook getWb() { return wb; }
    public void setWb(XSSFWorkbook wb) { this.wb = wb; }
}
