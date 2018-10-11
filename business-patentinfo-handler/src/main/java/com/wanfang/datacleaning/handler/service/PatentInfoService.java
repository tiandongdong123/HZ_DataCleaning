package com.wanfang.datacleaning.handler.service;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 16:44 
 *  @Version  V1.0   
 */
public interface PatentInfoService {
    /**
     * 缓存专利类型信息
     *
     * @return boolean
     */
    boolean cacheBasePatentTypeInfo();

    /**
     * 更新专利信息
     */
    void updatePatentInfo();
}
