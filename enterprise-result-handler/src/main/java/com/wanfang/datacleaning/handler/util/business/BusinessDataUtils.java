package com.wanfang.datacleaning.handler.util.business;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/7 13:51 
 *  @Version  V1.0   
 */
public class BusinessDataUtils {

    private static Map<String, String> baseEntNameInfo = new HashMap<>();

    private BusinessDataUtils() {
    }

    /**
     * 缓存企业(机构)名称信息
     *
     * @param entName 企业(机构)名称
     * @param pripid  主体身份代码
     */
    public static void cacheEntNameInfo(String entName, String pripid) {
        baseEntNameInfo.put(entName, pripid);
    }

    /**
     * 获取缓存的企业(机构)名称信息
     *
     * @return Map<String, String>
     */
    public static Map<String, String> getCacheEntNameInfo() {
        return baseEntNameInfo;
    }

    /**
     * 通过企业(机构)名称获取缓存的主体身份代码
     *
     * @param entName 企业(机构)名称
     * @return String 若无此entName，则返回""
     */
    public static String getCachePripidByEntName(String entName) {
        String pripid = baseEntNameInfo.get(entName);

        return StringUtils.defaultString(pripid);
    }

}
