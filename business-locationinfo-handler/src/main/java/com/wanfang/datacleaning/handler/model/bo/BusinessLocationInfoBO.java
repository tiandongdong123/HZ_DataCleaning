package com.wanfang.datacleaning.handler.model.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:22 
 *  @Version  V1.0   
 */
public class BusinessLocationInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 住所
     */
    private String dom;
    /**
     * 省份
     */
    private Integer province;
    /**
     * 市
     */
    private Integer city;
    /**
     * 区
     */
    private Integer area;
    /**
     * 经纬度， 格式：lat,lon
     */
    private String latLon;
    /**
     * 更新时间（10位时间戳）
     */
    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getLatLon() {
        return latLon;
    }

    public void setLatLon(String latLon) {
        this.latLon = latLon;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessLocationInfoBO{" +
                "id=" + id +
                ", dom='" + dom + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", latLon='" + latLon + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
