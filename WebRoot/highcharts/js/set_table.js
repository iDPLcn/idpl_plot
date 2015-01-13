$(document).ready(function () {
		
	$.ajax({
		type: 'POST',
		url: 'NodeInfoServlet',
		data: {type:'json'},
		dataType: 'json',
		success:function(data){
			alert(data.nodeInfo[0].organization);
			for(i=1; i<data.nodeInfo.length; i++){
				var newRow = "<tr><th scope='row'>" + data.nodeInfo[0].host + "(" + data.nodeInfo[0].ip_address + ")" + 
								 "</th><th scope='row'>" +data.nodeInfo[i].host + "(" + data.nodeInfo[i].ip_address + ")" + 
								 "</th><td>25</td><td>21</td></tr>";
				$("#tableTBody tr:last").after(newRow);
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