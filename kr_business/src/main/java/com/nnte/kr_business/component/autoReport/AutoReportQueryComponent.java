package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DBSchemaInterface;
import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DBSchemaColum;
import com.nnte.framework.entity.DataColDef;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.entity.ObjKeyValue;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.entity.autoReport.ReportControl;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefine;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQueryService;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefineService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportQueryComponent extends BaseComponent {
    @Autowired
    private AutoReportComponent autoReportComponent;
    @Autowired
    private MerchantReportDefineService merchantReportDefineService;
    @Autowired
    private AutoReportDBConnComponent autoReportDBConnComponent;
    @Autowired
    private MerchantReportQueryService merchantReportQueryService;
    //组件数据字典区域
    //------------------------------------------
    @DataLibItem("报表查询类型")
    public final static List<KeyValue> LibReportQueryType = new ArrayList<>();
    static {//S:单行查询，M：多行查询
        LibReportQueryType.add(new KeyValue("S","单行查询"));
        LibReportQueryType.add(new KeyValue("M","多行查询"));
    }
    @DataLibItem("报表分割标志")
    public final static List<KeyValue> LibReportQueryCutFlag = new ArrayList<>();
    static {//S:单行查询，M：多行查询
        LibReportQueryCutFlag.add(new KeyValue("0","否"));
        LibReportQueryCutFlag.add(new KeyValue("1","是"));
    }
    public static class ResKeyWord{
        public static final String START_TIME="_startTime";
        public static final String END_TIME="_endTime";
        public static final String PERIOD_NO="_periodNo";
        public static final String CUT_KEY="_cutKey";
        public static final String CUT_NAME="_cutName";
    }
    @DataLibItem("查询保留关键字(Reserved keywords)")
    public final static List<KeyValue> LibQueryResKeyWord = new ArrayList<>();
    static {//S:单行查询，M：多行查询
        LibQueryResKeyWord.add(new KeyValue(ResKeyWord.START_TIME,"报表开始时间"));
        LibQueryResKeyWord.add(new KeyValue(ResKeyWord.END_TIME,"报表结束时间"));
        LibQueryResKeyWord.add(new KeyValue(ResKeyWord.PERIOD_NO,"报表期数"));
        LibQueryResKeyWord.add(new KeyValue(ResKeyWord.CUT_KEY,"报表分割字段值"));
        LibQueryResKeyWord.add(new KeyValue(ResKeyWord.CUT_NAME,"报表分割字段名"));
    }
    //------------------------------------------
    public MerchantReportQuery getReportQueryDefineById(Long queryId){
        return merchantReportQueryService.findModelByKey(queryId);
    }

    //通过CODE查找报表查询记录
    public MerchantReportQuery getReportQueryByCode(ConnSqlSessionFactory cssf, Long merchantId, String code) {
        MerchantReportQuery dto = new MerchantReportQuery();
        dto.setParMerchantId(merchantId);
        dto.setQueryCode(code);
        List<MerchantReportQuery> list;
        if (cssf!=null)
            list=merchantReportQueryService.findModelList(cssf, dto);
        else
            list=merchantReportQueryService.findModelList(dto);
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }
    public List<MerchantReportQuery> queryMerchantCutQuerys(Long parMerchantId,ConnSqlSessionFactory cssf){
        MerchantReportQuery dto = new MerchantReportQuery();
        dto.setParMerchantId(parMerchantId);
        dto.setCutFlag("1");
        List<MerchantReportQuery> list = null;
        if (cssf==null)
            list=merchantReportQueryService.findModelList(dto);
        else
            list=merchantReportQueryService.findModelList(cssf, dto);
        return list;
    }
    //获取报表的数据查询定义（不包含分割查询）
    public List<MerchantReportQuery> queryReportDataQuerys(Long parMerchantId,MerchantReportDefine mrd,
                                                           ConnSqlSessionFactory cssf){
        MerchantReportQuery dto = new MerchantReportQuery();
        dto.setParMerchantId(parMerchantId);
        dto.setReportId(mrd.getId());
        dto.setCutFlag("0");
        List<MerchantReportQuery> list = null;
        if (cssf==null)
            list=merchantReportQueryService.findModelList(dto);
        else
            list=merchantReportQueryService.findModelList(cssf, dto);
        return list;
    }

    //查询商户的分割查询列表，以KEY-VALUE形式返回
    public List<KeyValue> getMerchantCutFlagQuerys(Long parMerchantId,ConnSqlSessionFactory cssf){
        List<MerchantReportQuery> list = queryMerchantCutQuerys(parMerchantId,cssf);
        List<KeyValue> ret = new ArrayList<>();
        ret.add(new KeyValue("","不分割"));
        if (list != null && list.size() > 0)
        {
            for(MerchantReportQuery mrq:list){
                ret.add(new KeyValue(mrq.getQueryCode(),mrq.getCutTypeName()));
            }
            return ret;
        }
        return ret;
    }

    @DBSrcTranc
    public Map<String, Object> exportMerchantReporQuerys(Map<String, Object> paramMap) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantReportQuery> list=merchantReportQueryService.queryReportQueryList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"没有可导出数据");
            return ret;
        }
        //准备输出列定义
        //---------------------------------
        List<ExpotColDef> colDefList=new ArrayList<>();
        colDefList.add(new ExpotColDef("queryCode","查询代码",null,"",""));
        colDefList.add(new ExpotColDef("queryName","查询名称",null,"",""));
        colDefList.add(new ExpotColDef("queryType","查询类型", LibReportQueryType,"",""));
        colDefList.add(new ExpotColDef("reportId","报表名称", autoReportComponent.getUsedReportsByMerchantKV(loginMerchant.getParMerchantId()),"",""));
        colDefList.add(new ExpotColDef("connId","连接名称",autoReportDBConnComponent.getUsedReportDBConnsKV(loginMerchant.getParMerchantId()),"",""));
        colDefList.add(new ExpotColDef("maxRowCount","最大行数", null,"",""));
        //---------------------------------
        ret=DataExport(loginMerchant.getParMerchantId(),list,colDefList,
                sysParamComponent.getMerchantExportSheetCount(loginMerchant.getParMerchantId()));
        return ret;
    }

    @DBSrcTranc
    public Map<String, Object> loadMerchantReportQuerys(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantReportQuery> list=merchantReportQueryService.queryReportQueryList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"未取得查询定义列表数据");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"取得报表查询列表成功");
        ret.put("list",list);
        ret.put("count",paramMap.get("count"));
        return ret;
    }

    //保存报表查询定义
    @DBSrcTranc
    public Map<String, Object> saveReportQuery(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator loginMerchantOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");

        String queryCode= StringUtils.defaultString(paramMap.get("queryCode"));
        MerchantReportQuery dto = getReportQueryByCode(cssf,loginMerchant.getParMerchantId(),queryCode);
        if (dto == null) {
            //如果没有查到记录，则新增一条
            dto = new MerchantReportQuery();
            dto.setParMerchantId(loginMerchant.getParMerchantId());
            dto.setQueryCode(queryCode);
            dto.setQueryName(StringUtils.defaultString(paramMap.get("queryName")));
            dto.setQueryType(StringUtils.defaultString(paramMap.get("queryType")));
            dto.setReportId(NumberUtil.getDefaultLong(paramMap.get("reportId")));
            dto.setConnId(NumberUtil.getDefaultLong(paramMap.get("connId")));
            dto.setMaxRowCount(NumberUtil.getDefaultInteger(paramMap.get("maxRowCount")));
            dto.setCutFlag(StringUtils.defaultString(paramMap.get("cutFlag")));
            dto.setCutTypeName(StringUtils.defaultString(paramMap.get("cutTypeName")));
            //-----------------------------------------------------------------------
            dto.setCreateTime(new Date());
            dto.setCreateOperId(loginMerchantOperator.getId());
            dto.setCreateOperName(loginMerchantOperator.getOpeName());
            dto.setUpdateTime(dto.getCreateTime());
            dto = merchantReportQueryService.save(cssf,dto,false);
        }else{//如果有，则更改记录
            dto.setQueryName(StringUtils.defaultString(paramMap.get("queryName")));
            dto.setQueryType(StringUtils.defaultString(paramMap.get("queryType")));
            dto.setReportId(NumberUtil.getDefaultLong(paramMap.get("reportId")));
            dto.setConnId(NumberUtil.getDefaultLong(paramMap.get("connId")));
            dto.setMaxRowCount(NumberUtil.getDefaultInteger(paramMap.get("maxRowCount")));
            dto.setCutFlag(StringUtils.defaultString(paramMap.get("cutFlag")));
            dto.setCutTypeName(StringUtils.defaultString(paramMap.get("cutTypeName")));
            dto.setUpdateTime(new Date());
            dto = merchantReportQueryService.save(cssf,dto,false);
        }
        if (dto==null||dto.getId()==null||dto.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"报表查询保存错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"报表查询保存成功");
        return ret;
    }
    //取得分割查询字段的类型：Integer,Long,String,Date,Float,Double(?)
    public String getCutFieldType(MerchantReportQuery cutQuery,String fieldName){
        JSONArray jarray=JsonUtil.getJsonArray4Json(cutQuery.getQuerySqlCols());
        if (jarray!=null){
            for(int i=0;i<jarray.size();i++){
                JSONObject jobj=jarray.getJSONObject(i);
                if (jobj!=null && jobj.get("colName").equals(fieldName)){
                    Object dataType=jobj.get("dataType");
                    if (dataType!=null)
                        return dataType.toString();
                    break;
                }
            }
        }
        return null;
    }
    //返回取得查询结构时分割字段内容替换
    //fieldType:Integer,Long,Float,Double,String,Date
    private String getSchemaCutReplaceContent(String repFormat,String cutFieldType){
        if ("Integer".equals(cutFieldType)||"Long".equals(cutFieldType))
            return getReplaceContentByFormat(repFormat,new Integer(0));
        else if ("Date".equals(cutFieldType))
            return getReplaceContentByFormat(repFormat,new Date());
        else if ("Float".equals(cutFieldType)||"Double".equals(cutFieldType))
            return getReplaceContentByFormat(repFormat,new Double(0.0));
        return getReplaceContentByFormat(null,"");
    }

    /*
    * 对查询语句的内容进行替换，以rc为主要区分标志，当rc不为null时，表示是为获取数据而进行
    * sql语句替换，此时query和cssf无效；如果rc==null,表示是为获取查询结果的结构而进行替换，
    * 此时query和cssf有效
    * */
    public String replaceQuerySql(String srcSql,ReportControl rc,MerchantReportQuery query,
                                    ConnSqlSessionFactory cssf){
        String srcString = srcSql;
        String regex = "\\$\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(srcString);
        List<String> matchedList = new ArrayList<>();
        while (m.find()) {
            int i = 1;
            matchedList.add(m.group(i));
            i++;
        }
        for(String matchedStr:matchedList){
            String replaceStr="\\$\\{"+matchedStr+"\\}";
            if (rc==null)
                srcString=srcString.replaceFirst(replaceStr,getSchemaReplaceContent(matchedStr,query,cssf));
            else
                srcString=srcString.replaceFirst(replaceStr,getQueryDataReplaceMatche(matchedStr,rc));
        }
        return srcString;
    }

    /*
    * 保存查询体的设置
    * */
    @DBSrcTranc
    public Map<String, Object> saveQueryBodySet(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        String queryCode= StringUtils.defaultString(paramMap.get("queryCode"));
        MerchantReportQuery dto = getReportQueryByCode(cssf,loginMerchant.getParMerchantId(),queryCode);
        if (dto == null) {
            BaseNnte.setRetFalse(ret, 1002,"未找到指定的查询");
            return ret;
        }
        MerchantDbconnectDefine mdd=autoReportDBConnComponent.getReportDBConnDefineById(dto.getConnId());
        if (mdd==null || mdd.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"查询未指定数据连接");
            return ret;
        }
        //需要对查询的内容进行替换------
        String sSql=StringUtils.defaultString(paramMap.get("querySql"));
        String schemaSql=replaceQuerySql(sSql,null,dto,cssf);
        //-----------------------------
        List<DBSchemaColum> listCol=autoReportDBConnComponent.execSqlForSchema(mdd,schemaSql);
        if (listCol==null || listCol.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"查询未能取得数据结构");
            return ret;
        }
        MerchantReportQuery updateDto=new MerchantReportQuery();
        updateDto.setId(dto.getId());
        updateDto.setQuerySql(sSql);
        String cols= JsonUtil.getJsonString4JavaList(listCol,"");
        updateDto.setQuerySqlCols(cols);
        MerchantReportQuery retDto=merchantReportQueryService.save(cssf,updateDto,false);
        if (retDto==null){
            BaseNnte.setRetFalse(ret, 1002,"保存查询体信息失败");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询取得数据结构成功");
        ret.put("cols",listCol);
        return ret;
    }

    @DBSrcTranc
    public Map<String, Object> saveReportDefineCutSetting(Map<String, Object> paramMap,MerchantReportDefine MRD){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        MerchantReportDefine srcMRD = autoReportComponent.getReportRecordByCode(cssf,loginMerchant.getParMerchantId(), MRD.getReportCode());
        if (srcMRD==null){
            BaseNnte.setRetFalse(ret, 1002,"未取得报表定义");
            return ret;
        }
        MerchantReportDefine dto = new MerchantReportDefine();
        dto.setId(srcMRD.getId());
        dto.setParMerchantId(loginMerchant.getParMerchantId());
        if (StringUtils.isEmpty(srcMRD.getReportClass())){
            BaseNnte.setRetFalse(ret, 1002,"未取得报表分割查询定义");
            return ret;
        }
        MerchantReportQuery cutQuery=getReportQueryByCode(cssf,loginMerchant.getParMerchantId(),srcMRD.getReportClass());
        if (cutQuery==null){
            BaseNnte.setRetFalse(ret, 1002,"未查询到报表分割查询定义");
            return ret;
        }
        dto.setCutKeyField(MRD.getCutKeyField());
        dto.setCutNameField(MRD.getCutNameField());
        dto.setCutKeyType(getCutFieldType(cutQuery,MRD.getCutKeyField()));
        dto.setCutNameType(getCutFieldType(cutQuery,MRD.getCutNameField()));
        if (!merchantReportDefineService.updateModel(cssf,dto).equals(1)){
            BaseNnte.setRetFalse(ret, 1002,"设置报表分割字段失败");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"设置报表分割字段成功");
        return ret;
    }
    //时间内容格式转换
    public static String getReplaceDateContentByFormat(String format,Object valObj){
        Date dateObj = (Date) valObj;
        if (StringUtils.isNotEmpty(format))
            return DateUtils.dateToString(dateObj,format);
        return DateUtils.dateToString(dateObj,"yyyy-MM-dd");
    }
    //Integer内容格式转换
    public static String getReplaceIntegerContentByFormat(String format,Object valObj){
        Integer val=NumberUtil.getDefaultInteger(valObj);
        if (StringUtils.isNotEmpty(format))
            return String.format(format,val);
        return val.toString();
    }
    //Float内容格式转换
    public static String getReplaceFloatContentByFormat(String format,Object valObj){
        Double val=NumberUtil.getDefaultDouble(valObj);
        if (StringUtils.isNotEmpty(format))
            return String.format(format,val);
        return Double.valueOf(NumberUtil.getScaleValue4Money(val)).toString();
    }
    //获取替换的内容,按格式替换
    public static String getReplaceContentByFormat(String format,Object valObj){
        if (valObj==null)
            return "";
        if (DataColDef.getDataType(valObj.getClass().getTypeName()).equals(DataColDef.DataType.DATA_DATE))
            return getReplaceDateContentByFormat(format,valObj);
        else if (DataColDef.getDataType(valObj.getClass().getTypeName()).equals(DataColDef.DataType.DATA_INT))
            return getReplaceIntegerContentByFormat(format,valObj);
        else if (DataColDef.getDataType(valObj.getClass().getTypeName()).equals(DataColDef.DataType.DATA_FLOT))
            return getReplaceFloatContentByFormat(format,valObj);
        else
            return valObj.toString();
    }

    private String[] getMatcheStrings(String matchedStr){
        String mstr=matchedStr;
        mstr=mstr.trim();
        String repType=mstr;
        String repFormat=null;
        if (mstr.indexOf(",")>0){
            String[] splitStrs=mstr.split(",");
            if (splitStrs!=null && splitStrs.length==2){
                repType=splitStrs[0].trim();
                repFormat=splitStrs[1].trim();
            }
        }
        String[] ret=new String[2];
        ret[0]=repType;
        ret[1]=repFormat;
        return ret;
    }
    //获取用于取得查询结果集结构的环境变量替换的内容
    public String getSchemaReplaceContent(String matchedStr,MerchantReportQuery query,
                                                 ConnSqlSessionFactory cssf){
        String[] mStrs=getMatcheStrings(matchedStr);
        String repType=mStrs[0];
        String repFormat=mStrs[1];
        MerchantReportDefine mrd=autoReportComponent.getReportRecordById(query.getReportId());
        if (ResKeyWord.START_TIME.equals(repType)||ResKeyWord.END_TIME.equals(repType))
            return getReplaceContentByFormat(repFormat,new Date());
        else if (ResKeyWord.PERIOD_NO.equals(repType))
            return getReplaceContentByFormat(repFormat,new Integer(0));
        else if (ResKeyWord.CUT_KEY.equals(repType)){
            return getSchemaCutReplaceContent(repFormat,mrd.getCutKeyType());
        }else if (ResKeyWord.CUT_NAME.equals(repType)){
            return getSchemaCutReplaceContent(repFormat,mrd.getCutNameType());
        }
        return null;
    }

    //获取用于取得查询结果集结构的环境变量替换的内容
    public String getQueryDataReplaceMatche(String matchedStr, ReportControl rc){
        String[] mStrs=getMatcheStrings(matchedStr);
        String repType=mStrs[0];
        String repFormat=mStrs[1];
        return getReplaceContentByFormat(repFormat,rc.getReportDataEnv().get(repType));
    }
    //执行查询获取查询结果
    public Map<String,Object> execQuerySqlForContent(MerchantReportQuery query){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (query==null || query.getId()==null) {
            BaseNnte.setRetFalse(ret, 1002,"查询定义不合法");
            return ret;
        }
        if (query.getConnId()==null|| query.getConnId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"查询的数据连接不合法");
            return ret;
        }
        MerchantDbconnectDefine mdbd=autoReportDBConnComponent.getReportDBConnDefineById(query.getConnId());
        if (mdbd==null){
            BaseNnte.setRetFalse(ret, 1002,"未找到查询的数据连接");
            return ret;
        }
        DBSchemaInterface DBSI= DynamicDatabaseSourceHolder.getSchemaInterfaceByDBType(mdbd.getDbType());
        if (DBSI==null)
        {
            BaseNnte.setRetFalse(ret, 1002,"查询的数据连接类型不合法");
            return ret;
        }
        Connection conn=DBSI.connectNoPoolDataSource(mdbd.getDbIp(),mdbd.getDbPort(),mdbd.getDbSchema(),
                mdbd.getDbUser(),mdbd.getDbPassword());
        try {
            if (conn == null || conn.isClosed() == true) {
                BaseNnte.setRetFalse(ret, 1002,"不能正常连接查询的数据库");
                return ret;
            }
            String execSql=DBSI.getSqlIncludeLimit(query.getQuerySql(),0,query.getMaxRowCount());
            return DBSI.execSqlForContent(conn,execSql);
        }catch (Exception e){
            BaseNnte.setRetFalse(ret, 9999,"执行查询的SQL语句异常");
            return ret;
        }finally {
            DynamicDatabaseSourceHolder.CloseDBConnection(conn);
        }
    }
    /*
    * 取得报表分割查询结果
    * */
    public Map<String,Object> getReportCutQueryContent( ConnSqlSessionFactory cssf,MerchantReportDefine mrd){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        if (StringUtils.isNotEmpty(mrd.getReportClass())){
            MerchantReportQuery cutQuery=getReportQueryByCode(cssf,mrd.getParMerchantId(),mrd.getReportClass());
            if (cutQuery==null){
                BaseNnte.setRetFalse(ret, 1002,"报表未找到有效的分割查询");
                return ret;
            }
            //执行查询，按KEY-NAME取得查询结果
            Map<String,Object> queryRet=execQuerySqlForContent(cutQuery);
            if (!BaseNnte.getRetSuc(queryRet))
                return queryRet;
            if (NumberUtil.getDefaultInteger(queryRet.get("count"))<=0){
                BaseNnte.setRetFalse(ret, 1002,"报表分割查询没有数据，不能生成报表");
                return ret;
            }
            List<JSONObject> cutList=(List<JSONObject>)queryRet.get("rows");
            List<ObjKeyValue> cutContentList=new ArrayList<>();
            for(JSONObject jobj:cutList){
                ObjKeyValue kv=new ObjKeyValue(jobj.get(mrd.getCutKeyField()),jobj.get(mrd.getCutNameField()));
                cutContentList.add(kv);
            }
            ret.put("cutQuery",cutQuery);
            ret.put("cutContentList",cutContentList);
        }else{
            BaseNnte.setRetFalse(ret, 1002,"报表没有定义分割查询");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"查询报表分割内容成功");
        return ret;
    }
}
