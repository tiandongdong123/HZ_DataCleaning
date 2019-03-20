package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.WhetherFlagEnum;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.master.TblStandardDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessStandardInfoBO;
import com.wanfang.datacleaning.handler.model.bo.StandardStdTypeBO;
import com.wanfang.datacleaning.handler.service.StandardInfoService;
import com.wanfang.datacleaning.handler.util.business.StandardCodeUtils;
import com.wanfang.datacleaning.handler.util.business.StandardDataUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("standardInfoService")
public class StandardInfoServiceImpl implements StandardInfoService {
    private static final Logger logger = LoggerFactory.getLogger(StandardInfoServiceImpl.class);

    /**
     * 更新标准信息成功数量
     */
    private int stdInfoUpdSuccessCount;
    /**
     * 更新标准信息失败数量
     */
    private int stdInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;
    @Resource
    private TblStandardDao tblStandardDao;

    @Override
    public void handleStandardInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的专利信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新标准信息
            updateStandardInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的专利信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (stdInfoUpdSuccessCount + stdInfoUpdFailCount), stdInfoUpdSuccessCount, stdInfoUpdFailCount, System.currentTimeMillis() - startTime);
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

        // 判断住所产权代码文件是否缓存成功
        if (StandardCodeUtils.getCacheCodeMapSize() < 1) {
            logger.warn("文件：【{}】,sheet：【{}】，标准代码数据为空", StandardCodeUtils.STANDARD_CODE_FILE_PATH, StandardCodeUtils.STANDARD_CODE_SHEET_NAME);
            return false;
        }

        // 缓存标准数据信息
        boolean cacheSuccessFlag = cacheBaseStandardTypeInfo();
        if (!cacheSuccessFlag) {
            logger.warn("缓存专利表的标准类型信息失败");
            return false;
        }
        return true;
    }

    /**
     * 缓存标准数据信息
     *
     * @return boolean true：缓存成功
     */
    private boolean cacheBaseStandardTypeInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("缓存DB标准表的标准类型信息开始");
        // 递归缓存
        cacheBaseStandardTypeInfoByRecursion(0, CmnConstant.PAGE_SIZE);

        int cacheSize = StandardDataUtils.getCacheStdTypeInfoMapWithFilterSize();
        if (cacheSize > 0) {
            logger.info("缓存DB标准表的标准类型信息结束，共缓存【{}】条数据，耗时【{}】ms", cacheSize, System.currentTimeMillis() - startTime);
            return true;
        }
        logger.info("缓存DB标准表的标准类型信息结束，共缓存【{}】条数据，耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    /**
     * 递归缓存标准类型信息
     *
     * @param idStartPosition 起始位置
     * @param pageSize        每页数量
     */
    private void cacheBaseStandardTypeInfoByRecursion(int idStartPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        logger.info("查询DB标准表的标准类型信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<StandardStdTypeBO> stdTypeBOList = tblStandardDao.getBaseStandardTypeInfoByPage(idStartPosition, pageSize);
        int qryResultSize = stdTypeBOList.size();
        logger.info("查询DB标准表的标准类型信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (stdTypeBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = stdTypeBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("缓存DB标准表的标准类型信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        StandardDataUtils.cacheStdTypeInfoMapWithFilter(stdTypeBOList);
        logger.info("缓存DB标准表的标准类型信息结束，id区间为：[{},{}]，共缓存【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize) {
            cacheBaseStandardTypeInfoByRecursion(lastPosition + 1, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归更新标准信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateStandardInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的标准信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessStandardInfoBO> standardInfoBOList = tblBusinessDao.getStandardInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = standardInfoBOList.size();
        logger.info("查询DB工商表的标准信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (standardInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = standardInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的标准信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessStandardInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(standardInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新标准信息
                if (updateBatchStandardInfo(batchList)) {
                    stdInfoUpdSuccessCount += batchList.size();
                } else {
                    stdInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的标准信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateStandardInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理标准信息
     *
     * @param standardInfoBO 标准信息信息
     * @return BusinessStandardInfoBO
     */
    private BusinessStandardInfoBO handleStdInfo(BusinessStandardInfoBO standardInfoBO) {
        // 判断主体身份代码是否为空
        if (StringUtils.isBlank(standardInfoBO.getPripid())) {
            standardInfoBO = initBusinessStandardInfoBO(standardInfoBO);
            standardInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return standardInfoBO;
        }

        // 去除空白字符
        String entName = StringUtils.deleteWhitespace(standardInfoBO.getEntName());
        Map<String, List<StandardStdTypeBO>> stdTypeBOMap = StandardDataUtils.getCacheStdTypeInfoMapWithFilter();
        List<StandardStdTypeBO> stdTypeBOList = stdTypeBOMap.get(entName);
        if (stdTypeBOList == null || stdTypeBOList.isEmpty()) {
            standardInfoBO = initBusinessStandardInfoBO(standardInfoBO);
            standardInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return standardInfoBO;
        }

        int hasSrStd = WhetherFlagEnum.NO.getValue();
        long stdNum = 0;
        StringBuilder patTypeListBuilder = new StringBuilder();
        for (StandardStdTypeBO stdTypeBO : stdTypeBOList) {
            // 判断“发布日期”是否在经营期限内
            if (!isInOperationPeriod(standardInfoBO.getOpFrom(), standardInfoBO.getOpTo(), stdTypeBO.getIssueDate())) {
                continue;
            }
            // 判断是否参与起草标准
            if (StandardDataUtils.meetStdStatus(stdTypeBO.getStandardStatus())) {
                hasSrStd = WhetherFlagEnum.YES.getValue();
            }
            // 起草标准类型列表，格式：1,2,3
            patTypeListBuilder.append(CmnConstant.SEPARATOR_COMMA).append(StandardDataUtils.getDrStdType(stdTypeBO.getStandardType()));
            // 标准数量
            stdNum++;
        }

        standardInfoBO.setHasDrStandard(hasSrStd);
        standardInfoBO.setDrStandardTypeList(StandardDataUtils.handleDrStdTypeList(patTypeListBuilder.toString()));
        standardInfoBO.setDrStandardNum(stdNum);
        standardInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return standardInfoBO;
    }

    /**
     * 批量更新标准信息
     *
     * @param standardInfoBOList 标准信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchStandardInfo(List<BusinessStandardInfoBO> standardInfoBOList) {
        List<BusinessStandardInfoBO> handleList = new ArrayList<>();
        long idStartPosition = standardInfoBOList.get(0).getId();
        long idEndPosition = standardInfoBOList.get(standardInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessStandardInfoBO standardInfoBO : standardInfoBOList) {
                handleList.add(handleStdInfo(standardInfoBO));
            }
            logger.info("id区间为：[{},{}]，处理标准信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, standardInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchStandardInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表标准信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新标准信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }

        return true;
    }

    /**
     * 是否在经营期限内
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @param checkDate 检测时间
     * @return boolean
     */
    private boolean isInOperationPeriod(Date startDate, Date endDate, Date checkDate) {
        boolean inFlag = checkDate == null || (startDate == null && endDate == null)
                || (startDate == null && checkDate.compareTo(endDate) <= 0)
                || (endDate == null && checkDate.compareTo(startDate) >= 0)
                || (checkDate.compareTo(startDate) >= 0 && checkDate.compareTo(endDate) <= 0);
        logger.debug("startDate：【{}】，startDate：【{}】，checkDate：【{}】，检验结果：【{}】", startDate, endDate, checkDate, inFlag);
        return inFlag;
    }

    /**
     * 初始化标准相关信息
     *
     * @param standardInfoBO
     * @return BusinessStandardInfoBO
     */
    private BusinessStandardInfoBO initBusinessStandardInfoBO(BusinessStandardInfoBO standardInfoBO) {
        standardInfoBO.setHasDrStandard(WhetherFlagEnum.NO.getValue());
        standardInfoBO.setDrStandardTypeList("");
        standardInfoBO.setDrStandardNum(0L);
        return standardInfoBO;
    }
}

