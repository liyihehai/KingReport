package com.nnte.kr_business.component.base;


import com.nnte.framework.annotation.DataLibItem;
import com.nnte.framework.annotation.DataLibType;
import com.nnte.framework.base.ConnSqlSessionFactory;
import com.nnte.framework.base.DataLibrary;
import com.nnte.framework.entity.KeyValue;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.StringUtils;
import com.nnte.kr_business.mapper.workdb.base.sysparam.BaseSysParam;
import com.nnte.kr_business.mapper.workdb.base.sysparam.BaseSysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* 系统参数组件
* */
@Component
@DataLibType
public class SysParamComponent {
    //组件数据字典区域
    //------------------------------------------
    public enum ValCol{
        VAL_100,
        VAL_200,
        VAL_500,
        VAL_TXT
    }
    public static class ParamType{
        public final static String TYPE_SYS_SINGLE="SS";
        public final static String TYPE_SYS_MUL="SM";
        public final static String TYPE_MER_SINGLE="MS";
        public final static String TYPE_MER_MUL="MM";
    }
    @DataLibItem("系统参数类型")
    public final static List<KeyValue> LibSysParamType = new ArrayList<>();
    static {//SS：系统单值参数，SM：系统多值参数，MS：商户单值参数，MM：商家多值参数
        LibSysParamType.add(new KeyValue(ParamType.TYPE_SYS_SINGLE,"系统单值参数"));
        LibSysParamType.add(new KeyValue(ParamType.TYPE_SYS_MUL,"系统多值参数"));
        LibSysParamType.add(new KeyValue(ParamType.TYPE_MER_SINGLE,"商户单值参数"));
        LibSysParamType.add(new KeyValue(ParamType.TYPE_MER_MUL,"商家多值参数"));
    }
    @Autowired
    private BaseSysParamService baseSysParamService;
    /*
    * 取得商户多值参数队列：商户多值参数通过class_id(分类号)取得，生成Key - Value键值对列表
    * */
    public List<KeyValue> getMerchantMulKVParams(Long merchantId,String classId){
        List<BaseSysParam> list=getMerchantMulParams(merchantId,classId);
        if (list==null || list.size()<=0)
            return null;
        List<KeyValue> ret = new ArrayList<>();
        for(BaseSysParam p:list){
            ret.add(new KeyValue(p.getPkey(),p.getValue1()));
        }
        return ret;
    }
    //取得特定的商户多值参数列表
    public List<BaseSysParam> getMerchantMulParams(Long merchantId,String classId){
        if (merchantId==null || merchantId<=0 || StringUtils.isEmpty(classId))
            return null;
        BaseSysParam dto = new BaseSysParam();
        dto.setParMerchantId(merchantId);//特定商户定义
        dto.setParamType("MM");     //参数类型为商家多值参数
        dto.setClassId(classId);    //按分类号查询多键值对
        List<BaseSysParam> list=baseSysParamService.findModelList(dto);
        if (list==null || list.size()<=0)
            return null;
        return list;
    }
    //取得商户多值参数的单项设置
    public BaseSysParam getMerchantMulParamItem(Long merchantId,String classId,String key){
        List<BaseSysParam> list=getMerchantMulParams(merchantId,classId);
        if (list==null || list.size()<=0)
            return null;
        for(BaseSysParam bsp:list){
            if (bsp.getPkey().equals(key))
                return bsp;
        }
        return null;
    }
    /*
     * 取得商户单参数对象
     * */
    public BaseSysParam getMerchantSingleParamObj(Long merchantId,String key){
        BaseSysParam dto = new BaseSysParam();
        dto.setParMerchantId(merchantId);//特定商户定义
        dto.setParamType("MS");     //参数类型为商家单值参数
        dto.setPkey(key);           //按分类号查询多键值对
        List<BaseSysParam> list=baseSysParamService.findModelList(dto);
        if (list==null || list.size()<=0)
            return null;
        return list.get(0);
    }
    /*
     * 取得商户单值参数：商户单值参数通过key取得Value值
     * */
    public String getMerchantSingleParams(Long merchantId,String key,ValCol vc){
        if (merchantId==null || merchantId<=0 || StringUtils.isEmpty(key))
            return null;
        BaseSysParam bsp=getMerchantSingleParamObj(merchantId,key);
        if (bsp==null || bsp.getParamState()==null || !bsp.getParamState().equals(1))
            return null;
        if (vc!=null) {
            if (vc.equals(ValCol.VAL_100))
                return bsp.getValue1();
            if (vc.equals(ValCol.VAL_200))
                return bsp.getValue2();
            if (vc.equals(ValCol.VAL_500))
                return bsp.getValue5();
            if (vc.equals(ValCol.VAL_TXT))
                return bsp.getValueText();
        }
        return bsp.getValue1();
    }

    //保存商户单值参数（返回为1表示操作成功，失败返回0）
    public Integer saveMerchantSingleParams(ConnSqlSessionFactory cssf, Long merchantId, String key, ValCol vc,
                                            String value, Long opeId, String opeName){
        BaseSysParam insertDto = getMerchantSingleParamObj(merchantId,key);
        if (insertDto==null)
        {
            insertDto=new BaseSysParam();
            insertDto.setParMerchantId(merchantId);//特定商户定义
            insertDto.setParamType("MS");     //参数类型为商家单值参数
            insertDto.setParamName(DataLibrary.getSysParamDesc(insertDto.getParamType(),key));
            insertDto.setPkey(key);           //按分类号查询多键值对
            insertDto.setParamState(1);       //参数状态为有效
        }
        else {
            if (insertDto.getParamState()==null || !insertDto.getParamState().equals(1))
                return 0;
        }
        insertDto.setUpdateTime(new Date());
        insertDto.setUpdateOpeId(opeId);
        insertDto.setUpdateOpeName(opeName);
        if (vc!=null) {
            if (vc.equals(ValCol.VAL_100))
                insertDto.setValue1(value);
            if (vc.equals(ValCol.VAL_200))
                insertDto.setValue2(value);
            if (vc.equals(ValCol.VAL_500))
                insertDto.setValue5(value);
            if (vc.equals(ValCol.VAL_TXT))
                insertDto.setValueText(value);
        }else
            insertDto.setValue1(value);
        BaseSysParam saveDto=baseSysParamService.save(cssf,insertDto,false);
        if (saveDto!=null && saveDto.getId()!=null && saveDto.getId()>0)
            return 1;
        return 0;
    }

    public Integer getMerchantExportSheetCount(Long parMerchantId){
        Integer sheetCount= NumberUtil.getDefaultInteger(getMerchantSingleParams(
                parMerchantId,"MERCHANT_EXPORT_EXCEL_SHEET_COUNT",null));
        if (sheetCount<=0)
            sheetCount=3000;//默认3000条一页
        return sheetCount;
    }
}
