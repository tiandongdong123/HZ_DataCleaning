package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.model.bo.StandardStdTypeBO;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:54 
 *  @Version  V1.0   
 */
public class StandardDataUtils {

    private static final Logger abCodeDataLogger = LoggerFactory.getLogger(CmnEnum.LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private static Map<String, List<StandardStdTypeBO>> stdTypeBOMapWithFilter = new HashMap<>(16);

    private StandardDataUtils() {
    }

    /**
     * 效力级别
     */
    public enum EffectLevelEnum {
        /**
         * 1：国家标准
         */
        COUNTRY("1", "国家标准"),
        /**
         * 2：行业标准
         */
        INDUSTRY("2", "行业标准"),
        /**
         * 3：企业标准
         */
        ENTERPRISE("3", "企业标准");

        private String key;
        private String value;

        EffectLevelEnum(String key, String value) {
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
            for (EffectLevelEnum c : EffectLevelEnum.values()) {
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
     * 标准状态
     */
    public enum StandardStatusEnum {

        /**
         * 现行
         */
        XX("现行"),

        /**
         * 废止
         */
        FZ("废止"),

        /**
         * 作废
         */
        ZF("作废");

        private final String value;

        StandardStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 缓存标准类型信息(过滤不符合条件的数据)
     *
     * @param stdTypeBOList
     */
    public static void cacheStdTypeInfoMapWithFilter(List<StandardStdTypeBO> stdTypeBOList) {
        if (stdTypeBOList != null && stdTypeBOList.size() > 0) {
            String[] draftUnitArray;
            String splitDraftUnit;
            List<StandardStdTypeBO> typeBOList;
            String draftUnitsWithoutWhitespace;

            for (StandardStdTypeBO stdTypeBO : stdTypeBOList) {
                if (stdTypeBO == null || StringUtils.isBlank(stdTypeBO.getDraftUnit())) {
                    continue;
                }

                // 去除空白字符
                draftUnitsWithoutWhitespace = StringUtils.deleteWhitespace(stdTypeBO.getDraftUnit());
                // 分隔企业名称
                draftUnitArray = splitEntName(draftUnitsWithoutWhitespace);
                for (int j = 0; j < draftUnitArray.length; j++) {
                    splitDraftUnit = draftUnitArray[j];
                    if (StringUtils.isEmpty(splitDraftUnit)) {
                        continue;
                    }

                    if (stdTypeBOMapWithFilter.containsKey(splitDraftUnit)) {
                        stdTypeBOMapWithFilter.get(splitDraftUnit).add(stdTypeBO);
                    } else {
                        typeBOList = new LinkedList<>();
                        typeBOList.add(stdTypeBO);
                        stdTypeBOMapWithFilter.put(splitDraftUnit, typeBOList);
                    }
                }
            }
        }
    }

    /**
     * 获取缓存的标准类型信息(过滤不符合条件的数据)长度
     *
     * @return int
     */
    public static int getCacheStdTypeInfoMapWithFilterSize() {
        return stdTypeBOMapWithFilter.size();
    }

    /**
     * 获取缓存的标准类型信息(过滤不符合条件的数据)
     *
     * @return int
     */
    public static Map<String, List<StandardStdTypeBO>> getCacheStdTypeInfoMapWithFilter() {
        return stdTypeBOMapWithFilter;
    }

    /**
     * 获取起草标准类型
     *
     * @param stdType 标准类型
     * @return String
     */
    public static String getDrStdType(String stdType) {
        if (StringUtils.isBlank(stdType)) {
            return "";
        }

        String drStdType = "";
        String effectLevelName = StandardCodeUtils.getEffectLevelByCode(StringUtils.deleteWhitespace(stdType));
        if (StringUtils.isEmpty(effectLevelName)) {
            LoggerUtils.appendWarnLog(abCodeDataLogger, "stdType：【{}】，标准代码文件中不含此代码", stdType);
            return "";
        }

        if (EffectLevelEnum.COUNTRY.getValue().equals(effectLevelName)) {
            drStdType = EffectLevelEnum.COUNTRY.getKey();
        } else if (EffectLevelEnum.INDUSTRY.getValue().equals(effectLevelName)) {
            drStdType = EffectLevelEnum.INDUSTRY.getKey();
        } else if (EffectLevelEnum.ENTERPRISE.getValue().equals(effectLevelName)) {
            drStdType = EffectLevelEnum.ENTERPRISE.getKey();
        }

        return drStdType;
    }

    /**
     * 处理起草标准类型字符串
     *
     * @param stdTypeList
     * @return String
     */
    public static String handleStdTypeList(String stdTypeList) {
        if (stdTypeList == null) {
            return "";
        }

        StringBuilder handleResultBuilder = new StringBuilder("");
        if (stdTypeList.contains(EffectLevelEnum.COUNTRY.getKey())) {
            handleResultBuilder.append(CmnConstant.SEPARATOR_COMMA).append(EffectLevelEnum.COUNTRY.getKey());
        }

        if (stdTypeList.contains(EffectLevelEnum.INDUSTRY.getKey())) {
            handleResultBuilder.append(CmnConstant.SEPARATOR_COMMA).append(EffectLevelEnum.INDUSTRY.getKey());
        }

        if (stdTypeList.contains(EffectLevelEnum.ENTERPRISE.getKey())) {
            handleResultBuilder.append(CmnConstant.SEPARATOR_COMMA).append(EffectLevelEnum.ENTERPRISE.getKey());
        }

        return handleResultBuilder.toString().replaceFirst(CmnConstant.SEPARATOR_COMMA, "");
    }

    /**
     * 是否符合标准状态
     *
     * @param stdStatus
     * @return boolean
     */
    public static boolean meetStdStatus(String stdStatus) {
        boolean meetFlag = false;

        if (StringUtils.isBlank(stdStatus)) {
            return false;
        }

        if (StandardStatusEnum.XX.getValue().equals(stdStatus)) {
            meetFlag = true;
        } else if (StandardStatusEnum.FZ.getValue().equals(stdStatus)) {
            meetFlag = true;
        } else if (StandardStatusEnum.ZF.getValue().equals(stdStatus)) {
            meetFlag = true;
        }

        return meetFlag;
    }

    /**
     * 分隔企业名称
     *
     * @param entName 企业名称
     * @return String[]
     */
    public static String[] splitEntName(String entName) {

        if (entName.contains(CmnConstant.SEPARATOR_SEMICOLON)) {
            return StringUtils.split(entName, CmnConstant.SEPARATOR_SEMICOLON);
        }

        if (entName.contains(CmnConstant.SEPARATOR_CN_SEMICOLON)) {
            return StringUtils.split(entName, CmnConstant.SEPARATOR_CN_SEMICOLON);
        }

        return new String[]{entName};
    }
}
