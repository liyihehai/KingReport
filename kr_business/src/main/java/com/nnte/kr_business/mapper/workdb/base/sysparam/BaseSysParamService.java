package com.nnte.kr_business.mapper.workdb.base.sysparam;

import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.kr_business.base.BaseService;
import com.nnte.kr_business.entity.autoReport.ReportBusiType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BaseSysParamService extends BaseService<BaseSysParamDao, BaseSysParam> {
    public BaseSysParamService(){
        super(BaseSysParamDao.class);
    }

    //按条件查询用于加载的商户报表业务类型定义定义列表
    public List<ReportBusiType> queryReportParamBusiTypesList(ConnSqlSessionFactory cssf, Map<String,Object> paramMap){
        try (connDaoSession cds=getConnDaoSession(cssf.getSsf(),cssf.getConn())){
            Integer count=cds.getMapper().queryReportParamBusiTypesCount(paramMap);
            if (count!=null && count>0){
                paramMap.put("count",count);
                List<BaseSysParam> list = cds.getMapper().queryReportParamBusiTypes(paramMap);
                if (list!=null && list.size()>0){
                    List<ReportBusiType> retList = new ArrayList<>();
                    for(BaseSysParam bsp:list) {
                        ReportBusiType rbt = new ReportBusiType();
                        rbt.setParMerchantId(bsp.getParMerchantId());
                        rbt.setBusiTypeCode(bsp.getPkey());
                        rbt.setBusiTypeName(bsp.getValue1());
                        rbt.setBusiTypeState(bsp.getParamState());
                        retList.add(rbt);
                    }
                    return retList;
                }
            }
            else
                paramMap.put("count",0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

