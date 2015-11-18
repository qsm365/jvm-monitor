package com.qsm.jvmmonitor.bean;

public class C3P0BaseInfo {

	public String name;
	public Integer initial_pool_size;
	public Integer min_pool_size;
	public Integer max_pool_size;
	
	public C3P0BaseInfo(
			String name,
			Integer initial_pool_size,
			Integer min_pool_size,
			Integer max_pool_size){
		this.name=name;
		this.initial_pool_size=initial_pool_size;
		this.min_pool_size=min_pool_size;
		this.max_pool_size=max_pool_size;
	}
	
	public C3P0BaseInfo(){
		
	}
	
}
