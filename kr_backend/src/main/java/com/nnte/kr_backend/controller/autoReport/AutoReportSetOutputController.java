package com.nnte.kr_backend.controller.autoReport;

import com.nnte.framework.base.BaseNnte;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping(value = "/autoReportSetOutput")
public class AutoReportSetOutputController extends BaseController {

    @Autowired
    private AutoReportComponent autoReportComponent;

    @RequestMapping(value = "openSetOutput")
    public ModelAndView openSetOutput(HttpServletRequest request,String reportCode, ModelAndView modelAndView){
        //打开报表输出设置页面
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        map.put("reportCode",reportCode);
        Map<String,Object> mrdMap=autoReportComponent.getReportRecordByCode(map);
        if (!BaseNnte.getRetSuc(mrdMap)) {
            return null;//没有找到报表或报表的商户与请求不一致
        }
        map.put("LibControlCircleItemTypeOption",getKeyValListOption(AutoReportComponent.LibControlCircleItemType,null));
        map.put("LibCircleItemDataTypeOption",getKeyValListOption(AutoReportComponent.LibCircleItemDataType,null));
        map.put("merchantReportDefine",mrdMap.get("merchantReportDefine"));
        modelAndView.addObject("map", map);
        modelAndView.setViewName("front/autoReport/reportSetOutput");
        return modelAndView;
    }


}
