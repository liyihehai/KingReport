package com.nnte.kr_business.mapper.workdb.merchant.report;

import com.nnte.framework.base.BaseService;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.entity.autoReport.TemplateItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
    //初始化报表的模板文件集
    public static JSONArray initReportTempfileCollect(MerchantReportDefine mrd){
        if (mrd==null || StringUtils.isEmpty(mrd.getTempfileCollect())){
            return new JSONArray();
        }else{
            try {
                return JSONArray.fromObject(mrd.getTempfileCollect());
            }catch (Exception e){
                e.printStackTrace();
                return new JSONArray();
            }
        }
    }

    public static void setReportTempfileCollect(MerchantReportDefine mrd,JSONArray tcArray){
        if (mrd!=null && tcArray!=null)
            mrd.setTempfileCollect(tcArray.toString());
    }
    //增加一条模板文件记录到模板文件集
    public static void addSubmitFileToCollect(MerchantReportDefine mrd,String fileName,String submitName){
        if (mrd==null)
            return;
        JSONArray tcArray=initReportTempfileCollect(mrd);
        boolean exist=false;
        for(int i=0;i<tcArray.size();i++){
            JSONObject jobj=tcArray.getJSONObject(i);
            if (StringUtils.defaultString(jobj.get("submitName")).equals(submitName)) {
                exist=true;
                break;
            }
        }
        if (!exist){
            JSONObject jobj=new JSONObject();
            jobj.put("fileName",fileName);
            jobj.put("submitName",submitName);
            tcArray.add(0,jobj);
            setReportTempfileCollect(mrd,tcArray);
        }
    }

    /**
     * 按原始文件名增加（或更改）一张报表的模板文件集
     * @param mrd:报表对象
     * @param fileName：原始文件名
     * @param submitName：提交文件名
     * @return：如果原报表存在该原始文件名，返回相应的原始提交文件名，否则返回NULL
     */
    public static String addFileToCollect(MerchantReportDefine mrd,String fileName,String submitName){
        if (mrd==null)
            return null;
        JSONArray tcArray=initReportTempfileCollect(mrd);
        String srcSubmitName=null;
        for(int i=0;i<tcArray.size();i++){
            JSONObject jobj=tcArray.getJSONObject(i);
            TemplateItem mti= (TemplateItem) JSONObject.toBean(jobj,TemplateItem.class);
            if (mti!=null && mti.getFileName().equals(fileName)) {
                srcSubmitName = mti.getSubmitName();
                tcArray.remove(i);
                break;
            }
        }
        TemplateItem ti=new TemplateItem();
        ti.setFileName(fileName);
        ti.setSubmitName(submitName);
        tcArray.add(0,JSONObject.fromObject(ti));
        setReportTempfileCollect(mrd,tcArray);
        return srcSubmitName;
    }
    //从模板文件集删除一条模板文件记录
    public static void delTempfileFromCollect(MerchantReportDefine mrd,String submitName){
        if (mrd==null)
            return;
        JSONArray tcArray=initReportTempfileCollect(mrd);
        for(int i=0;i<tcArray.size();i++){
            JSONObject jobj=tcArray.getJSONObject(i);
            if (StringUtils.defaultString(jobj.get("submitName")).equals(submitName)) {
                tcArray.remove(i);
                setReportTempfileCollect(mrd,tcArray);
                break;
            }
        }
    }

    public Integer saveReportTempfileCollect(ConnSqlSessionFactory cssf,MerchantReportDefine mrd){
        if (cssf==null || mrd==null || mrd.getId()==null || mrd.getId()<=0)
            return 0;
        MerchantReportDefine updateDto=new MerchantReportDefine();
        updateDto.setId(mrd.getId());
        updateDto.setTempfileCollect(mrd.getTempfileCollect());
        return this.updateModel(cssf,updateDto);
    }
}

