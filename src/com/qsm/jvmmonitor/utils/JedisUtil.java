package com.qsm.jvmmonitor.utils;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/** 
 * Redis������,���ڻ�ȡRedisPool. 
 * �ο�����˵�����£� 
 * You shouldn't use the same instance from different threads because you'll have strange errors. 
 * And sometimes creating lots of Jedis instances is not good enough because it means lots of sockets and connections, 
 * which leads to strange errors as well. A single Jedis instance is not threadsafe! 
 * To avoid these problems, you should use JedisPool, which is a threadsafe pool of network connections. 
 * This way you can overcome those strange errors and achieve great performance. 
 * To use it, init a pool: 
 *  JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost"); 
 *  You can store the pool somewhere statically, it is thread-safe. 
 *  JedisPoolConfig includes a number of helpful Redis-specific connection pooling defaults. 
 *  For example, Jedis with JedisPoolConfig will close a connection after 300 seconds if it has not been returned. 
 * @author wujintao
 */
public class JedisUtil  {
    
    /**
     * ˽�й�����.
     */
    private JedisUtil() {
        
    }
    private static Map<String,JedisPool> maps  = new HashMap<String,JedisPool>();
    
    
    /**
     * ��ȡ���ӳ�.
     * @return ���ӳ�ʵ��
     */
    private static JedisPool getPool(String ip,int port) {
        String key = ip+":" +port;
        JedisPool pool = null;
        if(!maps.containsKey(key)) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10);
            config.setMaxIdle(2);
            config.setMaxWaitMillis(1000*30);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            try{
                /**
                 *��������� java.net.SocketTimeoutException: Read timed out exception���쳣��Ϣ
                 *�볢���ڹ���JedisPool��ʱ�������Լ��ĳ�ʱֵ. JedisPoolĬ�ϵĳ�ʱʱ����2��(��λ����)
                 */
                pool = new JedisPool(config, ip, port);
                maps.put(key, pool);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }else{
            pool = maps.get(key);
        }
        return pool;
    }

    /**
     *�༶���ڲ��࣬Ҳ���Ǿ�̬�ĳ�Աʽ�ڲ��࣬���ڲ����ʵ�����ⲿ���ʵ��
     *û�а󶨹�ϵ������ֻ�б����õ�ʱ�Ż�װ�أ��Ӷ�ʵ�����ӳټ��ء�
     */
    private static class RedisUtilHolder{
        /**
         * ��̬��ʼ��������JVM����֤�̰߳�ȫ 
         */
        private static JedisUtil instance = new JedisUtil();
    }

    /** 
     *��getInstance������һ�α����õ�ʱ������һ�ζ�ȡ
     *RedisUtilHolder.instance������RedisUtilHolder��õ���ʼ�������������װ�ز�����ʼ����ʱ�򣬻��ʼ�����ľ�
     *̬�򣬴Ӷ�����RedisUtil��ʵ���������Ǿ�̬�������ֻ���������װ�����ʱ���ʼ��һ�Σ��������������֤�����̰߳�ȫ��
     *���ģʽ���������ڣ�getInstance������û�б�ͬ��������ֻ��ִ��һ����ķ��ʣ�����ӳٳ�ʼ����û�������κη��ʳɱ�
     */
    public static JedisUtil getInstance() {
        return RedisUtilHolder.instance;
    }
    
    /**
     * ��ȡRedisʵ��.
     * @return Redis������ʵ�� 
     */
    public Jedis getJedis(String ip,int port) {
        Jedis jedis  = null;
        int count =0;
        do{
            try{
                jedis = getPool(ip,port).getResource();
                //log.info("get redis master1!");
            } catch (Exception e) {
                //log.error("get redis master1 failed!", e);
            	//e.printStackTrace();
            }
            count++;
        }while(jedis==null&&count<3);
        return jedis;
    }
}