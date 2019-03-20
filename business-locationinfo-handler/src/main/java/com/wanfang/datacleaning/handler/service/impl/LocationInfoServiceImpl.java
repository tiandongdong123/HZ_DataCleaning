package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.BusinessLocationInfoBO;
import com.wanfang.datacleaning.handler.service.LocationInfoService;
import com.wanfang.datacleaning.handler.util.PropertiesUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import com.wanfang.datacleaning.util.gaodemap.constant.CmnEnum;
import com.wanfang.datacleaning.util.gaodemap.geocode.GeoCodeUtils;
import com.wanfang.datacleaning.util.gaodemap.geocode.model.GcQryParam;
import com.wanfang.datacleaning.util.gaodemap.geocode.model.GcQryResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service("locationInfoService")
public class LocationInfoServiceImpl implements LocationInfoService {

    private static final Logger logger = LoggerFactory.getLogger(LocationInfoServiceImpl.class);

    /**
     * key
     */
    private static final String GD_MAP_KEY = PropertiesUtils.getValue("gdMap_key");
    /**
     * 地理编码url
     */
    private static final String GEOCODE_URL = PropertiesUtils.getValue("gdMap_geocode_url");
    /**
     * 地理编码sig
     */
    private static final String GEOCODE_SIG = PropertiesUtils.getValue("gdMap_geocode_sig");
    /**
     * 更新位置信息成功数量
     */
    private int locInfoUpdSuccessCount;
    /**
     * 更新位置信息失败数量
     */
    private int locInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleLocationInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的位置信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新位置信息
            updateLocationInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的位置信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (locInfoUpdSuccessCount + locInfoUpdFailCount),
                    locInfoUpdSuccessCount, locInfoUpdFailCount, System.currentTimeMillis() - startTime);
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
        return true;
    }

    /**
     * 递归更新位置信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateLocationInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的位置信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessLocationInfoBO> locationInfoBOList = tblBusinessDao.getLocationInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = locationInfoBOList.size();
        logger.info("查询DB工商表的位置信息结束，idStartPosition：【{}】，pageSize：【{}】共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (locationInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = locationInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的位置信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessLocationInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(locationInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新位置信息
                if (updateBatchLocationInfo(batchList)) {
                    locInfoUpdSuccessCount += batchList.size();
                } else {
                    locInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的位置信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateLocationInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理位置信息
     *
     * @param locationInfoBO 位置信息
     * @return BusinessLocationInfoBO 若出现异常，则返回null
     */
    private BusinessLocationInfoBO handleLocInfo(BusinessLocationInfoBO locationInfoBO) {
        if (locationInfoBO == null || StringUtils.isBlank(locationInfoBO.getDom())) {
            locationInfoBO.setLatLon("");
            locationInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return locationInfoBO;
        }

        try {
            String handleAddressStr = GeoCodeUtils.handleSpecialCharInAddress(locationInfoBO.getDom());
            Integer city = locationInfoBO.getCity();
            GcQryParam qryParam = new GcQryParam();
            qryParam.setKey(GD_MAP_KEY);
            qryParam.setAddress(handleAddressStr);
            qryParam.setCity(city != null && city != 0 ? city.toString() : "");
            qryParam.setSig(GEOCODE_SIG);
            GcQryResult qryResult = GeoCodeUtils.getGeoCode(GEOCODE_URL, qryParam);

            boolean meetFlag = CmnEnum.RequestStatusEnum.SUCCESS.getValue().equals(qryResult.getStatus())
                    && qryResult.getGcGeoCodes().size() > 0 && StringUtils.isNotBlank(qryResult.getGcGeoCodes().get(0).getLocation());
            if (meetFlag) {
                String[] locArray = qryResult.getGcGeoCodes().get(0).getLocation().split(CmnConstant.SEPARATOR_COMMA);
                String locationStr = locArray[1] + CmnConstant.SEPARATOR_COMMA + locArray[0];

                locationInfoBO.setLatLon(locationStr);
                locationInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
                return locationInfoBO;
            } else {
                logger.error("locationInfoBO：【{}】，qryResult：【{}】，位置信息获取失败！", locationInfoBO, qryResult);
                return null;
            }
        } catch (Exception e) {
            logger.error("locationInfoBO：【{}】，处理位置信息(handleLocInfo())出现异常：", locationInfoBO, e);
            return null;
        }
    }

    /**
     * 批量更新位置信息
     *
     * @param locationInfoBOList 位置信息集合
     * @return boolean true：更新成功
     */
    private boolean updateBatchLocationInfo(List<BusinessLocationInfoBO> locationInfoBOList) {
        List<BusinessLocationInfoBO> handleList = new ArrayList<>();
        long idStartPosition = locationInfoBOList.get(0).getId();
        long idEndPosition = locationInfoBOList.get(locationInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessLocationInfoBO locationInfoBO : locationInfoBOList) {
                locationInfoBO = handleLocInfo(locationInfoBO);
                if (locationInfoBO != null && StringUtils.isNotBlank(locationInfoBO.getDom())) {
                    handleList.add(locationInfoBO);
                }
            }
            logger.info("id区间为：[{},{}]，处理位置信息【{}】条,共耗时【{}】ms", idStartPosition, idEndPosition, locationInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchLocationInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，批量更新DB工商表位置信息【{}】条,共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，批量更新位置信息出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }
}

