package com.nnte.kr_business.component.base;

import com.nnte.framework.base.SpringContextHolder;
import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.base.KRConfigInterface;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Map;

public class KingReportComponent {
    public static BaseMerchant getLoginMerchantFromRequest(HttpServletRequest request) {
        Map<String, Object> envData = (Map<String, Object>) request.getSession().getAttribute("envData");
        BaseMerchant loginMerchant = (BaseMerchant) envData.get("loginMerchant");
        return loginMerchant;
    }

    public static BaseMerchantOperator getBaseMerchantOperatorFromRequest(HttpServletRequest request) {
        Map<String, Object> envData = (Map<String, Object>) request.getSession().getAttribute("envData");
        BaseMerchantOperator loginMerchantOperator = (BaseMerchantOperator) envData.get("loginMerchantOperator");
        return loginMerchantOperator;
    }

    public static BaseMerchant getLoginMerchantFromParamMap(Map<String, Object> paramMap) {
        Map<String, Object> envData = (Map<String, Object>) paramMap.get("envData");
        BaseMerchant loginMerchant = (BaseMerchant) envData.get("loginMerchant");
        return loginMerchant;
    }

    public static BaseMerchantOperator getBaseMerchantOperatorFromParamMap(Map<String, Object> paramMap) {
        Map<String, Object> envData = (Map<String, Object>) paramMap.get("envData");
        BaseMerchantOperator loginMerchantOperator = (BaseMerchantOperator) envData.get("loginMerchantOperator");
        return loginMerchantOperator;
    }

    private static void setKRConfig(Object obj,Field[] fields,KRConfigInterface KRConfig){
        for(Field field : fields){
            ConfigLoad a=field.getAnnotation(ConfigLoad.class);
            if (a!=null){
                try {
                    field.setAccessible(true);
                    field.set(obj,KRConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //扫描所有组件，为组件的配置接口设置实现对象
    public static void LoadConfigComponent(KRConfigInterface KRConfig){
        Map<String,Object> beans = SpringContextHolder.getApplicationContext().getBeansWithAnnotation(Component.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instanceBody=entry.getValue();
            Field[] fields=instanceBody.getClass().getFields();
            setKRConfig(instanceBody,fields,KRConfig);
            fields=instanceBody.getClass().getDeclaredFields();
            setKRConfig(instanceBody,fields,KRConfig);
        }
    }
}
