//删除 ajax
function storeCmsDelete(){
	var merchantId = $("#merchantId").val();
	var cmsId = $("#cmsId").val();
	
	 var a=confirm("确认删除吗？");
	 if(a==true){
		 $.ajax({
				type : "post",
				url : "/w/storeCmsDelete.action",
				data : {
					'cmsId':cmsId
				},
				dataType : "json",
				success : function(data) {
					alert('删除成功!');
					showUrlDetail("/w/merchantCmsIndex.action");
				},
				error : function(msg) {
					alert('删除失败!');
				}
			});
	 }
}

//删除公告详情 ajax
function cmsDetailDelete(cmsDetailId,addType){
	var merchantId = $("#merchantId").val();
	var cmsId = $("#cmsId").val();
	var url = "/w/noticeEditIndex.action";
	if("add" == addType){
		url = "/w/noticeAddIndex.action";
	}
	 $.ajax({
			type : "post",
			url : "/w/storeCmsDetailDelete.action",
			data : {
				'cmsDetailId':cmsDetailId
			},
			dataType : "json",
			success : function(data) {
				showUrlDetail(url+"?merchantId="+merchantId+'&cmsId='+cmsId);
			},
			error : function(msg) {
				alert('删除失败!');
			}
		});
}

//商品公告商品停用、启用ajax
function storeCmsTurnOff(){
	var cmsId = $("#cmsId").val();
	var isNoDetail = $("#isNoDetail").val();
	
	if(cmsId == "" || isNoDetail != 1){
		alert("请先添加公告内容。")
		return;
	}
	
	$.ajax({
		type : "post",
		url : "/w/editStoreCms.action",
		data : {
			'qjbCms.id':cmsId,
			'qjbCms.state':1
		},
		dataType : "json",
		success : function(data) {
			table.ajax.reload();
			alert("保存发布成功!");
			 $('.level0').show();
             $('.level1').empty();
		},
		error : function(msg) {
			alert("保存发布失败!");
		}
	});
}

//跳转
function jumpCms(cmsId,merchantId){
	showUrlDetail("/w/noticeAddIndex.action?cmsId=" + cmsId+"&merchantId="+merchantId);
}

//添加 ajax
function addCmsAjax(detailType,cmsValue,cmsIndex){
	var cmsId = $("#cmsId").val();
	var merchantId = $("#merchantId").val();
	
	$.ajax({
		type : "post",
		url : "/w/noticeAdd.action",
		data : {
			'cmsDetail.cmsIndex' : cmsIndex,
			'cmsDetail.cmsId' : cmsId,
			'cmsDetail.detailType' : detailType,
			'cmsDetail.cmsValue' : cmsValue,
			'qjbCms.merchantId':merchantId
		},
		dataType : "json",
		success : function(data) {
			jumpCms(data.cmsId,merchantId);
		},
		error : function(msg) {
			alert("添加失败");
		}	
	});
}
var cmsIndexPic;
//添加图片
$(".btn-headPic").on("click",function(e) {
	cmsIndexPic = $(this).attr("title");
	QjbImageUploadFrame.upload({
		formUpload: true,//是否直接上传
		domain: "merchant",
		success: function(data){
			if (data!=undefined && data.url!=undefined && data.url!=""){
				addCmsAjax(1,data.url,cmsIndexPic);
		  }
		}
	});
});

//添加文字
function addCmsText(cmsIndex){
	addCmsAjax(2,"",cmsIndex);			
}
//添加商品
function addCmsGoods(cmsIndex,type){
	var cmsId = $("#cmsId").val();
	var merchantId = $("#merchantId").val();
	BootstrapDialog.show({
	    message: $('<div></div>').load('/w/commonGoodsIndex.action?cmsId='+cmsId+'&merchantId='+merchantId+'&cmsIndex='+cmsIndex),
	    onshow:function(dialog) {
		  dialog.setTitle("选择商品");
	    }
    });
}


//修改文字
function changeText(obj){
	var textId = obj.id;
	var nowValue = obj.value;
	
	var oldValue = $("#text"+textId).val();
	if(nowValue != oldValue){
		$.ajax({
			type : "post",
			url : "/w/editStoreCmsDetail.action",
			data : {
				'cmsDetail.cmsValue':nowValue,
				'cmsDetail.id':textId
			},
			dataType : "json",
			success : function(data) {
			},
			error : function(msg) {
				alert("修改内容失败");
			}
		});
	}
}

//修改标题
function changeTitle(obj){
	var nowValue = obj.value;
	var oldValue = $("#cmsTitle").val();
	var cmsId = $("#cmsId").val();
	var merchantId = $("#merchantId").val();
	
	if(nowValue != oldValue){
		$.ajax({
			type : "post",
			url : "/w/editStoreCms.action",
			async: false,
			data : {
				'qjbCms.title':nowValue,
				'qjbCms.id':cmsId,
				'qjbCms.merchantId':merchantId
			},
			dataType : "json",
			success : function(data) {
				if(cmsId ==""){
					$("#cmsId").val(data.id);
				}
			},
			error : function(msg) {
				alert("修改标题失败");
			}
		});
	}
}


