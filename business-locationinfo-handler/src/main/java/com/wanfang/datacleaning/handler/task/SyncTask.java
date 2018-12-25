package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.LocationInfoService;
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
    private LocationInfoService locationInfoService;

    /**
     * 定时同步工商表位置信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblLocationInfoTiming.cron}")
    public void syncBusinessTblLocationInfoTiming() {

        long startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "syncBusinessTblLocationInfoTiming（）定时任务执行开始");

        // 判断更新起始、结束位置索引是否正确
        if (CmnConstant.END_INDEX <= CmnConstant.START_INDEX) {
            LoggerUtils.appendErrorLog(logger, "startIndex：【{}】，endIndex：【{}】，更新结束位置索引小于或等于更新起始位置索引", CmnConstant.START_INDEX, CmnConstant.END_INDEX);
            return;
        }

        locationInfoService.updateLocationInfo();

        LoggerUtils.appendInfoLog(logger, "syncBusinessTblLocationInfoTiming（）定时任务执行结束,耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
