package com.qsm.jvmmonitor.bean;

public class C3P0MonitorInfo {
	public String name;
	public Integer num_connections;
	public Integer num_busy_connections;
	public Integer thread_pool_size;
	public Integer thread_pool_num_idle_threads;
	
	public C3P0MonitorInfo(
			String name,
			Integer num_connections,
			Integer num_busy_connections,
			Integer thread_pool_size,
			Integer thread_pool_num_idle_threads
			){
		this.name=name;
		this.num_connections=num_connections;
		this.num_busy_connections=num_busy_connections;
		this.thread_pool_size=thread_pool_size;
		this.thread_pool_num_idle_threads=thread_pool_num_idle_threads;
	}
	
	public C3P0MonitorInfo(){
		
	}
}
