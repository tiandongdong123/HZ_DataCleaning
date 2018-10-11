package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.StandardInfoService;
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
    private StandardInfoService standardInfoService;

    /**
     * 定时同步工商表标准信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblStandardInfoTiming.cron}")
    public void syncBusinessTblStandardInfoTiming() {

        long startTimeMillis;
        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblStandardInfoTiming（）定时任务执行开始*****");

        startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "***** 缓存标准表的标准类型信息开始*****");
        boolean cacheSuccessFlag = standardInfoService.cacheBaseStandardTypeInfo();
        LoggerUtils.appendInfoLog(logger, "***** 缓存标准表的标准类型信息结束,缓存结果【{}】,耗时：【{}】ms *****", cacheSuccessFlag, System.currentTimeMillis() - startTimeMillis);

        if (cacheSuccessFlag) {
            standardInfoService.updateStandardInfo();
        }

        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblStandardInfoTiming（）定时任务执行结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);
    }
}
