package com.nnte.kr_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class NnteFrontConfigurer implements WebMvcConfigurer {
    @Autowired
    private NnteFrontInterceptor nnteFrontInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截的管理器
        InterceptorRegistration registration = registry.addInterceptor(nnteFrontInterceptor);
        registration.addPathPatterns("/**");//所有路径都被拦截
        registration.excludePathPatterns("/","/autoCode","/login","/error","/static/**","/logout");//添加不拦截路径
    }
}
