package com.nnte.kr_business.component;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.nnte.kr_business.component")
@EnableAspectJAutoProxy//开启AspectJ注解
public class WorkDBAopConfig {
}
