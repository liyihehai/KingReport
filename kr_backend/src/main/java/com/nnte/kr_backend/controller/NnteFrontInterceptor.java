package com.nnte.kr_backend.controller;

import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.base.KRConfigInterface;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class NnteFrontInterceptor implements HandlerInterceptor {
    @ConfigLoad
    private KRConfigInterface appconfig;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求进入这个拦截器
        HttpSession session = request.getSession();
        if (session.getAttribute("envData")==null) {
            Map<String, Object> envData = new HashMap<>();
            envData.put("debug", appconfig.getConfig("debug").toUpperCase());
            envData.put("staticRoot", appconfig.getConfig("staticRoot"));
            envData.put("localHostName", appconfig.getConfig("localHostName"));

            //测试用，设置默认的商户信息
            BaseMerchant loginMerchant=new BaseMerchant();
            loginMerchant.setId(1L);
            loginMerchant.setParMerchantId(1L);
            loginMerchant.setMerchantName("测试商户");
            envData.put("loginMerchant",loginMerchant);
            //测试用，设置默认的商户操作员
            BaseMerchantOperator loginMerchantOperator=new BaseMerchantOperator();
            loginMerchantOperator.setId(1L);
            loginMerchantOperator.setOpeName("测试操作员");
            envData.put("loginMerchantOperator",loginMerchantOperator);
            session.setAttribute("envData", envData);
        }
        /*
        if(session.getAttribute("user") == null){//判断session中有没有user信息
            // System.out.println("进入拦截器");
            if("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))){
                response.sendError(401);
            }
            response.sendRedirect("/");     //没有user信息的话进行路由重定向
            return false;
        }*/
        return true;        //有的话就继续操作
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
