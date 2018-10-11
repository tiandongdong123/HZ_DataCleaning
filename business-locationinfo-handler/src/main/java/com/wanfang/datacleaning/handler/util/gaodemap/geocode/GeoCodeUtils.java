package com.wanfang.datacleaning.handler.util.gaodemap.geocode;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.constant.CmnConstant;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.model.GCQryParam;
import com.wanfang.datacleaning.handler.util.gaodemap.geocode.model.GCQryResult;
import com.wanfang.datacleaning.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *    
 *  @Description 高德地图工具类
 *  @Author   luqs   
 *  @Date 2018/8/20 12:10 
 *  @Version  V1.0   
 */
public class GeoCodeUtils {

    private GeoCodeUtils() {
    }

    /**
     * 获取地理编码
     *
     * @param qryParam
     * @return GeoCoderQryResult
     */
    public static GCQryResult getGeoCode(GCQryParam qryParam) {

        StringBuilder urlStrBuilder = new StringBuilder(CmnConstant.GEOCODE_URL)
                .append("key=").append(qryParam.getKey())
                .append("&address=").append(qryParam.getAddress())
                .append("&city=").append(StringUtils.defaultString(qryParam.getCity()))
                .append("&batch=").append(StringUtils.defaultString(qryParam.getBatch()))
                .append("&sig=").append(qryParam.getSig())
                .append("&output=").append(StringUtils.defaultString(qryParam.getOutput()))
                .append("&callback=").append(StringUtils.defaultString(qryParam.getCallback()));

        String resultStr = HttpUtils.requestByGet(urlStrBuilder.toString());

        GCQryResult qryResult = JSON.parseObject(resultStr, GCQryResult.class);

        return qryResult;
    }
}
