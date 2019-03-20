package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.AreaResultBO;
import com.wanfang.datacleaning.handler.model.bo.BusinessAreaInfoBO;
import com.wanfang.datacleaning.handler.service.AreaInfoService;
import com.wanfang.datacleaning.handler.util.PropertiesUtils;
import com.wanfang.datacleaning.handler.util.business.AreaCodeUtils;
import com.wanfang.datacleaning.handler.util.business.PostalCodeUtils;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import com.wanfang.datacleaning.util.gaodemap.constant.CmnEnum;
import com.wanfang.datacleaning.util.gaodemap.geocode.GeoCodeUtils;
import com.wanfang.datacleaning.util.gaodemap.geocode.model.GcGeoCode;
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
import java.util.regex.Pattern;

/**
 * @author yifei
 * @date 2018/11/25
 */
@Service("areaInfoService")
public class AreaInfoServiceImpl implements AreaInfoService {
    private static final Logger logger = LoggerFactory.getLogger(AreaInfoServiceImpl.class);

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
     * 正则表达式-汉字
     */
    private static final Pattern CHINESE_PATTEN = Pattern.compile("[\u4e00-\u9fa5]+");
    /**
     * 更新行政区划信息成功数量
     */
    private int areaInfoUpdSuccessCount;
    /**
     * 更新行政区划信息失败数量
     */
    private int areaInfoUpdFailCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    @Override
    public void handleAreaInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("校验信息处理的前提条件开始");
        boolean meetFlag = meetPrerequisite();
        logger.info("校验信息处理的前提条件结束,校验结果为【{}】，耗时【{}】ms", meetFlag, System.currentTimeMillis() - startTime);

