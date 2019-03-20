package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessCapInfoBO;
import com.wanfang.datacleaning.handler.service.CapInfoService;
import com.wanfang.datacleaning.handler.util.business.ForeignExRateUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
@Service("capInfoService")
public class CapInfoServiceImpl implements CapInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CapInfoServiceImpl.class);

    /**
     * 更新资金信息成功数量
     */
    private int capInfoUpdSuccessCount;
    /**
     * 更新资金信息失败数量
     */
    private int capInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleCapInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的资金信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新资金信息
            updateCapInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的资金信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (capInfoUpdSuccessCount + capInfoUpdFailCount),
                    capInfoUpdSuccessCount, capInfoUpdFailCount, System.currentTimeMillis() - startTime);
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
        // 判断外汇牌价信息文件是否缓存成功
        if (ForeignExRateUtils.getCacheForeignExRateSize() < 1) {
            logger.warn("文件：【{}】,sheet：【{}】，外汇牌价信息为空", ForeignExRateUtils.FOREIGN_EX_RATE_FILE_PATH, ForeignExRateUtils.FOREIGN_EX_RATE_SHEET_NAME);
            return false;
        }
        return true;
    }

    /**
     * 递归更新资金信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateCapInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的资金信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessCapInfoBO> capInfoBOList = tblBusinessDao.getCapInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = capInfoBOList.size();
        logger.info("查询DB工商表的资金信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (capInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = capInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的资金信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessCapInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(capInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新资金信息
                if (updateBatchCapInfo(batchList)) {
                    capInfoUpdSuccessCount += batchList.size();
                } else {
                    capInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的资金信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateCapInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理资金信息
     *
     * @param capInfoBO 资金信息
     * @return BusinessCapInfoBO
     */
    private BusinessCapInfoBO handleCapInfo(BusinessCapInfoBO capInfoBO) {
        if (capInfoBO.getRegCap() == null) {
            capInfoBO.setRegCapRmb(null);
            capInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return capInfoBO;
        }

        BigDecimal regCapRmb = "人民币元".equals(capInfoBO.getRegCapCur()) ? capInfoBO.getRegCap().setScale(6, BigDecimal.ROUND_HALF_UP) : ForeignExRateUtils.getRmb(capInfoBO.getRegCapCur(), capInfoBO.getRegCap());
        capInfoBO.setRegCapRmb(regCapRmb);
        capInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return capInfoBO;
    }

    /**
     * 批量更新资金信息
     *
     * @param capInfoBOList 资金信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchCapInfo(List<BusinessCapInfoBO> capInfoBOList) {
        List<BusinessCapInfoBO> handleList = new ArrayList<>();
        long idStartPosition = capInfoBOList.get(0).getId();
        long idEndPosition = capInfoBOList.get(capInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessCapInfoBO capInfoBO : capInfoBOList) {
                handleList.add(handleCapInfo(capInfoBO));
            }
            logger.info("id区间为：[{},{}]，处理资金信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, capInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchCapInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，批量更新DB工商表资金信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新资金信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }
}

