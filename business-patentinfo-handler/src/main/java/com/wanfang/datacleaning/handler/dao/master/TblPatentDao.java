package com.wanfang.datacleaning.handler.dao.master;

import com.wanfang.datacleaning.handler.model.bo.PatentPatTypeBO;
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
     * 分页获取专利类型信息
     *
     * @param idStartPosition id起始位置
     * @param pageSize        每页数量
     * @return List<PatentPatTypeBO>
     */
    List<PatentPatTypeBO> getBasePatentTypeInfoByPage(@Param("idStartPosition") int idStartPosition, @Param("pageSize") int pageSize);
}
