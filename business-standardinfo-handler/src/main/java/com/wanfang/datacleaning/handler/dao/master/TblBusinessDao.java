package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessStandardInfoBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/1 22:41 
 *  @Version  V1.0   
 */
public interface TblBusinessDao {

    /**
     * 分页获取标准信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessStandardInfoBO>
     */
    List<BusinessStandardInfoBO> getStandardInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新标准信息
     *
     * @param standardInfoBO
     * @return int
     */
    int updateStandardInfoByKey(BusinessStandardInfoBO standardInfoBO);

    /**
     * 批量更新标准信息
     *
     * @param standardInfoBOList
     * @return int
     */
    int updateBatchStandardInfoByKey(List<BusinessStandardInfoBO> standardInfoBOList);
}
