package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.StandardStdTypeBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/1 22:42 
 *  @Version  V1.0   
 */
public interface TblStandardDao {

    /**
     * 分页获取标准类型信息
     *
     * @param idStartPosition id起始位置
     * @param pageSize        每页数量
     * @return List<StandardStdTypeBO>
     */
    List<StandardStdTypeBO> getBaseStandardTypeInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("pageSize") int pageSize);

}
