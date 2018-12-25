package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.dao.master.TblUsCreditCodeDao;
import com.wanfang.datacleaning.handler.model.bo.UsCreditCodeBO;
import com.wanfang.datacleaning.handler.service.UsCreditCodeService;
import com.wanfang.datacleaning.handler.util.business.UsCreditCodeDataUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private TblUsCreditCodeDao tblUsCreditCodeDao;
    @Autowired
    private TblBusinessDao tblBusinessDao;

    @Override
    public boolean cacheUsCreditCodeInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "缓存DB统一社会信用代码表的信用代码信息开始");

        // 递归缓存
        cacheBaseUsCreditCodeByRecursion(0, CmnConstant.PAGE_SIZE);

        int cacheSize = UsCreditCodeDataUtils.getgetCacheUsCreditCodeSize();
        if (cacheSize > 0) {
            LoggerUtils.appendInfoLog(logger, "缓存DB统一社会信用代码表的信用代码信息结束，共缓存【{}】条数据， 耗时【{}】ms", cacheSize, System.currentTimeMillis() - startTime);
            return true;
        }

        LoggerUtils.appendInfoLog(logger, "缓存DB统一社会信用代码表的信用代码信息结束，共缓存【{}】条数据， 耗时【{}】ms", 0, System.currentTimeMillis() - startTime);
        return false;
    }

    @Override
    public void updateUsCreditCode() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的B统一社会信用代码信息开始");

        // 递归更新统一社会信用代码信息
        updateUsCreditCodeByRecursion(CmnConstant.START_INDEX, CmnConstant.PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的B统一社会信用代码信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                (patentInfoUpdSuccessCount + patentInfoUpdFailCount), patentInfoUpdSuccessCount, patentInfoUpdFailCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归缓存统一社会信用代码信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void cacheBaseUsCreditCodeByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "查询DB统一社会信用代码表的第【{}】页信用代码信息开始", pageNum);
        List<UsCreditCodeBO> creditCodeBOList = tblUsCreditCodeDao.getUsCreditCodeByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB统一社会信用代码表的第【{}】页信用代码信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, creditCodeBOList.size(), System.currentTimeMillis() - startTime);

        if (creditCodeBOList != null && creditCodeBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "缓存DB统一社会信用代码表的第【{}】页信用代码信息开始", pageNum);
            UsCreditCodeDataUtils.cacheUsCreditCodeMap(creditCodeBOList);
            LoggerUtils.appendInfoLog(logger, "缓存DB统一社会信用代码表的第【{}】页信用代码信息结束,共缓存【{}】条数据，耗时【{}】ms", pageNum, creditCodeBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (creditCodeBOList.size() == CmnConstant.PAGE_SIZE) {
                cacheBaseUsCreditCodeByRecursion(startIndex + pageSize, CmnConstant.PAGE_SIZE);
            }
        }
    }

    /**
     * 递归更新统一社会信用代码信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateUsCreditCodeByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        pageSize = (CmnConstant.END_INDEX - startIndex) >= pageSize ? pageSize : CmnConstant.END_INDEX - startIndex + 1;
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页统一社会信用代码信息开始", pageNum);
        List<UsCreditCodeBO> usCreditCodeBOList = tblBusinessDao.getUsCreditCodeByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页统一社会信用代码信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, usCreditCodeBOList.size(), System.currentTimeMillis() - startTime);

        if (usCreditCodeBOList != null && usCreditCodeBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页统一社会信用代码信息开始", pageNum);
            List<UsCreditCodeBO> batchList = new LinkedList<>();
            for (int i = 0; i < usCreditCodeBOList.size(); i++) {
                batchList.add(usCreditCodeBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == usCreditCodeBOList.size()) {
                    // 批量更新统一社会信用代码信息
                    if (updateBatchUsCreditCode(batchList)) {
                        patentInfoUpdSuccessCount += batchList.size();
                    } else {
                        patentInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页统一社会信用代码信息结束,共更新【{}】条数据，耗时【{}】ms", pageNum, usCreditCodeBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (usCreditCodeBOList.size() == CmnConstant.PAGE_SIZE) {
                updateUsCreditCodeByRecursion(startIndex + pageSize, CmnConstant.PAGE_SIZE);
            }
        }
    }

    /**
     * 处理统一社会信用代码信息
     *
     * @param usCreditCodeBO
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
     * @param usCreditCodeBOList
     * @return boolean
     */
    private boolean updateBatchUsCreditCode(List<UsCreditCodeBO> usCreditCodeBOList) {
        List<UsCreditCodeBO> handleList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            for (UsCreditCodeBO usCreditCodeBO : usCreditCodeBOList) {
                if (usCreditCodeBO != null) {
                    handleList.add(handleUsCreditCodeInfo(usCreditCodeBO));
                }
            }
            LoggerUtils.appendInfoLog(logger, "处理【{}】条统一社会信用代码信息共耗时【{}】ms", usCreditCodeBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchUsCreditCodeByKey(handleList);
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表【{}】条统一社会信用代码信息开始共耗时【{}】ms", handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新统一社会信用代码信息(updateBatchUsCreditCode())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }
    }

}
