//var glob_JS_Static_Host = "";
String.prototype.trim = function () {
    var t = this.replace(/(^\s*)|(\s*$)/g, "");
    return t.replace(/(^\u3000*)|(\u3000*$)/g, "");
};

//弹出窗口
function openDialog(title, href) {
    BootstrapDialog.show({
        message: $('<div></div>').load(href),
        closable: false,
        title: title
    });
    setTimeout(function () {
        $('.bootstrap-dialog-close-button').show();
    }, 200);
}

//关闭弹出窗口
function closeDialog() {
    $('.close').click();
}

function msgBox(text) {
    var dialog;
    $('.modal-dialog').hide();
    BootstrapDialog.show({
        message: $('<div></div>').load(glob_JS_Static_Host + '/html/tipsPage.html'),
        title: "温馨提示",
        onshow: function (dialog_) {
            dialog = dialog_;
        }
    });
    setTimeout(function () {
        $('.modal-content-span-tips').text(text);
    }, 200);
    setTimeout(function () {
        dialog.close();
        $('.modal-dialog').show();
    }, 3500);
}

function confirmBox(text, successCallback, failCallback) {
    BootstrapDialog.confirm({
        title: '温馨提示',
        message: $('<div></div>').load(glob_JS_Static_Host + '/html/tipsPage.html'),
        callback: function (result) {
            // result will be true if button was click, while it will be false if users close the dialog directly.
            if (result) {
                if (successCallback)
                    successCallback();
            } else {
                if (failCallback)
                    failCallback();
                $('.close').click();
            }
        }
    });
    setTimeout(function () {
        $('.modal-content-span-tips').text(text);
    }, 200);
}


function showSystips() {
    $.post(
        '/w/orderTips.action',
        {},
        function (r, s) {
            var rs = JSON.parse(r);
            $('#orderAllTips').text(rs.orderTips + rs.refundOrderTips);
            $('#orderTips').text(rs.orderTips + rs.refundOrderTips);
            //$('#coinOrderTips').text(rs.platOrderTips);
        }
    );
}

/**封装系统等待提示框**/
function syswait(msg) {
    BootstrapDialog.show({
        message: $('<div></div>').load(glob_JS_Static_Host + '/html/loadingPage.html'),
        onshow: function (dialog) {
            dialog.setTitle(msg);
        }
    });
}


/**数据导出封装**/
function dataExport(searchParams, exportUrl) {
    syswait("正在导出...");
    $.post(
        exportUrl,
        searchParams,
        function (r, s) {
            if (r != '10000') {
                window.location.href = r;
            }
            setTimeout(function () {
                closeDialog();
            }, 200);

            if (r == '10000') {
                setTimeout(function () {
                    msgBox('没有找到相应数据,请稍后再试');
                }, 700);
            }
        }
    )
}


var tabCount = 0;
var menuMap = new Map();

function jumpPage(jumpurl, title, menukey, nav) {
    var value = menuMap.get(menukey);
    if (value && nav != 1) {
        $('#' + menukey).click();
        var iframe = $('#' + value).find('iframe');
        iframe.attr('src', jumpurl);
        return;
    }
    if (!jumpurl) {
        return;
    }
    if (tabCount > 7) {
        alert('当前打开菜单太多，请关闭闲置菜单');
        return;
    }
    tabCount++;
    var tabId = menukey + tabCount;
    menuMap.put(menukey, tabId);
    var menu_title = '<li><a href="#' + tabId + '" data-toggle="tab" aria-expanded="false" id="' + menukey + '">' + title +
        '<button class="btn btn-box-tool" onclick="removeMe(this)" style="margin-top:-8px;"><i class="fa fa-remove"></i></button></a></li>';
    var menu_content = '<div class="tab-pane" id="' + tabId + '">' +
        '<iframe src="' + jumpurl + '" frameborder=0 width="100%" height="750px" scrolling=no></iframe>' +
        '</div>';
    if (nav == 1) {
        parent.$("#menu_title").append(menu_title);
        parent.$("#menu_content").append(menu_content);
        parent.$('#' + menukey).click();

    } else {

        $("#menu_title").append(menu_title);
        $("#menu_content").append(menu_content);
        $('#' + menukey).click();

    }
    //$("#nav_link").empty().html(nav);
}

function removeMe(obj) {
    menuMap.remove($(obj).parent().attr('id'));
    tabCount--;
    $(obj).parent().parent().remove();
    $($(obj).parent().attr('href')).remove();
    var activeMenu = $("#menu_title .active");
    if (!activeMenu.text()) {
        $('#HOME').click();
        return;
    }
}


