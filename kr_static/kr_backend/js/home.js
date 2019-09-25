
$(function(){
	totalData("7");
	$(".selectDay .func-date").click(function(){
		if($(this).hasClass("active")){
			var day = $(this).attr("day");
			totalData(day);
		}
		
	})
})

function memberReception() {
	var nav='1';
	jumpPage("/w/memberReceptionIndex.action","会员接待","MEMBER_FANS_RECEPTION",nav);
}

function totalData(day){
	$.ajax({
		url: "/w/totalDataHome.action",
		type: "POST",
		data: {daySerach:day},
		success: function(str){
			try{
				data = JSON.parse(str);
				if (data){
					console.info(data);
					$("#fwNum").html(data.visiteNum!=undefined&&data.visiteNum!=''?data.visiteNum:"0");
					$("#fansNum").html(data.fansNum!=undefined&&data.fansNum!=''?data.fansNum:"0");
					$("#finishNum").html(data.finish!=undefined&&data.finish!=''?data.finish:"0");
					$("#payNum").html(data.paymentCount!=undefined&&data.paymentCount!=''?data.paymentCount:"0") ;
					$("#payAmount").html(data.amount!=undefined&&data.amount!=''?data.amount:"0");
					$("#sellCount").html(data.sellCount!=undefined&&data.sellCount!=''?data.sellCount:"0");
					$("#waitCount").html(data.waitCount!=undefined&&data.waitCount!=''?data.waitCount:"0");
					var fwNumPercent= data.visiteNumPercent!=undefined&&data.visiteNumPercent!=''?data.visiteNumPercent:"0.00";
					$("#fwNumPercent").html("↑"+fwNumPercent+"%");
					var fansNumPercent= data.fansNumPercent!=undefined&&data.fansNumPercent!=''?data.fansNumPercent:"0.00";
					$("#fansNumPercent").html("↑"+fansNumPercent+"%");
					var finishPercent= data.finishPercent!=undefined&&data.finishPercent!=''?data.finishPercent:"0.00";
					$("#finishNumPercent").html("↑"+finishPercent+"%");
					var paymentCountPercent= data.paymentCountPercent!=undefined&&data.paymentCountPercent!=''?data.paymentCountPercent:"0.00";
					$("#payNumPercent").html("↑"+paymentCountPercent+"%");
					var amountPercent= data.amountPercent!=undefined&&data.amountPercent!=''?data.amountPercent:"0.00";
					$("#payAmountPercent").html("↑"+amountPercent+"%");
				}else{
					show("网络错误···");
				}
			}catch(err){alert(err);}
		},
		error: function(){
			alert("网络错误···");
		}
	});	
}


 