package com.qsm.jvmmonitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

public class JMXCommonService {
	public static Object getValueFromJMXMBean(JMXConnector c,String name,String attribute) throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException{
		Object o = c.getMBeanServerConnection().getAttribute(new ObjectName(name), attribute);
		return o;
	}
	
	public static Boolean hasNameInJMXMbean(JMXConnector c,String name) throws MalformedObjectNameException, IOException{
		return c.getMBeanServerConnection().isRegistered(new ObjectName(name));
	}
	
	public static Set<ObjectName> queryNames(JMXConnector c,String query) throws MalformedObjectNameException, IOException{
		String name = query.split(":")[0]+":*";
		//System.out.println("name:"+name);
		Set<ObjectName> result = new HashSet<ObjectName>();
		for (ObjectName mbean : c.getMBeanServerConnection().queryNames(new ObjectName(name), null)){
			String pat = toJavaPattern(query);
			//System.out.println("pattern:"+pat);
			if(java.util.regex.Pattern.matches(pat, mbean.toString())){
				//System.out.println(mbean);
				result.add(mbean);
			}
		}
		return result;
	}
	
	private static String toJavaPattern(String pattern) {
        String result = "^";
        char metachar[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '.', '/' };
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean isMeta = false;
            for (int j = 0; j < metachar.length; j++) {
                if (ch == metachar[j]) {
                    result += "\\" + ch;
                    isMeta = true;
                    break;
                }
            }
            if (!isMeta) {
                if (ch == '*') {
                    result += ".*";
                } else {
                    result += ch;
                }
            }
        }
        result += "$";
        return result;
    }
}
