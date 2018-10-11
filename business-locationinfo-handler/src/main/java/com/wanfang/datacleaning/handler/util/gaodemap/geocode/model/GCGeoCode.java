package com.wanfang.datacleaning.handler.util.gaodemap.geocode.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/20 12:35 
 *  @Version  V1.0   
 */
public class GCGeoCode {

    /**
     * 结构化地址信息
     * 省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码
     */
    @JSONField(name = "formatted_address")
    private String formattedAddress;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 城市编码，如：010
     */
    @JSONField(name = "citycode")
    private String cityCode;
    /**
     * 区
     */
    private String district;
    /**
     * 乡镇
     */
    private String township;
    /**
     * 街道
     */
    private String street;
    /**
     * 门牌
     */
    private String number;
    /**
     * 区域编码
     */
    @JSONField(name = "adcode")
    private String adCode;
    /**
     * 坐标点，格式：经度，纬度
     */
    private String location;
    /**
     * 匹配级别
     */
    private String level;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "GCGeoCode{" +
                "formattedAddress='" + formattedAddress + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", district='" + district + '\'' +
                ", township='" + township + '\'' +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", adCode='" + adCode + '\'' +
                ", location='" + location + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
