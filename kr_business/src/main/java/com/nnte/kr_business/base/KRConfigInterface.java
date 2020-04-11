package com.nnte.kr_business.base;

import java.lang.reflect.Field;

public interface KRConfigInterface {

    static Field getClassField(Class cls,String key){
        Field retF=null;
        try {
            retF = cls.getDeclaredField(key);
        }catch (NoSuchFieldException e) {
        }
        if (retF==null){
            Class superClass=cls.getSuperclass();
            if (superClass!=null)
                return getClassField(superClass,key);
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
