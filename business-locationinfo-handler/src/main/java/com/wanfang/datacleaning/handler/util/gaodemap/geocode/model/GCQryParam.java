package com.wanfang.datacleaning.handler.util.gaodemap.geocode.model;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/20 12:23 
 *  @Version  V1.0   
 */
public class GCQryParam {
    /**
     * 高德Key
     */
    private String key;

    /**
     * 结构化地址信息，
     * 规则遵循：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦，如：北京市朝阳区阜通东大街6号。
     * 如果需要解析多个地址的话，请用"|"进行间隔，并且将 batch 参数设置为 true，最多支持 10 个地址进进行"|"分割形式的请求。
     */
    private String address;
    /**
     * 地址所在的城市名
     * 可选输入内容包括：指定城市的中文（如北京）、指定城市的中文全拼（beijing）、citycode（010）、adcode（110000）。当指定城市查询内容为空时，会进行全国范围内的地址转换检索。
     */
    private String city;
    /**
     * 批量查询控制
     * batch 参数设置为 true 时进行批量查询操作，最多支持 10 个地址进行批量查询。
     * batch 参数设置为 false 时进行单点查询，此时即使传入多个地址也只返回第一个地址的解析查询结果。
     */
    private String batch;
    /**
     * 数字签名
     */
    private String sig;
    /**
     * 输出格式
     * 可选输入内容包括：JSON，XML。设置 JSON 返回结果数据将会以JSON结构构成；如果设置 XML 返回结果数据将以 XML 结构构成。
     */
    private String output;
    /**
     * 回调函数
     * callback 值是用户定义的函数名称，此参数只在 output 参数设置为 JSON 时有效。
     */
    private String callback;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "GeoCoderQryParam{" +
                "key='" + key + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", batch='" + batch + '\'' +
                ", sig='" + sig + '\'' +
                ", output='" + output + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }
}
