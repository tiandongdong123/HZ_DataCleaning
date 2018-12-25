package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.model.bo.BusinessEntNameBO;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/7 13:51 
 *  @Version  V1.0   
 */
public class BusinessDataUtils {

    private static Map<String, List<BusinessEntNameBO>> baseEntNameInfoMap = new HashMap<>();

    private BusinessDataUtils() {
    }

    /**
     * 缓存企业(机构)名称信息
     *
     * @param businessEntNameBOList
     */
    public static void cacheEntNameInfo(List<BusinessEntNameBO> businessEntNameBOList) {
        String entName;
        List<BusinessEntNameBO> entNameBOList;

        for (BusinessEntNameBO entNameBO : businessEntNameBOList) {
            // 判断企业名称是否为空
            if (StringUtils.isBlank(entNameBO.getEntName())) {
                continue;
            }

            // 去除空白字符
            entName = StringUtils.deleteWhitespace(entNameBO.getEntName());
            if (baseEntNameInfoMap.containsKey(entName)) {
                baseEntNameInfoMap.get(entName).add(entNameBO);
            } else {
                entNameBOList = new LinkedList<>();
                entNameBOList.add(entNameBO);
                baseEntNameInfoMap.put(entName, entNameBOList);
            }
        }
    }

    /**
     * 获取缓存的企业(机构)名称信息长度
     *
     * @return int
     */
    public static int getCacheEntNameInfoMapSize() {
        return baseEntNameInfoMap.size();
    }

    /**
     * 通过企业名称获取缓存的企业(机构)名称信息
     *
     * @param entName 企业名称
     * @return List<BusinessEntNameBO>
     */
    public static List<BusinessEntNameBO> getCacheEntNameInfoListByEntNmae(String entName) {
        return baseEntNameInfoMap.get(entName);
    }

}
