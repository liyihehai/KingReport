var popupBackdropType = -1;
//生成随机数
function getRandom(){
	var random=Math.floor(Math.random()*999+1);
	//进入时请求验证码
	var userTel=$('#userName').val();
	var imgSrc="/w/refreshCode.action?keycode="+userTel+'&random='+random;
	return imgSrc;
	}
function getCH(){
	var userTel=$('#userName').val();
	var arrCH = new Array;
	$.ajax({
		  url: "/w/getRandomCode.action?keycode="+userTel,
		  type: 'get',
		  success: function (data) {
 		    arrCH = data.split(',');
 		    $('#num-1').html(arrCH[0]);
 		    $('#num-2').html(arrCH[1]);
		    
		  },
		  fail: function (err) {
		    console.log(err)
		  }
		})
		
}
//获取鼠标点击区域中的相对位置坐标
function getXAndY(event) {
	//鼠标点击的绝对位置
	Ev = event || window.event;
	var mousePos = mouseCoords(event);
	var x = mousePos.x;
	var y = mousePos.y;

	//获取div在body中的绝对位置
	var x1 = $(".auth-img").offset().left;
	var y1 = $(".auth-img").offset().top;

	//鼠标点击位置相对于div的坐标
	var x2 = x - x1;
	var y2 = y - y1;
	return {
		x: x2,
		y: y2
	};
}

//获取鼠标点击区域在Html绝对位置坐标
function mouseCoords(event) {
	if(event.pageX || event.pageY) {
		return {
			x: event.pageX,
			y: event.pageY
		};
	}
	return {
		x: event.clientX + document.body.scrollLeft - document.body.clientLeft,
		y: event.clientY + document.body.scrollTop - document.body.clientTop
	};
};
//验证码弹窗
function popAuthCode(callBack){
	if(!$('#userName').val()){
	   	mui.toast("请填写手机号码!");
		return;
	}
	
	$('.container-fluid').prepend('<div class="auth-cont"><div class="auth-img bk-b"><img id="yzm-img" onload="getCH()" src=""><div class="refresh-btn"><img src="../backend/pc/images/yzm-refresh.png"/></div></div><div class="yzm-msg">请顺序点击【<span id="num-1"></span>】【<span id="num-2"></span>】</div></div>');
    if(popupBackdropType==-1){
    	$('.popup-backdrop').fadeIn(400);
    	$('.popup-auth').addClass('bounceIn').removeClass('bounceOutUp').fadeIn();
    }       

	
	$('#yzm-img').attr('src',getRandom());
	//刷新验证码
	var count = 0;
	$('.refresh-btn').click(function(){
		$('#yzm-img').attr('src',getRandom());
		$('.cont-1').remove();
		$('.cont-2').remove();
		
		count=0;
	});
var arr = new Array();
	//提交验证码
$('#yzm-img').click(function(event) {

	count++;
	var data1 = mouseCoords(event);
	var x1 = data1.x;
	var y1 = data1.y;

	var data2 = getXAndY(event);
	var x2 = data2.x;
	var y2 = data2.y;
	
	if(count == 1) {
	
		$('.auth-img').prepend('<div class="cont-1">1</div>');
		$('.cont-1').css('left', x2 - 15);
		$('.cont-1').css('top', y2 - 15);
		arr[0] = '{x1:'+x2+',y1:'+y2+'}';
	} else if(count == 2) {
		$('.auth-img').prepend('<div class="cont-2">2</div>');
		$('.cont-2').css('left', x2 - 15);
		$('.cont-2').css('top', y2 - 15);
		var param2={"x1":x2,"y1":y2};
		arr[1] = '{x1:'+x2+',y1:'+y2+'}';
		
		$('#yzm').val("["+arr.toString()+"]");
		var loginType = callBack();
		loginType = loginType == undefined?true:loginType;
		if(!loginType){
			$(".refresh-btn").click();
		}
		
		var type = $(".auth-cont").attr("type");
		if(type=="1"){
			$(".refresh-btn").click();
		}else {
			$('.auth-cont').hide();
		}

	}

});
	$('.popup-backdrop').click(function(event) {
		$('.popup-backdrop').fadeOut(400);
		$('.popup-auth').removeClass('bounceIn').addClass('bounceOutUp').fadeOut();
		$('.popup-auth').remove()
		$('.auth-cont').hide();
	})
}