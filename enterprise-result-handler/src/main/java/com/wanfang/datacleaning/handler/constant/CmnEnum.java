package com.wanfang.datacleaning.handler.constant;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/4 16:26 
 *  @Version  V1.0   
 */
public class CmnEnum {
    private CmnEnum() {
    }

    /**
     * 是否
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

    /**
     * 成果类型
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

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
