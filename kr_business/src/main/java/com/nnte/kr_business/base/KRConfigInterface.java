package com.nnte.kr_business.base;

import java.lang.reflect.Field;

public interface KRConfigInterface {
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

    static Field getClassField(Class cls,String key){
        Field retF=null;
        try {
            retF = cls.getField(key);
        }catch (NoSuchFieldException e) {
        }
        if (retF==null){
            try {
                retF = cls.getDeclaredField(key);
            }catch (NoSuchFieldException e) {
            }
        }
        return retF;
    }

    default String getConfig(String key){
        Class cls = this.getClass();
        try {
            Field field = getClassField(cls,key);
            if (field==null)
                return "";
            field.setAccessible(true);
            //获取属性值
            Object value = field.get(this);
            //一个个赋值
            if (value != null)
                return value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
