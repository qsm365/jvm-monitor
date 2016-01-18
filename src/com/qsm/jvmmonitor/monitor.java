package com.qsm.jvmmonitor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMException;
//import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.qsm.jvmmonitor.utils.JVMMBeanDataDisplay;
import com.qsm.jvmmonitor.utils.myRedis;

public class monitor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host = args[0];
		Integer port = Integer.parseInt(args[1]);
		Map<String,String[]> map = new HashMap<String,String[]>();
		String[] credentials = new String[2];
		credentials[0] = args[2];
		credentials[1] = args[3];
		map.put("jmx.remote.credentials", credentials);
		JMXConnector c = null;
		String redishost = args[4];
		Integer redisport = Integer.parseInt(args[5]);
		Boolean debug = false;
		String debugcontent = "";
		if(args.length>6){
			if(args[6].startsWith("debug")){
				debug = true;
				debugcontent = args[6].split("=")[1];
			}
		}
		myRedis.init(host, port, redishost, redisport);
		myRedis.clearHistory();
		while(true){
			
			try {
				if(null == c){
					c = JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), map);
					//c = JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), null);
					c.connect();
					System.out.println("connect");
					if(debug){
						displayAll(c,new ObjectName(debugcontent+":*"));
					}
					init(c);
				}
				
				JvmService.monitorJvm(c);
				for(String con:JvmService.jvm_monitor_content){
					if("bes".equals(con)){
						BesService.monitorBes(c);
					}
					if("tomcat".equals(con)){
						TomcatService.monitorTomcat(c);
					}
					if("c3p0".equals(con)){
						try{
							C3P0Service.monitorC3P0(c);
						}catch(InstanceNotFoundException e){
							C3P0Service.initC3P0(c);
						}
					}
				}
				
				try{
					Thread.sleep(10000);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					//ie.printStackTrace();
					System.out.println("sleep faild");
				}
				
			} catch (ConnectException e){
				System.out.println("connection failed,wait for 60s to retry");
				c = null;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					//ie.printStackTrace();
					System.out.println("sleep faild");
					break;
				}
			} catch (IOException e) {
				System.out.println("io exception,wait for 60s to retry");
				//e.printStackTrace();
				c = null;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					//ie.printStackTrace();
					System.out.println("sleep faild");
					break;
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				System.out.println("Access denied");
				//e.printStackTrace();
				break;
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("AttributeNotFoundException");
				//e.printStackTrace();
				break;
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("InstanceNotFoundException,wait for 60s to retry");
				//e.printStackTrace();
				c = null;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					//ie.printStackTrace();
					System.out.println("sleep faild");
					break;
				}
			} catch (MalformedObjectNameException e) {
				// TODO Auto-generated catch block
				System.out.println("MalformedObjectNameException");
				//e.printStackTrace();
				break;
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				System.out.println("MBeanException");
				//e.printStackTrace();
				break;
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				System.out.println("ReflectionException");
				//e.printStackTrace();
				break;
			} catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				System.out.println("IntrospectionException");
				//e.printStackTrace();
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("other Exception");
				e.printStackTrace();
				break;
			}
		}
	}
	
	private static JMXServiceURL createConnectionURL(String host, int port) throws MalformedURLException
	{
	    return new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
	}

	private static void init(JMXConnector c) throws AttributeNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException, IntrospectionException, InstanceNotFoundException{
		String vmVendor = (String)JMXCommonService.getValueFromJMXMBean(c,"java.lang:type=Runtime","VmVendor");
		
		if("IBM Corporation".equals(vmVendor)){
			JvmService.jvm_monitor_type="ibm";
		}else if("Sun Microsystems Inc.".equals(vmVendor)){
			JvmService.jvm_monitor_type="sun";
		}
		
		JvmService.initJVM(c);
		
		JvmService.jvm_monitor_content.clear();
		
		if(JMXCommonService.hasNameInJMXMbean(c,"com.bes.appserv:type=Engine")){
			//this is a bes jvm
			JvmService.jvm_monitor_content.add("bes");
			BesService.initBES(c);
		}
		if(JMXCommonService.hasNameInJMXMbean(c,"Catalina:type=Engine")){
			//this is a bes jvm
			JvmService.jvm_monitor_content.add("tomcat");
			TomcatService.initTomcat(c);
		}
		if(JMXCommonService.hasNameInJMXMbean(c,"com.mchange.v2.c3p0:type=C3P0Registry")){
			//this is a bes jvm
			JvmService.jvm_monitor_content.add("c3p0");
			C3P0Service.initC3P0(c);
		}
		System.out.println("this is a(n) "+JvmService.jvm_monitor_type+" jvm");
		for (String i : JvmService.jvm_monitor_content){
			System.out.println("with:"+i);
		}
	}
	
	
	public final static String separator = 
            "\n=====================================================================\n";
    
    public static void displayAll(JMXConnector c,
            ObjectName pattern) throws IOException, JMException {
        final JVMMBeanDataDisplay display = new JVMMBeanDataDisplay(c.getMBeanServerConnection());
        System.out.println(separator);
        for (ObjectName mbean : c.getMBeanServerConnection().queryNames(pattern,null)) {
            System.out.println(display.toString(mbean));
            System.out.println(separator);
        }
    }
    
	
}
