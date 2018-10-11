package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.dao.dao.master.TblStandardDao;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessStandardInfoBO;
import com.wanfang.datacleaning.dao.model.master.bo.StandardStdTypeBO;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.service.StandardInfoService;
import com.wanfang.datacleaning.handler.util.business.StandardDataUtils;
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
import java.util.Map;

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

    @Autowired
    private TblBusinessDao tblBusinessDao;
    @Autowired
    private TblStandardDao tblStandardDao;

    @Override
    public boolean cacheBaseStandardTypeInfo() {
        LoggerUtils.appendInfoLog(logger, "*********** 缓存DB标准表的标准类型信息开始 ***********");
        long startTime = System.currentTimeMillis();

        // 递归缓存
        cacheeBaseStandardTypeInfoByRecursion(0, CmnConstant.DEFAULT_PAGE_SIZE);
        Map<String, List<StandardStdTypeBO>> stdTypeBOMap = StandardDataUtils.getCacheStdTypeInfoMapWithFilter();
        if (stdTypeBOMap != null && stdTypeBOMap.size() > 0) {
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB标准表的标准类型信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", stdTypeBOMap.size(), System.currentTimeMillis() - startTime);
            return true;
        }

        LoggerUtils.appendInfoLog(logger, "*********** 缓存DB标准表的标准类型信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    @Override
    public void updateStandardInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的专利信息开始 ***********");

        // 递归更新标准信息
        updateStandardInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的专利信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                (stdInfoUpdSuccessCount + stdInfoUpdFailCount), stdInfoUpdSuccessCount, stdInfoUpdFailCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归缓存标准类型信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void cacheeBaseStandardTypeInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB标准表的第【{}】页标准类型信息开始 ***********", pageNum);
        List<StandardStdTypeBO> stdTypeBOList = tblStandardDao.getBaseStandardTypeInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB标准表的第【{}】页标准类型信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, stdTypeBOList.size(), System.currentTimeMillis() - startTime);

        if (stdTypeBOList != null && stdTypeBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB标准表的第【{}】页标准类型信息开始 ***********", pageNum);
            StandardDataUtils.cacheStdTypeInfoMapWithFilter(stdTypeBOList);
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB标准表的第【{}】页标准类型信息结束,共缓存【{}】条数据，耗时【{}】ms ***********", pageNum, stdTypeBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (stdTypeBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                cacheeBaseStandardTypeInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 递归更新标准信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateStandardInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页标准信息开始 ***********", pageNum);
        List<BusinessStandardInfoBO> standardInfoBOList = tblBusinessDao.getStandardInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页标准信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, standardInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (standardInfoBOList != null && standardInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页标准信息开始 ***********", pageNum);
            List<BusinessStandardInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < standardInfoBOList.size(); i++) {
                batchList.add(standardInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == standardInfoBOList.size()) {
                    // 批量更新标准信息
                    if (updateBatchStandardInfo(batchList)) {
                        stdInfoUpdSuccessCount += batchList.size();
                    } else {
                        stdInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页标准信息结束,共更新【{}】条数据，耗时【{}】ms ***********", pageNum, standardInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (standardInfoBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                updateStandardInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理标准信息
     *
     * @param standardInfoBO
     * @return BusinessStandardInfoBO
     */
    private BusinessStandardInfoBO handleStdInfo(BusinessStandardInfoBO standardInfoBO) {
        Map<String, List<StandardStdTypeBO>> stdTypeBOMap = StandardDataUtils.getCacheStdTypeInfoMapWithFilter();
        int hasSrStd = CmnEnum.WhetherFlagEnum.NO.getValue();
        StringBuilder patTypeListBuilder = new StringBuilder("");
        long stdNum = 0;
        String draftUnit;
        String entName = standardInfoBO.getEntName();

        List<StandardStdTypeBO> stdTypeBOList = stdTypeBOMap.get(entName);
        if (stdTypeBOList == null || stdTypeBOList.isEmpty()) {
            standardInfoBO.setHasDrStandard(hasSrStd);
            standardInfoBO.setDrStandardTypeList("");
            standardInfoBO.setDrStandardNum(stdNum);
            standardInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return standardInfoBO;
        }

        for (StandardStdTypeBO stdTypeBO : stdTypeBOList) {
            draftUnit = stdTypeBO.getDraftUnit();
            // 关联标识
            boolean relationFlag = stdTypeBO != null && StringUtils.isNotEmpty(draftUnit) && draftUnit.contains(entName);
            if (relationFlag) {
                // 是否是否参与起草标准
                if (StandardDataUtils.meetStdStatus(stdTypeBO.getStandardStatus())) {
                    hasSrStd = CmnEnum.WhetherFlagEnum.YES.getValue();
                }
                // 起草标准类型列表，格式：1,2,3
                patTypeListBuilder.append(StandardDataUtils.getDrStdType(stdTypeBO.getStandardType()));
                // 标准数量
                stdNum++;
            }
        }

        standardInfoBO.setHasDrStandard(hasSrStd);
        standardInfoBO.setDrStandardTypeList(StandardDataUtils.handleStdTypeList(patTypeListBuilder.toString()));
        standardInfoBO.setDrStandardNum(stdNum);
        standardInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return standardInfoBO;
    }

    /**
     * 批量更新标准信息
     *
     * @param standardInfoBOList
     * @return boolean
     */
    private boolean updateBatchStandardInfo(List<BusinessStandardInfoBO> standardInfoBOList) {
        List<BusinessStandardInfoBO> handleList = new ArrayList<>();
        try {
            for (BusinessStandardInfoBO standardInfoBO : standardInfoBOList) {
                if (standardInfoBO != null) {
                    handleList.add(handleStdInfo(standardInfoBO));
                }
            }
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchStandardInfoByKey(handleList);
            }
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新标准信息(updateBatchStandardInfo())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }

        return true;
    }
}

