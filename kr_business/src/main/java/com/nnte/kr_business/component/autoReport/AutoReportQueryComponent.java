package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DBSchemaColum;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefine;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQuery;
import com.nnte.kr_business.mapper.workdb.merchant.query.MerchantReportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportQueryComponent extends BaseComponent {
    @Autowired
    private AutoReportComponent autoReportComponent;
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
    //查询商户的分割查询列表，以KEY-VALUE形式返回
    public List<KeyValue> getMerchantCutFlagQuerys(Long parMerchantId,ConnSqlSessionFactory cssf){
        MerchantReportQuery dto = new MerchantReportQuery();
        dto.setParMerchantId(parMerchantId);
        dto.setCutFlag("1");
        List<MerchantReportQuery> list = null;
        if (cssf==null)
            list=merchantReportQueryService.findModelList(dto);
        else
            list=merchantReportQueryService.findModelList(cssf, dto);
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
            dto.setMaxRowCount(NumberUtil.getDefaultLong(paramMap.get("maxRowCount")));
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
            dto.setMaxRowCount(NumberUtil.getDefaultLong(paramMap.get("maxRowCount")));
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
        String sSql=StringUtils.defaultString(paramMap.get("querySql"));
        List<DBSchemaColum> listCol=autoReportDBConnComponent.execSqlForSchema(mdd,sSql);
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
}
