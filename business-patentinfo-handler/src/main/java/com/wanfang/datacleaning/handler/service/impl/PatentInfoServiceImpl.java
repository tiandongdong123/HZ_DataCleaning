package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.dao.dao.master.TblPatentDao;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessPatentInfoBO;
import com.wanfang.datacleaning.dao.model.master.bo.PatentPatTypeBO;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.service.PatentInfoService;
import com.wanfang.datacleaning.handler.util.business.PatentDataUtils;
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
@Service("patentInfoService")
public class PatentInfoServiceImpl implements PatentInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PatentInfoServiceImpl.class);

    /**
     * 更新专利信息成功数量
     */
    private int patentInfoUpdSuccessCount;
    /**
     * 更新专利信息失败数量
     */
    private int patentInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;
    @Autowired
    private TblPatentDao tblPatentDao;

    @Override
    public boolean cacheBasePatentTypeInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 缓存DB专利表的专利类型信息开始 ***********");

        // 递归缓存
        cacheBasePatentTypByRecursion(0, CmnConstant.DEFAULT_PAGE_SIZE);

        Map<String, List<PatentPatTypeBO>> patTypeBOMap = PatentDataUtils.getCachePatTypeInfoMapWithFilter();
        if (patTypeBOMap != null && patTypeBOMap.size() > 0) {
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB专利表的专利类型信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", patTypeBOMap.size(), System.currentTimeMillis() - startTime);
            return true;
        }

        LoggerUtils.appendInfoLog(logger, "*********** 缓存DB专利表的专利类型信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    @Override
    public void updatePatentInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的专利信息开始 ***********");

        // 递归更新专利信息
        updatePatentInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的专利信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                (patentInfoUpdSuccessCount + patentInfoUpdFailCount), patentInfoUpdSuccessCount, patentInfoUpdFailCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归缓存专利类型信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void cacheBasePatentTypByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB专利表的第【{}】页专利类型信息开始 ***********", pageNum);
        List<PatentPatTypeBO> patTypeBOList = tblPatentDao.getBasePatentTypeInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB专利表的第【{}】页专利类型信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, patTypeBOList.size(), System.currentTimeMillis() - startTime);

        if (patTypeBOList != null && patTypeBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB专利表的第【{}】页专利类型信息开始 ***********", pageNum);
            PatentDataUtils.cachePatTypeInfoMapWithFilter(patTypeBOList);
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB专利表的第【{}】页专利类型信息结束,共缓存【{}】条数据，耗时【{}】ms ***********", pageNum, patTypeBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (patTypeBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                cacheBasePatentTypByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 递归更新专利信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updatePatentInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页专利信息开始 ***********", pageNum);
        List<BusinessPatentInfoBO> patentInfoBOList = tblBusinessDao.getPatentInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页专利信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, patentInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (patentInfoBOList != null && patentInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页专利信息开始 ***********", pageNum);
            List<BusinessPatentInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < patentInfoBOList.size(); i++) {
                batchList.add(patentInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == patentInfoBOList.size()) {
                    // 批量更新专利信息
                    if (updateBatchPatentInfo(batchList)) {
                        patentInfoUpdSuccessCount += batchList.size();
                    } else {
                        patentInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页专利信息结束,共更新【{}】条数据，耗时【{}】ms ***********", pageNum, patentInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (patentInfoBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                updatePatentInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理专利信息
     *
     * @param patentInfoBO
     * @return BusinessPatentInfoBO
     */
    private BusinessPatentInfoBO handlePatentInfo(BusinessPatentInfoBO patentInfoBO) {
        Map<String, List<PatentPatTypeBO>> patTypeBOMap = PatentDataUtils.getCachePatTypeInfoMapWithFilter();
        int hasPatent = CmnEnum.WhetherFlagEnum.NO.getValue();
        StringBuilder patTypeListBuilder = new StringBuilder("");
        long patentNum = 0;
        String entName = patentInfoBO.getEntName();

        List<PatentPatTypeBO> patTypeBOList = patTypeBOMap.get(entName);
        if (patTypeBOList == null || patTypeBOList.isEmpty()) {
            patentInfoBO.setHasPatent(hasPatent);
            patentInfoBO.setPatentTypeList("");
            patentInfoBO.setPatentNum(patentNum);
            patentInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return patentInfoBO;
        }

        for (PatentPatTypeBO patTypeBO : patTypeBOList) {
            // 是否拥有专利
            boolean hasPatentFlag = patTypeBO != null && StringUtils.isNotEmpty(patTypeBO.getProposerName())
                    && StringUtils.isNotEmpty(entName)
                    && patTypeBO.getProposerName().contains(entName);
            if (hasPatentFlag) {
                hasPatent = CmnEnum.WhetherFlagEnum.YES.getValue();
                // 专利类型枚举值列表，格式：1,2,3
                patTypeListBuilder.append(PatentDataUtils.getPatentTypeCode(patTypeBO.getPatentType()));
                // 专利数量
                patentNum++;
            }
        }

        patentInfoBO.setHasPatent(hasPatent);
        patentInfoBO.setPatentTypeList(PatentDataUtils.handlePatTypeList(patTypeListBuilder.toString()));
        patentInfoBO.setPatentNum(patentNum);
        patentInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return patentInfoBO;
    }

    /**
     * 批量更新专利信息
     *
     * @param patentInfoBOList
     * @return boolean
     */
    private boolean updateBatchPatentInfo(List<BusinessPatentInfoBO> patentInfoBOList) {
        List<BusinessPatentInfoBO> handleList = new ArrayList<>();
        try {
            for (BusinessPatentInfoBO patentInfoBO : patentInfoBOList) {
                if (patentInfoBO != null) {
                    handleList.add(handlePatentInfo(patentInfoBO));
                }
            }
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchPatentInfoByKey(handleList);
            }
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新专利信息(updateBatchPatentInfo())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }

        return true;
    }
}

