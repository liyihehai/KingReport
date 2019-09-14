package com.nnte.kr_business.mapper.workdb.merchant.report;

import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.kr_business.base.BaseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MerchantReportDefineService extends BaseService<MerchantReportDefineDao,MerchantReportDefine> {
    public MerchantReportDefineService(){
        super(MerchantReportDefineDao.class);
    }

    //按条件查询用于加载的商户报表定义列表
    public List<MerchantReportDefine> queryModelList(ConnSqlSessionFactory cssf, Map<String,Object> paramMap){
        try (connDaoSession cds=getConnDaoSession(cssf.getSsf(),cssf.getConn())){
            Integer count=cds.getMapper().queryModelListWithPgCount(paramMap);
            if (count!=null && count>0){
                paramMap.put("count",count);
                return cds.getMapper().queryModelListWithPg(paramMap);
            }
            else
                paramMap.put("count",0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

