package com.nnte.kr_backend;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.DynamicDatabaseSourceHolder;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.JedisCom;
import com.nnte.kr_business.base.NConfig;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "nnte.kr.conf.kingreport")
@PropertySource(value = "classpath:nnte-kingreport-config.properties")

public class KingReportConfig extends NConfig implements ApplicationRunner {
    //工作数据库连接配置
    private String workDBDriverClassName;
    private String workDBIp;
    private String workDBPort;
    private String workDBSchema;
    private String workDBUser;
    private String workDBPassword;

    //文件服务器配置
    private String fileSysUrl;
    private String templateType;
    private String convPdfUrl;
    private String convPdfType;

    public String getWorkDBDriverClassName() {
        return workDBDriverClassName;
    }

    public void setWorkDBDriverClassName(String workDBDriverClassName) {
        this.workDBDriverClassName = workDBDriverClassName;
    }

    public String getWorkDBIp() {
        return workDBIp;
    }

    public void setWorkDBIp(String workDBIp) {
        this.workDBIp = workDBIp;
    }

    public String getWorkDBPort() {
        return workDBPort;
    }

    public void setWorkDBPort(String workDBPort) {
        this.workDBPort = workDBPort;
    }

    public String getWorkDBSchema() {
        return workDBSchema;
    }

    public void setWorkDBSchema(String workDBSchema) {
        this.workDBSchema = workDBSchema;
    }

    public String getWorkDBUser() {
        return workDBUser;
    }

    public void setWorkDBUser(String workDBUser) {
        this.workDBUser = workDBUser;
    }

    public String getWorkDBPassword() {
        return workDBPassword;
    }

    public void setWorkDBPassword(String workDBPassword) {
        this.workDBPassword = workDBPassword;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        appInit();
    }

    private static void appInit(){
        BaseNnte.outConsoleLog("执行后续初始化功能......");
        KingReportConfig Config= SpringContextHolder.getBean("kingReportConfig");
        BaseNnte.outConsoleLog("初始化KingReportConfig组件......");
        KingReportComponent.LoadConfigComponent(Config);
        DynamicDatabaseSourceHolder ddh= SpringContextHolder.getBean("dynamicDatabaseSourceHolder");
        BaseNnte.outConsoleLog("初始化DynamicDatabaseSourceHolder组件......"+(ddh==null?"null":"suc"));
        DynamicDatabaseSourceHolder.loadDBSchemaInterface();
        JedisCom jedisCom = SpringContextHolder.getBean("jedisCom");
        BaseNnte.outConsoleLog("初始化JedisCom组件......");
        jedisCom.initJedisCom();
        //--初始化文件服务器连接--
        FdfsClientMgrComponent fdfsClientMgrComponent = SpringContextHolder.getBean("fdfsClientMgrComponent");
        fdfsClientMgrComponent.runFdfsClientMgr(null);
        //------------------------
        BaseNnte.outConsoleLog("初始化工作数据库连接......");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(Config.getWorkDBDriverClassName());
        config.setJdbcUrl("jdbc:mysql://"+Config.getWorkDBIp()+":"+Config.getWorkDBPort()+"/"+
                Config.getWorkDBSchema()+"?autoReconnect=true&autoReconnectForPools=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        config.setUsername(Config.getWorkDBUser());
        config.setPassword(Config.getWorkDBPassword());
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(5);
        config.setIdleTimeout(1000*20);
        config.setConnectionTestQuery("SELECT 1");

        List<String> mappers=new ArrayList<>();
        mappers.add("com.nnte.kr_business.mapper.workdb");
        BaseNnte.outConsoleLog("初始化工作数据库连接数据源......");
        ddh.initDataBaseSource(DBSrcTranc.Work_DBSrc_Name,config,mappers,KingReportComponent.class,true);
        BaseNnte.outConsoleLog("KingReport Backend main......end");
    }

    public String getFileSysUrl() {
        return fileSysUrl;
    }

    public void setFileSysUrl(String fileSysUrl) {
        this.fileSysUrl = fileSysUrl;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getConvPdfUrl() {
        return convPdfUrl;
    }

    public void setConvPdfUrl(String convPdfUrl) {
        this.convPdfUrl = convPdfUrl;
    }

    public String getConvPdfType() {
        return convPdfType;
    }

    public void setConvPdfType(String convPdfType) {
        this.convPdfType = convPdfType;
    }
}
