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
     * 企业状态
     */
    public enum EntStatusEnum {
        /**
         * 1：在营（开业）企业
         */
        DOING_BUSINESS("1", "在营（开业）企业"),

        /**
         * 2：吊销企业
         */
        REVOCATION("2", "吊销企业"),
        /**
         * 3：注销企业
         */
        CANCEL("3", "注销企业"),
        /**
         * 4：迁出
         */
        MOVING_OUT("4", "迁出");

        private String key;
        private String value;

        EntStatusEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 通过key获取值
         *
         * @param key
         * @return 若无此key, 则返回null
         */
        public static String getName(String key) {
            for (EntStatusEnum c : EntStatusEnum.values()) {
                if (c.getKey().equals(key)) {
                    return c.getValue();
                }
            }
            return null;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 日志logger枚举
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
}
