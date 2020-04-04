package com.nnte.kr_business.mapper.workdb.merchant.dbconn;

import com.nnte.framework.base.BaseService;
import com.nnte.framework.base.ConnSqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MerchantDbconnectDefineService extends BaseService<MerchantDbconnectDefineDao,MerchantDbconnectDefine> {
    public MerchantDbconnectDefineService(){
        super(MerchantDbconnectDefineDao.class);
    }


    //按条件查询用于加载的商户报表定义列表
    public List<MerchantDbconnectDefine> queryDbconnectList(ConnSqlSessionFactory cssf, Map<String,Object> paramMap){
        try (connDaoSession cds=getConnDaoSession(cssf.getSsf(),cssf.getConn())){
            Integer count=cds.getMapper().queryDbconnectListCount(paramMap);
            if (count!=null && count>0){
                paramMap.put("count",count);
                return cds.getMapper().queryDbconnectList(paramMap);
            }
            else
                paramMap.put("count",0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