function showUrlDetail(url) {
    syswait("正在打开菜单...");
    var level1 = $('.level1');
    if (!level1.attr('class')) {
        var level1htm = '<div class="row level1" style="display:none;"></div>';
        $('.level0').after(level1htm);
    } else {
        level1.empty();
    }
    if (!url) {
        return;
    }
    $.ajax({
        type: "post",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: url,
        cache: false,
        dataType: "html",
        success: function (html) {
            $('.level1').append(html);

            var removes = document.querySelectorAll('[data-widget="remove"]');
            for (i = 0; i < removes.length; ++i) {
                removes[i].onclick = function tst() {
                    $('.level0').show();
                    $('.level1').empty();
                }
            }
            $('.level0').hide();
            $('.level1').show();
            setTimeout(function () {
                closeDialog();
            }, 200);
        }
    });
}

function showMeDetail(obj) {
    syswait("正在打开菜单...");
    var level1 = $('.level1');
    if (!level1.attr('class')) {
        var level1htm = '<div class="row level1" style="display:none;"></div>';
        $('.level0').after(level1htm);
    } else {
        level1.empty();
    }
    var url = $(obj).attr('detail-url');
    if (!url) {
        return;
    }

    $.ajax({
        type: "post",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        url: url,
        cache: false,
        dataType: "html",
        success: function (html) {
            $('.level1').append(html);
            var removes = document.querySelectorAll('[data-widget="remove"]');
            for (i = 0; i < removes.length; ++i) {
                removes[i].onclick = function tst() {
                    $('.level0').show();
                    $('.level1').empty();
                }
            }
            $('.level0').hide();
            $('.level1').show();
            setTimeout(function () {
                closeDialog();
            }, 200);
        }
    });
}


function Map() {
    this.container = new Object();
}

Map.prototype.put = function (key, value) {
    this.container[key] = value;
}

Map.prototype.get = function (key) {
    return this.container[key];
}

Map.prototype.remove = function (key) {
    delete this.container[key];
}

//全局JS Ajax闭包
function AppJSGlobAjax(token) {
    this.client_token = token;
    this.getAjaxSetting = function (url, data) {
        var settings = {
            "async": true,
            "crossDomain": true,
            "url": url,
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "cache-control": "no-cache",
                "Postman-Token": this.client_token
            },
            "processDate": false,
            "data": data
        }
        return settings;
    };
    this.normalAjax = function (settings, onSucceed, onFailed) {
        var ajax = $.ajax(settings);
        ajax.done(onSucceed);
        if (onFailed == undefined || onFailed == null)
            ajax.fail(function (msg) {
                (new messageBox()).showMsgBox("网络错误......");
            });
        else
            ajax.fail(onFailed);
    };
    this.AjaxApplicationJson = function (url, data, onSucceed) {
        var settings = this.getAjaxSetting(url, data);
        this.normalAjax(settings, onSucceed, null);
    };
}

function AppJSGlobDatePicker() {
    this.initDatePickerStyle = function () {
        $.fn.datetimepicker.dates['zh-CN'] = {
            days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
            daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日"],
            daysMin: ["日", "一", "二", "三", "四", "五", "六", "日"],
            months: ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
            monthsShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
            today: "今天",
            suffix: [],
            format: "yyyy-mm-dd",
            meridiem: ["上午", "下午"]
        };
    }
    this.setDatePickerParams = function (DPObject, isInit) {
        if (isInit != undefined && isInit != null && isInit)
            this.initDatePickerStyle();
        DPObject.datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            minView: 2,
            forceParse: 0
        });
    }
    this.setDateRangePickerParams = function (DRPObject, opens,isInit) {
        if (isInit != undefined && isInit != null && isInit)
            this.initDatePickerStyle();
        DRPObject.daterangepicker({
            format: 'YYYY-MM-DD',
            opens: opens, //日期选择框的弹出位置
            locale: {
                applyLabel: '确定',
                cancelLabel: '取消',
                fromLabel: '从',
                toLabel: '到',
                customRangeLabel: 'Custom',
                daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                monthNames: ['一月', '二月', '三月', '四月', '五月',
                    '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
                firstDay: 0
            }
        });
    }
}

function AppJSGlobDataTable(){
    this.dataTableNormalSet=function (staticRoot) {
        var normalSet={//下面是一些汉语翻译
            "sSearch": "搜索",
            "sLengthMenu": "每页显示 _MENU_",
            "sZeroRecords": "没有检索到数据",
            "sInfo": "显示 _START_ 至 _END_ 条 &nbsp;&nbsp;共 _TOTAL_ 条 &nbsp;&nbsp;",
            "sInfoFiltered": "(筛选自 _MAX_ 条数据)",
            "sInfoEmtpy": "没有数据",
            "sProcessing": "<img src='"+staticRoot+"/resources/images/loading.gif' /> 数据加载中...",
            "oPaginate":
                {
                    "sFirst": "首页",
                    "sPrevious": "前一页",
                    "sNext": "后一页",
                    "sLast": "末页"
                }
        };
        return normalSet;
    }
    this.tableAppendMenuFunction=function (buttonTxt,funcs){
        var hm='';
        hm+='<div class="btn-group">'+
            '<button type="button" class="btn btn-warning btn-default" data-toggle="dropdown">'+buttonTxt+'<i class="fa fa-caret-down"></i></button>'+
            '<ul class="dropdown-menu" role="menu" style="width:100px;">';
        for(var i=0;i<funcs.length;i++){
            hm+='<li><a href="javascript:void(0);" onclick="'+funcs[i].funcName+
                '('+funcs[i].funcId+')">'+funcs[i].funcText+'</a></li>';
        }
        hm+='</ul>'+
            '</div>';
        return hm;
    }
}

