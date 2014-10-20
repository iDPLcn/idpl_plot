package com.classes
{
	[Bindable]
	[RemoteClass(alias="cn.edu.buaa.jsi.psmanager.OwampPoint")]
	public class OwampPoint
	{
		public var unixtime:Number;
		public var minDelay:Number;
		public var maxError:Number;
		public var maxDelay:Number;
		public var duplicates:Number;
		public var loss:Number;
	}
}