var ImgViewDialog= {
    showDialog : function(imgSrc,imgTitle){
        var hm = "<div align='center'><img id='ImgViewDialog_img' src='"+imgSrc+"' atl='"+imgTitle+"' style='width: auto;height: 100%'/><div>";
        hm += "<script>";
        hm += "UrlDialog.pushDataFunction(function(){return {};});";
    //    hm += "ImgViewDialog.imageviewer();";
        hm += "</script>";
        var content = {};
        content.code=0;
        content.htmlBody=hm;
        UrlDialog.appendData={};
        UrlDialog.appendData.title=GlobalUtil.isEmpty(imgTitle)?'图片浏览':imgTitle;
        UrlDialog.appendData.winSize=DialogGlobal.nomalSize();
        UrlDialog.localContent(content,null,null);
    },
    imageviewer:function(){
        var $image = $("#ImgViewDialog_img");
        $image.viewer({
            inline: true,
            viewed: function() {
                $image.viewer('zoomTo', 1);
            }
        });
    },
    setImgClickView:function(){
        $(".dialog-view-img").off("click").on("click",function (event) {
            var img = $(event.target);
            ImgViewDialog.showDialog(img.attr("src"),img.attr("atl"));
        });
    }
}