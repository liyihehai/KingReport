package com.nnte.kr_business.mapper.workdb.merchant.report;


import com.nnte.framework.base.BaseDao;

import java.util.List;
import java.util.Map;

public interface MerchantReportDefineDao extends BaseDao {
    Integer queryModelListWithPgCount(Map<String,Object> paramMap);
    List<MerchantReportDefine> queryModelListWithPg(Map<String,Object> paramMap);
}
