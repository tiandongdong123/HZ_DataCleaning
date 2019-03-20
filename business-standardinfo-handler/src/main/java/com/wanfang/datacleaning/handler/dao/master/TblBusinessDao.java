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
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessStandardInfoBO>
     */
    List<BusinessStandardInfoBO> getStandardInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新标准信息
     *
     * @param standardInfoBO 标准信息
     * @return int
     */
    int updateStandardInfoByKey(BusinessStandardInfoBO standardInfoBO);

    /**
     * 批量更新标准信息
     *
     * @param standardInfoBOList 标准信息集合
     * @return int
     */
    int updateBatchStandardInfoByKey(List<BusinessStandardInfoBO> standardInfoBOList);
}
