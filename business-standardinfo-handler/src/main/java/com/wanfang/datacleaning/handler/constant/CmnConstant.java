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
