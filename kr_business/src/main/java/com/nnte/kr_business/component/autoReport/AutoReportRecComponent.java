package com.nnte.kr_business.component.autoReport;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.annotation.WorkDBAspect;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.annotation.DBSrcTranc;
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
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;

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
    /*
     * 获取报表预览文件的绝对路径
     * */
    public String getReportOutPDFFileAbPath(MerchantReportRec reportRec){
        if (reportRec==null||StringUtils.isEmpty(reportRec.getReportFileName()))
            return null;
        String xlsName=reportRec.getReportFileName();
        return FileUtil.changeFileNameExten(xlsName,"pdf");
    }

    /*
     * 前端接口函数，取得报表预览文件路径
     * paramMap.id=报表记录ID
     * */
    @DBSrcTranc
    public Map<String,Object> getReportRecPriviewPath(Map<String,Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        MerchantReportRec reportRec=merchantReportRecService.findModelByKey(cssf,paramMap.get("id"));
        if (reportRec==null){
            BaseNnte.setRetFalse(ret, 1002,"未找到指定的报表记录");
            return ret;
        }
        String priviewFileName=getReportOutPDFFileAbPath(reportRec);
        if (StringUtils.isEmpty(priviewFileName)){
            BaseNnte.setRetFalse(ret, 1002,"未找到报表记录的预览文件");
            return ret;
        }
        ret.put("priviewFileName",priviewFileName);
        BaseNnte.setRetTrue(ret, "找到报表记录的预览文件");
        return ret;
    }

    @DBSrcTranc
    public Map<String,Object> getReportRecPriviewBytes(Map<String,Object> paramMap){
        Map<String, Object> ret = BaseNnte.newMapRetObj();
        ConnSqlSessionFactory cssf = (ConnSqlSessionFactory) paramMap.get("ConnSqlSessionFactory");
        MerchantReportRec reportRec=merchantReportRecService.findModelByKey(cssf,paramMap.get("id"));
        if (reportRec==null){
            BaseNnte.setRetFalse(ret, 1002,"未找到指定的报表记录");
            return ret;
        }
        String name=reportRec.getReportFileName();
        if (StringUtils.isEmpty(name)){
            BaseNnte.setRetFalse(ret, 1002,"未找到指定的报表的预览文件");
            return ret;
        }
        String[] names=name.split(":");
        if (names==null || names.length<2){
            BaseNnte.setRetFalse(ret, 1002,"未找到指定的报表的预览文件名");
            return ret;
        }
        byte[] priviewContent=fdfsClientMgrComponent.downloadFile("reportConvPdf",names[1]);
        if (priviewContent==null || priviewContent.length<=0){
            BaseNnte.setRetFalse(ret, 1002,"未取得指定的报表的预览文件数据");
            return ret;
        }
        ret.put("priviewContent",priviewContent);
        BaseNnte.setRetTrue(ret, "找到报表记录的预览文件");
        return ret;
    }
    /*
     * 获取用于预览的指定商户报表生成记录
     * */
    public MerchantReportRec priOpenPeroidReport(ConnSqlSessionFactory cssf,MerchantReportRec queryDto,Integer off){
        MerchantReportRec dto=new MerchantReportRec();
        dto.setParMerchantId(queryDto.getParMerchantId());
        dto.setReportCode(queryDto.getReportCode());
        if (StringUtils.isNotEmpty(queryDto.getCutValue()))
            dto.setCutValue(queryDto.getCutValue());
        dto.setSort("PERIOD_NO");//按账期排序
        dto.setDir("ASC");
        List<MerchantReportRec> list=merchantReportRecService.findModelWithPg(cssf,dto);
        for(int i=0;i<list.size();i++){
            MerchantReportRec mrr=list.get(i);
            if (mrr.getPeriodNo().equals(queryDto.getPeriodNo())){
                int needIndex=i+off;
                if (needIndex<=0)
                    needIndex=0;
                else if (needIndex>=list.size())
                    needIndex=list.size()-1;
                return list.get(needIndex);
            }
        }
        return null;
    }
}
