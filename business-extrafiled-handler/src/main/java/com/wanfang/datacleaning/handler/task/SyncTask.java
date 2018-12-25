package com.wanfang.datacleaning.handler.task;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.ExtraFiledService;
import com.wanfang.datacleaning.handler.util.business.DomPropertyUtils;
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
    private ExtraFiledService extraFiledService;

    /**
     * 定时同步工商表拓展字段信息
     */
    @Scheduled(cron = "${SyncTask.syncBusinessTblExtraFieldTiming.cron}")
    public void syncBusinessTblExtraFieldTiming() {

        long startTimeMillis = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "syncBusinessTblExtraFieldTiming（）定时任务执行开始");

        // 判断更新起始、结束位置索引是否正确
        if (CmnConstant.END_INDEX <= CmnConstant.START_INDEX) {
            LoggerUtils.appendErrorLog(logger, "startIndex：【{}】，endIndex：【{}】，更新结束位置索引小于或等于更新起始位置索引", CmnConstant.START_INDEX, CmnConstant.END_INDEX);
            return;
        }

        // 判断住所产权代码文件是否缓存成功
        if (DomPropertyUtils.getCacheCodeMapSize() < 1) {
            LoggerUtils.appendErrorLog(logger, "文件：【{}】,sheet：【{}】，代码数据为空", DomPropertyUtils.DOM_PROPERTY_CODE_FILE_PATH, DomPropertyUtils.DOM_PROPERTY_CODE_SHEET_NAME);
            return;
        }

        extraFiledService.updateExtraFieldInfo();

        LoggerUtils.appendInfoLog(logger, "syncBusinessTblExtraFieldTiming（）定时任务执行结束,耗时：【{}】ms", System.currentTimeMillis() - startTimeMillis);
    }
}
