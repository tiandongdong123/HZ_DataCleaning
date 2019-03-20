package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.master.TblUsCreditCodeDao;
import com.wanfang.datacleaning.handler.model.bo.UsCreditCodeBO;
import com.wanfang.datacleaning.handler.service.UsCreditCodeService;
import com.wanfang.datacleaning.handler.util.business.UsCreditCodeDataUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
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
 *  @Date 2018/11/7 9:33 
 *  @Version  V1.0   
 */
@Service("usCreditCodeService")
public class UsCreditCodeServiceImpl implements UsCreditCodeService {

    private static final Logger logger = LoggerFactory.getLogger(UsCreditCodeServiceImpl.class);

    /**
     * 更新专利信息成功数量
     */
    private int patentInfoUpdSuccessCount;
    /**
     * 更新专利信息失败数量
     */
    private int patentInfoUpdFailCount;

    @Resource
    private TblUsCreditCodeDao tblUsCreditCodeDao;
    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleUsCreditCode() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的B统一社会信用代码信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新统一社会信用代码信息
            updateUsCreditCodeByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的B统一社会信用代码信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (patentInfoUpdSuccessCount + patentInfoUpdFailCount), patentInfoUpdSuccessCount, patentInfoUpdFailCount, System.currentTimeMillis() - startTime);
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

        // 缓存统一社会信用代码信息
        boolean cacheSuccessFlag = cacheUsCreditCodeInfo();
        if (!cacheSuccessFlag) {
            logger.warn("缓存统一社会信用代码信息失败");
            return false;
        }
        return true;
    }

    /**
     * 缓存统一社会信用代码信息
     *
     * @return boolean
     */
    private boolean cacheUsCreditCodeInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("缓存DB统一社会信用代码表的信用代码信息开始");
        // 递归缓存
        cacheBaseUsCreditCodeByRecursion(0, CmnConstant.PAGE_SIZE);

        int cacheSize = UsCreditCodeDataUtils.getCacheUsCreditCodeSize();
        if (cacheSize > 0) {
            logger.info("缓存DB统一社会信用代码表的信用代码信息结束，共缓存【{}】条数据， 耗时【{}】ms", cacheSize, System.currentTimeMillis() - startTime);
            return true;
        }
        logger.info("缓存DB统一社会信用代码表的信用代码信息结束，共缓存【{}】条数据， 耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    /**
     * 递归缓存统一社会信用代码信息
     *
     * @param idStartPosition id起始位置
     * @param pageSize        每页数量
     */
    private void cacheBaseUsCreditCodeByRecursion(int idStartPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        logger.info("查询DB统一社会信用代码表的信用代码信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<UsCreditCodeBO> creditCodeBOList = tblUsCreditCodeDao.getUsCreditCodeByPage(idStartPosition, pageSize);
        int qryResultSize = creditCodeBOList.size();
        logger.info("查询DB统一社会信用代码表的信用代码信息结束，idStartPosition：【{}】，pageSize：【{}】，查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (creditCodeBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = creditCodeBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("缓存DB统一社会信用代码表的信用代码信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        UsCreditCodeDataUtils.cacheUsCreditCodeMap(creditCodeBOList);
        logger.info("缓存DB统一社会信用代码表的信用代码信息结束，id区间为：[{},{}]，共缓存【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize) {
            cacheBaseUsCreditCodeByRecursion(lastPosition + 1, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归更新统一社会信用代码信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateUsCreditCodeByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的统一社会信用代码信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<UsCreditCodeBO> usCreditCodeBOList = tblBusinessDao.getUsCreditCodeByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = usCreditCodeBOList.size();
        logger.info("查询DB工商表的统一社会信用代码信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (usCreditCodeBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = usCreditCodeBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的统一社会信用代码信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<UsCreditCodeBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(usCreditCodeBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新统一社会信用代码信息
                if (updateBatchUsCreditCode(batchList)) {
                    patentInfoUpdSuccessCount += batchList.size();
                } else {
                    patentInfoUpdFailCount += batchList.size();
                }

                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的统一社会信用代码信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateUsCreditCodeByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理统一社会信用代码信息
     *
     * @param usCreditCodeBO 统一社会信用代码信息
     * @return BusinessPatentInfoBO
     */
    private UsCreditCodeBO handleUsCreditCodeInfo(UsCreditCodeBO usCreditCodeBO) {
        String usCreditCode = UsCreditCodeDataUtils.getCacheUsCreditCode(usCreditCodeBO.getPripid());

        usCreditCodeBO.setUsCreditCode(usCreditCode);
        usCreditCodeBO.setOrgCode(UsCreditCodeDataUtils.getOrgCodeByUsCreditCode(usCreditCode));
        usCreditCodeBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return usCreditCodeBO;
    }

    /**
     * 批量更新统一社会信用代码信息
     *
     * @param usCreditCodeBOList 统一社会信用代码信息集合
     * @return boolean
     */
    private boolean updateBatchUsCreditCode(List<UsCreditCodeBO> usCreditCodeBOList) {
        List<UsCreditCodeBO> handleList = new ArrayList<>();
        long idStartPosition = usCreditCodeBOList.get(0).getId();
        long idEndPosition = usCreditCodeBOList.get(usCreditCodeBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (UsCreditCodeBO usCreditCodeBO : usCreditCodeBOList) {
                handleList.add(handleUsCreditCodeInfo(usCreditCodeBO));
            }
            logger.info("id区间为：[{},{}]，处理统一社会信用代码信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, usCreditCodeBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchUsCreditCodeByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表统一社会信用代码信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，参数：【{}】，批量更新统一社会信用代码信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }

}
