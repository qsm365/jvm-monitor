package com.qsm.jvmmonitor.bean;

public class BesMonitorInfo {
	public Integer http_total_threads;
	public Integer http_idle_threads;
	public Integer http_busy_threads;
	public Integer session_active;
	public Integer queued;
	
	public BesMonitorInfo(Integer http_total_threads,
			Integer http_idle_threads,
			Integer http_busy_threads,
			Integer session_active,
			Integer queued){
		this.http_total_threads=http_total_threads;
		this.http_idle_threads=http_idle_threads;
		this.http_busy_threads=http_busy_threads;
		this.session_active=session_active;
		this.queued=queued;
	}
	
	public BesMonitorInfo(){
		
	}
}
