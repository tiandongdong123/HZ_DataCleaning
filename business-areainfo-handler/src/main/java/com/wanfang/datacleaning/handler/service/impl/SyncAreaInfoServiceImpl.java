package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.slave.SlaveTblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.ShortBusinessAreaInfoBO;
import com.wanfang.datacleaning.handler.service.SyncAreaInfoService;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/12/25 9:50 
 *  @Version  V1.0   
 */
@Service("syncAreaInfoService")
public class SyncAreaInfoServiceImpl implements SyncAreaInfoService {

    private static final Logger logger = LoggerFactory.getLogger(SyncAreaInfoServiceImpl.class);

    /**
     * 更新行政区划信息成功数量
     */
    private int areaInfoUpdSuccessCount;
    /**
     * 更新行政区划信息失败数量
     */
    private int areaInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;
    @Autowired
    private SlaveTblBusinessDao slaveTblBusinessDao;

    @Override
    public void syncAreaInfo() {
        try {
            long startTimeMillis = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的行政区划信息开始");
            // 递归更新行政区划信息
            updateAreaInfoByRecursion(CmnConstant.START_INDEX, CmnConstant.PAGE_SIZE);
            LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的行政区划信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    (areaInfoUpdSuccessCount + areaInfoUpdFailCount), areaInfoUpdSuccessCount, areaInfoUpdFailCount, System.currentTimeMillis() - startTimeMillis);
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "syncAreaInfo()出现异常：", e);
        }
    }

    /**
     * 递归更新行政区划信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateAreaInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        pageSize = (CmnConstant.END_INDEX - startIndex) >= pageSize ? pageSize : CmnConstant.END_INDEX - startIndex + 1;
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页行政区划信息开始", pageNum);
        List<ShortBusinessAreaInfoBO> areaInfoBOList = tblBusinessDao.getShortAreaInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页行政区划信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, areaInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (areaInfoBOList != null && areaInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页行政区划信息开始", pageNum);
            List<ShortBusinessAreaInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < areaInfoBOList.size(); i++) {
                batchList.add(areaInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == areaInfoBOList.size()) {
                    // 批量更新行政区划信息
                    if (updateBatchAreaInfo(batchList)) {
                        areaInfoUpdSuccessCount += batchList.size();
                    } else {
                        areaInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页行政区划信息结束,共更新【{}】条数据，耗时【{}】ms", pageNum, areaInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (areaInfoBOList.size() == CmnConstant.PAGE_SIZE) {
                updateAreaInfoByRecursion(startIndex + pageSize, CmnConstant.PAGE_SIZE);
//                updateAreaInfoByRecursion(Integer.parseInt(areaInfoBOList.get(CmnConstant.PAGE_SIZE - 1).getId() + ""), CmnConstant.PAGE_SIZE);
            }
        }
    }

    /**
     * 批量更新行政区划信息
     *
     * @param areaInfoBOList
     * @return boolean
     */
    private boolean updateBatchAreaInfo(List<ShortBusinessAreaInfoBO> areaInfoBOList) {
        try {
            long startTime = System.currentTimeMillis();
            slaveTblBusinessDao.updateBatchAreaInfoByKey(areaInfoBOList);
            LoggerUtils.appendInfoLog(logger, "更新DB工商表【{}】条行政区划信息开始共耗时【{}】ms", areaInfoBOList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，更新行政区划信息(updateBatchAreaInfo())出现异常：", JSON.toJSONString(areaInfoBOList), e);
            return false;
        }
    }
}
