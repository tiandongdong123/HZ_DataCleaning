package com.wanfang.datacleaning.dao.dao.master;

import com.wanfang.datacleaning.dao.model.master.bo.*;
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

    /**
     * 分页获取拓展字段信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessExtraFieldBO> getExtraFieldInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新拓展字段信息
     *
     * @param extraFieldBO
     * @return int
     */
    int updateExtraFieldByKey(BusinessExtraFieldBO extraFieldBO);

    /**
     * 批量更新拓展字段信息
     *
     * @param extraFieldBOList
     * @return int
     */
    int updateBatchExtraFieldByKey(List<BusinessExtraFieldBO> extraFieldBOList);

    /**
     * 分页获取高新技术企业信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessHighNewInfoBO> getHighNewInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新高新技术企业信息
     *
     * @param highNewInfoBO
     * @return int
     */
    int updateHighNewInfoByKey(BusinessHighNewInfoBO highNewInfoBO);

    /**
     * 批量更新高新信息
     *
     * @param highNewInfoBOList
     * @return int
     */
    int updateBatchHighNewInfoByKey(List<BusinessHighNewInfoBO> highNewInfoBOList);

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

    /**
     * 分页获取标准信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessStandardInfoBO>
     */
    List<BusinessStandardInfoBO> getStandardInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新标准信息
     *
     * @param standardInfoBO
     * @return int
     */
    int updateStandardInfoByKey(BusinessStandardInfoBO standardInfoBO);

    /**
     * 批量更新标准信息
     *
     * @param standardInfoBOList
     * @return int
     */
    int updateBatchStandardInfoByKey(List<BusinessStandardInfoBO> standardInfoBOList);

    /**
     * 分页获取位置信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessLocationInfoBO> getLocationInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新位置信息
     *
     * @param locationInfoBO
     * @return int
     */
    int updateLocationInfoByKey(BusinessLocationInfoBO locationInfoBO);

    /**
     * 批量更新位置信息
     *
     * @param locationInfoBOList
     * @return int
     */
    int updateBatchLocationInfoByKey(List<BusinessLocationInfoBO> locationInfoBOList);

    /**
     * 分页获取资金信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<BusinessCapInfoBO> getCapInfoByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新资金信息
     *
     * @param capInfoBO
     * @return int
     */
    int updateCapInfoByKey(BusinessCapInfoBO capInfoBO);

    /**
     * 批量更新资金信息
     *
     * @param capInfoBOList
     * @return int
     */
    int updateBatchCapInfoByKey(List<BusinessCapInfoBO> capInfoBOList);

    /**
     * 分页获取统一社会信用代码信息
     *
     * @param startIndex 起始位置
     * @param pageSize   每页数量
     * @return List<BusinessEntNameBO>
     */
    List<UsCreditCodeBO> getUsCreditCodeByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 更新统一社会信用代码信息
     *
     * @param creditCodeBO
     * @return int
     */
    int updateUsCreditCodeByKey(UsCreditCodeBO creditCodeBO);

    /**
     * 批量更新统一社会信用代码信息
     *
     * @param creditCodeBOList
     * @return int
     */
    int updateBatchUsCreditCodeByKey(List<UsCreditCodeBO> creditCodeBOList);

}
