package com.qsm.jvmmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import com.qsm.jvmmonitor.bean.TomcatBaseInfo;
import com.qsm.jvmmonitor.bean.TomcatMonitorInfo;
import com.qsm.jvmmonitor.utils.myRedis;

public class TomcatService {
	
	public static Integer http_port;
	public static Integer ajp_port;
	public static String http_io_mode;
	public static String ajp_io_mode;
	public static List<String> app_context=new ArrayList<String>();
	public static List<String> app_host=new ArrayList<String>();
	
	public static void initTomcat(JMXConnector c) throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException{
		getTomcatPort(c);
		String basedir = (String)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=Engine","baseDir");
		http_io_mode=getTomcatHTTPIOMode(c);
		ajp_io_mode=getTomcatAJPIOMode(c);
		Integer max_http_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"http-"+http_io_mode+"-"+http_port+"\"","maxThreads");
		Integer max_ajp_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"ajp-"+ajp_io_mode+"-"+ajp_port+"\"","maxThreads");
		Integer http_keepalive_timeout = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"http-"+http_io_mode+"-"+http_port+"\"","keepAliveTimeout");
		getTomcatAppContext(c);
		
		System.out.println("tomcat install in "+basedir);
		System.out.println("http running in "+http_io_mode+" mode,max_threads:"+max_http_threads+",keepalive_timeout:"+http_keepalive_timeout);
		System.out.println("ajp running in "+ajp_io_mode+" mode,max_threads:"+max_ajp_threads);
		System.out.println("app context:");
		for(String ac : app_context){
			System.out.println("\t"+ac);
		}
		TomcatBaseInfo val = new TomcatBaseInfo(basedir,http_io_mode,ajp_io_mode,max_http_threads,max_ajp_threads,http_keepalive_timeout,http_port,ajp_port,app_context);
		myRedis.saveTomcatBase(val);
	}
	
	public static void monitorTomcat(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		Integer http_total_threads=-1;
		Integer http_busy_threads=-1;
		Integer ajp_total_threads=-1;
		Integer ajp_busy_threads=-1;
		Integer session_active=-1;
		http_total_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"http-"+http_io_mode+"-"+http_port+"\"","currentThreadCount");
		http_busy_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"http-"+http_io_mode+"-"+http_port+"\"","currentThreadsBusy");
		
		ajp_total_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"ajp-"+ajp_io_mode+"-"+ajp_port+"\"","currentThreadCount");
		ajp_busy_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=ThreadPool,name=\"ajp-"+ajp_io_mode+"-"+ajp_port+"\"","currentThreadsBusy");
		
		int sa=0;
		for(int i=0;i<app_context.size();i++){
			sa+=(Integer)JMXCommonService.getValueFromJMXMBean(c,"Catalina:type=Manager,context="+app_context.get(i)+",host="+app_host.get(i),"activeSessions");
		}
		session_active=sa;
		//System.out.println("http_total_threads"+http_total_threads);
		//System.out.println("http_busy_threads"+http_busy_threads);
		//System.out.println("ajp_total_threads"+ajp_total_threads);
		//System.out.println("ajp_busy_threads"+ajp_busy_threads);
		//System.out.println("session_active"+session_active);
		TomcatMonitorInfo val = new TomcatMonitorInfo(http_total_threads,http_busy_threads,ajp_total_threads,ajp_busy_threads,session_active);
		myRedis.saveTomcatMonitor(val);
	}
	private static void getTomcatPort(JMXConnector c) throws MalformedObjectNameException, IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException{
		String query="Catalina:type=Connector,port=*";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		
		for(ObjectName on:qr){
			String protocol = (String)c.getMBeanServerConnection().getAttribute(on, "protocol");
			if(protocol.startsWith("HTTP/")){
				http_port=Integer.parseInt(on.getKeyProperty("port"));
			}
			if(protocol.startsWith("AJP/")){
				ajp_port=Integer.parseInt(on.getKeyProperty("port"));
			}
		}
	}
	private static String getTomcatHTTPIOMode(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		String query="Catalina:type=Connector,port="+http_port;
		String phc = (String)JMXCommonService.getValueFromJMXMBean(c,query,"protocolHandlerClassName");
		String result = "";
		if("org.apache.coyote.http11.Http11Protocol".equals(phc)){
			result="bio";
		}else if("org.apache.coyote.http11.Http11NioProtocol".equals(phc)){
			result="nio";
		}
		return result;
	}
	private static String getTomcatAJPIOMode(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		String query="Catalina:type=Connector,port="+ajp_port;
		String phc = (String)JMXCommonService.getValueFromJMXMBean(c,query,"protocolHandlerClassName");
		String result = "";
		if("org.apache.coyote.ajp.AjpProtocol".equals(phc)){
			result="bio";
		}else if("org.apache.coyote.ajp.AjpNioProtocol".equals(phc)){
			result="nio";
		}
		return result;
	}
	private static void getTomcatAppContext(JMXConnector c) throws MalformedObjectNameException, IOException{
		String query="Catalina:type=Manager,context=*,host=*";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		for(ObjectName on:qr){
			app_context.add(on.getKeyProperty("context"));
			app_host.add(on.getKeyProperty("host"));
		}
	}
}
