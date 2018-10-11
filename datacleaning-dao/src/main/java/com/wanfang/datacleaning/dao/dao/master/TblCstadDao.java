package com.wanfang.datacleaning.dao.dao.master;

import com.wanfang.datacleaning.dao.model.master.bo.CstadCompUnitBO;
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
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<CstadCompUnitBO>
     */
    List<CstadCompUnitBO> getBaseCompUnitInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

}
