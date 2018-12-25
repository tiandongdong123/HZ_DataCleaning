package com.wanfang.datacleaning.util.gaodemap.constant;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/20 18:48 
 *  @Version  V1.0   
 */
public class CmnEnum {
    /**
     * 请求状态
     */
    public enum RequestStatusEnum {
        /**
         * 0：请求失败
         */
        FAIL("0"),
        /**
         * 1：请求成功
         */
        SUCCESS("1");

        private final String value;

        RequestStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 数据格式
     */
    public enum ResponseDataFormatEnum {
        /**
         * JSON：JSON格式
         */
        JSON("JSON"),
        /**
         * XML：XML格式
         */
        XML("XML");

        private final String value;

        ResponseDataFormatEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 批量查询控制
     */
    public enum BatchEnum {
        /**
         * true ：批量查询
         */
        TRUE("true "),
        /**
         * false：单条查询
         */
        FALSE("false");

        private final String value;

        BatchEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
