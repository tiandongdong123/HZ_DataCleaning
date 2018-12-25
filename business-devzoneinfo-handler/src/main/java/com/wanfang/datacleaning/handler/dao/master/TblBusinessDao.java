package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessDevZoneInfoBO;
import com.wanfang.datacleaning.handler.model.bo.DevZoneInfoBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yifei
 * @date 2018/12/16
 */
public interface TblBusinessDao {

    /**
     * 分页获取经纬度字段信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessDevZoneInfoBO> getDevZoneInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新开发区字段信息
     *
     * @param devZoneInfoBO
     * @return int
     */
    int updateDevZoneInfoByKey(BusinessDevZoneInfoBO devZoneInfoBO);

    /**
     * 批量更新开发区字段信息
     *
     * @param devZoneInfoBOList
     * @return int
     */
    int updateBatchDevZoneInfoByKey(List<BusinessDevZoneInfoBO> devZoneInfoBOList);

    /**
     * 分页获取经纬度信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<DevZoneInfoBO> getLatLonInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

}
