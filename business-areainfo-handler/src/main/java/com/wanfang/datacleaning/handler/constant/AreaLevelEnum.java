package com.wanfang.datacleaning.handler.constant;

/**
 * 是否
 *
 * @author yifei
 * @date 2019/1/19
 */
public enum AreaLevelEnum {
    /**
     * province:省
     */
    PROVINCE("province"),
    /**
     * city:市
     */
    CITY("city"),
    /**
     * district:区
     */
    DISTRICT("district");

    private final String value;

    AreaLevelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
