package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.CstadCompUnitBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/1 22:42 
 *  @Version  V1.0   
 */
public interface TblCstadDao {

    /**
     * 分页获取完成单位信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<CstadCompUnitBO>
     */
    List<CstadCompUnitBO> getBaseCompUnitInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

}
