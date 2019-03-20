package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.CapInfoService;
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
    private CapInfoService capInfoService;

    /**
     * 定时处理工商表资金信息
     */
    @Scheduled(cron = "${HandleTask.handleBusinessTblCapInfoTiming.cron}")
    public void handleBusinessTblCapInfoTiming() {
        long startTimeMillis = System.currentTimeMillis();
        logger.info("处理工商表资金信息定时任务执行开始");
        capInfoService.handleCapInfo();
        logger.info("处理工商表资金信息定时任务执行结束，耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
