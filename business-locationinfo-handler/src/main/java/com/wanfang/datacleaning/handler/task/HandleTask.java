package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.LocationInfoService;
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
    private LocationInfoService locationInfoService;

    /**
     * 定时处理工商表位置信息
     */
    @Scheduled(cron = "${HandleTask.handleBusinessTblLocationInfoTiming.cron}")
    public void handleBusinessTblLocationInfoTiming() {
        long startTimeMillis = System.currentTimeMillis();
        logger.info("处理工商表位置信息定时任务执行开始");
        locationInfoService.handleLocationInfo();
        logger.info("处理工商表位置信息定时任务执行结束，耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
