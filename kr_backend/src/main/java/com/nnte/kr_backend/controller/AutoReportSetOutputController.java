package com.nnte.kr_backend.controller;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportComponent;
import com.nnte.kr_business.component.autoReport.AutoReportQueryComponent;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;

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
        MerchantReportDefine mrd=(MerchantReportDefine)mrdMap.get("merchantReportDefine");
        map.put("merchantReportDefine",mrd);

        //查询报表查询(排除分割查询)
        modelAndView.addObject("map", map);
        modelAndView.setViewName("front/autoReport/reportSetOutput");
        return modelAndView;
    }

    @RequestMapping("/saveReportOutput")
    @ResponseBody
    public Map<String, Object> saveReportOutput(HttpServletRequest request,@RequestBody String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        JsonUtil.JNode jNode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
        if (jNode==null){
            BaseNnte.setRetFalse(ret,-1,"数据输入错误");
            return ret;
        }
        String reportCode=jNode.getText("reportCode");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        map.put("reportCode",reportCode);
        Map<String,Object> mrdMap=autoReportComponent.getReportRecordByCode(map);
        if (!BaseNnte.getRetSuc(mrdMap)) {
            BaseNnte.setRetFalse(ret,-1,"没有找到报表或报表的商户与请求不一致");
            return ret;
        }
        Object cntrolObj=jNode.get("reportContrlObj");
        map.put("merchantReportDefine",mrdMap.get("merchantReportDefine"));
        map.put("output",cntrolObj.toString());
        Map<String,Object> outputSaveMap=autoReportComponent.saveReportOutput(map);
        if (!BaseNnte.getRetSuc(outputSaveMap)) {
            return outputSaveMap;
        }
        BaseNnte.setRetTrue(ret,"保存报表输出成功");
        return ret;
    }

    @RequestMapping("/queryReportDataQuerysForOption")
    @ResponseBody
    public Map<String, Object> queryReportDataQuerysForOption(HttpServletRequest request,@RequestBody String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        JsonUtil.JNode jNode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
        if (jNode==null){
            BaseNnte.setRetFalse(ret,-1,"数据输入错误");
            return ret;
        }
        String reportCode=jNode.getText("reportCode");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        map.put("reportCode",reportCode);
        Map<String,Object> rdqOptionMap=autoReportQueryComponent.queryReportDataQuerysForOption(map);
        return rdqOptionMap;
    }
}
