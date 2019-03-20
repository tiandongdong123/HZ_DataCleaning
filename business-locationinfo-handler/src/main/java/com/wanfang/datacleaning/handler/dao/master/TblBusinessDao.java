package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessLocationInfoBO;
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
     * 分页获取位置信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessLocationInfoBO> getLocationInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新位置信息
     *
     * @param locationInfoBO
     * @return int
     */
    int updateLocationInfoByKey(BusinessLocationInfoBO locationInfoBO);

    /**
     * 批量更新位置信息
     *
     * @param locationInfoBOList 位置信息集合
     * @return int
     */
    int updateBatchLocationInfoByKey(List<BusinessLocationInfoBO> locationInfoBOList);

}
