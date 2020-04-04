package com.nnte.kr_backend.controller.autoReport;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportDBConnComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping(value = "/autoReport")
public class AutoReportDBConnController extends BaseController {
    @Autowired
    private AutoReportDBConnComponent autoReportDBConnComponent;

    @RequestMapping(value = "reportDbConnMgr")
    public ModelAndView reportDbConnMgr(HttpServletRequest request,ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportDBConnMgr");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        BaseMerchant merchant= KingReportComponent.getLoginMerchantFromRequest(request);
        map.put("LibReportDBConnState", JsonUtil.beanToJson(AutoReportDBConnComponent.LibReportDBConnState));
        List<KeyValue> LibDBConnType=autoReportDBConnComponent.getLibDBConnType();
        map.put("LibDBConnType", JsonUtil.beanToJson(LibDBConnType));
        map.put("LibDBConnTypeOption",getKeyValListOption(LibDBConnType,null));
        modelAndView.addObject("map", map);
        return modelAndView;
    }

    @RequestMapping("/queryReportDBConnDefine")
    @ResponseBody
    public Map<String, Object> queryReportDBConnDefine(@RequestBody MerchantDbconnectDefine MDBD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MDBD==null || MDBD.getId()==null || MDBD.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(连接编号错误)");
            return ret;
        }
        MerchantDbconnectDefine mdbd=autoReportDBConnComponent.getReportDBConnDefineById(MDBD.getId());
        if (mdbd==null){
            BaseNnte.setRetFalse(ret,1002,"未查询到指定的连接");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询指定连接成功");
        ret.put("entity",mdbd);
        return ret;
    }

    private void setLoadCondition(HttpServletRequest request,Map<String,Object> reqParamMap,Map<String,Object> pMap){
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"connCode")))
            pMap.put("connCode",getRequestParam(request,reqParamMap,"connCode"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"connName")))
            pMap.put("connName",getRequestParam(request,reqParamMap,"connName"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"dbType")))
            pMap.put("dbType",getRequestParam(request,reqParamMap,"dbType"));
        Object o=getRequestParam(request,reqParamMap,"connState");
        if (o!=null) {
            Integer connState = NumberUtil.getDefaultInteger(o);
            if (connState >= 0)
                pMap.put("connState", connState);
        }
    }

    @RequestMapping("/exportMerchantReportConnDefs")
    @ResponseBody
    public Map<String, Object> exportMerchantReportConnDefs(HttpServletRequest request,
                                                        @RequestBody Map<String,Object> paramMap){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        setLoadCondition(request,paramMap,pMap);
        Map<String,Object> ret=autoReportDBConnComponent.exportMerchantReportConnDefs(pMap);
        return ret;
    }

    @RequestMapping("/loadMerchantReportConnDefs")
    public void loadMerchantReportConnDefs(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        Integer sEcho = NumberUtil.getDefaultInteger(request.getParameter("sEcho"));
        pMap.put("start", NumberUtil.getDefaultInteger(request.getParameter("iDisplayStart")));
        pMap.put("limit", NumberUtil.getDefaultInteger(request.getParameter("iDisplayLength")));
        setLoadCondition(request,null,pMap);
        Map<String,Object> loadMap=autoReportDBConnComponent.loadMerchantReportConnDefs(pMap);
        Integer count = NumberUtil.getDefaultInteger(loadMap.get("count"));
        List<MerchantDbconnectDefine> lists = (List<MerchantDbconnectDefine>)loadMap.get("list");
        if (lists!=null)
            printLoadListMsg(response,sEcho+1,count, JsonUtil.beanToJson(lists));
        else {
            lists = new ArrayList<>();
            printLoadListMsg(response,sEcho + 1, 0, JsonUtil.beanToJson(lists));
        }
    }


    @RequestMapping("/saveReportDBconn")
    @ResponseBody
    public Map<String, Object> saveReportDBconn(HttpServletRequest request,@RequestBody JsonNode jsonParam){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (jsonParam==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(空)");
            return ret;
        }
        JsonUtil.JNode jNode=JsonUtil.createJNode(jsonParam);
        Map<String,Object> pMap=new HashMap<>();
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("connCode")),
                "connCode",pMap, ret,1002,"参数错误(连接代码未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("connName")),
                "connName",pMap, ret,1002,"参数错误( 连接名称未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbType")),
                "dbType",pMap, ret,1002,"参数错误(数据库类型未设置)"))
            return ret;
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbIp")),
                "dbIp",pMap, ret,1002,"参数错误(数据库IP未设置)");
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbPort")),
                "dbPort",pMap, ret,1002,"参数错误(数据库端口未设置)");
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbSchema")),
                "dbSchema",pMap, ret,1002,"参数错误(数据库名未设置)"))
            return ret;
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbUser")),
                "dbUser",pMap, ret,1002,"参数错误(数据库用户名未设置)");
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jNode.get("dbPassword")),
                "dbPassword",pMap, ret,1002,"参数错误(数据库密码未设置)");
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportDBConnComponent.saveReportDBconn(pMap);
        return ret;
    }

    @RequestMapping("/updateDBConnState")
    @ResponseBody
    public Map<String, Object> updateDBConnState(HttpServletRequest request,
                                                 @RequestBody MerchantDbconnectDefine MDBD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MDBD==null || MDBD.getId()==null || MDBD.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(未指定连接)");
            return ret;
        }
        if (MDBD.getConnState()==null || MDBD.getConnState()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(状态值错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportDBConnComponent.updateDBConnState(pMap,MDBD.getId(),MDBD.getConnState());
        return ret;
    }
}
