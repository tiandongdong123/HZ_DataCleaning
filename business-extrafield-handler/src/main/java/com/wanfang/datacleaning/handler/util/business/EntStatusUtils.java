package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yifei
 * @date 2019/1/19
 */
public class EntStatusUtils {

    private static final Logger abnormalCodeDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private EntStatusUtils() {
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

        public String getValue() {
            return value;
        }
    }

    /**
     * 获取企业状态代码
     *
     * @param statusName   企业状态名称
     * @param defaultValue 默认值
     * @return 若匹配不到statusName，则返回defaultValue
     */
    public static String getEntStatusCode(String statusName, String defaultValue) {
        // 去除空白字符
        statusName = StringUtils.deleteWhitespace(statusName);

        if (EntStatusEnum.DOING_BUSINESS.getValue().equals(statusName)) {
            return EntStatusEnum.DOING_BUSINESS.getKey();
        } else if (EntStatusEnum.REVOCATION.getValue().equals(statusName)) {
            return EntStatusEnum.REVOCATION.getKey();
        } else if (EntStatusEnum.CANCEL.getValue().equals(statusName)) {
            return EntStatusEnum.CANCEL.getKey();
        } else if (EntStatusEnum.MOVING_OUT.getValue().equals(statusName)) {
            return EntStatusEnum.MOVING_OUT.getKey();
        }

        abnormalCodeDataLogger.warn("statusName：【{}】，defaultValue：【{}】，没有对应的企业状态码", statusName, defaultValue);
        return defaultValue;
    }
}
