package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import com.wanfang.datacleaning.handler.constant.ResultTypeEnum;
import com.wanfang.datacleaning.handler.dao.master.*;
import com.wanfang.datacleaning.handler.model.bo.BusinessEntNameBO;
import com.wanfang.datacleaning.handler.model.bo.CstadCompUnitBO;
import com.wanfang.datacleaning.handler.model.bo.PatentProposerNameBO;
import com.wanfang.datacleaning.handler.model.bo.StandardDraftUnitBO;
import com.wanfang.datacleaning.handler.model.entity.master.TblEntResultEntity;
import com.wanfang.datacleaning.handler.service.EnterpriseResultService;
import com.wanfang.datacleaning.handler.util.business.BusinessDataUtils;
import com.wanfang.datacleaning.handler.util.business.CommonUtils;
import com.wanfang.datacleaning.handler.util.business.ThreadUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("enterpriseResultService")
public class EnterpriseResultServiceImpl implements EnterpriseResultService {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseResultServiceImpl.class);
    private static final Logger patentResultLogger = LoggerFactory.getLogger(LoggerEnum.PATENT_RESULT.getValue());
    private static final Logger stdResultLogger = LoggerFactory.getLogger(LoggerEnum.STD_RESULT.getValue());
    private static final Logger cstadResultLogger = LoggerFactory.getLogger(LoggerEnum.CSTAD_RESULT.getValue());

    /**
     * 正则表达式：日期
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("^[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}$");
    /**
     * 正则表达式：日期
     */
    private static final Pattern DATE_PATTERN2 = Pattern.compile("^[0-9]{8}$");
    /**
     * 分隔符-短中划线（-）
     */
    private static final String SEPARATOR_SHORT_MIDDLE_LINE = "-";

    /**
     * 同步成果表企业[专利]成功数量
     */
    private int patentSuccessCount;
    /**
     * 同步成果表企业[专利]失败数量
     */
    private int patentFailCount;
    /**
     * 同步成果表企业[标准]成功数量
     */
    private int stdSuccessCount;
    /**
     * 同步成果表企业[标准]失败数量
     */
    private int stdFailCount;
    /**
     * 同步成果表企业[成果]成功数量
     */
    private int cstadSuccessCount;
    /**
     * 同步成果表企业[成果]失败数量
     */
    private int cstadSyncFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;
    @Resource
    private TblPatentDao tblPatentDao;
    @Resource
    private TblPatentResultDao tblPatentResultDao;
    @Resource
    private TblStandardDao tblStandardDao;
    @Resource
    private TblStandardResultDao tblStandardResultDao;
    @Resource
    private TblCstadDao tblCstadDao;
    @Resource
    private TblCstadResultDao tblCstadResultDao;

    @Override
    public void handleEntResultInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);
        if (!meetFlag) {
            return;
        }

        String[] syncResultTypeArray = StringUtils.split(CmnConstant.SYNC_RESULT_TYPE, CmnConstant.SEPARATOR_COMMA);
        logger.info("同步的成果类型为：【{}】", JSON.toJSONString(syncResultTypeArray));
        if (syncResultTypeArray != null && syncResultTypeArray.length > 0) {
            for (int i = 0; i < syncResultTypeArray.length; i++) {
                if (ResultTypeEnum.PATENT.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTimeMillis = System.currentTimeMillis();
                        patentResultLogger.info("递归同步企业成果[专利]开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
                        syncPatentResultInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
                        patentResultLogger.info("递归同步企业成果[专利]结束，id更新区间为：[{},{}]，共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (patentSuccessCount + patentFailCount), patentSuccessCount, patentFailCount, System.currentTimeMillis() - startTimeMillis);
                    });
                    continue;
                }

                if (ResultTypeEnum.STANDARD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTimeMillis = System.currentTimeMillis();
                        stdResultLogger.info("递归同步企业成果[标准]开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
                        syncStandardResultInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
                        stdResultLogger.info("递归同步企业成果[标准]结束，id更新区间为：[{},{}]，共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (stdSuccessCount + stdFailCount), stdSuccessCount, stdFailCount, System.currentTimeMillis() - startTimeMillis);
                    });
                    continue;
                }

                if (ResultTypeEnum.CSTAD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTimeMillis = System.currentTimeMillis();
                        cstadResultLogger.info("递归同步企业成果[成果]开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
                        syncCstadResultInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
                        cstadResultLogger.info("递归同步企业成果[成果]结束，id更新区间为：[{},{}]，共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (cstadSuccessCount + cstadSyncFailCount), cstadSuccessCount, cstadSyncFailCount, System.currentTimeMillis() - startTimeMillis);
                    });
                    continue;
                }
            }
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

        // 缓存企业信息
        boolean cacheSuccessFlag = cacheBaseEntNameInfo();
        if (!cacheSuccessFlag) {
            logger.warn("缓存企业信息失败");
            return false;
        }
        return true;
    }

    /**
     * 缓存企业信息
     *
     * @return boolean true: 缓存成功
     */
    private boolean cacheBaseEntNameInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("缓存工商表的企业(机构)名称信息开始");
        // 递归缓存
        cacheBaseEntNameInfoByRecursion(0, CmnConstant.PAGE_SIZE);
        int cacheEntNameInfoMapSize = BusinessDataUtils.getCacheEntNameInfoMapSize();
        if (cacheEntNameInfoMapSize > 0) {
            logger.info("缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms", cacheEntNameInfoMapSize, System.currentTimeMillis() - startTime);
            return true;
        }

        logger.info("缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    /**
     * 递归缓存企业(机构)名称信息
     *
     * @param idStartPosition id起始位置
     * @param pageSize        每页数量
     */
    private void cacheBaseEntNameInfoByRecursion(int idStartPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        logger.info("查询DB工商表的企业(机构)名称信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessEntNameBO> entNameBOList = tblBusinessDao.getBaseEntNameInfoByPage(idStartPosition, pageSize);
        int qryResultSize = entNameBOList.size();
        logger.info("查询DB工商表的企业(机构)名称信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (entNameBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = entNameBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("缓存DB工商表的企业(机构)名称信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        BusinessDataUtils.cacheEntNameInfo(entNameBOList);
        logger.info("缓存DB工商表的企业(机构)名称信息结束，id区间为：[{},{}]，共缓存【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize) {
            cacheBaseEntNameInfoByRecursion(lastPosition + 1, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归同步企业成果[专利]
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void syncPatentResultInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        patentResultLogger.info("查询DB专利表的申请（专利权）人信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<PatentProposerNameBO> proposerNameBOList = tblPatentDao.getBaseProposerNameInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = proposerNameBOList.size();
        patentResultLogger.info("查询DB专利表的申请（专利权）人信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (proposerNameBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = proposerNameBOList.get(qryResultSize - 1).getId().intValue();
        patentResultLogger.info("添加企业成果[专利]信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        for (PatentProposerNameBO proposerNameBO : proposerNameBOList) {
            if (StringUtils.isBlank(proposerNameBO.getProposerName())) {
                continue;
            }

            long handleStartTime = System.currentTimeMillis();
            patentResultLogger.info("处理企业成果[专利]信息开始，id为：【{}】", proposerNameBO.getId());
            List<TblEntResultEntity> handleList = handlePatentResult(proposerNameBO);
            patentResultLogger.info("处理企业成果[专利]信息结束，id为：【{}】，处理结果数：【{}】，耗时：【{}】ms", proposerNameBO.getId(), handleList.size(), System.currentTimeMillis() - handleStartTime);
            // 添加企业成果信息
            if (addBatch(tblPatentResultDao, handleList)) {
                patentSuccessCount++;
            } else {
                patentFailCount++;
            }
        }
        patentResultLogger.info("添加企业成果[专利]信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            syncPatentResultInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归同步企业成果[标准]
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void syncStandardResultInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        stdResultLogger.info("查询DB标准表的起草单位信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<StandardDraftUnitBO> draftUnitBOList = tblStandardDao.getBaseDraftUnitInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = draftUnitBOList.size();
        stdResultLogger.info("查询DB标准表的起草单位信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        int lastPosition = draftUnitBOList.get(qryResultSize - 1).getId().intValue();
        stdResultLogger.info("添加企业成果[标准]信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        for (StandardDraftUnitBO draftUnitBO : draftUnitBOList) {
            if (StringUtils.isBlank(draftUnitBO.getDraftUnit())) {
                continue;
            }

            long handleStartTime = System.currentTimeMillis();
            patentResultLogger.info("处理企业成果[标准]信息开始，id为：【{}】", draftUnitBO.getId());
            List<TblEntResultEntity> handleList = handleStandardResult(draftUnitBO);
            patentResultLogger.info("处理企业成果[标准]信息结束，id为：【{}】，处理结果数：【{}】，耗时：【{}】ms", draftUnitBO.getId(), handleList.size(), System.currentTimeMillis() - handleStartTime);
            // 添加企业成果信息
            if (addBatch(tblStandardResultDao, handleList)) {
                stdSuccessCount++;
            } else {
                stdFailCount++;
            }
        }
        stdResultLogger.info("添加企业成果[标准]信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            syncStandardResultInfoByRecursion(idStartPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 递归同步企业成果[成果]
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void syncCstadResultInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        cstadResultLogger.info("查询DB成果表的完成单位信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<CstadCompUnitBO> compUnitBOList = tblCstadDao.getBaseCompUnitInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = compUnitBOList.size();
        cstadResultLogger.info("查询DB成果表的完成单位信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (compUnitBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = compUnitBOList.get(qryResultSize - 1).getId().intValue();
        cstadResultLogger.info("添加企业成果[成果]信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        for (CstadCompUnitBO compUnitBO : compUnitBOList) {
            if (StringUtils.isBlank(compUnitBO.getCompUnit()) || compUnitBO.getResultId() == null) {
                continue;
            }

            long handleStartTime = System.currentTimeMillis();
            patentResultLogger.info("处理企业成果[成果]信息开始，id为：【{}】", compUnitBO.getId());
            List<TblEntResultEntity> handleList = handleCstadResult(compUnitBO);
            patentResultLogger.info("处理企业成果[成果]信息结束，id为：【{}】，处理结果数：【{}】，耗时：【{}】ms", compUnitBO.getId(), handleList.size(), System.currentTimeMillis() - handleStartTime);
            // 添加企业成果信息
            if (addBatch(tblCstadResultDao, handleList)) {
                cstadSuccessCount++;
            } else {
                cstadSyncFailCount++;
            }
        }
        cstadResultLogger.info("添加企业成果[成果]信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            syncCstadResultInfoByRecursion(idStartPosition, CmnConstant.ID_START_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理成果[专利]信息
     *
     * @param patentProposerNameBO 成果[专利]信息
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handlePatentResult(PatentProposerNameBO patentProposerNameBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] proposerNameArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(patentProposerNameBO.getProposerName()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < proposerNameArray.length; i++) {
            // 判断申请（专利权）人是否为空
            if (StringUtils.isBlank(proposerNameArray[i])) {
                continue;
            }
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(proposerNameArray[i]);
            // 判断企业是否存在
            if (entNameBOList == null || entNameBOList.isEmpty()) {
                continue;
            }

            String pripid;
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isBlank(pripid)) {
                    continue;
                }
                // 判断申请号是否为空
                if (StringUtils.isBlank(patentProposerNameBO.getPatentId())) {
                    continue;
                }
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriodForPatent(entNameBO.getOpFrom(), entNameBO.getOpTo(), StringUtils.deleteWhitespace(patentProposerNameBO.getAppDate()))) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(proposerNameArray[i]);
                entResultEntity.setResultNum(patentProposerNameBO.getPatentId());
                entResultEntity.setResultType(ResultTypeEnum.PATENT.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());
                handleList.add(entResultEntity);
            }
        }

        return handleList;
    }

    /**
     * 处理成果[标准]信息
     *
     * @param standardDraftUnitBO 成果[标准]信息
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handleStandardResult(StandardDraftUnitBO standardDraftUnitBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] draftUnitArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(standardDraftUnitBO.getDraftUnit()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < draftUnitArray.length; i++) {
            // 判断起草单位是否为空
            if (StringUtils.isBlank(draftUnitArray[i])) {
                continue;
            }
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(draftUnitArray[i]);
            // 判断企业是否存在
            if (entNameBOList == null || entNameBOList.isEmpty()) {
                continue;
            }

            String pripid;
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isBlank(pripid)) {
                    continue;
                }
                // 判断标准编号是否为空
                if (StringUtils.isBlank(standardDraftUnitBO.getStandNum())) {
                    continue;
                }
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriod(entNameBO.getOpFrom(), entNameBO.getOpTo(), standardDraftUnitBO.getIssueDate())) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(draftUnitArray[i]);
                entResultEntity.setResultNum(standardDraftUnitBO.getStandNum());
                entResultEntity.setResultType(ResultTypeEnum.STANDARD.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());
                handleList.add(entResultEntity);
            }
        }

        return handleList;
    }

    /**
     * 处理成果[成果]信息
     *
     * @param cstadCompUnitBO 成果[成果]信息
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handleCstadResult(CstadCompUnitBO cstadCompUnitBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] compUnitArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(cstadCompUnitBO.getCompUnit()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < compUnitArray.length; i++) {
            // 判断完成单位是否为空
            if (StringUtils.isBlank(compUnitArray[i])) {
                continue;
            }
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(compUnitArray[i]);
            // 判断企业是否存在
            if (entNameBOList == null || entNameBOList.isEmpty()) {
                continue;
            }

            String pripid;
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isBlank(pripid)) {
                    continue;
                }
                // 判断编号是否为空
                if (cstadCompUnitBO.getResultId() == null) {
                    continue;
                }
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriodForCstad(entNameBO.getOpFrom(), entNameBO.getOpTo(), StringUtils.deleteWhitespace(cstadCompUnitBO.getDeclareDate()))) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(compUnitArray[i]);
                entResultEntity.setResultNum(cstadCompUnitBO.getResultId() + "");
                entResultEntity.setResultType(ResultTypeEnum.CSTAD.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());
                handleList.add(entResultEntity);
            }
        }

        return handleList;
    }

    /**
     * 是否在经营期限内[专利]
     *
     * @param startDate    起始时间
     * @param endDate      结束时间
     * @param checkDateStr 检测时间
     * @return boolean true：在经营期限内
     */
    private boolean isInOperationPeriodForPatent(Date startDate, Date endDate, String checkDateStr) {
        if (StringUtils.isBlank(checkDateStr)) {
            return true;
        }

        if (DATE_PATTERN.matcher(checkDateStr).matches()) {
            Date checkDate = convertStringToDate(checkDateStr.replace(".", SEPARATOR_SHORT_MIDDLE_LINE));
            return isInOperationPeriod(startDate, endDate, checkDate);
        }
        return true;
    }

    /**
     * 是否在经营期限内[成果]
     *
     * @param startDate    起始时间
     * @param endDate      结束时间
     * @param checkDateStr 检测时间
     * @return boolean true：在经营期限内
     */
    private boolean isInOperationPeriodForCstad(Date startDate, Date endDate, String checkDateStr) {
        if (StringUtils.isBlank(checkDateStr)) {
            return true;
        }

        if (DATE_PATTERN2.matcher(checkDateStr).matches()) {
            checkDateStr = checkDateStr.substring(0, 4) + SEPARATOR_SHORT_MIDDLE_LINE + checkDateStr.substring(4, 6)
                    + SEPARATOR_SHORT_MIDDLE_LINE + checkDateStr.substring(6, 8);
            Date checkDate = convertStringToDate(checkDateStr);

            return isInOperationPeriod(startDate, endDate, checkDate);
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
            logger.error("dateStr：【{}】，转换String为Date出现异常：", dateStr, e);
            return null;
        }
    }

    /**
     * 是否在经营期限内
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @param checkDate 检测时间
     * @return boolean true：在经营期限内
     */
    private boolean isInOperationPeriod(Date startDate, Date endDate, Date checkDate) {
        boolean inFlag = checkDate == null || (startDate == null && endDate == null)
                || (startDate == null && checkDate.compareTo(endDate) <= 0)
                || (endDate == null && checkDate.compareTo(startDate) >= 0)
                || (startDate != null && endDate != null && checkDate.compareTo(startDate) >= 0 && checkDate.compareTo(endDate) <= 0);
        logger.debug("startDate：【{}】，startDate：【{}】，checkDate：【{}】，检验结果：【{}】", startDate, endDate, checkDate, inFlag);
        return inFlag;
    }

    /**
     * 批量添加企业成果信息
     *
     * @param tblEntResultDao     接口服务
     * @param entResultEntityList 企业成果信息集合
     * @return boolean
     */
    private boolean addBatch(TblEntResultDao tblEntResultDao, List<TblEntResultEntity> entResultEntityList) {
        try {
            long startTime = System.currentTimeMillis();
            if (!entResultEntityList.isEmpty()) {
                tblEntResultDao.addBatch(entResultEntityList);
            }
            logger.info("批量添加企业成果信息【{}】条，共耗时【{}】ms", entResultEntityList.size(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("entResultEntityList：【{}】，批量添加企业成果信息出现异常：", entResultEntityList, e);
            return false;
        }
        return true;
    }
}

