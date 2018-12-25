package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.AreaInfoService;
import com.wanfang.datacleaning.handler.service.SyncAreaInfoService;
import com.wanfang.datacleaning.handler.util.business.PostalCodeUtils;
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
    private AreaInfoService areaInfoService;
    @Autowired
    private SyncAreaInfoService syncAreaInfoService;

    /**
     * 定时同步工商表行政区划信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblAreaInfoTiming.cron}")
    public void syncBusinessTblAreaInfoTiming() {

        long startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "syncBusinessTblAreaInfoTiming（）定时任务执行开始");

        // 判断更新起始、结束位置索引是否正确
        if (CmnConstant.END_INDEX <= CmnConstant.START_INDEX) {
            LoggerUtils.appendErrorLog(logger, "startIndex：【{}】，endIndex：【{}】，更新结束位置索引小于或等于更新起始位置索引", CmnConstant.START_INDEX, CmnConstant.END_INDEX);
            return;
        }

        // 缓存邮编与行政区划对应关系代码
        if (PostalCodeUtils.getCacheTotal() < 1) {
            LoggerUtils.appendErrorLog(logger, "文件：【{}】,sheet：【{}】，邮编与行政区划对应关系代码数据为空", PostalCodeUtils.POSTAL_CODE_FILE_PATH, PostalCodeUtils.POSTAL_CODE_FILE_SHEET_NAME);
        }

        areaInfoService.updateAreaInfo();

        LoggerUtils.appendInfoLog(logger, "syncBusinessTblAreaInfoTiming（）定时任务执行结束,耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }

    /**
     * 定时同步工商表行政区划信息到云库
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblAreaInfoToCloudDbTiming.cron}")
    public void syncBusinessTblAreaInfoToCloudDbTiming() {

        long startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "syncBusinessTblAreaInfoToCloudDbTiming（）定时任务执行开始");

        syncAreaInfoService.syncAreaInfo();

        LoggerUtils.appendInfoLog(logger, "syncBusinessTblAreaInfoToCloudDbTiming（）定时任务执行结束,耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
