package com.wanfang.datacleaning.util.gaodemap.geocode;

import com.alibaba.fastjson.JSON;
import com.wanfang.datacleaning.util.HttpUtils;
import com.wanfang.datacleaning.util.gaodemap.geocode.model.GcQryParam;
import com.wanfang.datacleaning.util.gaodemap.geocode.model.GcQryResult;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    public static GcQryResult getGeoCode(String url, GcQryParam qryParam) throws IOException {

        StringBuilder urlStrBuilder = new StringBuilder(url)
                .append("key=").append(qryParam.getKey())
                .append("&address=").append(qryParam.getAddress())
                .append("&city=").append(StringUtils.defaultString(qryParam.getCity()))
                .append("&batch=").append(StringUtils.defaultString(qryParam.getBatch()))
                .append("&sig=").append(qryParam.getSig())
                .append("&output=").append(StringUtils.defaultString(qryParam.getOutput()))
                .append("&callback=").append(StringUtils.defaultString(qryParam.getCallback()));

        String resultStr = HttpUtils.requestByGet(urlStrBuilder.toString());

        GcQryResult qryResult = JSON.parseObject(resultStr, GcQryResult.class);

        return qryResult;
    }

    /**
     * 处理地址中的特殊字符
     *
     * @param address 地址
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String handleSpecialCharInAddress(String address) throws UnsupportedEncodingException {
        address = StringUtils.deleteWhitespace(address);
        return URLEncoder.encode(address, "UTF-8");
    }
}
