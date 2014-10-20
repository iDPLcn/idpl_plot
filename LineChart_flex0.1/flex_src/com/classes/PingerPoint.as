package com.classes
{
	[Bindable]
	[RemoteClass(alias="cn.edu.buaa.jsi.psmanager.PingerPoint")]
	public class PingerPoint
	{
		public var unixtime:Number;
		public var minRtt:Number;
		public var maxRtt:Number;
		public var medianRtt:Number;
		public var meanRtt:Number;
		public var iqrIpd:Number;
		public var maxIpd:Number;
		public var meanIpd:Number;
	}
}