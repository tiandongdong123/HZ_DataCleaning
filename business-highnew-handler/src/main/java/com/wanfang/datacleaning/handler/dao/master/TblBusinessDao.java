package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessHighNewInfoBO;
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
     * 分页获取高新技术企业信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessHighNewInfoBO>
     */
    List<BusinessHighNewInfoBO> getHighNewInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新高新技术企业信息
     *
     * @param highNewInfoBO
     * @return int
     */
    int updateHighNewInfoByKey(BusinessHighNewInfoBO highNewInfoBO);

    /**
     * 批量更新高新信息
     *
     * @param highNewInfoBOList
     * @return int
     */
    int updateBatchHighNewInfoByKey(List<BusinessHighNewInfoBO> highNewInfoBOList);

}
