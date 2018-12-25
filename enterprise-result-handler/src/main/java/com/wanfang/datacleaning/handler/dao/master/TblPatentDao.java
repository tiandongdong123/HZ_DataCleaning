package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.PatentProposerNameBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/1 22:42 
 *  @Version  V1.0   
 */
public interface TblPatentDao {

    /**
     * 分页获取申请（专利权）人信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<PatentProposerNameBO>
     */
    List<PatentProposerNameBO> getBaseProposerNameInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

}
