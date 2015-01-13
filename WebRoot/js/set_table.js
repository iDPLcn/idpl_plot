$(document).ready(function () {
		
	$.ajax({
		type: 'POST',
		url: 'NodeInfoServlet',
		data: {type:'json'},
		dataType: 'json',
		success:function(data){
//			alert(data.nodeInfo[0].organization);
			var k = 0;
			for(i=0; i<data.nodeInfo.length; i++){
				for(j=i+1; j<data.nodeInfo.length; j++){
					var newRow = "<tr><th scope='row'>" + data.nodeInfo[i].host + "(" + data.nodeInfo[i].ip_address + ")" + 
					 "</th><th scope='row'>" + data.nodeInfo[j].host + "(" + data.nodeInfo[j].ip_address + ")" + 
					 "</th><td>" + data.positiveAvgArray[k] +
					 "</td><td>" + data.negativeAvgArray[k++] + "</td></tr>";
					$("#tableTBody tr:last").after(newRow);
				}			
			}			
			$('tbody tr').on('mouseenter', function() {
				 $(this).addClass('odd');
			}).on(' mouseleave', function() {
			  $(this).removeClass('odd');
			});
			var firstTr=document.getElementById("tableTBody");
			firstTr.deleteRow(0);
			$('tbody tr').on('click', function() {
				var index = $(this).index();
				window.open('graph.html?src='+data.nodeInfo[0].host+
									   '&dst='+data.nodeInfo[index+1].host +
									   '&srcIP='+data.nodeInfo[0].ip_address+
									   '&dstIP='+data.nodeInfo[index+1].ip_address);//这样点击每行，就能跳转到不同的地址了
				
			});
		}
	});

});