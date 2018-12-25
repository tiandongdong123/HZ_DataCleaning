package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.BusinessPatentInfoBO;
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
     * 分页获取专利信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessPatentInfoBO>
     */
    List<BusinessPatentInfoBO> getPatentInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新专利字段信息
     *
     * @param patentInfoBO
     * @return int
     */
    int updatePatentInfoByKey(BusinessPatentInfoBO patentInfoBO);

    /**
     * 批量更新专利字段信息
     *
     * @param patentInfoBOList
     * @return int
     */
    int updateBatchPatentInfoByKey(List<BusinessPatentInfoBO> patentInfoBOList);
}
