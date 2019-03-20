package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessDevZoneInfoBO;
import com.wanfang.datacleaning.handler.service.DevZoneService;
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
 * @author yifei
 * @date 2018/12/16
 */
@Service("devZoneService")
public class DevZoneServiceImpl implements DevZoneService {

    private static final Logger logger = LoggerFactory.getLogger(DevZoneServiceImpl.class);

    /**
     * 更新开发区信息成功数量
     */
    private int devZoneInfoUpdSuccessCount;
    /**
     * 更新开发区信息失败数量
     */
    private int devZoneInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    /**
     * 处理开发区信息
     */
    @Override
    public void handleDevZoneInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的开发区信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新开发区信息
            updateDevZoneInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的开发区信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (devZoneInfoUpdSuccessCount + devZoneInfoUpdFailCount), devZoneInfoUpdSuccessCount, devZoneInfoUpdFailCount, System.currentTimeMillis() - startTime);
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
        return true;
    }

    /**
     * 递归更新开发区信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateDevZoneInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的开发区信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessDevZoneInfoBO> devZoneInfoBOList = tblBusinessDao.getDevZoneInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = devZoneInfoBOList.size();
        logger.info("查询DB工商表的开发区信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (devZoneInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = devZoneInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的开发区信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessDevZoneInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(devZoneInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新开发区信息
                if (updateBatchDevZoneInfo(batchList)) {
                    devZoneInfoUpdSuccessCount += batchList.size();
                } else {
                    devZoneInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的开发区信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateDevZoneInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理开发区信息
     *
     * @param devZoneInfoBO 开发区信息
     * @return BusinessDevZoneInfoBO 若出现异常，则返回null
     */
    private BusinessDevZoneInfoBO handleDevZoneInfo(BusinessDevZoneInfoBO devZoneInfoBO) {
        if (devZoneInfoBO == null || StringUtils.isBlank(devZoneInfoBO.getLatLon())) {
            devZoneInfoBO.setEcoTecDevZone("");
            devZoneInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return devZoneInfoBO;
        }

        try {

            return null;
        } catch (Exception e) {
            logger.error("devZoneInfoBO：【{}】，处理开发区信息出现异常：", devZoneInfoBO, e);
            return null;
        }
    }

    /**
     * 批量更新开发区信息
     *
     * @param devZoneInfoBOList 开发区信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchDevZoneInfo(List<BusinessDevZoneInfoBO> devZoneInfoBOList) {
        List<BusinessDevZoneInfoBO> handleList = new ArrayList<>();
        long idStartPosition = devZoneInfoBOList.get(0).getId();
        long idEndPosition = devZoneInfoBOList.get(devZoneInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessDevZoneInfoBO devZoneInfoBO : devZoneInfoBOList) {
                devZoneInfoBO = handleDevZoneInfo(devZoneInfoBO);
                if (devZoneInfoBO != null) {
                    handleList.add(devZoneInfoBO);
                }
            }
            logger.info("id区间为：[{},{}]，处理开发区信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, devZoneInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchDevZoneInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表开发区信息【{}】条，开始共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新开发区信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }

}
