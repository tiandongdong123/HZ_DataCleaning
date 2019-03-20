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
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     * @return List<BusinessPatentInfoBO>
     */
    List<BusinessPatentInfoBO> getPatentInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("idEndPosition") int idEndPosition, @Param("pageSize") int pageSize);

    /**
     * 更新专利字段信息
     *
     * @param patentInfoBO 利字段信息
     * @return int
     */
    int updatePatentInfoByKey(BusinessPatentInfoBO patentInfoBO);

    /**
     * 批量更新专利字段信息
     *
     * @param patentInfoBOList 利字段信息集合
     * @return int
     */
    int updateBatchPatentInfoByKey(List<BusinessPatentInfoBO> patentInfoBOList);
}
