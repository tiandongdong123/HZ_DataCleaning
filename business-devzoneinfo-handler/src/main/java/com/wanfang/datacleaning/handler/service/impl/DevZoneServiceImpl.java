package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessDevZoneInfoBO;
import com.wanfang.datacleaning.handler.service.DevZoneService;
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

/**
 * @author yifei
 * @date 2018/12/16
 */
@Service("devZoneService")
public class DevZoneServiceImpl implements DevZoneService {

    private static final Logger logger = LoggerFactory.getLogger(DevZoneServiceImpl.class);

    /**
     * 更新开发区信息成功数量
     */
    private int devZoneInfoUpdSuccessCount;
    /**
     * 更新开发区信息失败数量
     */
    private int devZoneInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;

    /**
     * 更新开发区信息
     */
    @Override
    public void updateDevZoneInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的开发区信息开始");

        // 递归更新开发区信息
        updateDevZoneInfoByRecursion(CmnConstant.START_INDEX, CmnConstant.PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "递归更新DB工商表的开发区信息结束，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                (devZoneInfoUpdSuccessCount + devZoneInfoUpdFailCount), devZoneInfoUpdSuccessCount, devZoneInfoUpdFailCount, System.currentTimeMillis() - startTime);

    }

    /**
     * 递归更新开发区信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateDevZoneInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        pageSize = (CmnConstant.END_INDEX - startIndex) >= pageSize ? pageSize : CmnConstant.END_INDEX - startIndex + 1;
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页开发区信息开始", pageNum);
        List<BusinessDevZoneInfoBO> devZoneInfoBOList = tblBusinessDao.getDevZoneInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "查询DB工商表的第【{}】页开发区信息结束,共查询到【{}】条数据，耗时【{}】ms", pageNum, devZoneInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (devZoneInfoBOList != null && devZoneInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页开发区信息开始", pageNum);
            List<BusinessDevZoneInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < devZoneInfoBOList.size(); i++) {
                batchList.add(devZoneInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == devZoneInfoBOList.size()) {
                    // 批量更新开发区信息
                    if (updateBatchDevZoneInfo(batchList)) {
                        devZoneInfoUpdSuccessCount += batchList.size();
                    } else {
                        devZoneInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表的第【{}】页开发区信息结束,共更新【{}】条数据，耗时【{}】ms", pageNum, devZoneInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (devZoneInfoBOList.size() == CmnConstant.PAGE_SIZE) {
                updateDevZoneInfoByRecursion(startIndex + pageSize, CmnConstant.PAGE_SIZE);
            }
        }
    }

    /**
     * 处理开发区信息
     *
     * @param devZoneInfoBO
     * @return BusinessDevZoneInfoBO 若出现异常，则返回null
     */
    private BusinessDevZoneInfoBO handleDevZoneInfo(BusinessDevZoneInfoBO devZoneInfoBO) {
        if (devZoneInfoBO == null || StringUtils.isBlank(devZoneInfoBO.getLatLon())) {
            devZoneInfoBO.setEcoTecDevZone("");
            devZoneInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return devZoneInfoBO;
        }

        try {
            return null;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "devZoneInfoBO：【{}】，处理开发区信息(handleLocInfo())出现异常：", devZoneInfoBO, e);
            return null;
        }
    }

    /**
     * 批量更新开发区信息
     *
     * @param devZoneInfoBOList
     * @return boolean
     */
    private boolean updateBatchDevZoneInfo(List<BusinessDevZoneInfoBO> devZoneInfoBOList) {
        List<BusinessDevZoneInfoBO> handleList = new ArrayList<>();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessDevZoneInfoBO devZoneInfoBO : devZoneInfoBOList) {
                devZoneInfoBO = handleDevZoneInfo(devZoneInfoBO);
                if (devZoneInfoBO != null && StringUtils.isNotEmpty(devZoneInfoBO.getLatLon())) {
                    handleList.add(devZoneInfoBO);
                }
            }
            LoggerUtils.appendInfoLog(logger, "处理【{}】条开发区信息共耗时【{}】ms", devZoneInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchDevZoneInfoByKey(handleList);
            }
            LoggerUtils.appendInfoLog(logger, "更新DB工商表【{}】条开发区信息开始共耗时【{}】ms", handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新开发区信息(updateBatchDevZoneInfo())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }
    }

}
