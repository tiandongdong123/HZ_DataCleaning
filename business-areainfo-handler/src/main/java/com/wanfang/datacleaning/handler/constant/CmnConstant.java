package com.wanfang.datacleaning.handler.constant;

import com.wanfang.datacleaning.handler.util.PropertiesUtils;

/**
 * @author yifei
 * @date 2018/11/25
 */
public class CmnConstant {

    /**
     * 起始位置
     */
    public static final int START_INDEX = Integer.parseInt(PropertiesUtils.getValue("startIndex"));

    /**
     * 结束位置
     */
    public static final int END_INDEX = Integer.parseInt(PropertiesUtils.getValue("endIndex"));

    /**
     * 每页数量
     */
    public static final int PAGE_SIZE = Integer.parseInt(PropertiesUtils.getValue("pageSize"));

    /**
     * 数据库批量更新大小
     */
    public static final int BATCH_SIZE = Integer.parseInt(PropertiesUtils.getValue("batchSize"));

    /**
     * 国家工商行政管理总局代码：100000
     */
    public static final String SAIC_CODE = "100000";
}
