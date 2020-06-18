/***
 * 页面加载动画操作
 * 使用方法：
 *   显示加载动画：loading.show()
 * 	  隐藏加载动画：loading.hide()
 */
var loading = {
    show: function() {
        if (this.test()){
            var inntHtml = '';
            inntHtml += '<div class="u-loading">';
            inntHtml +=   '<div class="u-loading-text">加载中···</div>';
            inntHtml += '</div>';
            $("body").append(inntHtml);
        }
    },
    showTitle: function(title) {
        if (this.test()){
            var inntHtml = '';
            inntHtml += '<div class="u-loading">';
            inntHtml +=   '<div class="u-loading-text">'+title+'</div>';
            inntHtml += '</div>';
            $("body").append(inntHtml);
        }
    },
    hide: function() {
        this.hideDelay(500);
    },
    hideDelay: function(ms){
        $(".u-loading").fadeOut(ms, function(){
            $(this).remove();
        });
    },
    test: function(){
        var obj = $(".u-loading");
        return ( obj==undefined || obj.length==0 );
    }
};