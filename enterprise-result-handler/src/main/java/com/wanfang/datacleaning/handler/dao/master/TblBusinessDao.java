package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessEntNameBO;
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
     * 分页获取企业(机构)名称信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessEntNameBO> getBaseEntNameInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

}
