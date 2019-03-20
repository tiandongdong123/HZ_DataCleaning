package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.WhetherFlagEnum;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessHighNewInfoBO;
import com.wanfang.datacleaning.handler.service.HighNewInfoService;
import com.wanfang.datacleaning.handler.util.business.HighNewEnterUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
     * 更新高新企业信息成功数量
     */
    private int highNewInfoUpdSuccessCount;
    /**
     * 更新高新企业信息失败数量
     */
    private int highNewInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleHighNewInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的高新信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新高新信息
            updateLocationInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的高新信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (highNewInfoUpdSuccessCount + highNewInfoUpdFailCount), highNewInfoUpdSuccessCount, highNewInfoUpdFailCount, System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 是否满足前提条件
     *
     * @return boolean true：满足
     */
    private boolean meetPrerequisite() {
        // 判断更新起始、结束位置索引是否正确
        if (CmnConstant.ID_END_POSITION <= CmnConstant.ID_START_POSITION) {
            logger.warn("idStartPosition：【{}】，idEndPosition：【{}】，更新结束位置小于或等于更新起始位置", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            return false;
        }
        // 判断高新企业是否缓存成功
        if (HighNewEnterUtils.getCacheHighNewEnterInfoSize() < 1) {
            logger.warn("文件：【{}】,sheet：【{}】，高新企业数据为空", HighNewEnterUtils.HIGH_NEW_ENTER_FILE_PATH, HighNewEnterUtils.HIGH_NEW_ENTER_SHEET_NAME);
            return false;
        }
        return true;
    }

    /**
     * 递归更新位置信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateLocationInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的高新信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessHighNewInfoBO> highNewInfoBOList = tblBusinessDao.getHighNewInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = highNewInfoBOList.size();
        logger.info("查询DB工商表的高新信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (highNewInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = highNewInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的高新信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessHighNewInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(highNewInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新高新信息
                if (updateBatchHighNewInfo(batchList)) {
                    highNewInfoUpdSuccessCount += batchList.size();
                } else {
                    highNewInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的高新信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateLocationInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理高新信息
     *
     * @param highNewInfoBO 高新信息
     * @return BusinessLocationInfoBO 若出现异常，则返回null
     */
    private BusinessHighNewInfoBO handleHighNewInfo(BusinessHighNewInfoBO highNewInfoBO) {
        if (highNewInfoBO == null || StringUtils.isBlank(highNewInfoBO.getEntName())) {
            highNewInfoBO.setHighNewEnter(WhetherFlagEnum.NO.getValue());
            return highNewInfoBO;
        }

        // 是否高新技术企业
        if (HighNewEnterUtils.isHighNewEnter(highNewInfoBO.getEntName())) {
            highNewInfoBO.setHighNewEnter(WhetherFlagEnum.YES.getValue());
        } else {
            highNewInfoBO.setHighNewEnter(WhetherFlagEnum.NO.getValue());
        }

        // 更新时间（10位时间戳）
        highNewInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());

        return highNewInfoBO;
    }

    /**
     * 批量更新高新信息
     *
     * @param highNewInfoBOList 高新信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchHighNewInfo(List<BusinessHighNewInfoBO> highNewInfoBOList) {
        List<BusinessHighNewInfoBO> handleList = new ArrayList<>();
        long idStartPosition = highNewInfoBOList.get(0).getId();
        long idEndPosition = highNewInfoBOList.get(highNewInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessHighNewInfoBO highNewInfoBO : highNewInfoBOList) {
                handleList.add(handleHighNewInfo(highNewInfoBO));
            }
            logger.info("id区间为：[{},{}]，处理高新信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, highNewInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchHighNewInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表高新信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新高新信息出现异常：", idStartPosition, idEndPosition, JSON.toJSONString(handleList), e);
            return false;
        }
    }
}

