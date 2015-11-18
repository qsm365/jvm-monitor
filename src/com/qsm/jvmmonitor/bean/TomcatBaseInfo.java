package com.qsm.jvmmonitor.bean;

import java.util.List;

public class TomcatBaseInfo {
	public String basedir;
	public String http_io_mode;
	public String ajp_io_mode;
	public Integer max_http_threads;
	public Integer max_ajp_threads;
	public Integer http_keepalive_timeout;
	public Integer http_port;
	public Integer ajp_port;
	public List<String> app_context;
	
	public TomcatBaseInfo(String basedir,
			String http_io_mode,
			String ajp_io_mode,
			Integer max_http_threads,
			Integer max_ajp_threads,
			Integer http_keepalive_timeout,
			Integer http_port,
			Integer ajp_port,
			List<String> app_context){
		this.basedir=basedir;
		this.http_io_mode=http_io_mode;
		this.ajp_io_mode=ajp_io_mode;
		this.max_http_threads=max_http_threads;
		this.max_ajp_threads=max_ajp_threads;
		this.http_keepalive_timeout=http_keepalive_timeout;
		this.http_port=http_port;
		this.ajp_port=ajp_port;
		this.app_context=app_context;
	}
	
	public TomcatBaseInfo(){
		
	}
}
