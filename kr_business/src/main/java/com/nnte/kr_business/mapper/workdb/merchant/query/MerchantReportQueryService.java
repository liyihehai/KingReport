package com.nnte.kr_business.mapper.workdb.merchant.query;

import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.kr_business.base.BaseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MerchantReportQueryService extends BaseService<MerchantReportQueryDao,MerchantReportQuery> {
    public MerchantReportQueryService(){
        super(MerchantReportQueryDao.class);
    }

    //按条件查询用于加载的商户报表查询定义列表
    public List<MerchantReportQuery> queryReportQueryList(ConnSqlSessionFactory cssf, Map<String,Object> paramMap){
        try (connDaoSession cds=getConnDaoSession(cssf.getSsf(),cssf.getConn())){
            Integer count=cds.getMapper().queryReportQueryListCount(paramMap);
            if (count!=null && count>0){
                paramMap.put("count",count);
                return cds.getMapper().queryReportQueryList(paramMap);
            }
            else
                paramMap.put("count",0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

