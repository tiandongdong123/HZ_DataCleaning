package com.wanfang.datacleaning.handler.util.business.area.model;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/7/18 17:38 
 *  @Version  V1.0   
 */
public class Area {

    /**
     * 省（/直辖市）码
     */
    private String provinceCode;
    /**
     * 省（/直辖市）名
     */
    private String provinceName;
    /**
     * 市（/县）码
     */
    private String cityCode;
    /**
     * 市（/县）名
     */
    private String cityName;
    /**
     * 区（/县）码
     */
    private String districtCode;
    /**
     * 区（/县）名
     */
    private String districtName;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    public String toString() {
        return "Area{" +
                "provinceCode='" + provinceCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtCode='" + districtCode + '\'' +
                ", districtName='" + districtName + '\'' +
                '}';
    }
}
