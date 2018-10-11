package com.wanfang.datacleaning.handler.util.gaodemap.geocode.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/20 15:33 
 *  @Version  V1.0   
 */
public class GCQryResult {

    /**
     * 结果状态值，返回值为 0 或 1，0 表示请求失败；1 表示请求成功。
     */
    private String status;
    /**
     * 状态code
     */
    @JSONField(name = "infocode")
    private String infoCode;
    /**
     * 状态说明
     */
    private String info;
    /**
     * 结果数目
     */
    private String count;
    /**
     * 地理编码信息列表
     */
    @JSONField(name = "geocodes")
    private List<GCGeoCode> gcGeoCodes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<GCGeoCode> getGcGeoCodes() {
        return gcGeoCodes;
    }

    public void setGcGeoCodes(List<GCGeoCode> gcGeoCodes) {
        this.gcGeoCodes = gcGeoCodes;
    }

    @Override
    public String toString() {
        return "GCQryResult{" +
                "status='" + status + '\'' +
                ", infoCode='" + infoCode + '\'' +
                ", info='" + info + '\'' +
                ", count='" + count + '\'' +
                ", gcGeoCodes=" + gcGeoCodes +
                '}';
    }
}
