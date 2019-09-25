$(document).ready(function() {
			$(".tr-cont").hover(function() {
				$(this).css("border", "1px solid #4990E2");
			}, function() {
				$(this).css("border", "none");
			});
			$(".edit").hover(function() {
				$(this).children(".operation-icon").css("visibility", "visible");
			}, function() {
				$(this).children(".operation-icon").css("visibility", "hidden");
			});
			$(".edit").click(function() {
				$(this).children("input").removeAttr("disabled");
			});
			$("editable").blur(function() {
				$(this).attr("disabled","disabled");
			});
			$(".tag-show").hover(function() {
				$(this).children(".tag-cont").show();
			}, function() {
				$(this).children(".tag-cont").hide();
			});
			//头部tab切换
			if($(".tab-cont li") && $(".tab-cont li").length > 0) {
				$(".tab-cont li").off("click");
				$(".tab-cont li").on("click", function() {
					var index = $(this).index();
					$(this).addClass("active").siblings("li").removeClass("active");
					$('.tab-cont').eq(index).show().siblings('.tab-cont').hide();
				});
			};
			//首页 7天、30天切换
			$(".func-date").click(function() {
				$(".func-date").removeClass("active");
				$(this).addClass("active");
			});

		});


	