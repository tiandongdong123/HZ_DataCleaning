package com.wanfang.datacleaning.handler.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/23 16:11 
 *  @Version  V1.0   
 */
public class TestDemo {

    /**
     * 比较顺序查找和散列查找的性能
     */
    @Test
    public void mapTest() {
        // 创建线性表集合
        List<String> list = new ArrayList<String>();
        // 创建散列表集合
        Map<String, Integer> map = new HashMap<String, Integer>();
        // 向集合中同时添加数据
        for (int i = 0; i < 500000; i++) {
            String key = "Tom" + i;
            list.add(key);
            // 将key,value成对的加入到map
            map.put(key, i);
        }
        // 被查找的 key
        String key = "Tom499999";
        // 线性表中顺序查找:
        long t1 = System.nanoTime();
        int i = list.indexOf(key);
        long t2 = System.nanoTime();
        System.out.println(i + "," + (t2 - t1)); //499999,11245379

        // 散列表中的散列查找:
        t1 = System.nanoTime();
        int n = map.get(key);
        t2 = System.nanoTime();
        System.out.println(n + "," + (t2 - t1)); //499999,15317
    }
}
