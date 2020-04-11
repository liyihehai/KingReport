package com.nnte.kr_business.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NConfig implements KRConfigInterface{

    private String debug;                   //Debug标志
    private String localHostName;           //本机访问Host名称
    private String localHostAbstractName;   //本机绝对HOST名称
    private String staticRoot;              //静态资源URL根目录
    private String abStaticRoot;            //静态资源URL根目录的绝对路径
    private String redisServer;             //redis服务器IP
    private String redisPws;                //redis服务器密码
    private String reportRoot;              //报表根目录
    private String reportTemplateRoot;      //报表模板文件根目录
    private String reportFileRoot;          //报表EXCEL文件根目录
    private String expotExcelTmpRoot;       //数据导出Excel文件的tmp根目录
}
