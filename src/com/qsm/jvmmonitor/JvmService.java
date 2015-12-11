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
		Long used_eden_mem = 0L;
		Long used_old_mem = 0L;
		Long used_perm_mem = 0L;
		Long used_surv_mem = 0L;
		
		if("ibm".equals(jvm_monitor_type)){
			cpuload = -1.0;
		}else if("sun".equals(jvm_monitor_type)){
			CompositeData cd1 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Eden Space","Usage");
			CompositeData cd2 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Old Gen","Usage");
			CompositeData cd3 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Perm Gen","Usage");
			CompositeData cd4 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Survivor Space","Usage");
			used_eden_mem = (Long)cd1.get("used");
			used_old_mem = (Long)cd2.get("used");
			used_perm_mem = (Long)cd3.get("used");
			used_surv_mem = (Long)cd4.get("used");
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
		
		JvmMonitorInfo val = new JvmMonitorInfo((Long)heapmem.get("used"),(Long)nonheapmem.get("used"),threadcount,cpuload,classcount,open_file_descriptor,used_eden_mem,used_old_mem,used_perm_mem,used_surv_mem);
		myRedis.saveJvmMonitor(val);
	}
	
	public static void initJVM(JMXConnector c) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		CompositeData cd = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Memory","HeapMemoryUsage");
		Long init_heap_mem = (Long) cd.get("init");
		Long max_heap_mem = (Long) cd.get("max");
		Long total_phy_mem = 0L;
		Long init_eden_mem = 0L;
		Long init_old_mem = 0L;
		Long init_perm_mem = 0L;
		Long init_surv_mem = 0L;
		Long max_eden_mem = 0L;
		Long max_old_mem= 0L;
		Long max_perm_mem = 0L;
		Long max_surv_mem = 0L;
		if("ibm".equals(jvm_monitor_type)){
			total_phy_mem = (Long)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=OperatingSystem","TotalPhysicalMemory");
		}else if("sun".equals(jvm_monitor_type)){
			CompositeData cd1 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Eden Space","Usage");
			CompositeData cd2 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Old Gen","Usage");
			CompositeData cd3 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Perm Gen","Usage");
			CompositeData cd4 = (CompositeData)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=MemoryPool,name=PS Survivor Space","Usage");
			init_eden_mem = (Long)cd1.get("init");
			max_eden_mem = (Long)cd1.get("max");
			init_old_mem = (Long)cd2.get("init");
			max_old_mem= (Long)cd2.get("max");
			init_perm_mem = (Long)cd3.get("init");
			max_perm_mem = (Long)cd3.get("max");
			init_surv_mem = (Long)cd4.get("init");
			max_surv_mem = (Long)cd4.get("max");
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
		
		JvmBaseInfo val = new JvmBaseInfo(init_heap_mem,max_heap_mem,total_phy_mem,total_phy_cpu,vm_name,vm_version,os_name,os_arch,os_version,max_file_descriptor,init_eden_mem,init_old_mem,init_perm_mem,init_surv_mem,max_eden_mem,max_old_mem,max_perm_mem,max_surv_mem);
		myRedis.saveJvmBase(val);
	}
	
	
}
