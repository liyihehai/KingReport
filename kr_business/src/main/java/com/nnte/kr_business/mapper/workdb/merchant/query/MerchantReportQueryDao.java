package com.nnte.kr_business.mapper.workdb.merchant.query;

import com.nnte.framework.base.BaseDao;

import java.util.List;
import java.util.Map;

public interface MerchantReportQueryDao extends BaseDao {
    Integer queryReportQueryListCount(Map<String,Object> paramMap);
    List<MerchantReportQuery> queryReportQueryList(Map<String,Object> paramMap);
}
