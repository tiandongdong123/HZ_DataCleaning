package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.WhetherFlagEnum;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessExtraFieldBO;
import com.wanfang.datacleaning.handler.service.ExtraFiledService;
import com.wanfang.datacleaning.handler.util.business.DomPropertyUtils;
import com.wanfang.datacleaning.handler.util.business.EntStatusUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("extraFiledService")
public class ExtraFiledServiceImpl implements ExtraFiledService {
    private static final Logger logger = LoggerFactory.getLogger(ExtraFiledServiceImpl.class);

    /**
     * 更新拓展字段成功数量
     */
    private int extraFieldInfoUpdSuccessCount;
    /**
     * 更新拓展字段失败数量
     */
    private int extraFieldInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleExtraFieldInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的拓展字段信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新拓展字段
            updateExtraFieldInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的拓展字段信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (extraFieldInfoUpdSuccessCount + extraFieldInfoUpdFailCount),
                    extraFieldInfoUpdSuccessCount, extraFieldInfoUpdFailCount, System.currentTimeMillis() - startTime);
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
        if (DomPropertyUtils.getCacheCodeMapSize() < 1) {
            logger.warn("文件：【{}】,sheet：【{}】，代码数据为空", DomPropertyUtils.DOM_PROPERTY_CODE_FILE_PATH, DomPropertyUtils.DOM_PROPERTY_CODE_SHEET_NAME);
            return false;
        }
        return true;
    }

    /**
     * 递归更新拓展字段
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateExtraFieldInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的拓展字段信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessExtraFieldBO> extraFieldBOList = tblBusinessDao.getExtraFieldInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = extraFieldBOList.size();
        logger.info("查询DB工商表的拓展字段信息结束，idStartPosition：【{}】，pageSize：【{}】,共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (extraFieldBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = extraFieldBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的拓展字段信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessExtraFieldBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(extraFieldBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新拓展字段信息
                if (updateBatchExtraFieldInfo(batchList)) {
                    extraFieldInfoUpdSuccessCount += batchList.size();
                } else {
                    extraFieldInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的拓展字段信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量且最后一条的id位置小于结束位置，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateExtraFieldInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理拓展字段
     *
     * @param fieldBO 拓展字段信息
     * @return BusinessExtraFieldBO
     */
    private BusinessExtraFieldBO handleBusinessExtraField(BusinessExtraFieldBO fieldBO) {
        // 企业状态
        String entStatus = fieldBO.getEntStatus();
        if (StringUtils.isNotBlank(entStatus)) {
            fieldBO.setEntStatus(EntStatusUtils.getEntStatusCode(entStatus, entStatus));
        }

        // 成立年份、月份
        Date esDate = fieldBO.getEsDate();
        if (esDate != null) {
            fieldBO.setEsYear(Integer.parseInt(DateUtils.format(esDate, DateUtils.YEAR_FORMAT)));
            fieldBO.setEsMonth(Integer.parseInt(DateUtils.format(esDate, DateUtils.MONTH_M_FORMAT)));
        } else {
            fieldBO.setEsYear(0);
            fieldBO.setEsMonth(0);
        }

        // 住所产权
        String domProRight = fieldBO.getDomProRight();
        if (StringUtils.isNotBlank(domProRight)) {
            fieldBO.setDomProRight(DomPropertyUtils.getCodeWithoutZero(domProRight, domProRight));
        }

        // 是否有许可经营项目
        if (StringUtils.isNotBlank(fieldBO.getAbuItem())) {
            fieldBO.setHasAbuItem(WhetherFlagEnum.YES.getValue());
        } else {
            fieldBO.setHasAbuItem(WhetherFlagEnum.NO.getValue());
        }

        // 更新时间（10位时间戳）
        fieldBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return fieldBO;
    }

    /**
     * 批量更新拓展字段
     *
     * @param extraFieldBOList 拓展字段信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchExtraFieldInfo(List<BusinessExtraFieldBO> extraFieldBOList) {
        List<BusinessExtraFieldBO> handleList = new ArrayList<>();
        long idStartPosition = extraFieldBOList.get(0).getId();
        long idEndPosition = extraFieldBOList.get(extraFieldBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessExtraFieldBO fieldBO : extraFieldBOList) {
                handleList.add(handleBusinessExtraField(fieldBO));
            }
            logger.info("id区间为：[{},{}]，处理拓展字段信息【{}】条,耗时【{}】ms", idStartPosition, idEndPosition, extraFieldBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchExtraFieldByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，批量更新DB工商表拓展字段信息【{}】条,耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新拓展字段出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }
}

