package com.wanfang.datacleaning.util.business;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2019/1/26 10:43 
 *  @Version  V1.0   
 */
public class CmnUtils {

    private CmnUtils() {
    }

    /**
     * 处理每页数量
     *
     * @param startPosition 开始位置
     * @param endPosition   结束位置
     * @param pageSize      每页数量
     * @return int
     */
    public static int handlePageSize(int startPosition, int endPosition, int pageSize) {
        return (endPosition - startPosition) >= pageSize ? pageSize : (endPosition - startPosition) + 1;
    }

    /**
     * 获取结束位置
     *
     * @param startPosition 开始位置
     * @param pageSize      每页数量
     * @return int
     */
    public static int getEndPosition(int startPosition, int pageSize) {
        return startPosition + pageSize - 1;
    }
}
