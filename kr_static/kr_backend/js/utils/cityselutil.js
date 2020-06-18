function citySelUtil() {
    $.fn.cityselutil = this;
    /**
     * valType=2:表示province，city，area输入的汉字文本，读取的时候也返回汉字文本
     * 否则表示输入和输出都是编号文本
     * cityData:城市数据，jsonArray格式
     * */
    this.initSelUtil=function(valType,cityData,provinceSelCtrl,province,citySelCtrl,city,areaSelCtrl,area){
        $.fn.cityData3 = cityData;
        $.fn.cityselutil.valType=valType;
        $.fn.cityselutil.provinceSelCtrl = provinceSelCtrl;
        $.fn.cityselutil.citySelCtrl = citySelCtrl;
        $.fn.cityselutil.areaSelCtrl = areaSelCtrl;
        if (cityData){
            if (provinceSelCtrl){
                var provinceHtml=this.loadOption($.fn.cityData3,"选择省份");
                $.fn.cityselutil.provinceSelCtrl.html(provinceHtml);
                if (province){
                    this.setProvince(province);
                    if (city){
                        this.setCity(city);
                        if (area){
                            this.setArea(area);
                        }
                    }else
                        this.cityChange(null);
                }
                else
                    this.provinceChange(null);
                $.fn.cityselutil.provinceSelCtrl.off("change").on("change",this.onProvinceChanged);
                if (citySelCtrl){
                    $.fn.cityselutil.citySelCtrl.off("change").on("change",this.onCityChanged);
                }
            }
        }
    }
    this.loadOption=function(lib,blank){
        var html = "<option value=''>"+blank+"</option>";;
        if (!lib || lib.length<=0)
            return html;
        for(var i = 0; i < lib.length; i++) {
            var node = lib[i];
            var opt="<option value='"+node.value+"'>"+node.text+"</option>";
            html+=opt;
        }
        return html;
    }
    this.findChildren=function(lib,child){
        if (!lib || lib.length<=0)
            return null;
        for(var i = 0; i < lib.length; i++){
            var node = lib[i];
            if (node.value==child)
                return node.children;
        }
        return null;
    }
    this.findNodeByText=function(lib,text){
        if (!lib || lib.length<=0)
            return null;
        for(var i = 0; i < lib.length; i++){
            var node = lib[i];
            if (node.text==text)
                return node;
        }
        return null;
    }
    this.setProvince=function(province){
        if ($.fn.cityselutil.valType==2){
            var node=this.findNodeByText($.fn.cityData3,province);
            if (node){
                $.fn.cityselutil.provinceSelCtrl.val(node.value);
                this.provinceChange(node.children);
            }
        }else{
            $.fn.cityselutil.provinceSelCtrl.val(province);
            this.provinceChange($.fn.cityselutil.findChildren($.fn.cityData3,province));
        }
    }
    this.setCity=function(city){
        if ($.fn.cityselutil.provinceSel && city){
            if ($.fn.cityselutil.valType==2){
                var node=this.findNodeByText($.fn.cityselutil.provinceSel,city);
                if (node){
                    $.fn.cityselutil.citySelCtrl.val(node.value);
                    this.cityChange(node.children);
                }
            }else{
                $.fn.cityselutil.citySelCtrl.val(city);
                this.cityChange($.fn.cityselutil.findChildren($.fn.provinceSel,city));
            }
        }
    }
    this.setArea=function(area){
        if ($.fn.cityselutil.citySel && area){
            if ($.fn.cityselutil.valType==2){
                var node=this.findNodeByText($.fn.cityselutil.citySel,area);
                if (node)
                    $.fn.cityselutil.areaSelCtrl.val(node.value);
            }else{
                $.fn.cityselutil.areaSelCtrl.val(area);
            }
        }
    }
    this.provinceChange=function (provinceSel) {
        $.fn.cityselutil.provinceSel = provinceSel;
        var cityHtml=$.fn.cityselutil.loadOption($.fn.cityselutil.provinceSel,"选择城市");
        $.fn.cityselutil.citySelCtrl.html(cityHtml);
        $.fn.cityselutil.citySel = null;
        $.fn.cityselutil.cityChange($.fn.cityselutil.citySel);
    }
    this.onProvinceChanged=function(event){
        if (event.target.value)
            $.fn.cityselutil.provinceChange($.fn.cityselutil.findChildren($.fn.cityData3,event.target.value));
    }
    this.cityChange=function(citySel){
        $.fn.cityselutil.citySel = citySel;
        var areaHtml=$.fn.cityselutil.loadOption($.fn.cityselutil.citySel,"选择区县");
        $.fn.cityselutil.areaSelCtrl.html(areaHtml);
    }
    this.onCityChanged=function(event){
        if (event.target.value)
            $.fn.cityselutil.cityChange($.fn.cityselutil.findChildren($.fn.cityselutil.provinceSel,event.target.value));
    }
    this.getCtrlSel=function(selCtrl){
        if (selCtrl){
            if ($.fn.cityselutil.valType==2){
                return selCtrl.find("option:selected").text();
            }else
                return selCtrl.val();
        }
        return '';
    }
    this.getProvice=function(){
        return this.getCtrlSel($.fn.cityselutil.provinceSelCtrl);
    }
    this.getCity=function(){
        return this.getCtrlSel($.fn.cityselutil.citySelCtrl);
    }
    this.getArea=function(){
        return this.getCtrlSel($.fn.cityselutil.areaSelCtrl);
    }
}