package com.wanfang.datacleaning.handler.constant;

import com.wanfang.datacleaning.handler.util.PropertiesUtils;

/**
 * @author yifei
 * @date 2018/12/16
 */
public class CmnConstant {
    /**
     * id起始位置
     */
    public static final int ID_START_POSITION = Integer.parseInt(PropertiesUtils.getValue("id.start.position"));

    /**
     * id结束位置
     */
    public static final int ID_END_POSITION = Integer.parseInt(PropertiesUtils.getValue("id.end.position"));

    /**
     * 每页数量
     */
    public static final int PAGE_SIZE = Integer.parseInt(PropertiesUtils.getValue("pageSize"));

    /**
     * 数据库批量更新大小
     */
    public static final int BATCH_SIZE = Integer.parseInt(PropertiesUtils.getValue("batchSize"));

    private CmnConstant() {
    }
}
