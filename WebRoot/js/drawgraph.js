var srcNode;
var dstNode;
var srcIP;
var dstIP;
var time;
var pretime;
var timeRange;//标记当前时间区间：1d/3d/1w/1m/1y
var colors = ['#F61404', '#0FDD3D', '#1931D2', '#F943F3', '#010209', '#BE9F7E', '#AA46F6', '#92A8CD', '#A47D7C', '#E38559', '#5CD1E0'];
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
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
		}
	});
});

function drawGraph(data) {
	var allTransferTime = data.allTransferTime;
	var perfsonarThroughput = data.perfsonarThroughput;
	var noData = 0;
	for(var i=0; i<allTransferTime.length; i++){
		if(allTransferTime[i].transferTime.length != 0)
			noData=1;
	}
	if(noData==0 && perfsonarThroughput.length==0){
		$('#container').html('<h3>There is no data at this time</h3><br/>'+
				'<a>If you have any question, please contact administrator.</a>');
	}else{
		
		Highcharts.setOptions({
			lang: {
				numericSymbols: ["K" , "M" , "G" , "T" , "P" , "E"]
			},
			global: { useUTC: false  }
		});	
		var mychart;
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
				min:pretime*1000-3600000,
				max:time*1000+3600000,
				tickPixelInterval: 100
			},
			yAxis: [{
				title: {
					text: 'Throughput(bps)',
					rotation:270
				},
//				gridLineWidth:0,
				lineWidth : 1,
				min:0,
			}],
			tooltip: {
				formatter: function () {
					var unit = 'Mbps';		
					if(this.series.name == 'perfsonar(scatter)' || this.series.name == 'perfsonar(line)'){
						return '<b>' + this.series.name + '</b><br/>' +
						Highcharts.dateFormat('%Y-%m-%d', this.x) + '<br/>' +
						Highcharts.dateFormat('%H:%M:%S', this.x) + '<br/>' +
						Highcharts.numberFormat(this.y/(1024*1024), 2) + unit;
					}else{
						return '<b>' + this.series.name + '</b><br/>' +
						Highcharts.dateFormat('%Y-%m-%d', this.x) + '<br/>' +
						Highcharts.dateFormat('%H:%M:%S', this.x) + '<br/>' +
						Highcharts.numberFormat(this.y/(1024*1024), 2) + unit;
					}
				}
			},
			plotOptions: {
				series: {
					marker: {
						radius: 3,  //���ߵ�뾶��Ĭ����4
						lineColor: null // inherit from series
					},
					turboThreshold:1500000
				}
			},

			legend: {
				enabled: true,
				itemWidth: 350,
				
			},
			series:[]
		});
		var index = 0;//表示添加到chart的series下标
		var allTransferTimeLength = allTransferTime.length;
		for(var k=0; k<allTransferTimeLength; k++){
			var transferTime = allTransferTime[k].transferTime;
			
			var dataArray_transferTime;				//线段
			var dataArrayMiddlePoint_transferTime = new Array(transferTime.length);//中间点
			if(transferTime.length != 0){
				dataArray_transferTime = new Array(transferTime.length*3-1);
				var dataArray_transferTimeLength = dataArray_transferTime.length;
				for(var i=0,j=0; i<dataArray_transferTimeLength; i+=3,j++){
					var transferTimeBandwidth = parseFloat(transferTime[j].bandwidth);
					if(parseInt(transferTime[j].duration) != 0 && transferTimeBandwidth < 10000000000.00 ){
						dataArray_transferTime[i] = new Array(parseInt(transferTime[j].time_start)*1000,transferTimeBandwidth);
						dataArray_transferTime[i+1] = new Array(parseInt(transferTime[j].time_end)*1000,transferTimeBandwidth);
						if(i+2<dataArray_transferTimeLength){
							dataArray_transferTime[i+2] = null;
						}			
						dataArrayMiddlePoint_transferTime[j] = new Array(parseInt(transferTime[j].time_start+transferTime[j].time_end)*500, transferTimeBandwidth);
					}		
				}
				
				chartobj.addSeries({
					name: transferTime[0].measurement.tool_name + '(time interval)',
					color: colors[k%10],
					data: dataArray_transferTime,
					yAxis: 0,
				});
				chartobj.addSeries({
					name: transferTime[0].measurement.tool_name + '(throughput-scatter)',
					type: 'scatter',
					color: colors[k%10],
					data: dataArrayMiddlePoint_transferTime,
					yAxis: 0,
				});
				chartobj.addSeries({
					name: transferTime[0].measurement.tool_name + '(throughput-line)',
					color: colors[k%10],
					data: dataArrayMiddlePoint_transferTime,
					yAxis: 0,
				});
				chartobj.series[3*index].setData(dataArray_transferTime);
				chartobj.series[3*index+1].setData(dataArrayMiddlePoint_transferTime);
				chartobj.series[3*index+2].setData(dataArrayMiddlePoint_transferTime);
				chartobj.series[3*index+2].hide();
				if(index != 0){
					chartobj.series[3*index].hide();
					chartobj.series[3*index+1].hide();
				}
				index++;
			}	
		}
		
		var perfsonarDataArray = new Array(perfsonarThroughput.length);
		var perfsonarThroughputLength = perfsonarThroughput.length;
		for(var i=0; i<perfsonarThroughputLength; i++){
			if(parseFloat(perfsonarThroughput[i].y_value) < 10000000000.00){
				perfsonarDataArray[i] = new Array(parseInt(perfsonarThroughput[i].x_value)*1000, parseFloat(perfsonarThroughput[i].y_value));
			}
			       		
		}
		
		if(perfsonarThroughput.length != 0){
			chartobj.addSeries({
				name: 'perfsonar(scatter)',
				color: colors[10],
				type: 'scatter',
				data: perfsonarDataArray,
				yAxis: 0,
			});
			chartobj.addSeries({
				name: 'perfsonar(line)',
				color: colors[10],
				data: perfsonarDataArray,
				yAxis: 0,
			});
			chartobj.series[chartobj.series.length-1].hide();
		}
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
	$('#next').text("Next 1d");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP,  time_start:pretime, time_end:time},
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
	$('#next').text("Next 3d");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP,  time_start:pretime, time_end:time},
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
	$('#next').text("Next 1w");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP, time_start:pretime, time_end:time},
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
	$('#next').text("Next 1m");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP,  time_start:pretime, time_end:time},
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
	$('#next').text("Next 1y");
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP, time_start:pretime, time_end:time},
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
	$('#next').css('color', 'blue');
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
			$('#next').show();
		}
	});	
}

function next() {
	
	pretime = time ;
	time = pretime+3600*24*timeRange;
	setTime();
	$('#next').css('color', 'red');
	$('#previous').css('color', 'blue');
	$.ajax({
		type: 'POST',
		url: 'GetTransferTimeServlet',
		data: {src:srcNode, dst:dstNode, srcip:srcIP, dstip:dstIP, time_start:pretime, time_end:time},
		dataType: 'json',
		success:function(data){
			drawGraph(data);
			if(time > Math.round((new Date()).getTime()/1000)){
				$('#next').hide();
			}
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
	 $('#next').css('color', 'blue');
}

function getRandomColor(){
	return '#'+(Math.random()*0xffffff<<0).toString(16); 
}

function setTime() {
	$('#graphTimeRange').text((new Date(pretime*1000)).toString() + " —— " + (new Date(time*1000)).toString());
}