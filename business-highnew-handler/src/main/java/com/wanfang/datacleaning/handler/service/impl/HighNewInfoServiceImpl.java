package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessHighNewInfoBO;
import com.wanfang.datacleaning.handler.service.HighNewInfoService;
import com.wanfang.datacleaning.handler.util.business.HighNewEnterUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("highNewInfoService")
public class HighNewInfoServiceImpl implements HighNewInfoService {

    private static final Logger logger = LoggerFactory.getLogger(HighNewInfoServiceImpl.class);

    /**
     * 更新位置信息成功数量
     */
    private int highNewInfoUpdSuccessCount;
    /**
     * 更新位置信息失败数量
     */
    private int highNewInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;

    @Override
    public void updateHighNewInfo() {

        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的高新信息开始");

        // 递归更新高新信息
        updateLocationInfoByRecursion(CmnConstant.START_INDEX, CmnConstant.PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的高新信息结束，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                (highNewInfoUpdSuccessCount + highNewInfoUpdFailCount), highNewInfoUpdSuccessCount, highNewInfoUpdFailCount, System.currentTimeMillis() - startTime);

    }

    /**
     * 递归更新位置信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateLocationInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        pageSize = (CmnConstant.END_INDEX - startIndex) >= pageSize ? pageSize : CmnConstant.END_INDEX - startIndex + 1;
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页高新信息开始", pageNum);
        List<BusinessHighNewInfoBO> highNewInfoBOList = tblBusinessDao.getHighNewInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页高新信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, highNewInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (highNewInfoBOList != null && highNewInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页高新信息开始", pageNum);
            List<BusinessHighNewInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < highNewInfoBOList.size(); i++) {
                batchList.add(highNewInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == highNewInfoBOList.size()) {
                    // 批量更新高新信息
                    if (updateBatchHighNewInfo(batchList)) {
                        highNewInfoUpdSuccessCount += batchList.size();
                    } else {
                        highNewInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页高新信息结束,共更新【{}】条数据，耗时【{}】ms", pageNum, highNewInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (highNewInfoBOList.size() == CmnConstant.PAGE_SIZE) {
                updateLocationInfoByRecursion(startIndex + pageSize, CmnConstant.PAGE_SIZE);
            }
        }
    }

    /**
     * 处理高新信息
     *
     * @param highNewInfoBO
     * @return BusinessLocationInfoBO 若出现异常，则返回null
     */
    private BusinessHighNewInfoBO handleHighNewInfo(BusinessHighNewInfoBO highNewInfoBO) {
        if (highNewInfoBO == null || StringUtils.isBlank(highNewInfoBO.getEntName())) {
            highNewInfoBO.setHighNewEnter(CmnEnum.WhetherFlagEnum.NO.getValue());
            return highNewInfoBO;
        }

        // 是否高新技术企业
        if (HighNewEnterUtils.isHighNewEnter(highNewInfoBO.getEntName())) {
            highNewInfoBO.setHighNewEnter(CmnEnum.WhetherFlagEnum.YES.getValue());
        } else {
            highNewInfoBO.setHighNewEnter(CmnEnum.WhetherFlagEnum.NO.getValue());
        }

        // 更新时间（10位时间戳）
        highNewInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());

        return highNewInfoBO;
    }

    /**
     * 批量更新高新信息
     *
     * @param highNewInfoBOList
     * @return boolean
     */
    private boolean updateBatchHighNewInfo(List<BusinessHighNewInfoBO> highNewInfoBOList) {
        List<BusinessHighNewInfoBO> handleList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessHighNewInfoBO highNewInfoBO : highNewInfoBOList) {
                if (highNewInfoBO != null) {
                    handleList.add(handleHighNewInfo(highNewInfoBO));
                }
            }
            LoggerUtils.appendInfoLog(logger, "处理【{}】条高新信息共耗时【{}】ms", highNewInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchHighNewInfoByKey(handleList);
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表【{}】条高新信息开始共耗时【{}】ms", handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，updateBatchHighNewInfo()出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }
    }
}

