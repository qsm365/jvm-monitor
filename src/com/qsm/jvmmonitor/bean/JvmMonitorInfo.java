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
	
	public Long used_eden_mem;
	
	public Long used_old_mem;
	
	public Long used_perm_mem;
	
	public Long used_surv_mem;
	
	public JvmMonitorInfo(){
		
	}
	
	public JvmMonitorInfo(Long used_heap_mem,
			Long used_nonheap_mem,
			Integer thread_count,
			Double cpu_load,
			Integer class_count,
			Long open_file_descriptor,
			Long used_eden_mem,
			Long used_old_mem,
			Long used_perm_mem,
			Long used_surv_mem
			){
		this.used_heap_mem=used_heap_mem;
		this.used_nonheap_mem=used_nonheap_mem;
		this.thread_count=thread_count;
		this.cpu_load=cpu_load;
		this.class_count=class_count;
		this.open_file_descriptor=open_file_descriptor;
		this.used_eden_mem=used_eden_mem;
		this.used_old_mem=used_old_mem;
		this.used_perm_mem=used_perm_mem;
		this.used_surv_mem=used_surv_mem;
	}
}
