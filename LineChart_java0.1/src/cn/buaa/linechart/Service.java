package cn.buaa.linechart;

import java.util.*;

import cn.edu.buaa.jsi.psmanager.*;

public class Service {

//	List<Point> point_list = new ArrayList<Point>();

	List<BwctlPoint> bwctlPoint = new ArrayList<BwctlPoint>();
	List<OwampPoint> owampPoint = new ArrayList<OwampPoint>();
	List<PingerPoint> pingerPoint = new ArrayList<PingerPoint>();
	
	public List<BwctlPoint> setBwctlPointList(
			String bwctlHost,String srcIP, String destIP, long bwctlTimeStart, long bwctlTimeEnd){

//		bwctlHost="http://10.4.9.202:8085/perfSONAR_PS/services/pSB";
		PSConnector bwctlCon = new PSConnector(bwctlHost, srcIP, destIP, bwctlTimeStart, bwctlTimeEnd);
		bwctlPoint = bwctlCon.getBwctlDataPoint();
		System.out.println(bwctlPoint.size());
		return bwctlPoint;
	}
	
	public List<OwampPoint> setOwampPointList(
			String owampHost,String srcIP, String destIP, long owampTimeStart, long owampTimeEnd){

//		owampHost="http://10.4.9.202:8085/perfSONAR_PS/services/pSB";
		PSConnector owampConn = new PSConnector(
				owampHost, srcIP, destIP, owampTimeStart, owampTimeEnd);
		owampPoint = owampConn.getOwampDataPoint();
		System.out.println(owampPoint.size());
		return owampPoint;		
	}
	
	public List<PingerPoint> setPingerPointList(
			String pingerHost, String srcIP, String destIP, long  pingerTimeStart, long pingerTimeEnd){

//		pingerHost="http://115.25.138.244:8075/perfSONAR_PS/services/pinger/ma";
		PSConnector pingerConn = new PSConnector(
				pingerHost, srcIP, destIP, pingerTimeStart, pingerTimeEnd);
		pingerPoint = pingerConn.getPingerDataPoint();
		System.out.println(pingerPoint.size());
//		for(int i=0; i<pingerPoint.size(); i++){
//			pingerPoint.get(i).printPoint();
//		}
//		
		return pingerPoint;		
	}
}
