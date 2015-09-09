var networkPositiveAverage,enetworkNegativeAverage;
var linearray = new Array();
var realdata;
$(document).ready(function () {
		
	var time = Math.round((new Date()).getTime()/1000);
	var pretime = time -3600*24*7;
	var networkPositiveAverage =0;
	var i,j;
	$.ajax({
		type: 'POST',
		url: 'NodeInfoServlet',
		data: {type:'json'},
		dataType: 'json',
		success:function(jsondata){			
			var perfsonarPositiveAvg;
			var condorPositiveAvg;
//			var perfsonarNegativeAvg;
//			var condorNegativeAvg;
			var internationalNode = new Array();
			var domesticNode = new Array();
			for(i=0; i<jsondata.nodeInfo.length; i++){	
				var newRow = "<tr><th scope='row'>" + jsondata.nodeInfo[i].source.host + 
				"</th><th scope='row'>" + jsondata.nodeInfo[i].destination.host + "</th>";			
				perfsonarPositiveAvg = "N/A";
//				perfsonarNegativeAvg = "N/A";
				if(jsondata.perfsonarPositiveAvgArray[i] != null){
					perfsonarPositiveAvg = (jsondata.perfsonarPositiveAvgArray[i]/(1024*1024)).toFixed(2)+"Mbps";
				}				
				newRow += "<td>" + perfsonarPositiveAvg + "</td>";
				
				var k;
				for(k=0; k<jsondata.condorPositiveAvgArray.length; k++){
					if(jsondata.condorPositiveAvgArray[k].condorPositiveAvg[i] != null){
						newRow += "<td>" + (jsondata.condorPositiveAvgArray[k].condorPositiveAvg[i]/(1024*1024)).toFixed(2)+"Mbps" +"</td>";
					}else{
						newRow += "<td>N/A</td>";
					}
				}
				newRow += "</tr>";
				if(jsondata.nodeInfo[i].source.pool_no == 1){
					$("#tableTBody_International tr:last").after(newRow);
					internationalNode.push(jsondata.nodeInfo[i]);
				}else{
					$("#tableTBody_Domestic tr:last").after(newRow);
					domesticNode.push(jsondata.nodeInfo[i]);
				}
				
			}
			var firstTr=document.getElementById("tableTBody_International");
			firstTr.deleteRow(0);
			firstTr=document.getElementById("tableTBody_Domestic");
			firstTr.deleteRow(0);
			$('tbody tr').on('mouseenter', function() {
				 $(this).addClass('odd');
			}).on(' mouseleave', function() {
			  $(this).removeClass('odd');
			});
			$('#tableTBody_International tr').on('click', function() {
				var index = $(this).index();
				window.open('graph.html?src='+internationalNode[index].source.host+
									   '&dst='+internationalNode[index].destination.host +
									   '&srcIP='+internationalNode[index].source.ip_address+
									   '&dstIP='+internationalNode[index].destination.ip_address);//这样点击每行，就能跳转到不同的地址了
				
			});
			$('#tableTBody_Domestic tr').on('click', function() {
				var index = $(this).index();
				window.open('graph.html?src='+domesticNode[index].source.host+
									   '&dst='+domesticNode[index].destination.host +
									   '&srcIP='+domesticNode[index].source.ip_address+
									   '&dstIP='+domesticNode[index].destination.ip_address);//这样点击每行，就能跳转到不同的地址了
				
			});
		}
	});

});

