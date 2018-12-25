package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessExtraFieldBO;
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
     * 分页获取拓展字段信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessExtraFieldBO> getExtraFieldInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新拓展字段信息
     *
     * @param extraFieldBO
     * @return int
     */
    int updateExtraFieldByKey(BusinessExtraFieldBO extraFieldBO);

    /**
     * 批量更新拓展字段信息
     *
     * @param extraFieldBOList
     * @return int
     */
    int updateBatchExtraFieldByKey(List<BusinessExtraFieldBO> extraFieldBOList);

}
