package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import org.apache.commons.lang3.StringUtils;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/21 10:37 
 *  @Version  V1.0   
 */
public class CommonUtils {

    private CommonUtils() {
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