        if (meetFlag) {
            startTime = System.currentTimeMillis();
            logger.info("递归更新DB工商表的行政区划信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
            // 递归更新行政区划信息
            updateAreaInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
            logger.info("递归更新DB工商表的行政区划信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                    CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (areaInfoUpdSuccessCount + areaInfoUpdFailCount), areaInfoUpdSuccessCount, areaInfoUpdFailCount, System.currentTimeMillis() - startTime);
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

        // 缓存邮编与行政区划对应关系代码
        if (PostalCodeUtils.getCacheTotal() < 1) {
            logger.warn("文件：【{}】,sheet：【{}】，邮编与行政区划对应关系代码数据为空", PostalCodeUtils.POSTAL_CODE_FILE_PATH, PostalCodeUtils.POSTAL_CODE_FILE_SHEET_NAME);
            return false;
        }
        return true;
    }

    /**
     * 递归更新行政区划信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateAreaInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的行政区划信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<BusinessAreaInfoBO> areaInfoBOList = tblBusinessDao.getAreaInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = areaInfoBOList.size();
        logger.info("查询DB工商表的行政区划信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (areaInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = areaInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("更新DB工商表的行政区划信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        List<BusinessAreaInfoBO> batchList = new LinkedList<>();
        for (int i = 0; i < qryResultSize; i++) {
            batchList.add(areaInfoBOList.get(i));
            if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == qryResultSize) {
                // 批量更新行政区划信息
                if (updateBatchAreaInfo(batchList)) {
                    areaInfoUpdSuccessCount += batchList.size();
                } else {
                    areaInfoUpdFailCount += batchList.size();
                }
                batchList = new LinkedList<>();
            }
        }
        logger.info("更新DB工商表的行政区划信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, qryResultSize, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateAreaInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 处理行政区划信息
     *
     * @param areaInfoBO 行政区划信息
     * @return BusinessAreaInfoBO
     */
    private BusinessAreaInfoBO handleAreaInfo(BusinessAreaInfoBO areaInfoBO) {
        BusinessAreaInfoBO resultAreaInfoBO;
        // 通过邮编获取行政区划信息
        AreaResultBO postalCodeResultBO = getAreaInfoByPostalCode(areaInfoBO);
        // 判断行政区划码是否为区等级
        if (isDistrictLevel(postalCodeResultBO.getAreaLevelEnum())) {
            resultAreaInfoBO = updateTheOldCodeToTheLatest(postalCodeResultBO.getBusinessAreaInfoBO());
            resultAreaInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return resultAreaInfoBO;
        }

        // 通过住所获取行政区划信息
        AreaResultBO domResultBO = getAreaInfoByDom(areaInfoBO);
        // 判断行政区划码是否为区等级
        if (isDistrictLevel(domResultBO.getAreaLevelEnum())) {
            resultAreaInfoBO = updateTheOldCodeToTheLatest(domResultBO.getBusinessAreaInfoBO());
            resultAreaInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return resultAreaInfoBO;
        }

        // 通过经营场所获取行政区划信息
        AreaResultBO opLocResultBO = getAreaInfoByOpLoc(areaInfoBO);
        // 判断行政区划码是否为区等级
        if (isDistrictLevel(opLocResultBO.getAreaLevelEnum())) {
            resultAreaInfoBO = updateTheOldCodeToTheLatest(opLocResultBO.getBusinessAreaInfoBO());
            resultAreaInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return resultAreaInfoBO;
        }

        // 通过登记机关获取行政区划信息
        AreaResultBO regOrgResultBO = getAreaInfoByRegOrg(areaInfoBO);
        // 判断行政区划码是否为区等级
        if (isDistrictLevel(regOrgResultBO.getAreaLevelEnum())) {
            resultAreaInfoBO = updateTheOldCodeToTheLatest(regOrgResultBO.getBusinessAreaInfoBO());
            resultAreaInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
            return resultAreaInfoBO;
        }

        resultAreaInfoBO = getSuitableInfoBO(areaInfoBO, postalCodeResultBO, domResultBO, opLocResultBO, regOrgResultBO);
        resultAreaInfoBO.setUpdateTime(DateUtils.getCurrentTimeStamp());
        return resultAreaInfoBO;
    }

    /**
     * 通过邮编获取行政区划信息
     *
     * @param areaInfoBO 行政区划信息
     * @return AreaResultBO
     */
    private AreaResultBO getAreaInfoByPostalCode(BusinessAreaInfoBO areaInfoBO) {
        AreaResultBO areaResultBO = new AreaResultBO();
        // 邮编
        String postalCode = areaInfoBO.getPostalCode();

        // 判断邮编是否为空
        if (StringUtils.isBlank(postalCode)) {
            logger.warn("BusinessAreaInfoBO：【{}】，邮编为空！", areaInfoBO);
            areaResultBO.setExistFlag(false);
            return areaResultBO;
        }

        // 通过邮编获取缓存的邮编信息
        postalCode = PostalCodeUtils.handlePostalCode(postalCode);
        List<PostalCodeUtils.PostalCodeInfo> postalCodeInfoList = PostalCodeUtils.getCachePostalCodeInfoByKey(postalCode);
        if (postalCodeInfoList == null || postalCodeInfoList.isEmpty()) {
            logger.warn("BusinessAreaInfoBO：【{}】，邮编代码文件不存在此邮编！", areaInfoBO);
            areaResultBO.setExistFlag(false);
            return areaResultBO;
        }
        if (postalCodeInfoList.size() > 1) {
            logger.warn("BusinessAreaInfoBO：【{}】，邮编代码文件中存在多个此邮编！", areaInfoBO);
            areaResultBO.setExistFlag(false);
            return areaResultBO;
        }

        String areaCode = postalCodeInfoList.get(0).getAreaCode();
        // 判断行政区划码是否为空
        if (StringUtils.isBlank(areaCode)) {
            logger.warn("BusinessAreaInfoBO：【{}】，邮编代码文件中此邮编对应的行政区划码为空！", areaInfoBO);
            areaResultBO.setExistFlag(false);
            return areaResultBO;
        }

        // 获取行政区划等级码
        AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(areaCode);
        if (areaCodeInfo == null) {
            logger.warn("BusinessAreaInfoBO：【{}】，areaCode：【{}】，邮编代码文件中此邮编对应的行政区划码不符合标准格式！", areaInfoBO, areaCode);
            areaResultBO.setExistFlag(false);
            return areaResultBO;
        }

        return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCode);
    }

    /**
     * 通过住所获取行政区划信息
     *
     * @param areaInfoBO 行政区划信息
     * @return AreaResultBO
     */
    private AreaResultBO getAreaInfoByDom(BusinessAreaInfoBO areaInfoBO) {
        // 住所行政区划码
        String domDistrict = StringUtils.deleteWhitespace(areaInfoBO.getDomDistrict());
        // 判断住所行政区划码是否为空
        if (StringUtils.isBlank(domDistrict)) {
            logger.warn("BusinessAreaInfoBO：【{}】，住所行政区划码为空！", areaInfoBO);
        } else {
            // 获取行政区划等级码
            AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(domDistrict);
            if (areaCodeInfo == null) {
                logger.warn("BusinessAreaInfoBO：【{}】，住所行政区划码不符合标准格式！", areaInfoBO);
            }

            // 判断住所行政区划码是否符合要求
            if (areaCodeInfo != null && areaCodeInfo.getDistrictCode() != null) {
                return setAreaResultBO(areaInfoBO, areaCodeInfo, domDistrict);
            }
        }

        // 住所
        String dom = areaInfoBO.getDom();
        // 判断住所是否满足正确的地址
        if (!meetRightAddress(dom)) {
            logger.warn("BusinessAreaInfoBO：【{}】，住所不符合地址格式！", areaInfoBO);
            return initIfExist(areaInfoBO, areaInfoBO.getDomDistrict());
        }

        // 获取地理编码信息
        GcQryResult qryResult = getGeoCodeInfo(dom, null);
        // 判断是否有查询结果
        boolean haveResultFlag = qryResult != null && CmnEnum.RequestStatusEnum.SUCCESS.getValue().equals(qryResult.getStatus()) && Integer.parseInt(StringUtils.defaultIfEmpty(qryResult.getCount(), "0")) > 0;
        if (!haveResultFlag) {
            logger.warn("BusinessAreaInfoBO：【{}】，qryResult：【{}】，通过住所获取的地理编码信息为空！", areaInfoBO, qryResult);
            return initIfExist(areaInfoBO, areaInfoBO.getDomDistrict());
        }

        List<GcGeoCode> gcGeoCodeList = qryResult.getGcGeoCodes();
        // 若只有一条查询结果，则取之
        if (gcGeoCodeList != null && gcGeoCodeList.size() == 1) {
            // 获取行政区划等级码
            AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(gcGeoCodeList.get(0).getAdCode());
            return setAreaResultBO(areaInfoBO, areaCodeInfo, gcGeoCodeList.get(0).getAdCode());
        }

        // 在查询结果中匹配相应的行政区划码(住所行政区划码)
        AreaCodeUtils.AreaCode areaCodeInfo = matchAreaCodeByQryResult(gcGeoCodeList, areaInfoBO.getDomDistrict());
        if (areaCodeInfo != null) {
            return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCodeInfo.getDistrictCode());
        }

        // 在查询结果中匹配相应的行政区划码(经营场所行政区划码)
        areaCodeInfo = matchAreaCodeByQryResult(gcGeoCodeList, areaInfoBO.getOpLocDistrict());
        if (areaCodeInfo != null) {
            return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCodeInfo.getDistrictCode());
        }

