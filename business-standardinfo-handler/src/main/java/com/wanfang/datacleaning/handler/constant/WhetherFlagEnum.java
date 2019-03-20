package com.wanfang.datacleaning.handler.constant;

/**
 * 是否
 *
 * @author yifei
 * @date 2019/1/19
 */
public enum WhetherFlagEnum {
    /**
     * 否：0
     */
    NO(0),
    /**
     * 是：1
     */
    YES(1);

    private final int value;

    WhetherFlagEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
