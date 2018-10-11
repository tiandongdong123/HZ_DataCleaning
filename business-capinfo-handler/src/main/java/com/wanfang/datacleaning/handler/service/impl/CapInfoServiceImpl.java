package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessCapInfoBO;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.CapInfoService;
import com.wanfang.datacleaning.handler.util.business.ForeignExRateUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("capInfoService")
public class CapInfoServiceImpl implements CapInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CapInfoServiceImpl.class);

    /**
     * 更新资金信息成功数量
     */
    private int capInfoUpdSuccessCount;
    /**
     * 更新资金信息失败数量
     */
    private int capInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;

    @Override
    public void updateCapInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的资金信息开始 ***********");

        // 递归更新资金信息
        updateCapInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的资金信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                (capInfoUpdSuccessCount + capInfoUpdFailCount), capInfoUpdSuccessCount, capInfoUpdFailCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归更新资金信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateCapInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页资金信息开始 ***********", pageNum);
        List<BusinessCapInfoBO> capInfoBOList = tblBusinessDao.getCapInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页资金信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, capInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (capInfoBOList != null && capInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页资金信息开始 ***********", pageNum);
            List<BusinessCapInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < capInfoBOList.size(); i++) {
                batchList.add(capInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == capInfoBOList.size()) {
                    // 批量更新资金信息
                    if (updateBatchCapInfo(batchList)) {
                        capInfoUpdSuccessCount += batchList.size();
                    } else {
                        capInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页资金信息结束,共更新【{}】条数据，耗时【{}】ms ***********", pageNum, capInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (capInfoBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                updateCapInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理资金信息
     *
     * @param capInfoBO
     * @return handleCapInfo
     */
    private BusinessCapInfoBO handleCapInfo(BusinessCapInfoBO capInfoBO) {
        if (capInfoBO.getRegCap() == null) {
            capInfoBO.setRegCapRmb(null);
            capInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return capInfoBO;
        }

        BigDecimal regCapRmb = "人民币元".equals(capInfoBO.getRegCapCur()) ? capInfoBO.getRegCap().setScale(6, BigDecimal.ROUND_HALF_UP) : ForeignExRateUtils.getRmb(capInfoBO.getRegCapCur(), capInfoBO.getRegCap());
        capInfoBO.setRegCapRmb(regCapRmb);
        capInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());

        return capInfoBO;
    }

    /**
     * 批量更新资金信息
     *
     * @param capInfoBOList
     * @return boolean
     */
    private boolean updateBatchCapInfo(List<BusinessCapInfoBO> capInfoBOList) {
        List<BusinessCapInfoBO> handleList = new ArrayList<>();
        try {
            for (BusinessCapInfoBO capInfoBO : capInfoBOList) {
                if (capInfoBO != null) {
                    handleList.add(handleCapInfo(capInfoBO));
                }
            }
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchCapInfoByKey(handleList);
            }
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，更新资金信息(updateCapInfo())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }
    }
}

