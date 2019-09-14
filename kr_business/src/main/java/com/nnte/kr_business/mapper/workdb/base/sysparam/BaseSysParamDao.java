package com.nnte.kr_business.mapper.workdb.base.sysparam;


import com.nnte.framework.base.BaseDao;

import java.util.List;
import java.util.Map;

public interface BaseSysParamDao extends BaseDao {
    Integer queryReportParamBusiTypesCount(Map<String,Object> paramMap);
    List<BaseSysParam> queryReportParamBusiTypes(Map<String,Object> paramMap);
}
