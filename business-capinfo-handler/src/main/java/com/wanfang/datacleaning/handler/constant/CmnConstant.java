package com.wanfang.datacleaning.handler.constant;

import com.wanfang.datacleaning.handler.util.PropertiesUtils;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/7 14:02 
 *  @Version  V1.0   
 */
public class CmnConstant {

    /**
     * 默认起始位置
     */
    public static final int DEFAULT_START_INDEX = Integer.parseInt(PropertiesUtils.getValue("startIndex"));

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.parseInt(PropertiesUtils.getValue("pageSize"));

    /**
     * 数据库批量更新大小
     */
    public static final int BATCH_SIZE = Integer.parseInt(PropertiesUtils.getValue("batchSize"));

    /**
     * 国家工商行政管理总局代码：100000
     */
    public static final String SAIC_CODE = "100000";

    private CmnConstant() {
    }
}
