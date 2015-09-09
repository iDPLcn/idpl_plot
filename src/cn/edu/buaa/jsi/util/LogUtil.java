package cn.edu.buaa.jsi.util;

import org.apache.log4j.Logger;


/**
 * 日志管理工具类
 * @author wk
 *
 */
public class LogUtil {
	private static Logger log = Logger.getLogger("highcharts");
	
	public static void debug(String message){
		log.debug(message);
	}
	
	public static void info(String message){
		log.debug(message);
	}
	
	public static void warn(String message){
		log.info(message);
	}
	
	public static void error(String message){
		log.error(message);
	}
	
	public static void fatal(String message){
		log.fatal(message);
	}
}
