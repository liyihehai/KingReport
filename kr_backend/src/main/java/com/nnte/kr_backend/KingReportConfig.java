package com.nnte.kr_backend;

import com.nnte.kr_business.base.NConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.kr.conf.kingreport")
@PropertySource(value = "classpath:nnte-kingreport-config.properties")

public class KingReportConfig extends NConfig {
    //工作数据库连接配置
    private String workDBDriverClassName;
    private String workDBIp;
    private String workDBPort;
    private String workDBSchema;
    private String workDBUser;
    private String workDBPassword;
    //PDF文件转换服务器配置
    private String converPDFUrl;

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

    public String getConverPDFUrl() {
        return converPDFUrl;
    }

    public void setConverPDFUrl(String converPDFUrl) {
        this.converPDFUrl = converPDFUrl;
    }

    @Override
    public String getConfig(String key) {
        if (key.equals("converPDFUrl"))
            return converPDFUrl;
        return null;
    }
}
