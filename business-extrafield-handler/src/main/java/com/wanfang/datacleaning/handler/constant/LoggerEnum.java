package com.wanfang.datacleaning.handler.constant;

/**
 * 日志logger枚举
 *
 * @author yifei
 * @date 2019/1/19
 */
public enum LoggerEnum {
    /**
     * abnormalCodeDataLogger:异常代码数据
     */
    ABNORMAL_CODE_DATA("abnormalCodeDataLogger");

    private final String value;

    LoggerEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
