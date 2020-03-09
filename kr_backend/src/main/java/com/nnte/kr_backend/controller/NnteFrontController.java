package com.nnte.kr_backend.controller;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.HttpUtil;
import com.nnte.kr_business.annotation.ConfigLoad;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.base.KRConfigInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

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
    /*
    * 测试Excel文件的服务器转换
    * */
    @RequestMapping(value = "testConvExcel")
    @ResponseBody
    public Map<String,Object> testConvExcel(HttpServletRequest request){
        Map<String,Object> retMap=BaseNnte.newMapRetObj();
        String file="123.xls";
        String url="http://localhost:8891/KingService/OfficeConverPDF/office2PdfBytes?type=webstatic&fileName="+file;
        String filepath="/d:";
        try {
            String retmsg=HttpUtil.sendHttpFile(url,filepath+"/"+file);
            BaseNnte.setRetTrue(retMap,retmsg);
            return retMap;
        } catch (IOException e) {
            e.printStackTrace();
            BaseNnte.setRetFalse(retMap,1002,e.getMessage());
            return retMap;
        }
    }
}
