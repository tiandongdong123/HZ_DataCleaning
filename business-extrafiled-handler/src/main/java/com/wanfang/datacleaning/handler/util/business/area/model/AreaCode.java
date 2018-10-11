package com.wanfang.datacleaning.handler.util.business.area.model;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/7/18 18:00 
 *  @Version  V1.0   
 */
public class AreaCode {

    /**
     * 省（/直辖市）码
     */
    private String provinceCode;
    /**
     * 市（/县）码
     */
    private String cityCode;
    /**
     * 区（/县）码
     */
    private String districtCode;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    @Override
    public String toString() {
        return "AreaCode{" +
                "provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", districtCode='" + districtCode + '\'' +
                '}';
    }
}
