package com.zheng.jedis;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhenglian
 * @Date 2018/1/5 17:41
 */
public class JedisTest {
    
    @Test
    public void testSet() {
        String key = "nihaoya";
        JedisUtil.set(key, "tom");
        String value = JedisUtil.get(key);
        System.out.println(value);
    }
    
    @Test
    public void testMset() {
        Map<String, String> kvs = new HashMap<>();
        kvs.put("1", "1");
        kvs.put("2", "2");
        JedisUtil.mset(kvs);
        List<String> keys = new ArrayList<>();
        keys.add("1");
        keys.add("2");
        Map<String, String> values = JedisUtil.mget(keys);
        System.out.println(values);
    }
}
