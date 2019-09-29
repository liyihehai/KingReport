package com.nnte.kr_backend.controller.autoReport;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportComponent;
import com.nnte.kr_business.component.autoReport.AutoReportDBConnComponent;
import com.nnte.kr_business.component.autoReport.AutoReportQueryComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping(value = "/autoReport")
public class AutoReportQueryController extends BaseController {
    @Autowired
    private AutoReportComponent autoReportComponent;
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;
    @Autowired
    private AutoReportDBConnComponent autoReportDBConnComponent;

    @RequestMapping(value = "reportQueryMgr")
    public ModelAndView reportQueryMgr(HttpServletRequest request,ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportQueryMgr");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        BaseMerchant merchant= KingReportComponent.getLoginMerchantFromRequest(request);
        map.put("LibReportQueryType", JsonUtil.getJsonString4JavaList(AutoReportQueryComponent.LibReportQueryType, DateUtils.DF_YMDHMS));
        map.put("LibReportQueryTypeOption",getKeyValListOption(AutoReportQueryComponent.LibReportQueryType,null));
        List<KeyValue> LibMerchantReport=autoReportComponent.getUsedReportsByMerchantKV(merchant.getParMerchantId());
        map.put("LibMerchantReport", JsonUtil.getJsonString4JavaList(LibMerchantReport, DateUtils.DF_YMDHMS));
        map.put("LibMerchantReportOption",getKeyValListOption(LibMerchantReport,null));
        List<KeyValue> LibReportDBConn=autoReportDBConnComponent.getUsedReportDBConnsKV(merchant.getParMerchantId());
        map.put("LibReportDBConn", JsonUtil.getJsonString4JavaList(LibReportDBConn,DateUtils.DF_YMDHMS));
        map.put("LibReportDBConnOption",getKeyValListOption(LibReportDBConn,null));
        map.put("LibReportQueryCutFlag", JsonUtil.getJsonString4JavaList(AutoReportQueryComponent.LibReportQueryCutFlag,DateUtils.DF_YMDHMS));
        map.put("LibReportQueryCutFlagOption",getKeyValListOption(AutoReportQueryComponent.LibReportQueryCutFlag,null));
        modelAndView.addObject("map", map);
        return modelAndView;
    }

    @RequestMapping(value = "reportQueryBodySet")
    public ModelAndView reportQueryBodySet(HttpServletRequest request,ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportQueryBodySet");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        BaseMerchant merchant=KingReportComponent.getLoginMerchantFromRequest(request);
        //------------------------------------------------
        modelAndView.addObject("merchant", merchant);
        //------------------------------------------------
        map.put("LibQueryResKeyWord",autoReportQueryComponent.LibQueryResKeyWord);
        modelAndView.addObject("map", map);
        return modelAndView;
    }
    @RequestMapping("/queryReportQueryDefine")
    @ResponseBody
    public Map<String, Object> queryReportQueryDefine(@RequestBody MerchantReportQuery MRQ){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRQ==null || MRQ.getId()==null || MRQ.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(查询编号错误)");
            return ret;
        }
        MerchantReportQuery mrd=autoReportQueryComponent.getReportQueryDefineById(MRQ.getId());
        if (mrd==null){
            BaseNnte.setRetFalse(ret,1002,"未查询到指定的查询");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询指定报表查询成功");
        ret.put("entity",mrd);
        return ret;
    }

