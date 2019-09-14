package com.nnte.kr_backend.controller;

import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.base.KRConfigInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/front")
public class NnteFrontController {

    private static Log logger = LogFactory.getLog(NnteFrontController.class);

    @ConfigLoad
    private KRConfigInterface app_Config;
    @Autowired
    private DynamicDatabaseSourceHolder dynamicDSHolder;

    @RequestMapping(value = "index")
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView.setViewName("front/index");
        return modelAndView;
    }
}
