package com.wanfang.datacleaning.handler.service;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:44 
 *  @Version  V1.0   
 */
public interface EnterpriseResultService {
    /**
     * 缓存企业(机构)名称信息
     *
     * @return boolean
     */
    boolean cacheBaseEntNameInfo();

    /**
     * 同步企业成果信息
     */
    void syncEntResultInfo();
}