        return initIfExist(areaInfoBO, areaInfoBO.getDomDistrict());
    }

    /**
     * 通过经营场所获取行政区划信息
     *
     * @param areaInfoBO 行政区划信息
     * @return AreaResultBO
     */
    private AreaResultBO getAreaInfoByOpLoc(BusinessAreaInfoBO areaInfoBO) {
        // 经营场所行政区划码
        String opLocDistrict = areaInfoBO.getOpLocDistrict();
        if (StringUtils.isBlank(opLocDistrict)) {
            logger.warn("BusinessAreaInfoBO：【{}】，经营场所行政区划码为空！", areaInfoBO);
        } else {
            // 获取行政区划等级码
            AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(opLocDistrict);
            if (areaCodeInfo == null) {
                logger.warn("BusinessAreaInfoBO：【{}】，经营场所行政区划码不符合标准格式！", areaInfoBO);
            }

            // 判断经营场所行政区划码是否符合要求
            if (areaCodeInfo != null && areaCodeInfo.getDistrictCode() != null) {
                return setAreaResultBO(areaInfoBO, areaCodeInfo, opLocDistrict);
            }
        }

        // 经营场所
        String opLoc = areaInfoBO.getOpLoc();
        // 判断经营场所是否满足正确的地址
        if (!meetRightAddress(opLoc)) {
            logger.warn("BusinessAreaInfoBO：【{}】，经营场所不符合地址格式！", areaInfoBO);
            return initIfExist(areaInfoBO, areaInfoBO.getOpLocDistrict());
        }

        // 获取地理编码信息
        GcQryResult qryResult = getGeoCodeInfo(opLoc, null);
        // 判断是否有查询结果
        boolean haveResultFlag = qryResult != null && CmnEnum.RequestStatusEnum.SUCCESS.getValue().equals(qryResult.getStatus()) && Integer.parseInt(StringUtils.defaultIfEmpty(qryResult.getCount(), "0")) > 0;
        if (!haveResultFlag) {
            logger.warn("BusinessAreaInfoBO：【{}】，通过经营场所获取的地理编码信息为空！", areaInfoBO);
            return initIfExist(areaInfoBO, areaInfoBO.getOpLocDistrict());
        }

        List<GcGeoCode> gcGeoCodeList = qryResult.getGcGeoCodes();
        // 若只有一条查询结果，则取之
        if (gcGeoCodeList != null && gcGeoCodeList.size() == 1) {
            // 获取行政区划等级码
            AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(gcGeoCodeList.get(0).getAdCode());
            return setAreaResultBO(areaInfoBO, areaCodeInfo, gcGeoCodeList.get(0).getAdCode());
        }

        // 在查询结果中匹配相应的行政区划码(经营场所行政区划码)
        AreaCodeUtils.AreaCode areaCodeInfo = matchAreaCodeByQryResult(gcGeoCodeList, areaInfoBO.getDomDistrict());
        if (areaCodeInfo != null) {
            return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCodeInfo.getDistrictCode());
        }

        // 在查询结果中匹配相应的行政区划码(住所行政区划码)
        areaCodeInfo = matchAreaCodeByQryResult(gcGeoCodeList, areaInfoBO.getOpLocDistrict());
        if (areaCodeInfo != null) {
            return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCodeInfo.getDistrictCode());
        }

        return initIfExist(areaInfoBO, areaInfoBO.getOpLocDistrict());
    }

    /**
     * 通过登记机关获取行政区划信息
     *
     * @param areaInfoBO 行政区划信息
     * @return BusinessAreaInfoBO
     */
    private AreaResultBO getAreaInfoByRegOrg(BusinessAreaInfoBO areaInfoBO) {
        // 登记机关
        String regOrg = StringUtils.deleteWhitespace(areaInfoBO.getRegOrg());
        if (StringUtils.isBlank(regOrg)) {
            logger.warn("BusinessAreaInfoBO：【{}】，登记机关为空！", areaInfoBO);
        } else if (CmnConstant.SAIC_CODE.equals(regOrg)) {
            logger.warn("BusinessAreaInfoBO：【{}】，登记机关为【{}】！", areaInfoBO, CmnConstant.SAIC_CODE);
        } else {
            // 获取行政区划等级码
            regOrg = StringUtils.substring(regOrg, 0, 6);
            AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(regOrg);
            if (areaCodeInfo == null) {
                logger.warn("BusinessAreaInfoBO：【{}】，登记机关不符合标准格式！", areaInfoBO);
            }

            // 判断登记机关是否符合要求
            if (areaCodeInfo != null && areaCodeInfo.getDistrictCode() != null) {
                return setAreaResultBO(areaInfoBO, areaCodeInfo, regOrg);
            }
        }

        AreaResultBO areaResultBO = new AreaResultBO();
        areaResultBO.setExistFlag(false);
        areaResultBO.setAreaLevelEnum(null);
        areaResultBO.setBusinessAreaInfoBO(BusinessAreaInfoBO.initEmptyBO(areaInfoBO.getId()));
        return areaResultBO;
    }

    /**
     * 获取地理编码信息
     *
     * @param address 地址
     * @param city    市行政区划码
     * @return GcQryResult 若出现异常，则返回null
     */
    private GcQryResult getGeoCodeInfo(String address, String city) {
        GcQryParam qryParam = new GcQryParam();
        try {
            qryParam.setKey(GD_MAP_KEY);
            qryParam.setAddress(GeoCodeUtils.handleSpecialCharInAddress(address));
            qryParam.setCity(StringUtils.defaultString(city));
            qryParam.setSig(GEOCODE_SIG);

            return GeoCodeUtils.getGeoCode(GEOCODE_URL, qryParam);
        } catch (Exception e) {
            logger.error("address：【{}】，qryParam：【{}】,getGeoCodeInfo()出现异常：", address, qryParam, e);
            return null;
        }
    }

    /**
     * 在查询结果中匹配相应的行政区划码
     *
     * @param gcGeoCodeList 查询结果
     * @param districtCode  行政区划码
     * @return AreaCodeUtils.AreaCode
     */
    private AreaCodeUtils.AreaCode matchAreaCodeByQryResult(List<GcGeoCode> gcGeoCodeList, String districtCode) {
        if (gcGeoCodeList == null || StringUtils.isBlank(districtCode)) {
            return null;
        }

        String adCode;
        for (GcGeoCode gcGeoCode : gcGeoCodeList) {
            adCode = gcGeoCode.getAdCode();
            if (StringUtils.isNotEmpty(adCode) && adCode.substring(0, 2).equals(districtCode.substring(0, 2))) {
                return AreaCodeUtils.getAreaCode(adCode);
            }
        }

        return null;
    }

    /**
     * 是否满足正确的地址(大于等于3个字符且含汉字)
     *
     * @param address 地址
     * @return boolean true:满足
     */
    private boolean meetRightAddress(String address) {
        address = StringUtils.deleteWhitespace(address);

        final int minLen = 3;
        return StringUtils.isNotEmpty(address) && address.length() >= minLen && CHINESE_PATTEN.matcher(address).find();
    }

    /**
     * 初始化为0
     *
     * @param str 字符串
     * @return int
     */
    private int initZeroIfBlank(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }

        return Integer.parseInt(str);
    }

    /**
     * 若存在行政区划码，则初始化
     *
     * @param areaInfoBO 行政区划信息
     * @param areaCode   行政区划码
     * @return AreaResultBO
     */
    private AreaResultBO initIfExist(BusinessAreaInfoBO areaInfoBO, String areaCode) {
        // 获取行政区划等级码
        AreaCodeUtils.AreaCode areaCodeInfo = AreaCodeUtils.getAreaCode(areaCode);

        return setAreaResultBO(areaInfoBO, areaCodeInfo, areaCode);
    }

    /**
     * 若存在行政区划码，则初始化
     *
     * @param areaInfoBO   行政区划信息
     * @param areaCodeInfo 行政区划代码信息
     * @param areaCode     行政区划码
     * @return AreaResultBO
     */
    private AreaResultBO setAreaResultBO(BusinessAreaInfoBO areaInfoBO, AreaCodeUtils.AreaCode areaCodeInfo, String areaCode) {
        AreaResultBO areaResultBO = new AreaResultBO();
        if (areaCodeInfo != null) {
            areaInfoBO = BusinessAreaInfoBO.init(areaInfoBO.getId(), initZeroIfBlank(areaCodeInfo.getProvinceCode()), initZeroIfBlank(areaCodeInfo.getCityCode()), initZeroIfBlank(areaCodeInfo.getDistrictCode()));
            areaResultBO.setExistFlag(true);
            areaResultBO.setAreaLevelEnum(getAreaLevelByAreaCode(areaCode));
            areaResultBO.setBusinessAreaInfoBO(areaInfoBO);
            return areaResultBO;
        }

        areaResultBO.setExistFlag(false);
        return areaResultBO;
    }

    /**
     * 获取最佳区划码信息
     *
     * @param areaInfoBO    行政区划信息
     * @param areaResultBOS 按优先级排序
     * @return BusinessAreaInfoBO
     */
    private BusinessAreaInfoBO getSuitableInfoBO(BusinessAreaInfoBO areaInfoBO, AreaResultBO... areaResultBOS) {
        for (AreaResultBO areaResultBO : areaResultBOS) {
            if (areaResultBO.getExistFlag() && com.wanfang.datacleaning.handler.constant.AreaLevelEnum.CITY.equals(areaResultBO.getAreaLevelEnum())) {
                return areaResultBO.getBusinessAreaInfoBO();
            }
        }

        for (AreaResultBO areaResultBO : areaResultBOS) {
            if (areaResultBO.getExistFlag() && areaResultBO.getBusinessAreaInfoBO().getProvince() != 0) {
                return areaResultBO.getBusinessAreaInfoBO();
            }
        }

        return BusinessAreaInfoBO.initEmptyBO(areaInfoBO.getId());
    }

    /**
     * 将旧的行政区划码更新至最新
     *
     * @param areaInfoBO 行政区划信息
     * @return BusinessAreaInfoBO
     */
    private BusinessAreaInfoBO updateTheOldCodeToTheLatest(BusinessAreaInfoBO areaInfoBO) {
        switch ("" + areaInfoBO.getArea()) {
            case "321011":
                areaInfoBO.setArea(Integer.parseInt("321003"));
                break;
            case "321088":
                areaInfoBO.setArea(Integer.parseInt("321012"));
                break;
            case "321091":
                areaInfoBO.setArea(Integer.parseInt("321071"));
                break;
            default:
        }

        return areaInfoBO;
    }

    /**
     * 批量更新行政区划信息
     *
     * @param areaInfoBOList 行政区划信息集合
     * @return boolean
     */
    private boolean updateBatchAreaInfo(List<BusinessAreaInfoBO> areaInfoBOList) {
        List<BusinessAreaInfoBO> handleList = new ArrayList<>();
        long idStartPosition = areaInfoBOList.get(0).getId();
        long idEndPosition = areaInfoBOList.get(areaInfoBOList.size() - 1).getId();
        try {
            long startTime = System.currentTimeMillis();
            for (BusinessAreaInfoBO areaInfoBO : areaInfoBOList) {
                handleList.add(handleAreaInfo(areaInfoBO));
            }
            logger.info("id区间为：[{},{}]，处理行政区划信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, areaInfoBOList.size(), System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();
            if (!handleList.isEmpty()) {
                tblBusinessDao.updateBatchAreaInfoByKey(handleList);
            }
            logger.info("id区间为：[{},{}]，更新DB工商表行政区划信息【{}】条，共耗时【{}】ms", idStartPosition, idEndPosition, handleList.size(), System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            logger.error("id区间为：[{},{}]，更新行政区划信息(updateBatchAreaInfo())出现异常：", idStartPosition, idEndPosition, e);
            return false;
        }
    }

    /**
     * 通过行政区划码获取行政区等级
     *
     * @param areaCode 行政区划码
     * @return AreaLevelEnum
     */
    private com.wanfang.datacleaning.handler.constant.AreaLevelEnum getAreaLevelByAreaCode(String areaCode) {
        // 市（/县）级码
        String cityLevelCode = StringUtils.substring(areaCode, 2, 4);
        // 区（/县）级码
        String districtLevelCode = StringUtils.substring(areaCode, 4, 6);

        if (!AreaCodeUtils.AREA_LEVEL_START_CODE.equals(districtLevelCode)) {
            return com.wanfang.datacleaning.handler.constant.AreaLevelEnum.DISTRICT;
        }

        if (!AreaCodeUtils.AREA_LEVEL_START_CODE.equals(cityLevelCode)) {
            return com.wanfang.datacleaning.handler.constant.AreaLevelEnum.CITY;
        }

        return com.wanfang.datacleaning.handler.constant.AreaLevelEnum.PROVINCE;
    }

    /**
     * 是否区等级
     *
     * @param areaLevelEnum 行政区等级
     * @return boolean
     */
    private boolean isDistrictLevel(com.wanfang.datacleaning.handler.constant.AreaLevelEnum areaLevelEnum) {
        return com.wanfang.datacleaning.handler.constant.AreaLevelEnum.DISTRICT.equals(areaLevelEnum);
    }
}
