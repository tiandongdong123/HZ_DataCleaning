package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import com.wanfang.datacleaning.handler.model.bo.PatentPatTypeBO;
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
public class PatentDataUtils {
    private static final Logger abnormalCodeDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());
    private static Map<String, List<PatentPatTypeBO>> patTypeBOMapWithFilter = new HashMap<>(16);

    private PatentDataUtils() {
    }

    /**
     * 专利类型
     */
    public enum PatentTypeEnum {
        /**
         * 1：外观专利
         */
        APPEARANCE("1", "外观设计"),
        /**
         * 2：发明专利
         */
        INVENTION("2", "发明专利"),
        /**
         * 3：实用新型专利
         */
        UTILITY_MODEL("3", "实用新型");

        private String key;
        private String value;

        PatentTypeEnum(String key, String value) {
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
            for (PatentTypeEnum c : PatentTypeEnum.values()) {
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
     * 缓存专利类型信息(过滤不符合条件的数据，如：个人)
     *
     * @param patTypeBOList 专利类型信息集合
     */
    public static void cachePatTypeInfoMapWithFilter(List<PatentPatTypeBO> patTypeBOList) {
        if (patTypeBOList == null || patTypeBOList.isEmpty()) {
            return;
        }

        String[] propNameArray;
        String splitPropName;
        List<PatentPatTypeBO> typeBOList;
        String propNamesWithoutWhitespace;
        for (PatentPatTypeBO patTypeBO : patTypeBOList) {
            if (patTypeBO == null || StringUtils.isBlank(patTypeBO.getProposerName())) {
                continue;
            }

            // 去除空白字符
            propNamesWithoutWhitespace = StringUtils.deleteWhitespace(patTypeBO.getProposerName());
            // 分隔企业名称
            propNameArray = splitEntName(propNamesWithoutWhitespace);
            for (int j = 0; j < propNameArray.length; j++) {
                splitPropName = propNameArray[j];
                if (StringUtils.isEmpty(splitPropName)) {
                    continue;
                }

                if (patTypeBOMapWithFilter.containsKey(splitPropName)) {
                    patTypeBOMapWithFilter.get(splitPropName).add(patTypeBO);
                } else {
                    typeBOList = new LinkedList<>();
                    typeBOList.add(patTypeBO);
                    patTypeBOMapWithFilter.put(splitPropName, typeBOList);
                }
            }
        }
    }

    /**
     * 获取缓存的专利类型信息(过滤不符合条件的数据,如：个人)长度
     *
     * @return Map
     */
    public static int getCachePatTypeInfoMapWithFilterSize() {
        return patTypeBOMapWithFilter.size();
    }

    /**
     * 获取缓存的专利类型信息(过滤不符合条件的数据,如：个人)
     *
     * @return Map
     */
    public static Map<String, List<PatentPatTypeBO>> getCachePatTypeInfoMapWithFilter() {
        return patTypeBOMapWithFilter;
    }

    /**
     * 获取专利类型code
     *
     * @param patentTypeName 专利类型名称
     * @param defaultValue   默认值
     * @return String 未匹配到专利类型时，则返回默认值
     */
    public static String getPatentTypeCode(String patentTypeName, String defaultValue) {
        patentTypeName = StringUtils.deleteWhitespace(patentTypeName);

        PatentTypeEnum[] typeEnums = PatentTypeEnum.values();
        for (PatentTypeEnum typeEnum : typeEnums) {
            if (typeEnum.getValue().equals(patentTypeName)) {
                return typeEnum.getKey();
            }
        }
        abnormalCodeDataLogger.warn("专利类型：【{}】在枚举中不存在！", patentTypeName);
        return defaultValue;
    }

    /**
     * 处理专利类型字符串
     *
     * @param patTypeList 专利类型列表
     * @return String
     */
    public static String handlePatTypeList(String patTypeList) {
        if (StringUtils.isBlank(patTypeList)) {
            return StringUtils.EMPTY;
        }

        StringBuilder handleResultBuilder = new StringBuilder();
        PatentTypeEnum[] typeEnums = PatentTypeEnum.values();
        for (PatentTypeEnum typeEnum : typeEnums) {
            if (patTypeList.contains(typeEnum.getKey())) {
                handleResultBuilder.append(CmnConstant.SEPARATOR_COMMA).append(typeEnum.getKey());
            }
        }
        return handleResultBuilder.toString().replaceFirst(CmnConstant.SEPARATOR_COMMA, "");
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
