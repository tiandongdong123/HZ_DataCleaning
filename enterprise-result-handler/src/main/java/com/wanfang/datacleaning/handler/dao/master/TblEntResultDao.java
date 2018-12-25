package com.wanfang.datacleaning.handler.dao.master;


import com.wanfang.datacleaning.handler.model.entity.master.TblEntResultEntity;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/29 18:34 
 *  @Version  V1.0   
 */
public interface TblEntResultDao {

    /**
     * 添加
     *
     * @param resultEntity
     * @return int
     */
    int add(TblEntResultEntity resultEntity);

    /**
     * 批量添加
     *
     * @param resultEntityList
     * @return int
     */
    int addBatch(List<TblEntResultEntity> resultEntityList);
}
