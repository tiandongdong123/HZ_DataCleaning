package com.wanfang.datacleaning.handler.service;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/11/7 9:33 
 *  @Version  V1.0   
 */
public interface UsCreditCodeService {

    /**
     * 缓存统一社会信用代码信息
     *
     * @return boolean
     */
    boolean cacheUsCreditCodeInfo();

    /**
     * 更新统一社会信用代码信息
     */
    void updateUsCreditCode();
}
