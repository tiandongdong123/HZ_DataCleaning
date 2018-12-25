package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
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
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private static final Logger patentResultLogger = LoggerFactory.getLogger(CmnEnum.LoggerTypeEnum.PATENT_RESULT.getValue());
    private static final Logger stdResultLogger = LoggerFactory.getLogger(CmnEnum.LoggerTypeEnum.STD_RESULT.getValue());
    private static final Logger cstadResultLogger = LoggerFactory.getLogger(CmnEnum.LoggerTypeEnum.CSTAD_RESULT.getValue());

    /**
     * 正则表达式：日期
     */
    private static final String REGEX_DATE = "^[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}$";
    /**
     * 正则表达式：日期
     */
    private static final String REGEX_DATE2 = "^[0-9]{8}$";
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

    @Autowired
    private TblBusinessDao tblBusinessDao;
    @Autowired
    private TblPatentDao tblPatentDao;
    @Autowired
    private TblPatentResultDao tblPatentResultDao;
    @Autowired
    private TblStandardDao tblStandardDao;
    @Autowired
    private TblStandardResultDao tblStandardResultDao;
    @Autowired
    private TblCstadDao tblCstadDao;
    @Autowired
    private TblCstadResultDao tblCstadResultDao;

    @Override
    public boolean cacheBaseEntNameInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "缓存工商表的企业(机构)名称信息开始");

        // 递归缓存
        cacheBaseEntNameInfoByRecursion(0, CmnConstant.DEFAULT_PAGE_SIZE);

        int cacheEntNameInfoMapSize = BusinessDataUtils.getCacheEntNameInfoMapSize();
        if (cacheEntNameInfoMapSize > 0) {
            LoggerUtils.appendInfoLog(logger, "缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms", cacheEntNameInfoMapSize, System.currentTimeMillis() - startTime);
            return true;
        }

        LoggerUtils.appendInfoLog(logger, "缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    @Override
    public void syncEntResultInfo() {
        String[] syncResultTypeArray = StringUtils.split(CmnConstant.SYNC_RESULT_TYPE, CmnConstant.SEPARATOR_COMMA);
        LoggerUtils.appendInfoLog(logger, "同步的成果类型为：【{}】", JSON.toJSONString(syncResultTypeArray));
        if (syncResultTypeArray != null && syncResultTypeArray.length > 0) {
            for (int i = 0; i < syncResultTypeArray.length; i++) {
                if (CmnEnum.ResultTypeEnum.PATENT.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(patentResultLogger, "递归同步企业成果[专利]开始");
                        syncPatentResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(patentResultLogger, "递归同步企业成果[专利]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                (patentSuccessCount + patentFailCount), patentSuccessCount, patentFailCount, System.currentTimeMillis() - startTime);
                    });
                    continue;
                }

                if (CmnEnum.ResultTypeEnum.STANDARD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(stdResultLogger, "递归同步企业成果[标准]开始");
                        syncStandardResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(stdResultLogger, "递归同步企业成果[标准]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                (stdSuccessCount + stdFailCount), stdSuccessCount, stdFailCount, System.currentTimeMillis() - startTime);
                    });
                    continue;
                }

                if (CmnEnum.ResultTypeEnum.CSTAD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(cstadResultLogger, "递归同步企业成果[成果]开始");
                        syncCstadResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(cstadResultLogger, "递归同步企业成果[成果]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                                (cstadSuccessCount + cstadSyncFailCount), cstadSuccessCount, cstadSyncFailCount, System.currentTimeMillis() - startTime);
                    });
                    continue;
                }
            }
        }
    }

    /**
     * 递归缓存企业(机构)名称信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void cacheBaseEntNameInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页企业(机构)名称信息开始", pageNum);
        List<BusinessEntNameBO> entNameBOList = tblBusinessDao.getBaseEntNameInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页企业(机构)名称信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, entNameBOList.size(), System.currentTimeMillis() - startTime);

        if (entNameBOList != null && entNameBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "缓存DB工商表的企业(机构)名称信息开始");
            BusinessDataUtils.cacheEntNameInfo(entNameBOList);
            LoggerUtils.appendInfoLog(logger, "缓存DB工商表的企业(机构)名称信息结束,共缓存【{}】条数据，耗时【{}】ms", entNameBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (entNameBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                cacheBaseEntNameInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 递归同步企业成果[专利]
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void syncPatentResultInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(patentResultLogger, "查询DB专利表的第【{}】页申请（专利权）人信息开始", pageNum);
        List<PatentProposerNameBO> proposerNameBOList = tblPatentDao.getBaseProposerNameInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(patentResultLogger, "查询DB专利表的第【{}】页申请（专利权）人信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, proposerNameBOList.size(), System.currentTimeMillis() - startTime);

        if (proposerNameBOList != null && proposerNameBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(patentResultLogger, "添加企业成果[专利]信息开始");
            for (PatentProposerNameBO proposerNameBO : proposerNameBOList) {
                if (proposerNameBO == null || StringUtils.isBlank(proposerNameBO.getProposerName())) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handlePatentResult(proposerNameBO);
                // 添加企业成果信息
                if (addBatch(tblPatentResultDao, handleList)) {
                    patentSuccessCount++;
                } else {
                    patentFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(patentResultLogger, "添加企业成果[专利]信息结束,共更新【{}】条数据，耗时【{}】ms", proposerNameBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (proposerNameBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                syncPatentResultInfoByRecursion(startIndex + CmnConstant.DEFAULT_PAGE_SIZE, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 递归同步企业成果[标准]
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void syncStandardResultInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "查询DB标准表的第【{}】页起草单位信息开始", pageNum);
        List<StandardDraftUnitBO> draftUnitBOList = tblStandardDao.getBaseDraftUnitInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB标准表的第【{}】页起草单位信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, draftUnitBOList.size(), System.currentTimeMillis() - startTime);

        if (draftUnitBOList != null && draftUnitBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "添加企业成果[标准]信息开始");
            for (StandardDraftUnitBO draftUnitBO : draftUnitBOList) {
                if (StringUtils.isEmpty(draftUnitBO.getDraftUnit())) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handleStandardResult(draftUnitBO);
                // 添加企业成果信息
                if (addBatch(tblStandardResultDao, handleList)) {
                    stdSuccessCount++;
                } else {
                    stdFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(logger, "添加企业成果[标准]信息结束,共更新【{}】条数据，耗时【{}】ms", draftUnitBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (draftUnitBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                syncStandardResultInfoByRecursion(startIndex + CmnConstant.DEFAULT_PAGE_SIZE, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 递归同步企业成果[成果]
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void syncCstadResultInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "查询DB成果表的第【{}】页完成单位信息开始", pageNum);
        List<CstadCompUnitBO> compUnitBOList = tblCstadDao.getBaseCompUnitInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB成果表的第【{}】页完成单位信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, compUnitBOList.size(), System.currentTimeMillis() - startTime);

        if (compUnitBOList != null && compUnitBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "添加企业成果[成果]信息开始");
            for (CstadCompUnitBO compUnitBO : compUnitBOList) {
                if (StringUtils.isEmpty(compUnitBO.getCompUnit()) || compUnitBO.getResultId() == null) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handleCstadResult(compUnitBO);
                // 添加企业成果信息
                if (addBatch(tblCstadResultDao, handleList)) {
                    cstadSuccessCount++;
                } else {
                    cstadSyncFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(logger, "添加企业成果[成果]信息结束,共更新【{}】条数据，耗时【{}】ms", compUnitBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (compUnitBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                syncCstadResultInfoByRecursion(startIndex + CmnConstant.DEFAULT_PAGE_SIZE, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理成果[专利]信息
     *
     * @param patentProposerNameBO
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handlePatentResult(PatentProposerNameBO patentProposerNameBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] entNameArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(patentProposerNameBO.getProposerName()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < entNameArray.length; i++) {
            if (StringUtils.isEmpty(entNameArray[i])) {
                continue;
            }

            String pripid;
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(entNameArray[i]);
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriodForPatent(entNameBO.getOpFrom(), entNameBO.getOpTo(), StringUtils.deleteWhitespace(patentProposerNameBO.getAppDate()))) {
                    continue;
                }

                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isNotEmpty(pripid)) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(entNameArray[i]);
                entResultEntity.setResultNum(patentProposerNameBO.getPatentId());
                entResultEntity.setResultType(CmnEnum.ResultTypeEnum.PATENT.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());

                handleList.add(entResultEntity);
            }
        }

        return handleList;
    }

    /**
     * 处理成果[标准]信息
     *
     * @param standardDraftUnitBO
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handleStandardResult(StandardDraftUnitBO standardDraftUnitBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] draftUnitArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(standardDraftUnitBO.getDraftUnit()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < draftUnitArray.length; i++) {
            if (StringUtils.isEmpty(draftUnitArray[i])) {
                continue;
            }

            String pripid;
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(draftUnitArray[i]);
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriod(entNameBO.getOpFrom(), entNameBO.getOpTo(), standardDraftUnitBO.getIssueDate())) {
                    continue;
                }

                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isNotEmpty(pripid)) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(draftUnitArray[i]);
                entResultEntity.setResultNum(standardDraftUnitBO.getStandNum());
                entResultEntity.setResultType(CmnEnum.ResultTypeEnum.STANDARD.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());

                handleList.add(entResultEntity);
            }
        }

        return handleList;
    }

    /**
     * 处理成果[成果]信息
     *
     * @param cstadCompUnitBO
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handleCstadResult(CstadCompUnitBO cstadCompUnitBO) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] compUnitArray = CommonUtils.splitEntName(StringUtils.deleteWhitespace(cstadCompUnitBO.getCompUnit()));
        List<BusinessEntNameBO> entNameBOList;

        for (int i = 0; i < compUnitArray.length; i++) {
            if (StringUtils.isEmpty(compUnitArray[i])) {
                continue;
            }

            String pripid;
            entNameBOList = BusinessDataUtils.getCacheEntNameInfoListByEntNmae(compUnitArray[i]);
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                // 判断“申请日”是否在经营期限内
                if (!isInOperationPeriodForCstad(entNameBO.getOpFrom(), entNameBO.getOpTo(), StringUtils.deleteWhitespace(cstadCompUnitBO.getDeclareDate()))) {
                    continue;
                }

                // 判断pripid是否为空
                pripid = entNameBO.getPripid();
                if (StringUtils.isNotEmpty(pripid)) {
                    continue;
                }

                TblEntResultEntity entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(compUnitArray[i]);
                entResultEntity.setResultNum(cstadCompUnitBO.getResultId() + "");
                entResultEntity.setResultType(CmnEnum.ResultTypeEnum.CSTAD.getKey());
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
     * @return boolean
     */
    private boolean isInOperationPeriodForPatent(Date startDate, Date endDate, String checkDateStr) {
        if (StringUtils.isBlank(checkDateStr)) {
            return true;
        }

        if (Pattern.matches(REGEX_DATE, checkDateStr)) {
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
     * @return boolean
     */
    private boolean isInOperationPeriodForCstad(Date startDate, Date endDate, String checkDateStr) {
        if (StringUtils.isBlank(checkDateStr)) {
            return true;
        }

        if (Pattern.matches(REGEX_DATE2, checkDateStr)) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
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

        return inFlag;
    }

    /**
     * 批量添加企业成果信息
     *
     * @param tblEntResultDao
     * @param entResultEntityList
     * @return boolean
     */
    private boolean addBatch(TblEntResultDao tblEntResultDao, List<TblEntResultEntity> entResultEntityList) {
        try {
            if (entResultEntityList != null && entResultEntityList.size() > 0) {
                tblEntResultDao.addBatch(entResultEntityList);
            }
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量添加企业成果信息(addBatch())出现异常：", JSON.toJSONString(entResultEntityList), e);
            return false;
        }
        return true;
    }
}

