package com.nnte.kr_business.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented

@Inherited

/*
 * 本注解用于用于标识配置接口，引用该接口的组件需要手动设置配置对象
 * */

public @interface ConfigLoad {
}
