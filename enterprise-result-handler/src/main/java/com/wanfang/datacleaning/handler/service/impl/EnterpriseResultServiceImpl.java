package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.*;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessEntNameBO;
import com.wanfang.datacleaning.dao.model.master.bo.CstadCompUnitBO;
import com.wanfang.datacleaning.dao.model.master.bo.PatentProposerNameBO;
import com.wanfang.datacleaning.dao.model.master.bo.StandardDraftUnitBO;
import com.wanfang.datacleaning.dao.model.master.entity.TblEntResultEntity;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        LoggerUtils.appendInfoLog(logger, "*********** 缓存工商表的企业(机构)名称信息开始 ***********");

        // 递归缓存
        cacheBaseEntNameInfoByRecursion(0, CmnConstant.DEFAULT_PAGE_SIZE);

        Map<String, String> baseEntNameInfo = BusinessDataUtils.getCacheEntNameInfo();
        if (baseEntNameInfo != null && baseEntNameInfo.size() > 0) {
            LoggerUtils.appendInfoLog(logger, "*********** 缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", baseEntNameInfo.size(), System.currentTimeMillis() - startTime);
            return true;
        }

        LoggerUtils.appendInfoLog(logger, "*********** 缓存工商表的企业(机构)名称信息结束，共缓存【{}】条数据， 耗时【{}】ms ***********", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    @Override
    public void syncEntResultInfo() {
        String[] syncResultTypeArray = StringUtils.split(CmnConstant.SYNC_RESULT_TYPE, CmnConstant.SEPARATOR_COMMA);
        LoggerUtils.appendInfoLog(logger, "*********** 同步的成果类型为：【{}】", JSON.toJSONString(syncResultTypeArray));
        if (syncResultTypeArray != null && syncResultTypeArray.length > 0) {
            for (int i = 0; i < syncResultTypeArray.length; i++) {
                if (CmnEnum.ResultTypeEnum.PATENT.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[专利]开始 ***********");
                        syncPatentResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[专利]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                                (patentSuccessCount + patentFailCount), patentSuccessCount, patentFailCount, System.currentTimeMillis() - startTime);
                    });
                    continue;
                }

                if (CmnEnum.ResultTypeEnum.STANDARD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[标准]开始 ***********");
                        syncStandardResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[标准]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                                (stdSuccessCount + stdFailCount), stdSuccessCount, stdFailCount, System.currentTimeMillis() - startTime);
                    });
                    continue;
                }

                if (CmnEnum.ResultTypeEnum.CSTAD.getValue().equals(syncResultTypeArray[i])) {
                    ThreadUtils.getThreadPoolExecutor().execute(() -> {
                        long startTime = System.currentTimeMillis();
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[成果]开始 ***********");
                        syncCstadResultInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);
                        LoggerUtils.appendInfoLog(logger, "*********** 递归同步企业成果[成果]结束,共同步【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
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
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页企业(机构)名称信息开始 ***********", pageNum);
        List<BusinessEntNameBO> entNameBOList = tblBusinessDao.getBaseEntNameInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页企业(机构)名称信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, entNameBOList.size(), System.currentTimeMillis() - startTime);

        if (entNameBOList != null && entNameBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB工商表的企业(机构)名称信息开始 ***********");
            for (BusinessEntNameBO entNameBO : entNameBOList) {
                BusinessDataUtils.cacheEntNameInfo(entNameBO.getEntName(), entNameBO.getPripid());
            }
            LoggerUtils.appendInfoLog(logger, "*********** 缓存DB工商表的企业(机构)名称信息结束,共缓存【{}】条数据，耗时【{}】ms ***********", entNameBOList.size(), System.currentTimeMillis() - startTime);

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
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB专利表的第【{}】页申请（专利权）人信息开始 ***********", pageNum);
        List<PatentProposerNameBO> proposerNameBOList = tblPatentDao.getBaseProposerNameInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB专利表的第【{}】页申请（专利权）人信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, proposerNameBOList.size(), System.currentTimeMillis() - startTime);

        if (proposerNameBOList != null && proposerNameBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[专利]信息开始 ***********");
            for (PatentProposerNameBO proposerNameBO : proposerNameBOList) {
                if (StringUtils.isEmpty(proposerNameBO.getProposerName())) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handleEntResult(proposerNameBO.getProposerName(), StringUtils.defaultString(proposerNameBO.getPatentId()), CmnEnum.ResultTypeEnum.PATENT);
                // 添加企业成果信息
                if (addBatch(tblPatentResultDao, handleList)) {
                    patentSuccessCount++;
                } else {
                    patentFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[专利]信息结束,共更新【{}】条数据，耗时【{}】ms ***********", proposerNameBOList.size(), System.currentTimeMillis() - startTime);

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
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB标准表的第【{}】页起草单位信息开始 ***********", pageNum);
        List<StandardDraftUnitBO> draftUnitBOList = tblStandardDao.getBaseDraftUnitInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB标准表的第【{}】页起草单位信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, draftUnitBOList.size(), System.currentTimeMillis() - startTime);

        if (draftUnitBOList != null && draftUnitBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[标准]信息开始 ***********");
            for (StandardDraftUnitBO draftUnitBO : draftUnitBOList) {
                if (StringUtils.isEmpty(draftUnitBO.getDraftUnit())) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handleEntResult(draftUnitBO.getDraftUnit(), StringUtils.defaultString(draftUnitBO.getStandNum()), CmnEnum.ResultTypeEnum.STANDARD);
                // 添加企业成果信息
                if (addBatch(tblStandardResultDao, handleList)) {
                    stdSuccessCount++;
                } else {
                    stdFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[标准]信息结束,共更新【{}】条数据，耗时【{}】ms ***********", draftUnitBOList.size(), System.currentTimeMillis() - startTime);

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
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB成果表的第【{}】页完成单位信息开始 ***********", pageNum);
        List<CstadCompUnitBO> compUnitBOList = tblCstadDao.getBaseCompUnitInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB成果表的第【{}】页完成单位信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, compUnitBOList.size(), System.currentTimeMillis() - startTime);

        if (compUnitBOList != null && compUnitBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[成果]信息开始 ***********");
            for (CstadCompUnitBO compUnitBO : compUnitBOList) {
                if (StringUtils.isEmpty(compUnitBO.getCompUnit()) || compUnitBO.getResultId() == null) {
                    continue;
                }

                List<TblEntResultEntity> handleList = handleEntResult(compUnitBO.getCompUnit(), String.valueOf(compUnitBO.getResultId()), CmnEnum.ResultTypeEnum.CSTAD);
                // 添加企业成果信息
                if (addBatch(tblCstadResultDao, handleList)) {
                    cstadSuccessCount++;
                } else {
                    cstadSyncFailCount++;
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 添加企业成果[成果]信息结束,共更新【{}】条数据，耗时【{}】ms ***********", compUnitBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (compUnitBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                syncCstadResultInfoByRecursion(startIndex + CmnConstant.DEFAULT_PAGE_SIZE, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理成果信息
     *
     * @param entName        企业名称
     * @param resultNum      成果编号
     * @param resultTypeEnum 成果类型
     * @return List<TblEntResultEntity>
     */
    private List<TblEntResultEntity> handleEntResult(String entName, String resultNum, CmnEnum.ResultTypeEnum resultTypeEnum) {
        List<TblEntResultEntity> handleList = new ArrayList<>();
        String[] entNameArray = CommonUtils.splitEntName(entName);
        for (int i = 0; i < entNameArray.length; i++) {
            String pripid = BusinessDataUtils.getCachePripidByEntName(entNameArray[i]);
            TblEntResultEntity entResultEntity;
            if (StringUtils.isNotEmpty(pripid)) {
                entResultEntity = new TblEntResultEntity();
                entResultEntity.setPripid(pripid);
                entResultEntity.setEntName(entNameArray[i]);
                entResultEntity.setResultNum(resultNum);
                entResultEntity.setResultType(resultTypeEnum.getKey());
                entResultEntity.setUpdateTime(DateUtils.getCurrentTimeStamp());

                handleList.add(entResultEntity);
            }
        }

        return handleList;
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

