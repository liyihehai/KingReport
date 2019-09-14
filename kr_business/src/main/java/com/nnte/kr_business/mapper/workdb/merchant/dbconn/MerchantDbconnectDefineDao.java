package com.nnte.kr_business.mapper.workdb.merchant.dbconn;

import com.nnte.framework.base.BaseDao;

import java.util.List;
import java.util.Map;

public interface MerchantDbconnectDefineDao extends BaseDao {
    Integer queryDbconnectListCount(Map<String,Object> paramMap);
    List<MerchantDbconnectDefine> queryDbconnectList(Map<String,Object> paramMap);
}
