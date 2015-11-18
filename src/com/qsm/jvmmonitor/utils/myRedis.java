package com.qsm.jvmmonitor.utils;
import com.qsm.jvmmonitor.bean.BesBaseInfo;
import com.qsm.jvmmonitor.bean.BesMonitorInfo;
import com.qsm.jvmmonitor.bean.C3P0BaseInfo;
import com.qsm.jvmmonitor.bean.C3P0MonitorInfo;
import com.qsm.jvmmonitor.bean.JvmBaseInfo;
import com.qsm.jvmmonitor.bean.JvmMonitorInfo;
import com.qsm.jvmmonitor.bean.TomcatBaseInfo;
import com.qsm.jvmmonitor.bean.TomcatMonitorInfo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class myRedis {

	private static String host;
	private static Integer port;
	private static String redishost;
	private static Integer redisport;
	
	public static void init(String host,Integer port,String redishost,Integer redisport){
		myRedis.host = host;
		myRedis.port = port;
		myRedis.redishost = redishost;
		myRedis.redisport = redisport;
	}
	
	public static void saveC3P0Base(C3P0BaseInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		try{
			String key=host+":"+port+"-c3p0base";
			String value=val.name+";"
						+val.initial_pool_size+";"
						+val.min_pool_size+";"
						+val.max_pool_size;
			j.lpush(key,value);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveTomcatBase(TomcatBaseInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		try{
			String key=host+":"+port+"-tomcatbase";
			j.hset(key,"basedir" ,val.basedir);
			j.hset(key,"http_io_mode" ,val.http_io_mode);
			j.hset(key,"ajp_io_mode" ,val.ajp_io_mode);
			j.hset(key,"max_http_threads" ,val.max_http_threads+"");
			j.hset(key,"max_ajp_threads" ,val.max_ajp_threads+"");
			j.hset(key,"http_keepalive_timeout" ,val.http_keepalive_timeout+"");
			j.hset(key,"http_port" ,val.http_port+"");
			j.hset(key,"ajp_port" ,val.ajp_port+"");
			String v = "";
			for(String ac:val.app_context){
				v+=ac+";";
			}
			j.hset(key,"app_context" ,v);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveBesBase(BesBaseInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		try{
			String key=host+":"+port+"-besbase";
			j.hset(key,"port" ,val.port+"");
			j.hset(key,"instance_basedir" ,val.instance_basedir);
			j.hset(key,"io_mode" ,val.io_mode);
			j.hset(key,"max_http_threads" ,val.max_http_threads+"");
			j.hset(key,"max_queued" ,val.max_queued+"");
			j.hset(key,"keepalive_timeout" ,val.keepalive_timeout+"");
			j.hset(key,"instance_name" ,val.instance_name);
			String v = "";
			for(String ac:val.app_context){
				v+=ac+";";
			}
			j.hset(key,"app_context" ,v);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveJvmBase(JvmBaseInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		try{
			String key=host+":"+port+"-jvmbase";
			j.hset(key,"max_heap_mem" ,val.max_heap_mem+"");
			j.hset(key,"init_heap_mem" ,val.init_heap_mem+"");
			j.hset(key,"total_phy_mem" ,val.total_phy_mem+"");
			j.hset(key,"total_phy_cpu" ,val.total_phy_cpu+"");
			j.hset(key,"vm_name" ,val.vm_name);
			j.hset(key,"vm_version" ,val.vm_version);
			j.hset(key,"os_name" ,val.os_name);
			j.hset(key,"os_arch" ,val.os_arch);
			j.hset(key,"os_version" ,val.os_version);
			j.hset(key,"max_file_descriptor" ,val.max_file_descriptor+"");
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveC3P0Monitor(C3P0MonitorInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		long start = System.currentTimeMillis();
		String log = start+";"+
					val.name+";"+
					val.num_connections+";"+
					val.num_busy_connections+";"+
					val.thread_pool_size+";"+
					val.thread_pool_num_idle_threads+";";
		try{
			j.lpush(host+":"+port+"-c3p0",log);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}

	public static void saveTomcatMonitor(TomcatMonitorInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		long start = System.currentTimeMillis();
		String log = start+";"+
				 val.http_total_threads+";"+
				 val.http_busy_threads+";"+
				 val.ajp_total_threads+";"+
				 val.ajp_busy_threads+";"+
				 val.session_active+";";
		try{
			//System.out.println("save["+host+":"+port+"-tomcat]:"+log);
			j.lpush(host+":"+port+"-tomcat",log);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveBesMonitor(BesMonitorInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		long start = System.currentTimeMillis();
		String log = start+";"+
				 val.http_total_threads+";"+
				 val.http_idle_threads+";"+
				 val.http_busy_threads+";"+
				 val.session_active+";"+
				 val.queued+";";
		try{
			j.lpush(host+":"+port+"-bes",log);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
	
	public static void saveJvmMonitor(JvmMonitorInfo val){
		JedisUtil ju = JedisUtil.getInstance();
		Jedis j = ju.getJedis(redishost, redisport);
		long start = System.currentTimeMillis();
		String log = start+";"+
					 val.used_heap_mem+";"+
					 val.used_nonheap_mem+";"+
					 val.thread_count+";"+
					 val.cpu_load+";"+
					 val.class_count+";"+
		             val.open_file_descriptor+";";
		try{
			j.lpush(host+":"+port+"-jvm",log);
			j.hset("jvm-list", host+":"+port, ""+start);
			j.close();
		}catch(NullPointerException e){
			System.out.println("connect to redis error");
		}catch(JedisConnectionException e){
			System.out.println("connect to redis error");
		}catch(Exception e){
			j.close();
			System.out.println("redis server error");
		}
	}
}
