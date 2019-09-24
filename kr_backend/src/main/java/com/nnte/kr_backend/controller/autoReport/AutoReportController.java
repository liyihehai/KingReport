package com.nnte.kr_backend.controller.autoReport;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.Excel.ExcelConfig;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.base.ExcelUtil;
import com.nnte.kr_business.component.autoReport.AutoReportComponent;
import com.nnte.kr_business.component.autoReport.AutoReportQueryComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping(value = "/autoReport")
public class AutoReportController extends BaseController {
    @Autowired
    private AutoReportComponent autoReportComponent;
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;

    @RequestMapping(value = "indexReport")
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/indexReport");
        return modelAndView;
    }

    @RequestMapping(value = "reportMain")
    public ModelAndView reportMain(ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportMain");
        return modelAndView;
    }

    @RequestMapping(value = "reportManager")
    public ModelAndView reportManager(HttpServletRequest request,ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportManager");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        BaseMerchant merchant= KingReportComponent.getLoginMerchantFromRequest(request);
        List<KeyValue> LibReportClass = autoReportQueryComponent.getMerchantCutFlagQuerys(merchant.getParMerchantId(),null);
        map.put("LibReportClass",JsonUtil.getJsonString4JavaList(LibReportClass,DateUtils.DF_YMDHMS));
        map.put("LibReportClassOption",getKeyValListOption(LibReportClass,null));
        map.put("LibReportPeriod",JsonUtil.getJsonString4JavaList(DataLibrary.getLibKeyValueList("LibReportPeriod"),DateUtils.DF_YMDHMS));
        map.put("LibReportPeriodOption",getKeyValListOption(DataLibrary.getLibKeyValueList("LibReportPeriod"),DateUtils.DF_YMDHMS));
        map.put("LibReportState",JsonUtil.getJsonString4JavaList(DataLibrary.getLibKeyValueList("LibReportState"),DateUtils.DF_YMDHMS));
        //取商户报表业务分类,从系统参数中取得
        List<KeyValue> LibReportBusiType=sysParamComponent.getMerchantMulKVParams(merchant.getParMerchantId(),"SYSPARAM_MERCHANT_REPORT_BUSI_TYPE");
        map.put("LibReportBusiType",JsonUtil.getJsonString4JavaList(LibReportBusiType,DateUtils.DF_YMDHMS));
        map.put("LibReportBusiTypeOption",getKeyValListOption(LibReportBusiType,null));
        modelAndView.addObject("map", map);
        return modelAndView;
    }

    @RequestMapping(value = "openReport")
    @ResponseBody
    public String openReport(String reportCode){
        String retHtml="";
        String templateFile="temp";
        String reportRoot="/";
        String trPath="excel/";
        String refPath="excel/";
        String rdfPath="excel/";
        ExcelConfig ec = new ExcelConfig(reportRoot,trPath,refPath,rdfPath);
        ExcelUtil.XSSFWorkbookAndOPC wao=ExcelUtil.openExcelTemplate(ec,templateFile);
        if (wao!=null)
        {
            ExcelUtil.saveExcelFile(wao,"template_ins");
            ExcelUtil.closeExcelTemplate(wao);
        }
        return retHtml;
    }

    @RequestMapping("/uploadReportTemplateFile")
    @ResponseBody
    public Map<String, Object> uploadReportTemplateFile(HttpServletRequest request,String reportCode){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (StringUtils.isEmpty(reportCode)){
            BaseNnte.setRetFalse(ret,1002,"报表代码不合法");
            return ret;
        }
        // 转型为MultipartHttpRequest：
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获得文件：
        MultipartFile templateFile= multipartRequest.getFile("ReportTemplateFile");
        if (templateFile==null){
            BaseNnte.setRetFalse(ret,1003,"不能取得上传的文件内容");
            return ret;
        }
        String fname=templateFile.getOriginalFilename();
        byte[] bytes = null;
        try {
            bytes = templateFile.getBytes();
            if (bytes==null || bytes.length<=0){
                BaseNnte.setRetFalse(ret,1003,"不能取得上传的文件内容");
                return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
            BaseNnte.setRetFalse(ret,1003,"不能取得上传的文件内容");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportComponent.uploadTemplateFile(pMap,1L,reportCode,fname,bytes);
        return ret;
    }

    @RequestMapping("/saveMerchantReportDefine")
    @ResponseBody
    public Map<String, Object> saveMerchantReportDefine(HttpServletRequest request,
                                                        @RequestBody JSONObject jsonParam){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (jsonParam==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(空)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportCode")),
                "reportCode",pMap, ret,1002,"参数错误(报表代码未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportName")),
                "reportName",pMap, ret,1002,"参数错误(报表名称未设置)"))
            return ret;
        //报表分割可以不设置
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportClass")),
                "reportClass",pMap, ret,1002,"参数错误(报表分割未设置)");
        //-----------------
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportYWType")),
                "reportBusiType",pMap, ret,1002,"参数错误(业务分级未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(jsonParam.get("reportPeriod")),
                "reportPeriod",pMap, ret,1002,"参数错误(报表周期未设置)"))
            return ret;
        String startDate=StringUtils.defaultString(jsonParam.get("startDate"));
        Date sd = DateUtils.stringToDate(startDate,"yyyy-MM-dd");
        if (sd==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(开始日期未设置)");
            return ret;
        }
        pMap.put("startDate",sd);
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportComponent.saveMerchantReportDefine(pMap);
        return ret;
    }

    private void setLoadCondition(HttpServletRequest request,Map<String,Object> reqParamMap,Map<String,Object> pMap){
        String createDateRange=StringUtils.defaultString(getRequestParam(request,reqParamMap,"createDateRange"));
        if (StringUtils.isNotEmpty(createDateRange)) {
            Map<String,Object> dateMap= DateUtils.splitDateRange(createDateRange);
            if (dateMap.get("suc")!=null && "1".equals(dateMap.get("suc").toString()))
            {
                pMap.put("timeStart", dateMap.get("startdate"));
                pMap.put("timeEnd", dateMap.get("enddate"));
            }
        }
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportCode")))
            pMap.put("reportCode",getRequestParam(request,reqParamMap,"reportCode"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportName")))
            pMap.put("reportName",getRequestParam(request,reqParamMap,"reportName"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportClass")))
            pMap.put("reportClass",getRequestParam(request,reqParamMap,"reportClass"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportBusiType")))
            pMap.put("reportBusiType",getRequestParam(request,reqParamMap,"reportBusiType"));
        if (StringUtils.isNotEmpty(getRequestParam(request,reqParamMap,"reportPeriod")))
            pMap.put("reportPeriod", getRequestParam(request,reqParamMap,"reportPeriod"));
        Object o=getRequestParam(request,reqParamMap,"reportState");
        if (o!=null) {
            Integer reportState = NumberUtil.getDefaultInteger(o);
            if (reportState >= 0)
                pMap.put("reportState", reportState);
        }
    }

    @RequestMapping("/loadMerchantReportDef")
    public void loadMerchantReportDef(HttpServletRequest request,HttpServletResponse response){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        Integer sEcho = NumberUtil.getDefaultInteger(request.getParameter("sEcho"));
        pMap.put("start", NumberUtil.getDefaultInteger(request.getParameter("iDisplayStart")));
        pMap.put("limit", NumberUtil.getDefaultInteger(request.getParameter("iDisplayLength")));
        setLoadCondition(request,null,pMap);
        Map<String,Object> loadMap=autoReportComponent.loadMerchantReportDef(pMap);
        Integer count = NumberUtil.getDefaultInteger(loadMap.get("count"));
        List<MerchantReportDefine> lists = (List<MerchantReportDefine>)loadMap.get("list");
        if (lists!=null)
            printLoadListMsg(response,sEcho+1,count, JsonUtil.getJsonString4JavaList(lists,DateUtils.DF_YMDHMS));
        else {
            lists = new ArrayList<>();
            printLoadListMsg(response,sEcho + 1, 0, JsonUtil.getJsonString4JavaList(lists, DateUtils.DF_YMDHMS));
        }
    }

    @RequestMapping("/queryMerchantReportDefine")
    @ResponseBody
    public Map<String, Object> queryMerchantReportDefineById(@RequestBody MerchantReportDefine MRD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRD==null || MRD.getId()==null || MRD.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(报表编号错误)");
            return ret;
        }
        MerchantReportDefine merchantReportDefine=autoReportComponent.getReportRecordById(MRD.getId());
        if (merchantReportDefine==null){
            BaseNnte.setRetFalse(ret,1002,"未查询到指定的报表");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询指定报表成功");
        ret.put("merchantReportDefine",merchantReportDefine);
        //查询模板文件列表
        List<String> templateFiles=autoReportComponent.getReportTemplateFileNames(merchantReportDefine.getParMerchantId(),
                merchantReportDefine.getReportCode());
        if (templateFiles!=null)
            ret.put("templateFiles",templateFiles);
        return ret;
    }

    @RequestMapping("/genNextPeriodReport")
    @ResponseBody
    public Map<String, Object> genNextPeriodReport(@RequestBody MerchantReportDefine MRD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRD==null || MRD.getId()==null || MRD.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(报表编号错误)");
            return ret;
        }
        MerchantReportDefine merchantReportDefine=autoReportComponent.getReportRecordById(MRD.getId());
        if (merchantReportDefine==null){
            BaseNnte.setRetFalse(ret,1002,"未查询到指定的报表");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        ret=autoReportComponent.generatorReportFile(pMap,merchantReportDefine);
        return ret;
    }

    @RequestMapping("/saveReportTemplate")
    @ResponseBody
    public Map<String, Object> saveReportTemplate(HttpServletRequest request,
                                                  @RequestBody MerchantReportDefine MRD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRD==null || MRD.getReportCode()==null || StringUtils.isEmpty(MRD.getReportCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(报表代码错误)");
            return ret;
        }
        if (StringUtils.isEmpty(MRD.getTemplateFile())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(模板文件名错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportComponent.saveReportTemplate(pMap,MRD.getReportCode(),MRD.getTemplateFile());
        return ret;
    }
    @RequestMapping("/updateReportState")
    @ResponseBody
    public Map<String, Object> updateReportState(HttpServletRequest request,
                                                  @RequestBody MerchantReportDefine MRD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRD==null || MRD.getId()==null || MRD.getId()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(未指定报表)");
            return ret;
        }
        if (MRD.getReportState()==null || MRD.getReportState()<=0){
            BaseNnte.setRetFalse(ret,1002,"参数错误(状态值错误)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportComponent.updateMerchantReportState(pMap,MRD.getId(),MRD.getReportState());
        return ret;
    }

    @RequestMapping("/exportMerchantReportDefs")
    @ResponseBody
    public Map<String, Object> exportMerchantReportDefs(HttpServletRequest request,
                                                        @RequestBody Map<String,Object> paramMap){
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        setLoadCondition(request,paramMap,pMap);
        Map<String,Object> ret=autoReportComponent.exportMerchantReportDefs(pMap);
        return ret;
    }
}
