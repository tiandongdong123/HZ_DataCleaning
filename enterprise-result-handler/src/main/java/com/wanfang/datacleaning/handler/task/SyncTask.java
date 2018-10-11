package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.service.EnterpriseResultService;
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
    private EnterpriseResultService enterpriseResultService;

    /**
     * 定时同步企业成果信息
     */
    @Scheduled(cron = "${SyncTask.syncEntResultInfoTiming.cron}")
    public void syncEntResultInfoTiming() {

        long startTimeMillis;
        LoggerUtils.appendInfoLog(logger, "***** syncEntResultInfoTiming（）定时任务执行开始*****");

        startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "***** 缓存工商表的企业(机构)名称信息开始*****");
        boolean cacheSuccessFlag = enterpriseResultService.cacheBaseEntNameInfo();
        LoggerUtils.appendInfoLog(logger, "***** 缓存工商表的企业(机构)名称信息结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);

        if (cacheSuccessFlag) {
            startTimeMillis = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "***** entResultService.syncEntResultInfo()开始*****");
            enterpriseResultService.syncEntResultInfo();
            LoggerUtils.appendInfoLog(logger, "***** entResultService.syncEntResultInfo()结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);
        }

        LoggerUtils.appendInfoLog(logger, "***** syncEntResultInfoTiming（）定时任务执行结束,耗时：【{}】ms *****", System.currentTimeMillis() - startTimeMillis);
    }
}
