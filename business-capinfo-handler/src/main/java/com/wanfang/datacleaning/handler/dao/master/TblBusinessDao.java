package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessCapInfoBO;
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
     * 分页获取资金信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessCapInfoBO> getCapInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新资金信息
     *
     * @param capInfoBO 资金信息
     * @return int
     */
    int updateCapInfoByKey(BusinessCapInfoBO capInfoBO);

    /**
     * 批量更新资金信息
     *
     * @param capInfoBOList 资金信息集合
     * @return int
     */
    int updateBatchCapInfoByKey(List<BusinessCapInfoBO> capInfoBOList);

}
