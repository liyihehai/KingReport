package com.nnte.kr_backend.controller.autoReport;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportParamsComponent;
import com.nnte.kr_business.component.autoReport.ReportPeriodComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.entity.autoReport.ReportBusiType;
import com.nnte.kr_business.entity.autoReport.ReportPeriodSetting;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
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
public class AutoReportParamsController extends BaseController {
    @Autowired
    private AutoReportParamsComponent autoReportParamsComponent;
    @Autowired
    private ReportPeriodComponent reportPeriodComponent;

    @RequestMapping(value = "reportParamsMgr")
    public ModelAndView reportQueryMgr(HttpServletRequest request, ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportParamsMgr");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        List<KeyValue> LibBusiTypeState=AutoReportParamsComponent.LibBusiTypeState;
        map.put("LibBusiTypeState", JsonUtil.getJsonString4JavaList(LibBusiTypeState, DateUtils.DF_YMDHMS));
        Map<String,Object> mrpMap=autoReportParamsComponent.getMerchantReportPeriod(map);
        map.put("rps",mrpMap.get("rps"));
        modelAndView.addObject("map", map);
        return modelAndView;
    }

    private void setLoadCondition(HttpServletRequest request,Map<String,Object> reqParamMap,Map<String,Object> pMap){
    }

    @RequestMapping("/loadReportParamBusiTypes")
    public void loadReportParamBusiTypes(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        Integer sEcho = NumberUtil.getDefaultInteger(request.getParameter("sEcho"));
        pMap.put("start", NumberUtil.getDefaultInteger(request.getParameter("iDisplayStart")));
        pMap.put("limit", NumberUtil.getDefaultInteger(request.getParameter("iDisplayLength")));
        setLoadCondition(request,null,pMap);
        Map<String,Object> loadMap=autoReportParamsComponent.loadReportParamBusiTypes(pMap,false);
        Integer count = NumberUtil.getDefaultInteger(loadMap.get("count"));
        List<ReportBusiType> lists = (List<ReportBusiType>)loadMap.get("list");
        if (lists!=null)
            printLoadListMsg(response,sEcho+1,count, JsonUtil.getJsonString4JavaList(lists, DateUtils.DF_YMDHMS));
        else {
            lists = new ArrayList<>();
            printLoadListMsg(response,sEcho + 1, 0, JsonUtil.getJsonString4JavaList(lists, DateUtils.DF_YMDHMS));
        }
    }

    @RequestMapping("/saveMerchantPeriodSetting")
    @ResponseBody
    public Map<String, Object> saveMerchantPeriodSetting(HttpServletRequest request, @RequestBody ReportPeriodSetting period) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (period == null) {
            BaseNnte.setRetFalse(ret, 1002, "参数错误(空)");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeDay()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(日报表截止时间未设置)");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeWeek()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(周报表截止星期未设置))");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeMonth()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(月报表截止日期未设置))");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeQuarter()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(季报表截止日期未设置))");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeHalfYear()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(半年报截止日期未设置))");
            return ret;
        }
        if (StringUtils.isEmpty(period.getEndTimeYear()))
        {
            BaseNnte.setRetFalse(ret,1002,"参数错误(年报截止日期未设置))");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=reportPeriodComponent.saveReportPeriodSetting(pMap,
                KingReportComponent.getLoginMerchantFromParamMap(pMap).getParMerchantId(),period);
        return ret;
    }
    @RequestMapping("/defaultMerchantPeriodSetting")
    @ResponseBody
    public Map<String, Object> defaultMerchantPeriodSetting() {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseNnte.setRetTrue(ret,"获得默认值成功");
        ret.put("settings",new ReportPeriodSetting());
        return ret;
    }


    @RequestMapping("/queryParamBusiType")
    @ResponseBody
    public Map<String, Object> queryParamBusiType(HttpServletRequest request,@RequestBody ReportBusiType RBT){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (RBT==null || StringUtils.isEmpty(RBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型编号错误)");
            return ret;
        }
        BaseMerchant merchant=KingReportComponent.getLoginMerchantFromRequest(request);
        ReportBusiType queryRBT=autoReportParamsComponent.queryParamBusiType(merchant.getParMerchantId(),
                RBT.getBusiTypeCode());
        if (queryRBT==null){
            BaseNnte.setRetFalse(ret,1002,"未查询到指定的业务类型");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询指定报表业务类型成功");
        ret.put("entity",queryRBT);
        return ret;
    }

    @RequestMapping("/onSaveParamBusiType")
    @ResponseBody
    public Map<String, Object> onSaveParamBusiType(HttpServletRequest request,@RequestBody ReportBusiType RBT) {
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (RBT==null || StringUtils.isEmpty(RBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型编号错误)");
            return ret;
        }
        if (StringUtils.isEmpty(RBT.getBusiTypeName())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型名称错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        pMap.put("saveRBT",RBT);
        return autoReportParamsComponent.saveParamBusiTypes(pMap);
    }
    @RequestMapping("/deleteParamBusiType")
    @ResponseBody
    public Map<String, Object> deleteParamBusiType(HttpServletRequest request,@RequestBody ReportBusiType RBT) {
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (RBT==null || StringUtils.isEmpty(RBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型编号错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        pMap.put("deleteRBT",RBT);
        return autoReportParamsComponent.deleteParamBusiType(pMap);
    }

    @RequestMapping("/stateParamBusiType")
    @ResponseBody
    public Map<String, Object> stateParamBusiType(HttpServletRequest request,@RequestBody ReportBusiType RBT) {
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (RBT==null || StringUtils.isEmpty(RBT.getBusiTypeCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型编号错误)");
            return ret;
        }
        if (RBT.getBusiTypeState()==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(业务类型状态错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        pMap.put("stateRBT",RBT);
        return autoReportParamsComponent.stateParamBusiType(pMap);
    }
}
