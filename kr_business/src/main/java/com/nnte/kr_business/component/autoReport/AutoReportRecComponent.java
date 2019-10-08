package com.nnte.kr_business.component.autoReport;

import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.mapper.workdb.merchant.gendetail.MerchantReportGendetail;
import com.nnte.kr_business.mapper.workdb.merchant.rec.MerchantReportRec;
import com.nnte.kr_business.mapper.workdb.merchant.rec.MerchantReportRecService;
import com.nnte.kr_business.mapper.workdb.merchant.report.MerchantReportDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@WorkDBAspect
@DataLibType
public class AutoReportRecComponent {
    @Autowired
    private MerchantReportRecService merchantReportRecService;

    public static class ReportRecState{
        public final static Integer PAUSE=0;  //暂停
        public final static Integer USED=1;   //启用
        public final static Integer DELETED=9;//删除
    }
    @DataLibItem("报表记录状态")
    public final static List<KeyValue> LibReportRecState = new ArrayList<>();
    static {//0：未启用，1：启用：2：删除
        LibReportRecState.add(new KeyValue(ReportRecState.PAUSE.toString(),"暂停"));
        LibReportRecState.add(new KeyValue(ReportRecState.USED.toString(),"启用"));
        LibReportRecState.add(new KeyValue(ReportRecState.DELETED.toString(),"删除"));
    }

    public MerchantReportRecService getMerchantReportRecService(){
        return merchantReportRecService;
    }
    //生成报表记录的显示名称
    public static String getReportRecTitle(MerchantReportDefine mrd,MerchantReportGendetail gendetail){
        StringBuilder builder=new StringBuilder();
        builder.append(mrd.getReportName());
        if (StringUtils.isNotEmpty(mrd.getReportClass())){
            builder.append("(").append(gendetail.getCutName()).append(")");
        }
        builder.append("第").append(gendetail.getPeriodNo().toString()).append("期");
        return builder.toString();
    }
    /*
    * 保存产生报表时对报表记录的更改
    * */
    public Map<String,Object> saveReportRecFromGen(ConnSqlSessionFactory cssf,MerchantReportGendetail gendetail,
                                                   MerchantReportDefine mrd){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        MerchantReportRec mrr=new MerchantReportRec();
        mrr.setParMerchantId(gendetail.getParMerchantId());
        mrr.setReportCode(gendetail.getReportCode());
        mrr.setPeriodNo(gendetail.getPeriodNo());
        if (StringUtils.isNotEmpty(mrd.getReportClass()))
            mrr.setReportClass(mrd.getReportClass());
        if (StringUtils.isNotEmpty(gendetail.getCutValue()))
            mrr.setCutValue(gendetail.getCutValue());
        MerchantReportRec saveDto=null;
        List<MerchantReportRec> list=merchantReportRecService.findModelWithPg(cssf,mrr);
        if (list==null||list.size()<=0){
            //需要生成新的报表记录
            mrr.setReportTitle(getReportRecTitle(mrd,gendetail));
            mrr.setReportBusiType(mrd.getReportBusiType());
            mrr.setCutName(gendetail.getCutName());
            mrr.setReportPeriod(gendetail.getReportPeriod());
            mrr.setStartTime(gendetail.getStartTime());
            mrr.setEndTime(gendetail.getEndTime());
            mrr.setReportName(gendetail.getReportName());
            mrr.setReportFileName(gendetail.getReportFileName());
            mrr.setReportState(ReportRecState.USED);//设置状态为1：可用
            mrr.setUpdateTime(new Date());
            mrr.setUpdateOperId(gendetail.getCreateOperId());
            saveDto=merchantReportRecService.save(cssf,mrr,false);
        }else {
            //需要更改原有的报表记录
            mrr=list.get(0);
            mrr.setStartTime(gendetail.getStartTime());
            mrr.setEndTime(gendetail.getEndTime());
            mrr.setReportName(gendetail.getReportName());
            mrr.setReportFileName(gendetail.getReportFileName());
            mrr.setUpdateTime(new Date());
            mrr.setUpdateOperId(gendetail.getCreateOperId());
            saveDto=merchantReportRecService.save(cssf,mrr,true);
        }
        if (saveDto==null||saveDto.getId()==null||saveDto.getId()<=0){
            BaseNnte.setRetFalse(ret, 1002,"保存报表记录时错误");
            return ret;
        }
        BaseNnte.setRetTrue(ret,"保存报表记录成功");
        return ret;
    }
}
