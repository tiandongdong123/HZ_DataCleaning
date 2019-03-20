package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.slave.SlaveTblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.ShortBusinessAreaInfoBO;
import com.wanfang.datacleaning.handler.service.SyncAreaInfoService;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private TblBusinessDao tblBusinessDao;
    @Resource
    private SlaveTblBusinessDao slaveTblBusinessDao;

    @Override
    public void syncAreaInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的行政区划信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新行政区划信息
            updateAreaInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的行政区划信息结束，id更新区间为：[{},{}]，更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (areaInfoUpdSuccessCount + areaInfoUpdFailCount), areaInfoUpdSuccessCount, areaInfoUpdFailCount, System.currentTimeMillis() - startTime);
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
     * 递归更新行政区划信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateAreaInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的行政区划信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<ShortBusinessAreaInfoBO> areaInfoBOList = tblBusinessDao.getShortAreaInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = areaInfoBOList.size();
        logger.info("查询DB工商表的行政区划信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (areaInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = areaInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的行政区划信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<ShortBusinessAreaInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(areaInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新行政区划信息
                if (updateBatchAreaInfo(batchList)) {
                    areaInfoUpdSuccessCount += batchList.size();
                } else {
                    areaInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的行政区划信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateAreaInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 批量更新行政区划信息
     *
     * @param areaInfoBOList 行政区划信息集合
     * @return boolean
     */
    private boolean updateBatchAreaInfo(List<ShortBusinessAreaInfoBO> areaInfoBOList) {
        long idStartPosition = areaInfoBOList.get(0).getId();
        long idEndPosition = areaInfoBOList.get(areaInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            slaveTblBusinessDao.updateBatchAreaInfoByKey(areaInfoBOList);
            logger.info("id区间为：[{},{}]，更新DB工商表行政区划信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, areaInfoBOList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新行政区划信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }
}
