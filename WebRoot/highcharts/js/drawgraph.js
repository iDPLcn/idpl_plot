var srcNode;
var dstNode;
var time;
var pretime;
var timeRange;//标记当前时间区间：1d/3d/1w/1m/1y

$(function () {
	
	function GetQueryString(name)
	{
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if(r!=null)return  unescape(r[2]); return null;
		
	}
	srcNode=GetQueryString("src");
	dstNode=GetQueryString("dst");
	srcIP=GetQueryString("srcIP");
	dstIP=GetQueryString("dstIP");
    $('#source_host_0').text(srcNode);
    $('#dest_host_0').text(dstNode);
    $('#source_ip_0').text(srcIP);
    $('#dest_ip_0').text(dstIP);
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*7;
	setTime();
	timeRange = 7;
	$('#1wLink').css('color', 'red');
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
});

function drawGraph(data) {
	var transferTime = data.transferTime;
	var dataArray = new Array(transferTime.length);
	for(var i=0; i<transferTime.length; i++){
		dataArray[i] = new Array(parseInt(transferTime[i].time_end)*1000, parseFloat(transferTime[i].duration));       		
	}
	Highcharts.setOptions({
		lang: {
			numericSymbols: ["K" , "M" , "G" , "T" , "P" , "E"]
		},
		global: { useUTC: false  }
	});	
	
	if(transferTime.length==0){
		$('#container').html('<h3>the chart is not ready</h3><br/>'+
				'<h5>the reason might be:</h5><br/>'+
				'<a>&emsp;1.Transmission time is too short</a><br/>'+
				'<a>&emsp;2.The value of measurement is invalid</a><br/><br/>'+
				'<a>If you have any question, please contact administrator.</a>');
	}else{
		var chartobj = new Highcharts.Chart({
			chart: {
				zoomType: 'x',
				renderTo: container,
				alignTicks:true
			},
			title: {
	            text: ''
	        },
			xAxis: {
				type: 'datetime',
				min:pretime*1000,
				max:time*1000,
				tickPixelInterval: 100
			},
			yAxis: [{
				title: {
					text: 'Latency(ms)',
					rotation:90
				},
//				gridLineWidth:0,
				lineWidth : 1,
				min:0,
			}],
			tooltip: {
				formatter: function () {
					var unit = 'ms';					
					return '<b>' + this.series.name + '</b><br/>' +
					Highcharts.dateFormat('%Y-%m-%d', this.x) + '<br/>' +
					Highcharts.dateFormat('%H:%M:%S', this.x) + '<br/>' +
					Highcharts.numberFormat(this.y, 2) + unit;
				}
			},
			plotOptions: {
				series: {
					marker: {
						radius: 2,  //���ߵ�뾶��Ĭ����4
						lineColor: null // inherit from series
					}
				}
			},
			legend: {
				enabled: true
			},
			series:[{
				data: dataArray,
				name:'Latency',
				color:'#28FF28',
				yAxis:0
			}]
		});
	}
}

function get1dLink() {
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*1;
	setTime();
	timeRange = 1;
	resetColor();
	$('#1dLink').css('color', 'red');
	$('#previous').text("Previous 1d");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
}

function get3dLink() {
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*3;
	setTime();
	timeRange = 3;
	resetColor();
	$('#3dLink').css('color', 'red');
	$('#previous').text("Previous 3d");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
}

function get1wLink() {
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*7;
	setTime();
	timeRange = 7;
	resetColor();
	$('#1wLink').css('color', 'red');
	$('#previous').text("Previous 1w");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
}

function get1mLink() {
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*30;
	setTime();
	timeRange = 30;
	resetColor();
	$('#1mLink').css('color', 'red');
	$('#previous').text("Previous 1m");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
}

function get1yLink() {
	$('#container').html('<h4>waiting for data...</h4>');
	time = Math.round((new Date()).getTime()/1000);
	pretime = time -3600*24*365;
	setTime();
	timeRange = 365;
	resetColor();
	$('#1yLink').css('color', 'red');
	$('#previous').text("Previous 1y");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
}

function previous() {
	time = pretime;
	pretime = time -3600*24*timeRange;
	setTime();
	$('#previous').css('color', 'red');
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});	
}

function resetColor() {
	 $('#1dLink').css('color', 'blue');
	 $('#3dLink').css('color', 'blue');
	 $('#1wLink').css('color', 'blue');
	 $('#1mLink').css('color', 'blue');
	 $('#1yLink').css('color', 'blue');
	 $('#previous').css('color', 'blue');
}

function setTime() {
	$('#graphTimeRange').text((new Date(pretime*1000)).toString() + " —— " + (new Date(time*1000)).toString());
}