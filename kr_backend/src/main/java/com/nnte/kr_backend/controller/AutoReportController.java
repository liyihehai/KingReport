package com.nnte.kr_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.*;
import com.nnte.kr_backend.KingReportConfig;
import com.nnte.kr_business.base.BaseController;
import com.nnte.kr_business.component.autoReport.AutoReportComponent;
import com.nnte.kr_business.component.autoReport.AutoReportQueryComponent;
import com.nnte.kr_business.component.autoReport.AutoReportRecComponent;
import com.nnte.kr_business.component.autoReport.AutoReportServerComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import com.nnte.kr_business.mapper.workdb.merchant.rec.MerchantReportRec;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private KingReportConfig kingReportConfig;
    @Autowired
    private AutoReportComponent autoReportComponent;
    @Autowired
    private AutoReportQueryComponent autoReportQueryComponent;
    @Autowired
    private AutoReportServerComponent autoReportServerComponent;
    @Autowired
    private AutoReportRecComponent autoReportRecComponent;

    @RequestMapping(value = "indexReport")
    public ModelAndView index(HttpServletRequest request,ModelAndView modelAndView){
        //取得商户报表分类定义
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        Map<String,Object> busiMap=autoReportComponent.getMerchantBTReportList(map,true);
        if (BaseNnte.getRetSuc(busiMap)){
            List<Map<String,Object>> BTReportList=(List<Map<String,Object>>)busiMap.get("BTReportList");
            if (BTReportList!=null && BTReportList.size()>0){
                map.put("BTReportList",BTReportList);
            }
        }
        modelAndView.addObject("map", map);
        modelAndView.setViewName("front/autoReport/indexReport");
        return modelAndView;
    }

    /*
     * 按业务分类打开报表装载页面
     * */
    @RequestMapping(value = "openBusiTypeReport")
    public ModelAndView openBusiTypeReport(HttpServletRequest request, String reportCode, ModelAndView modelAndView){
        modelAndView.setViewName("front/autoReport/reportBTOpen");
        Map<String,Object> map=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,map);
        map.put("reportCode",reportCode);
        Map<String,Object> mapOpen=autoReportServerComponent.onOpenBusiTypeReport(map);
        if (BaseNnte.getRetSuc(mapOpen)) {
            map.put("reportDefine", mapOpen.get("reportDefine"));
            map.put("cutQuery",mapOpen.get("cutQuery"));
            map.put("cutKVList",mapOpen.get("cutKVList"));
        }
        modelAndView.addObject("map", map);
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
        map.put("LibReportClass",JsonUtil.beanToJson(LibReportClass));
        map.put("LibReportClassOption",getKeyValListOption(LibReportClass,null));
        map.put("LibReportPeriod",JsonUtil.beanToJson(DataLibrary.getLibKeyValueList("LibReportPeriod")));
        map.put("LibReportPeriodOption",getKeyValListOption(DataLibrary.getLibKeyValueList("LibReportPeriod"),DateUtils.DF_YMDHMS));
        map.put("LibReportState",JsonUtil.beanToJson(DataLibrary.getLibKeyValueList("LibReportState")));
        //取商户报表业务分类,从系统参数中取得
        List<KeyValue> LibReportBusiType=sysParamComponent.getMerchantMulKVParams(merchant.getParMerchantId(),"SYSPARAM_MERCHANT_REPORT_BUSI_TYPE");
        map.put("LibReportBusiType",JsonUtil.beanToJson(LibReportBusiType));
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
        pMap.put("KingReportConfig",kingReportConfig);
        ret=autoReportComponent.uploadTemplateFile(pMap,1L,reportCode,fname,bytes);
        return ret;
    }

    @RequestMapping("/saveMerchantReportDefine")
    @ResponseBody
    public Map<String, Object> saveMerchantReportDefine(HttpServletRequest request,
                                                        @RequestBody JsonNode jsonParam){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        JsonUtil.JNode node=JsonUtil.createJNode(jsonParam);
        if (jsonParam==null){
            BaseNnte.setRetFalse(ret,1002,"参数错误(空)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(node.get("reportCode")),
                "reportCode",pMap, ret,1002,"参数错误(报表代码未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(node.get("reportName")),
                "reportName",pMap, ret,1002,"参数错误(报表名称未设置)"))
            return ret;
        //报表分割可以不设置
        BaseNnte.checkSetParamMapStr(StringUtils.defaultString(node.get("reportClass")),
                "reportClass",pMap, ret,1002,"参数错误(报表分割未设置)");
        //-----------------
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(node.get("reportYWType")),
                "reportBusiType",pMap, ret,1002,"参数错误(业务分级未设置)"))
            return ret;
        if (!BaseNnte.checkSetParamMapStr(StringUtils.defaultString(node.get("reportPeriod")),
                "reportPeriod",pMap, ret,1002,"参数错误(报表周期未设置)"))
            return ret;
        String startDate=StringUtils.defaultString(node.get("startDate"));
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
            printLoadListMsg(response,sEcho+1,count, JsonUtil.beanToJson(lists));
        else {
            lists = new ArrayList<>();
            printLoadListMsg(response,sEcho + 1, 0, JsonUtil.beanToJson(lists));
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
        List<String> templateFiles=autoReportComponent.getReportTemplateFileNames(merchantReportDefine);
        if (templateFiles!=null)
            ret.put("templateFiles",templateFiles);
        //查询分割选项
        if (StringUtils.isNotEmpty(merchantReportDefine.getReportClass())) {
            MerchantReportQuery mrq=autoReportQueryComponent.getReportQueryByCode(null,
                    merchantReportDefine.getParMerchantId(),merchantReportDefine.getReportClass());
            if (mrq!=null) {
                ret.put("cutTypeName", mrq.getCutTypeName());
                List<JsonNode> jarray=JsonUtil.jsonToNodeArray(mrq.getQuerySqlCols());
                if (jarray!=null){
                    List<String> colNameList=new ArrayList<>();
                    for (int i=0;i<jarray.size();i++){
                        JsonNode jobj=jarray.get(i);
                        colNameList.add(jobj.get("colName").textValue());
                    }
                    ret.put("cutColNameList", colNameList);
                }
            }
        }
        return ret;
    }

    @RequestMapping("/genNextPeriodReport")
    @ResponseBody
    public Map<String, Object> genNextPeriodReport(HttpServletRequest request,@RequestBody MerchantReportDefine MRD){
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
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportServerComponent.generatorReportFile(pMap,merchantReportDefine);
        return ret;
    }
    /*
    * 保存报表模板文件设置
    * */
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
    /*
    * 保存报表分割字段设置
    */

    @RequestMapping("/saveReportDefineCutSetting")
    @ResponseBody
    public Map<String, Object> saveReportDefineCutSetting(HttpServletRequest request,
                                                  @RequestBody MerchantReportDefine MRD){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        if (MRD==null || MRD.getReportCode()==null || StringUtils.isEmpty(MRD.getReportCode())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(报表代码错误)");
            return ret;
        }
        if (StringUtils.isEmpty(MRD.getCutKeyField())||StringUtils.isEmpty(MRD.getCutNameField())){
            BaseNnte.setRetFalse(ret,1002,"参数错误(分割字段未指定)");
            return ret;
        }
        Map<String,Object> pMap=new HashMap<>();
        BaseNnte.setParamMapDataEnv(request,pMap);
        ret=autoReportQueryComponent.saveReportDefineCutSetting(pMap,MRD);
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
    /*
    * 打开特定账期的报表前取得相关的参数
    * */
    @RequestMapping("/priOpenPeroidReport")
    @ResponseBody
    public Map<String, Object> priOpenPeroidReport(HttpServletRequest request,
                                                   @RequestBody Map<String,Object> paramMap){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        MerchantReportRec queryDto=new MerchantReportRec();
        try {
            Map<String,Object> pMap=new HashMap<>();
            BaseNnte.setParamMapDataEnv(request,pMap);
            MapUtil.copyFromSrcMap(paramMap, queryDto);
            Integer off=NumberUtil.getDefaultInteger(paramMap.get("off"));
            ret=autoReportServerComponent.priOpenPeroidReport(pMap,queryDto,off);
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,1002,e.getMessage());
        }
        return ret;
    }
    @RequestMapping("/previewReport")
    public ResponseEntity<byte[]> previewReport(HttpServletRequest request) {
        // 转换并返回结果
        Map<String,Object> map=new HashMap<>();
        Object reportRecId=request.getParameter("reportRecId");
        BaseNnte.setParamMapDataEnv(request,map);
        map.put("id",NumberUtil.getDefaultLong(reportRecId.toString()));
        Map<String,Object> pathMap=autoReportRecComponent.getReportRecPriviewBytes(map);
        if (!BaseNnte.getRetSuc(pathMap)){
            return null;
        }
        try {
            //byte[] pdfFileBytes = FileUtil.getContent(StringUtils.defaultString(pathMap.get("priviewFileName")));
            byte[] pdfFileBytes = (byte[])pathMap.get("priviewContent");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf("application/pdf"));
            httpHeaders.setContentLength(pdfFileBytes.length);
            httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            return new ResponseEntity<>(pdfFileBytes, httpHeaders, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getDownloadUrl(MerchantReportRec mrr){
        String p1= StringUtils.pathAppend(kingReportConfig.getStaticRoot(),kingReportConfig.getReportRoot());
        String p2= StringUtils.pathAppend(p1,kingReportConfig.getReportFileRoot());
        String p3= StringUtils.pathAppend(p2,mrr.getParMerchantId().toString());
        String p4= StringUtils.pathAppend(p3,mrr.getReportCode());
        String p5= StringUtils.pathAppend(p4,mrr.getPeriodNo().toString());
        return StringUtils.pathAppend(p5,mrr.getReportName());
    }

    @RequestMapping("/downloadReportFile")
    @ResponseBody
    public Map<String, Object> downloadReportFile(HttpServletRequest request,
                                                  @RequestBody Map<String,Object> paramMap){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        MerchantReportRec queryDto=new MerchantReportRec();
        try {
            Map<String,Object> pMap=new HashMap<>();
            BaseNnte.setParamMapDataEnv(request,pMap);
            MapUtil.copyFromSrcMap(paramMap, queryDto);
            Map<String,Object> queryMap =autoReportServerComponent.priOpenPeroidReport(pMap,queryDto,0);
            if (!BaseNnte.getRetSuc(queryMap))
                return queryMap;
            MerchantReportRec mrr=(MerchantReportRec)queryMap.get("merchantReportRec");
            String downloadFileUrl=getDownloadUrl(mrr);
            if ("pdf".equalsIgnoreCase(StringUtils.defaultString(paramMap.get("fileType")))){
                downloadFileUrl=FileUtil.changeFileNameExten(downloadFileUrl,"pdf");
            }
            ret.put("downloadFileUrl",downloadFileUrl);
            BaseNnte.setRetTrue(ret,"取得下载文件成功");
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,1002,e.getMessage());
        }
        return ret;
    }
}
