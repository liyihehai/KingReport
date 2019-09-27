package com.nnte.kr_backend;

import com.nnte.kr_business.base.KRConfigInterface;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.kr.conf.kingreport")
@PropertySource(value = "classpath:nnte-kingreport-config.properties")

public class KingReportConfig implements KRConfigInterface {

    private String Debug;                   //Debug标志
    private String LocalHostName;           //本机访问Host名称
    private String LocalHostAbstractName;   //本机绝对HOST名称
    private String staticRoot;              //静态资源URL根目录
    private String abStaticRoot;            //静态资源URL根目录的绝对路径
    private String RedisServer;             //redis服务器IP
    private String RedisPws;                //redis服务器密码
    private String ReportRoot;              //报表根目录
    private String reportTemplateRoot;      //报表模板文件根目录
    private String reportFileRoot;          //报表EXCEL文件根目录
    private String ExpotExcelTmpRoot;       //数据导出Excel文件的tmp根目录

    @Override
    public String getLocalHostName() {
        return LocalHostName;
    }

    @Override
    public void setLocalHostName(String localHostName) {
        this.LocalHostName=localHostName;
    }

    @Override
    public String getLocalHostAbstractName() {
        return LocalHostAbstractName;
    }

    @Override
    public void setLocalHostAbstractName(String localHostAbstractName) {
        this.LocalHostAbstractName=localHostAbstractName;
    }

    @Override
    public String getRedisServer() {
        return RedisServer;
    }

    @Override
    public void setRedisServer(String redisServer) {
        this.RedisServer=redisServer;
    }

    @Override
    public String getRedisPws() {
        return RedisPws;
    }

    @Override
    public void setRedisPws(String redisPws) {
        this.RedisPws=redisPws;
    }

    @Override
    public String getDebug() {
        return Debug;
    }

    @Override
    public void setDebug(String debug) {
        this.Debug=debug;
    }

    @Override
    public String getReportRoot() {
        return ReportRoot;
    }

    @Override
    public void setReportRoot(String reportRoot) {
        this.ReportRoot=reportRoot;
    }

    @Override
    public String getStaticRoot() {
        return staticRoot;
    }

    @Override
    public void setStaticRoot(String staticRoot) {
        this.staticRoot=staticRoot;
    }

    @Override
    public String getExpotExcelTmpRoot() {
        return ExpotExcelTmpRoot;
    }

    @Override
    public void setExpotExcelTmpRoot(String expotExcelTmpRoot) {
        this.ExpotExcelTmpRoot=expotExcelTmpRoot;
    }

    @Override
    public String getAbStaticRoot() {
        return abStaticRoot;
    }

    @Override
    public void setAbStaticRoot(String abStaticRoot) {
        this.abStaticRoot=abStaticRoot;
    }

    @Override
    public String getReportTemplateRoot() {
        return reportTemplateRoot;
    }

    @Override
    public void setReportTemplateRoot(String reportTemplateRoot) {
        this.reportTemplateRoot=reportTemplateRoot;
    }

    @Override
    public String getReportFileRoot() {
        return reportFileRoot;
    }

    @Override
    public void setReportFileRoot(String reportFileRoot) {
        this.reportFileRoot=reportFileRoot;
    }
}
