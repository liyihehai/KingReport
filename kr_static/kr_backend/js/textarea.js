
	var selected_area;
    function autoTextArea(clsname){
    	var _cls_name = '.'+clsname;
    	if ( $(_cls_name) && $(_cls_name).length>0 ){
    		var t = document.createElement("textarea");
    		$(t).attr("id", "_textareacopy");
    		$(t).css("position", "absolute");
    		$(t).css("left", "-9999px");
    		$(t).height("23");
    		$(t).appendTo( $(document.body) );
    	}
    	function change(){
    		if (selected_area){
    			$("#_textareacopy").val( $(selected_area).val() );
    			var iHeight=document.getElementById("_textareacopy").scrollHeight;
    			var iPaddingTop=0, iPaddingBottom=0;
    			if ($("#_textareacopy").has("padding-top")){
    				iPaddingTop = $("#_textareacopy").css("padding-top").replace("px","");
    			}
    			if ($("#_textareacopy").has("padding-bottom")){
    				iPaddingBottom = $("#_textareacopy").css("padding-bottom").replace("px","");
    			}
//  			console.log( iHeight + ", " + iPaddingTop + ", " + $("#_textareacopy").has("left"));
//  			console.log( $("#_textareacopy").height() );
    			$(selected_area).height( iHeight - iPaddingTop - iPaddingBottom );
    		}
    	}
    	$(_cls_name).off("propertychange");
    	$(_cls_name).on("propertychange",function(){
    		selected_area = this;
    		change();
    	});
    	$(_cls_name).off("input");
    	$(_cls_name).on("input", function(){
    		selected_area = this;
    		change();
    	});
    	$(selected_area).css("overflow", "hide");
    	$(selected_area).css("resize", "none");
    }
    
    $(window).on("load",function(){
    	autoTextArea("target");
    })

