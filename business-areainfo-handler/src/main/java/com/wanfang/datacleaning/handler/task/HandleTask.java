package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.AreaInfoService;
import com.wanfang.datacleaning.handler.service.SyncAreaInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/7 15:27 
 *  @Version  V1.0   
 */
@Component
@Configurable
@EnableScheduling
public class HandleTask {

    private static final Logger logger = LoggerFactory.getLogger(HandleTask.class);

    @Resource
    private AreaInfoService areaInfoService;
    @Resource
    private SyncAreaInfoService syncAreaInfoService;

    /**
     * 定时处理工商表行政区划信息
     */
    @Scheduled(cron = "${HandleTask.handleBusinessTblAreaInfoTiming.cron}")
    public void handleBusinessTblAreaInfoTiming() {
        long startTimeMillis = System.currentTimeMillis();
        logger.info("处理工商表行政区划信息定时任务执行开始");
        areaInfoService.handleAreaInfo();
        logger.info("处理工商表行政区划信息定时任务执行结束，耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }

    /**
     * 定时同步工商表行政区划信息到云库
     */
    @Scheduled(cron = "${HandleTask.syncBusinessTblAreaInfoToCloudDbTiming.cron}")
    public void syncBusinessTblAreaInfoToCloudDbTiming() {
        long startTimeMillis = System.currentTimeMillis();
        logger.info("同步工商表行政区划信息到云库定时任务执行开始");
        syncAreaInfoService.syncAreaInfo();
        logger.info("同步工商表行政区划信息到云库定时任务执行结束，耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
