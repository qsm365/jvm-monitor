package com.qsm.jvmmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import com.qsm.jvmmonitor.bean.JvmBaseInfo;
import com.qsm.jvmmonitor.bean.JvmMonitorInfo;
import com.qsm.jvmmonitor.utils.myRedis;
import com.qsm.jvmmonitor.JMXCommonService;

public class JvmService {
	
	public static String jvm_monitor_type="";
	public static List<String> jvm_monitor_content=new ArrayList<String>();
	
	public static void monitorJvm(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		CompositeData heapmem = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Memory", "HeapMemoryUsage");
		
		CompositeData nonheapmem = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Memory", "NonHeapMemoryUsage");
		
		Integer threadcount = (Integer)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Threading", "ThreadCount");
		
		Double cpuload = 0.0;
		if("ibm".equals(jvm_monitor_type)){
			cpuload = -1.0;
		}else if("sun".equals(jvm_monitor_type)){
			cpuload = -1.0;
		}
		//windows only (Double)getValueFromJMXMBean(c,"java.lang:type=OperatingSystem", "ProcessCpuLoad");
		
		Integer classcount = (Integer)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=ClassLoading", "LoadedClassCount");
		
		Long open_file_descriptor = -1L;
		if("ibm".equals(jvm_monitor_type)){
			open_file_descriptor = -1L;
		}else if("sun".equals(jvm_monitor_type)){
			open_file_descriptor = (Long)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem", "OpenFileDescriptorCount");
		}
		
		JvmMonitorInfo val = new JvmMonitorInfo((Long)heapmem.get("used"),(Long)nonheapmem.get("used"),threadcount,cpuload,classcount,open_file_descriptor);
		myRedis.saveJvmMonitor(val);
	}
	
	public static void initJVM(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		CompositeData cd = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Memory","HeapMemoryUsage");
		Long init_heap_mem = (Long) cd.get("init");
		Long max_heap_mem = (Long) cd.get("max");
		Long total_phy_mem = 0L;
		if("ibm".equals(jvm_monitor_type)){
			total_phy_mem = (Long)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","TotalPhysicalMemory");
		}else if("sun".equals(jvm_monitor_type)){
			total_phy_mem = (Long)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","TotalPhysicalMemorySize");
		}
		Integer total_phy_cpu = (Integer)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","AvailableProcessors");
		
		String vm_name = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Runtime","VmName");
		String vm_version = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Runtime","VmVersion");
		
		String os_name = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","Name");
		String os_arch = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","Arch");
		String os_version = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","Version");
		
		Long max_file_descriptor = -1L;
		if("ibm".equals(jvm_monitor_type)){
			max_file_descriptor = -1L;
		}else if("sun".equals(jvm_monitor_type)){
			max_file_descriptor = (Long)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem", "MaxFileDescriptorCount");
		}
		
		JvmBaseInfo val = new JvmBaseInfo(init_heap_mem,max_heap_mem,total_phy_mem,total_phy_cpu,vm_name,vm_version,os_name,os_arch,os_version,max_file_descriptor);
		myRedis.saveJvmBase(val);
	}
	
	
}
