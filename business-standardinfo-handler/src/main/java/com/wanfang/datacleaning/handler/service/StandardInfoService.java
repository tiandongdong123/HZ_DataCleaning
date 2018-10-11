package com.wanfang.datacleaning.handler.service;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:44 
 *  @Version  V1.0   
 */
public interface StandardInfoService {
    /**
     * 缓存标准类型信息
     *
     * @return boolean
     */
    boolean cacheBaseStandardTypeInfo();

    /**
     * 更新标准信息
     */
    void updateStandardInfo();
}
