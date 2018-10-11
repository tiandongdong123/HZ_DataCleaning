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
}