function AppJSGlobUtil() {
    this.dateFtt=function(fmt,date)
    { //author: meizz
        var o = {
            "M+" : date.getMonth()+1,                 //月份
            "d+" : date.getDate(),                    //日
            "h+" : date.getHours(),                   //小时
            "m+" : date.getMinutes(),                 //分
            "s+" : date.getSeconds(),                 //秒
            "q+" : Math.floor((date.getMonth()+3)/3), //季度
            "S"  : date.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt))
            fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)
            if(new RegExp("("+ k +")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        return fmt;
    }
    this.parasColKeyValList=function (data,kvList,unknown) {
        if (kvList && kvList!=0 && kvList!=null && kvList.length>0){
            for(var i=0;i<kvList.length;i++){
                if (data==kvList[i].key)
                    return kvList[i].value;
            }
        }
        return unknown;
    }
    this.getS=function(def,ojbVal){
        if (ojbVal==undefined||ojbVal==null)
            return def;
        return ojbVal;
    }
    this.getL=function(def,ojbVal){
        if (ojbVal==undefined||ojbVal==null)
        {
            if (def==undefined||def==null)
                return 0;
            return def;
        }
        return ojbVal;
    }
    this.getD=function(def,fmt,ojbVal){
        if (ojbVal==undefined||ojbVal==null)
        {
            ojbVal=null;
            if (def==undefined||def==null)
                ojbVal=new Date(Date.now());
            else
                ojbVal=new Date(def);
        }else
            ojbVal=new Date(ojbVal);
        if (fmt==undefined||fmt==null)
            return this.dateFtt(ojbVal,"yyyy-MM-dd");
        return this.dateFtt(ojbVal,fmt);
    }
    //取得文件名的扩展名(文件类型)
    this.getFileNameExten=function (fileName) {
        var index = fileName.lastIndexOf(".");
        var suffix = fileName.substr(index+1);
        return suffix;
    }
}

function messageBox() {
    var onComfire = function(agrs){};
    this.showMsgBox=function(text) {
        $('.modal-dialog').hide();
        BootstrapDialog.show({
            message: text,
            title: "温馨提示",
            size : BootstrapDialog.SIZE_SMALL,
            buttons: [{
                id: 'button-known',
                label: '知道了',
                hotkey: 27,
                action: function(dialog){
                    dialog.close();
                    $('.modal-dialog').show();
                }
            }]
        });
    }
    this.showComfireBox = function(text,param,onComfire) {
        $('.modal-dialog').hide();
        BootstrapDialog.show({
            message : text,
            type: BootstrapDialog.TYPE_DEFAULT,
            title : "询问窗",
            size : BootstrapDialog.SIZE_SMALL,
            buttons : [ {
                id : 'button-comfire',
                label : '确定',
                hotkey : 13,
                action : function(dialog) {
                    dialog.close();
                    $('.modal-dialog').show();
                    if (onComfire!=undefined && onComfire!=null)
                        onComfire(param);
                }
            },
                {
                    id : 'button-cancel',
                    label : '取消',
                    hotkey : 27,
                    action : function(dialog) {
                        dialog.close();
                        $('.modal-dialog').show();
                    }
                }],
            onhidden : function () {
                $('.modal-dialog').show();
            }
        });
    }
    this.showUrlPage=function (url,OnPageOpened) {
        syswait("正在打开菜单...");
        var level1 = $('.level1');
        if (!level1.attr('class')) {
            var level1htm = '<div class="row level1" style="display:none;"></div>';
            $('.level0').after(level1htm);
        } else {
            level1.empty();
        }
        if (!url) {
            return;
        }
        $.ajax({
            type: "post",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            url: url,
            cache: false,
            dataType: "html",
            success: function (html) {
                $('.level1').append(html);
                var removes = document.querySelectorAll('[data-widget="remove"]');
                for (i = 0; i < removes.length; ++i) {
                    removes[i].onclick = function tst() {
                        $('.level0').show();
                        $('.level1').empty();
                    }
                }
                $('.level0').hide();
                $('.level1').show();
                if (OnPageOpened!=null)
                    OnPageOpened();
                setTimeout(function () {
                    closeDialog();
                }, 200);
            }
        });
    }
}
var msgbox=new messageBox();
 



