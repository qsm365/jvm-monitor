package com.qsm.jvmmonitor.bean;

public class TomcatMonitorInfo {
	public Integer http_total_threads;
	public Integer http_busy_threads;
	public Integer ajp_total_threads;
	public Integer ajp_busy_threads;
	public Integer session_active;
	
	public TomcatMonitorInfo(Integer http_total_threads,
		Integer http_busy_threads,
		Integer ajp_total_threads,
		Integer ajp_busy_threads,
		Integer session_active){
		this.http_total_threads=http_total_threads;
		this.http_busy_threads=http_busy_threads;
		this.ajp_total_threads=ajp_total_threads;
		this.ajp_busy_threads=ajp_busy_threads;
		this.session_active=session_active;
	}
	
	public TomcatMonitorInfo(){
		
	}
}
