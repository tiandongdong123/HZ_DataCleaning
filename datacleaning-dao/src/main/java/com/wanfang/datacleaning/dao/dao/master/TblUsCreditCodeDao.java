package com.wanfang.datacleaning.dao.dao.master;

import com.wanfang.datacleaning.dao.model.master.bo.UsCreditCodeBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/11/6 23:50 
 *  @Version  V1.0   
 */
public interface TblUsCreditCodeDao {
    /**
     * 分页获取统一社会信用代码信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<UsCreditCodeBO>
     */
    List<UsCreditCodeBO> getUsCreditCodeByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);
}
