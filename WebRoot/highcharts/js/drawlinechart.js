$(function () {
	
	function GetQueryString(name)
	{
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if(r!=null)return  unescape(r[2]); return null;
		
	}
	$('#container').html('<h4>waiting for data...</h4>');
	$.ajax({
		type: 'POST',
		url: 'PerfsonarServlet',
		data: {type:GetQueryString("type"),src:GetQueryString("src"),dst:GetQueryString("dst"),time_start:GetQueryString("time_start"),time_end:GetQueryString("time_end")},
		dataType: 'json',
		success:function(data){
			var loss = new Array(data.loss.length);
			var owdelay = new Array(data.owdelay.length);
			var ping = new Array(data.ping.length);
			var throughput = new Array(data.throughput.length);
			var subtitleString = "source:"+GetQueryString("src") +" -- destination:"+GetQueryString("dst");
			for(var i=0; i<data.loss.length; i++){
				loss[i] = new Array(parseInt(data.loss[i].x_value)*1000, parseFloat(data.loss[i].y_value)*100);       		
			}
			for(var i=0; i<data.owdelay.length; i++){
				owdelay[i] = new Array(parseInt(data.owdelay[i].x_value)*1000, parseFloat(data.owdelay[i].y_value));        	}
			for(var i=0; i<data.ping.length; i++){
				ping[i] = new Array(parseInt(data.ping[i].x_value)*1000, parseFloat(data.ping[i].y_value));
			}
			for(var i=0; i<data.throughput.length; i++){
				throughput[i] = new Array(parseInt(data.throughput[i].x_value)*1000,parseFloat(data.throughput[i].y_value));
			}
			Highcharts.setOptions({
				lang: {
					numericSymbols: ["K" , "M" , "G" , "T" , "P" , "E"]
				},
				global: { useUTC: false  }
			});	
			
			if(loss.length==0 && owdelay.length==0 && ping.length==0 && throughput.length==0){
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
						text: 'iDPL network status',
						x: -20 //center
					},
					subtitle: {
						text: subtitleString,
						x: -20 //center
					},
					xAxis: {
						type: 'datetime',
						tickPixelInterval: 100
					},
					yAxis: [{
						title: {
							text: 'Throughput(bps)',
							rotation:90
						},
						lineWidth : 1,
						lineColor:'#28FF28'
					},{
						title: {
							text: 'Latency(ms)',
							rotation:90
						},
//						gridLineWidth:0,
						lineWidth : 1,
						min:0,
						opposite:true
					},{
						title: {
							text: 'LOSS(%)',
							rotation:90
						},
//						gridLineWidth:0,
						lineWidth : 1,
						lineColor:'#0080FF',
						max: 100,
						min:0,
						maxPadding: 0,
						endOnTick:false,
						opposite:true
					}],
					tooltip: {
						formatter: function () {
							var unit;
							switch (this.series.name) {
							case 'throughput':
								unit = 'bps';
								break;
							case 'loss':
								unit = '%';
								break;
							default:
								unit = 'ms';
								break;
							}						
							return '<b>' + this.series.name + '</b><br/>' +
							Highcharts.dateFormat('%Y-%m-%d', this.x) + '<br/>' +
							Highcharts.dateFormat('%H:%M:%S', this.x) + '<br/>' +
							Highcharts.numberFormat(this.y, 2) + unit;
						}
					},
					plotOptions: {
						series: {
							marker: {
								radius: 2,  //曲线点半径，默认是4
								lineColor: null // inherit from series
							}
						}

					},
					legend: {
						enabled: true
					},
					series:[{
						data: throughput,
						name:'throughput',
						color:'#28FF28',
						yAxis:0
					},{
						data: owdelay,
						name:'owdelay',
						color:'#FF5809',
						yAxis:1
					},{
						data: ping,
						name:'ping',
						color:'#AE57A4',
						yAxis:1
					},{
						data: loss,
						name:'loss',
						color:'#0080FF',
						yAxis:2
					}]
				});
			}
		}
	});
});