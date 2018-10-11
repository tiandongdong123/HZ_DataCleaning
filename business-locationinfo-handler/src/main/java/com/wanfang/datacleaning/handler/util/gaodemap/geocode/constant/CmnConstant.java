package com.wanfang.datacleaning.handler.util.gaodemap.geocode.constant;

import com.wanfang.datacleaning.handler.util.PropertiesUtils;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/20 18:44 
 *  @Version  V1.0   
 */
public class CmnConstant {

    /**
     * 地理编码url
     */
    public static final String GEOCODE_URL = PropertiesUtils.getValue("gdMap_geocode_url");

    /**
     * 地理编码sig
     */
    public static final String GEOCODE_SIG = PropertiesUtils.getValue("gdMap_geocode_sig");
}
