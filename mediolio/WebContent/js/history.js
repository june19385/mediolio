/**
 * 
 */
var imgCnt = 1;

$(function(){
	$("#addHistoryBtn").click(function(){
		// 히스토리 추가
		$("#addHistoryForm").submit();
	});
	
	$(".btn_timeCard_addImage").click(function(){
		// 사진 추가
		if(imgCnt<=3)
			imgCnt++;
		
		switch(imgCnt){
			case 1:
				$("#imgFileBox1").css("display","block");
				break;
			case 2:
				$("#imgFileBox2").css("display","block");
				break;
			case 3:
				$("#imgFileBox3").css("display","block");
				break;
			default:
				alert("이미지는 최대 3개까지만 업로드 가능합니다.");	
		}
	});
	
	$(".file_timeCard").change(function(){
		// 파일 업로드 했을 때 이름 보여주기
		var fileName = $(this).val().split("\\")[2];
		$(this).parent().parent().find("label").html(fileName);
	});
	
	$(".btn_timeCard_delImage").click(function(){
		// 사진 삭제
		imgCnt--;
		$(this).parent().css("display","none");
		// 왜 위로 올라감 ㅡㅡ
		// input 파일 내용 비우기
	});
	
	$(".br_delete_btn").click(function(){
		// 브랜치 삭제
		$.ajax({
			type: "POST",
			url: "deleteBranch",
			data: {
				br_id: $(this).parent().find(".branch_id").val(),
				ht_id: $("#recentHtId").val()
			}, 
			success: function(result){
				
			}
		});
	});
	
	$(".historyList_addRelated").keyup(function(){
		// 관련 과목 입력 시 자동 완성
		if($(this).val().trim() != ""){
	    	$.ajax({
	    		type: "POST",
	    		url: "autocompleteClass",
	    		data: {
	    			cl_name: $(this).val()
	    		},
	    		dataType: "json",
	    		success: function(jdata){
	    			var codes = "";
	    			var arr = jdata;
	    			for(var i=0; i<arr.length; i++){
	    				codes += "<li onclick='addClass(this)'>"+arr[i]+"</li>";
	    			}
	    			if(arr.length>0){
	    				$("#autoCompleteArea").html(codes);
	        			$(".autoCompleteBox").css({
	        				display: "block",
	        				top: $(".historyList_addRelated").offset().top-108
	        			});
	        			
	            		// moveAutoCompleteBox();
	        			// 자동 완성 목록 위치 변경: 현재 커서 위치에 맞게
	    			}
	    		}
	    	});
    	} else {
    		$(".autoCompleteBox").css("display","none");
    	}

	})
	
});

function addClass(li){
	// 자동 완성 목록에서 항목 클릭 시 관련 과목 영역에 추가
	var newClass = li;
	console.log("과목: "+$(newClass).find("span").html());
	$(".historyList_addRelated").val($(newClass).find("span").html());
	$(".autoCompleteBox").css("display","none");
	$(".timeCard_writeDisplayWrap").append("<input type='hidden' name='cl_id' value="+$(newClass).find(".classId").val()+">");
}

function deleteHistory(ht_id){
	// 히스토리 삭제
	$("#deleteHt"+ht_id).submit();
}

function changeHtPublic(ht_id,ht_public){
	// 공개 여부 변경
	$.ajax({
		type: "POST",
		url: "changeHtPublic",
		data: {
			ht_id: ht_id,
			ht_public: ht_public
		}, 
		success: function(result){
			$(".historyExWrap").html(result);
		}
	});
}