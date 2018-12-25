package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessAreaInfoBO;
import com.wanfang.datacleaning.handler.model.bo.ShortBusinessAreaInfoBO;
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
     * 分页获取行政区划信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessAreaInfoBO>
     */
    List<BusinessAreaInfoBO> getAreaInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新行政区划信息
     *
     * @param areaInfoBO
     * @return int
     */
    int updateAreaInfoByKey(BusinessAreaInfoBO areaInfoBO);

    /**
     * 批量更新行政区划信息
     *
     * @param areaInfoBOList
     * @return int
     */
    int updateBatchAreaInfoByKey(List<BusinessAreaInfoBO> areaInfoBOList);

    /**
     * 分页获取行政区划信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessAreaInfoBO>
     */
    List<ShortBusinessAreaInfoBO> getShortAreaInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);
}
