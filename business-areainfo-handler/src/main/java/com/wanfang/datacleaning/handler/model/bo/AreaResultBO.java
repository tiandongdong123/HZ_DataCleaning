package com.wanfang.datacleaning.handler.model.bo;

import com.wanfang.datacleaning.handler.constant.CmnEnum;

/**
 * @author yifei
 * @date 2018/12/15
 */
public class AreaResultBO {

    /**
     * 存在标识
     */
    private Boolean existFlag;
    /**
     * 行政区等级
     */
    private CmnEnum.AreaLevelEnum areaLevelEnum;
    /**
     * 行政区信息
     */
    private BusinessAreaInfoBO businessAreaInfoBO;

    public Boolean getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(Boolean existFlag) {
        this.existFlag = existFlag;
    }

    public CmnEnum.AreaLevelEnum getAreaLevelEnum() {
        return areaLevelEnum;
    }

    public void setAreaLevelEnum(CmnEnum.AreaLevelEnum areaLevelEnum) {
        this.areaLevelEnum = areaLevelEnum;
    }

    public BusinessAreaInfoBO getBusinessAreaInfoBO() {
        return businessAreaInfoBO;
    }

    public void setBusinessAreaInfoBO(BusinessAreaInfoBO businessAreaInfoBO) {
        this.businessAreaInfoBO = businessAreaInfoBO;
    }

    @Override
    public String toString() {
        return "AreaResultBO{" +
                "existFlag=" + existFlag +
                ", areaLevelEnum=" + areaLevelEnum +
                ", businessAreaInfoBO=" + businessAreaInfoBO +
                '}';
    }
}
