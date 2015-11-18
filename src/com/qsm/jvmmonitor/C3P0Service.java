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

import com.qsm.jvmmonitor.bean.C3P0BaseInfo;
import com.qsm.jvmmonitor.bean.C3P0MonitorInfo;
import com.qsm.jvmmonitor.utils.myRedis;

public class C3P0Service {
	
	private static List<String> data_source_name = new ArrayList<String>();
	
	public static void monitorC3P0(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		for(String dsn:data_source_name){
			String on = "com.mchange.v2.c3p0:type=PooledDataSource"+dsn;
			Integer num_connections = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"numConnections");
			Integer num_busy_connections = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"numBusyConnections");
			Integer thread_pool_size = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"threadPoolSize");
			Integer thread_pool_num_idle_threads = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"threadPoolNumIdleThreads");
			String name = (String)JMXCommonService.getValueFromJMXMBean(c,on,"user");
			C3P0MonitorInfo val = new C3P0MonitorInfo(name,num_connections,num_busy_connections,thread_pool_size,thread_pool_num_idle_threads);
			myRedis.saveC3P0Monitor(val);
		}
	}
	
	public static void initC3P0(JMXConnector c) throws MalformedObjectNameException, IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException{
		getName(c);
		for(String dsn:data_source_name){
			String on = "com.mchange.v2.c3p0:type=PooledDataSource"+dsn;
			Integer initial_pool_size = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"initialPoolSize");
			Integer min_pool_size = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"minPoolSize");
			Integer max_pool_size = (Integer)JMXCommonService.getValueFromJMXMBean(c,on,"maxPoolSize");
			String name = (String)JMXCommonService.getValueFromJMXMBean(c,on,"user");
			C3P0BaseInfo val = new C3P0BaseInfo(name,initial_pool_size,min_pool_size,max_pool_size);
			myRedis.saveC3P0Base(val);
		}
	}
	
	private static void getName(JMXConnector c) throws MalformedObjectNameException, IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException{
		String query="com.mchange.v2.c3p0:type=PooledDataSource*";
		Set<ObjectName> qr=JMXCommonService.queryNames(c,query);
		for(ObjectName on:qr){
			String dsn = (String)c.getMBeanServerConnection().getAttribute(on,"dataSourceName");
			data_source_name.add("["+dsn+"]");
		}
	}
}
