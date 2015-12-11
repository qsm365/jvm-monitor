package com.qsm.jvmmonitor.bean;

public class JvmBaseInfo {
	//java.lang:type=Memory
	//MinHeapSize
	public Long init_heap_mem;
	
	//java.lang:type=Memory
	//MaxHeapSize
	public Long max_heap_mem;
	
	//java.lang:type=OperatingSystem
	//IBM	TotalPhysicalMemory
	//SUN	TotalPhysicalMemorySize
	public Long total_phy_mem;
	
	//java.lang:type=OperatingSystem
	//AvailableProcessors
	public Integer total_phy_cpu;
	
	//java.lang:type=Runtime
	//VmName
	public String vm_name;
	
	//java.lang:type=Runtime
	//VmVersion
	public String vm_version;
	
	//java.lang:type=OperatingSystem
	//Name
	public String os_name;
	
	//java.lang:type=OperatingSystem
	//Arch
	public String os_arch;
	
	//java.lang:type=OperatingSystem
	//Version
	public String os_version;
	
	//java.lang:type=OperatingSystem
	//MaxFileDescriptorCount
	public Long max_file_descriptor;
	
	public Long init_eden_mem;
	
	public Long init_old_mem;
	
	public Long init_perm_mem;
	
	public Long init_surv_mem;
	
	public Long max_eden_mem;
	
	public Long max_old_mem;
	
	public Long max_perm_mem;
	
	public Long max_surv_mem;

	
	public JvmBaseInfo(Long init_heap_mem,
			Long max_heap_mem,
			Long total_phy_mem,
			Integer total_phy_cpu,
			String vm_name,
			String vm_version,
			String os_name,
			String os_arch,
			String os_version,
			Long max_file_descriptor,
			Long init_eden_mem,
			Long init_old_mem,
			Long init_perm_mem,
			Long init_surv_mem,
			Long max_eden_mem,
			Long max_old_mem,
			Long max_perm_mem,
			Long max_surv_mem){
		this.init_heap_mem = init_heap_mem;
		this.max_heap_mem = max_heap_mem;
		this.total_phy_mem = total_phy_mem;
		this.total_phy_cpu = total_phy_cpu;
		this.vm_name = vm_name;
		this.vm_version = vm_version;
		this.os_name = os_name;
		this.os_arch = os_arch;
		this.os_version = os_version;
		this.max_file_descriptor = max_file_descriptor;
		this.init_eden_mem =init_eden_mem;
		this.init_old_mem =init_old_mem;
		this.init_perm_mem =init_perm_mem;
		this.init_surv_mem =init_surv_mem;
		this.max_eden_mem =max_eden_mem;
		this.max_old_mem =max_old_mem;
		this.max_perm_mem =max_perm_mem;
		this.max_surv_mem =max_surv_mem;
	}
	
	public JvmBaseInfo(){
		
	}
}
