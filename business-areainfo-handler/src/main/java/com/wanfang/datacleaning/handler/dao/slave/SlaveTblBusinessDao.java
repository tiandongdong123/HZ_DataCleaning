package com.wanfang.datacleaning.handler.dao.slave;

import com.wanfang.datacleaning.handler.model.bo.BusinessAreaInfoBO;
import com.wanfang.datacleaning.handler.model.bo.ShortBusinessAreaInfoBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/1 22:41 
 *  @Version  V1.0   
 */
public interface SlaveTblBusinessDao {

    /**
     * 批量更新行政区划信息
     *
     * @param areaInfoBOList
     * @return int
     */
    int updateBatchAreaInfoByKey(List<ShortBusinessAreaInfoBO> areaInfoBOList);

}
