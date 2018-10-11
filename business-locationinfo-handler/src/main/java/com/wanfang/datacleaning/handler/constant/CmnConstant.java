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
     * 分隔符-分号（;）
     */
    public static final String SEPARATOR_SEMICOLON = ";";
    /**
     * 分隔符-中文分号（；）
     */
    public static final String SEPARATOR_CN_SEMICOLON = "；";
    /**
     * 分隔符-逗号（,）
     */
    public static final String SEPARATOR_COMMA = ",";

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

    private CmnConstant() {
    }
}
