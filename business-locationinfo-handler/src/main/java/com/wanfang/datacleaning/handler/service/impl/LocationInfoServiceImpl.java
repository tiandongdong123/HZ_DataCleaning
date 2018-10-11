package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessLocationInfoBO;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.service.LocationInfoService;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.GeoCodeUtils;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.model.GCQryParam;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.model.GCQryResult;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
     * 更新位置信息成功数量
     */
    private int locInfoUpdSuccessCount;
    /**
     * 更新位置信息失败数量
     */
    private int locInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;

    @Override
    public void updateLocationInfo() {

        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的位置信息开始 ***********");

        // 递归更新位置信息
        updateLocationInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的位置信息结束，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                (locInfoUpdSuccessCount + locInfoUpdFailCount), locInfoUpdSuccessCount, locInfoUpdFailCount, System.currentTimeMillis() - startTime);

    }

    /**
     * 递归更新位置信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateLocationInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();

        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页位置信息开始 ***********", pageNum);
        List<BusinessLocationInfoBO> locationInfoBOList = tblBusinessDao.getLocationInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页位置信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, locationInfoBOList.size(), System.currentTimeMillis() - startTime);

        if (locationInfoBOList != null && locationInfoBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页位置信息开始 ***********", pageNum);
            List<BusinessLocationInfoBO> batchList = new LinkedList<>();
            for (int i = 0; i < locationInfoBOList.size(); i++) {
                batchList.add(locationInfoBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == locationInfoBOList.size()) {
                    // 批量更新位置信息
                    if (updateBatchLocationInfo(batchList)) {
                        locInfoUpdSuccessCount += batchList.size();
                    } else {
                        locInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页位置信息结束,共更新【{}】条数据，耗时【{}】ms ***********", pageNum, locationInfoBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (locationInfoBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                updateLocationInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理位置信息
     *
     * @param locationInfoBO
     * @return BusinessLocationInfoBO 若出现异常，则返回null
     */
    private BusinessLocationInfoBO handleLocInfo(BusinessLocationInfoBO locationInfoBO) {
        /**
         * todo:后期删掉(跳过已更新过的经纬度信息)
         */
        if (locationInfoBO == null || StringUtils.isEmpty(locationInfoBO.getDom()) || StringUtils.isNotEmpty(locationInfoBO.getLatLon())) {
            return null;
        }

        if (locationInfoBO == null || StringUtils.isEmpty(locationInfoBO.getDom())) {
            locationInfoBO.setLatLon("");
            locationInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return locationInfoBO;
        }

        try {
            String handleAddressStr = handleSpecialCharInAddress(locationInfoBO.getDom());
            Integer city = locationInfoBO.getCity();
            GCQryParam qryParam = new GCQryParam();
            qryParam.setKey(com.wanfang.datacleaning.handler.util.gaodemap.constant.CmnConstant.GD_MAP_KEY);
            qryParam.setAddress(handleAddressStr);
            qryParam.setCity(city != null && city != 0 ? city.toString() : "");
            qryParam.setSig(com.wanfang.datacleaning.handler.util.gaodemap.geocode.constant.CmnConstant.GEOCODE_SIG);
            GCQryResult qryResult = GeoCodeUtils.getGeoCode(qryParam);

            boolean meetFlag = com.wanfang.datacleaning.handler.util.gaodemap.constant.CmnEnum.RequestStatusEnum.SUCCESS.getValue().equals(qryResult.getStatus())
                    && qryResult.getGcGeoCodes().size() > 0 && StringUtils.isNoneEmpty(qryResult.getGcGeoCodes().get(0).getLocation());
            if (meetFlag) {
                String[] locArray = qryResult.getGcGeoCodes().get(0).getLocation().split(CmnConstant.SEPARATOR_COMMA);
                String locationStr = locArray[1] + CmnConstant.SEPARATOR_COMMA + locArray[0];

                locationInfoBO.setLatLon(locationStr);
                locationInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            } else {
                LoggerUtils.appendInfoLog(logger, "locationInfoBO：【{}】，qryResult：【{}】，位置信息获取失败！", locationInfoBO, qryResult);
                locationInfoBO.setLatLon("");
                locationInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            }
            return locationInfoBO;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "locationInfoBO：【{}】，处理位置信息(handleLocInfo())出现异常：", locationInfoBO, e);
            return null;
        }
    }

    /**
     * 批量更新位置信息
     *
     * @param locationInfoBOList
     * @return boolean
     */
    private boolean updateBatchLocationInfo(List<BusinessLocationInfoBO> locationInfoBOList) {
        List<BusinessLocationInfoBO> handleList = new ArrayList<>();
        try {
            for (BusinessLocationInfoBO locationInfoBO : locationInfoBOList) {
                locationInfoBO = handleLocInfo(locationInfoBO);
                if (locationInfoBO != null && StringUtils.isNotEmpty(locationInfoBO.getDom())) {
                    handleList.add(locationInfoBO);
                }
            }
            if (handleList.size() > 0) {
                tblBusinessDao.updateBatchLocationInfoByKey(handleList);
            }
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新位置信息(updateBatchLocationInfo())出现异常：", JSON.toJSONString(handleList), e);
            return false;
        }

        return true;
    }

    /**
     * 处理地址中的特殊字符
     *
     * @param address 地址
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String handleSpecialCharInAddress(String address) throws UnsupportedEncodingException {
        address = address.replace(" ", "");
        return URLEncoder.encode(address, "UTF-8");
    }
}