    private void setLoadCondition(HttpServletRequest request,Map<String,Object> reqParamMap,Map<String,Object> pMap){
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"queryCode")))
            pMap.put("queryCode",getRequestParam(request,reqParamMap,"queryCode"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"queryName")))
            pMap.put("queryName",getRequestParam(request,reqParamMap,"queryName"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"queryType")))
            pMap.put("queryType",getRequestParam(request,reqParamMap,"queryType"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportId")))
            pMap.put("reportId", NumberUtil.getDefaultLong(getRequestParam(request,reqParamMap,"reportId")));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"connId")))
            pMap.put("connId",NumberUtil.getDefaultLong(getRequestParam(request,reqParamMap,"connId")));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"cutFlag")))
            pMap.put("cutFlag",NumberUtil.getDefaultLong(getRequestParam(request,reqParamMap,"cutFlag")));
    }

    @RequestMapping("/exportMerchantReporQuerys")
    @ResponseBody
    public Map<String, Object> exportMerchantReporQuerys(HttpServletRequest request,
                                                        @RequestBody Map<String,Object> paramMap){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        setLoadCondition(request,paramMap,pMap);
        Map<String,Object> ret=autoReportQueryComponent.exportMerchantReporQuerys(pMap);
        return ret;
    }

    @RequestMapping("/loadMerchantReportQuerys")
    public void loadMerchantReportQuerys(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        Integer sEcho = NumberUtil.getDefaultInteger(request.getParameter("sEcho"));
        pMap.put("start", NumberUtil.getDefaultInteger(request.getParameter("iDisplayStart")));
        pMap.put("limit", NumberUtil.getDefaultInteger(request.getParameter("iDisplayLength")));
        setLoadCondition(request,null,pMap);
        Map<String,Object> loadMap=autoReportQueryComponent.loadMerchantReportQuerys(pMap);
        Integer count = NumberUtil.getDefaultInteger(loadMap.get("count"));
        List<MerchantReportQuery> lists = (List<MerchantReportQuery>)loadMap.get("list");
        if (lists!=null)
            printLoadListMsg(response,sEcho+1,count, JsonUtil.getJsonString4JavaList(lists,DateUtils.DF_YMDHMS));
        else {
            lists = new ArrayList<>();
            printLoadListMsg(response,sEcho + 1, 0, JsonUtil.getJsonString4JavaList(lists, DateUtils.DF_YMDHMS));
        }
    }

    @RequestMapping("/saveReportQuery")
    @ResponseBody
    public Map<String, Object> saveReportDBconn(HttpServletRequest request,@RequestBody JSONObject jsonParam){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (jsonParam==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(空)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("queryCode")),
                "queryCode",pMap, ret,1002,"参数错误(查询代码未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("queryName")),
                "queryName",pMap, ret,1002,"参数错误(查询名称未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("queryType")),
                "queryType",pMap, ret,1002,"参数错误(查询类型未设置)"))
            return ret;
        //如果分割标志为是，不设置所属报表
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("cutFlag")),
                "cutFlag",pMap, ret,1002,"参数错误(分割标志未设置)"))
            return ret;
        if (pMap.get("cutFlag").equals("1")){
            if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("cutTypeName")),
                    "cutTypeName",pMap, ret,1002,"参数错误(分割名称未设置)"))
                return ret;
        }else {
            if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportId")),
                    "reportId", pMap, ret, 1002, "参数错误(所属报表)"))
                return ret;
        }
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("connId")),
                "connId",pMap, ret,1002,"参数错误(数据连接未设置)"))
            return ret;
        String maxRowCount_str=StringUtils.defaultString(jsonParam.get("maxRowCount"));
        Integer maxRowCount=NumberUtil.getDefaultInteger(maxRowCount_str);
        if (maxRowCount<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(最大行数未设置)");
            return ret;
        }
        if (maxRowCount>3000){
            BaseNnte.setRetFalse(ret,1002,"参数错误(最大行不能超过3000)");
            return ret;
        }
        pMap.put("maxRowCount",maxRowCount);
        //-------------------------------------------------
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportQueryComponent.saveReportQuery(pMap);
        return ret;
    }

    @RequestMapping("/saveQueryBodySet")
    @ResponseBody
    public Map<String, Object> saveQueryBodySet(HttpServletRequest request,
                                               @RequestBody JSONObject jsonParam){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        Map<String,Object> pMap=new HashMap<>();
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("queryCode")),
                "queryCode",pMap, ret,1002,"参数错误(查询代码未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("querySql")),
                "querySql",pMap, ret,1002,"参数错误(查询语句未设置)"))
            return ret;
        //-------------------------------------------------
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportQueryComponent.saveQueryBodySet(pMap);
        return ret;
    }
}
