package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.UsCreditCodeBO;
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
     * 分页获取统一社会信用代码信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessEntNameBO>
     */
    List<UsCreditCodeBO> getUsCreditCodeByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新统一社会信用代码信息
     *
     * @param creditCodeBO 统一社会信用代码信息
     * @return int
     */
    int updateUsCreditCodeByKey(UsCreditCodeBO creditCodeBO);

    /**
     * 批量更新统一社会信用代码信息
     *
     * @param creditCodeBOList 统一社会信用代码信息集合
     * @return int
     */
    int updateBatchUsCreditCodeByKey(List<UsCreditCodeBO> creditCodeBOList);

}
