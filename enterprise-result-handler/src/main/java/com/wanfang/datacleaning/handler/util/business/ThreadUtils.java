package com.wanfang.datacleaning.handler.util.business;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/7 14:58 
 *  @Version  V1.0   
 */
public class ThreadUtils {

    /**
     * 线程池（注：设置线程池参数时，请详细了解ThreadPoolExecutor的相关知识，以免出现OOM）
     */
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("threadPool_%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    private ThreadUtils() {
    }

    /**
     * 获取线程池执行器
     *
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
