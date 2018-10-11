package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.PatentInfoService;
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
    private PatentInfoService patentInfoService;

    /**
     * 定时同步工商表专利信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblPatentInfoTiming.cron}")
    public void syncBusinessTblPatentInfoTiming() {

        long startTimeMillis;
        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblPatentInfoTiming（）定时任务执行开始*****");

        startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "***** 缓存专利表的专利类型信息开始*****");
        boolean cacheSuccessFlag = patentInfoService.cacheBasePatentTypeInfo();
        LoggerUtils.appendInfoLog(logger, "***** 缓存专利表的专利类型信息结束,缓存结果【{}】,耗时：【{}】ms *****", cacheSuccessFlag, System.currentTimeMillis() - startTimeMillis);

        if (cacheSuccessFlag) {
            patentInfoService.updatePatentInfo();
        }
        LoggerUtils.appendInfoLog(logger, "***** syncBusinessTblPatentInfoTiming（）定时任务执行结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);
    }
}
