package com.nnte.kr_backend;

import com.nnte.framework.base.SpringContextHolder;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.base.JedisCom;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.nnte")
public class KingReportApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(KingReportApplication.class, args);
        KingReportConfig Config=SpringContextHolder.getBean("kingReportConfig");
        KingReportComponent.LoadConfigComponent(Config);
        DynamicDatabaseSourceHolder ddh= SpringContextHolder.getBean("dynamicDatabaseSourceHolder");
        DynamicDatabaseSourceHolder.loadDBSchemaInterface();
        JedisCom jedisCom = SpringContextHolder.getBean("jedisCom");
        jedisCom.initJedisCom();

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
        ddh.initDataBaseSource(DBSrcTranc.Work_DBSrc_Name,config,mappers,true);
    }
}
