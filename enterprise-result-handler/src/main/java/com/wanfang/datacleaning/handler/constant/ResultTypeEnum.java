package com.wanfang.datacleaning.handler.constant;

/**
 * @author yifei
 * @date 2019/3/3
 */
public enum ResultTypeEnum {
    /**
     * 1：专利
     */
    PATENT(1, "patent"),
    /**
     * 2：标准
     */
    STANDARD(2, "standard"),
    /**
     * 3：成果
     */
    CSTAD(3, "cstad");

    private int key;
    private String value;

    ResultTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return 若无此key, 则返回null
     */
    public static String getName(int key) {
        for (ResultTypeEnum c : ResultTypeEnum.values()) {
            if (c.getKey() == key) {
                return c.getValue();
            }
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
