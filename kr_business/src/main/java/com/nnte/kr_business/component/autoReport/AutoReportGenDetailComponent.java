package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.entity.autoReport.ReportControl;
import com.nnte.kr_business.mapper.workdb.base.operator.BaseMerchantOperator;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetail;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@WorkDBAspect
public class AutoReportGenDetailComponent {
    @Autowired
    private MerchantReportGendetailService merchantReportGendetailService;

    //------------------------------------------
    //返回报表控制中的报表开始时间
    public static Date getReportStartTimeFromReportControl(ReportControl rc){
        if (rc==null || rc.getReportDataEnv()==null)
            return null;
        return (Date)rc.getReportDataEnv().get(AutoReportQueryComponent.ResKeyWord.START_TIME);
    }
    //返回报表控制中的报表结束时间
    public static Date getReportEndTimeFromReportControl(ReportControl rc){
        if (rc==null || rc.getReportDataEnv()==null)
            return null;
        return (Date)rc.getReportDataEnv().get(AutoReportQueryComponent.ResKeyWord.END_TIME);
    }
    //返回报表控制中的报表的期数
    public static Integer getReportPeriodNoFromReportControl(ReportControl rc){
        if (rc==null || rc.getReportDataEnv()==null)
            return null;
        return NumberUtil.getDefaultInteger(rc.getReportDataEnv().get(AutoReportQueryComponent.ResKeyWord.PERIOD_NO));
    }
    /*
    * 保存报表产生明细
    * */
    public Map<String,Object> saveReportGenDetail(ConnSqlSessionFactory cssf,BaseMerchantOperator operator, ReportControl rc,
                                                  String outfn,String outpfn,Date createStartTime){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        MerchantReportGendetail gendetail=new MerchantReportGendetail();
        gendetail.setParMerchantId(rc.getReportDefine().getParMerchantId());
        gendetail.setReportCode(rc.getReportDefine().getReportCode());
        gendetail.setReportBusiType(rc.getReportDefine().getReportBusiType());
        gendetail.setCutValue(StringUtils.defaultString(
                rc.getReportDataEnv().get(AutoReportQueryComponent.ResKeyWord.CUT_KEY)));
        gendetail.setCutName(StringUtils.defaultString(
                rc.getReportDataEnv().get(AutoReportQueryComponent.ResKeyWord.CUT_NAME)));
        gendetail.setPeriodNo(getReportPeriodNoFromReportControl(rc));
        gendetail.setReportPeriod(rc.getReportDefine().getReportPeriod());
        gendetail.setStartTime(getReportStartTimeFromReportControl(rc));
        gendetail.setEndTime(getReportEndTimeFromReportControl(rc));
        gendetail.setReportName(outfn);
        gendetail.setReportFileName(outpfn);
        gendetail.setCreateStartTime(createStartTime);
        gendetail.setCreateEndTime(new Date());
        gendetail.setCreateOperId(operator.getId());
        MerchantReportGendetail saveDto=merchantReportGendetailService.save(cssf,gendetail,false);
        if (saveDto==null||saveDto.getId()==null||saveDto.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"生成报表产生明细记录时错误");
            return ret;
        }
        ret.put("gendetail",gendetail);
        BaseNnte.setRetTrue(ret,"生成报表产生明细记录成功");
        return ret;
    }
}
