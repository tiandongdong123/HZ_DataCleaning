package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.StandardInfoService;
import com.wanfang.datacleaning.handler.util.business.StandardCodeUtils;
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

        // 判断更新起始、结束位置索引是否正确
        if (CmnConstant.END_INDEX <= CmnConstant.START_INDEX) {
            LoggerUtils.appendErrorLog(logger, "startIndex：【{}】，endIndex：【{}】，更新结束位置索引小于或等于更新起始位置索引", CmnConstant.START_INDEX, CmnConstant.END_INDEX);
            return;
        }

        // 判断标准代码文件是否缓存成功
        if (StandardCodeUtils.getCacheCodeMapSize() < 1) {
            LoggerUtils.appendErrorLog(logger, "文件：【{}】,sheet：【{}】，代码数据为空", StandardCodeUtils.STANDARD_CODE_FILE_PATH, StandardCodeUtils.STANDARD_CODE_SHEET_NAME);
            return;
        }

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
