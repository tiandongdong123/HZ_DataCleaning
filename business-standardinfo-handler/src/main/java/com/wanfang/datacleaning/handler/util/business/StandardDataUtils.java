package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import com.wanfang.datacleaning.handler.model.bo.StandardStdTypeBO;
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

    private static final Logger abCodeDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

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

        public String getValue() {
            return value;
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
     * @param stdTypeBOList 标准类型信息集
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
            return StringUtils.EMPTY;
        }

        String effectLevelName = StandardCodeUtils.getEffectLevelByCode(StringUtils.deleteWhitespace(stdType));
        if (StringUtils.isBlank(effectLevelName)) {
            abCodeDataLogger.warn("stdType：【{}】，标准代码文件中不含此代码", stdType);
            return StringUtils.EMPTY;
        }

        EffectLevelEnum[] levelEnums = EffectLevelEnum.values();
        for (EffectLevelEnum levelEnum : levelEnums) {
            if (levelEnum.getValue().equals(effectLevelName)) {
                return levelEnum.getKey();
            }
        }
        abCodeDataLogger.warn("stdType：【{}】，effectLevelName：【{}】，效力级别枚举不含此值，请确认后添加！", stdType, effectLevelName);
        return StringUtils.EMPTY;
    }

    /**
     * 处理起草标准类型字符串
     *
     * @param stdTypeList 起草标准类型列表
     * @return String
     */
    public static String handleDrStdTypeList(String stdTypeList) {
        if (StringUtils.isBlank(stdTypeList)) {
            return StringUtils.EMPTY;
        }

        StringBuilder handleResultBuilder = new StringBuilder();
        EffectLevelEnum[] levelEnums = EffectLevelEnum.values();
        for (EffectLevelEnum levelEnum : levelEnums) {
            if (stdTypeList.contains(levelEnum.getKey())) {
                handleResultBuilder.append(CmnConstant.SEPARATOR_COMMA).append(levelEnum.getKey());
            }
        }
        return handleResultBuilder.toString().replaceFirst(CmnConstant.SEPARATOR_COMMA, "");
    }

    /**
     * 是否符合标准状态
     *
     * @param stdStatus 标准状态
     * @return boolean true：符合
     */
    public static boolean meetStdStatus(String stdStatus) {
        if (StringUtils.isBlank(stdStatus)) {
            return false;
        }

        stdStatus = StringUtils.deleteWhitespace(stdStatus);
        StandardStatusEnum[] statusEnums = StandardStatusEnum.values();
        for (StandardStatusEnum statusEnum : statusEnums) {
            if (statusEnum.getValue().equals(stdStatus)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 分隔企业名称
     *
     * @param entName 企业名称
     * @return String[]
     */
    private static String[] splitEntName(String entName) {

        if (entName.contains(CmnConstant.SEPARATOR_SEMICOLON)) {
            return StringUtils.split(entName, CmnConstant.SEPARATOR_SEMICOLON);
        }

        if (entName.contains(CmnConstant.SEPARATOR_CN_SEMICOLON)) {
            return StringUtils.split(entName, CmnConstant.SEPARATOR_CN_SEMICOLON);
        }

        return new String[]{entName};
    }
}
