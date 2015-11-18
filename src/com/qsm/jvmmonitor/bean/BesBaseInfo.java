package com.qsm.jvmmonitor.bean;
import java.util.ArrayList;
import java.util.List;


public class BesBaseInfo {
	//com.bes.appserv:type=protocolHandler,className=com.bes.enterprise.web.connector.crane.CraneHttpProtocol
	//port
	public Integer port;
	
	//com.bes.appserv:category=monitor,server=*,type=root
	//server
	public String instance_name;
	
	//com.bes.appserv:type=Engine
	//baseDir
	public String instance_basedir;
	
	//com.bes.appserv:type=ThreadPool,name=*
	//com.bes.appserv:type=Selector,name=*
	public String io_mode;
	
	public List<String> app_context = new ArrayList<String>();
	
	public Integer max_http_threads;
	
	public Integer max_queued;
	
	public Integer keepalive_timeout;

	public BesBaseInfo(
			Integer port,
			String instance_name,
			String instance_basedir,
			String io_mode,
			List<String> app_context,
			Integer max_http_threads,
			Integer max_queued,
			Integer keepalive_timeout){
		this.port = port;
		this.instance_name = instance_name;
		this.instance_basedir = instance_basedir;
		this.io_mode = io_mode;
		this.app_context = app_context;
		this.max_http_threads = max_http_threads;
		this.max_queued = max_queued;
		this.keepalive_timeout = keepalive_timeout;
	}
	
	public BesBaseInfo(){
		
	}
}
