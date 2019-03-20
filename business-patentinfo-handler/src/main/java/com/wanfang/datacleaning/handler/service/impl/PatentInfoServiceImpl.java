package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.WhetherFlagEnum;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.master.TblPatentDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessPatentInfoBO;
import com.wanfang.datacleaning.handler.model.bo.PatentPatTypeBO;
import com.wanfang.datacleaning.handler.service.PatentInfoService;
import com.wanfang.datacleaning.handler.util.business.PatentDataUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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
     * 正则表达式：日期
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("^[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}$");
    /**
     * 更新专利信息成功数量
     */
    private int patentInfoUpdSuccessCount;
    /**
     * 更新专利信息失败数量
     */
    private int patentInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;
    @Resource
    private TblPatentDao tblPatentDao;

    @Override
    public void handlePatentInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的专利信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新专利信息
            updatePatentInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的专利信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
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

        // 缓存专利数据信息
        boolean cacheSuccessFlag = cacheBasePatentTypeInfo();
        if (!cacheSuccessFlag) {
            logger.warn("缓存专利表的专利类型信息失败");
            return false;
        }
        return true;
    }

    /**
     * 缓存专利数据信息
     *
     * @return boolean true：缓存成功
     */
    private boolean cacheBasePatentTypeInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("缓存DB专利表的专利类型信息开始");
        // 递归缓存
        cacheBasePatentTypByRecursion(0, CmnConstant.PAGE_SIZE);

        int cacheSize = PatentDataUtils.getCachePatTypeInfoMapWithFilterSize();
        if (cacheSize > 0) {
            logger.info("缓存DB专利表的专利类型信息结束，共缓存【{}】条数据，耗时【{}】ms", cacheSize, System.currentTimeMillis() - startTime);
            return true;
        }

        logger.info("缓存DB专利表的专利类型信息结束，共缓存【{}】条数据，耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    /**
     * 递归缓存专利类型信息
     *
     * @param idStartPosition id起始位置
     * @param pageSize        每页数量
     */
    private void cacheBasePatentTypByRecursion(int idStartPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        logger.info("查询DB专利表的专利类型信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<PatentPatTypeBO> patTypeBOList = tblPatentDao.getBasePatentTypeInfoByPage(idStartPosition, pageSize);
        int qryResultSize = patTypeBOList.size();
        logger.info("查询DB专利表的专利类型信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (patTypeBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = patTypeBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("缓存DB专利表的专利类型信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        PatentDataUtils.cachePatTypeInfoMapWithFilter(patTypeBOList);
        logger.info("缓存DB专利表的专利类型信息结束，id区间为：[{},{}]，共缓存【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize) {
            cacheBasePatentTypByRecursion(lastPosition + 1, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归更新专利信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updatePatentInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的专利信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessPatentInfoBO> patentInfoBOList = tblBusinessDao.getPatentInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = patentInfoBOList.size();
        logger.info("查询DB工商表的专利信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (patentInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = patentInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的专利信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessPatentInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(patentInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新专利信息
                if (updateBatchPatentInfo(batchList)) {
                    patentInfoUpdSuccessCount += batchList.size();
                } else {
                    patentInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的专利信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updatePatentInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理专利信息
     *
     * @param patentInfoBO 专利信息
     * @return BusinessPatentInfoBO
     */
    private BusinessPatentInfoBO handlePatentInfo(BusinessPatentInfoBO patentInfoBO) {
        // 判断主体身份代码是否为空
        if (StringUtils.isBlank(patentInfoBO.getPripid())) {
            patentInfoBO = initBusinessPatentInfoBO(patentInfoBO);
            patentInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return patentInfoBO;
        }

        // 去除空白字符
        String entName = StringUtils.deleteWhitespace(patentInfoBO.getEntName());
        Map<String, List<PatentPatTypeBO>> patTypeBOMap = PatentDataUtils.getCachePatTypeInfoMapWithFilter();
        List<PatentPatTypeBO> patTypeBOList = patTypeBOMap.get(entName);
        if (patTypeBOList == null || patTypeBOList.isEmpty()) {
            patentInfoBO = initBusinessPatentInfoBO(patentInfoBO);
            patentInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return patentInfoBO;
        }

        int hasPatent = WhetherFlagEnum.NO.getValue();
        long patentNum = 0;
        StringBuilder patTypeListBuilder = new StringBuilder();
        for (PatentPatTypeBO patTypeBO : patTypeBOList) {
            // 判断“申请日”是否在经营期限内
            if (!isInOperationPeriod(patentInfoBO.getOpFrom(), patentInfoBO.getOpTo(), StringUtils.deleteWhitespace(patTypeBO.getAppDate()))) {
                continue;
            }
            hasPatent = WhetherFlagEnum.YES.getValue();
            // 专利类型枚举值列表，格式：1,2,3
            patTypeListBuilder.append(CmnConstant.SEPARATOR_COMMA).append(PatentDataUtils.getPatentTypeCode(patTypeBO.getPatentType(), patTypeBO.getPatentType()));
            // 专利数量
            patentNum++;
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
     * @param patentInfoBOList 专利信息集合
     * @return boolean
     */
    private boolean updateBatchPatentInfo(List<BusinessPatentInfoBO> patentInfoBOList) {
        List<BusinessPatentInfoBO> handleList = new ArrayList<>();
        long idStartPosition = patentInfoBOList.get(0).getId();
        long idEndPosition = patentInfoBOList.get(patentInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessPatentInfoBO patentInfoBO : patentInfoBOList) {
                handleList.add(handlePatentInfo(patentInfoBO));
            }
            logger.info("id区间为：[{},{}]，处理专利信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, patentInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchPatentInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表专利信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新专利信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }

        return true;
    }

    /**
     * 是否在经营期限内
     *
     * @param startDate    起始时间
     * @param endDate      结束时间
     * @param checkDateStr 检测时间
     * @return boolean
     */
    private boolean isInOperationPeriod(Date startDate, Date endDate, String checkDateStr) {
        if (StringUtils.isBlank(checkDateStr)) {
            return true;
        }

        if (DATE_PATTERN.matcher(checkDateStr).matches()) {
            Date checkDate = convertStringToDate(checkDateStr.replace(".", "-"));
            boolean inFlag = checkDate == null || (startDate == null && endDate == null)
                    || (startDate == null && checkDate.compareTo(endDate) <= 0)
                    || (endDate == null && checkDate.compareTo(startDate) >= 0)
                    || (startDate != null && endDate != null && checkDate.compareTo(startDate) >= 0 && checkDate.compareTo(endDate) <= 0);
            logger.debug("startDate：【{}】，startDate：【{}】，checkDateStr：【{}】，检验结果：【{}】", startDate, endDate, checkDateStr, inFlag);
            return inFlag;
        }

        return true;
    }

    /**
     * 转换String为Date
     *
     * @param dateStr 日期字符串 格式：2018-11-11
     * @return Date
     */
    private Date convertStringToDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 初始化专利相关信息
     *
     * @param patentInfoBO 专利相关信息
     * @return BusinessPatentInfoBO
     */
    private BusinessPatentInfoBO initBusinessPatentInfoBO(BusinessPatentInfoBO patentInfoBO) {
        patentInfoBO.setHasPatent(WhetherFlagEnum.NO.getValue());
        patentInfoBO.setPatentTypeList("");
        patentInfoBO.setPatentNum(0L);
        return patentInfoBO;
    }
}

