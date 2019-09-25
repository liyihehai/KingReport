var picId;
var headPicId
var listImage="";
$(".btn-m-bgimage").on("click",function(e) {
		picId = $(this).attr("id");
		QjbImageUploadFrame.upload({
			formUpload: true,//是否直接上传
			domain: "merchant",
			success: function(data){
				if (data!=undefined && data.url!=undefined && data.url!=""){
					$("#"+picId).attr("src", data.url);
				}
			}
		});
	});

$(".btn-headPic").on("click",function(e) {
	headPicId = $(this).attr("id");
	QjbImageUploadFrame.show({
		domain: "merchant",
		aspectRatio: "1:1",
		thumbnail:[100],
		success: function(data){
			if (data!=undefined && data.url!=undefined && data.url!=""){
				listImage=data.thumbnail[0].url;
				$("#"+headPicId).attr("src", data.url);
		  }
		}
	});
});

function changeCity(){
	var cityId = $('#cityDomainId1 option:selected').val();
	
	$.ajax({
		type : "post",
		url : "/w/getQjbArea.action",
		data : {
			'cityDomainId':cityId
		},
		dataType : "json",
		success : function(data) {
			$("#areaId").empty();
			if(data.length > 0){
				for(var i=0;i<data.length;i++){
					$("#areaId").append("<option value=\""+data[i].value+"\">"+data[i].text+"</option>");
				}
			}else{
				$("#areaId").append("<option value=\"1\">该城市下地区尚未开通</option>");
			}
		},
		error : function(msg) {
			alert("城市地区查询失败");
		}
	});
}

function editCity(){
	var cityId = $('#cityDomainId2').val();
	var cityAreaHid = $('#cityAreaHid').val();
	
	$.ajax({
		type : "post",
		url : "/w/getQjbArea.action",
		data : {
			'cityDomainId':cityId
		},
		async:false,
		dataType : "json",
		success : function(data) {
			$("#areaId").empty();
			if(data.length > 0){
				for(var i=0;i<data.length;i++){
					$("#areaId").append("<option value=\""+data[i].text+"\">"+data[i].text+"</option>");
				}
			}else{
				$("#areaId").append("<option value=\"1\">该城市下地区尚未开通</option>");
			}
		},
		error : function(msg) {
			alert("城市地区查询失败");
		}
	});
	$("#areaId").get(0).value = cityAreaHid;
}