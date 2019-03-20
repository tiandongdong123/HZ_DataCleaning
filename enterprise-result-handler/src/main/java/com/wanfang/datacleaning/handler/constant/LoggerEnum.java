package com.wanfang.datacleaning.handler.constant;

/**
 * @author yifei
 * @date 2019/3/3
 */
public enum LoggerEnum {
    /**
     * patentResultLogger：专利
     */
    PATENT_RESULT("patentResultLogger"),
    /**
     * stdResultLogger：标准
     */
    STD_RESULT("stdResultLogger"),
    /**
     * cstadResultLogger：成果
     */
    CSTAD_RESULT("cstadResultLogger");

    private String value;

    LoggerEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
