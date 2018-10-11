package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.CapInfoService;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class SyncTask {

    private static final Logger logger = LoggerFactory.getLogger(SyncTask.class);

    @Autowired
    private CapInfoService capInfoService;

    /**
     * 定时同步工商表资金信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblCapInfoTiming.cron}")
    public void syncBusinessTblCapInfoTiming() {

        long startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblCapInfoTiming（）定时任务执行开始*****");

        capInfoService.updateCapInfo();

        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblCapInfoTiming（）定时任务执行结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);
    }
}
