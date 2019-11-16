package com.nnte.kr_business.base;

public interface KRConfigInterface {
    public String getConfig(String key);
    public String getLocalHostName();
    public void setLocalHostName(String localHostName);
    public String getLocalHostAbstractName();
    public void setLocalHostAbstractName(String localHostAbstractName);
    public String getRedisServer();
    public void setRedisServer(String redisServer);
    public String getRedisPws();
    public void setRedisPws(String redisPws);
    public String getDebug();
    public void setDebug(String debug);
    public String getReportRoot();
    public void setReportRoot(String reportRoot);
    public String getStaticRoot();
    public void setStaticRoot(String staticRoot);
    public String getExpotExcelTmpRoot();
    public void setExpotExcelTmpRoot(String expotExcelTmpRoot);
    public String getAbStaticRoot();
    public void setAbStaticRoot(String abStaticRoot);
    //------------------------------------------
    public String getReportTemplateRoot();         //报表模板文件根目录
    public void setReportTemplateRoot(String reportTemplateRoot);
    public String getReportFileRoot();             //报表EXCEL文件根目录
    public void setReportFileRoot(String reportFileRoot);
}
