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
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessExtraFieldBO> getExtraFieldInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新拓展字段信息
     *
     * @param extraFieldBO 拓展字段信息
     * @return int
     */
    int updateExtraFieldByKey(BusinessExtraFieldBO extraFieldBO);

    /**
     * 批量更新拓展字段信息
     *
     * @param extraFieldBOList 拓展字段信息集合
     * @return int
     */
    int updateBatchExtraFieldByKey(List<BusinessExtraFieldBO> extraFieldBOList);

}
