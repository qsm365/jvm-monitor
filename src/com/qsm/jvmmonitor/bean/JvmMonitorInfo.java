package com.qsm.jvmmonitor.bean;

public class JvmMonitorInfo {
	//java.lang:type=Memory
	//HeapMemoryUsage
	//used
	public Long used_heap_mem;
	
	//java.lang:type=Memory
	//NonHeapMemoryUsage
	//used
	public Long used_nonheap_mem;
	
	//java.lang:type=Threading
	//ThreadCount
	public Integer thread_count;
	
	//java.lang:type=OperatingSystem
	//ProcessCpuLoad
	//Windows ONLY
	public Double cpu_load;
	
	//java.lang:type=ClassLoading
	//LoadedClassCount
	public Integer class_count;
	
	//java.lang:type=OperatingSystem
	//OpenFileDescriptorCount
	public Long open_file_descriptor;
	
	public JvmMonitorInfo(){
		
	}
	
	public JvmMonitorInfo(Long used_heap_mem,
			Long used_nonheap_mem,
			Integer thread_count,
			Double cpu_load,
			Integer class_count,
			Long open_file_descriptor){
		this.used_heap_mem=used_heap_mem;
		this.used_nonheap_mem=used_nonheap_mem;
		this.thread_count=thread_count;
		this.cpu_load=cpu_load;
		this.class_count=class_count;
		this.open_file_descriptor=open_file_descriptor;
	}
}
