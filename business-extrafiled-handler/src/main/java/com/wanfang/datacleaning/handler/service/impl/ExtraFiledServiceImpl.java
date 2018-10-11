package com.wanfang.datacleaning.handler.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.dao.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.dao.model.master.bo.BusinessExtraFieldBO;
import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.service.ExtraFiledService;
import com.wanfang.datacleaning.handler.util.business.DomPropertyUtils;
import com.wanfang.datacleaning.handler.util.business.area.AreaCodeUtils;
import com.wanfang.datacleaning.handler.util.business.area.model.AreaCode;
import com.wanfang.datacleaning.util.DateUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:45 
 *  @Version  V1.0   
 */
@Service("extraFiledService")
public class ExtraFiledServiceImpl implements ExtraFiledService {
    private static final Logger logger = LoggerFactory.getLogger(ExtraFiledServiceImpl.class);

    /**
     * 更新拓展字段成功数量
     */
    private int extraFieldInfoUpdSuccessCount;
    /**
     * 更新拓展字段失败数量
     */
    private int extraFieldInfoUpdFailCount;

    @Autowired
    private TblBusinessDao tblBusinessDao;

    @Override
    public void updateExtraFieldInfo() {
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的拓展字段信息开始 ***********");

        // 递归更新拓展字段
        updateExtraFieldInfoByRecursion(CmnConstant.DEFAULT_START_INDEX, CmnConstant.DEFAULT_PAGE_SIZE);

        LoggerUtils.appendInfoLog(logger, "*********** 递归更新DB工商表的拓展字段信息结束,共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms ***********",
                (extraFieldInfoUpdSuccessCount + extraFieldInfoUpdFailCount), extraFieldInfoUpdSuccessCount, extraFieldInfoUpdFailCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归更新拓展字段
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     */
    private void updateExtraFieldInfoByRecursion(int startIndex, int pageSize) {
        int pageNum = startIndex / pageSize + 1;
        long startTime = System.currentTimeMillis();
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页拓展字段信息开始 ***********", pageNum);
        List<BusinessExtraFieldBO> extraFieldBOList = tblBusinessDao.getExtraFieldInfoByPage(startIndex, pageSize);
        LoggerUtils.appendInfoLog(logger, "*********** 查询DB工商表的第【{}】页拓展字段信息结束,共查询到【{}】条数据，耗时【{}】ms ***********", pageNum, extraFieldBOList.size(), System.currentTimeMillis() - startTime);

        if (extraFieldBOList != null && extraFieldBOList.size() > 0) {
            startTime = System.currentTimeMillis();
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页拓展字段信息开始 ***********", pageNum);
            List<BusinessExtraFieldBO> batchList = new LinkedList<>();
            for (int i = 0; i < extraFieldBOList.size(); i++) {
                batchList.add(extraFieldBOList.get(i));
                if ((i + 1) % CmnConstant.BATCH_SIZE == 0 || i + 1 == extraFieldBOList.size()) {
                    // 批量更新拓展字段信息
                    if (updateBatchExtraFieldInfo(batchList)) {
                        extraFieldInfoUpdSuccessCount += batchList.size();
                    } else {
                        extraFieldInfoUpdFailCount += batchList.size();
                    }

                    batchList = new LinkedList<>();
                }
            }
            LoggerUtils.appendInfoLog(logger, "*********** 更新DB工商表的第【{}】页拓展字段信息结束,共更新【{}】条数据，耗时【{}】ms ***********", pageNum, extraFieldBOList.size(), System.currentTimeMillis() - startTime);

            // 若查到的当前页数据数量等于每页数量，则往后再查
            if (extraFieldBOList.size() == CmnConstant.DEFAULT_PAGE_SIZE) {
                updateExtraFieldInfoByRecursion(startIndex + pageSize, CmnConstant.DEFAULT_PAGE_SIZE);
            }
        }
    }

    /**
     * 处理拓展字段
     *
     * @param fieldBO
     * @return BusinessExtraFieldBO
     */
    private BusinessExtraFieldBO handleBusinessExtraField(BusinessExtraFieldBO fieldBO) {
        // 企业状态
        String entStatus = fieldBO.getEntStatus();
        if (StringUtils.isNotBlank(entStatus)) {
            fieldBO.setEntStatus(getEntStatusCode(entStatus));
        }

        // 成立年份、月份
        Date esDate = fieldBO.getEsDate();
        if (esDate != null) {
            fieldBO.setEsYear(Integer.parseInt(DateUtils.convertDateToString(esDate, DateUtils.YEAR_FORMAT)));
            fieldBO.setEsMonth(Integer.parseInt(DateUtils.convertDateToString(esDate, DateUtils.MONTH_M_FORMAT)));
        } else {
            fieldBO.setEsYear(0);
            fieldBO.setEsMonth(0);
        }

        // 住所产权
        String domProRight = fieldBO.getDomProRight();
        if (StringUtils.isNotBlank(domProRight)) {
            fieldBO.setDomProRight(DomPropertyUtils.getCodeWithoutZero(domProRight));
        }

        // 是否有许可经营项目
        if (StringUtils.isNotBlank(fieldBO.getAbuItem())) {
            fieldBO.setHasAbuItem(CmnEnum.WhetherFlagEnum.YES.getValue());
        } else {
            fieldBO.setHasAbuItem(CmnEnum.WhetherFlagEnum.NO.getValue());
        }

        // 省份、市、区
        String regOrg = fieldBO.getRegOrg();
        if (StringUtils.isNotBlank(regOrg)) {
            AreaCode areaCode;
            if (CmnConstant.SAIC_CODE.equals(regOrg)) {
                String localAdm = fieldBO.getLocalAdm();
                areaCode = AreaCodeUtils.getAreaCode(localAdm != null && localAdm.length() == 9 ? localAdm.substring(0, 6) : localAdm);
            } else {
                areaCode = AreaCodeUtils.getAreaCode(regOrg.length() == 9 ? regOrg.substring(0, 6) : regOrg);
            }

            if (areaCode != null) {
                fieldBO.setProvince(Integer.parseInt(areaCode.getProvinceCode()));
                fieldBO.setCity(StringUtils.isNotEmpty(areaCode.getCityCode()) ? Integer.parseInt(areaCode.getCityCode()) : null);
                fieldBO.setArea(StringUtils.isNotEmpty(areaCode.getDistrictCode()) ? Integer.parseInt(areaCode.getDistrictCode()) : null);
            }
        }

        // 更新时间（10位时间戳）
        fieldBO.setUpdateTime(DateUtils.getCurrentTimeStamp());

        return fieldBO;
    }

    /**
     * 批量更新拓展字段
     *
     * @param extraFieldBOList
     * @return boolean
     */
    private boolean updateBatchExtraFieldInfo(List<BusinessExtraFieldBO> extraFieldBOList) {
        List<BusinessExtraFieldBO> handleExtraFieldBoList = new ArrayList<>();
        try {
            for (BusinessExtraFieldBO fieldBO : extraFieldBOList) {
                if (fieldBO != null) {
                    handleExtraFieldBoList.add(handleBusinessExtraField(fieldBO));
                }
            }
            if (handleExtraFieldBoList.size() > 0) {
                tblBusinessDao.updateBatchExtraFieldByKey(handleExtraFieldBoList);
            }
            return true;
        } catch (Exception e) {
            LoggerUtils.appendErrorLog(logger, "参数：【{}】，批量更新拓展字段(updateBatchExtraFieldInfo())出现异常：", JSON.toJSONString(handleExtraFieldBoList), e);
            return false;
        }
    }

    /**
     * 获取企业状态代码
     *
     * @param statusName 企业状态名称
     * @return String
     */
    private String getEntStatusCode(String statusName) {
        if (CmnEnum.EntStatusEnum.DOING_BUSINESS.getValue().equals(statusName)) {
            return CmnEnum.EntStatusEnum.DOING_BUSINESS.getKey();
        } else if (CmnEnum.EntStatusEnum.REVOCATION.getValue().equals(statusName)) {
            return CmnEnum.EntStatusEnum.REVOCATION.getKey();
        } else if (CmnEnum.EntStatusEnum.CANCEL.getValue().equals(statusName)) {
            return CmnEnum.EntStatusEnum.CANCEL.getKey();
        } else if (CmnEnum.EntStatusEnum.MOVING_OUT.getValue().equals(statusName)) {
            return CmnEnum.EntStatusEnum.MOVING_OUT.getKey();
        }

        return null;
    }
}

