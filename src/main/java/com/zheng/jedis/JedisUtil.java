package com.zheng.jedis;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @Author zhenglian
 * @Date 2018/1/5 17:21
 */
public class JedisUtil {
    private static JedisPoolConfig cfg = new JedisPoolConfig();
    private static JedisPool pool;
    static {
        Properties properties = new Properties();
        try {
            properties.load(JedisUtil.class.getClassLoader().getResourceAsStream("redis.properties"));
        } catch (IOException e) {
            throw new RuntimeException("加载redis配置文件redis.properties失败");
        }
        
        Integer maxIdle = 1;
        String maxIdleProperty = properties.getProperty("redis.pool.maxIdle");
        if (StringUtils.isNotEmpty(maxIdleProperty)) {
            maxIdle = Integer.parseInt(maxIdleProperty);
        }
        cfg.setMaxIdle(maxIdle);

        Integer maxSize = 10;
        String maxSizeProperty = properties.getProperty("redis.pool.maxSize");
        if (StringUtils.isNotEmpty(maxSizeProperty)) {
            maxSize = Integer.parseInt(maxSizeProperty);
        }
        cfg.setMaxTotal(maxSize);
        
        String host = properties.getProperty("redis.host");
        String portProperty = properties.getProperty("redis.port");
        Integer port = Integer.parseInt(portProperty);
        
        if (StringUtils.isEmpty(host)) {
            throw new RuntimeException("redis配置错误：没有指定redis主机名");
        }
        
        if (!Optional.ofNullable(port).isPresent()) {
            port = 6379;
        }
        
        
        pool = new JedisPool(cfg, host, port);
    }

    /**
     * 设置值
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (StringUtils.isEmpty(value)) {
            value = "";
        }
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public static String get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * 判断给定的key列表是否在缓存中存在
     * @param keys
     * @return
     */
    public static boolean exists(String... keys) {
        if (!Optional.ofNullable(keys).isPresent() || keys.length == 0) {
            return false;
        }
        Jedis jedis = null;
        Boolean exist = Boolean.FALSE;
        try {
            jedis = pool.getResource();
            Long num = jedis.exists(keys);
            exist = num > 0;
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
        return exist;
    }

    /**
     * 判断给定的key列表是否在缓存中存在
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        Jedis jedis = null;
        Boolean exist = Boolean.FALSE;
        try {
            jedis = pool.getResource();
            exist = jedis.exists(key);
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
        return exist;
    }

    /**
     * 一次设置多个值
     * @param kvs
     */
    public static void mset(Map<String, String> kvs) {
        if (!Optional.ofNullable(kvs).isPresent() || MapUtils.isEmpty(kvs)) {
            return;
        }
        // 封装请求参数
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : kvs.entrySet()) {
            values.add(entry.getKey());
            values.add(entry.getValue());
        }
        
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String[] params = new String[values.size()];
            jedis.mset(values.toArray(params));
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
    }

    /**
     * 一次获取多个值
     * @param keys
     * @return
     */
    public static Map<String, String> mget(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new HashMap<>();
        }
        Map<String, String> result = new HashMap<>();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String[] params = new String[keys.size()];
            List<String> values = jedis.mget(keys.toArray(params));
            for (int i = 0; i < keys.size(); i++) {
                result.put(keys.get(i), values.get(i));
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            if (Optional.ofNullable(jedis).isPresent()) {
                jedis.close();
            }
        }
        return result;
    }
    
    /**
     * 处理异常信息
     * @param e
     */
    private static void handleException(Exception e) {
        // TODO 这里需要用户自定义异常处理
        e.printStackTrace();
    }

}
