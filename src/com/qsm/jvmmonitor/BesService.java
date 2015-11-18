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

import com.qsm.jvmmonitor.bean.BesBaseInfo;
import com.qsm.jvmmonitor.bean.BesMonitorInfo;
import com.qsm.jvmmonitor.utils.myRedis;

public class BesService {
	
	public static Integer app_port;
	public static String bes_io_mode;
	public static List<String> app_context;
	
	public static void initBES(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		Integer port = getBesPort(c);
		app_port = port;
		String instance_basedir = (String)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Engine","baseDir");
		String io_mode = getBesIOMode(c);
		bes_io_mode = io_mode;
		Integer max_http_threads = -1;
		Integer max_queued = -1;
		Integer keepalive_timeout = -1;
		if("nio".equals(bes_io_mode)){
			max_http_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Selector,name=http"+port,"maxThreads");
			keepalive_timeout = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=protocolHandler,className=com.bes.enterprise.web.connector.crane.CraneHttpProtocol","keepAliveTimeoutInSeconds");
			max_queued = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=PWCConnectionQueue,name=http"+port,"maxQueued");
		}else if("bio".equals(bes_io_mode)){
			max_http_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=ThreadPool,name=http"+port,"maxThreads");
			keepalive_timeout = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=protocolHandler,className=org.apache.coyote.http11.Http11Protocol","keepAliveTimeoutInSeconds")/1000;
		}
		
		String instance_name = getBesInstanceName(c);
		app_context = getBesAppContext(c);
		System.out.println("bes instance name:"+instance_name);
		System.out.println("in "+bes_io_mode+" mode");
		System.out.println("bes application context:");
		for(String ac:app_context){
			System.out.println("\t"+ac);
		}
		BesBaseInfo val = new BesBaseInfo(port,instance_name,instance_basedir,io_mode,app_context,max_http_threads,max_queued,keepalive_timeout);
		myRedis.saveBesBase(val);
	}
	
	
	
	public static void monitorBes(JMXConnector c) throws Exception{
				
		Integer http_total_threads=-1;
		Integer http_idle_threads=-1;
		Integer http_busy_threads=-1;
		Integer session_active=-1;
		Integer queued=-1;
		
		if("nio".equals(bes_io_mode)){
			http_total_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Selector,name=http"+app_port,"currentThreadCountStats");
			http_idle_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Selector,name=http"+app_port,"countThreadsIdleStats");
			http_busy_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Selector,name=http"+app_port,"currentThreadsBusyStats");
			queued = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=PWCConnectionQueue,name=http"+app_port,"countQueued");
		}else if("bio".equals(bes_io_mode)){
			http_total_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=ThreadPool,name=http"+app_port,"currentThreadCount");
			String[] threadStatus = (String[])JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=ThreadPool,name=http"+app_port,"threadStatus");
			http_idle_threads=0;
			http_busy_threads=0;
			
			for(String status:threadStatus){
				//System.out.println("status:"+status);
				if("ended".equals(status) || "service".equals(status) ||"parsing http request".equals(status)){
					http_busy_threads++;
				}else{
					http_idle_threads++;
				}
			}
		}
		int sa=0;
		for(String ac : app_context){
			sa+=(Integer)JMXCommonService.getValueFromJMXMBean(c,"com.bes.appserv:type=Manager,path="+ac+",host=server", "activeSessions");
		}
		session_active=sa;
		//System.out.println("http_total_threads:"+http_total_threads+"");
		//System.out.println("http_idle_threads:"+http_idle_threads+"");
		//System.out.println("http_busy_threads:"+http_busy_threads+"");
		//System.out.println("session_active:"+session_active+"");
		BesMonitorInfo val = new BesMonitorInfo(http_total_threads,http_idle_threads,http_busy_threads,session_active,queued);
		myRedis.saveBesMonitor(val);
	}
	
	
	
	private static String getBesInstanceName(JMXConnector c) throws MalformedObjectNameException, IOException{
		String query="com.bes.appserv:type=root,category=monitor,server=*";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		String result = "";
		for(ObjectName on:qr){
			result=on.getKeyProperty("server");
		}
		return result;
	}
	//org.apache.catalina.mbeans.ConnectorMBean
	private static Integer getBesPort(JMXConnector c) throws MalformedObjectNameException, IOException{
		String query="com.bes.appserv:type=Connector,port=*,address=*";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		Integer result = 0;
		for(ObjectName on:qr){
			result=Integer.parseInt(on.getKeyProperty("port"));
		}
		return result;
	}

	private static List<String> getBesAppContext(JMXConnector c) throws MalformedObjectNameException, IOException{
		String query="com.bes.appserv:type=Manager,path=*,host=server";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		List<String> result = new ArrayList<String>();
		for(ObjectName on:qr){
			if(!"/__wstx-services".equals(on.getKeyProperty("path")) && !"/".equals(on.getKeyProperty("path"))){
				result.add(on.getKeyProperty("path"));
			}
		}
		if(result.size()<1){
			result.add("/");
		}
		return result;
	}
	
	private static String getBesIOMode(JMXConnector c) throws MalformedObjectNameException, IOException{
		String query1="com.bes.appserv:type=Selector,name=*";
		String query2="com.bes.appserv:type=ThreadPool,name=*";
		Set<ObjectName> qr1=JMXCommonService.queryNames(c,query1);
		Set<ObjectName> qr2=JMXCommonService.queryNames(c,query2);
		String result = "";
		if(qr1.size()>0){
			result="nio";
		}else if(qr2.size()>0){
			result="bio";
		}
		return result;
	}
}
