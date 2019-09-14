package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DBSchemaInterface;
import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DBSchemaColum;
import com.nnte.framework.entity.ExpotColDef;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
import com.nnte.kr_business.base.BaseComponent;
import com.nnte.kr_business.base.DynamicDatabaseSourceHolder;
import com.nnte.kr_business.component.base.KingReportComponent;
import com.nnte.kr_business.mapper.workdb.base.merchant.BaseMerchant;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefine;
import com.nnte.kr_business.mapper.workdb.merchant.dbconn.MerchantDbconnectDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportDBConnComponent extends BaseComponent {
    @Autowired
    private MerchantDbconnectDefineService merchantDbconnectDefineService;
    //组件数据字典区域
    //------------------------------------------
    public static class DBConnState{
        public final static Integer UN_USER=0;
        public final static Integer USED=1;
        public final static Integer PAUSE=2;
        public final static Integer DELETED=9;
    };
    @DataLibItem("报表连接状态")
    public final static List<KeyValue> LibReportDBConnState = new ArrayList<>();
    static {//0：未启用，1：启用：2：删除
        LibReportDBConnState.add(new KeyValue(DBConnState.UN_USER.toString(),"未启用"));
        LibReportDBConnState.add(new KeyValue(DBConnState.USED.toString(),"启用"));
        LibReportDBConnState.add(new KeyValue(DBConnState.PAUSE.toString(),"暂停"));
        LibReportDBConnState.add(new KeyValue(DBConnState.DELETED.toString(),"删除"));
    }

    public List<KeyValue> getLibDBConnType(){
        String[] dbTyps= DynamicDatabaseSourceHolder.queryDBTypes();
        if (dbTyps!=null && dbTyps.length>0){
            List<KeyValue> ret = new ArrayList<>();
            for(int i=0;i<dbTyps.length;i++)
                ret.add(new KeyValue(dbTyps[i],dbTyps[i]));
            return ret;
        }
        return null;
    }

    public List<MerchantDbconnectDefine> getUsedReportDBConns(Long parMerchantId){
        if (parMerchantId==null || parMerchantId<=0)
            return null;
        MerchantDbconnectDefine dto = new MerchantDbconnectDefine();
        dto.setParMerchantId(parMerchantId);
        dto.setConnState(DBConnState.USED);
        return merchantDbconnectDefineService.findModelList(dto);
    }

    public List<KeyValue> getUsedReportDBConnsKV(Long parMerchantId) {
        List<MerchantDbconnectDefine> list=getUsedReportDBConns(parMerchantId);
        if (list==null || list.size()<=0)
            return null;
        List<KeyValue> ret =new ArrayList<>();
        for(MerchantDbconnectDefine MDD:list){
            ret.add(new KeyValue(MDD.getId().toString(),MDD.getConnName()));
        }
        return ret;
    }
    //------------------------------------------
    @DBSrcTranc
    public MerchantDbconnectDefine getReportDBConnDefineById(Long id){
        return merchantDbconnectDefineService.findModelByKey(id);
    }
    //通过CODE查找报表记录
    public MerchantDbconnectDefine getReportDBConnByCode(ConnSqlSessionFactory cssf, Long merchantId, String code) {
        MerchantDbconnectDefine dto = new MerchantDbconnectDefine();
        dto.setParMerchantId(merchantId);
        dto.setConnCode(code);
        List<MerchantDbconnectDefine> list = merchantDbconnectDefineService.findModelList(cssf, dto);
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }
    //更改指定的商户报表的状态
    @DBSrcTranc
    public Map<String, Object> updateDBConnState(Map<String, Object> paramMap,Long id,int newState){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant= KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        MerchantDbconnectDefine dto = getReportDBConnDefineById(id);
        if (dto == null) {
            BaseNnte.setRetFalse(ret, 1002,"未取得连接定义");
            return ret;
        }
        if (!loginMerchant.getParMerchantId().equals(dto.getParMerchantId())){
            BaseNnte.setRetFalse(ret, 1002,"非该商户连接");
            return ret;
        }
        MerchantDbconnectDefine updateDto=new MerchantDbconnectDefine();
        updateDto.setId(dto.getId());
        updateDto.setConnState(newState);
        if (!merchantDbconnectDefineService.updateModel(cssf,updateDto).equals(1)){
            BaseNnte.setRetFalse(ret, 1002,"更改商户连接状态失败");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"更改商户连接状态成功");
        return ret;
    }
    //根据条件查询商户报表定义列表
    @DBSrcTranc
    public Map<String, Object> loadMerchantReportConnDefs(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantDbconnectDefine> list=merchantDbconnectDefineService.queryDbconnectList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"未取得连接定义列表数据");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"取得报表定义列表成功");
        ret.put("list",list);
        ret.put("count",paramMap.get("count"));
        return ret;
    }
    //导出商户报表定义到Excel中
    @DBSrcTranc
    public Map<String, Object> exportMerchantReportConnDefs(Map<String, Object> paramMap) {
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        //--------------------------------
        paramMap.put("parMerchantId",loginMerchant.getParMerchantId());
        List<MerchantDbconnectDefine> list=merchantDbconnectDefineService.queryDbconnectList(cssf,paramMap);
        if (list==null || list.size()<=0){
            BaseNnte.setRetFalse(ret, 1002,"没有可导出数据");
            return ret;
        }
        //准备输出列定义
        //---------------------------------
        List<ExpotColDef> colDefList=new ArrayList<>();
        colDefList.add(new ExpotColDef("connCode","连接代码",null,"",""));
        colDefList.add(new ExpotColDef("connName","连接名称",null,"",""));
        colDefList.add(new ExpotColDef("dbType","数据库类型", getLibDBConnType(),"",""));
        colDefList.add(new ExpotColDef("dbIp","数据库IP", null,"",""));
        colDefList.add(new ExpotColDef("dbPort","端口号",null,"",""));
        colDefList.add(new ExpotColDef("dbSchema","数据库", null,"",""));
        colDefList.add(new ExpotColDef("connState","连接状态", LibReportDBConnState,"",""));
        //---------------------------------
        ret=DataExport(loginMerchant.getParMerchantId(),list,colDefList,
                sysParamComponent.getMerchantExportSheetCount(loginMerchant.getParMerchantId()));
        return ret;
    }

    //保存连接定义
    @DBSrcTranc
    public Map<String, Object> saveReportDBconn(Map<String, Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        BaseMerchant loginMerchant=KingReportComponent.getLoginMerchantFromParamMap(paramMap);
        BaseMerchantOperator loginMerchantOperator=KingReportComponent.getBaseMerchantOperatorFromParamMap(paramMap);
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");

        String connCode= StringUtils.defaultString(paramMap.get("connCode"));
        MerchantDbconnectDefine dto = getReportDBConnByCode(cssf,loginMerchant.getParMerchantId(),connCode);
        if (dto == null) {
            //如果没有查到记录，则新增一条
            dto = new MerchantDbconnectDefine();
            dto.setParMerchantId(loginMerchant.getParMerchantId());
            dto.setConnCode(connCode);
            dto.setConnName(StringUtils.defaultString(paramMap.get("connName")));
            dto.setDbType(StringUtils.defaultString(paramMap.get("dbType")));
            dto.setDbIp(StringUtils.defaultString(paramMap.get("dbIp")));
            dto.setDbPort(NumberUtil.getDefaultLong(paramMap.get("dbPort")));
            dto.setDbSchema(StringUtils.defaultString(paramMap.get("dbSchema")));
            dto.setDbUser(StringUtils.defaultString(paramMap.get("dbUser")));
            dto.setDbPassword(StringUtils.defaultString(paramMap.get("dbPassword")));
            dto.setConnState(0);
            dto.setCreateTime(new Date());
            dto.setCreateOperId(loginMerchantOperator.getId());
            dto.setCreateOperName(loginMerchantOperator.getOpeName());
            dto.setUpdateTime(dto.getCreateTime());
            if (!testMechantDataSource(dto)){
                BaseNnte.setRetFalse(ret, 1002,"数据源不能正常连接");
                return ret;
            }
            dto = merchantDbconnectDefineService.save(cssf,dto,false);
        }else{//如果有，则更改记录
            dto.setConnName(StringUtils.defaultString(paramMap.get("connName")));
            dto.setDbType(StringUtils.defaultString(paramMap.get("dbType")));
            dto.setDbIp(StringUtils.defaultString(paramMap.get("dbIp")));
            dto.setDbPort(NumberUtil.getDefaultLong(paramMap.get("dbPort")));
            dto.setDbSchema(StringUtils.defaultString(paramMap.get("dbSchema")));
            dto.setDbUser(StringUtils.defaultString(paramMap.get("dbUser")));
            dto.setDbPassword(StringUtils.defaultString(paramMap.get("dbPassword")));
            dto.setUpdateTime(new Date());
            if (!testMechantDataSource(dto)){
                BaseNnte.setRetFalse(ret, 1002,"数据源不能正常连接");
                return ret;
            }
            dto = merchantDbconnectDefineService.save(cssf,dto,false);
        }
        if (dto==null||dto.getId()==null||dto.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"报表保存错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"报表保存成功");
        return ret;
    }

    //连接一个非连接池的数据源(注意该连接一定要关闭)
    public Connection connectNoPoolDataSource(MerchantDbconnectDefine mdbd){
        if (mdbd==null || mdbd.getDbType()==null || mdbd.getDbSchema()==null)
            return null;
        DBSchemaInterface DBSI=DynamicDatabaseSourceHolder.getSchemaInterfaceByDBType(mdbd.getDbType());
        return DBSI.connectNoPoolDataSource(mdbd.getDbIp(),mdbd.getDbPort(),mdbd.getDbSchema(),
                mdbd.getDbUser(),mdbd.getDbPassword());
    }
    //测试数据源是否能正常连接
    public boolean testMechantDataSource(MerchantDbconnectDefine mdbd){
        Connection conn=connectNoPoolDataSource(mdbd);
        try {
            if (conn == null || conn.isClosed() == true)
                return false;
        }catch (Exception e){
            return false;
        }finally {
            DynamicDatabaseSourceHolder.CloseDBConnection(conn);
        }
        return true;
    }

    public List<DBSchemaColum> execSqlForSchema(MerchantDbconnectDefine mdbd, String sSql){
        if (mdbd==null || mdbd.getDbType()==null || mdbd.getDbSchema()==null)
            return null;
        DBSchemaInterface DBSI=DynamicDatabaseSourceHolder.getSchemaInterfaceByDBType(mdbd.getDbType());
        if (DBSI==null)
            return null;
        Connection conn=DBSI.connectNoPoolDataSource(mdbd.getDbIp(),mdbd.getDbPort(),mdbd.getDbSchema(),
                mdbd.getDbUser(),mdbd.getDbPassword());
        try {
            if (conn == null || conn.isClosed() == true)
                return null;
            return DBSI.execSqlForSchema(conn,sSql);
        }catch (Exception e){
            return null;
        }finally {
            DynamicDatabaseSourceHolder.CloseDBConnection(conn);
        }
    }
}
